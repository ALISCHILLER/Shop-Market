package com.varanegar.framework.database.mapper;

import com.varanegar.framework.database.model.BaseModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 6/2/2018.
 */

public abstract class ContentValueMapList<T2, T extends BaseModel> {
    private int idx = 0;
    private List<T2> list = new ArrayList<>();

    public T2 getCurrentItem() {
        if (idx == 0)
            return list.get(0);
        else
            return list.get(idx - 1);
    }

    public ContentValueMapList(List<T2> items) {
        list = items;
    }

    public boolean hasNext() {
        return idx < list.size();
    }

    public ContentValueMap<T2, T> getNextMap() {
        if (idx >= list.size())
            return null;
        else {
            ContentValueMap<T2, T> contentValueMap = getContentValueMap(list.get(idx));
            idx++;
            return contentValueMap;
        }
    }

    protected abstract ContentValueMap<T2, T> getContentValueMap(T2 item);

    public abstract UUID getUniqueId(T2 item);
}
