package varanegar.com.discountcalculatorlib.api;

import retrofit2.Call;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallOrderDataOnline;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountOutputOnline;
import varanegar.com.discountcalculatorlib.viewmodel.PreSaleEvcHeaderViewModel;


public class CalcOnlinePromotionAPI extends BaseApi implements ICalcOnlinePromotionAPI {

    @Override
    public Call<DiscountOutputOnline> getPromotion(DiscountCallOrderDataOnline order, boolean calcDiscount, boolean calcSaleRestriction, boolean calcPaymentType) {
        ICalcOnlinePromotionAPI api = getRetrofitBuilder().build().create(ICalcOnlinePromotionAPI.class);
        return api.getPromotion(order, calcDiscount, calcSaleRestriction, calcPaymentType);
    }

    @Override
    public Call<DiscountOutputOnline> getDistOnlinePromotion(PreSaleEvcHeaderViewModel order,
                                                             boolean calcDiscount,
                                                             boolean calcSaleRestriction,
                                                             boolean calcPaymentType) {
        ICalcOnlinePromotionAPI api = getRetrofitBuilder().build().create(ICalcOnlinePromotionAPI.class);
        return api.getDistOnlinePromotion(order, calcDiscount, calcSaleRestriction, calcPaymentType);
    }
}

