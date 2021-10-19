package com.varanegar.vaslibrary.action;

import android.graphics.Typeface;
import android.os.Build;

import androidx.annotation.Nullable;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.printlib.BitmapPrinterDriver;
import com.varanegar.printlib.PrintHelper;
import com.varanegar.printlib.PrintSize;
import com.varanegar.printlib.driver.ConnectionCallback;
import com.varanegar.printlib.driver.PrintCallback;
import com.varanegar.printlib.layout.RootLayout;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.customercall.CustomerPrintCountManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.print.BasePrintHelper;
import com.varanegar.vaslibrary.print.InvoicePrint.InvoicePrintHelper;
import com.varanegar.vaslibrary.print.datahelper.OrderPrintType;
import com.varanegar.vaslibrary.print.datahelper.PrintDataHelper;

import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 5/28/2018.
 */

public class PrintAction extends CheckPathAction {
    public PrintAction(MainVaranegarActivity activity, ActionsAdapter adapter, UUID selectedId) {
        super(activity, adapter, selectedId);
        icon = R.drawable.ic_print_black_24dp;
    }

    @Nullable
    @Override
    public String isEnabled() {
        String error = super.isEnabled();
        if (error != null)
            return error;

        if (getCallManager().isDataSent(getCalls(),true))
            return getActivity().getString(R.string.customer_operation_is_sent_already);

//        NGT-2442
//        if (!getCallManager().isConfirmed(getCalls()))
//            return getActivity().getString(R.string.you_have_to_confirm_operation);

        if (!isFactor() && VaranegarApplication.is(VaranegarApplication.AppId.HotSales) && !SysConfigManager.getBooleanValue(getCloudConfig(ConfigKey.PrintInvoice), false))
            return getActivity().getString(R.string.pre_factor_printing_is_not_allowed);

        int printCounts = SysConfigManager.getIntValue(getCloudConfig(ConfigKey.PrintCounts), 0);
        int printCount = new CustomerPrintCountManager(getActivity()).getCount(getSelectedId());
        if (printCount < printCounts)
            return null;
        else
            return getActivity().getString(R.string.print_counts);

    }

    @Override
    public String getName() {

        if (isFactor()) {
            return getActivity().getString(R.string.invoice);
        } else {
            return getActivity().getString(R.string.proforma);
        }
    }

    @Override
    protected boolean isDone() {
        int printCount = new CustomerPrintCountManager(getActivity()).getCount(getSelectedId());
        return printCount > 0;
    }

    @Override
    public void run() {
        setRunning(true);
        if (getCallManager().isConfirmed(getCalls()) || canNotEditOperationAfterPrint()) {
            int printCounts = SysConfigManager.getIntValue(getCloudConfig(ConfigKey.PrintCounts), 0);
            int printCount = getPrintCounts();
            if (printCount < printCounts) {
                InvoicePrintHelper printInvoice = new InvoicePrintHelper(getActivity(), getSelectedId(), OrderPrintType.Invoice);
                printInvoice.start(new BasePrintHelper.IPrintCallBack() {
                    @Override
                    public void run() {
                        setRunning(false);
                        runActionCallBack();
                    }
                });
            } else {
                setRunning(false);
                runActionCallBack();
                CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                dialog.setIcon(Icon.Warning);
                dialog.setTitle(R.string.error);
                dialog.setMessage(R.string.print_counts);
                dialog.setPositiveButton(R.string.ok, null);
                dialog.show();
            }
        } else {
            InvoicePrintHelper printInvoice = new InvoicePrintHelper(getActivity(), getSelectedId(), OrderPrintType.Preview);
            printInvoice.start(new BasePrintHelper.IPrintCallBack() {
                @Override
                public void run() {
                    setRunning(false);
                    runActionCallBack();
                }
            });
        }
    }

    @Override
    public void runOnLongClick() {
        setRunning(true);
        final BitmapPrinterDriver bitmapPrinterDriver = new BitmapPrinterDriver(getActivity());
        bitmapPrinterDriver.connect(new ConnectionCallback() {
            @Override
            public void connected() {
                SysConfigModel printFormat = getCloudConfig(ConfigKey.CustomizePrint);
                if (printFormat != null && printFormat.Value != null && !printFormat.Value.isEmpty()) {
                    try {
                        RootLayout rootLayout = new RootLayout();
                        Typeface customFont;
                        if (Build.MODEL.equalsIgnoreCase("Sepehr A1"))
                            customFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Iran Sans Bold.ttf");
                        else
                            customFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/anatoli.ttf");
                        PrintHelper.init(bitmapPrinterDriver);
                        PrintHelper.getInstance().setDefaultTypeFace(customFont);
                        PrintHelper.getInstance().setMargin(PrintSize.xxSmall);
                        rootLayout.deserialize(printFormat.Value, new RootLayout.IPopulatePrintData() {
                            @Override
                            public void run(List<String> list) {
                                PrintDataHelper printDataHelper = new PrintDataHelper(getActivity());
                                printDataHelper.addLogo();
                                printDataHelper.addCustomerInfo(getSelectedId());
                                printDataHelper.addOrdersInfo(getSelectedId());
                                printDataHelper.addTitle(OrderPrintType.Preview);
                                printDataHelper.addTourInfo();
                                printDataHelper.addDealerInfo();
                                printDataHelper.addReturnsInfo(getSelectedId());
                                printDataHelper.addPaymentInfo(getSelectedId());
                            }
                        });
                        // send to printer
                        PrintHelper.getInstance().print(new PrintCallback() {
                            @Override
                            public void done() {
                                setRunning(false);

                            }

                            @Override
                            public void failed() {
                                setRunning(false);
                                Timber.d("Print failed");
                                PrintHelper.dispose();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        setRunning(false);
                    }
                }

            }

            @Override
            public void failed() {
                setRunning(false);
            }

            @Override
            public void connecting() {

            }
        });
    }

    @Nullable
    @Override
    public UUID getTaskUniqueId() {
        return null;
    }

    @Nullable
    @Override
    protected String isMandatory() {
        return null;
    }

    private Boolean isFactor() {
        return !VaranegarApplication.is(VaranegarApplication.AppId.PreSales) && (getCallManager().isConfirmed(getCalls()) || canNotEditOperationAfterPrint());
    }
}
