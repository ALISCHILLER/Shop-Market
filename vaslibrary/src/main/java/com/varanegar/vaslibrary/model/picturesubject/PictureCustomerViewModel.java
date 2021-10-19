package com.varanegar.vaslibrary.model.picturesubject;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 10/18/2017.
 */
@Table
public class PictureCustomerViewModel extends BaseModel {
    @Column
    public UUID PictureSubjectId;
    @Column
    public String Title;
    @Column
    public UUID CustomerId;
    @Column
    public UUID DemandTypeUniqueId;
    @Column
    public boolean AlreadyTaken;
    @Column(isEnum = true)
    public PictureDemandType DemandType;
    @Column
    public String FileIds;
    @Column
    public int FileCount;
    @Column
    public String NoPictureReason;


    @NonNull
    public List<UUID> getFileIds() {
        final List<UUID> fileIds = new ArrayList<>();
        try {
            StringTokenizer tokenizer = new StringTokenizer(FileIds, ":", false);
            while (tokenizer.hasMoreTokens()) {
                String str = tokenizer.nextToken();
                fileIds.add(UUID.fromString(str));
            }
            return fileIds;
        }
        catch (Exception ex){
            Timber.e(ex);
            return fileIds;
        }
    }
}
