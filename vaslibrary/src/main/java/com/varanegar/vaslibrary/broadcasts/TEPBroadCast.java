package com.varanegar.vaslibrary.broadcasts;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.varanegar.vaslibrary.base.VasActivity;
import com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader.TejaratElectronicParsianCardReader;

import java.util.List;

import timber.log.Timber;
/**
 * Created by saeedeh on 4/12/2021.
 */
public class TEPBroadCast extends BroadcastReceiver {
    public OnReceivedListener listener=null;
    @SuppressLint("WrongConstant")
    @Override
    public void onReceive(Context context, Intent intent) {
        String str = "Hi I got your message : \n";
        TEPResponse response = new TEPResponse();
        response.ER = intent.getStringExtra(TEPResponse.Error);
        response.TO = intent.getStringExtra(TEPResponse.TimeOut);
        response.SC = intent.getStringExtra(TEPResponse.SwipeCard);
        response.BR = intent.getStringExtra(TEPResponse.BarCodeStatus);
        response.CP = intent.getStringExtra(TEPResponse.CheckPaper);
        response.PS = intent.getStringExtra(TEPResponse.PrintStatus);
        response.MI = intent.getStringExtra(TEPResponse.MifareResult);
        response.RS = intent.getStringExtra(TEPResponse.ResponseCode);
        response.GI = intent.getStringArrayExtra(TEPResponse.GetInfo);
        response.PO = intent.getStringArrayExtra(TEPResponse.GetPositionInfo);

        boolean b = true;
        if(response.ER != null)      { str = str + "Error : " + response.ER; }
        else if(response.SC != null) { str = str + "SwipeCard : " + response.SC; b = false; }
        else if(response.TO != null)   str = str + "TimeOut : " + response.TO;
        else if(response.BR != null)   str = str + "BarCodeStatus : " + response.BR;
        else if(response.CP != null)   str = str + "CheckPaper : " + response.CP;
        else if(response.PS != null)   str = str + "PrintStatus : " + response.PS;
        else if(response.GI != null)   str = str + "MerchantInfo : \n"
                + "merchantName :" + response.GI[0] + "\n"
                + "merchantTell :" + response.GI[1] + "\n"
                + "terminalID :"   + response.GI[2] + "\n"
                + "serialNumber :" + response.GI[3];

        else if(response.PO != null)   str = str + "PositionInfo : \n"
                + "Latitude :" + response.PO[0] + "\n"
                + "Longitude :" + response.PO[1];
        else if(response.MI != null) {
            List<Mifare> mifareList = Mifare.getMifare(response.MI);
            int len = mifareList.size();
            str = str + "MifareResult len : " + len + "\n";
            for(int i = 0; i < len; i++) str = str + mifareList.get(i).toString() + "\n";
        }
        else if(response.RS != null) {
            str = str + "ResponseCode : " + response.RS + "\n";
            if(response.RS.equals("05")) {
                String qrResult = intent.getStringExtra("QR_Result");
                if(qrResult != null) {
                    if(qrResult.equals("success")) {
                        int qrAM = intent.getIntExtra("QR_AM", 0);
                        response._RRN = intent.getStringExtra(TEPResponse.RRN);
                        response.SN = intent.getStringExtra(TEPResponse.SerialNumber);
                        response.DT = intent.getStringExtra(TEPResponse.DateTime);
                        if(qrAM == 1000) {
                            str = str + "پرداخت QR موفق بوده است." + "\n" +
                                    "شماره مرجع : "  + response._RRN + "\n" +
                                    "شماره سریال : " + response.SN   + "\n" +
                                    "تاریخ-زمان : "  + response.DT   + "\n";
                        }
                        else str = str + "عدم تطابق مبلغ پرداختی و مبلغ استعلام شده";
                    }
                    else if(qrResult.equals("failed")) str = str + "پرداخت QR ناموفق بوده است.";
                }
                else str = str + "Transaction Cancelled";
            }
            else {
                response.trxType = intent.getStringExtra(TEPResponse.TransactionType);
                response.CN      = intent.getStringExtra(TEPResponse.CardNumber);
                response._RRN    = intent.getStringExtra(TEPResponse.RRN);
                response.SN      = intent.getStringExtra(TEPResponse.SerialNumber);
                response.DT      = intent.getStringExtra(TEPResponse.DateTime);
                response.TN      = intent.getStringExtra(TEPResponse.TerminalNumber);
                switch (response.trxType) {
                    case "Sale":
                    case "BillPayment":
                        response.AM = intent.getStringExtra(TEPResponse.Amount);
                        break;
                }
                str = str + response.toString();
            }
        }
        Log.e("AAAAAA", "******* " + response.toString());
        if(listener!=null && response!=null && response.RS!=null ){
            listener.onReceived(intent);
        }

    }
    public interface OnReceivedListener{
        void onReceived(Intent intent);
    }
    public void  setOnReceivedListener(OnReceivedListener onReceivedListener){
        this.listener= onReceivedListener;
    }
}