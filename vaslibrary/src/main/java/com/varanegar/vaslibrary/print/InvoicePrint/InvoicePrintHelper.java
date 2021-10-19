package com.varanegar.vaslibrary.print.InvoicePrint;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.java.util.Currency;
import com.varanegar.printlib.PrintHelper;
import com.varanegar.printlib.PrintSize;
import com.varanegar.printlib.box.Box;
import com.varanegar.printlib.box.BoxColumn;
import com.varanegar.printlib.box.BoxRow;
import com.varanegar.printlib.driver.PrintCallback;
import com.varanegar.printlib.layout.RootLayout;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.CustomerCallOrderOrderViewManager;
import com.varanegar.vaslibrary.manager.CustomerCallReturnLinesViewManager;
import com.varanegar.vaslibrary.manager.CustomerRemainPerLineManager;
import com.varanegar.vaslibrary.manager.OldInvoiceHeaderViewManager;
import com.varanegar.vaslibrary.manager.OrderAmount;
import com.varanegar.vaslibrary.manager.ProductManager;
import com.varanegar.vaslibrary.manager.ProductUnitViewManager;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercall.CallOrderLineManager;
import com.varanegar.vaslibrary.manager.customercall.CallOrderLinesQtyTempManager;
import com.varanegar.vaslibrary.manager.customercall.CallOrderLinesTempManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallOrderManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerPrintCountManager;
import com.varanegar.vaslibrary.manager.customercallreturnview.CustomerCallReturnViewManager;
import com.varanegar.vaslibrary.manager.image.LogoManager;
import com.varanegar.vaslibrary.manager.paymentmanager.PaymentManager;
import com.varanegar.vaslibrary.manager.paymentmanager.paymenttypes.PaymentType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigMap;
import com.varanegar.vaslibrary.manager.sysconfigmanager.OwnerKeysWrapper;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.call.CallOrderLineModel;
import com.varanegar.vaslibrary.model.call.CustomerCallOrder;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderModel;
import com.varanegar.vaslibrary.model.call.temporder.CallOrderLinesQtyTempModel;
import com.varanegar.vaslibrary.model.call.temporder.CallOrderLinesTempModel;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModel;
import com.varanegar.vaslibrary.model.customercallreturnlinesview.CustomerCallReturnLinesViewModel;
import com.varanegar.vaslibrary.model.customercallreturnview.CustomerCallReturnViewModel;
import com.varanegar.vaslibrary.model.customerremainperline.CustomerRemainPerLineModel;
import com.varanegar.vaslibrary.model.oldinvoiceheaderview.OldInvoiceHeaderViewModel;
import com.varanegar.vaslibrary.model.payment.PaymentModel;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.productUnitView.ProductUnitViewModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.print.BasePrintHelper;
import com.varanegar.vaslibrary.print.datahelper.OrderPrintType;
import com.varanegar.vaslibrary.print.datahelper.PrintDataHelper;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by g.aliakbar on 04/04/2018.
 */

public class InvoicePrintHelper extends BasePrintHelper {


    private UUID customerId;
    private OrderPrintType type;
    private ConfigMap configs;

    public InvoicePrintHelper(@NonNull AppCompatActivity activity, UUID customerId, OrderPrintType type) {
        super(activity);
        this.customerId = customerId;
        this.type = type;
    }

    @Override
    public void print(@NonNull final PrintCallback callback) {
        SysConfigManager sysConfigManager = new SysConfigManager(getActivity());
        configs = sysConfigManager.read(SysConfigManager.cloud);

        try {
            SysConfigModel printFormat = configs.get(ConfigKey.CustomizePrint);
            if (printFormat != null && printFormat.Value != null && !printFormat.Value.isEmpty())
                runCustomizedPrint(printFormat.Value);  // new print -> customized print
            else
                runOldPrint();     // old print -> fixed print

            // send to printer
            PrintHelper.getInstance().print(new PrintCallback() {
                @Override
                public void done() {
                    Timber.d("Print finished");
                    PrintHelper.dispose();
                    if (type == OrderPrintType.Invoice) {
                        try {
                            new CustomerPrintCountManager(getActivity()).addCount(customerId);
                        } catch (Exception e) {
                            Timber.e(e);
                        }
                    }
                    callback.done();
                }

                @Override
                public void failed() {
                    Timber.d("Print failed");
                    PrintHelper.dispose();
                    callback.done();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.failed();
        }


    }

    private void runOldPrint() {
        PrintData printData = fillPrintData();

        SysConfigManager sysConfigManager = new SysConfigManager(getActivity());
        OwnerKeysWrapper ownerKeys = sysConfigManager.readOwnerKeys();

        // print logo
        Bitmap logoBitmap = new LogoManager(getActivity()).getLogoBitmap();
        if (logoBitmap != null)
            PrintHelper.getInstance().addBitmap(logoBitmap, Paint.Align.CENTER);

        // region print title
        PrintHelper.getInstance().feedLine(PrintSize.small);
        String title;
        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
            title = getActivity().getString(R.string.pre_invoice);
        } else {
            if (type == OrderPrintType.Invoice || type == OrderPrintType.contractor) {
                if (ownerKeys.DataOwnerKey.equalsIgnoreCase("45565f9e-48eb-408a-bd33-dcb10008c1db") || ownerKeys.DataOwnerKey.equalsIgnoreCase("1B6D0016-6E41-491D-8639-DCE21450EFA6"))
                    title = getActivity().getString(R.string.product_delivery_paper);
                else
                    title = getActivity().getString(R.string.sales_invoice);

            } else {
                if (ownerKeys.DataOwnerKey.equalsIgnoreCase("45565f9e-48eb-408a-bd33-dcb10008c1db") || ownerKeys.DataOwnerKey.equalsIgnoreCase("1B6D0016-6E41-491D-8639-DCE21450EFA6"))
                    title = getActivity().getString(R.string.product_delivery_preview);
                else
                    title = getActivity().getString(R.string.sales_invoice_preview);
            }
        }
        PrintHelper.getInstance().addParagraph(PrintSize.larger, title, Paint.Align.CENTER);
        PrintHelper.getInstance().feedLine(PrintSize.small);
        // endregion

        // region print header

        BoxColumn headerColumn = new BoxColumn();
        if (ownerKeys.DataOwnerKey.equalsIgnoreCase("8638C58A-F145-467D-960E-817EA44EBF45")) {//mihan kish
            addRowItem(headerColumn, R.string.date, DateHelper.toString(new Date(), DateFormat.Date, VasHelperMethods.getSysConfigLocale(getActivity())), true, PrintSize.medium);
        }
        if (configs.compare(ConfigKey.PrintCustomerName, true)) {
            addRowItem(headerColumn, R.string.print_customername, printData.CustomerName, true, PrintSize.medium);
            addRowItem(headerColumn, R.string.print_customercode, printData.CustomerCode, true, PrintSize.medium);
        }
//        if (ownerKeys.DataOwnerKey.equalsIgnoreCase("CAF5A390-CBE1-435B-BDDE-1F682E004693")) {
//            addRowItem(headerColumn, R.string.print_customer_mobile, printData.CustomerMobile, true, PrintSize.medium);
//        }
        if (configs.compare(ConfigKey.PrintCustomerMobile, true)) {
            addRowItem(headerColumn, R.string.print_customer_mobile, printData.CustomerMobile, true, PrintSize.medium);
        }
        if (configs.compare(ConfigKey.PrintStoreName, true)) {
            addRowItem(headerColumn, R.string.print_storename, printData.StoreName, true, PrintSize.medium);
        }
        if (configs.compare(ConfigKey.PrintCustomerRemain, true)) {
            addRowItem(headerColumn, R.string.print_customer_remain, HelperMethods.currencyToString(printData.CustomerRemain), true, PrintSize.medium);
        }
        if (configs.compare(ConfigKey.PrintFinalCustomerRemain, true)) {
            addRowItem(headerColumn, R.string.print_finalcustomerremain, HelperMethods.currencyToString(printData.CustomerRemainAll), true, PrintSize.medium);
        }
        addRowItem(headerColumn, R.string.print_customeraddress, printData.CustomerAddress);


        PrintHelper.getInstance().addBox(new Box().addColumn(headerColumn));
        PrintHelper.getInstance().feedLine(PrintSize.small);

        // endregion
        ArrayList<BigDecimal> smallUnitsQtyForPrint = new ArrayList<>();
        ArrayList<BigDecimal> middleUnitsQtyForPrint = new ArrayList<>();
        ArrayList<BigDecimal> largeUnitsQtyForPrint = new ArrayList<>();
        for (OrderPrintData orderPrintData : printData.orderPrintData) {

            // region invoice header
            PrintHelper.getInstance().feedLine(PrintSize.small);
            PrintHelper.getInstance().addParagraph(PrintSize.medium, getActivity().getString(R.string.order), Paint.Align.CENTER);
            PrintHelper.getInstance().feedLine(PrintSize.xSmall);
            if (configs.compare(ConfigKey.PrintFollowUpNo, true)) {
                BoxColumn invoiceHeaderColumn = new BoxColumn();
                addRowItem(invoiceHeaderColumn, R.string.print_followupno, orderPrintData.LocalPaperNo, true);
                PrintHelper.getInstance().addBox(new Box().addColumn(invoiceHeaderColumn));
            }
            if (VaranegarApplication.is(VaranegarApplication.AppId.Dist) && configs.compare(ConfigKey.PrintDealerName, true)) {
                BoxColumn invoiceHeaderColumn = new BoxColumn();
                addRowItem(invoiceHeaderColumn, R.string.print_dealername, orderPrintData.DealerName, true);
                PrintHelper.getInstance().addBox(new Box().addColumn(invoiceHeaderColumn));
            }
            if (VaranegarApplication.is(VaranegarApplication.AppId.Dist) && ownerKeys.DataOwnerKey.equalsIgnoreCase("CAF5A390-CBE1-435B-BDDE-1F682E004693")) {
                BoxColumn invoiceHeaderColumn = new BoxColumn();
                addRowItem(invoiceHeaderColumn, R.string.print_dealer_mobile, orderPrintData.DealerMobile, true);
                PrintHelper.getInstance().addBox(new Box().addColumn(invoiceHeaderColumn));
            }
            String rowName;
            if (configs.compare(ConfigKey.PrintProductRow, true)) {
                rowName = getActivity().getString(R.string.row);
            } else {
                rowName = getActivity().getString(R.string.code);
            }
            BoxRow headerProductRow = new BoxRow();
            if (ownerKeys.DataOwnerKey.equalsIgnoreCase("146A800D-EA09-4454-B943-9BAF33DBBB16")) {
                headerProductRow.addColumn(new BoxColumn(1).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.discount_percent)).setTextSize(PrintSize.xSmall).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE));
            }
            headerProductRow.addColumn(new BoxColumn(2).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.price)).setTextSize(PrintSize.smaller).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE));
            headerProductRow.addColumn(new BoxColumn(configs.compare(ConfigKey.DisplayunitbyBasedOn, UUID.fromString("FBC0CA9F-2739-452A-A09D-5626F17DF26C")) ? 2 : 1).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.count)).setTextSize(PrintSize.smaller).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE));
            if (ownerKeys.DataOwnerKey.equalsIgnoreCase("e00f3940-e47a-4c49-83a4-352c4a260aed")) {// ofogh roudehen
                headerProductRow.addColumn(new BoxColumn(5).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.product_name)).setTextSize(PrintSize.smaller).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE));
            } else {
                headerProductRow.addColumn(new BoxColumn(4).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.product_name)).setTextSize(PrintSize.smaller).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE));
            }
            headerProductRow.addColumn(new BoxColumn(1).setBackgroundColor(Color.BLACK).setText(rowName).setTextSize(PrintSize.smaller).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE));
            PrintHelper.getInstance().addBox(new Box().addColumn(new BoxColumn().addRows(headerProductRow)));
            // endregion

            // region invoice
            List<BoxRow> orderLineRows = new ArrayList<>();
            if (ownerKeys.DataOwnerKey.equalsIgnoreCase("687109bf-e8b7-4749-929e-d1b03bdaa7d6") || ownerKeys.DataOwnerKey.equalsIgnoreCase("146a800d-ea09-4454-b943-9baf33dbbb16") || ownerKeys.DataOwnerKey.equalsIgnoreCase("cd615e29-f9e9-4ce5-8708-a3e5ce67c901") || ownerKeys.DataOwnerKey.equalsIgnoreCase("62f26a2c-8c74-4dda-ac78-c2f1f5961411")) { //Bastani Dusti & Ruzane & NemayandeBastaniMihan & LabanDash
                for (OrderLinePrintData line : orderPrintData.Lines) {
                    BoxRow newProductRow = new BoxRow();
                    newProductRow.addColumn(new BoxColumn(8).setBorderWidth(0).addRows(
                            new BoxRow().setBorderWidth(0).addColumn(
                                    new BoxColumn().setText(line.ProductName).setTextAlign(Paint.Align.RIGHT).setTextSize(PrintSize.small)
                            ))
                    );
                    String rowValue = configs.compare(ConfigKey.PrintProductRow, true) ? String.valueOf(line.Row) : line.ProductCode;
                    newProductRow.addColumn(new BoxColumn(1).setText(rowValue).setTextSize(PrintSize.smaller).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER));
                    orderLineRows.add(newProductRow);

                    BoxRow row = new BoxRow();
                    if (ownerKeys.DataOwnerKey.equalsIgnoreCase("146A800D-EA09-4454-B943-9BAF33DBBB16")) {
                        double disDouble = ((HelperMethods.currencyToDouble(line.DiscountAmount)) / (HelperMethods.currencyToDouble(line.RequestAmount))) * (100d);
                        int disInt = (int) Math.ceil(disDouble);
                        row.addColumn(new BoxColumn(1).setText(String.valueOf(disInt)).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.smaller).setMargin(PrintSize.xxxSmall));
                    }
                    if (configs.compare(ConfigKey.PrintaddtaxforeEachinvoiceitem, true)) {
                        row.addColumn(new BoxColumn(2).setBorderWidth(0).addRows(
                                new BoxRow().setBorderWidth(0).addColumn(
                                        new BoxColumn().setText(HelperMethods.currencyToString(line.NetAmount)).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.small).setMargin(PrintSize.xxxSmall).setBorderWidth(0))
                                , new BoxRow().setBorderWidth(0).addColumn(
                                        new BoxColumn().setText(HelperMethods.currencyToString(line.AddAmount) + " + ").setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.small).setMargin(PrintSize.xxxSmall).setBorderWidth(0))
                                )
                        );
                    } else
                        row.addColumn(new BoxColumn(2).setText(HelperMethods.currencyToString(line.NetAmount)).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.small).setMargin(PrintSize.xxxSmall));

                    if (configs.compare(ConfigKey.DisplayunitbyBasedOn, UUID.fromString("FBC0CA9F-2739-452A-A09D-5626F17DF26C"))) {
                        String qty = VasHelperMethods.chopTotalQtyToString(line.TotalQty, line.UnitName, line.ConvertFactor, null, null);
                        row.addColumn(new BoxColumn(2).setText(qty).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.smaller).setMargin(PrintSize.xxxSmall));
                        String[] unitValues = qty.split(":");
                        if (unitValues.length == 1) {
                            smallUnitsQtyForPrint.add(new BigDecimal(unitValues[0]));
                        } else if (unitValues.length == 2) {
                            largeUnitsQtyForPrint.add(new BigDecimal(unitValues[0]));
                            smallUnitsQtyForPrint.add(new BigDecimal(unitValues[1]));
                        } else if (unitValues.length == 3) {
                            largeUnitsQtyForPrint.add(new BigDecimal(unitValues[0]));
                            middleUnitsQtyForPrint.add(new BigDecimal(unitValues[1]));
                            smallUnitsQtyForPrint.add(new BigDecimal(unitValues[2]));
                        }
                    } else if (configs.compare(ConfigKey.DisplayunitbyBasedOn, UUID.fromString("92605985-A7BC-4D4A-86AC-9F27734DEE9D"))) {
                        List<BaseUnit> units = VasHelperMethods.chopTotalQty(line.TotalQty, line.UnitName, line.ConvertFactor, null, null);
                        if (units.size() > 0) {
                            BaseUnit u = units.get(0);
                            DiscreteUnit largeUnit = null;
                            if (u instanceof DiscreteUnit)
                                largeUnit = (DiscreteUnit) u;
                            for (BaseUnit unit :
                                    units) {
                                if (unit instanceof DiscreteUnit && ((DiscreteUnit) unit).ConvertFactor > largeUnit.ConvertFactor)
                                    largeUnit = (DiscreteUnit) unit;
                            }
                            row.addColumn(new BoxColumn(1).setText(HelperMethods.doubleToString(largeUnit.value)).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.smaller).setMargin(PrintSize.xxxSmall));
                            largeUnitsQtyForPrint.add(new BigDecimal(largeUnit.value));
                        }
                    } else
                        row.addColumn(new BoxColumn(1).setText(HelperMethods.bigDecimalToString(line.TotalQty)).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.smaller).setMargin(PrintSize.xxxSmall));

                    if (configs.compare(ConfigKey.PrintProductPrice, true)) {
                        row.addColumn(new BoxColumn(4).setBorderWidth(0).addRows(
                                new BoxRow().setBorderWidth(0).addColumn(
                                        new BoxColumn().setText(getActivity().getString(R.string.unit_price) + ": " + HelperMethods.currencyToString(line.UnitPrice)).setTextAlign(Paint.Align.RIGHT).setTextSize(PrintSize.small)
                                ))
                        );
                    } else {
                        row.addColumn(new BoxColumn(4).setBorderWidth(0).addRows(
                                new BoxRow().setBorderWidth(0).addColumn(
                                        new BoxColumn().setText("").setTextAlign(Paint.Align.RIGHT).setTextSize(PrintSize.small)
                                ))
                        );
                    }
                    row.addColumn(new BoxColumn(1).setText("").setTextSize(PrintSize.xSmall).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER));
                    orderLineRows.add(row);
                }
            } else {
                for (OrderLinePrintData line : orderPrintData.Lines) {
                    BoxRow row = new BoxRow();
                    if (ownerKeys.DataOwnerKey.equalsIgnoreCase("21818fe1-1e63-4d64-89c9-989bc9ee3e58")) {
                        row.addColumn(new BoxColumn(3).setBorderWidth(0).addRows(
                                new BoxRow().setBorderWidth(0).addColumn(
                                        new BoxColumn().setText(getActivity().getString(R.string.gross) + HelperMethods.currencyToString(line.RequestAmount)).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.smaller).setMargin(PrintSize.xxxSmall).setBorderWidth(0))
                                , new BoxRow().setBorderWidth(0).addColumn(
                                        new BoxColumn().setText(HelperMethods.currencyToString(line.NetAmount)).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.small).setMargin(PrintSize.xxxSmall).setBorderWidth(0))
                                )
                        );
                    } else {
                        if (configs.compare(ConfigKey.PrintaddtaxforeEachinvoiceitem, true)) {
                            row.addColumn(new BoxColumn(2).setBorderWidth(0).addRows(
                                    new BoxRow().setBorderWidth(0).addColumn(
                                            new BoxColumn().setText(HelperMethods.currencyToString(line.NetAmount)).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.small).setMargin(PrintSize.xxxSmall).setBorderWidth(0))
                                    , new BoxRow().setBorderWidth(0).addColumn(
                                            new BoxColumn().setText(HelperMethods.currencyToString(line.AddAmount) + " + ").setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.small).setMargin(PrintSize.xxxSmall).setBorderWidth(0))
                                    )
                            );
                        } else
                            row.addColumn(new BoxColumn(2).setText(HelperMethods.currencyToString(line.NetAmount)).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.small).setMargin(PrintSize.xxxSmall));
                    }

                    if (configs.compare(ConfigKey.DisplayunitbyBasedOn, UUID.fromString("FBC0CA9F-2739-452A-A09D-5626F17DF26C"))) {
                        String qty = VasHelperMethods.chopTotalQtyToString(line.TotalQty, line.UnitName, line.ConvertFactor, null, null);
                        row.addColumn(new BoxColumn(2).setText(qty).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.smaller).setMargin(PrintSize.xxxSmall));
                        String[] unitValues = qty.split(":");
                        if (unitValues.length == 1) {
                            smallUnitsQtyForPrint.add(new BigDecimal(unitValues[0]));
                        } else if (unitValues.length == 2) {
                            largeUnitsQtyForPrint.add(new BigDecimal(unitValues[0]));
                            smallUnitsQtyForPrint.add(new BigDecimal(unitValues[1]));
                        } else if (unitValues.length == 3) {
                            largeUnitsQtyForPrint.add(new BigDecimal(unitValues[0]));
                            middleUnitsQtyForPrint.add(new BigDecimal(unitValues[1]));
                            smallUnitsQtyForPrint.add(new BigDecimal(unitValues[2]));
                        }
                    } else if (configs.compare(ConfigKey.DisplayunitbyBasedOn, UUID.fromString("92605985-A7BC-4D4A-86AC-9F27734DEE9D"))) {
                        List<BaseUnit> units = VasHelperMethods.chopTotalQty(line.TotalQty, line.UnitName, line.ConvertFactor, null, null);
                        if (units.size() > 0) {
                            BaseUnit u = units.get(0);
                            DiscreteUnit largeUnit = null;
                            if (u instanceof DiscreteUnit)
                                largeUnit = (DiscreteUnit) u;
                            for (BaseUnit unit :
                                    units) {
                                if (unit instanceof DiscreteUnit && ((DiscreteUnit) unit).ConvertFactor > largeUnit.ConvertFactor)
                                    largeUnit = (DiscreteUnit) unit;
                            }
                            row.addColumn(new BoxColumn(1).setText(HelperMethods.doubleToString(largeUnit.value)).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.smaller).setMargin(PrintSize.xxxSmall));
                            largeUnitsQtyForPrint.add(new BigDecimal(largeUnit.value));
                        }
                    } else
                        row.addColumn(new BoxColumn(1).setText(HelperMethods.bigDecimalToString(line.TotalQty)).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.smaller).setMargin(PrintSize.xxxSmall));
                    if (configs.compare(ConfigKey.PrintProductPrice, true)) {
                        if (ownerKeys.DataOwnerKey.equalsIgnoreCase("e00f3940-e47a-4c49-83a4-352c4a260aed")) {// ofogh roudehen
                            row.addColumn(new BoxColumn(6).setBorderWidth(0).addRows(
                                    new BoxRow().setBorderWidth(0).addColumn(
                                            new BoxColumn().setText(line.ProductName).setTextAlign(Paint.Align.RIGHT).setTextSize(PrintSize.smaller)
                                    ),
                                    new BoxRow().setBorderWidth(0).addColumns(
                                            new BoxColumn().setBorderWidth(0).addRow(
                                                    new BoxRow().setBorderWidth(0).addColumns(
                                                            new BoxColumn(2).setText(HelperMethods.currencyToString(line.UnitPrice)).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.small).setBorderWidth(0),
                                                            new BoxColumn(1).setText(getActivity().getString(R.string.unit_price)).setTextAlign(Paint.Align.RIGHT).setTextSize(PrintSize.smaller).setBorderWidth(0)
                                                    ))
                                    ))
                            );
                        } else {
                            row.addColumn(new BoxColumn(4).setBorderWidth(0).addRows(
                                    new BoxRow().setBorderWidth(0).addColumn(
                                            new BoxColumn().setText(line.ProductName).setTextAlign(Paint.Align.RIGHT).setTextSize(line.ProductName.length() > 20 ? PrintSize.xSmall : PrintSize.smaller)
                                    ),
                                    new BoxRow().setBorderWidth(0).addColumns(
                                            new BoxColumn().setBorderWidth(0).addRow(
                                                    new BoxRow().setBorderWidth(0).addColumns(
                                                            new BoxColumn(2).setText(HelperMethods.currencyToString(line.UnitPrice)).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.small).setBorderWidth(0),
                                                            new BoxColumn(1).setText(getActivity().getString(R.string.unit_price)).setTextAlign(Paint.Align.RIGHT).setTextSize(PrintSize.smaller).setBorderWidth(0)
                                                    ))
                                    ))
                            );
                        }
                    } else {
                        if (ownerKeys.DataOwnerKey.equalsIgnoreCase("e00f3940-e47a-4c49-83a4-352c4a260aed")) {// ofogh roudehen
                            row.addColumn(new BoxColumn(6).setBorderWidth(0).addRows(
                                    new BoxRow().setBorderWidth(0).addColumn(
                                            new BoxColumn().setText(line.ProductName).setTextAlign(Paint.Align.RIGHT).setTextSize(PrintSize.smaller)
                                    ))
                            );
                        } else {
                            row.addColumn(new BoxColumn(4).setBorderWidth(0).addRows(
                                    new BoxRow().setBorderWidth(0).addColumn(
                                            new BoxColumn().setText(line.ProductName).setTextAlign(Paint.Align.RIGHT).setTextSize(line.ProductName.length() > 20 ? PrintSize.xSmall : PrintSize.smaller)
                                    ))
                            );
                        }

                    }
                    String rowValue = configs.compare(ConfigKey.PrintProductRow, true) ? String.valueOf(line.Row) : line.ProductCode;
                    row.addColumn(new BoxColumn(1).setText(rowValue).setTextSize(PrintSize.xSmall).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER));
                    orderLineRows.add(row);
                }
            }
            PrintHelper.getInstance().addBox(new Box().addColumn(new BoxColumn().addRows(orderLineRows)));

            // endregion

            // region invoice footer
            boolean hasInvoiceFooterColumn = false;
            BoxColumn invoiceFooterColumn = new BoxColumn();
            if (configs.compare(ConfigKey.PrintItemsCount, true)) {
                if (configs.compare(ConfigKey.DisplayunitbyBasedOn, UUID.fromString("FBC0CA9F-2739-452A-A09D-5626F17DF26C"))) {
                    String totalQtyForPrint = "";
                    BigDecimal sumLargeValues = BigDecimal.ZERO;
                    if (largeUnitsQtyForPrint.size() > 0) {
                        for (BigDecimal value :
                                largeUnitsQtyForPrint) {
                            sumLargeValues = sumLargeValues.add(value);
                        }
                        if (totalQtyForPrint.isEmpty())
                            totalQtyForPrint += HelperMethods.bigDecimalToString(sumLargeValues);
                        else
                            totalQtyForPrint += ":" + HelperMethods.bigDecimalToString(sumLargeValues);
                    }
                    BigDecimal sumMiddleValues = BigDecimal.ZERO;
                    if (middleUnitsQtyForPrint.size() > 0) {
                        for (BigDecimal value :
                                middleUnitsQtyForPrint) {
                            sumMiddleValues = sumMiddleValues.add(value);
                        }
                        if (totalQtyForPrint.isEmpty())
                            totalQtyForPrint += HelperMethods.bigDecimalToString(sumMiddleValues);
                        else
                            totalQtyForPrint += ":" + HelperMethods.bigDecimalToString(sumMiddleValues);
                    }
                    BigDecimal sumSmallValues = BigDecimal.ZERO;
                    if (smallUnitsQtyForPrint.size() > 0) {
                        for (BigDecimal value :
                                smallUnitsQtyForPrint) {
                            sumSmallValues = sumSmallValues.add(value);
                        }
                        if (totalQtyForPrint.isEmpty())
                            totalQtyForPrint += HelperMethods.bigDecimalToString(sumSmallValues);
                        else
                            totalQtyForPrint += ":" + HelperMethods.bigDecimalToString(sumSmallValues);
                    }

                    addRowItem(invoiceFooterColumn, R.string.print_itemscount, totalQtyForPrint, true, PrintSize.medium);
                } else if (configs.compare(ConfigKey.DisplayunitbyBasedOn, UUID.fromString("92605985-A7BC-4D4A-86AC-9F27734DEE9D"))) {
                    BigDecimal totalQtyForPrint = BigDecimal.ZERO;
                    if (largeUnitsQtyForPrint.size() > 0)
                        for (BigDecimal value :
                                largeUnitsQtyForPrint) {
                            totalQtyForPrint = totalQtyForPrint.add(value);
                        }
                    addRowItem(invoiceFooterColumn, R.string.print_itemscount, HelperMethods.bigDecimalToString(totalQtyForPrint), true, PrintSize.medium);
                } else {
                    addRowItem(invoiceFooterColumn, R.string.print_itemscount, HelperMethods.bigDecimalToString(orderPrintData.TotalQty), true, PrintSize.medium);
                }
                hasInvoiceFooterColumn = true;
            }
            if (configs.compare(ConfigKey.PrintTotalPurchaseAmount, true)) {
                addRowItem(invoiceFooterColumn, R.string.gross_amount, HelperMethods.currencyToString(orderPrintData.TotalAmount), true, PrintSize.medium);
                hasInvoiceFooterColumn = true;
            }
            if (/*!VaranegarApplication.is(VaranegarApplication.AppId.PreSales) && */configs.compare(ConfigKey.PrintTotalDiscounts, true) && orderPrintData.DiscountAmount.compareTo(Currency.ZERO) == 1) {
                if (ownerKeys.DataOwnerKey.equalsIgnoreCase("CAF5A390-CBE1-435B-BDDE-1F682E004693") || ownerKeys.DataOwnerKey.equalsIgnoreCase("146A800D-EA09-4454-B943-9BAF33DBBB16")) {
                    addRowItem(invoiceFooterColumn, R.string.print_totalpromotion, HelperMethods.currencyToString(orderPrintData.Dis2), true, PrintSize.medium);
                    addRowItem(invoiceFooterColumn, R.string.print_discount, HelperMethods.currencyToString(orderPrintData.Dis1), true, PrintSize.medium);
                }
                if (ownerKeys.DataOwnerKey.equalsIgnoreCase("146A800D-EA09-4454-B943-9BAF33DBBB16"))
                    addRowItem(invoiceFooterColumn, R.string.branding_discount, HelperMethods.currencyToString(orderPrintData.Dis3), true, PrintSize.medium);

                addRowItem(invoiceFooterColumn, R.string.print_totaldiscounts, HelperMethods.currencyToString(orderPrintData.DiscountAmount), true, PrintSize.medium);
                hasInvoiceFooterColumn = true;
            }
            if (/*!VaranegarApplication.is(VaranegarApplication.AppId.PreSales) && */configs.compare(ConfigKey.PrintSumOfComplications, true) && orderPrintData.AddAmount.compareTo(Currency.ZERO) == 1) {
                addRowItem(invoiceFooterColumn, R.string.print_sumofcomplications, HelperMethods.currencyToString(orderPrintData.AddAmount), true, PrintSize.medium);
                hasInvoiceFooterColumn = true;
            }
            if ((!VaranegarApplication.is(VaranegarApplication.AppId.PreSales) && configs.compare(ConfigKey.PrintGrossPurchases, true)) || (!VaranegarApplication.is(VaranegarApplication.AppId.PreSales) && configs.compare(ConfigKey.PrintGrossPurchases, true) && orderPrintData.NetAmount.compareTo(Currency.ZERO) == 1)) {
                addRowItem(invoiceFooterColumn, R.string.print_net_amount, HelperMethods.currencyToString(orderPrintData.NetAmount), true, PrintSize.medium);
                hasInvoiceFooterColumn = true;
            }

            if (hasInvoiceFooterColumn)
                PrintHelper.getInstance().addBox(new Box().addColumn(invoiceFooterColumn));
            // endregion
        }

        if (printData.returnPrintData != null) {
            // region return header

            PrintHelper.getInstance().feedLine(PrintSize.small);
            PrintHelper.getInstance().addParagraph(PrintSize.medium, getActivity().getString(R.string.returns), Paint.Align.CENTER);
            PrintHelper.getInstance().feedLine(PrintSize.xSmall);

            String rowName;
            if (configs.compare(ConfigKey.PrintProductRow, true)) {
                rowName = getActivity().getString(R.string.row);
            } else {
                rowName = getActivity().getString(R.string.code);
            }

            if (ownerKeys.DataOwnerKey.equalsIgnoreCase("e00f3940-e47a-4c49-83a4-352c4a260aed")) {// ofogh roudehen
                PrintHelper.getInstance().addBox(new Box().addColumns(
                        new BoxColumn(2).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.price)).setTextSize(PrintSize.smaller).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE),
                        new BoxColumn(1).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.count)).setTextSize(PrintSize.smaller).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE),
                        new BoxColumn(5).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.product_name)).setTextSize(PrintSize.smaller).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE),
                        new BoxColumn(1).setBackgroundColor(Color.BLACK).setText(rowName).setTextSize(PrintSize.smaller).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE)
                ));
            } else {
                PrintHelper.getInstance().addBox(new Box().addColumns(
                        new BoxColumn(2).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.price)).setTextSize(PrintSize.smaller).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE),
                        new BoxColumn(1).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.count)).setTextSize(PrintSize.smaller).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE),
                        new BoxColumn(3).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.product_name)).setTextSize(PrintSize.smaller).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE),
                        new BoxColumn(1).setBackgroundColor(Color.BLACK).setText(rowName).setTextSize(PrintSize.smaller).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE)
                ));
            }
            // endregion

            // region return
            List<BoxRow> returnLineRows = new ArrayList<>();
            if (ownerKeys.DataOwnerKey.equalsIgnoreCase("687109bf-e8b7-4749-929e-d1b03bdaa7d6") || ownerKeys.DataOwnerKey.equalsIgnoreCase("146a800d-ea09-4454-b943-9baf33dbbb16") || ownerKeys.DataOwnerKey.equalsIgnoreCase("cd615e29-f9e9-4ce5-8708-a3e5ce67c901")) { //Bastani Dusti & Ruzane & NemayandeBastaniMihan
                for (OrderLinePrintData line : printData.returnPrintData.Lines) {
                    BoxRow newProductRow = new BoxRow();
                    newProductRow.addColumn(new BoxColumn(8).setBorderWidth(0).addRows(
                            new BoxRow().setBorderWidth(0).addColumn(
                                    new BoxColumn().setText(line.ProductName).setTextAlign(Paint.Align.RIGHT).setTextSize(PrintSize.small)
                            ))
                    );
                    String rowValue = configs.compare(ConfigKey.PrintProductRow, true) ? String.valueOf(line.Row) : line.ProductCode;
                    newProductRow.addColumn(new BoxColumn(1).setText(rowValue).setTextSize(PrintSize.smaller).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER));
                    returnLineRows.add(newProductRow);

                    BoxRow row = new BoxRow();
                    row.addColumns(
                            new BoxColumn(2).setText(HelperMethods.currencyToString(line.NetAmount)).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.small).setMargin(PrintSize.xxxSmall),
                            new BoxColumn(1).setText(HelperMethods.bigDecimalToString(line.TotalQty)).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.small).setMargin(PrintSize.xxxSmall)
                    );

                    if (configs.compare(ConfigKey.PrintProductPrice, true)) {
                        row.addColumn(new BoxColumn(4).setBorderWidth(0).addRows(
                                new BoxRow().setBorderWidth(0).addColumn(
                                        new BoxColumn().setText(getActivity().getString(R.string.unit_price) + ": " + HelperMethods.currencyToString(line.UnitPrice)).setTextAlign(Paint.Align.RIGHT).setTextSize(PrintSize.small)
                                ))
                        );
                    } else {
                        row.addColumn(new BoxColumn(4).setBorderWidth(0).addRows(
                                new BoxRow().setBorderWidth(0).addColumn(
                                        new BoxColumn().setText("").setTextAlign(Paint.Align.RIGHT).setTextSize(PrintSize.small)
                                ))
                        );
                    }
                    returnLineRows.add(row);
                }
            } else {
                for (OrderLinePrintData line : printData.returnPrintData.Lines) {
                    BoxRow row = new BoxRow();
                    row.addColumns(
                            new BoxColumn(2).setText(HelperMethods.currencyToString(line.NetAmount)).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.smaller).setMargin(PrintSize.xxxSmall),
                            new BoxColumn(1).setText(HelperMethods.bigDecimalToString(line.TotalQty)).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.smaller).setMargin(PrintSize.xxxSmall)
                    );

                    if (configs.compare(ConfigKey.PrintProductPrice, true)) {
                        if (ownerKeys.DataOwnerKey.equalsIgnoreCase("e00f3940-e47a-4c49-83a4-352c4a260aed")) {// ofogh roudehen
                            row.addColumn(new BoxColumn(6).setBorderWidth(0).addRows(
                                    new BoxRow().setBorderWidth(0).addColumn(
                                            new BoxColumn().setText(line.ProductName).setTextAlign(Paint.Align.RIGHT).setTextSize(PrintSize.smaller)
                                    ),
                                    new BoxRow().setBorderWidth(0).addColumns(
                                            new BoxColumn().setBorderWidth(0).addRow(
                                                    new BoxRow().setBorderWidth(0).addColumns(
                                                            new BoxColumn(2).setText(HelperMethods.currencyToString(line.UnitPrice)).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.smaller).setBorderWidth(0),
                                                            new BoxColumn(1).setText(getActivity().getString(R.string.unit_price)).setTextAlign(Paint.Align.RIGHT).setTextSize(PrintSize.smaller).setBorderWidth(0)
                                                    ))
                                    ))
                            );
                        } else {
                            row.addColumn(new BoxColumn(4).setBorderWidth(0).addRows(
                                    new BoxRow().setBorderWidth(0).addColumn(
                                            new BoxColumn().setText(line.ProductName).setTextAlign(Paint.Align.RIGHT).setTextSize(line.ProductName.length() > 20 ? PrintSize.xSmall : PrintSize.smaller)
                                    ),
                                    new BoxRow().setBorderWidth(0).addColumns(
                                            new BoxColumn().setBorderWidth(0).addRow(
                                                    new BoxRow().setBorderWidth(0).addColumns(
                                                            new BoxColumn(2).setText(HelperMethods.currencyToString(line.UnitPrice)).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.smaller).setBorderWidth(0),
                                                            new BoxColumn(1).setText(getActivity().getString(R.string.unit_price)).setTextAlign(Paint.Align.RIGHT).setTextSize(PrintSize.smaller).setBorderWidth(0)
                                                    ))
                                    ))
                            );
                        }
                    } else {
                        if (ownerKeys.DataOwnerKey.equalsIgnoreCase("e00f3940-e47a-4c49-83a4-352c4a260aed")) {// ofogh roudehen
                            row.addColumn(new BoxColumn(6).setBorderWidth(0).addRows(
                                    new BoxRow().setBorderWidth(0).addColumn(
                                            new BoxColumn().setText(line.ProductName).setTextAlign(Paint.Align.RIGHT).setTextSize(PrintSize.smaller)
                                    ))
                            );
                        } else {
                            row.addColumn(new BoxColumn(4).setBorderWidth(0).addRows(
                                    new BoxRow().setBorderWidth(0).addColumn(
                                            new BoxColumn().setText(line.ProductName).setTextAlign(Paint.Align.RIGHT).setTextSize(line.ProductName.length() > 20 ? PrintSize.xSmall : PrintSize.smaller)
                                    ))
                            );
                        }

                    }
                    String rowValue = configs.compare(ConfigKey.PrintProductRow, true) ? String.valueOf(line.Row) : line.ProductCode;
                    row.addColumn(new BoxColumn(1).setText(rowValue).setTextSize(PrintSize.xSmall).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER));
                    returnLineRows.add(row);
                }
            }
            PrintHelper.getInstance().addBox(new Box().addColumn(new BoxColumn().addRows(returnLineRows)));
            // endregion

            // region invoice footer
            BoxColumn returnFooterColumn = new BoxColumn();
            if (configs.compare(ConfigKey.PrintItemsCount, true)) {
                addRowItem(returnFooterColumn, R.string.print_itemscount, HelperMethods.bigDecimalToString(printData.returnPrintData.TotalQty), true, PrintSize.medium);
            }
            if (configs.compare(ConfigKey.PrintTotalPurchaseAmount, true)) {
                addRowItem(returnFooterColumn, R.string.print_total_return_amount, HelperMethods.currencyToString(printData.ReturnNetAmount), true, PrintSize.medium);
            }
//            if (configs.compare(ConfigKey.PrintTotalDiscounts, true)) {
//                addRowItem(returnFooterColumn, R.string.print_totaldiscounts, HelperMethods.currencyToString(printData.returnPrintData.DiscountAmount), true);
//            }
//            if (configs.compare(ConfigKey.PrintSumOfComplications, true)) {
//                addRowItem(returnFooterColumn, R.string.print_sumofcomplications, HelperMethods.currencyToString(printData.returnPrintData.AddAmount), true);
//            }
//            if (configs.compare(ConfigKey.PrintGrossPurchases, true)) {
//                addRowItem(returnFooterColumn, R.string.print_grosspurchases, HelperMethods.currencyToString(printData.returnPrintData.NetAmount), true);
//            }

            PrintHelper.getInstance().addBox(new Box().addColumn(returnFooterColumn));
            // endregion
        }

        // region payments
        if (!VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
            PrintHelper.getInstance().feedLine(PrintSize.small);
            PrintHelper.getInstance().addParagraph(PrintSize.medium, getActivity().getString(R.string.customer_payments), Paint.Align.CENTER);
            PrintHelper.getInstance().feedLine(PrintSize.xSmall);
            BoxColumn totalColumn = new BoxColumn();
            if (printData.OpenInvoiceAmount.compareTo(Currency.ZERO) != 0 || printData.ReturnNetAmount.compareTo(Currency.ZERO) != 0) {
                if (configs.compare(ConfigKey.PrintReturn, true)) {
                    addRowItem(totalColumn, R.string.sell_return_net_amount, HelperMethods.currencyToString(printData.ReturnNetAmount), true, PrintSize.medium);
                }
                if (!VaranegarApplication.is(VaranegarApplication.AppId.Contractor))
                    addRowItem(totalColumn, R.string.old_invoice_debt, HelperMethods.currencyToString(printData.OpenInvoiceAmount), true, PrintSize.medium);
                addRowItem(totalColumn, R.string.total_orders_net_amount, HelperMethods.currencyToString(printData.TotalNetAmount), true, PrintSize.medium);
            }
            if (VaranegarApplication.is(VaranegarApplication.AppId.Contractor)) {
                addRowItem(totalColumn, R.string.discount, HelperMethods.currencyToString(printData.DiscountPayment), true, PrintSize.medium);
                addRowItem(totalColumn, R.string.net_amount, HelperMethods.currencyToString(printData.TotalNetAmount.subtract(printData.DiscountPayment)), true, PrintSize.medium);
            } else
                addRowItem(totalColumn, R.string.net_amount, HelperMethods.currencyToString(printData.TotalNetAmount.add(printData.OpenInvoiceAmount).subtract(printData.ReturnNetAmount)), true, PrintSize.medium);
            if (configs.compare(ConfigKey.PrintCustomerRemainWithThisInvoice, true)) {
                addRowItem(totalColumn, R.string.all_net_amount, HelperMethods.currencyToString((printData.TotalNetAmount.subtract(printData.ReturnNetAmount).add(printData.CustomerRemain))), true, PrintSize.medium);
            }
            PrintHelper.getInstance().addBox(new Box().addColumn(totalColumn));
            if (!VaranegarApplication.is(VaranegarApplication.AppId.Contractor)) {
                PrintHelper.getInstance().feedLine(PrintSize.large);
                BoxColumn paymentsColumn = new BoxColumn();
                if (printData.CashPayment.compareTo(Currency.ZERO) > 0)
                    addRowItem(paymentsColumn, R.string.cach_total_payment, HelperMethods.currencyToString(printData.CashPayment), true, PrintSize.medium);
                if (printData.CardPayment.compareTo(Currency.ZERO) > 0)
                    addRowItem(paymentsColumn, R.string.card_total_payment, HelperMethods.currencyToString(printData.CardPayment), true, PrintSize.medium);
                if (printData.CheckPayment.compareTo(Currency.ZERO) > 0)
                    addRowItem(paymentsColumn, R.string.check_total_payment, HelperMethods.currencyToString(printData.CheckPayment), true, PrintSize.medium);
                addRowItem(paymentsColumn, R.string.total_customer_payment, HelperMethods.currencyToString(printData.TotalPayment.subtract(printData.DiscountPayment)), true, PrintSize.medium);
                if (configs.compare(ConfigKey.PrintTotalDiscountAmount, true)) {
                    addRowItem(paymentsColumn, R.string.print_totaldiscountamount, HelperMethods.currencyToString(printData.DiscountPayment), true, PrintSize.medium);
                }
                addRowItem(paymentsColumn, R.string.receipt_sum, HelperMethods.currencyToString(printData.TotalNetAmount.add(printData.OpenInvoiceAmount).subtract(printData.ReturnNetAmount).subtract(printData.TotalPayment)), true, PrintSize.large);
                if (configs.compare(ConfigKey.PrintCustomerbalances, true)) {
                    addRowItem(paymentsColumn, R.string.account_balance, HelperMethods.currencyToString((printData.TotalNetAmount.subtract(printData.ReturnNetAmount).add(printData.CustomerRemain)).subtract(printData.TotalPayment.subtract(printData.CreditPayment))), true, PrintSize.larger);
                }
                PrintHelper.getInstance().addBox(new Box().addColumn(paymentsColumn));
            }
        }
        // endregion

        // region print footer
        boolean hasPrintFooterColumn = false;
        BoxColumn printFooterColumn = new BoxColumn();

        if (configs.compare(ConfigKey.PrintCompanyName, true)) {
            addRowItem(printFooterColumn, R.string.print_companyname, printData.CompanyName, true, PrintSize.medium);
            hasPrintFooterColumn = true;
        }
        if (configs.compare(ConfigKey.PrintCompanyEmail, true)) {
            addRowItem(printFooterColumn, R.string.print_companyemail, printData.CompanyEmail, true, PrintSize.medium);
            hasPrintFooterColumn = true;
        }
        if (configs.compare(ConfigKey.PrintCompanyTelephone, true)) {
            addRowItem(printFooterColumn, R.string.print_companytelephone, printData.CompanyTell, true, PrintSize.medium);
            hasPrintFooterColumn = true;
        }

        if (configs.compare(ConfigKey.PrintDealerMobile, true)) {
            if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
                addRowItem(printFooterColumn, R.string.print_distributer_mobile, printData.DealerMobile, true, PrintSize.medium);
            else
                addRowItem(printFooterColumn, R.string.print_dealer_mobile, printData.DealerMobile, true, PrintSize.medium);

            hasPrintFooterColumn = true;
        }
        if (configs.compare(ConfigKey.PrintDealerName, true)) {
            if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
                addRowItem(printFooterColumn, R.string.print_distributername, printData.DistributerName, true, PrintSize.medium);
            else
                addRowItem(printFooterColumn, R.string.print_dealername, printData.DealerName, true, PrintSize.medium);
            hasPrintFooterColumn = true;
        }
        if (configs.compare(ConfigKey.PrintDistributionCenterName, true)) {
            addRowItem(printFooterColumn, R.string.print_distributioncentername, printData.DistributionCenterName, true, PrintSize.medium);
            hasPrintFooterColumn = true;
        }
        if (configs.compare(ConfigKey.PrintTourNo, true)) {
            addRowItem(printFooterColumn, R.string.print_tourno, printData.TourNo, true, PrintSize.medium);
            hasPrintFooterColumn = true;
        }
        if (configs.compare(ConfigKey.PrintFactorTime, true)) {
            if (!VaranegarApplication.is(VaranegarApplication.AppId.PreSales))
                addRowItem(printFooterColumn, R.string.print_factortime, DateHelper.toString(new Date(), DateFormat.Complete, VasHelperMethods.getSysConfigLocale(getActivity())), true, PrintSize.medium);
            else
                addRowItem(printFooterColumn, R.string.print_pre_factortime, DateHelper.toString(new Date(), DateFormat.Complete, VasHelperMethods.getSysConfigLocale(getActivity())), true, PrintSize.medium);

            hasPrintFooterColumn = true;
        }
        if (hasPrintFooterColumn) {
            PrintHelper.getInstance().feedLine(PrintSize.medium);
            PrintHelper.getInstance().addBox(new Box().addColumn(printFooterColumn));
        }

        if (ownerKeys.DataOwnerKey.equalsIgnoreCase("8638C58A-F145-467D-960E-817EA44EBF45")) {//mihan kish
            PrintHelper.getInstance().feedLine(new PrintSize(30.0F));
            PrintHelper.getInstance().addParagraph(PrintSize.medium, getActivity().getString(R.string.print_sign) + ":", Paint.Align.RIGHT);
        }
        if (configs.compare(ConfigKey.PrintCompanyAddress, true)) {
            PrintHelper.getInstance().feedLine(PrintSize.small);
            PrintHelper.getInstance().addParagraph(PrintSize.smaller, getActivity().getString(R.string.print_companyaddress) + " : " + printData.CompanyAddress, Paint.Align.RIGHT);
            PrintHelper.getInstance().feedLine(PrintSize.small);
        }

        // endregion

        addFooter(configs.getStringValue(ConfigKey.PrintInvoiceMessage, null));

    }

    private void runCustomizedPrint(String fortmat) throws Exception {
        RootLayout rootLayout = new RootLayout();
        Typeface customFont;
        if (Build.MODEL.equalsIgnoreCase("Sepehr A1"))
            customFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Iran Sans Bold.ttf");
        else
            customFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/anatoli.ttf");
        PrintHelper.getInstance().setDefaultTypeFace(customFont);
        PrintHelper.getInstance().setMargin(PrintSize.xxSmall);
        rootLayout.deserialize(fortmat, new RootLayout.IPopulatePrintData() {
            @Override
            public void run(List<String> list) {
                PrintDataHelper printDataHelper = new PrintDataHelper(getActivity());
                printDataHelper.addLogo();
                printDataHelper.addCustomerInfo(customerId);
                printDataHelper.addOrdersInfo(customerId);
                printDataHelper.addTitle(type);
                printDataHelper.addTourInfo();
                printDataHelper.addDealerInfo();
                printDataHelper.addReturnsInfo(customerId);
                printDataHelper.addPaymentInfo(customerId);
            }
        });


//                String str = LayoutDataMap.getInstance().toString();
//                Timber.d(str);
//
//                str = LayoutDataMap.getInstance().printDescriptions();
//                Timber.d(str);

    }

    private PrintData fillPrintData() {
        CustomerModel customerModel = new CustomerManager(getActivity()).getItem(customerId);
        if (customerModel == null)
            throw new NullPointerException("Customer with id " + customerId.toString() + " not found.");
        PrintData printData = new PrintData();
        List<CustomerCallOrderModel> customerCallOrderModels = new CustomerCallOrderManager(getActivity()).getCustomerCallOrders(customerId);
        CustomerCallOrderOrderViewManager customerCallOrderOrderViewManager = new CustomerCallOrderOrderViewManager(getActivity());

        UserModel userModel = UserManager.readFromFile(getActivity());
        if (userModel == null)
            throw new NullPointerException();

        TourManager tourManager = new TourManager(getActivity());
        TourModel tourModel = tourManager.loadTour();
        if (tourModel == null)
            throw new NullPointerException();

        if (VaranegarApplication.is(VaranegarApplication.AppId.Contractor)) {
            List<CustomerCallOrderModel> customerCallOrderModels1 = new CustomerCallOrderManager(getActivity()).getCustomerCallOrders(customerId);
            Currency discount = Currency.ZERO;
            for (CustomerCallOrderModel customerCallOrderModel : customerCallOrderModels1) {
                discount = discount.add(customerCallOrderModel.RoundOrderOtherDiscount);
            }
            printData.DiscountPayment = discount;
        }

        printData.CustomerName = customerModel.CustomerName == null ? "" : customerModel.CustomerName;
        printData.CustomerCode = customerModel.CustomerCode == null ? "" : customerModel.CustomerCode;
        printData.CustomerMobile = customerModel.Mobile == null ? "" : customerModel.Mobile;
        printData.CustomerAddress = customerModel.Address == null ? "" : customerModel.Address;
        printData.StoreName = customerModel.StoreName == null ? "" : customerModel.StoreName;
        printData.CustomerRemainAll = customerModel.RemainDebit;
        CustomerRemainPerLineManager customerRemainPerLineManager = new CustomerRemainPerLineManager(getActivity());
        CustomerRemainPerLineModel customerRemainPerLineModel = customerRemainPerLineManager.getCustomerRemainPerLine(customerId);
        if (customerRemainPerLineModel != null)
            printData.CustomerRemain = customerRemainPerLineModel.CustRemAmount;
        else
            printData.CustomerRemain = customerModel.CustomerRemain;
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            printData.DealerMobile = tourModel.AgentMobile;
        } else
            printData.DealerMobile = configs.getStringValue(ConfigKey.Mobile, "");
        printData.DealerName =
                printData.DistributerName = userModel.UserName == null ? "" : userModel.UserName;
        printData.CompanyName = configs.getStringValue(ConfigKey.CompanyName, "");
        printData.CompanyAddress = configs.getStringValue(ConfigKey.CompanyAddress, "");
        printData.CompanyEmail = configs.getStringValue(ConfigKey.CompanyEmail, "");
        printData.CompanyTell = configs.getStringValue(ConfigKey.CompanyPhone, "");
        printData.DistributionCenterName = configs.getStringValue(ConfigKey.DistributionCenterName, "");
        printData.TourNo = String.valueOf(tourModel.TourNo);
        CustomerCallReturnViewModel customerCallReturnViewModel = new CustomerCallReturnViewManager(getActivity()).getItem(CustomerCallReturnViewManager.getLines(customerId, null, null));
        if (customerCallReturnViewModel != null) {
            OrderPrintData callOrderPrint = new OrderPrintData();
            callOrderPrint.TotalAmount = customerCallReturnViewModel.TotalRequestAmount != null ? customerCallReturnViewModel.TotalRequestAmount : Currency.ZERO;
            callOrderPrint.NetAmount = customerCallReturnViewModel.TotalRequestNetAmount != null ? customerCallReturnViewModel.TotalRequestNetAmount : Currency.ZERO;
            callOrderPrint.InvoiceDate = DateHelper.toString(new Date(), DateFormat.Date, VasHelperMethods.getSysConfigLocale(getActivity()));

            CustomerCallReturnLinesViewManager customerCallReturnLinesViewManager = new CustomerCallReturnLinesViewManager(getActivity());
            List<CustomerCallReturnLinesViewModel> lines = customerCallReturnLinesViewManager.getCustomerLines(customerId, null);
            int row = 1;
            for (CustomerCallReturnLinesViewModel item : lines) {
                boolean hasQty = item.TotalReturnQty != null && item.TotalReturnQty.compareTo(BigDecimal.ZERO) > 0;
                if (hasQty) {
                    OrderLinePrintData orderLinePrintData = new OrderLinePrintData();
                    orderLinePrintData.Row = row++;
                    orderLinePrintData.ProductName = item.ProductName;
                    orderLinePrintData.ProductCode = item.ProductCode;
                    orderLinePrintData.UnitName = item.UnitName;
                    orderLinePrintData.TotalQty = item.TotalReturnQty;
                    orderLinePrintData.Qty = item.Qty;
                    orderLinePrintData.ConvertFactor = item.ConvertFactor;
                    orderLinePrintData.UnitPrice = item.RequestUnitPrice;
                    orderLinePrintData.RequestAmount = item.TotalRequestAmount != null ? item.TotalRequestAmount : Currency.ZERO;
                    if (item.TotalReturnQty != null) {
                        callOrderPrint.TotalQty = callOrderPrint.TotalQty.add(item.TotalReturnQty);
                    }
                    double totalDiscount = HelperMethods.currencyToDouble(item.TotalRequestDis1Amount) +
                            HelperMethods.currencyToDouble(item.TotalRequestDis2Amount) +
                            HelperMethods.currencyToDouble(item.TotalRequestDis3Amount) +
                            HelperMethods.currencyToDouble(item.TotalRequestDisOtherAmount);

                    orderLinePrintData.DiscountAmount = Currency.valueOf(totalDiscount);
                    callOrderPrint.DiscountAmount.add(Currency.valueOf(totalDiscount));
                    if (item.TotalRequestDis1Amount != null)
                        callOrderPrint.Dis1.add(item.TotalRequestDis1Amount);
                    if (item.TotalRequestDis2Amount != null)
                        callOrderPrint.Dis2.add(item.TotalRequestDis2Amount);
                    if (item.TotalRequestDis3Amount != null)
                        callOrderPrint.Dis3.add(item.TotalRequestDis3Amount);
                    if (item.TotalRequestAdd1Amount != null) {
                        orderLinePrintData.AddAmount = orderLinePrintData.AddAmount.add(item.TotalRequestAdd1Amount);
                    }
                    if (item.TotalRequestAdd2Amount != null) {
                        orderLinePrintData.AddAmount = orderLinePrintData.AddAmount.add(item.TotalRequestAdd2Amount);
                    }
                    callOrderPrint.AddAmount.add(orderLinePrintData.AddAmount);
                    Currency returnLineNetAmount = orderLinePrintData.RequestAmount.add(orderLinePrintData.AddAmount).subtract(orderLinePrintData.DiscountAmount);
                    if (item.TotalRequestAmount != null) {
                        orderLinePrintData.NetAmount = returnLineNetAmount;
                        printData.ReturnNetAmount = printData.ReturnNetAmount.add(returnLineNetAmount);
                    }
                    callOrderPrint.Lines.add(orderLinePrintData);
                }
            }

            printData.returnPrintData = callOrderPrint;
        }

        PaymentManager paymentManager = new PaymentManager(getActivity());
        Currency totalPaid = paymentManager.getTotalPaid(customerId);
        printData.TotalPayment = printData.TotalPayment.add(totalPaid);
        List<PaymentModel> payments = paymentManager.listPayments(customerId);
        if (payments.size() > 0) {
            for (PaymentModel paymentModel :
                    payments) {
                if (paymentModel.PaymentType.equals(PaymentType.Card))
                    printData.CardPayment = printData.CardPayment.add(paymentModel.Amount);
                else if (paymentModel.PaymentType.equals(PaymentType.Check))
                    printData.CheckPayment = printData.CheckPayment.add(paymentModel.Amount);
                else if (paymentModel.PaymentType.equals(PaymentType.Credit))
                    printData.CreditPayment = printData.CreditPayment.add(paymentModel.Amount);
                else if (paymentModel.PaymentType.equals(PaymentType.Cash))
                    printData.CashPayment = printData.CashPayment.add(paymentModel.Amount);
                else if (paymentModel.PaymentType.equals(PaymentType.Discount))
                    printData.DiscountPayment = printData.DiscountPayment.add(paymentModel.Amount);
            }
        }
        List<OldInvoiceHeaderViewModel> oldInvoices = new OldInvoiceHeaderViewManager(getActivity()).getInvoicesByCustomerId(customerId);
        for (OldInvoiceHeaderViewModel oldInvoiceHeaderViewModel :
                oldInvoices) {
            if (oldInvoiceHeaderViewModel.HasPayment)
                printData.OpenInvoiceAmount = printData.OpenInvoiceAmount.add(oldInvoiceHeaderViewModel.RemAmount);
        }
        for (CustomerCallOrderModel callOrder : customerCallOrderModels) {
            OrderPrintData callOrderPrint = new OrderPrintData();
            OrderAmount orderAmount = customerCallOrderOrderViewManager.calculateTotalAmount(callOrder.UniqueId);
            printData.TotalNetAmount = printData.TotalNetAmount.add(orderAmount.NetAmount);
            callOrderPrint.TotalAmount = callOrderPrint.TotalAmount.add(orderAmount.TotalAmount);
            if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
                List<CallOrderLineModel> orderLines = new CallOrderLineManager(getActivity()).getOrderLines(callOrder.UniqueId);
                for (CallOrderLineModel callOrderLineModel : orderLines) {
                    callOrderPrint.DiscountAmount = callOrderPrint.DiscountAmount.add(callOrderLineModel.PresalesDis1Amount.add(callOrderLineModel.PresalesDis2Amount).add(callOrderLineModel.PresalesDis3Amount).add(callOrderLineModel.PresalesOtherDiscountAmount));
                    callOrderPrint.AddAmount = callOrderPrint.AddAmount.add(callOrderLineModel.PresalesAdd1Amount.add(callOrderLineModel.PresalesAdd2Amount).add(callOrderLineModel.PresalesOtherAddAmount));
                }
                if (callOrderPrint.DiscountAmount.compareTo(Currency.ZERO) == 1 || callOrderPrint.AddAmount.compareTo(Currency.ZERO) == 1)
                    callOrderPrint.NetAmount = callOrderPrint.NetAmount.add(orderAmount.TotalAmount.add(callOrderPrint.AddAmount).subtract(callOrderPrint.DiscountAmount));
            } else {
                callOrderPrint.DiscountAmount = callOrderPrint.DiscountAmount.add(orderAmount.DiscountAmount);
                callOrderPrint.Dis1 = callOrderPrint.Dis1.add(orderAmount.Dis1Amount);
                callOrderPrint.Dis2 = callOrderPrint.Dis2.add(orderAmount.Dis2Amount);
                callOrderPrint.Dis3 = callOrderPrint.Dis3.add(orderAmount.Dis3Amount);
                callOrderPrint.AddAmount = callOrderPrint.AddAmount.add(orderAmount.AddAmount);
                callOrderPrint.NetAmount = callOrderPrint.NetAmount.add(orderAmount.NetAmount);
            }
            callOrderPrint.Comment = callOrder.Comment;
            callOrderPrint.LocalPaperNo = callOrder.LocalPaperNo;
            callOrderPrint.DealerName = callOrder.DealerName;
            callOrderPrint.DealerMobile = callOrder.DealerMobile;
            callOrderPrint.InvoiceDate = DateHelper.toString(callOrder.SaleDate, DateFormat.Date, VasHelperMethods.getSysConfigLocale(getActivity()));
            if (callOrder.UniqueId != null) {
                List<CustomerCallOrderOrderViewModel> lines = customerCallOrderOrderViewManager.getLines(callOrder.UniqueId, customerCallOrderOrderViewManager.getOrderBy());
                int row = 1;
                for (CustomerCallOrderOrderViewModel item : lines) {
                    OrderLinePrintData orderLinePrintData = new OrderLinePrintData();
                    orderLinePrintData.Row = row++;
                    orderLinePrintData.ProductName = item.ProductName;
                    orderLinePrintData.ProductCode = item.ProductCode;
                    orderLinePrintData.UnitName = item.UnitName;
                    orderLinePrintData.TotalQty = item.TotalQty;
                    orderLinePrintData.ConvertFactor = item.ConvertFactor;
                    orderLinePrintData.Qty = item.Qty;
                    orderLinePrintData.UnitPrice = item.UnitPrice;
                    if (item.IsPromoLine)
                        orderLinePrintData.RequestAmount = item.PromotionPrice == null ? Currency.ZERO : item.PromotionPrice;
                    else
                        orderLinePrintData.RequestAmount = item.RequestAmount;
                    if (item.TotalQty != null) {
                        callOrderPrint.TotalQty = callOrderPrint.TotalQty.add(item.TotalQty);
                    }

                    if (item.RequestDis1Amount != null) {
                        orderLinePrintData.DiscountAmount = orderLinePrintData.DiscountAmount.add(item.RequestDis1Amount);
                    }
                    if (item.RequestDis2Amount != null) {
                        orderLinePrintData.DiscountAmount = orderLinePrintData.DiscountAmount.add(item.RequestDis2Amount);
                    }
                    if (item.RequestDis3Amount != null) {
                        orderLinePrintData.DiscountAmount = orderLinePrintData.DiscountAmount.add(item.RequestDis3Amount);
                    }
                    if (item.RequestOtherDiscountAmount != null) {
                        orderLinePrintData.DiscountAmount = orderLinePrintData.DiscountAmount.add(item.RequestOtherDiscountAmount);
                    }
                    if (item.RequestAdd1Amount != null) {
                        orderLinePrintData.AddAmount = orderLinePrintData.AddAmount.add(item.RequestAdd1Amount);
                    }
                    if (item.RequestAdd2Amount != null) {
                        orderLinePrintData.AddAmount = orderLinePrintData.AddAmount.add(item.RequestAdd2Amount);
                    }
                    if (item.RequestAddOtherAmount != null) {
                        orderLinePrintData.AddAmount = orderLinePrintData.AddAmount.add(item.RequestAddOtherAmount);
                    }
                    if (item.RequestTaxAmount != null) {
                        orderLinePrintData.AddAmount = orderLinePrintData.AddAmount.add(item.RequestTaxAmount);
                    }
                    if (item.RequestChargeAmount != null) {
                        orderLinePrintData.AddAmount = orderLinePrintData.AddAmount.add(item.RequestChargeAmount);
                    }
                    if (item.RequestAmount != null)
                        orderLinePrintData.NetAmount = orderLinePrintData.RequestAmount.add(orderLinePrintData.AddAmount).subtract(orderLinePrintData.DiscountAmount);
                    callOrderPrint.Lines.add(orderLinePrintData);
                }

//                Add Promotions to lines for presale
                if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales) && configs.compare(ConfigKey.PrintPrize, true)) {
                    CallOrderLinesTempManager callOrderLinesTempManager = new CallOrderLinesTempManager(getActivity());
                    List<CallOrderLinesTempModel> callOrderLinesTemp = callOrderLinesTempManager.getLines(callOrder.UniqueId);
                    ProductManager productManager = new ProductManager(getActivity());
                    for (CallOrderLinesTempModel callOrderLinesTempModel : callOrderLinesTemp) {
                        OrderLinePrintData orderLinePrintData = new OrderLinePrintData();
                        orderLinePrintData.Row = row++;
                        ProductModel productModel = productManager.getItem(callOrderLinesTempModel.ProductUniqueId);
                        if (productModel != null) {
                            orderLinePrintData.ProductName = productModel.ProductName;
                            orderLinePrintData.ProductCode = productModel.ProductCode;
                        }
                        CallOrderLinesQtyTempManager callOrderLinesQtyTempManager = new CallOrderLinesQtyTempManager(getActivity());
                        List<CallOrderLinesQtyTempModel> callOrderLinesQtyTempModelList = callOrderLinesQtyTempManager.getLines(callOrderLinesTempModel.UniqueId);
                        ProductUnitViewManager productUnitViewManager = new ProductUnitViewManager(getActivity());
                        StringBuilder unitName = new StringBuilder();
                        BigDecimal totalQty = BigDecimal.ZERO;
                        StringBuilder convertFactor = new StringBuilder();
                        StringBuilder qty = new StringBuilder();
                        for (int i = 0; i < callOrderLinesQtyTempModelList.size(); i++) {
                            ProductUnitViewModel productUnitViewModel = productUnitViewManager.getItem(callOrderLinesQtyTempModelList.get(i).ProductUnitId);
                            if (productUnitViewModel != null) {
                                if (i == 0) {
                                    unitName.append(productUnitViewModel.UnitName);
                                    convertFactor.append(productUnitViewModel.ConvertFactor);
                                    qty.append(callOrderLinesQtyTempModelList.get(i).Qty);
                                } else {
                                    unitName.append(":").append(productUnitViewModel.UnitName);
                                    convertFactor.append(":").append(productUnitViewModel.ConvertFactor);
                                    qty.append(":").append(callOrderLinesQtyTempModelList.get(i).Qty);
                                }
                                if (callOrderLinesTempModel.RequestBulkQtyUnitUniqueId != null) {
                                    totalQty = totalQty.add(callOrderLinesQtyTempModelList.get(i).Qty);
                                } else {
                                    totalQty = totalQty.add(callOrderLinesQtyTempModelList.get(i).Qty.multiply(new BigDecimal(productUnitViewModel.ConvertFactor)));
                                }
                            }
                        }
                        orderLinePrintData.UnitName = unitName.toString();
                        orderLinePrintData.TotalQty = totalQty;
                        orderLinePrintData.ConvertFactor = convertFactor.toString();
                        orderLinePrintData.Qty = qty.toString();

                        //we just save promotions in CustomerCallOrderLinesTempTable
                        orderLinePrintData.UnitPrice = callOrderLinesTempModel.PromotionPrice;
                        orderLinePrintData.RequestAmount = callOrderLinesTempModel.PromotionPrice == null ? Currency.ZERO : callOrderLinesTempModel.PromotionPrice;

                        if (totalQty != null) {
                            callOrderPrint.TotalQty = callOrderPrint.TotalQty.add(totalQty);
                        }
                        if (callOrderLinesTempModel.RequestDis1Amount != null) {
                            orderLinePrintData.DiscountAmount = orderLinePrintData.DiscountAmount.add(callOrderLinesTempModel.RequestDis1Amount);
                        }
                        if (callOrderLinesTempModel.RequestDis2Amount != null) {
                            orderLinePrintData.DiscountAmount = orderLinePrintData.DiscountAmount.add(callOrderLinesTempModel.RequestDis2Amount);
                        }
                        if (callOrderLinesTempModel.RequestDis3Amount != null) {
                            orderLinePrintData.DiscountAmount = orderLinePrintData.DiscountAmount.add(callOrderLinesTempModel.RequestDis3Amount);
                        }
                        if (callOrderLinesTempModel.RequestOtherDiscountAmount != null) {
                            orderLinePrintData.DiscountAmount = orderLinePrintData.DiscountAmount.add(callOrderLinesTempModel.RequestOtherDiscountAmount);
                        }
                        if (callOrderLinesTempModel.RequestAdd1Amount != null) {
                            orderLinePrintData.AddAmount = orderLinePrintData.AddAmount.add(callOrderLinesTempModel.RequestAdd1Amount);
                        }
                        if (callOrderLinesTempModel.RequestAdd2Amount != null) {
                            orderLinePrintData.AddAmount = orderLinePrintData.AddAmount.add(callOrderLinesTempModel.RequestAdd2Amount);
                        }
                        if (callOrderLinesTempModel.RequestOtherAddAmount != null) {
                            orderLinePrintData.AddAmount = orderLinePrintData.AddAmount.add(callOrderLinesTempModel.RequestOtherAddAmount);
                        }
                        if (callOrderLinesTempModel.RequestTaxAmount != null) {
                            orderLinePrintData.AddAmount = orderLinePrintData.AddAmount.add(callOrderLinesTempModel.RequestTaxAmount);
                        }
                        if (callOrderLinesTempModel.RequestChargeAmount != null) {
                            orderLinePrintData.AddAmount = orderLinePrintData.AddAmount.add(callOrderLinesTempModel.RequestChargeAmount);
                        }
                        orderLinePrintData.NetAmount = orderLinePrintData.RequestAmount.add(orderLinePrintData.AddAmount).subtract(orderLinePrintData.DiscountAmount);
                        callOrderPrint.Lines.add(orderLinePrintData);
                    }
                }
            }
            printData.orderPrintData.add(callOrderPrint);
        }

//        if (customerCallOrderModel.OrderPaymentTypeUniqueId != null) {
//            PaymentTypeOrderModel paymentTypeOrderModel = new PaymentOrderTypeManager(getActivity()).getItem(customerCallOrderModel.OrderPaymentTypeUniqueId);
//            invoice.OrderPaymentTypeId = customerCallOrderModel.OrderPaymentTypeUniqueId;
//            invoice.OrderPaymentTypeRef = Integer.parseInt(paymentTypeOrderModel.BackOfficeId);
//            invoice.OrderPaymentTypeName = paymentTypeOrderModel.PaymentTypeOrderName;
//        }
        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales))
            printData.DocumentType = getActivity().getString(R.string.proforma);
        else if (VaranegarApplication.is(VaranegarApplication.AppId.HotSales))
            printData.DocumentType = getActivity().getString(R.string.invoice);
        else
            printData.DocumentType = getActivity().getString(R.string.check);
        return printData;
    }

    class PrintData {
        String CustomerName;
        String CustomerCode;
        String CustomerMobile;
        String CustomerAddress;
        String StoreName;
        Currency CustomerRemainAll;
        Currency CustomerRemain;
        String DealerMobile;
        String DealerName;
        String DistributerName;
        String CompanyAddress;
        String CompanyTell;
        String CompanyEmail;
        String CompanyName;
        String DistributionCenterName;
        ArrayList<OrderPrintData> orderPrintData = new ArrayList<>();
        OrderPrintData returnPrintData;
        String DocumentType;
        Currency TotalNetAmount = Currency.ZERO;
        Currency ReturnNetAmount = Currency.ZERO;
        String TourNo;
        Currency OpenInvoiceAmount = Currency.ZERO;
        Currency TotalPayment = Currency.ZERO;
        Currency CardPayment = Currency.ZERO;
        Currency CheckPayment = Currency.ZERO;
        Currency CreditPayment = Currency.ZERO;
        Currency CashPayment = Currency.ZERO;
        Currency DiscountPayment = Currency.ZERO;
    }

    class OrderPrintData {
        Currency TotalAmount = Currency.ZERO;
        Currency NetAmount = Currency.ZERO;
        BigDecimal TotalQty = BigDecimal.ZERO;
        String Comment = "";
        String LocalPaperNo = "";
        String DealerName = "";
        String DealerMobile = "";
        String InvoiceDate;
        Currency DiscountAmount = Currency.ZERO;
        Currency Dis1 = Currency.ZERO;
        Currency Dis2 = Currency.ZERO;
        Currency Dis3 = Currency.ZERO;
        Currency AddAmount = Currency.ZERO;
        ArrayList<OrderLinePrintData> Lines = new ArrayList<>();
    }

    class OrderLinePrintData {
        int Row;
        String ProductCode;
        String ProductName;
        Currency DiscountAmount = Currency.ZERO;
        Currency AddAmount = Currency.ZERO;
        Currency UnitPrice = Currency.ZERO;
        String UnitName;
        String Qty;
        String ConvertFactor;
        BigDecimal TotalQty = BigDecimal.ZERO;
        Currency RequestAmount = Currency.ZERO;
        Currency NetAmount = Currency.ZERO;
    }

//    class CustomerCallOrderItemsReadyForPrint {
//        List<BoxRow> rows = new ArrayList<>();
//        BoxRow footerInfo;
//    }
}