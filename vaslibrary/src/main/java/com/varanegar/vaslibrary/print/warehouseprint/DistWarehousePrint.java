package com.varanegar.vaslibrary.print.warehouseprint;

import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.varanegar.vaslibrary.manager.DistWarehouseProductQtyViewManager;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.OwnerKeysWrapper;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.DistWarehouseProductQtyViewModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.print.BasePrintHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 3/30/2020.
 */

public class DistWarehousePrint extends BasePrintHelper {

    private AppCompatActivity activity;
    private boolean inStock;

    public DistWarehousePrint(@NonNull AppCompatActivity activity, @Nullable Boolean inStock) {
        super(activity);
        this.activity = activity;
        this.inStock = inStock;

    }

    @Override
    public void print(@NonNull final PrintCallback callback) {
        SysConfigManager sysConfigManager = new SysConfigManager(getActivity());
        OwnerKeysWrapper ownerKeys = sysConfigManager.readOwnerKeys();

        DistWarehouseProductQtyViewManager distWarehouseProductQtyViewManager = new DistWarehouseProductQtyViewManager(activity);
        List<DistWarehouseProductQtyViewModel> list = distWarehouseProductQtyViewManager.getItems(DistWarehouseProductQtyViewManager.search(null, inStock, true));
        UserModel userModel = UserManager.readFromFile(getActivity());

        PrintHelper.getInstance().addParagraph(PrintSize.large, getActivity().getString(R.string.dist_warehouse_report), Paint.Align.CENTER);
        PrintHelper.getInstance().feedLine(PrintSize.small);

        BoxColumn headerColumn = new BoxColumn();
        addRowItem(headerColumn, R.string.date, DateHelper.toString(new Date(), DateFormat.Complete, VasHelperMethods.getSysConfigLocale(getActivity())), true);
        addRowItem(headerColumn, R.string.distributer_name, userModel.UserName);
        PrintHelper.getInstance().addBox(new Box().addColumn(headerColumn));
        PrintHelper.getInstance().feedLine(PrintSize.small);

        if (list.size() > 0) {
            if (ownerKeys.DataOwnerKey.equalsIgnoreCase("bb85e291-6049-454a-a7af-ee565d7673c5")) {
                PrintHelper.getInstance().addBox(new Box().addColumns(
                        new BoxColumn(4).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.product_name)).setTextSize(PrintSize.small).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE),
                        new BoxColumn(1).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.product_code)).setTextSize(PrintSize.small).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE)
                ));
                List<BoxRow> distWareHouseRows = new ArrayList<>();
                for (DistWarehouseProductQtyViewModel line : list) {
                    BoxRow row = new BoxRow();
                    row.addColumn(new BoxColumn().setBorderWidth(0).addRows(
                            new BoxRow().setBorderWidth(0).addColumns(
                                    new BoxColumn(4).setText(line.ProductName).setTextAlign(Paint.Align.CENTER).setTextSize(line.ProductName.length() > 35 ? PrintSize.smaller : PrintSize.small).setBorderWidth(0),
                                    new BoxColumn(1).setText(line.ProductCode).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.smaller).setBorderWidth(0)
                            ),
                            new BoxRow().setBorderWidth(0).addColumns(
                                    new BoxColumn().setBorderWidth(0).addRows(
                                            new BoxRow().setBorderWidth(0).addColumns(
                                                    new BoxColumn(1).setText("(" + line.WasteReturnQtyView + ")").setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.smaller).setBorderWidth(0),
                                                    new BoxColumn(1).setText(getActivity().getString(R.string.waste_returned_summary)).setTextAlign(Paint.Align.RIGHT).setTextSize(PrintSize.xSmall).setBorderWidth(0),
                                                    new BoxColumn(1).setText("(" + line.WellReturnQtyView + ")").setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.smaller).setBorderWidth(0),
                                                    new BoxColumn(1).setText(getActivity().getString(R.string.well_returned_summary)).setTextAlign(Paint.Align.RIGHT).setTextSize(PrintSize.smaller).setBorderWidth(0)
                                            ))
                            ),
                            new BoxRow().setBorderWidth(0).addColumns(
                                    new BoxColumn().setBorderWidth(0).addRows(
                                            new BoxRow().setBorderWidth(0).addColumns(
                                                    new BoxColumn(1).setText("(" + line.TotalReturnedQtyView + ")").setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.smaller).setBorderWidth(0),
                                                    new BoxColumn(1).setText(getActivity().getString(R.string.retuned_from_dist_summary)).setTextAlign(Paint.Align.RIGHT).setTextSize(PrintSize.smaller).setBorderWidth(0),
                                                    new BoxColumn(1).setText("(" + line.OnHandQtyView + ")").setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.smaller).setBorderWidth(0),
                                                    new BoxColumn(1).setText(getActivity().getString(R.string.onhand_qty_init_summary)).setTextAlign(Paint.Align.RIGHT).setTextSize(PrintSize.smaller).setBorderWidth(0)
                                            ))
                            ))

                    );
                    distWareHouseRows.add(row);
                }
                PrintHelper.getInstance().addBox(new Box().addColumn(new BoxColumn().addRows(distWareHouseRows)));
            } else {
                PrintHelper.getInstance().addBox(new Box().addColumns(
                        new BoxColumn(2).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.waste_returned)).setTextSize(PrintSize.small).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE),
                        new BoxColumn(2).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.well_returned)).setTextSize(PrintSize.small).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE),
                        new BoxColumn(4).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.product_name)).setTextSize(PrintSize.small).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE),
                        new BoxColumn(2).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.product_code)).setTextSize(PrintSize.small).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE)
                ));
                List<BoxRow> distWareHouseRows = new ArrayList<>();
                for (DistWarehouseProductQtyViewModel line : list) {
                    BoxRow row = new BoxRow();
                    row.addColumn(new BoxColumn().setBorderWidth(0).addRows(
                            new BoxRow().setBorderWidth(0).addColumns(
                                    new BoxColumn(2).setText(line.WasteReturnQtyView).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.smaller).setBorderWidth(0),
                                    new BoxColumn(2).setText(line.WellReturnQtyView).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.smaller).setBorderWidth(0),
                                    new BoxColumn(4).setText(line.ProductName).setTextAlign(Paint.Align.CENTER).setTextSize(line.ProductName.length() > 35 ? PrintSize.xSmall : PrintSize.smaller).setBorderWidth(0),
                                    new BoxColumn(2).setText(line.ProductCode).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.smaller).setBorderWidth(0)
                            ),
                            new BoxRow().setBorderWidth(0).addColumns(
                                    new BoxColumn().setBorderWidth(0).addRows(
                                            new BoxRow().setBorderWidth(0).addColumns(
                                                    new BoxColumn(1).setText("(" + line.TotalReturnedQtyView + ")").setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.smaller).setBorderWidth(0),
                                                    new BoxColumn(1).setText(getActivity().getString(R.string.retuned_from_dist_summary)).setTextAlign(Paint.Align.RIGHT).setTextSize(PrintSize.smaller).setBorderWidth(0),
                                                    new BoxColumn(1).setText("(" + line.OnHandQtyView + ")").setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.smaller).setBorderWidth(0),
                                                    new BoxColumn(1).setText(getActivity().getString(R.string.onhand_qty_init_summary)).setTextAlign(Paint.Align.RIGHT).setTextSize(PrintSize.smaller).setBorderWidth(0)
                                            ))
                            ))

                    );
                    distWareHouseRows.add(row);
                }
                PrintHelper.getInstance().addBox(new Box().addColumn(new BoxColumn().addRows(distWareHouseRows)));
            }
        }
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
