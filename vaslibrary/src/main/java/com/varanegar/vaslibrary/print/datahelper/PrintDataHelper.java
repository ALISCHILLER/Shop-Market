package com.varanegar.vaslibrary.print.datahelper;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.StringRes;
import android.util.Base64;

import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.java.util.Currency;
import com.varanegar.printlib.layout.datamodel.LayoutDataMap;
import com.varanegar.printlib.layout.datamodel.ListBinding;
import com.varanegar.printlib.layout.datamodel.SingleBinding;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.CustomerCallOrderOrderViewManager;
import com.varanegar.vaslibrary.manager.CustomerCallReturnLinesViewManager;
import com.varanegar.vaslibrary.manager.customercallreturnview.CustomerCallReturnViewManager;
import com.varanegar.vaslibrary.manager.CustomerRemainPerLineManager;
import com.varanegar.vaslibrary.manager.OldInvoiceHeaderViewManager;
import com.varanegar.vaslibrary.manager.OrderAmount;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallOrderManager;
import com.varanegar.vaslibrary.manager.image.LogoManager;
import com.varanegar.vaslibrary.manager.paymentmanager.PaymentManager;
import com.varanegar.vaslibrary.manager.paymentmanager.paymenttypes.PaymentType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigMap;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderModel;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModel;
import com.varanegar.vaslibrary.model.customercallreturnlinesview.CustomerCallReturnLinesViewModel;
import com.varanegar.vaslibrary.model.customercallreturnview.CustomerCallReturnViewModel;
import com.varanegar.vaslibrary.model.customerremainperline.CustomerRemainPerLineModel;
import com.varanegar.vaslibrary.model.oldinvoiceheaderview.OldInvoiceHeaderViewModel;
import com.varanegar.vaslibrary.model.payment.PaymentModel;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.model.user.UserModel;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class PrintDataHelper {
    private final Context context;

    public PrintDataHelper(Context context) {
        this.context = context;
    }

    protected String getString(@StringRes int res) {
        if (context != null)
            return context.getString(res);
        else
            return "";
    }

    public PrintDataHelper addLogo() {
        Bitmap logoBitmap = new LogoManager(context).getLogoBitmap();
        if (logoBitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            logoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            String base64 = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
            LayoutDataMap.getInstance().put(Keys.LOGO_BITMAP, new SingleBinding(base64, getString(R.string.company_logo)));
        }
        return this;
    }

    public PrintDataHelper addTitle(OrderPrintType orderPrintType) {
        if (orderPrintType == OrderPrintType.Preview)
            LayoutDataMap.getInstance().put(Keys.INVOICE_TITLE, new SingleBinding(getString(R.string.pre_invoice), getString(R.string.invoice_title)));
        else if (orderPrintType == OrderPrintType.Invoice)
            LayoutDataMap.getInstance().put(Keys.INVOICE_TITLE, new SingleBinding(getString(R.string.sales_invoice), getString(R.string.invoice_title)));
        else
            LayoutDataMap.getInstance().put(Keys.INVOICE_TITLE, new SingleBinding(getString(R.string.invoice), getString(R.string.invoice_title)));
        return this;
    }

    public PrintDataHelper addCustomerInfo(UUID customerId) {
        CustomerModel customerModel = new CustomerManager(context).getItem(customerId);
        if (customerModel == null)
            return this;

        LayoutDataMap.getInstance().put(Keys.CUSTOMER_CODE, new SingleBinding(customerModel.CustomerCode == null ? "" : customerModel.CustomerCode, getString(R.string.customer_code)));
        LayoutDataMap.getInstance().put(Keys.CUSTOMER_NAME, new SingleBinding(customerModel.CustomerName == null ? "" : customerModel.CustomerName, getString(R.string.customer_name)));
        LayoutDataMap.getInstance().put(Keys.STORE_NAME, new SingleBinding(customerModel.StoreName == null ? "" : customerModel.StoreName, getString(R.string.store_name)));
        LayoutDataMap.getInstance().put(Keys.CUSTOMER_ADDRESS, new SingleBinding(customerModel.Address == null ? "" : customerModel.Address, getString(R.string.customer_address)));
        LayoutDataMap.getInstance().put(Keys.CUSTOMER_ECONOMIC_CODE, new SingleBinding(customerModel.EconomicCode == null ? "" : customerModel.EconomicCode, getString(R.string.customer_economic_code)));
        LayoutDataMap.getInstance().put(Keys.CUSTOMER_MOBILE, new SingleBinding(customerModel.Mobile == null ? "" : customerModel.Mobile, getString(R.string.customer_mobile)));
        LayoutDataMap.getInstance().put(Keys.CUSTOMER_PHONE, new SingleBinding(customerModel.Mobile == null ? "" : customerModel.Phone, getString(R.string.customer_tel)));
        LayoutDataMap.getInstance().put(Keys.CUSTOMER_REMAIN_ALL, new SingleBinding(VasHelperMethods.currencyToStringWithoutCommas(customerModel.RemainDebit), getString(R.string.customer_remain_debit)));

        CustomerRemainPerLineManager customerRemainPerLineManager = new CustomerRemainPerLineManager(context);
        CustomerRemainPerLineModel customerRemainPerLineModel = customerRemainPerLineManager.getCustomerRemainPerLine(customerId);
        if (customerRemainPerLineModel != null)
            LayoutDataMap.getInstance().put(Keys.CUSTOMER_REMAIN, new SingleBinding(VasHelperMethods.currencyToStringWithoutCommas(customerRemainPerLineModel.CustRemAmount), getString(R.string.customer_remain_credit)));
        else
            LayoutDataMap.getInstance().put(Keys.CUSTOMER_REMAIN, new SingleBinding(VasHelperMethods.currencyToStringWithoutCommas(customerModel.CustomerRemain), getString(R.string.customer_remain_credit)));
        return this;
    }

    public PrintDataHelper addOrdersInfo(UUID customerId) {
        List<CustomerCallOrderModel> customerCallOrderModels = new CustomerCallOrderManager(context).getCustomerCallOrders(customerId);
        CustomerCallOrderOrderViewManager customerCallOrderOrderViewManager = new CustomerCallOrderOrderViewManager(context);

        ListBinding ordersList = new ListBinding();
        for (CustomerCallOrderModel callOrder : customerCallOrderModels) {
            if (callOrder.UniqueId == null)
                return this;
            OrderAmount orderAmount = customerCallOrderOrderViewManager.calculateTotalAmount(callOrder.UniqueId);
            OrderBinding orderBinding = new OrderBinding();
            orderBinding.TotalAmount = VasHelperMethods.currencyToStringWithoutCommas(orderAmount.TotalAmount);
            orderBinding.NetAmount = VasHelperMethods.currencyToStringWithoutCommas(orderAmount.NetAmount);
            orderBinding.DiscountAmount = VasHelperMethods.currencyToStringWithoutCommas(orderAmount.DiscountAmount);
            orderBinding.AddAmount = VasHelperMethods.currencyToStringWithoutCommas(orderAmount.AddAmount);
            orderBinding.Comment = callOrder.Comment;
            orderBinding.LocalPaperNo = callOrder.LocalPaperNo;
            orderBinding.OrderDate = DateHelper.toString(callOrder.SaleDate, DateFormat.Date, VasHelperMethods.getSysConfigLocale(context));

            List<CustomerCallOrderOrderViewModel> lines = customerCallOrderOrderViewManager.getLines(callOrder.UniqueId, customerCallOrderOrderViewManager.getOrderBy());
            int row = 1;
            Currency orderNetAmount = Currency.ZERO;
            BigDecimal orderTotalQty = BigDecimal.ZERO;
            for (CustomerCallOrderOrderViewModel item : lines) {
                OrderLineBinding orderLineBinding = new OrderLineBinding();
                orderLineBinding.Row = String.valueOf(row++);
                orderLineBinding.ProductName = item.ProductName;
                orderLineBinding.ProductCode = item.ProductCode;
                orderLineBinding.UnitName = item.UnitName;
                orderLineBinding.TotalQty = VasHelperMethods.bigDecimalToString(item.TotalQty);
                if (item.TotalQty != null)
                    orderTotalQty = orderTotalQty.add(item.TotalQty);
                orderLineBinding.ConvertFactor = item.ConvertFactor;
                orderLineBinding.Qty = item.Qty;
                orderLineBinding.UnitPrice = VasHelperMethods.currencyToStringWithoutCommas(item.UnitPrice);
                orderLineBinding.TotalAmount = VasHelperMethods.currencyToStringWithoutCommas(item.RequestAmount);
                Currency discountAmount = Currency.ZERO;
                if (item.RequestDis1Amount == null)
                    item.RequestDis1Amount = Currency.ZERO;

                if (item.RequestDis2Amount == null)
                    item.RequestDis2Amount = Currency.ZERO;

                if (item.RequestDis3Amount == null)
                    item.RequestDis3Amount = Currency.ZERO;

                if (item.RequestOtherDiscountAmount == null)
                    item.RequestOtherDiscountAmount = Currency.ZERO;

                if (item.RequestOtherDiscountAmount == null)
                    item.RequestOtherDiscountAmount = Currency.ZERO;

                discountAmount = discountAmount.add(item.RequestDis1Amount);
                orderLineBinding.Dis1Amount = VasHelperMethods.currencyToStringWithoutCommas(item.RequestDis1Amount);

                discountAmount = discountAmount.add(item.RequestDis2Amount);
                orderLineBinding.Dis2Amount = VasHelperMethods.currencyToStringWithoutCommas(item.RequestDis2Amount);

                discountAmount = discountAmount.add(item.RequestDis3Amount);
                orderLineBinding.Dis3Amount = VasHelperMethods.currencyToStringWithoutCommas(item.RequestDis3Amount);

                discountAmount = discountAmount.add(item.RequestOtherDiscountAmount);
                orderLineBinding.OtherDiscountAmount = VasHelperMethods.currencyToStringWithoutCommas(item.RequestOtherDiscountAmount);

                orderLineBinding.DiscountAmount = VasHelperMethods.currencyToStringWithoutCommas(discountAmount);

                Currency addAmount = Currency.ZERO;
                if (item.RequestAdd1Amount == null)
                    item.RequestAdd1Amount = Currency.ZERO;

                if (item.RequestAdd2Amount == null)
                    item.RequestAdd2Amount = Currency.ZERO;

                if (item.RequestAddOtherAmount == null)
                    item.RequestAddOtherAmount = Currency.ZERO;

                if (item.RequestTaxAmount == null)
                    item.RequestTaxAmount = Currency.ZERO;

                if (item.RequestChargeAmount == null)
                    item.RequestChargeAmount = Currency.ZERO;

                if (item.RequestAmount == null)
                    item.RequestAmount = Currency.ZERO;

                addAmount = addAmount.add(item.RequestAdd1Amount);
                orderLineBinding.Add1Amount = VasHelperMethods.currencyToStringWithoutCommas(item.RequestAdd1Amount);

                addAmount = addAmount.add(item.RequestAdd2Amount);
                orderLineBinding.Add2Amount = VasHelperMethods.currencyToStringWithoutCommas(item.RequestAdd2Amount);

                addAmount = addAmount.add(item.RequestAddOtherAmount);
                orderLineBinding.AddOtherAmount = VasHelperMethods.currencyToStringWithoutCommas(item.RequestAddOtherAmount);

                addAmount = addAmount.add(item.RequestTaxAmount);
                orderLineBinding.TaxAmount = VasHelperMethods.currencyToStringWithoutCommas(item.RequestTaxAmount);

                addAmount = addAmount.add(item.RequestChargeAmount);
                orderLineBinding.ChargeAmount = VasHelperMethods.currencyToStringWithoutCommas(item.RequestChargeAmount);

                orderLineBinding.AddAmount = VasHelperMethods.currencyToStringWithoutCommas(addAmount);
                orderLineBinding.NetAmount = VasHelperMethods.currencyToString(item.RequestAmount.add(addAmount).subtract(discountAmount));
                orderNetAmount = orderNetAmount.add(item.RequestAmount.add(addAmount).subtract(discountAmount));

                orderBinding.Lines.add(orderLineBinding);
            }

            orderBinding.NetAmount = VasHelperMethods.currencyToStringWithoutCommas(orderNetAmount);
            orderBinding.OrderTotalQty = VasHelperMethods.bigDecimalToString(orderTotalQty);

            ordersList.add(orderBinding);
            LayoutDataMap.getInstance().put(Keys.ORDER.LIST, ordersList);
        }

        return this;
    }

    public PrintDataHelper addDealerInfo() {
        SysConfigManager sysConfigManager = new SysConfigManager(context);
        ConfigMap configs = sysConfigManager.read(SysConfigManager.cloud);
        UserModel userModel = UserManager.readFromFile(context);
        if (userModel == null)
            return this;
        LayoutDataMap.getInstance().put(Keys.DEALER_NAME, new SingleBinding(userModel.UserName == null ? "" : userModel.UserName, getString(R.string.dealer_name)));
        LayoutDataMap.getInstance().put(Keys.DEALER_MOBILE, new SingleBinding(configs.getStringValue(ConfigKey.Mobile, ""), getString(R.string.dealer_mobile)));
        LayoutDataMap.getInstance().put(Keys.COMPANY_NAME, new SingleBinding(configs.getStringValue(ConfigKey.CompanyName, ""), getString(R.string.company_name)));
        LayoutDataMap.getInstance().put(Keys.COMPANY_ADDRESS, new SingleBinding(configs.getStringValue(ConfigKey.CompanyAddress, ""), getString(R.string.company_address)));
        LayoutDataMap.getInstance().put(Keys.COMPANY_EMAIL, new SingleBinding(configs.getStringValue(ConfigKey.CompanyEmail, ""), getString(R.string.company_email)));
        LayoutDataMap.getInstance().put(Keys.COMPANY_PHONE, new SingleBinding(configs.getStringValue(ConfigKey.CompanyPhone, ""), getString(R.string.company_tel)));
        LayoutDataMap.getInstance().put(Keys.CUSTOMER_MOBILE, new SingleBinding(configs.getStringValue(ConfigKey.CustomerMobile, ""), getString(R.string.customer_mobile)));
        LayoutDataMap.getInstance().put(Keys.DISTRIBUTION_CENTER_NAME, new SingleBinding(configs.getStringValue(ConfigKey.DistributionCenterName, ""), getString(R.string.distribution_center_name)));

        return this;
    }

    public PrintDataHelper addTourInfo() {
        TourManager tourManager = new TourManager(context);
        TourModel tourModel = tourManager.loadTour();
        if (tourModel == null)
            return this;
        LayoutDataMap.getInstance().put(Keys.TOUR_NO, new SingleBinding(String.valueOf(tourModel.TourNo), getString(R.string.tour_no)));
        return this;
    }

    public void addReturnsInfo(UUID customerId) {
        CustomerCallReturnViewModel customerCallReturnViewModel = new CustomerCallReturnViewManager(context).getItem(CustomerCallReturnViewManager.getLines(customerId, null, null));
        if (customerCallReturnViewModel != null) {
            BigDecimal orderTotalQty = BigDecimal.ZERO;
            Currency discountAmount = Currency.ZERO;
            Currency add1Amount = Currency.ZERO;
            Currency add2Amount = Currency.ZERO;

            LayoutDataMap.getInstance().put(Keys.RETURN.RETURN_TOTAL_AMOUNT, new SingleBinding(VasHelperMethods.currencyToString(customerCallReturnViewModel.TotalRequestAmount), "ارزش کل برگشتی"));
            LayoutDataMap.getInstance().put(Keys.RETURN.RETURN_NET_AMOUNT, new SingleBinding(VasHelperMethods.currencyToString(customerCallReturnViewModel.TotalRequestNetAmount), "مبلغ قابل پرداخت برگشتی"));

            CustomerCallReturnLinesViewManager customerCallReturnLinesViewManager = new CustomerCallReturnLinesViewManager(context);
            List<CustomerCallReturnLinesViewModel> lines = customerCallReturnLinesViewManager.getCustomerLines(customerId, null);
            int row = 1;

            ListBinding returnsList = new ListBinding();
            for (CustomerCallReturnLinesViewModel item : lines) {
                boolean hasQty = item.TotalReturnQty != null && item.TotalReturnQty.compareTo(BigDecimal.ZERO) > 0;
                if (hasQty) {
                    ReturnLineBinding returnLineBinding = new ReturnLineBinding();
                    returnLineBinding.Row = String.valueOf(row++);
                    returnLineBinding.ProductName = item.ProductName;
                    returnLineBinding.ProductCode = item.ProductCode;
                    returnLineBinding.UnitName = item.UnitName;
                    returnLineBinding.TotalQty = VasHelperMethods.bigDecimalToString(item.TotalReturnQty);
                    returnLineBinding.Qty = item.Qty;
                    returnLineBinding.ConvertFactor = item.ConvertFactor;
                    returnLineBinding.UnitPrice = VasHelperMethods.currencyToString(item.RequestUnitPrice);
                    returnLineBinding.TotalAmount = VasHelperMethods.currencyToString(item.TotalRequestAmount);

                    if (item.TotalReturnQty != null)
                        orderTotalQty = orderTotalQty.add(item.TotalReturnQty);

                    if (item.TotalRequestAdd1Amount == null)
                        item.TotalRequestAdd1Amount = Currency.ZERO;
                    if (item.TotalRequestAdd2Amount == null)
                        item.TotalRequestAdd2Amount = Currency.ZERO;
                    if (item.TotalRequestAddOtherAmount == null)
                        item.TotalRequestAddOtherAmount = Currency.ZERO;

                    add1Amount = add1Amount.add(item.TotalRequestAdd1Amount);
                    add2Amount = add2Amount.add(item.TotalRequestAdd2Amount);
                    discountAmount = discountAmount.add(item.TotalRequestAddOtherAmount);

                    Currency returnLineNetAmount = item.TotalRequestAmount.add(item.TotalRequestAdd1Amount).add(item.TotalRequestAdd2Amount).subtract(item.TotalRequestAddOtherAmount);
                    returnLineBinding.Add1Amount = VasHelperMethods.currencyToString(item.TotalRequestAdd1Amount);
                    returnLineBinding.Add2Amount = VasHelperMethods.currencyToString(item.TotalRequestAdd2Amount);
                    returnLineBinding.DiscountAmount = VasHelperMethods.currencyToString(item.TotalRequestAddOtherAmount);
                    returnLineBinding.AddAmount = VasHelperMethods.currencyToString(add1Amount.add(add2Amount));
                    returnLineBinding.NetAmount = VasHelperMethods.currencyToString(returnLineNetAmount);
                    returnLineBinding.ChargeAmount = VasHelperMethods.currencyToString(item.TotalRequestCharge);
                    returnLineBinding.TaxAmount = VasHelperMethods.currencyToString(item.TotalRequestTax);

                    returnsList.add(returnLineBinding);
                }
            }

            LayoutDataMap.getInstance().put(Keys.RETURN.RETURN_TOTAL_QTY, new SingleBinding( VasHelperMethods.bigDecimalToString(orderTotalQty),"تعداد کل برگشتی"));
            LayoutDataMap.getInstance().put(Keys.RETURN.RETURN_DISCOUNT_AMOUNT,  new SingleBinding(VasHelperMethods.currencyToString(discountAmount), "حمع کل تخفیفات برگشتی"));
            LayoutDataMap.getInstance().put(Keys.RETURN.RETURN_ADD_AMOUNT, new SingleBinding( VasHelperMethods.currencyToString(add1Amount.add(add2Amount)), "جمع کل اضافات برگشتی"));
            LayoutDataMap.getInstance().put(Keys.RETURN.RETURNS_LIST, returnsList);
        }
    }

    public void addPaymentInfo(UUID customerId) {
        Currency openInvoiceAmount = Currency.ZERO;
        List<OldInvoiceHeaderViewModel> oldInvoices = new OldInvoiceHeaderViewManager(context).getInvoicesByCustomerId(customerId);
        for (OldInvoiceHeaderViewModel oldInvoiceHeaderViewModel :
                oldInvoices) {
            if (oldInvoiceHeaderViewModel.HasPayment)
                openInvoiceAmount = openInvoiceAmount.add(oldInvoiceHeaderViewModel.RemAmount);
        }
        LayoutDataMap.getInstance().put(Keys.PAYMENT.OPEN_INVOICES_AMOUNT, new SingleBinding(VasHelperMethods.currencyToString(openInvoiceAmount), "ارزش کل فاکتور های باز "));

        PaymentManager paymentManager = new PaymentManager(context);
        Currency totalPaid = paymentManager.getTotalPaid(customerId);
        LayoutDataMap.getInstance().put(Keys.PAYMENT.TOTAL_PAYED_AMOUNT, new SingleBinding(VasHelperMethods.currencyToString(totalPaid), " جمع مبلغ پرداختی "));
        Currency cardPayment = Currency.ZERO;
        Currency checkPayment = Currency.ZERO;
        Currency creditPayment = Currency.ZERO;
        Currency cashPayment = Currency.ZERO;
        Currency discountPayment = Currency.ZERO;
        List<PaymentModel> payments = paymentManager.listPayments(customerId);
        if (payments.size() > 0) {
            for (PaymentModel paymentModel :
                    payments) {
                if (paymentModel.PaymentType.equals(PaymentType.Card))
                    cardPayment = cardPayment.add(paymentModel.Amount);
                else if (paymentModel.PaymentType.equals(PaymentType.Check))
                    checkPayment = checkPayment.add(paymentModel.Amount);
                else if (paymentModel.PaymentType.equals(PaymentType.Credit))
                    creditPayment = creditPayment.add(paymentModel.Amount);
                else if (paymentModel.PaymentType.equals(PaymentType.Cash))
                    cashPayment = cashPayment.add(paymentModel.Amount);
                else if (paymentModel.PaymentType.equals(PaymentType.Discount))
                    discountPayment = discountPayment.add(paymentModel.Amount);
            }
        }
        LayoutDataMap.getInstance().put(Keys.PAYMENT.CARD_PAYED_AMOUNT, new SingleBinding(VasHelperMethods.currencyToString(cardPayment), " جمع مبلغ پرداختی با کارتحوان"));
        LayoutDataMap.getInstance().put(Keys.PAYMENT.CHECK_PAYED_AMOUNT, new SingleBinding(VasHelperMethods.currencyToString(checkPayment), " جمع مبلغ پرداختی با چک"));
        LayoutDataMap.getInstance().put(Keys.PAYMENT.CREDIT_PAYED_AMOUNT, new SingleBinding(VasHelperMethods.currencyToString(creditPayment), " جمع مبلغ پرداختی از اعتبار"));
        LayoutDataMap.getInstance().put(Keys.PAYMENT.CASH_PAYED_AMOUNT, new SingleBinding(VasHelperMethods.currencyToString(cashPayment), " جمع مبلغ پرداختی نقدی"));
        LayoutDataMap.getInstance().put(Keys.PAYMENT.DISCOUNT_PAYMENT_AMOUNT, new SingleBinding(VasHelperMethods.currencyToString(discountPayment), " جمع مبلغ تخفیف تسویه"));

    }
}
