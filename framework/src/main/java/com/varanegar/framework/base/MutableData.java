package com.varanegar.framework.base;

public class MutableData<T> {
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    private T data;
    public MutableData(T data){
        this.data = data;
    }
}
