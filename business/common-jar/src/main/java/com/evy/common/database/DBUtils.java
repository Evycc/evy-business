package com.evy.common.database;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.log.CommandLog;
import com.evy.common.utils.AppContextUtils;
import com.evy.common.utils.CommandUtils;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * db操作类
 *
 * @Author: EvyLiuu
 * @Date: 2019/11/7 22:49
 */
public class DBUtils {
    private static DataSource DATA_SOURCE;
    private static SqlSessionFactory SQL_SESSION_FACTORY;
    private static JdbcTransactionFactory JDBC_TRANSACTION_FACTORY;
    private static String MYBATIS_CONF_XML;
    private static int BATCH_INSERT_COUNT;

    static {
        init();
    }

    private static void init(){
        AppContextUtils.getSyncProp(properties -> {
            CommandLog.info("初始化DBUtils");
            MYBATIS_CONF_XML = properties.getDatabase().getMybatisXmlPath();
            BATCH_INSERT_COUNT = properties.getDatabase().getBatchInsertCount();
            initDataSource();
            initMybatis();
            JDBC_TRANSACTION_FACTORY = new JdbcTransactionFactory();
        });
    }

    /**
     * 初始化MyBatis，SqlSessionFactory 及 SqlSession
     */
    private static void initMybatis() {
        CommandLog.info("DBUtils初始化Mybatis配置");
        try {
            SQL_SESSION_FACTORY = AppContextUtils.getBean(SqlSessionFactory.class);
        } catch (Exception e) {
            CommandLog.errorThrow("initMybatis error!", e);
            if (SQL_SESSION_FACTORY == null) {
                try {
                    //从配置文件初始化Mybatis
                    SQL_SESSION_FACTORY = initMyBatisForXml(MYBATIS_CONF_XML);

                    if (SQL_SESSION_FACTORY == null) {
                        CommandLog.error("Mybatis SqlSessionFactory初始化失败，未找到Bean实例");
                    }
                } catch (IOException ex) {
                    CommandLog.errorThrow("initMyBatisForXml error!", e);
                }
            }
        }
    }

    /**
     * 通过Mybatis配置文件，初始化SqlSessionFactory </br>
     * 默认：MYBATIS_CONF_XML = "mybatis.xml"
     *
     * @param mybatisXml Mybatis配置文件
     * @return org.apache.ibatis.session.SqlSessionFactory
     */
    private static SqlSessionFactory initMyBatisForXml(String mybatisXml) throws IOException {
        CommandLog.info("DBUtils查找Mybatis配置文件:{}", mybatisXml);
        SqlSessionFactory sqlSessionFactory;
        try {
            sqlSessionFactory = getSqlSessionfFactory(mybatisXml);
        } catch (IOException e) {
            CommandLog.error("从{}初始化Mybatis失败", MYBATIS_CONF_XML);
            throw e;
        }

        return sqlSessionFactory;
    }

    /**
     * 从Mybatis配置文件初始化SqlSessionfFactory
     *
     * @param xmlName Mybatis配置文件
     * @return org.apache.ibatis.session.SqlSessionFactory
     * @throws IOException 读取或初始化SqlSessionfFactory异常
     */
    private static SqlSessionFactory getSqlSessionfFactory(String xmlName) throws IOException {
        SqlSessionFactory sqlSessionFactory;
        try {
            Reader reader = Resources.getResourceAsReader(xmlName);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        } catch (IOException e) {
            CommandLog.errorThrow("读取或初始化SqlSessionfFactory异常", e);
            throw e;
        }

        return sqlSessionFactory;
    }

    /**
     * 初始化jdbc DataSource
     */
    private static void initDataSource() {
        DATA_SOURCE = AppContextUtils.getBean(DataSource.class);
    }

    /**
     * 获取DataSource
     *
     * @return javax.sql.DataSource
     */
    public static DataSource getDataSource() {
        if (DATA_SOURCE == null) {
            initDataSource();
        }
        proxyDataSource(DATA_SOURCE);

        return DATA_SOURCE;
    }

    /**
     * 对ENC()的密码进行解密，并赋值到DataSource
     *
     * @param dataSource javax.sql.DataSource
     */
    private static void proxyDataSource(DataSource dataSource) {
        //对password进行解密
        if (dataSource instanceof HikariDataSource) {
            String pass1 = ((HikariDataSource) dataSource).getPassword();
            ((HikariDataSource) dataSource).setPassword(CommandUtils.decodeEnc(pass1));
        }
    }

    /**
     * 返回jdbc datasource
     *
     * @param beanName bean name
     * @return javax.sql.DataSource
     */
    public static DataSource getDataSource(String beanName) {
        Object obj = AppContextUtils.getBean(beanName);
        if (obj instanceof DataSource) {
            DataSource dataSource = (DataSource) obj;
            proxyDataSource(dataSource);
            return dataSource;
        }

        CommandLog.error("Bean {}非javax.sql.DataSource类型", beanName);
        return null;
    }

    /**
     * 返回Mybatsi SqlSession
     *
     * @return org.apache.ibatis.session.SqlSession
     */
    public static SqlSession getSqlSession() {
        SqlSession sqlSession = SQL_SESSION_FACTORY.openSession();
        proxyDataSource(sqlSession.getConfiguration().getEnvironment().getDataSource());

        return sqlSession;
    }

    /**
     * 指定返回的SqlSession类型
     *
     * @param type ExecutorType.SIMPLE || ExecutorType.REUSE || ExecutorType.BATCH </br>
     *             默认为ExecutorType.SIMPLE </br>
     *             ExecutorType.BATCH  批处理，使用后使用flushStatements清除缓存 </br>
     *             ExecutorType.REUSE  可复用的执行器，使用后使用flushStatements清除缓存 </br>
     * @return org.apache.ibatis.session.SqlSession
     */
    public static SqlSession getSqlSession(ExecutorType type) {
        SqlSession sqlSession;

        switch (type) {
            case BATCH:
            case REUSE:
            case SIMPLE:
                sqlSession = SQL_SESSION_FACTORY.openSession(type);
                break;
            default:
                sqlSession = SQL_SESSION_FACTORY.openSession();
                break;
        }
        return sqlSession;
    }

    /**
     * insert
     *
     * @param mapper mybatis mapper方法
     * @param input  入参
     * @return 0：insert失败  1：insert成功
     */
    public static int insert(String mapper, Object input) {
        long start = System.currentTimeMillis();
        int result;
        try (SqlSession sqlSession = getSqlSession()) {
            result = sqlSession.insert(mapper, input);
        }

        CommandLog.info("insert耗时:{}ms", (System.currentTimeMillis() - start));
        return result;
    }

    /**
     * 不依赖Spring进行事务<br>
     * 通过transaction.getConnection(); 开启事务<br>
     * 通过transaction.commit(); 进行事务提交<br>
     * 通过transaction.rollback(); 进行事务回滚
     *
     * @param sqlSession org.apache.ibatis.session.SqlSession
     * @return org.apache.ibatis.transaction.jdbc.JdbcTransaction
     * @throws SQLException 获取事务异常
     */
    public static Transaction getJdbcTransaction(SqlSession sqlSession) throws SQLException {
        Connection connection = sqlSession.getConnection();
        connection.setAutoCommit(false);
        Transaction transaction = JDBC_TRANSACTION_FACTORY.newTransaction(
                connection
        );
        transaction.getConnection();
        return transaction;
    }

    /**
     * 对事务进行回滚
     *
     * @param transaction transaction
     * @throws SQLException java.sql.SQLException
     */
    public static void transactionRollback(Transaction transaction) throws SQLException {
        if (transaction != null) {
            transaction.rollback();
            transaction.close();
        }
    }

    /**
     * 对事务进行Commit
     *
     * @param transaction transaction
     * @throws SQLException java.sql.SQLException
     */
    public static void transactionCommit(Transaction transaction) throws SQLException {
        if (transaction != null) {
            transaction.commit();
        }
    }

    /**
     * 批量提交DDL
     *
     * @param batchList batch结构参数
     * @return 批量提交数
     */
    public static int batchAny(List<BatchModel> batchList) {
        SqlSession sqlSession = null;
        long start = System.currentTimeMillis();
        int result = 0;

        Transaction transaction = null;
        try {
            sqlSession = getSqlSession(ExecutorType.BATCH);
            transaction = getJdbcTransaction(sqlSession);

            for (BatchModel batchModel : batchList) {
                Map<String, String> param1 = batchModel.getMap();
                Object param2 = batchModel.getParam();
                String mapper = batchModel.getMapper();
                BatchType batchType = batchModel.getType();

                switch (batchType) {
                    case INSERT:
                        sqlSession.insert(mapper, param1 == null ? param2 : param1);
                        break;
                    case DELETE:
                        sqlSession.delete(mapper, param1 == null ? param2 : param1);
                        break;
                    case UPDATE:
                        sqlSession.update(mapper, param1 == null ? param2 : param1);
                        break;
                    default:
                        throw new BasicException(new Exception("batchType类型错误"));
                }

                if (++result % BATCH_INSERT_COUNT == 0) {
                    CommandLog.info("batchAny批量提交中..");
                    sqlSession.commit();
                    transactionCommit(transaction);
                }
            }

            if (sqlSession.flushStatements().size() > 0) {
                CommandLog.info("batchAny批量提交中（提交剩余DDL）..");
                sqlSession.commit();
                transactionCommit(transaction);
            }
        } catch (Exception e) {
            CommandLog.errorThrow("batchAny批量提交失败", e);

            try {
                //true  回滚整个事务
                if (Objects.nonNull(sqlSession)) {
                    transactionRollback(transaction);
                    sqlSession.rollback();
                    sqlSession.close();
                }
            } catch (SQLException ex) {
                printErrorRollback(ex);
            }
        } finally {
            close(sqlSession, transaction);
            CommandLog.info("batchAny耗时:{}ms", (System.currentTimeMillis() - start));
        }

        return result;
    }

    /**
     * 批量insert
     *
     * @param mapper mybatis mapper方法
     * @param input  入参
     * @return 批量insert成功的记录数
     */
    public static int insertBatch(String mapper, List<Map<String, String>> input) {
        return insertBatch(mapper, input, BATCH_INSERT_COUNT);
    }

    /**
     * 批量insert
     *
     * @param mapper      mybatis mapper方法
     * @param input       入参
     * @param commitCount 0：默认一次提交{@link DBUtils#BATCH_INSERT_COUNT}条记录，反之一次提交size条记录
     * @return 批量insert成功的记录数
     */
    public static int insertBatch(String mapper, List<Map<String, String>> input, int commitCount) {
        SqlSession sqlSession = null;
        Transaction transaction = null;
        int result = 0;
        int batchCommitCount = commitCount > 0 ? commitCount : BATCH_INSERT_COUNT;
        long start = System.currentTimeMillis();

        try {
            sqlSession = getSqlSession(ExecutorType.BATCH);
            transaction = getJdbcTransaction(sqlSession);

            for (Map<String, String> map : input) {
                sqlSession.insert(mapper, map);

                if (++result % batchCommitCount == 0) {
                    CommandLog.info("insertBatch批量提交中..");
                    sqlSession.commit();
                    transactionCommit(transaction);
                }
            }

            if (sqlSession.flushStatements().size() > 0) {
                CommandLog.info("insertBatch批量提交中（提交剩余DDL）..");
                sqlSession.commit();
                transactionCommit(transaction);
            }
        } catch (Exception e) {
            CommandLog.errorThrow("insertBatch批量提交失败", e);
            try {
                if (sqlSession != null) {
                    transactionRollback(transaction);
                    sqlSession.rollback();
                    sqlSession.close();
                }
            } catch (SQLException ex) {
                printErrorRollback(ex);
            }
        } finally {
            close(sqlSession, transaction);
            CommandLog.info("insertBatch耗时:{}ms", (System.currentTimeMillis() - start));
        }

        return result;
    }

    /**
     * 单sql查询
     *
     * @param mapper mybatis mapper方法
     * @param input  入参
     * @param <T>    返回类型
     * @return T
     */
    public static <T> T selectOne(String mapper, Object input) {
        T result;
        long start = System.currentTimeMillis();

        try (SqlSession sqlSession = getSqlSession()) {
            result = sqlSession.selectOne(mapper, input);
        }
        CommandLog.info("selectOne耗时:{}ms", (System.currentTimeMillis() - start));

        return result;
    }

    /**
     * 单sql查询
     *
     * @param mapper mybatis mapper方法
     * @param <T>    返回类型
     * @return T
     */
    public static <T> T selectOne(String mapper) {
        T result;
        long start = System.currentTimeMillis();

        try (SqlSession sqlSession = getSqlSession()) {
            result = sqlSession.selectOne(mapper);
        }
        CommandLog.info("selectOne耗时:{}ms", (System.currentTimeMillis() - start));

        return result;
    }

    /**
     * select 多行结果
     *
     * @param mapper mybatis mapper方法
     * @param input  入参
     * @param <T>    返回类型
     * @return 返回List<T>列表
     */
    public static <T> List<T> selectList(String mapper, Object input) {
        List<T> results;
        long start = System.currentTimeMillis();

        try (SqlSession sqlSession = getSqlSession()) {
            results = sqlSession.selectList(mapper, input);
        }
        CommandLog.info("selectList耗时:{}ms", (System.currentTimeMillis() - start));

        return results;
    }

    /**
     * select 多行结果
     *
     * @param mapper mybatis mapper方法
     * @param <T>    返回类型
     * @return 返回List<T>列表
     */
    public static <T> List<T> selectList(String mapper) {
        List<T> results;
        long start = System.currentTimeMillis();

        try (SqlSession sqlSession = getSqlSession()) {
            results = sqlSession.selectList(mapper);
        }
        CommandLog.info("selectList耗时:{}ms", (System.currentTimeMillis() - start));

        return results;
    }

    /**
     * delete
     *
     * @param mapper mybatis mapper方法
     * @param input  入参
     * @return 返回删除的行数
     */
    public static int delete(String mapper, Object input) {
        int result;
        long start = System.currentTimeMillis();

        try (SqlSession sqlSession = getSqlSession()) {
            result = sqlSession.delete(mapper, input);
        }
        CommandLog.info("delete耗时:{}ms", (System.currentTimeMillis() - start));

        return result;
    }

    /**
     * delete
     *
     * @param mapper mybatis mapper方法
     * @return 返回删除的行数
     */
    public static int delete(String mapper) {
        int result;
        long start = System.currentTimeMillis();

        try (SqlSession sqlSession = getSqlSession()) {
            result = sqlSession.delete(mapper);
        }
        CommandLog.info("delete耗时:{}ms", (System.currentTimeMillis() - start));

        return result;
    }

    /**
     * update
     *
     * @param mapper mybatis mapper方法
     * @param input  键值对入参
     * @return 更新的行数
     */
    public static int update(String mapper, Object input) {
        int result;
        long start = System.currentTimeMillis();

        try (SqlSession sqlSession = getSqlSession()) {
            result = sqlSession.update(mapper, input);
        }
        CommandLog.info("update耗时:{}ms", (System.currentTimeMillis() - start));

        return result;
    }

    /**
     * 执行sql方法
     *
     * @param consumer 执行具体操作
     * @param sql      预执行的sql
     */
    public static void executeQuerySql(Consumer<ResultSet> consumer, String sql, List<Object> params) {
        try (Connection connection = DBUtils.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = setParamForList(statement, params).executeQuery()) {

            consumer.accept(resultSet);

        } catch (SQLException e) {
            CommandLog.errorThrow("初始化ResultSet异常", e);
        }
    }

    /**
     * 执行sql方法
     *
     * @param sql      预执行的sql
     * @return 方法默认返回2，执行异常
     */
    public static int executeUpdateSql(String sql, List<Object> params) {
        try (Connection connection = DBUtils.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (setParamForList(statement, params).execute()) {
                return BusinessConstant.SUCESS;
            }
        } catch (SQLException e) {
            CommandLog.errorThrow("executeUpdateSql执行异常", e);
        }
        return BusinessConstant.FAILED;
    }

    /**
     * 为java.sql.Statement设置参数
     *
     * @param statement java.sql.Statement
     * @param params    参数集合
     */
    private static PreparedStatement setParamForList(PreparedStatement statement, List<Object> params) throws SQLException {
        if (params != null && !params.isEmpty()) {
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }
        }
        return statement;
    }

    /**
     * update
     *
     * @param mapper mybatis mapper方法
     * @return 更新的行数
     */
    public static int update(String mapper) {
        int result;
        long start = System.currentTimeMillis();

        try (SqlSession sqlSession = getSqlSession()) {
            result = sqlSession.update(mapper);
        }
        CommandLog.info("update耗时:{}ms", (System.currentTimeMillis() - start));

        return result;
    }

    /**
     * 获取mapper实例
     *
     * @param mapperNaem mapper类名
     * @param <T>        mapper类型
     * @return mapper
     */
    public static <T> T getMapper(Class<T> mapperNaem) {
        try (SqlSession sqlSession = getSqlSession()) {
            return sqlSession.getMapper(mapperNaem);
        }
    }

    /**
     * 打印回滚异常日志
     *
     * @param e 异常
     */
    private static void printErrorRollback(Exception e) {
        CommandLog.errorThrow("回滚事务异常", e);
    }

    /**
     * 关闭连接
     * @param sqlSession    org.apache.ibatis.session.SqlSession
     * @param transaction   org.apache.ibatis.transaction.Transaction
     */
    private static void close(SqlSession sqlSession, Transaction transaction) {
        if (Objects.nonNull(sqlSession)) {
            sqlSession.close();
        }
        if (Objects.nonNull(transaction)) {
            try {
                transaction.close();
            } catch (SQLException throwables) {
                CommandLog.errorThrow("关闭transaction异常", throwables);
            }
        }
    }

    /**
     * 批量提交结构
     */
    public static class BatchModel {
        /**
         * mapper
         */
        String mapper;
        /**
         * sql 入参 键值对
         */
        Map<String, String> map;
        /**
         * sql 入参
         */
        Object param;
        BatchType type;

        private BatchModel(String mapper, Object param, BatchType type) {
            this.mapper = mapper;
            this.param = param;
            this.type = type;
        }

        private BatchModel(String mapper, Map<String, String> map, BatchType type) {
            this.mapper = mapper;
            this.map = map;
            this.type = type;
        }

        public static BatchModel create(String mapper, Object param, BatchType batchType) {
            return new BatchModel(mapper, param, batchType);
        }

        public static BatchModel create(String mapper, Map<String, String> map, BatchType batchType) {
            return new BatchModel(mapper, map, batchType);
        }

        public String getMapper() {
            return mapper;
        }

        public Map<String, String> getMap() {
            return map;
        }

        public Object getParam() {
            return param;
        }

        public BatchType getType() {
            return type;
        }
    }

    /**
     * 批量提交类型
     */
    public enum BatchType {
        //表示为UPDATE操作的SQL
        UPDATE,
        //表示为INSERT操作的SQL
        INSERT,
        //表示为DELETE操作的SQL
        DELETE
    }
}