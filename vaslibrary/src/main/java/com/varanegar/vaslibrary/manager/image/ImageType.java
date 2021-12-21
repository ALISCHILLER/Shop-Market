package com.varanegar.vaslibrary.manager.image;

/**
 * Created by A.Torabi on 11/4/2017.
 */

public enum ImageType {
    CustomerCallPicture("CustomerCallPicture"),
    QuestionnaireAttachments("QuestionnaireAttachments"),
    CatalogLarge("CatalogLarge"),
    CatalogSmall("CatalogSmall"),
    ProductGroup("ProductGroup"),
    Product("Product"),
    NationalCard("NationalCard"),
    Logo("Logo");

    public String getName() {
        return name;
    }

    private final String name;

    ImageType(String name) {
        this.name = name;
    }
}
