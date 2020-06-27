package com.evy.common.trace.infrastructure.tunnel.model;

/**
 * explain Extra优化建议
 * @Author: EvyLiuu
 * @Date: 2020/6/20 17:04
 */
public enum ExtraTypeEnum {
    //explain extra优化建议
    DISTINCT(ExtraTypeEnum.DISTINCT_TYPE),
    NO_TABLES_USED(ExtraTypeEnum.NO_TABLES_USED_TYPE),
    USING_INDEX(ExtraTypeEnum.USING_INDEX_TYPE),
    USING_JOIN_BUFFER_LOOP(ExtraTypeEnum.USING_JOIN_BUFFER_LOOP_TYPE),
    USING_JOIN_BUFFER_BATCHED(ExtraTypeEnum.USING_JOIN_BUFFER_BATCHED_TYPE),
    USING_SORT_UNION(ExtraTypeEnum.USING_SORT_UNION_TYPE),
    USING_SORT_INTERSECTION(ExtraTypeEnum.USING_SORT_INTERSECTION_TYPE),
    USING_UNION(ExtraTypeEnum.USING_UNION_TYPE),
    USING_INTERSECTION(ExtraTypeEnum.USING_INTERSECTION_TYPE),
    USING_FILESORT(ExtraTypeEnum.USING_FILESORT_TYPE),
    USING_WHERE(ExtraTypeEnum.USING_WHERE_TYPE),
    USING_TEMPORARY(ExtraTypeEnum.USING_TEMPORARY_TYPE),
    FIRST_MATCH(ExtraTypeEnum.FIRST_MATCH_TYPE),
    LOOSESCAN(ExtraTypeEnum.LOOSESCAN_TYPE),
    NOTNULL(ExtraTypeEnum.NULL);

    private static final String DISTINCT_TYPE = "警告!查询条件存在distinct,建议在应用层进行过滤";
    private static final String NO_TABLES_USED_TYPE = "警告!不带FROM字句或存在多个FROM";
    private static final String USING_INDEX_TYPE = "通过索引即可命中数据,不需要回表查询(不存在索引的列,需要通过主键进行定位)";
    private static final String USING_JOIN_BUFFER_LOOP_TYPE = "警告!建议减少驱动表记录数,小表驱动大表";
    private static final String USING_JOIN_BUFFER_BATCHED_TYPE = "警告!建议通过主键批量进行查询";
    private static final String USING_SORT_UNION_TYPE = "查询主键,获取记录合并排序后并集order by or";
    private static final String USING_SORT_INTERSECTION_TYPE = "查询主键,获取记录合并排序后交集order by and";
    private static final String USING_UNION_TYPE = "查询主键,获取记录合并后并集or";
    private static final String USING_INTERSECTION_TYPE = "查询主键,获取记录合并后交集and";
    private static final String USING_FILESORT_TYPE = "致命!排序语句必须加索引";
    private static final String USING_WHERE_TYPE = "致命!查询条件未全部覆盖索引";
    private static final String FIRST_MATCH_TYPE = "警告!存在大记录数子查询,需要评估子查询记录数";
    private static final String LOOSESCAN_TYPE = "警告!存在重复记录子查询,需要评估子查询数据辨识度";
    private static final String NULL = "无优化建议";

    /**
     * 1、数据表中包含BLOB/TEXT列；
     * 2、在 GROUP BY 或者 DSTINCT 的列中有超过 512字符 的字符类型列（或者超过 512字节的 二进制类型列，在5.6.15之前只管是否超过512字节）；
     * 3、在SELECT、UNION、UNION ALL查询中，存在最大长度超过512的列（对于字符串类型是512个字符，对于二进制类型则是512字节）；
     * 4、执行SHOW COLUMNS/FIELDS、DESCRIBE等SQL命令，因为它们的执行结果用到了BLOB列类型。
     */
    private static final String USING_TEMPORARY_TYPE = "语句使用临时表,存在性能风险";

    //extra 建议
    private final String extraDesc;

    ExtraTypeEnum(String extraDesc) {
        this.extraDesc = extraDesc;
    }

    public static ExtraTypeEnum convert(String extra) {
        String var1 = "Distinct";
        String var2 = "No tables used";
        String var3 = "Using filesort";
        String var4 = "Using index";
        String var5 = "Using sort_union";
        String var6 = "Using_union";
        String var7 = "Using intersect";
        String var8 = "Using sort_intersection";
        String var9 = "Using temporary";
        String var10 = "Using where";
        String var11 = "Firstmatch";
        String var12 = "Loosescan";
        String var13 = "Block Nested Loop";
        String var14 = "Batched Key Accss";

        return extra.contains(var1) ? ExtraTypeEnum.DISTINCT :
               extra.contains(var2) ? ExtraTypeEnum.NO_TABLES_USED :
               extra.contains(var3) ? ExtraTypeEnum.USING_FILESORT :
               extra.contains(var4) ? ExtraTypeEnum.USING_INDEX :
               extra.contains(var5) ? ExtraTypeEnum.USING_SORT_UNION :
               extra.contains(var6) ? ExtraTypeEnum.USING_UNION :
               extra.contains(var7) ? ExtraTypeEnum.USING_INTERSECTION :
               extra.contains(var8) ? ExtraTypeEnum.USING_SORT_INTERSECTION :
               extra.contains(var9) ? ExtraTypeEnum.USING_TEMPORARY :
               extra.contains(var10) ? ExtraTypeEnum.USING_WHERE :
               extra.contains(var11) ? ExtraTypeEnum.FIRST_MATCH :
               extra.contains(var12) ? ExtraTypeEnum.LOOSESCAN :
               extra.contains(var13) ? ExtraTypeEnum.USING_JOIN_BUFFER_LOOP :
               extra.contains(var14) ? ExtraTypeEnum.USING_JOIN_BUFFER_BATCHED :
               ExtraTypeEnum.NOTNULL;
    }

    public String getExtraDesc(){
        return extraDesc;
    }
}
