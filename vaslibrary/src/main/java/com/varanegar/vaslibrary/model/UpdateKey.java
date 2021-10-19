package com.varanegar.vaslibrary.model;

/**
 * Created by atp on 8/21/2016.
 */
public class UpdateKey {
    public static UpdateKey TourStartTime = new UpdateKey("TourStartTime");
    public static UpdateKey Catalog = new UpdateKey("Catalog");
    public static UpdateKey PriceHistory = new UpdateKey("PriceHistory");
    public static UpdateKey Product = new UpdateKey("Product");
    public static UpdateKey OnHandQty = new UpdateKey("OnHandQty");
    public static UpdateKey VisitTemplatePathCustomer = new UpdateKey("VisitTemplatePathCustomer");
    public static UpdateKey VisitDay = new UpdateKey("VisitDay");
    public static UpdateKey ProductUnit = new UpdateKey("ProductUnit");
    public static UpdateKey Unit = new UpdateKey("Unit");
    public static UpdateKey CustomerOldInvoice = new UpdateKey("CustomerOldInvoice");
    public static UpdateKey ProductMainSubType = new UpdateKey("productMainSubType");
    public static UpdateKey ProductGroup = new UpdateKey("ProductGroup");
    public static UpdateKey DiscountSDS = new UpdateKey("DiscountSDS");
    public static UpdateKey ProductTaxInfo = new UpdateKey("ProductTaxInfo");
    public static UpdateKey DiscountVnLite = new UpdateKey("DiscountVnLite");
    public static UpdateKey UserList = new UpdateKey("User");
    public static UpdateKey Customer = new UpdateKey("Customer");
    public static UpdateKey ProductBoGroup = new UpdateKey("ProductBoGroup");
    public static UpdateKey CatalogFiles = new UpdateKey("CatalogFiles");
    public static UpdateKey CustomerQuestionnaire = new UpdateKey("CustomerQuestionnaire");
    public static UpdateKey EmphaticProducts = new UpdateKey("EmphaticProducts");
    public static UpdateKey ProductCatalogGroup = new UpdateKey("ProductCatalogGroup");
    public static UpdateKey CustomerCardex = new UpdateKey("CustomerCardex");
    public static UpdateKey PictureSubject = new UpdateKey("PictureSubject");
    public static UpdateKey PictureTemplate = new UpdateKey("PictureTemplate");
    public static UpdateKey CustomerPictureSubject = new UpdateKey("CustomerPictureSubject");
    public static UpdateKey QuestionGroup = new UpdateKey("QuestionGroup");
    public static UpdateKey Target = new UpdateKey("Target");
    public static UpdateKey DiscountCondition = new UpdateKey("DiscountCondition");
    public static UpdateKey DiscountItemCount = new UpdateKey("DiscountItemCount");
    public static UpdateKey ContractPriceSDS = new UpdateKey("ContractPriceSDS");
    public static UpdateKey PriceClass = new UpdateKey("PriceClass");
    public static UpdateKey DealerPaymentType = new UpdateKey("DealerPaymentType");
    public static UpdateKey City = new UpdateKey("City");
    public static UpdateKey Bank = new UpdateKey("Bank");
    public static UpdateKey HotSalesOnHandQty = new UpdateKey("HotSalesOnHandQty");
    public static UpdateKey ProductRequest = new UpdateKey("ProductRequest");
    public static UpdateKey ValidPayType = new UpdateKey("ValidPayType");
    public static UpdateKey BatchOnHandQty = new UpdateKey("BatchOnHandQty");
    public static UpdateKey ProductBatchOnHandQty = new UpdateKey("ProductBatchOnHandQty");
    public static UpdateKey State = new UpdateKey("State");
    public static UpdateKey CustomerOwnerType = new UpdateKey("CustomerOwnerType");
    public static UpdateKey County = new UpdateKey("County");
    public static UpdateKey DiscountGood = new UpdateKey("DiscountGood");
    public static UpdateKey PaymentUsances = new UpdateKey("PaymentUsances");
    public static UpdateKey RetSaleHdr = new UpdateKey("RetSaleHdr");
    public static UpdateKey RetSaleItem = new UpdateKey("RetSaleItem");
    public static UpdateKey GoodsNosale = new UpdateKey("GoodsNosale");
    public static UpdateKey GoodsFixUnit = new UpdateKey("GoodsFixUnit");
    public static UpdateKey DisAcc = new UpdateKey("DisAcc");
    public static UpdateKey CustExtraField = new UpdateKey("CustExtraField");
    public static UpdateKey CustomerBoGroup = new UpdateKey("CustomerBoGroup");
    public static UpdateKey CustomerBarcode = new UpdateKey("CustomerBarcode");

    private final String name;

    public UpdateKey(String s) {
        name = s;
    }

    public String getName() {
        return name;
    }

    public static UpdateKey UpdateLog = new UpdateKey("UpdateLog");
}
