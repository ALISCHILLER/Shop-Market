package com.varanegar.vaslibrary.manager.picture;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.manager.image.ImageManager;
import com.varanegar.vaslibrary.manager.image.ImageType;
import com.varanegar.vaslibrary.model.picturesubject.PictureCustomerModel;
import com.varanegar.vaslibrary.model.picturesubject.PictureCustomerViewModel;
import com.varanegar.vaslibrary.model.picturesubject.PictureFile;
import com.varanegar.vaslibrary.model.picturesubject.PictureFileModel;
import com.varanegar.vaslibrary.model.picturesubject.PictureFileModelRepository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 10/22/2017.
 */

public class PictureFileManager extends BaseManager<PictureFileModel> {
    public PictureFileManager(@NonNull Context context) {
        super(context, new PictureFileModelRepository());
    }

    public String getPictureFileName(PictureFileModel pictureFileModel) {
        ImageManager imageManager = new ImageManager(getContext());
        return imageManager.getImagePath(pictureFileModel.CustomerId, pictureFileModel.FileId + ".png", ImageType.CustomerCallPicture);
        //return pictureCustomerModel.CustomerId + "_" + pictureCustomerModel.FileId + ".png";
    }

    public void save(final PictureCustomerViewModel selectedSubject, Bitmap bitmap, int width, int height, boolean isPortrait) throws IOException, ValidationException, DbException {
        PictureFileModel pictureFileModel = new PictureFileModel();
        pictureFileModel.UniqueId = UUID.randomUUID();
        pictureFileModel.FileId = UUID.randomUUID();
        pictureFileModel.CustomerId = selectedSubject.CustomerId;
        pictureFileModel.PictureSubjectId = selectedSubject.PictureSubjectId;
        pictureFileModel.Width = width;
        pictureFileModel.Height = height;
        pictureFileModel.IsPortrait = isPortrait;
        ImageManager imageManager = new ImageManager(getContext());
        imageManager.saveImage(bitmap, 30, selectedSubject.CustomerId,
                ImageType.CustomerCallPicture,
                pictureFileModel.FileId + ".png", true);
//        FileOutputStream out = null;
//        out = getContext().openFileOutput(getPictureFileName(pictureFileModel), Context.MODE_PRIVATE);
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, out);
        insertOrUpdate(pictureFileModel);
        PictureCustomerManager pictureCustomerManager = new PictureCustomerManager(getContext());
        PictureCustomerModel pictureCustomerModel = pictureCustomerManager.getPicture(selectedSubject.CustomerId, selectedSubject.PictureSubjectId);
        pictureCustomerModel.NoPictureReason = null;
        pictureCustomerModel.FileId = pictureFileModel.FileId;
        pictureCustomerManager.update(pictureCustomerModel);
    }

    public PictureFileModel getPictureFile(UUID fileId) {
        Query query = new Query();
        query.from(PictureFile.PictureFileTbl).whereAnd(Criteria.equals(PictureFile.FileId, fileId.toString()));
        return getItem(query);
    }

    public void deleteFile(UUID fileId) throws DbException {
        PictureFileModel file = getPictureFile(fileId);
        if (file == null)
            return;
        ImageManager imageManager = new ImageManager(getContext());
        boolean deleted = imageManager.deleteImage(file.CustomerId, file.FileId + ".png", ImageType.CustomerCallPicture);
        if (deleted) {
            delete(file.UniqueId);
        }
    }

    public void deleteFiles(@NonNull final List<PictureFileModel> files) throws DbException {
        ImageManager imageManager = new ImageManager(getContext());
        if (files.size() > 0) {
            for (final PictureFileModel file :
                    files) {
                boolean deleted = imageManager.deleteImage(file.CustomerId, file.FileId + ".png", ImageType.CustomerCallPicture);
                if (deleted) {
                    delete(file.UniqueId);
                }
            }
        }
    }

    public void deleteFiles(UUID customerId) throws DbException {
        List<PictureFileModel> files = getItems(new Query().from(PictureFile.PictureFileTbl).whereAnd(Criteria.equals(PictureFile.CustomerId, customerId.toString())));
        if (files.size() > 0)
            deleteFiles(files);
    }

}
