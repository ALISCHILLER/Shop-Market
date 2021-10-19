package com.varanegar.vaslibrary.print.SellAndReceivePrint;

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
import com.varanegar.vaslibrary.manager.RequestReportViewManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.RequestReportView.RequestReportViewModel;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.print.BasePrintHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by g.aliakbar on 09/04/2018.
 */

public class SaleOrRequestPrintHelper extends BasePrintHelper {


    public SaleOrRequestPrintHelper(@NonNull AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public void print(@NonNull final PrintCallback callback) {

        List<RequestReportViewModel> requestReportList = new RequestReportViewManager(getActivity()).getItems(RequestReportViewManager.getAll());
        TourManager tourManager = new TourManager(getActivity());
        TourModel tourModel = tourManager.loadTour();


        PrintHelper.getInstance().feedLine(PrintSize.small);
        PrintHelper.getInstance().addParagraph(PrintSize.large, getActivity().getString(R.string.sales_report), Paint.Align.CENTER);
        PrintHelper.getInstance().feedLine(PrintSize.small);

        BoxColumn headerInfoClm = new BoxColumn();
        String date = DateHelper.toString(new Date(), DateFormat.Date, VasHelperMethods.getSysConfigLocale(getActivity()));
        addRowItem(headerInfoClm, R.string.date, date);
        if (tourModel != null) {
            if (tourModel.AgentName != null) {
                addRowItem(headerInfoClm, R.string.print_dealername, tourModel.AgentName);
            }
        }
        PrintHelper.getInstance().addBox(new Box().addColumn(headerInfoClm));
        PrintHelper.getInstance().feedLine(PrintSize.small);
        PrintHelper.getInstance().addBox(new Box().addColumns(
                new BoxColumn(3).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.payment_type)).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE).setTextSize(PrintSize.smaller),
                new BoxColumn(2).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.total_order_net_amount)).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE).setTextSize(PrintSize.smaller),
                new BoxColumn(2).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.customer_name)).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE).setTextSize(PrintSize.smaller),
                new BoxColumn(2).setBackgroundColor(Color.BLACK).setText(getActivity().getString(R.string.customer_code)).setTextAlign(Paint.Align.CENTER).setTextColor(Color.WHITE).setTextSize(PrintSize.smaller)
        ));


        List<BoxRow> rows = new ArrayList<>();
        Currency totalPrice = Currency.ZERO;
        for (int i = 0; i < requestReportList.size(); i++) {
            BoxRow row = new BoxRow();
            row.addColumns(
                    new BoxColumn(3).setText(requestReportList.get(i).PaymentTypeBaseName).setTextAlign(Paint.Align.CENTER),
                    new BoxColumn(2).setText(HelperMethods.currencyToString(requestReportList.get(i).TotalOrderNetAmount)).setTextAlign(Paint.Align.CENTER).setTextSize(PrintSize.smaller),
                    new BoxColumn(2).setText(requestReportList.get(i).CustomerName).setTextAlign(Paint.Align.CENTER),
                    new BoxColumn(2).setText(requestReportList.get(i).CustomerCode).setTextAlign(Paint.Align.CENTER)
            );
            rows.add(row);
            totalPrice = totalPrice.add(requestReportList.get(i).TotalOrderNetAmount == null ? Currency.ZERO : requestReportList.get(i).TotalOrderNetAmount);
        }
        PrintHelper.getInstance().addBox(new Box().addColumn(new BoxColumn().addRows(rows)));
        PrintHelper.getInstance().feedLine(PrintSize.small);
        PrintHelper.getInstance().addParagraph(getActivity().getString(R.string.total) + " : " + HelperMethods.currencyToString(totalPrice), Paint.Align.CENTER);

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
