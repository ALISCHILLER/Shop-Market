package com.varanegar.framework.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.mapper.ContentValueMap;
import com.varanegar.framework.database.mapper.ContentValueMapList;
import com.varanegar.framework.database.mapper.ContentValuesMapper;
import com.varanegar.framework.database.mapper.CursorMapper;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.database.model.ModelProjection;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by atp on 8/10/2016.
 */
public class BaseRepository<T extends BaseModel> {
    private DbHandler dbHandler;
    private CursorMapper<T> cursorMapper;
    private ContentValuesMapper<T> contentValuesMapper;

    public BaseRepository(@NonNull CursorMapper<T> mapper, @NonNull ContentValuesMapper<T> contentValuesMapper) {
        this.dbHandler = VaranegarApplication.getInstance().getDbHandler();
        this.cursorMapper = mapper;
        this.contentValuesMapper = contentValuesMapper;
    }

    public long insert(@NonNull T item) throws NullPointerException {
        ContentValues contentValues = null;
        try {
            SQLiteDatabase database = dbHandler.getWritableDatabase();
            database.execSQL("PRAGMA foreign_keys=ON");
            if (contentValuesMapper.getUniqueIdGenerationPolicy().contains("Insert") && item.UniqueId == null)
                item.UniqueId = UUID.randomUUID();
            contentValues = contentValuesMapper.map(item);
            long i = database.insertOrThrow("`" + contentValuesMapper.getTblName() + "`", null, contentValues);
            return i;
        } catch (Exception ex) {
            if (contentValues != null)
                Timber.wtf(ex, "Model = " + contentValuesMapper.getTblName() + " Content values = " + contentValues.toString());
            else
                Timber.wtf(ex, "Model = " + contentValuesMapper.getTblName());
            throw ex;
        }
    }

    public long insert(@NonNull Iterable<T> items) throws IllegalArgumentException {
        long i = 0;
        if (!items.iterator().hasNext()) {
            Timber.wtf("Inserting an empty list of %s", items.getClass());
            throw new IllegalArgumentException(String.format("Inserting an empty list. %s ", items.getClass().getName()));
        }

        final SQLiteDatabase database = dbHandler.getWritableDatabase();
        database.execSQL("PRAGMA foreign_keys=ON");
        ContentValues contentValues = null;
        boolean uniqueIdPolicy = contentValuesMapper.getUniqueIdGenerationPolicy().contains("Insert");
        try {
            database.beginTransaction();
            for (T item : items) {
                if (uniqueIdPolicy && item.UniqueId == null)
                    item.UniqueId = UUID.randomUUID();
                contentValues = contentValuesMapper.map(item);
                database.insertOrThrow(contentValuesMapper.getTblName(), null, contentValues);
                i++;
            }
            database.setTransactionSuccessful();
            return i;
        } catch (Exception ex) {
            if (contentValues != null)
                Timber.wtf(ex, "Model = " + contentValuesMapper.getTblName() + " Content values = " + contentValues.toString());
            else
                Timber.wtf(ex, "Model = " + contentValuesMapper.getTblName());
            throw ex;
        } finally {
            database.endTransaction();
        }
    }

    public long insertOrUpdate(@NonNull T item) throws NullPointerException {
        if (item.UniqueId != null) {
            if (getItem(item.UniqueId) == null) {
                insert(item);
                return 1;
            } else
                return update(item);
        } else {
            Timber.wtf("UniqueId of %s is null", item.getClass().getName());
            throw new NullPointerException(String.format("UniqueId od %s is null.", item.getClass().getName()));
        }
    }

    public long insertOrUpdate(@NonNull Iterable<T> items) throws NullPointerException {
        long i = 0;
        if (!items.iterator().hasNext()) {
            Timber.wtf("Inserting an empty list of %s ", items.getClass().getName());
            throw new IllegalArgumentException(String.format("Inserting an empty list. %s", items.getClass().getName()));
        }
        final SQLiteDatabase database = dbHandler.getWritableDatabase();
        database.execSQL("PRAGMA foreign_keys=ON");
        ContentValues contentValues = null;
        boolean insertPolicy = contentValuesMapper.getUniqueIdGenerationPolicy().contains("Insert");
        boolean updatePolicy = contentValuesMapper.getUniqueIdGenerationPolicy().contains("Update");
        try {
            database.beginTransaction();
            for (T item : items) {
                if (item.UniqueId != null) {
                    Cursor cursor = database.rawQuery("SELECT * FROM " + getTblName() + " WHERE UniqueId = ?", new String[]{item.UniqueId.toString()});
                    if (cursor.getCount() > 0) {
                        UUID oldUniqueId = item.UniqueId;
                        if (updatePolicy)
                            item.UniqueId = UUID.randomUUID();
                        contentValues = contentValuesMapper.map(item);
                        int c = database.update(getTblName(), contentValues, " UniqueId=?", new String[]{oldUniqueId.toString()});
                        i += c;
                    } else {
                        if (insertPolicy && item.UniqueId == null)
                            item.UniqueId = UUID.randomUUID();
                        contentValues = contentValuesMapper.map(item);
                        database.insertOrThrow(contentValuesMapper.getTblName(), null, contentValues);
                        i++;
                    }
                    cursor.close();
                } else {
                    Timber.wtf("UniqueId of %s  is null", item.getClass().getName());
                    throw new NullPointerException(String.format("UniqueId of %s  is null", item.getClass().getName()));
                }
            }
            database.setTransactionSuccessful();
            return i;
        } catch (Exception ex) {
            if (contentValues != null)
                Timber.wtf(ex, "Model = " + contentValuesMapper.getTblName() + " Content values = " + contentValues.toString());
            else
                Timber.wtf(ex, "Model = " + contentValuesMapper.getTblName());
            throw ex;
        } finally {
            database.endTransaction();
        }

    }

    public long update(@NonNull T item) throws NullPointerException {
        SQLiteDatabase database = dbHandler.getWritableDatabase();
        database.execSQL("PRAGMA foreign_keys=ON");
        if (item.UniqueId != null) {
            UUID oldUniqueId = item.UniqueId;
            if (contentValuesMapper.getUniqueIdGenerationPolicy().contains("Update"))
                item.UniqueId = UUID.randomUUID();
            ContentValues contentValues = contentValuesMapper.map(item);
            try {
                return database.update(getTblName(), contentValues, " UniqueId=?", new String[]{oldUniqueId.toString()});
            } catch (Exception ex) {
                if (contentValues == null)
                    Timber.wtf(ex, "Model = " + contentValuesMapper.getTblName());
                else
                    Timber.wtf(ex, "Model = " + contentValuesMapper.getTblName() + " Content values = " + contentValues.toString());
                throw ex;
            }
        } else {
            Timber.wtf("UniqueId of %s is null", item.getClass().getName());
            throw new NullPointerException("Item uniqueId is null.");
        }
    }

    public long update(@NonNull Iterable<T> items) throws NullPointerException {
        long i = 0;
        if (!items.iterator().hasNext()) {
            Timber.wtf("updating an empty list of %s ", items.getClass().getName());
            throw new IllegalArgumentException(String.format("updating an empty list. %s", items.getClass().getName()));
        }
        final SQLiteDatabase database = dbHandler.getWritableDatabase();
        database.execSQL("PRAGMA foreign_keys=ON");
        ContentValues contentValues = null;
        try {
            database.beginTransaction();
            for (T item : items) {
                if (item.UniqueId != null) {
                    UUID oldUniqueId = item.UniqueId;
                    contentValues = contentValuesMapper.map(item);
                    int c = database.update(getTblName(), contentValues, " UniqueId=?", new String[]{oldUniqueId.toString()});
                    i += c;
                } else {
                    Timber.wtf("UniqueId of %s  is null", item.getClass().getName());
                    throw new NullPointerException(String.format("UniqueId of %s  is null", item.getClass().getName()));
                }
            }
            database.setTransactionSuccessful();
            return i;
        } catch (Exception ex) {
            if (contentValues != null)
                Timber.wtf(ex, "Model = " + contentValuesMapper.getTblName() + " Content values = " + contentValues.toString());
            else
                Timber.wtf(ex, "Model = " + contentValuesMapper.getTblName());
            throw ex;
        } finally {
            database.endTransaction();
        }

    }

    public <T2> long update(ContentValueMap<T2, T> map, Criteria criteria) throws NullPointerException {
        SQLiteDatabase database = dbHandler.getWritableDatabase();
        database.execSQL("PRAGMA foreign_keys=ON");
        ContentValues contentValues = map.getContentValues();
        String whereClause = criteria.build();
        String[] parameters = criteria.buildStringParameters();
        try {
            return database.update(getTblName(), contentValues, whereClause, parameters);
        } catch (Exception ex) {
            if (contentValues == null)
                Timber.wtf(ex, "Model = " + contentValuesMapper.getTblName());
            else
                Timber.wtf(ex, "Model = " + contentValuesMapper.getTblName() + " Content values = " + contentValues.toString());
            throw ex;
        }
    }

    public <T2> long update(ContentValueMapList<T2, T> map) throws NullPointerException {
        SQLiteDatabase database = dbHandler.getWritableDatabase();
        database.execSQL("PRAGMA foreign_keys=ON");
        database.beginTransaction();
        ContentValues contentValues = null;
        long i = 0;
        try {
            while (map.hasNext()) {
                ContentValueMap<T2, T> contentValueMap = map.getNextMap();
                contentValues = contentValueMap.getContentValues();
                String uniqueId = map.getUniqueId(map.getCurrentItem()).toString();
                int c = database.update(getTblName(), contentValues, " UniqueId=?", new String[]{uniqueId});
                i += c;
            }
            database.setTransactionSuccessful();
            return i;
        } catch (Exception ex) {
            if (contentValues == null)
                Timber.wtf(ex, "Model = " + contentValuesMapper.getTblName());
            else
                Timber.wtf(ex, "Model = " + contentValuesMapper.getTblName() + " Content values = " + contentValues.toString());
            throw ex;
        } finally {
            database.endTransaction();
        }

    }

    public long update(@NonNull T item, ModelProjection<T>... projections) throws NullPointerException {
        Set<String> keys = new HashSet<>();
        for (ModelProjection<T> projection :
                projections) {
            keys.add(projection.getSimpleName());
            keys.add(projection.getName());
        }
        return update(item, keys);
    }

    private long update(@NonNull T item, Set<String> columns) throws NullPointerException {
        SQLiteDatabase database = dbHandler.getWritableDatabase();
        database.execSQL("PRAGMA foreign_keys=ON");
        if (item.UniqueId != null) {
            UUID oldUniqueId = item.UniqueId;
            if (contentValuesMapper.getUniqueIdGenerationPolicy().contains("Update"))
                item.UniqueId = UUID.randomUUID();
            ContentValues contentValues = contentValuesMapper.map(item);
            ContentValues contentValues2 = contentValuesMapper.map(item);
            for (String key :
                    contentValues.keySet()) {
                if (!columns.contains(key))
                    contentValues2.remove(key);
            }
            try {
                return database.update(getTblName(), contentValues2, " UniqueId=?", new String[]{oldUniqueId.toString()});
            } catch (Exception ex) {
                if (contentValues == null)
                    Timber.wtf(ex, "Model = " + contentValuesMapper.getTblName());
                else
                    Timber.wtf(ex, "Model = " + contentValuesMapper.getTblName() + " Content values = " + contentValues.toString());
                throw ex;
            }
        } else {
            Timber.wtf("UniqueId of %s is null", item.getClass().getName());
            throw new NullPointerException("Item uniqueId is null.");
        }
    }

    public long update(@NonNull Iterable<T> items, ModelProjection<T>... projections) throws NullPointerException {
        Set<String> keys = new HashSet<>();
        for (ModelProjection<T> projection :
                projections) {
            keys.add(projection.getSimpleName());
            keys.add(projection.getName());
        }
        return update(items, keys);
    }

    private long update(@NonNull Iterable<T> items, Set<String> columns) throws NullPointerException {
        long i = 0;
        if (!items.iterator().hasNext()) {
            Timber.wtf("updating an empty list of %s ", items.getClass().getName());
            throw new IllegalArgumentException(String.format("updating an empty list. %s", items.getClass().getName()));
        }
        final SQLiteDatabase database = dbHandler.getWritableDatabase();
        database.execSQL("PRAGMA foreign_keys=ON");
        ContentValues contentValues = null;
        try {
            database.beginTransaction();
            for (T item : items) {
                if (item.UniqueId != null) {
                    UUID oldUniqueId = item.UniqueId;
                    contentValues = contentValuesMapper.map(item);
                    ContentValues contentValues2 = contentValuesMapper.map(item);
                    for (String key :
                            contentValues.keySet()) {
                        if (!columns.contains(key))
                            contentValues2.remove(key);
                    }
                    int c = database.update(getTblName(), contentValues2, " UniqueId=?", new String[]{oldUniqueId.toString()});
                    i += c;
                } else {
                    Timber.wtf("UniqueId of %s  is null", item.getClass().getName());
                    throw new NullPointerException(String.format("UniqueId of %s  is null", item.getClass().getName()));
                }
            }
            database.setTransactionSuccessful();
            return i;
        } catch (Exception ex) {
            if (contentValues != null)
                Timber.wtf(ex, "Model = " + contentValuesMapper.getTblName() + " Content values = " + contentValues.toString());
            else
                Timber.wtf(ex, "Model = " + contentValuesMapper.getTblName());
            throw ex;
        } finally {
            database.endTransaction();
        }

    }

    public long delete(@NonNull UUID uniqueId) throws NullPointerException {
        try {
            SQLiteDatabase database = dbHandler.getWritableDatabase();
            database.execSQL("PRAGMA foreign_keys=ON");
            return database.delete(contentValuesMapper.getTblName(), "UniqueId = ?", new String[]{uniqueId.toString()});
        } catch (Exception ex) {
            Timber.wtf(ex, "Model = " + contentValuesMapper.getTblName());
            throw ex;
        }
    }

    public long delete(@NonNull Criteria criteria) {
        try {
            String whereClause = criteria.build();
            String[] parameters = criteria.buildStringParameters();
            SQLiteDatabase database = dbHandler.getWritableDatabase();
            database.execSQL("PRAGMA foreign_keys=ON");
            return database.delete(contentValuesMapper.getTblName(), whereClause, parameters);
        } catch (Exception ex) {
            Timber.wtf(ex, "Model = " + contentValuesMapper.getTblName());
            throw ex;
        }
    }

    public long deleteAll() {
        try {
            SQLiteDatabase database = dbHandler.getWritableDatabase();
            database.execSQL("PRAGMA foreign_keys=ON");
            return database.delete(contentValuesMapper.getTblName(), "1", new String[]{});
        } catch (Exception ex) {
            Timber.wtf(ex, "Model = " + contentValuesMapper.getTblName());
            throw ex;
        }
    }

    public void resetPaging() {
        mPagingIndex = 0;
    }

    public void setPagingSize(int limit) {
        if (limit == 0) {
            Timber.wtf("limit should be larger than 0");
            throw new IllegalArgumentException("limit should be larger than 0");
        }
        mLimit = limit;
    }

    private int mPagingIndex = 0;
    private int mLimit = 20;

    @Nullable
    public List<T> getPagedItems(@NonNull Query query) {
        try {
            Query q = query.clone();
            q.skip(mPagingIndex).take(mLimit);
            String[] parameters = null;
            if (q.build().contains("?"))
                parameters = q.buildStringParameters();
            return getPagedItems(q.build(), parameters);
        } catch (CloneNotSupportedException e) {
            Timber.wtf(e);
            return null;
        } catch (Exception ex) {
            Timber.wtf(ex);
            throw ex;
        }
    }

    @NonNull
    public List<T> getPagedItems(@NonNull String query, @Nullable String[] parameters) {
        final List<T> items = new ArrayList<>();
        SQLiteDatabase database = dbHandler.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, parameters);
            int size = cursor.getCount();
            for (int i = 0; i < size; i++) {
                cursor.moveToPosition(i);
                items.add(cursorMapper.map(cursor));
            }
            mPagingIndex += items.size();
            return items;
        } catch (IllegalStateException ex) {
            throw new RuntimeException("Verify that the model class matches the table \"" + getTblName() + "\" in database ", ex);
        } catch (Exception ex) {
            Timber.wtf(ex);
            throw ex;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @NonNull
    public List<T> getItems(@NonNull Query query) {
        String[] parameters = null;
        if (query.build().contains("?"))
            parameters = query.buildStringParameters();
        return getItems(query.build(), parameters);
    }

    @NonNull
    public List<T> getItems(@NonNull String query, @Nullable String[] parameters) {
        final List<T> items = new ArrayList<>();
        SQLiteDatabase database = dbHandler.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, parameters);
            int size = cursor.getCount();
            for (int i = 0; i < size; i++) {
                cursor.moveToPosition(i);
                items.add(cursorMapper.map(cursor));
            }
            return items;
        } catch (IllegalStateException ex) {
            Timber.wtf(ex);
            throw new RuntimeException("Verify that the model class matches the table \"" + getTblName() + "\" in database ", ex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Nullable
    public T getItem(@NonNull UUID uniqueId) {
        return getItem("SELECT * FROM " + getTblName() + " WHERE UniqueId = ?", new String[]{uniqueId.toString()});
    }

    public T getItem(Query query) {
        if (query.hasLimit())
            throw new UnsupportedOperationException("Query that was passed to this function should not have limit");
        query.take(1);
        String[] parameters = null;
        if (query.build().contains("?"))
            parameters = query.buildStringParameters();
        return getItem(query.build(), parameters);
    }

    @Nullable
    public T getItem(@NonNull String query, @Nullable String[] parameters) {
        SQLiteDatabase database = dbHandler.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, parameters);
            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                T item = cursorMapper.map(cursor);
                cursor.close();
                return item;
            } else
                return null;
        } catch (IllegalStateException ex) {
            Timber.wtf(ex);
            throw new RuntimeException("Verify that the model class matches the table \"" + getTblName() + "\" in database ", ex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @NonNull
    public ContentValues[] query(@NonNull Query query) {
        String[] parameters = null;
        if (query.build().contains("?"))
            parameters = query.buildStringParameters();
        return query(query.build(), parameters);
    }

    @NonNull
    public ContentValues[] query(@NonNull String query, @Nullable String[] parameters) {
        SQLiteDatabase database = dbHandler.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, parameters);
            int count = cursor.getCount();
            ContentValues[] items = new ContentValues[count];
            int size = cursor.getCount();
            for (int i = 0; i < size; i++) {
                cursor.moveToPosition(i);
                items[i] = contentValuesMapper.map(cursorMapper.map(cursor));
            }
            return items;
        } catch (IllegalStateException ex) {
            Timber.wtf(ex);
            throw new RuntimeException("Verify that the model class matches the table \"" + getTblName() + "\" in database ", ex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @NonNull
    private String getTblName() {
        return contentValuesMapper.getTblName();
    }

    public void setPagingIndex(int pagingIndex) {
        this.mPagingIndex = pagingIndex;
    }

    public void execSql(@NonNull String sqlQuery) throws NullPointerException {
        dbHandler.execSql(sqlQuery);
    }

    public long insert(TableMap tableMap, @Nullable Criteria criteria) {
        SQLiteDatabase database = dbHandler.getWritableDatabase();
        if (tableMap.getColumnsCount() == 0) {
            Cursor cursor = database.rawQuery("PRAGMA table_info(" + tableMap.getSrc() + ")", null);
            StringBuilder columns = new StringBuilder();
            if (cursor != null) {
                cursor.moveToFirst();
                boolean firstRow = true;
                int size = cursor.getCount();
                for (int i = 0; i < size; i++) {
                    cursor.moveToPosition(i);
                    String columnName = cursor.getString(cursor.getColumnIndex("name"));
                    if (!tableMap.isColumnIgnored(columnName)) {
                        if (firstRow)
                            columns.append(columnName);
                        else
                            columns.append(",").append(columnName);
                    }
                    firstRow = false;
                }
            }
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO '")
                    .append(tableMap.getDest()).append("' (")
                    .append(columns).append(")")
                    .append(" SELECT ").append(columns).append(" FROM ")
                    .append(tableMap.getSrc());
            if (criteria != null) {
                String whereClause = criteria.build();
                String[] parameters = criteria.buildStringParameters();
                if (parameters != null && parameters.length > 0) {
                    for (String parameter : parameters) {
                        whereClause = whereClause.replace("?", "'" + parameter + "'");
                    }
                    sql.append(" WHERE ").append(whereClause);
                }
            }
            String raw = sql.toString();
            database.execSQL(raw);
            try {
                Cursor result = database.rawQuery("select changes();", null);
                if (result != null) {

                    result.moveToFirst();
                    long c = result.getLong(0);
                    return c;
                }
                return 0;
            } catch (Exception ignored) {
                Timber.e(ignored);
                return 0;
            }
        } else {
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO '").append(tableMap.getDest()).append("' (");
            StringBuilder values = new StringBuilder();
            if (tableMap.getColumns().size() > 1)
                for (int i = 0; i < tableMap.getColumns().size() - 1; i++) {
                    ColumnMap column = tableMap.getColumns().get(i);
                    if (!tableMap.isColumnIgnored(column.getSrc())) {
                        sql.append("'").append(column.getDest()).append("' , ");
                        values.append(column.getSrc()).append(",");
                    }
                }
            ColumnMap field = tableMap.getColumns().get(tableMap.getColumnsCount() - 1);
            sql.append("'").append(field.getDest()).append("') ");
            values.append(field.getSrc());
            sql.append(" SELECT ").append(values.toString()).append(" FROM ").append(tableMap.getSrc());
            if (criteria != null) {
                String whereClause = criteria.build();
                String[] parameters = criteria.buildStringParameters();
                if (parameters != null && parameters.length > 0) {
                    for (String parameter : parameters) {
                        whereClause = whereClause.replaceFirst("\\?", "'" + parameter + "'");
                    }
                    sql.append(" WHERE ").append(whereClause);
                }
            }
            String raw = sql.toString();
            database.execSQL(raw);
            try {
                Cursor result = database.rawQuery("select changes();", null);
                if (result != null) {

                    result.moveToFirst();
                    long c = result.getLong(0);
                    return c;
                }
                return 0;
            } catch (Exception ignored) {
                Timber.e(ignored);
                return 0;
            }
        }
    }
}
