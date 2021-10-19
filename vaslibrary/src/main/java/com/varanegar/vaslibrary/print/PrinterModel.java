package com.varanegar.vaslibrary.print;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Torabi on 3/15/2018.
 */
@Table
public class PrinterModel extends BaseModel {
    @Column
    public String PrinterName;
    @Column
    public boolean IsDefault;
    @Column
    public boolean IsFound;

    @Override
    public String toString() {
        if (IsFound)
            return "   ✓    " + PrinterName;
        else
            return "   ✗    " + PrinterName;
    }
}
