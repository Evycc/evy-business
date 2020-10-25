package com.evy.common.trace.service;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.db.DBUtils;
import com.evy.common.log.CommandLog;
import com.evy.common.trace.infrastructure.tunnel.model.*;
import com.evy.common.trace.infrastructure.tunnel.po.TraceSqlPO;
import com.evy.common.utils.AppContextUtils;
import org.springframework.util.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * 链路跟踪<br/>
 * DB : 统计慢SQL (sql 、 耗时 、 当前IP 、 优化建议) | 配置 : evy.trace.db.flag={0开启|1关闭}<br/>
 * @Author: EvyLiuu
 * @Date: 2020/6/27 16:13
 */
public class TraceSlowSql {
    /**
     * 配置常量
     **/
    private static final String DB_PRPO = "evy.trace.db.flag";
    private static final ConcurrentLinkedQueue<TraceDBModel> DB_MODELS = new ConcurrentLinkedQueue<>();
    private static final String SLOW_SQL_INSERT = "com.evy.common.trace.repository.mapper.TraceMapper.slowSqlInsert";
    private static final String EXPLAIN_SQL = "EXPLAIN ";
    private static final String EXPLAIN_ID = "id";
    private static final String EXPLAIN_SELECT_TYPE = "select_type";
    private static final String EXPLAIN_TABLE = "table";
    private static final String EXPLAIN_PARTITIONS = "partitions";
    private static final String EXPLAIN_TYPE = "type";
    private static final String EXPLAIN_POSSIBLE_KEYS = "possible_keys";
    private static final String EXPLAIN_KEY = "key";
    private static final String EXPLAIN_KEY_LEN = "key_len";
    private static final String EXPLAIN_REF = "ref";
    private static final String EXPLAIN_ROWS = "rows";
    private static final String EXPLAIN_FILTERED = "filtered";
    private static final String EXPLAIN_EXTRA = "Extra";

    /**
     * 记录慢sql
     *
     * @param slowSql         慢sql
     * @param takeUpTimesatmp 耗时
     */
    public static void addTraceSql(String slowSql, long takeUpTimesatmp) {
        try {
            Optional.ofNullable(AppContextUtils.getForEnv(DB_PRPO))
                    .ifPresent(flag -> {
                        if (BusinessConstant.ZERO.equals(flag)) {
                            DB_MODELS.offer(TraceDBModel.create(BusinessConstant.VM_HOST, takeUpTimesatmp, slowSql));
                        }
                    });
        } catch (Exception e) {
            CommandLog.errorThrow("addTraceHttp Error!", e);
        }
    }

    /**
     * 处理慢sql类型逻辑,入库并清理内存中链表对象
     */
    public static void executeSlowSql() {
        int size = DB_MODELS.size();
        final String select1 = "SELECT";
        final String select2 = "select";
        try {
            if (size > BusinessConstant.ZERO_NUM) {
                List<TraceDBModel> dbModels = new ArrayList<>(DB_MODELS);

                dbModels.forEach(model -> {
                    String slowSql = model.getSlowSql();
                    TraceSqlPO tracesqlpo;

//                    if (!slowSql.contains(select1) || !slowSql.contains(select2)) {
//                        //非SELECT开头查询语句,不进行explain
//                        tracesqlpo = TraceSqlPO.create(model.getReqIp(), model.getSlowSql(), String.valueOf(model.getTakeUpTimestamp()));
//                    } else {
//                        ExplainListModel explainListModel = explain(slowSql);
//                        String explain = handleExplain(explainListModel);
//                        String exlainContent = explainListModel.toString();
//                        tracesqlpo = TraceSqlPO.create(model.getReqIp(), model.getSlowSql(), String.valueOf(model.getTakeUpTimestamp()), explain, exlainContent);
//                    }

                    ExplainListModel explainListModel = explain(slowSql);
                    String explain = handleExplain(explainListModel);
                    String exlainContent = explainListModel.toString();
                    tracesqlpo = TraceSqlPO.create(model.getReqIp(), model.getSlowSql(), String.valueOf(model.getTakeUpTimestamp()), explain, exlainContent);

                    DBUtils.insert(SLOW_SQL_INSERT, tracesqlpo);
                });
                DB_MODELS.removeAll(dbModels);
                dbModels = null;
            }
        } catch (Exception e) {
            //捕捉记录trace的异常，不影响业务功能
            CommandLog.warn("记录executeSlowSql异常", e);
        }
    }

    /**
     * 通过explain给出慢sql优化建议
     *
     * @param explainContent 慢sql explain
     * @return 慢sql优化建议
     */
    private static String handleExplain(ExplainListModel explainContent) {
        String explain = BusinessConstant.EMPTY_STR;

        if (!Objects.isNull(explainContent)) {
            StringBuilder stringBuilder = new StringBuilder();
            List<ExplainModel> explainModels = explainContent.getExplainModelList();
            try {
                switchTables(explainModels, stringBuilder);
                switchIndex(explainModels, stringBuilder);
                switchExtra(explainModels, stringBuilder);
                switchTableToIndex(explainModels, stringBuilder);

                explain = stringBuilder.toString();
            } catch (Exception e) {
                //捕捉记录trace的异常，不影响业务功能
                explain = e.getMessage();
                CommandLog.warn("handleExplain异常", e);
            }
        }

        return explain;
    }

    /**
     * 处理优化建议
     *
     * @param explainModels explain集合
     * @param stringBuilder 处理后字符串
     */
    private static void switchExtra(List<ExplainModel> explainModels, StringBuilder stringBuilder) {
        //type
        String extra = "%s表查询类型优化建议:%s";
        String tmpExtra = "%s临时表查询类型优化建议:%s";
        String splitStr = ";";
        StringBuilder stringBuilder1 = new StringBuilder();
        String pre = "查询类型优化建议:\n";
        stringBuilder.append(pre);

        explainModels.forEach(model -> {
            String tn = model.getTable();
            String extraStr = model.getExtra();

            if (!StringUtils.isEmpty(extraStr)) {
                String[] extras = extraStr.split(splitStr, -1);

                for (String e : extras) {
                    stringBuilder1.append(ExtraTypeEnum.convert(e).getExtraDesc()).append(BusinessConstant.WHITE_EMPTY_STR);
                }
            } else {
                stringBuilder1.append(ExtraTypeEnum.NOTNULL.getExtraDesc()).append(BusinessConstant.WHITE_EMPTY_STR);
            }

            if (tn.contains(BusinessConstant.TMP_TABLE_NAME)) {
                //临时表处理
                stringBuilder.append(String.format(tmpExtra, tn, stringBuilder1.toString()));
            } else {
                stringBuilder.append(String.format(extra, tn, stringBuilder1.toString()));
            }
            stringBuilder1.delete(0, stringBuilder1.length());
            stringBuilder.append(BusinessConstant.LINE_FEED_STR);
        });
    }

    /**
     * 处理表索引建议优化
     *
     * @param explainModels explain集合
     * @param stringBuilder 处理后字符串
     */
    private static void switchTableToIndex(List<ExplainModel> explainModels, StringBuilder stringBuilder) {
        String index1 = "%s表索引优化建议:%s";
        String index2 = "%s临时表索引优化建议:%s";
        String pre = "索引优化建议:\n";
        stringBuilder.append(pre);

        explainModels.forEach(model -> {
            String tn = model.getTable();
            String type = model.getType();

            if (tn.contains(BusinessConstant.TMP_TABLE_NAME)) {
                //临时表处理
                stringBuilder.append(String.format(index2, tn, ExplainTypeEnum.convert(type).getTypeDesc()));
            } else {
                stringBuilder.append(String.format(index1, tn, ExplainTypeEnum.convert(type).getTypeDesc()));
            }
            stringBuilder.append(BusinessConstant.LINE_FEED_STR);
        });
    }

    /**
     * 处理表对应索引
     *
     * @param explainModels explain集合
     * @param stringBuilder 处理后字符串
     */
    private static void switchIndex(List<ExplainModel> explainModels, StringBuilder stringBuilder) {
        String tableRow = "%s表预计查找行数:%s||占比:%s||命中索引情况:%s||索引类型:%s";
        String tmpTableRow = "%s临时表行数:%s|占比:%s||命中索引情况:%s||索引类型:%s";
        String pre = "检索行数及索引命中:\n";
        stringBuilder.append(pre);

        explainModels.forEach(model -> {
            String tn = model.getTable();
            String rows = model.getRows();
            String filtered = model.getFiltered();
            String type = model.getType();
            String ref = model.getRef();

            if (tn.contains(BusinessConstant.TMP_TABLE_NAME)) {
                //临时表处理
                stringBuilder.append(String.format(tmpTableRow, tn, rows, filtered, ref, type));
            } else {
                stringBuilder.append(String.format(tableRow, tn, rows, filtered, ref, type));
            }
            stringBuilder.append(BusinessConstant.LINE_FEED_STR);
        });
    }

    /**
     * 处理表名
     *
     * @param explainModels explain集合
     * @param stringBuilder 处理后字符串
     */
    private static void switchTables(List<ExplainModel> explainModels, StringBuilder stringBuilder) {
        String sqlSearchTraceSimple = "SQL执行链路:单表%s";
        String sqlSearchTrace = "SQL执行链路:";
        String sqlSearchTracePost = "=>";
        String tableMaxNumsStr = "警告!SQL关联表数量不应该大于3!";
        int tableMaxNums = 3;

        List<String> tables = explainModels.stream().map(ExplainModel::getTable).collect(Collectors.toList());
        if (tables.size() == BusinessConstant.ONE_NUM) {
            //单表
            stringBuilder.append(String.format(sqlSearchTraceSimple, tables.get(0)));
        } else {
            int size = tables.size();
            stringBuilder.append(sqlSearchTrace);
            for (int i = 0; i < size; i++) {
                stringBuilder.append(tables.get(i));
                if (i + 1 < size) {
                    stringBuilder.append(sqlSearchTracePost);
                }
            }
        }
        stringBuilder.append(BusinessConstant.LINE_FEED_STR);
        int size = (int) explainModels
                .stream()
                .filter(model -> {
                    String tn = model.getTable();
                    return !tn.contains(BusinessConstant.TMP_TABLE_NAME);
                })
                .distinct().count();
        if (size > tableMaxNums) {
            stringBuilder.append(tableMaxNumsStr).append(BusinessConstant.LINE_FEED_STR);
        }
    }

    /**
     * 获取explain建议
     *
     * @param slowSql 慢sql
     * @return 返回explain正文
     */
    private static ExplainListModel explain(final String slowSql) {
        final ExplainListModel[] explainListModel = {null};
        try {
            DBUtils.executeQuerySql(resultSet -> {
                List<ExplainModel> models = new ArrayList<>();
                try {
                    while (resultSet.next()) {
                        models.add(ExplainModel.create(resultSet.getString(EXPLAIN_ID),
                                resultSet.getString(EXPLAIN_SELECT_TYPE),
                                resultSet.getString(EXPLAIN_TABLE),
                                resultSet.getString(EXPLAIN_PARTITIONS),
                                resultSet.getString(EXPLAIN_TYPE),
                                resultSet.getString(EXPLAIN_POSSIBLE_KEYS),
                                resultSet.getString(EXPLAIN_KEY),
                                resultSet.getString(EXPLAIN_KEY_LEN),
                                resultSet.getString(EXPLAIN_REF),
                                resultSet.getString(EXPLAIN_ROWS),
                                resultSet.getString(EXPLAIN_FILTERED),
                                resultSet.getString(EXPLAIN_EXTRA)));
                    }
                } catch (SQLException throwables) {
                    CommandLog.warn("explain异常", throwables);
                }
                explainListModel[0] = ExplainListModel.create(models);
            }, EXPLAIN_SQL + slowSql, null);
        } catch (Exception e) {
            //捕捉记录trace的异常，不影响业务功能
            CommandLog.warn("explain异常", e);
        }
        return explainListModel[0];
    }
}
