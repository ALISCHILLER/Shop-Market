package com.varanegar.framework.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.mapper.ContentValueMap;
import com.varanegar.framework.database.mapper.ContentValueMapList;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.database.model.ModelProjection;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.framework.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//import net.sf.oval.ConstraintViolation;


/**
 * Created by atp on 12/24/2016.
 */
public class BaseManager<T extends BaseModel> {

    protected BaseRepository<T> getRepository() {
        return baseRepository;
    }

    protected BaseRepository<T> baseRepository;
    private Context context;

    protected Context getContext() {
        return context;
    }

    public BaseManager(@NonNull Context context, @NonNull BaseRepository<T> baseRepository) {
        this.baseRepository = baseRepository;
        this.context = context;
    }

    public long insert(@NonNull final T item) throws ValidationException, DbException {
        Validator<T> validator = new Validator<>();
        validator.validate(item);
        try {
            return baseRepository.insert(item);
        } catch (Exception ex) {
            throw new DbException(ex.getMessage());
        }
    }


    public long insert(@NonNull final Iterable<T> items) throws ValidationException, DbException {
        Validator<T> validator = new Validator<>();
        validator.validate(items);
        try {
            return baseRepository.insert(items);
        } catch (Exception ex) {
            throw new DbException(ex.getMessage());
        }
    }


    public long update(@NonNull final T item) throws ValidationException, DbException {
        Validator<T> validator = new Validator<>();
        validator.validate(item);
        try {
            return baseRepository.update(item);
        } catch (Exception ex) {
            throw new DbException(ex.getMessage());
        }
    }

    public long update(@NonNull final Iterable<T> items) throws ValidationException, DbException {
        Validator<T> validator = new Validator<>();
        validator.validate(items);
        try {
            return baseRepository.update(items);
        } catch (Exception ex) {
            throw new DbException(ex.getMessage());
        }
    }

    public <T2> long update(ContentValueMap<T2, T> map, Criteria criteria) throws DbException {
        try {
            return baseRepository.update(map, criteria);
        } catch (Exception ex) {
            throw new DbException(ex.getMessage());
        }
    }

    public <T2> long update(ContentValueMapList<T2, T> map) throws DbException {
        try {
            return baseRepository.update(map);
        } catch (Exception ex) {
            throw new DbException(ex.getMessage());
        }
    }

    public long update(@NonNull T item, ModelProjection<T>... projections) throws DbException {
        try {
            return baseRepository.update(item, projections);
        } catch (Exception ex) {
            throw new DbException(ex.getMessage());
        }
    }

    public long update(@NonNull Iterable<T> items, ModelProjection<T>... projections) throws DbException {
        try {
            return baseRepository.update(items, projections);
        } catch (Exception ex) {
            throw new DbException(ex.getMessage());
        }
    }

    public long insertOrUpdate(@NonNull final T item) throws ValidationException, DbException {
        Validator<T> validator = new Validator<>();
        validator.validate(item);
        try {
            return baseRepository.insertOrUpdate(item);
        } catch (Exception ex) {
            throw new DbException(ex.getMessage());
        }
    }

    public long insertOrUpdate(@NonNull final Iterable<T> items) throws ValidationException, DbException {
        Validator<T> validator = new Validator<>();
        validator.validate(items);
        try {
            return baseRepository.insertOrUpdate(items);
        } catch (Exception ex) {
            throw new DbException(ex.getMessage());
        }
    }

    public long sync(@NonNull final Iterable<T> items) throws ValidationException, DbException {
        try {
            final List<T> removedItems = new ArrayList<>();
            List<T> updatedItems = new ArrayList<>();
            for (T item :
                    items) {
                if (item.isRemoved)
                    removedItems.add(item);
                else
                    updatedItems.add(item);
            }
            VaranegarApplication.getInstance().getDbHandler().beginTransaction();
            long affectedRows = 0;
            if (updatedItems.size() > 0)
                affectedRows = insertOrUpdate(updatedItems);
            if (removedItems.size() > 0)
                for (T item :
                        removedItems) {
                    long deletedRows = delete(item.UniqueId);
                    affectedRows += deletedRows;
                }
            VaranegarApplication.getInstance().getDbHandler().setTransactionSuccessful();
            VaranegarApplication.getInstance().getDbHandler().endTransaction();
            return affectedRows;
        } catch (Exception ex) {
            VaranegarApplication.getInstance().getDbHandler().endTransaction();
            throw new DbException(ex.getMessage());
        }
    }

    public long delete(@NonNull UUID uniqueId) throws DbException {
        try {
            return baseRepository.delete(uniqueId);
        } catch (Exception ex) {
            throw new DbException(ex.getMessage());
        }
    }

    public long delete(@NonNull Criteria criteria) throws DbException {
        try {
            return baseRepository.delete(criteria);
        } catch (Exception ex) {
            throw new DbException(ex.getMessage());
        }

    }


    public long deleteAll() throws DbException {
        try {
            return baseRepository.deleteAll();
        } catch (Exception ex) {
            throw new DbException(ex.getMessage());
        }
    }

    public void resetPaging() {
        baseRepository.resetPaging();
    }

    public void setPagingSize(int limit) {
        baseRepository.setPagingSize(limit);
    }

    @Nullable
    public List<T> getPagedItems(@NonNull Query query) {
        return baseRepository.getPagedItems(query);
    }

    @NonNull
    public List<T> getPagedItems(@NonNull String query, @Nullable String[] parameters) {
        return baseRepository.getPagedItems(query, parameters);
    }

    @NonNull
    public List<T> getItems(@NonNull Query query) {
        return baseRepository.getItems(query);
    }

    @NonNull
    public List<T> getItems(@NonNull String query, @Nullable String[] parameters) {
        return baseRepository.getItems(query, parameters);
    }

    @Nullable
    public T getItem(@NonNull UUID uniqueId) {
        return baseRepository.getItem(uniqueId);
    }

    @Nullable
    public T getItem(@NonNull Query query) {
        return baseRepository.getItem(query);
    }

    @Nullable
    public T getItem(@NonNull String query, @Nullable String[] parameters) {
        return baseRepository.getItem(query, parameters);
    }

    public long insert(TableMap tableMap, @Nullable Criteria criteria) {
        return baseRepository.insert(tableMap, criteria);
    }
}