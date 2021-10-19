package com.varanegar.vaslibrary.print.datahelper;


import com.varanegar.printlib.layout.datamodel.ListBinding;
import com.varanegar.printlib.layout.datamodel.ObjectBinding;
import com.varanegar.printlib.layout.datamodel.PrintData;

public class OrderBinding extends ObjectBinding {
    @PrintData(name = Keys.ORDER.ORDER_TOTAL_AMOUNT, description = "ارزش کل سفارش")
    public String TotalAmount;
    @PrintData(name = Keys.ORDER.ORDER_NET_AMOUNT , description = "مبلغ قابل پرداخت سفارش")
    public String NetAmount;
    @PrintData(name = Keys.ORDER.ORDER_DISCOUNT_AMOUNT , description = "حمع کل تخفیفات سفارش")
    public String DiscountAmount;
    @PrintData(name = Keys.ORDER.ORDER_ADD_AMOUNT , description = "جمع کل اضافات سفارش")
    public String AddAmount;
    @PrintData(name = Keys.ORDER.ORDER_COMMENT, description = "توضیحات")
    public String Comment;
    @PrintData(name = Keys.ORDER.ORDER_LOCAL_PAPER_NO , description = "شماره ئیگیری سفارش")
    public String LocalPaperNo;
    @PrintData(name = Keys.ORDER.ORDER_DATE , description = "تاریخ سفارش")
    public String OrderDate;
    @PrintData(name = Keys.ORDER.ORDER_ITEMS, description = "سطرهای سفارش")
    public ListBinding Lines = new ListBinding();
    @PrintData(name = Keys.ORDER.ORDER_TOTAL_QTY , description = "تعداد کل سفارش")
    public String OrderTotalQty;

    public OrderBinding() {
        super("اطلاعات سفارش مشتری");
    }
}
