package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;

import java.util.ArrayList;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountOrderPrizeViewModel;

/**
 * Created by A.Razavi on 3/27/2018.
 */

public class EVCTempOrderPrizeSDSDBAdapter extends DiscountBaseDataAdapter {
    public static final String DATABASE_TABLE = "EVCTempOrderPrizeSDS";
    private static EVCTempOrderPrizeSDSDBAdapter instance;

    public EVCTempOrderPrizeSDSDBAdapter() {
    }

    public static EVCTempOrderPrizeSDSDBAdapter getInstance() {

        if (instance == null) {
            instance = new EVCTempOrderPrizeSDSDBAdapter();
        }

        return instance;

    }

    public void clearAllData() {
        db.delete(DATABASE_TABLE, null, null);
    }


    public ArrayList<DiscountOrderPrizeViewModel> getAll(int orderRef){
        return new ArrayList<DiscountOrderPrizeViewModel>();
    }


}
