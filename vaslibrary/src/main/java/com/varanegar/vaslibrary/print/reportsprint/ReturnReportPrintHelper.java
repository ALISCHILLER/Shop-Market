package com.varanegar.vaslibrary.print.reportsprint;

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
import com.varanegar.vaslibrary.manager.ReturnReportViewManager;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.model.returnreportview.ReturnReportViewModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.print.BasePrintHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 11/5/2018.
 */

public class ReturnReportPrintHelper extends BasePrintHelper {
    public ReturnReportPrintHelper(@NonNull AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public void print(@NonNull final PrintCallback callback) {
        ReturnReportViewManager returnReportViewManager = new ReturnReportViewManager(getActivity());
        List<ReturnReportViewModel> returnReportViewModels = returnReportViewManager.getItems(ReturnReportViewManager.getAll());

        PrintHelper.getInstance().addParagraph(PrintSize.large, getActivity().getString(R.string.return_report), Paint.Align.CENTER);
        PrintHelper.getInstance().feedLine(PrintSize.small);

        UserModel userModel = UserManager.readFromFile(getActivity());

        BoxColumn headerColumn = new BoxColumn();
        addRowItem(headerColumn, R.string.date, DateHelper.toString(new Date(), DateFormat.Complete, VasHelperMethods.getSysConfigLocale(getActivity())), true);
        addRowItem(headerColumn, R.string.print_dealername, userModel.UserName);
        PrintHelper.getInstance().addBox(new Box().addColumn(headerColumn));
        PrintHelper.getInstance().feedLine(PrintSize.small);

        if (returnReportViewModels.size() > 0) {
            PrintHelper.getInstance().addBox(new Box().addColumns(
                    new BoxColumn(2).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.price)).setTextSize(PrintSize.small).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE),
                    new BoxColumn(2).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.count)).setTextSize(PrintSize.small).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE),
                    new BoxColumn(3).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.product_name)).setTextSize(PrintSize.small).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE),
                    new BoxColumn(1).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.product_code)).setTextSize(PrintSize.small).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE)
            ));
            List<BoxRow> returnLineRows = new ArrayList<>();
            for (ReturnReportViewModel line : returnReportViewModels) {
                boolean hasQty = line.TotalReturnQty != null && line.TotalReturnQty.compareTo(BigDecimal.ZERO) > 0;
                if (hasQty) {
                    BoxRow row = new BoxRow();
                    row.addColumns(
                            new BoxColumn(2).setText(HelperMethods.currencyToString(line.TotalRequestAmount)).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.smaller).setMargin(PrintSize.xxxSmall),
                            new BoxColumn(2).setText(HelperMethods.bigDecimalToString(line.TotalReturnQty)).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.smaller).setMargin(PrintSize.xxxSmall)
                    );
                    row.addColumn(new BoxColumn(3).setBorderWidth(0).addRows(
                            new BoxRow().setBorderWidth(0).addColumn(
                                    new BoxColumn().setText(line.ProductName).setTextAlign(Paint.Align.RIGHT).setTextSize(PrintSize.smaller)
                            ),
                            new BoxRow().setBorderWidth(0).addColumns(
                                    new BoxColumn().setBorderWidth(0).addRow(
                                            new BoxRow().setBorderWidth(0).addColumns(
                                                    new BoxColumn(2).setText(HelperMethods.currencyToString(line.RequestUnitPrice)).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.smaller).setBorderWidth(0),
                                                    new BoxColumn(1).setText(getActivity().getString(R.string.unit_price)).setTextAlign(Paint.Align.RIGHT).setTextSize(PrintSize.smaller).setBorderWidth(0)
                                            ))
                            ))
                    );
                    row.addColumn(new BoxColumn(1).setText(line.ProductCode).setTextSize(PrintSize.xSmall).setMargin(PrintSize.xxxSmall).setTextAlign(Paint.Align.CENTER));
                    returnLineRows.add(row);
                }
            }
            PrintHelper.getInstance().addBox(new Box().addColumn(new BoxColumn().addRows(returnLineRows)));
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
