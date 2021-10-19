package varanegar.com.discountcalculatorlib.handler.sds.v4_16;


import java.math.BigDecimal;

import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerOldInvoiceDetailDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCHeaderSDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCItemSDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductDBAdapter;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallReturnData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallReturnLineData;

/**
 * Created by m.aghajani on 7/2/2016.
 */
public class PromotionDecreaseEVCItemByQtyV3 {

    private static int methodType, evcType, disType;

    public static void DecreaseEVCItem(DiscountCallReturnData returnData, String evcId1, String evcId2)
    {
        methodType = getMethodType(returnData.returnRefId);
        String[] temp = getEVCValues(evcId2).split(",");
        evcType = Integer.parseInt(temp[0]);
        disType = Integer.parseInt(temp[1]);


//        BigDecimal returnTotalQty = BigDecimal.ZERO;
        for(DiscountCallReturnLineData lineData : returnData.callReturnLineItemData.values())
        {
            if(lineData.returnTotalQty.compareTo(BigDecimal.ZERO) == 1)
            {
                DecreaseEVCItemByQty(evcId1, evcId2, lineData.productId, lineData.returnTotalQty, returnData.returnRefId, returnData.callUniqueId);
            }
        }
    }

    private static int getMethodType(int saleId)
    {
        return DiscountCustomerOldInvoiceDetailDBAdapter.getInstance().getMethodType(saleId);
    }

    private static String getEVCValues(String evcId)
    {
        return EVCHeaderSDSDBAdapter.getInstance().getEVCValuesForReturn(evcId);
    }

    /*usp_DecreaseEVCItemByQty*/
    private static void DecreaseEVCItemByQty(String evcId1, String evcId2, int productId, BigDecimal totalQty, int saleId, String callId)
    {
        totalQty = checkCartonPrizeQty(productId, totalQty);
        BigDecimal newTotalQty = totalQty;
        if( (evcType == 2 || evcType == 10 || evcType == 12 ) && (disType ==2 || disType == 4))
        {
            newTotalQty = update1(evcId1, productId, totalQty, saleId, callId);
        }
        EVCItemSDSDBAdapter.getInstance().DecreaseEVCItemByQty_Internal(evcId2, newTotalQty, productId, methodType);

        EVCItemSDSDBAdapter.getInstance().calcTaxCharge(evcId2, productId, evcType);
    }

    private static BigDecimal checkCartonPrizeQty(int productId, BigDecimal totalQty)
    {
        return DiscountProductDBAdapter.getInstance().checkCartonPrizeQtyForEVC(productId, totalQty);
    }

    private static BigDecimal update1(String evcId1, int productId, BigDecimal totalQty, int saleId, String callId)
    {
        return  EVCItemSDSDBAdapter.getInstance().checkReferenceDisSale(evcId1, saleId, productId, totalQty, callId, evcType);
    }

}
