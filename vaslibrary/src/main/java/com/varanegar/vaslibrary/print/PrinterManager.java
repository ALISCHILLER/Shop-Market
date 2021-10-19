package com.varanegar.vaslibrary.print;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.printlib.bixolon.SPP_R200II;
import com.varanegar.printlib.bixolon.SPP_R200III;
import com.varanegar.printlib.bixolon.SPP_R220;
import com.varanegar.printlib.bixolon.SPP_R300;
import com.varanegar.printlib.bixolon.SPP_R310;
import com.varanegar.printlib.bixolon.SPP_R400;
import com.varanegar.printlib.bixolon.Seydi;
import com.varanegar.printlib.driver.PrinterDriver;
import com.varanegar.printlib.gprinter.GPrinterDriver;
import com.varanegar.printlib.urovo.UrovoPrinterDriver;
import com.varanegar.printlib.woosim.WoosimPrinterDriver;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.print.drivers.A910PdaPrinterDriver;
import com.varanegar.vaslibrary.print.drivers.I9000SPdaPrinterDriver;
import com.varanegar.vaslibrary.print.drivers.N910PdaPrinterDriver;
import com.varanegar.vaslibrary.print.drivers.RahyabPdaPrinterDriver;
import com.varanegar.vaslibrary.print.drivers.SamanKishPrinterDriver;
import com.varanegar.vaslibrary.print.drivers.SepehrPdaPrintDriver;
import com.varanegar.vaslibrary.print.drivers.TejaratElectronicParsianPdaPrinterDriver;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 3/15/2018.
 */

public class PrinterManager extends BaseManager<PrinterModel> {
    public PrinterManager(@NonNull Context context) {
        super(context, new PrinterModelRepository());
    }

    class BlueToothReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                final String deviceName = device.getName();
                addOrUpdatePrinter(deviceName);
            } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                //Device is now connected
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                final String deviceName = device.getName();
                addOrUpdatePrinter(deviceName);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //Done searching
                if (scanCallBack != null)
                    scanCallBack.onFinish();
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                //Device is about to disconnect
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                final String deviceName = device.getName();
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Device has disconnected
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                final String deviceName = device.getName();
            }
        }
    }

    private void addOrUpdatePrinter(@Nullable final String name) {
        if (name == null)
            return;

        List<String> printers = Linq.map(listSupportedPrinters(), new Linq.Map<SupportedPrinter, String>() {
            @Override
            public String run(SupportedPrinter item) {
                return item.PrinterName;
            }
        });
        boolean isSupported = Linq.exists(printers, new Linq.Criteria<String>() {
            @Override
            public boolean run(String item) {
                if (name.startsWith("K319"))
                    return name.startsWith(item);
                return name.equals(item);
            }
        });
        if (!isSupported)
            return;

        PrinterModel printerModel = getItem(new Query().from(Printer.PrinterTbl).whereAnd(Criteria.equals(Printer.PrinterName, name)));
        if (printerModel == null) {
            printerModel = new PrinterModel();
            printerModel.UniqueId = UUID.randomUUID();
        }
        printerModel.PrinterName = name;
        printerModel.IsFound = true;
        try {
            insertOrUpdate(printerModel);
            Timber.i("Printer " + name + " added.");
        } catch (Exception e) {
            Timber.e(e);

        }
    }

    public void registerScanner() {
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        IntentFilter filter4 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filter5 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        IntentFilter filter6 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        BlueToothReceiver bluetoothReceiver = new BlueToothReceiver();
        getContext().registerReceiver(bluetoothReceiver, filter1);
        getContext().registerReceiver(bluetoothReceiver, filter2);
        getContext().registerReceiver(bluetoothReceiver, filter3);
        getContext().registerReceiver(bluetoothReceiver, filter4);
        getContext().registerReceiver(bluetoothReceiver, filter5);
        getContext().registerReceiver(bluetoothReceiver, filter6);
    }

    private List<SupportedPrinter> listSupportedPrinters() {
        List<SupportedPrinter> printers = new ArrayList<>();
        printers.add(new SupportedPrinter("SPP-R200II", SPP_R200II.class));
        printers.add(new SupportedPrinter("SPP-R200III", SPP_R200III.class));
        printers.add(new SupportedPrinter("SPP-R220", SPP_R220.class));
        printers.add(new SupportedPrinter("SPP-R300", SPP_R300.class));
        printers.add(new SupportedPrinter("SPP-R310", SPP_R310.class));
        printers.add(new SupportedPrinter("SPP-R400", SPP_R400.class));
        printers.add(new SupportedPrinter("WOOSIM", WoosimPrinterDriver.class));
        printers.add(new SupportedPrinter("rahyab", RahyabPdaPrinterDriver.class));
        printers.add(new SupportedPrinter("sepehr", SepehrPdaPrintDriver.class));
        printers.add(new SupportedPrinter("K319", UrovoPrinterDriver.class));
        printers.add(new SupportedPrinter("Seydi", Seydi.class));
        printers.add(new SupportedPrinter("Printer_EED8", GPrinterDriver.class));
        printers.add(new SupportedPrinter("I9000S", I9000SPdaPrinterDriver.class));
        printers.add(new SupportedPrinter("SamanKish", SamanKishPrinterDriver.class));
        printers.add(new SupportedPrinter("A910", A910PdaPrinterDriver.class));
        printers.add(new SupportedPrinter("TejaratElectronicParsian", TejaratElectronicParsianPdaPrinterDriver.class));
        printers.add(new SupportedPrinter("N910", N910PdaPrinterDriver.class));
//        printers.add(new SupportedPrinter("K319_0135", K319_0135.class));
//        printers.add(new SupportedPrinter("K319_2256", K319_2256.class));
//        printers.add(new SupportedPrinter("K319_7644", K319_7644.class));
//        printers.add(new SupportedPrinter("K319_7644L", K319_7644L.class));
        return printers;
    }

    public List<PrinterModel> listAvailablePrinters() {
        Query query = new Query().from(Printer.PrinterTbl);
        return getItems(query);
    }

    private PrinterDriver getDriver(final String printerName) {
        SupportedPrinter supportedPrinter = Linq.findFirst(listSupportedPrinters(), new Linq.Criteria<SupportedPrinter>() {
            @Override
            public boolean run(SupportedPrinter item) {
                if (printerName.startsWith("K319"))
                    return printerName.startsWith(item.PrinterName);
                return item.PrinterName.equals(printerName);
            }
        });
        if (supportedPrinter == null) {
            Timber.e("Printer " + printerName + " is not supported");
            return null;
        }
        try {
            Constructor<? extends PrinterDriver> constructor = supportedPrinter.PrinterDriver.getConstructor(Context.class);
            return constructor.newInstance(getContext());
        } catch (NoSuchMethodException e) {
            Timber.e(e, "Constructor with context not found for " + printerName);
            return null;
        } catch (Exception e) {
            Timber.e(e, "Printer name = " + printerName);
            return null;
        }
    }

    public interface PrintDriverCallBack {
        void onSuccess(PrinterDriver driver);

        void onFailure(String error);

        void onCancel();
    }

    public void getPrinterDriver(@NonNull final AppCompatActivity activity, @NonNull final PrintDriverCallBack callBack) {
        PrinterModel defaultPrinter = getDefaultPrinter();
        if (defaultPrinter != null && defaultPrinter.IsFound) {
            getPrinterDriver(defaultPrinter, callBack);
        } else {
            showSelectionDialog(activity, callBack);
        }
    }

    private void getPrinterDriver(@NonNull PrinterModel printerModel, @NonNull PrintDriverCallBack callBack) {
        PrinterDriver driver = getDriver(printerModel.PrinterName);
        if (driver != null) {
            callBack.onSuccess(driver);
        } else {
            callBack.onFailure(getContext().getString(R.string.printer_is_not_supported));
        }
    }

    public void showSelectionDialog(@NonNull AppCompatActivity activity, @NonNull final PrintDriverCallBack callBack) {
        SelectPrinterDialog dialog = new SelectPrinterDialog();
        dialog.setCallBack(new SelectPrinterDialog.CallBack() {
            @Override
            public void onSelected(PrinterModel printerModel) {
                getPrinterDriver(printerModel, callBack);
            }

            @Override
            public void onCancel() {
                callBack.onCancel();
            }
        });
        dialog.show(activity.getSupportFragmentManager(), "SelectPrinterDialog");
    }

    public interface ScanCallBack {
        void onFinish();

        void onStart();

        void onError(String error);
    }

    private static ScanCallBack scanCallBack;

    public void registerCallBack(@NonNull ScanCallBack callBack) {
        scanCallBack = callBack;
    }

    public void unRegisterCallBack() {
        scanCallBack = null;
    }

    public void startScanner() {
        if (scanCallBack != null)
            scanCallBack.onStart();
        List<PrinterModel> printerModels = getItems(new Query().from(Printer.PrinterTbl));
        for (PrinterModel printerModel :
                printerModels) {
            printerModel.IsFound = false;
        }
        if (printerModels.size() > 0)
            try {
                update(printerModels);
            } catch (Exception e) {
                Timber.e(e);
            }

        List<String> otherPrinters = findNonBlueToothPrinters();
        if (otherPrinters.size() > 0) {
            for (String printerName :
                    otherPrinters) {
                addOrUpdatePrinter(printerName);
            }
        }

        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Timber.d("bluetooth hardware is not available!");
            if (scanCallBack != null) {
                if (otherPrinters.size() == 0)
                    scanCallBack.onError(getContext().getString(R.string.bluetooth_is_not_available));
                else
                    scanCallBack.onFinish();
            }
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            Timber.d("bluetooth is not enabled!");
            if (scanCallBack != null) {
                if (otherPrinters.size() == 0)
                    scanCallBack.onError(getContext().getString(R.string.bluetooth_is_not_enabled));
                else
                    scanCallBack.onFinish();
            }
            return;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices == null) {
            if (scanCallBack != null) {
                if (otherPrinters.size() == 0)
                    scanCallBack.onError(getContext().getString(R.string.no_paired_device));
                else
                    scanCallBack.onFinish();
            }
        } else {
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }
            bluetoothAdapter.startDiscovery();
        }

    }

    private List<String> findNonBlueToothPrinters() {
        List<String> otherPrinters = new ArrayList<>();
        Timber.d("Device model is = "+ Build.MODEL);
        if (Build.MODEL.equals("KS8223")) {
            otherPrinters.add("rahyab");
            RahyabPdaPrinterDriver.init(getContext());
        } else if (Build.MODEL.equals("i9000S")) {
            otherPrinters.add("I9000S");
        } else if (Build.MODEL.equals("A930")){
            otherPrinters.add("SamanKish");
        } else if (Build.MODEL.equals("A910")) {
            otherPrinters.add("A910");
        } else if (Build.MODEL.equals("P1000")) {
            otherPrinters.add("TejaratElectronicParsian");
        } else if (Build.MODEL.equalsIgnoreCase("Sepehr A1")) {
            otherPrinters.add("sepehr");
        } else if (Build.MODEL.equals("N910"))
            otherPrinters.add("N910");
        return otherPrinters;
    }


    public void setDefaultPrinter(@NonNull PrinterModel printer) throws ValidationException, DbException {
        List<PrinterModel> printerModels = listAvailablePrinters();
        for (PrinterModel printerModel :
                printerModels) {
            if (printerModel.PrinterName.equals(printer.PrinterName))
                printerModel.IsDefault = true;
            else
                printerModel.IsDefault = false;
        }
        update(printerModels);
    }

    @Nullable
    public PrinterModel getDefaultPrinter() {
        Query query = new Query().from(Printer.PrinterTbl).whereAnd(Criteria.equals(Printer.IsDefault, true));
        return getItem(query);
    }

    @Nullable
    public PrinterModel getPrinter(@NonNull String printerName) {
        Query query = new Query().from(Printer.PrinterTbl).whereAnd(Criteria.equals(Printer.PrinterName, printerName));
        return getItem(query);
    }
}
