package com.varanegar.hotsales.report;

import android.graphics.Color;
import android.graphics.Paint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.printlib.PrintHelper;
import com.varanegar.printlib.PrintSize;
import com.varanegar.printlib.box.Box;
import com.varanegar.printlib.box.BoxColumn;
import com.varanegar.printlib.box.BoxRow;
import com.varanegar.printlib.driver.PrintCallback;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.WarehouseProductQtyViewManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.OwnerKeysWrapper;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.WarehouseProductQtyView.WarehouseProductQtyViewModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.print.BasePrintHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by g.aliakbar on 07/04/2018.
 */

public class WarehouseQtyPrintHelper extends BasePrintHelper {
    private static final UUID AllProducts = UUID.fromString("1C99CFE7-849B-43D8-BE6B-1975F984AB9F");
    private static final UUID AllAvailableProducts = UUID.fromString("06646C61-D711-4AEF-A4FE-57474FE83B3E");

    public WarehouseQtyPrintHelper(@NonNull AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public void print(@NonNull final PrintCallback callback) {

        List<WarehouseProductQtyViewModel> warehouseList;
        SysConfigManager sysConfigManager = new SysConfigManager(getActivity());
        SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.PrintStockLevelsBasedOn, SysConfigManager.cloud);
        OwnerKeysWrapper ownerKeys = sysConfigManager.readOwnerKeys();

        if (SysConfigManager.compare(sysConfigModel, AllProducts)) {
            warehouseList = new WarehouseProductQtyViewManager(getActivity()).getItems(WarehouseProductQtyViewManager.search(null, null));
        } else {
            warehouseList = new WarehouseProductQtyViewManager(getActivity()).getItems(WarehouseProductQtyViewManager.search(null, true));
        }

        TourManager tourManager = new TourManager(getActivity());
        TourModel tourModel = tourManager.loadTour();


        PrintHelper.getInstance().addParagraph(PrintSize.large, getActivity().getString(R.string.print_warehouse_report), Paint.Align.CENTER);
        PrintHelper.getInstance().feedLine(PrintSize.small);

        BoxColumn headerInfoClm = new BoxColumn();
        String date = DateHelper.toString(new Date(), DateFormat.Date, VasHelperMethods.getSysConfigLocale(getActivity()));
        addRowItem(headerInfoClm, R.string.date, date);

        if (tourModel != null) {
            if (tourModel.AgentName != null) {
                addRowItem(headerInfoClm, R.string.print_dealername, tourModel.AgentName);
            }
            if (tourModel.DriverName != null) {
                addRowItem(headerInfoClm, R.string.print_driver, tourModel.DriverName);
            }
            if (tourModel.StockName != null) {
                addRowItem(headerInfoClm, R.string.print_stock_name, tourModel.StockName);
            }
            if (tourModel.VehicleName != null) {
                addRowItem(headerInfoClm, R.string.print_vehicle_Name, tourModel.VehicleName);
            }
        }
        PrintHelper.getInstance().addBox(new Box().addColumn(headerInfoClm));
        PrintHelper.getInstance().feedLine(PrintSize.larger);

        if (warehouseList.size() > 0) {
            if (ownerKeys.DataOwnerKey.equalsIgnoreCase("687109bf-e8b7-4749-929e-d1b03bdaa7d6")) {
                PrintHelper.getInstance().addBox(new Box().addColumns(
                        new BoxColumn(5).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.product_name)).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE),
                        new BoxColumn(1).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.product_code)).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE)
                ));
            } else {
                PrintHelper.getInstance().addBox(new Box().addColumns(
                        new BoxColumn(2).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.stock_level)).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE),
                        new BoxColumn(3).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.product_name)).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE),
                        new BoxColumn(1).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.product_code)).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE)
                ));
            }
            List<BoxRow> rows = new ArrayList<>();
            for (int i = 0; i < warehouseList.size(); i++) {
                if (warehouseList.get(i).TotalQty == null) {
                    warehouseList.get(i).TotalQty = BigDecimal.ZERO;
                }
                if (warehouseList.get(i).RenewQty == null) {
                    warehouseList.get(i).RenewQty = BigDecimal.ZERO;
                }
                if (warehouseList.get(i).OnHandQty == null) {
                    warehouseList.get(i).OnHandQty = BigDecimal.ZERO;
                }
                if (warehouseList.get(i).TotalReturnQty == null) {
                    warehouseList.get(i).TotalReturnQty = BigDecimal.ZERO;
                }

                String initialQty = VasHelperMethods.chopTotalQtyToString(warehouseList.get(i).OnHandQty, warehouseList.get(i).UnitName, warehouseList.get(i).ConvertFactor, null, null);
                String remain = VasHelperMethods.chopTotalQtyToString(warehouseList.get(i).OnHandQty.subtract(warehouseList.get(i).TotalQty), warehouseList.get(i).UnitName, warehouseList.get(i).ConvertFactor, null, null);

                BoxRow row = new BoxRow();
                if (ownerKeys.DataOwnerKey.equalsIgnoreCase("687109bf-e8b7-4749-929e-d1b03bdaa7d6")) {
                    row.addColumn(new BoxColumn().setBorderWidth(0).addRows(
                            new BoxRow().setBorderWidth(0).addColumns(
                                    new BoxColumn(5).setText(warehouseList.get(i).ProductName).setTextAlign(Paint.Align.CENTER).setBorderWidth(0).setTextSize(PrintSize.smaller),
                                    new BoxColumn(1).setText(warehouseList.get(i).ProductCode).setTextAlign(Paint.Align.CENTER).setBorderWidth(0).setTextSize(PrintSize.smaller)
                            ),
                            new BoxRow().setBorderWidth(0).addColumns(
                                    new BoxColumn(1).setText(getActivity().getString(com.varanegar.hotsales.R.string.renew_qty_print) + ": " + "(" + HelperMethods.bigDecimalToString(warehouseList.get(i).RenewQty) + ")").setTextAlign(Paint.Align.RIGHT).setTextSize(PrintSize.smaller).setBorderWidth(0),
                                    new BoxColumn(1).setText(getActivity().getString(R.string.onhand_qty_init_summary) + ": " + "(" + initialQty + ")").setTextAlign(Paint.Align.RIGHT).setTextSize(PrintSize.smaller).setBorderWidth(0),
                                    new BoxColumn(1).setText(getActivity().getString(R.string.stock_level) + ": " + "(" + remain + ")").setTextAlign(Paint.Align.RIGHT).setTextSize(PrintSize.smaller).setBorderWidth(0)
                                    ))
                    );
                } else {
                    row.addColumn(new BoxColumn().setBorderWidth(0).addRows(
                            new BoxRow().setBorderWidth(0).addColumns(
                                    new BoxColumn(2).setText(remain).setTextAlign(Paint.Align.CENTER).setBorderWidth(0).setTextSize(PrintSize.smaller),
                                    new BoxColumn(3).setText(warehouseList.get(i).ProductName).setTextAlign(Paint.Align.CENTER).setBorderWidth(0).setTextSize(PrintSize.smaller),
                                    new BoxColumn(1).setText(warehouseList.get(i).ProductCode).setTextAlign(Paint.Align.CENTER).setBorderWidth(0).setTextSize(PrintSize.smaller)
                            ),
                            new BoxRow().setBorderWidth(0).addColumns(
                                    new BoxColumn(1).setText(getActivity().getString(com.varanegar.hotsales.R.string.return_qty_print) + ": " + "(" + HelperMethods.bigDecimalToString(warehouseList.get(i).TotalReturnQty) + ")").setTextAlign(Paint.Align.RIGHT).setTextSize(PrintSize.smaller).setBorderWidth(0),
                                    new BoxColumn(1).setText(getActivity().getString(com.varanegar.hotsales.R.string.renew_qty_print) + ": " + "(" + HelperMethods.bigDecimalToString(warehouseList.get(i).RenewQty) + ")").setTextAlign(Paint.Align.RIGHT).setTextSize(PrintSize.smaller).setBorderWidth(0),
                                    new BoxColumn(1).setText(getActivity().getString(R.string.onhand_qty_init_summary) + ": " + "(" + initialQty + ")").setTextAlign(Paint.Align.RIGHT).setTextSize(PrintSize.smaller).setBorderWidth(0)
                            ))
                    );
                }
                rows.add(row);

            }
            PrintHelper.getInstance().addBox(new Box().addColumn(new BoxColumn().addRows(rows)));
        }
        PrintHelper.getInstance().feedLine(PrintSize.medium);
        BoxColumn printFooterColumn = new BoxColumn();
        addRowItem(printFooterColumn, R.string.print_warehouse_time, DateHelper.toString(new Date(), DateFormat.Complete, VasHelperMethods.getSysConfigLocale(getActivity())), false);
        PrintHelper.getInstance().addBox(new Box().addColumn(printFooterColumn));
        addFooter(null);

        PrintHelper.getInstance().print(new PrintCallback() {
            @Override
            public void done() {
                Timber.d("Print finished");
                PrintHelper.dispose();
                callback.done();
            }

            @Override
            public void failed() {
                Timber.d("Print failed");
                PrintHelper.dispose();
                callback.done();
            }
        });
    }
}
