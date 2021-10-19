package com.varanegar.vaslibrary.manager.emphaticitems;

import com.varanegar.vaslibrary.model.emphaticproductcount.EmphaticProductCountModel;

import java.util.List;

/**
 * Created by A.Torabi on 7/19/2017.
 */

public class EmphaticItemNotFoundException extends Exception {
    public List<EmphaticProductCountModel> getItems() {
        return items;
    }

    private List<EmphaticProductCountModel> items;

    public EmphaticItemNotFoundException(List<EmphaticProductCountModel> items) {
        this.items = items;
    }
}
