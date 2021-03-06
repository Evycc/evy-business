package com.evy.common.trace.infrastructure.tunnel.model;

/**
 * explain模型
 *
 * @Author: EvyLiuu
 * @Date: 2020/6/20 10:59
 */
public class ExplainModel {
    private final String id;
    private final String selectType;
    private final String table;
    private final String partitions;
    private final String type;
    private final String possibleKeys;
    private final String key;
    private final String keyLen;
    private final String ref;
    private final String rows;
    private final String filtered;
    private final String extra;

    private ExplainModel(String id, String selectType, String table, String partitions, String type, String possibleKeys, String key, String keyLen, String ref, String rows, String filtered, String extra) {
        this.id = id;
        this.selectType = selectType;
        this.table = table;
        this.partitions = partitions;
        this.type = type;
        this.possibleKeys = possibleKeys;
        this.key = key;
        this.keyLen = keyLen;
        this.ref = ref;
        this.rows = rows;
        this.filtered = filtered;
        this.extra = extra;
    }

    /**
     * 隐藏构造方法细节,全量参数构造方法
     */
    public static ExplainModel create(String id, String selectType, String table, String partitions, String type, String possibleKeys, String key, String keyLen, String ref, String rows, String filtered, String extra){
        return new ExplainModel(id, selectType, table, partitions, type, possibleKeys, key, keyLen, ref, rows, filtered, extra);
    }

    @Override
    public String toString() {
        return "ExplainModel{" +
                "id='" + id + '\'' +
                ", selectType='" + selectType + '\'' +
                ", table='" + table + '\'' +
                ", partitions='" + partitions + '\'' +
                ", type='" + type + '\'' +
                ", possibleKeys='" + possibleKeys + '\'' +
                ", key='" + key + '\'' +
                ", keyLen='" + keyLen + '\'' +
                ", ref='" + ref + '\'' +
                ", rows='" + rows + '\'' +
                ", filtered='" + filtered + '\'' +
                ", extra='" + extra + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getSelectType() {
        return selectType;
    }

    public String getTable() {
        return table;
    }

    public String getPartitions() {
        return partitions;
    }

    public String getType() {
        return type;
    }

    public String getPossibleKeys() {
        return possibleKeys;
    }

    public String getKey() {
        return key;
    }

    public String getKeyLen() {
        return keyLen;
    }

    public String getRef() {
        return ref;
    }

    public String getRows() {
        return rows;
    }

    public String getFiltered() {
        return filtered;
    }

    public String getExtra() {
        return extra;
    }
}
