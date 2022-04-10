package com.varanegar.supervisor.model;

import com.varanegar.supervisor.webapi.model_old.CustomerCallOrderLineOrderQtyDetailViewModel;

import java.util.List;
import java.util.UUID;


public class ProductModel {
    public UUID ProductUniqueId;
    public String ProductName;
    public String ProductCode;
    public List<CustomerCallOrderLineOrderQtyDetailViewModel> ProductUnits;

    @Override
    public String toString() {
        return ProductName + "(" + ProductCode + ")";
    }
}
