package com.varanegar.vaslibrary.print.SentTourInfoPrint;

import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.printlib.PrintHelper;
import com.varanegar.printlib.PrintSize;
import com.varanegar.printlib.box.Box;
import com.varanegar.printlib.box.BoxColumn;
import com.varanegar.printlib.box.BoxRow;
import com.varanegar.printlib.driver.PrintCallback;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.print.BasePrintHelper;

import java.text.DecimalFormat;

import timber.log.Timber;

/**
 * Created by g.aliakbar on 17/04/2018.
 */

public class SentTourInfoPrintHelper extends BasePrintHelper {

    public SentTourInfoPrintHelper(@NonNull AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public void print(@NonNull final PrintCallback callback) {
        TourInfo tourInfoForPrint = new TourManager(getActivity()).loadTourFromFile();

        BoxRow row = new BoxRow();
        BoxColumn infoClm = new BoxColumn();
        if (tourInfoForPrint != null) {
            if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                addRowItem(infoClm, R.string.tour_no, String.valueOf(tourInfoForPrint.TourNo));
                addRowItem(infoClm, R.string.today_customers_count, String.valueOf(tourInfoForPrint.DayCustomersCount));
                addRowItem(infoClm, R.string.total_customers_count, String.valueOf(tourInfoForPrint.TotalCustomersCount));
                addRowItem(infoClm, R.string.today_visited, String.valueOf(tourInfoForPrint.DayVisitedCount));
                addRowItem(infoClm, R.string.total_visited, String.valueOf(tourInfoForPrint.TotalVisitedCount));
                addRowItem(infoClm, R.string.today_ordered, String.valueOf(tourInfoForPrint.DayOrderedCount));
                addRowItem(infoClm, R.string.total_ordered, String.valueOf(tourInfoForPrint.TotalOrderedCount));
                addRowItem(infoClm, R.string.today_lack_of_order, String.valueOf(tourInfoForPrint.DayLackOfOrderCount));
                addRowItem(infoClm, R.string.total_lack_of_order, String.valueOf(tourInfoForPrint.TotalLackOfOrderCount));
                addRowItem(infoClm, R.string.today_lack_of_visit, String.valueOf(tourInfoForPrint.DayLackOfVisitCount));
                addRowItem(infoClm, R.string.visit_to_customer, new DecimalFormat("#.00").format(tourInfoForPrint.DayVisitRatio) + " %");
                addRowItem(infoClm, R.string.today_sum_of_ordered, HelperMethods.currencyToString(tourInfoForPrint.DayOrderSum));
                addRowItem(infoClm, R.string.total_sum_of_ordered, HelperMethods.currencyToString(tourInfoForPrint.TotalOrderSum));
                addRowItem(infoClm, R.string.total_visit_time, String.valueOf(tourInfoForPrint.VisitTime));
                addRowItem(infoClm, R.string.total_tour_time, String.valueOf(tourInfoForPrint.TourTime));
                if (tourInfoForPrint.Spd) {
                    addRowItem(infoClm, R.string.spd, getActivity().getString(R.string.check_sign));
                } else {
                    addRowItem(infoClm, R.string.multiplication_sign, getActivity().getString(R.string.spd));
                }
            } else {
                addRowItem(infoClm, R.string.tour_no, String.valueOf(tourInfoForPrint.TourNo));
                addRowItem(infoClm, R.string.dist_no_label, String.valueOf(tourInfoForPrint.DistNo));
                addRowItem(infoClm, R.string.delivered, String.valueOf(tourInfoForPrint.DeliveriesCount));
                addRowItem(infoClm, R.string.delivered_partially, String.valueOf(tourInfoForPrint.PartialDeliveriesCount));
                addRowItem(infoClm, R.string.lack_of_delivery_count, String.valueOf(tourInfoForPrint.LackOfDeliveriesCount));
                addRowItem(infoClm, R.string.return_count, String.valueOf(tourInfoForPrint.ReturnsCount));
                addRowItem(infoClm, R.string.total_customers_count, String.valueOf(tourInfoForPrint.TotalCustomersCount));
                addRowItem(infoClm, R.string.total_sum_of_ordered, String.valueOf(tourInfoForPrint.TotalOrderSum));
                addRowItem(infoClm, R.string.total_visited, String.valueOf(tourInfoForPrint.TotalVisitedCount));
                addRowItem(infoClm, R.string.total_visit_time, String.valueOf(tourInfoForPrint.VisitTime));
                addRowItem(infoClm, R.string.total_tour_time, String.valueOf(tourInfoForPrint.TourTime));
            }
            row.addColumn(infoClm);
        }

        PrintHelper.getInstance().feedLine(PrintSize.small);
        PrintHelper.getInstance().addParagraph(PrintSize.large, getActivity().getString(R.string.print_sent_tour_info), Paint.Align.CENTER);
        PrintHelper.getInstance().feedLine(PrintSize.small);
        PrintHelper.getInstance().addBox(new Box().addColumn(infoClm));
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
