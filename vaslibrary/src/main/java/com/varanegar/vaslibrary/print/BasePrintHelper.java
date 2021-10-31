package com.varanegar.vaslibrary.print;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.view.View;

import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.printlib.PrintHelper;
import com.varanegar.printlib.PrintSize;
import com.varanegar.printlib.box.BoxColumn;
import com.varanegar.printlib.box.BoxRow;
import com.varanegar.printlib.driver.ConnectionCallback;
import com.varanegar.printlib.driver.PrintCallback;
import com.varanegar.printlib.driver.PrinterDriver;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.sysconfigmanager.OwnerKeysWrapper;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;

import java.util.Locale;

import timber.log.Timber;

/**
 * Created by g.aliakbar on 04/04/2018.
 */

public abstract class BasePrintHelper {
    SysConfigManager sysConfigManager = new SysConfigManager(getActivity());
    OwnerKeysWrapper ownerKeys = sysConfigManager.readOwnerKeys();
    public AppCompatActivity getActivity() {
        return activity;
    }

    private AppCompatActivity activity;
    private ProgressDialog printerProgressDialog;

    public BasePrintHelper(@NonNull AppCompatActivity activity) {
        this.activity = activity;
    }


    IPrintCallBack printCallBack;

    public interface IPrintCallBack {
        void run();
    }

    protected void runPrintCallBack() {
        if (this.printCallBack != null)
            this.printCallBack.run();
    }

    public void start(@Nullable IPrintCallBack printCallBack) {
        this.printCallBack = printCallBack;
        showProgressDialog(R.string.please_wait, R.string.connecting_to_the_printer);
        final PrinterManager printerManager = new PrinterManager(activity);
        printerManager.getPrinterDriver(activity, new PrinterDriverCallBack());
    }

    private class PrinterDriverCallBack implements PrinterManager.PrintDriverCallBack {
        PrinterManager printerManager = new PrinterManager(activity);

        @Override
        public void onSuccess(final PrinterDriver driver) {
            driver.connect(new ConnectionCallback() {
                @Override
                public void connected() {
                    if (activity != null && !activity.isFinishing()) {
                        dismissPrinterProgressDialog();
                        Timber.d("connected to driver " + driver.name());
                        PrintHelper.init(driver);
                        Typeface customFont;
                        if (Build.MODEL.equalsIgnoreCase("Sepehr A1")||ownerKeys.isMihanKish())
                            customFont = Typeface.createFromAsset(activity.getAssets(), "fonts/Iran Sans Bold.ttf");
                        else
                            customFont = Typeface.createFromAsset(activity.getAssets(), "fonts/anatoli.ttf");
                        PrintHelper.getInstance().setDefaultTypeFace(customFont);
                        PrintHelper.getInstance().setMargin(PrintSize.xxSmall);
                        showProgressDialog(R.string.please_wait, R.string.printing);
                        print(new PrintCallback() {
                            @Override
                            public void done() {
                                runPrintCallBack();
                                dismissPrinterProgressDialog();
                            }

                            @Override
                            public void failed() {
                                runPrintCallBack();
                                dismissPrinterProgressDialog();
                                driver.disconnect();
                            }
                        });
                    }
                }

                @Override
                public void failed() {
                    runPrintCallBack();
                    if (activity != null && !activity.isFinishing()) {
                        dismissPrinterProgressDialog();
                        Timber.d("Error connecting to " + driver.name());
                        CuteMessageDialog dialog = new CuteMessageDialog(activity);
                        dialog.setIcon(Icon.Error);
                        dialog.setTitle(R.string.error_connecting_to_printer);
                        dialog.setMessage(activity.getString(R.string.error_connecting_to) + " " + driver.name());
                        dialog.setPositiveButton(R.string.close, null);
                        dialog.setNeutralButton(R.string.select_printer, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                printerManager.showSelectionDialog(activity, PrinterDriverCallBack.this);
                            }
                        });
                        dialog.show();
                    }
                }

                @Override
                public void connecting() {

                }
            });
        }

        @Override
        public void onFailure(String error) {
            runPrintCallBack();
            if (activity != null && !activity.isFinishing()) {
                dismissPrinterProgressDialog();
                Timber.e(error);
                CuteMessageDialog dialog = new CuteMessageDialog(activity);
                dialog.setIcon(Icon.Error);
                dialog.setTitle(R.string.error_connecting_to_printer);
                dialog.setMessage(error);
                dialog.setPositiveButton(R.string.close, null);
                dialog.setNeutralButton(R.string.select_printer, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        printerManager.showSelectionDialog(activity, PrinterDriverCallBack.this);
                    }
                });
                dialog.show();
            }
        }

        @Override
        public void onCancel() {
            runPrintCallBack();
            if (activity != null && !activity.isFinishing())
                dismissPrinterProgressDialog();
        }
    }

    private void showProgressDialog(@StringRes int title, @StringRes int message) {
        printerProgressDialog = new ProgressDialog(activity);
        printerProgressDialog.setTitle(title);
        printerProgressDialog.setMessage(activity.getString(message));
        printerProgressDialog.setCancelable(false);
        printerProgressDialog.show();
    }

    private void dismissPrinterProgressDialog() {
        if (printerProgressDialog != null && printerProgressDialog.isShowing()) {
            try {
                printerProgressDialog.dismiss();
            } catch (Exception ignored) {

            }
        }
    }

    public abstract void print(@NonNull PrintCallback callback);

    protected void addRowItem(@NonNull BoxColumn clm, @StringRes int title, @NonNull String value) {
        addRowItem(clm, title, value, false);
    }

    protected void addRowItem(@NonNull BoxColumn clm, @StringRes int title, @NonNull String value, boolean border) {
        addRowItem(clm, title, value, border, null);
    }

    protected void addRowItem(@NonNull BoxColumn clm, @StringRes int title, @NonNull String value, boolean border, @Nullable PrintSize textSize) {
        PrintSize printSize = textSize == null ? PrintSize.smaller : textSize;
        BoxColumn valueColumn = new BoxColumn(1).setBackgroundColor(Color.WHITE).setText(value).setTextAlign(Paint.Align.CENTER).setMargin(PrintSize.xxSmall).setTextSize(printSize);
        BoxColumn titleColumn = new BoxColumn(1).setBackgroundColor(Color.WHITE).setText(activity.getString(title)).setTextAlign(Paint.Align.CENTER).setMargin(PrintSize.xxSmall).setTextSize(printSize);
        if (!border) {
            valueColumn.setBorderWidth(0);
            titleColumn.setBorderWidth(0);
        }
        if (Locale.getDefault().getLanguage().equals("fa"))
            clm.addRow(new BoxRow().addColumns(valueColumn, titleColumn));
        else
            clm.addRow(new BoxRow().addColumns(titleColumn, valueColumn));
    }

    protected void addFooter(@Nullable String message) {
        if (message != null) {
            PrintHelper.getInstance().feedLine(PrintSize.xSmall);
            PrintHelper.getInstance().addParagraph(PrintSize.small, message, Paint.Align.CENTER);
            PrintHelper.getInstance().feedLine(PrintSize.xxxSmall);
        }

        PrintHelper.getInstance().addHorizontalLine();
        PrintHelper.getInstance().feedLine(PrintSize.medium);
        PrintHelper.getInstance().addParagraph(PrintSize.small, "Powered By : www.varanegar.com ", Paint.Align.CENTER);
        PrintHelper.getInstance().feedLine(PrintSize.xLarge);
    }
}
