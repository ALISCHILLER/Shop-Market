package com.varanegar.vaslibrary.print.datahelper;

import com.varanegar.printlib.layout.datamodel.ObjectBinding;
import com.varanegar.printlib.layout.datamodel.PrintData;

public class OrderLineBinding extends ObjectBinding {
    @PrintData(name = Keys.ORDER.ORDER_ITEM_ROW , description = "شماره ردیف کالا در سفارش")
    public String Row;
    @PrintData(name = Keys.ORDER.PRODUCT_NAME, description = "نام کالا")
    public String ProductName;
    @PrintData(name = Keys.ORDER.PRODUCT_CODE, description = "کد کالا")
    public String ProductCode;
    @PrintData(name = Keys.ORDER.UNIT_NAME, description = "نام واحد اندازی گیری")
    public String UnitName;
    @PrintData(name = Keys.ORDER.TOTAL_QTY, description = "تعداد کل")
    public String TotalQty;
    @PrintData(name = Keys.ORDER.CONVERT_FACTOR, description = "ضریب تبدیل")
    public String ConvertFactor;
    @PrintData(name = Keys.ORDER.ALL_QTYS, description = "تعداد به تفکیک واحدهای کالا")
    public String Qty;
    @PrintData(name = Keys.ORDER.UNIT_PRICE, description = "فی واحد")
    public String UnitPrice;
    @PrintData(name = Keys.ORDER.REQUEST_TOTAL_AMOUNT, description = "ارزش سفارش (برای یک سطر)")
    public String TotalAmount;
    @PrintData(name = Keys.ORDER.REQUEST_DISCOUNT_AMOUNT, description = "مبلغ تخفیف (برای یک سطر)")
    public String DiscountAmount;
    @PrintData(name = Keys.ORDER.REQUEST_DIS1_AMOUNT, description = "مبلغ تخفیف dis1 (برای یک سطر)")
    public String Dis1Amount;
    @PrintData(name = Keys.ORDER.REQUEST_DIS2_AMOUNT, description = "مبلغ تخفیف dis2 (برای یک سطر)")
    public String Dis2Amount;
    @PrintData(name = Keys.ORDER.REQUEST_DIS3_AMOUNT, description = "مبلغ تخفیف dis3 (برای یک سطر)")
    public String Dis3Amount;
    @PrintData(name = Keys.ORDER.REQUEST_DIS_OTHER_AMOUNT, description = "مبلغ تخفیف other (برای یک سطر)")
    public String DisOtherAmount;
    @PrintData(name = Keys.ORDER.REQUEST_OTHER_DIS_AMOUNT, description = "مبلغ خالص Other (برای یک سطر)")
    public String OtherDiscountAmount;
    @PrintData(name = Keys.ORDER.REQUEST_ADD_AMOUNT, description = "مبلغ اضافات (برای یک سطر)")
    public String AddAmount;
    @PrintData(name = Keys.ORDER.REQUEST_ADD1_AMOUNT, description = "مبلغ اضافات add1 (برای یک سطر)")
    public String Add1Amount;
    @PrintData(name = Keys.ORDER.REQUEST_ADD2_AMOUNT, description = "مبلغ اضافات add2 (برای یک سطر)")
    public String Add2Amount;
    @PrintData(name = Keys.ORDER.REQUEST_ADD_OTHER_AMOUNT, description = "مبلغ اضافات other (برای یک سطر)")
    public String AddOtherAmount;
    @PrintData(name = Keys.ORDER.REQUEST_TAX_AMOUNT, description = "مبلغ مالیات  (برای یک سطر)")
    public String TaxAmount;
    @PrintData(name = Keys.ORDER.REQUEST_CHARGE_AMOUNT, description = "مبلغ اضافات charge (برای یک سطر)")
    public String ChargeAmount;
    @PrintData(name = Keys.ORDER.REQUEST_NET_AMOUNT, description = "مبلغ خالص (برای یک سطر)")
    public String NetAmount;

    public OrderLineBinding() {
        super("اطلاعات هر سطر سفارش");
    }
}
