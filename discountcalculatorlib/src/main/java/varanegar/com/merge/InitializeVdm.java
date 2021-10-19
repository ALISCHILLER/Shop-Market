package varanegar.com.merge;

import android.os.RemoteException;
import androidx.annotation.Nullable;

import varanegar.com.discountcalculatorlib.R;
import varanegar.com.discountcalculatorlib.utility.GlobalVariables;
import varanegar.com.discountcalculatorlib.utility.enumerations.BackOfficeType;
import varanegar.com.vdmclient.NoConnectionException;
import varanegar.com.vdmclient.OnBind;
import varanegar.com.vdmclient.VdmClient;
import varanegar.com.vdmclient.VdmInitializer;

/**
 * Created by A.Torabi on 8/26/2018.
 */

public class InitializeVdm {

    public void init(@Nullable final VdmInitializer.InitCallback callback) {
        final VdmClient client = new VdmClient(GlobalVariables.getDiscountInitializeHandler().getContext(),
                GlobalVariables.getDiscountInitializeHandler().getAppId());
        if (!client.isPackageInstalled()) {
            if (callback != null)
                callback.onFailure(GlobalVariables.getDiscountInitializeHandler().getContext().getString(R.string.vdm_apk_is_not_installed));
            return;
        }
        client.bind(new OnBind() {
            @Override
            public void connected() {
                Initializer initializer;
                if (GlobalVariables.getBackOffice().equals(BackOfficeType.VARANEGAR)) {
                    initializer = new Initializer(client);
                } else {
                    initializer = new Initializer(client);

                }

                try {
                    initializer.init(callback);
                } catch (RemoteException e) {
                    if (callback != null)
                        callback.onFailure(GlobalVariables.getDiscountInitializeHandler().getContext().getString(R.string.error_in_discount_module));
                } catch (NoConnectionException e) {
                    if (callback != null)
                        callback.onFailure(GlobalVariables.getDiscountInitializeHandler().getContext().getString(R.string.connection_to_discount_module_failed));
                }
            }

            @Override
            public void disConnected() {
                if (callback != null)
                    callback.onFailure(GlobalVariables.getDiscountInitializeHandler().getContext().getString(R.string.connection_to_discount_module_failed));
            }
        });
    }
}
