package varanegar.com.merge;

import androidx.annotation.NonNull;

import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallOrderData;
import varanegar.com.vdmclient.VdmCalculator;
import varanegar.com.vdmclient.VdmClient;
import varanegar.com.vdmclient.call.CalcData;

/**
 * Created by A.Torabi on 8/26/2018.
 */

public class BaseVdmCalculator extends VdmCalculator {
    private final DiscountCallOrderData discountCallOrderData;

    public BaseVdmCalculator(VdmClient client, DiscountCallOrderData discountCallOrderData) {
        super(client);
        this.discountCallOrderData = discountCallOrderData;
    }

    @NonNull
    @Override
    protected CalcData fillCustomerCallData(int evcType) {
        return DiscountCallOrderData.toCalcData(discountCallOrderData, evcType);
    }
}
