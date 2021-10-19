//package com.varanegar.vaslibrary.ui.fragment;
//
//import android.os.Bundle;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.google.zxing.Result;
//import com.varanegar.framework.base.VaranegarApplication;
//import com.varanegar.framework.base.VaranegarFragment;
//import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
//
//import me.dm7.barcodescanner.zxing.ZXingScannerView;
//
///**
// * Created by A.Torabi on 7/5/2017.
// */
//
//public class BarcodeFragment extends VaranegarFragment implements ZXingScannerView.ResultHandler {
//    private static final String TAG = "Barcode";
//    private ZXingScannerView mScannerView;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        mScannerView = new ZXingScannerView(getContext());   // Programmatically initialize the scanner view
//        return mScannerView;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
//        mScannerView.startCamera();          // Start camera on resume
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mScannerView.stopCamera();           // Stop camera on pause
//    }
//
//    @Override
//    public void handleResult(Result result) {
//        UpdateManager updateManager = new UpdateManager(getContext());
//        updateManager.saveBarcode(result.getText());
//
//        VaranegarApplication.getInstance().save("dd003d32-4f05-423f-b7ba-3ccc9f54fb39",result);
//        mScannerView.resumeCameraPreview(this);
//        getVaranegarActvity().popFragment();
//    }
//
//}
