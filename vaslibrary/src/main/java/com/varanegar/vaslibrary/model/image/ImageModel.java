package com.varanegar.vaslibrary.model.image;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.Date;
import java.util.UUID;

/**
 * Created by A.Torabi on 11/21/2018.
 */
@Table
public class ImageModel extends BaseModel {
    @Column
    public UUID TokenId;
    @Column
    public String ImageFileName;
    @Column
    public boolean IsDefault;
    @Column
    public String ImageType;
    @Column
    public boolean IsLocal;
    @Column
    public Date LastUpdate;

    public com.varanegar.vaslibrary.manager.image.ImageType getImageType() {
        if (ImageType == null)
            return null;

        if (ImageType.equals(com.varanegar.vaslibrary.manager.image.ImageType.CustomerCallPicture.getName()))
            return com.varanegar.vaslibrary.manager.image.ImageType.CustomerCallPicture;
        else if (ImageType.equals(com.varanegar.vaslibrary.manager.image.ImageType.QuestionnaireAttachments.getName()))
            return com.varanegar.vaslibrary.manager.image.ImageType.QuestionnaireAttachments;
        else if (ImageType.equals(com.varanegar.vaslibrary.manager.image.ImageType.CatalogLarge.getName()))
            return com.varanegar.vaslibrary.manager.image.ImageType.CatalogLarge;
        else if (ImageType.equals(com.varanegar.vaslibrary.manager.image.ImageType.CatalogSmall.getName()))
            return com.varanegar.vaslibrary.manager.image.ImageType.CatalogSmall;
        else if (ImageType.equals(com.varanegar.vaslibrary.manager.image.ImageType.ProductGroup.getName()))
            return com.varanegar.vaslibrary.manager.image.ImageType.ProductGroup;
        else if (ImageType.equals(com.varanegar.vaslibrary.manager.image.ImageType.Product.getName()))
            return com.varanegar.vaslibrary.manager.image.ImageType.Product;
        else return null;
    }
}
