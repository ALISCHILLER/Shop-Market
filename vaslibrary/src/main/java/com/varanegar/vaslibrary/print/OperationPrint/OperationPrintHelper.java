package com.varanegar.vaslibrary.print.OperationPrint;

import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.operationReport.OperationReportViewManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.operationReport.OperationReportViewModel;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.print.BasePrintHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by g.aliakbar on 01/05/2018.
 */

public class OperationPrintHelper extends BasePrintHelper {

    public OperationPrintHelper(@NonNull AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public void print(@NonNull final PrintCallback callback) {
        List<OperationReportViewModel> operationReportList = new OperationReportViewManager(getActivity()).getItems(OperationReportViewManager.getAll());
        TourManager tourManager = new TourManager(getActivity());
        TourModel tourModel = tourManager.loadTour();

        BoxRow headerInfo = new BoxRow();
        BoxColumn headerInfoClm = new BoxColumn();

        String date = DateHelper.toString(new Date(), DateFormat.Date, VasHelperMethods.getSysConfigLocale(getActivity()));
        addRowItem(headerInfoClm, R.string.date, date);

        if (tourModel != null) {
            if (tourModel.AgentName != null) {
                addRowItem(headerInfoClm, R.string.print_dealername, tourModel.AgentName);
            }
        }
        headerInfo.addColumn(headerInfoClm);


        BoxRow headerTitles = new BoxRow();
        headerTitles.addColumn(
                new BoxColumn().addRow(
                        new BoxRow().addColumns(
                                new BoxColumn(1).setBackgroundColor(Color.BLACK).setText(getActivity().getString(com.varanegar.vaslibrary.R.string.payment)).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE),
                                new BoxColumn(1).setBackgroundColor(Color.BLACK).setText(getActivity().getString(com.varanegar.vaslibrary.R.string.returns)).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE),
                                new BoxColumn(1).setBackgroundColor(Color.BLACK).setText(getActivity().getString(com.varanegar.vaslibrary.R.string.total_order_net_amount)).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE),
                                new BoxColumn(1).setBackgroundColor(Color.BLACK).setText(getActivity().getString(com.varanegar.vaslibrary.R.string.print_storename)).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE)
                        )
                )
        );
        List<BoxRow> rows = new ArrayList<>();
        Currency totalAllPaid = Currency.ZERO;
        Currency totalAllReturn = Currency.ZERO;
        Currency totalAllNet = Currency.ZERO;

        for (int i = 0; i < operationReportList.size(); i++) {

            BoxRow row = new BoxRow();
            row.addColumn(new BoxColumn().addRow(
                    new BoxRow().addColumns(
                            new BoxColumn(1).setText(HelperMethods.currencyToString(operationReportList.get(i).TotalPaidAmount)).setTextAlign(Paint.Align.CENTER),
                            new BoxColumn(1).setText(HelperMethods.currencyToString(operationReportList.get(i).ReturnDiscountAmount)).setTextAlign(Paint.Align.CENTER),
                            new BoxColumn(1).setText(HelperMethods.currencyToString(operationReportList.get(i).TotalNetAmount)).setTextAlign(Paint.Align.CENTER),
                            new BoxColumn(1).setText(operationReportList.get(i).StoreName).setTextAlign(Paint.Align.CENTER)
                    )
                    )
            );
            rows.add(row);
            totalAllPaid = totalAllPaid.add(operationReportList.get(i).TotalPaidAmount == null ? Currency.ZERO : operationReportList.get(i).TotalPaidAmount);
            totalAllReturn = totalAllReturn.add(operationReportList.get(i).ReturnDiscountAmount == null ? Currency.ZERO : operationReportList.get(i).ReturnDiscountAmount);
            totalAllNet = totalAllNet.add(operationReportList.get(i).TotalNetAmount == null ? Currency.ZERO : operationReportList.get(i).TotalNetAmount);
        }

        BoxRow footerInfo = new BoxRow();
        BoxColumn footerInfoClm = new BoxColumn();
        addRowItem(footerInfoClm, R.string.total, totalAllPaid.toString());
        addRowItem(footerInfoClm, R.string.return_sum, totalAllReturn.toString());
        addRowItem(footerInfoClm, R.string.peyment_sum, totalAllNet.toString());
        footerInfo.addColumn(footerInfoClm);

        PrintHelper.getInstance().feedLine(PrintSize.small);
        PrintHelper.getInstance().addParagraph(PrintSize.large, getActivity().getString(R.string.operation_report), Paint.Align.CENTER);
        PrintHelper.getInstance().feedLine(PrintSize.small);

        Box headerBox = new Box();
        headerBox.addColumn(new BoxColumn().addRows(headerInfo));
        PrintHelper.getInstance().addBox(headerBox);

        Box titleBox = new Box();
        titleBox.addColumn(new BoxColumn().addRows(headerTitles));
        PrintHelper.getInstance().addBox(titleBox);


        Box box = new Box();
        box.addColumn(new BoxColumn().addRows(rows));
        PrintHelper.getInstance().addBox(box);

        Box footerBox = new Box();
        footerBox.addColumn(new BoxColumn().addRows(footerInfo));
        PrintHelper.getInstance().addBox(footerBox);

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
