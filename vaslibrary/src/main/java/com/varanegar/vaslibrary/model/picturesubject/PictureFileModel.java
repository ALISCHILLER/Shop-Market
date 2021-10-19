package com.varanegar.vaslibrary.model.picturesubject;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Torabi on 10/18/2017.
 */
@Table
public class PictureFileModel extends BaseModel {
    @Column
    public UUID PictureSubjectId;
    @Column
    public UUID CustomerId;
    @Column
    public UUID FileId;
    @Column
    public int Width;
    @Column
    public int Height;
    @Column
    public boolean IsPortrait;

    public double getRatio() {
        return (double) Width / (double) Height;
    }
}
