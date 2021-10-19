package varanegar.com.vdmclient;

import android.content.Context;
import android.os.Message;
import android.os.RemoteException;
import androidx.annotation.NonNull;

import com.varanegar.framework.network.gson.VaranegarGsonBuilder;

import java.io.IOException;
import java.util.UUID;

import timber.log.Timber;
import varanegar.com.discountcalculatorlib.R;
import varanegar.com.vdmclient.call.CalcData;
import varanegar.com.vdmclient.handlers.VdmHandler;

/**
 * Created by A.Torabi on 7/18/2018.
 */

public abstract class VdmCalculator {
    private final String appId;
    private final Context context;
    private final String sessionId;
    private VdmClient vdmClient;
    private CalcCallBack calcCallBack;

    public VdmCalculator(VdmClient client) {
        this.vdmClient = client;
        sessionId = client.getSessionId();
        context = client.getContext();
        this.appId = client.getAppId();
    }


    @NonNull
    protected abstract CalcData fillCustomerCallData(int evcType);

    public interface CalcCallBack {
        void onSuccess(CalcData calcData);

        void onFailure(String error);
    }

    public void calc(@NonNull CalcCallBack calcCallBack, int evcType) throws RemoteException, NoConnectionException, IOException {
        CalcData calcData = fillCustomerCallData(evcType);
        this.calcCallBack = calcCallBack;
        if (sessionId == null) {
            calcCallBack.onFailure(context.getString(R.string.it_seems_vdm_is_not_initialized));
            return;
        }
        try {
            String data = VaranegarGsonBuilder.build().create().toJson(calcData);
            vdmClient.run(new CalcHandler(this, sessionId, appId, data));
        } catch (Exception ex) {
            Timber.e(ex);
            calcCallBack.onFailure(context.getString(R.string.error_calculating_discount));
        }
    }

    static class CalcHandler extends VdmHandler {

        private final VdmCalculator calculator;
        private final String myRequestId;

        public CalcHandler(VdmCalculator calculator, String sessionId, String appId, String calcData) {
            super(104);
            this.calculator = calculator;
            bundle.putString("SessionId", sessionId);
            bundle.putString("AppId", appId);
            bundle.putString("CalcData", calcData);
            myRequestId = UUID.randomUUID().toString();
            bundle.putString("RequestId", myRequestId);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 104) {
                String sessionId = msg.getData().getString("SessionId", null);
                String requestId = msg.getData().getString("RequestId", null);
                if (requestId == null || requestId.isEmpty()) {
                    calculator.calcCallBack.onFailure(calculator.context.getString(R.string.error_calculating_discount));
                    return;
                }
                if (sessionId == null || sessionId.isEmpty()) {
                    calculator.calcCallBack.onFailure(calculator.context.getString(R.string.error_calculating_discount));
                    return;
                }
                if (requestId.equals(myRequestId)) {
                    String str = msg.getData().getString("CalcData");
                    if (str == null || str.length() == 0) {
                        calculator.calcCallBack.onFailure(calculator.context.getString(R.string.error_calculating_discount));
                        return;
                    }
                    try {
                        CalcData calcData = VaranegarGsonBuilder.build().create().fromJson(str, CalcData.class);
                        calculator.calcCallBack.onSuccess(calcData);
                    } catch (Exception ex) {
                        Timber.e(ex);
                        calculator.calcCallBack.onFailure(calculator.context.getString(R.string.error_calculating_discount));
                    }
                }
            }
        }


    }

}
