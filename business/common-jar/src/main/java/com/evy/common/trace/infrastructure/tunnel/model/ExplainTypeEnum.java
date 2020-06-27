package com.evy.common.trace.infrastructure.tunnel.model;

/**
 * explain type字段含义，有好到坏，一般来说，得保证查询至少达到range级别，最好能达到ref，否则就可能会出现性能问题<br/>
 * NULL > system > const > eq_ref > ref > fulltext > ref_or_null > index_merge > unique_subquery > index_subquery > range > index > ALL<br/>
 * @Author: EvyLiuu
 * @Date: 2020/6/20 17:03
 */
public enum ExplainTypeEnum {
    //explain 索引type类型
    ALL(ExplainTypeEnum.ALL_TYPE),
    INDEX(ExplainTypeEnum.INDEX_TYPE),
    RANDE(ExplainTypeEnum.RANGE_TYPE),
    INDEX_SUBQUERY(ExplainTypeEnum.INDEX_SUBQUERY_TYPE),
    UNIQUE_SUBQUERY(ExplainTypeEnum.UNIQUE_SUBQUERY_TYPE),
    INDEX_MERGE(ExplainTypeEnum.INDEX_MERGE_TYPE),
    REF_OR_NULL(ExplainTypeEnum.REF_OR_NULL_TYPE),
    FULL_TEXT(ExplainTypeEnum.FULL_TEXT_TYPE),
    REF(ExplainTypeEnum.REF_TYPE),
    EQ_REF(ExplainTypeEnum.EQ_REF_TYPE),
    CONST(ExplainTypeEnum.CONST_TYPE),
    SYSTEM(ExplainTypeEnum.SYSTEM_TYPE),
    NULL(ExplainTypeEnum.NULL_TYPE);

    private static final String ALL_TYPE = "致命!全表检索,存在性能瓶颈,性能最差,必须为查询字段添加合理索引";
    private static final String INDEX_TYPE = "致命!命中索引(全表检索),存在性能瓶颈,索引应该选择辨识度高的字段";
    private static final String RANGE_TYPE = "命中索引(范围检索),需要合理评估检索范围,分段获取limit建议为2000-5000,条件最左前缀可以用主键限制检索范围";
    private static final String INDEX_SUBQUERY_TYPE = "命中普通索引,评估数据量,采取分段获取数据";
    private static final String UNIQUE_SUBQUERY_TYPE = "命中唯一索引,in形式子查询返回不重复值唯一值,索引应该选择辨识度高的字段";
    private static final String INDEX_MERGE_TYPE = "命中联合索引或多个索引";
    private static final String REF_OR_NULL_TYPE = "查询条件相同(可能存在Null),性能优";
    private static final String FULL_TEXT_TYPE = "全文索引,优先级高于普通索引,性能较优";
    private static final String REF_TYPE = "查询条件相同(不存在Null),性能优";
    private static final String EQ_REF_TYPE = "联合查询驱动表数据为1行,且为被驱动表主键或非空索引列,性能优越";
    private static final String CONST_TYPE = "主键或唯一索引命中一行数据,性能优越";
    private static final String SYSTEM_TYPE = "空表或只存在一行数据,请检查是否Innodb表,不存在性能瓶颈";
    private static final String NULL_TYPE = "无优化建议";

    /**
     * 索引type描述
     */
    private final String typeDesc;

    ExplainTypeEnum(String type) {
        typeDesc = type;
    }

    public static ExplainTypeEnum convert(String type) {
//        NULL > system > const > eq_ref > ref > fulltext > ref_or_null > index_merge
//        > unique_subquery > index_subquery > range > index > ALL<br/>

        String var1 = "ALL";
        String var2 = "index";
        String var3 = "range";
        String var4 = "index_subquery";
        String var5 = "unique_subquery";
        String var6 = "index_merge";
        String var7 = "ref_or_null";
        String var8 = "fulltext";
        String var9 = "ref";
        String var10 = "eq_ref";
        String var11 = "const";
        String var12 = "system";

        return var1.equals(type) ? ExplainTypeEnum.ALL :
               var2.equals(type) ? ExplainTypeEnum.INDEX :
               var3.equals(type) ? ExplainTypeEnum.RANDE :
               var4.equals(type) ? ExplainTypeEnum.INDEX_SUBQUERY :
               var5.equals(type) ? ExplainTypeEnum.UNIQUE_SUBQUERY :
               var6.equals(type) ? ExplainTypeEnum.INDEX_MERGE :
               var7.equals(type) ? ExplainTypeEnum.REF_OR_NULL :
               var8.equals(type) ? ExplainTypeEnum.FULL_TEXT :
               var9.equals(type) ? ExplainTypeEnum.REF :
               var10.equals(type) ? ExplainTypeEnum.EQ_REF :
               var11.equals(type) ? ExplainTypeEnum.CONST :
               var12.equals(type) ? ExplainTypeEnum.SYSTEM :
               ExplainTypeEnum.NULL;
    }

    public String getTypeDesc(){
        return typeDesc;
    }
}
