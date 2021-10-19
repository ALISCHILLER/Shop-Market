package com.varanegar.vaslibrary.print.datahelper;

import com.varanegar.printlib.layout.datamodel.ObjectBinding;
import com.varanegar.printlib.layout.datamodel.PrintData;

public class ReturnLineBinding extends ObjectBinding {
    @PrintData(name = Keys.RETURN.RETURN_ITEM_ROW , description = "شماره ردیف کالا در برگشتی")
    public String Row;
    @PrintData(name = Keys.RETURN.PRODUCT_NAME, description = "نام کالا")
    public String ProductName;
    @PrintData(name = Keys.RETURN.PRODUCT_CODE, description = "کد کالا")
    public String ProductCode;
    @PrintData(name = Keys.RETURN.UNIT_NAME, description = "نام واحد اندازی گیری")
    public String UnitName;
    @PrintData(name = Keys.RETURN.TOTAL_QTY, description = "تعداد کل")
    public String TotalQty;
    @PrintData(name = Keys.RETURN.CONVERT_FACTOR, description = "ضریب تبدیل")
    public String ConvertFactor;
    @PrintData(name = Keys.RETURN.ALL_QTYS, description = "تعداد به تفکیک واحدهای کالا")
    public String Qty;
    @PrintData(name = Keys.RETURN.UNIT_PRICE, description = "فی واحد")
    public String UnitPrice;
    @PrintData(name = Keys.RETURN.REQUEST_TOTAL_AMOUNT, description = "ارزش برگشتی (برای یک سطر)")
    public String TotalAmount;
    @PrintData(name = Keys.RETURN.REQUEST_DISCOUNT_AMOUNT, description = "مبلغ تخفیف (برای یک سطر)")
    public String DiscountAmount;
    @PrintData(name = Keys.RETURN.REQUEST_ADD_AMOUNT, description = "مبلغ اضافات (برای یک سطر)")
    public String AddAmount;
    @PrintData(name = Keys.RETURN.REQUEST_ADD1_AMOUNT, description = "مبلغ اضافات add1 (برای یک سطر)")
    public String Add1Amount;
    @PrintData(name = Keys.RETURN.REQUEST_ADD2_AMOUNT, description = "مبلغ اضافات add2 (برای یک سطر)")
    public String Add2Amount;
    @PrintData(name = Keys.RETURN.REQUEST_TAX_AMOUNT, description = "مبلغ مالیات  (برای یک سطر)")
    public String TaxAmount;
    @PrintData(name = Keys.RETURN.REQUEST_CHARGE_AMOUNT, description = "مبلغ اضافات charge (برای یک سطر)")
    public String ChargeAmount;
    @PrintData(name = Keys.RETURN.REQUEST_NET_AMOUNT, description = "مبلغ خالص (برای یک سطر)")
    public String NetAmount;


    public ReturnLineBinding() {
        super("اطلاعات هر سطر برگشتی");
    }
}
