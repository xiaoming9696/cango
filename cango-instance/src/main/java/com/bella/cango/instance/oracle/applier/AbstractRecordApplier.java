package com.bella.cango.instance.oracle.applier;

import com.bella.cango.instance.oracle.common.db.meta.ColumnMeta;
import com.bella.cango.instance.oracle.common.db.meta.ColumnValue;
import com.bella.cango.instance.oracle.common.db.meta.Table;
import com.bella.cango.instance.oracle.common.lifecycle.AbstractYuGongLifeCycle;
import com.bella.cango.instance.oracle.common.model.record.Record;
import com.bella.cango.instance.oracle.exception.YuGongException;
import com.google.common.collect.Lists;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author agapple 2014年2月25日 下午11:38:06
 * @since 1.0.0
 */
public abstract class AbstractRecordApplier extends AbstractYuGongLifeCycle implements RecordApplier {

    public static class TableSqlUnit {

        public String applierSql;
        public Map<String, Integer> applierIndexs;
    }

    protected Integer getIndex(final Map<String, Integer> indexs, ColumnValue cv) {
        return getIndex(indexs, cv, false);
    }

    protected Integer getIndex(final Map<String, Integer> indexs, ColumnValue cv, boolean notExistReturnNull) {
        Integer result = indexs.get(cv.getColumn().getName());
        if (result == null && !notExistReturnNull) {
            throw new YuGongException("not found column[" + cv.getColumn().getName() + "] in record");
        } else {
            return result;
        }
    }

    /**
     * 检查下是否存在必要的字段
     */
    protected void checkColumns(Table meta, Map<String, Integer> indexs) {
        Set<String> idx = new HashSet<String>();
        for (ColumnMeta column : meta.getColumns()) {
            idx.add(column.getName());
        }

        for (ColumnMeta column : meta.getPrimaryKeys()) {
            idx.add(column.getName());
        }

        for (String key : indexs.keySet()) {
            if (!idx.contains(key)) {
                throw new YuGongException("not found column[" + key + "] in target db");
            }
        }
    }

    protected void checkColumnsWithoutPk(Table meta, Map<String, Integer> indexs) {
        Set<String> idx = new HashSet<String>();
        for (ColumnMeta column : meta.getColumns()) {
            idx.add(column.getName());
        }

        for (String key : indexs.keySet()) {
            if (!idx.contains(key)) {
                throw new YuGongException("not found column[" + key + "] in target db");
            }
        }
    }

    /**
     * 获取主键字段信息
     */
    protected List<ColumnMeta> getPrimaryMetas(Record record) {
        List<ColumnMeta> result = Lists.newArrayList();
        for (ColumnValue col : record.getPrimaryKeys()) {
            result.add(col.getColumn());
        }
        return result;
    }

    /**
     * 获取普通列字段信息
     */
    protected List<ColumnMeta> getColumnMetas(Record record) {
        List<ColumnMeta> result = Lists.newArrayList();
        for (ColumnValue col : record.getColumns()) {
            result.add(col.getColumn());
        }
        return result;
    }

    /**
     * 获取主键字段信息
     */
    protected String[] getPrimaryNames(Record record) {
        String[] result = new String[record.getPrimaryKeys().size()];
        int i = 0;
        for (ColumnValue col : record.getPrimaryKeys()) {
            result[i++] = col.getColumn().getName();
        }
        return result;
    }

    /**
     * 获取主键字段信息，从Table元数据中获取。因为物化视图由with primary 改为with (shardkey)
     *
     * @param tableMeta
     * @return
     */
    protected String[] getPrimaryNames(Table tableMeta) {
        String[] result = new String[tableMeta.getPrimaryKeys().size()];
        int i = 0;
        for (ColumnMeta col : tableMeta.getPrimaryKeys()) {
            result[i++] = col.getName();
        }
        return result;
    }

    /**
     * 获取普通列字段信息
     */
    protected String[] getColumnNames(Record record) {
        String[] result = new String[record.getColumns().size()];
        int i = 0;
        for (ColumnValue col : record.getColumns()) {
            result[i++] = col.getColumn().getName();
        }
        return result;
    }

}
