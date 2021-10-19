package varanegar.com.discountcalculatorlib.handler.vnlite;

import java.math.BigDecimal;

import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerOldInvoiceDetailDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCHeaderVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCItemVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductDBAdapter;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallReturnData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallReturnLineData;

public class PromotionDecreaseEVCItemByQtyVnLiteV3 {

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
                DecreaseEVCItemByQty(evcId1, evcId2, lineData.productId, lineData.returnSmallQty, lineData.returnBigQty, returnData.returnRefId, returnData.callUniqueId, lineData.returnTotalQty);
            }
        }
    }

    private static int getMethodType(int saleId)
    {
        return DiscountCustomerOldInvoiceDetailDBAdapter.getInstance().getMethodType(saleId);
    }

    private static String getEVCValues(String evcId)
    {
        return EVCHeaderVnLiteDBAdapter.getInstance().getEVCValuesForReturn(evcId);
    }

    private static void DecreaseEVCItemByQty(String evcId1, String evcId2, int productId, BigDecimal smallQty, BigDecimal bigQty, int saleId, String callId, BigDecimal totalQty)
    {
        update2(evcId2, smallQty, bigQty, productId, totalQty);
    }

    private static BigDecimal checkCartonPrizeQty(int productId, BigDecimal totalQty)
    {
        return DiscountProductDBAdapter.getInstance().checkCartonPrizeQtyForEVC(productId, totalQty);
    }

    private static void update2(String evcId2, BigDecimal unitQty,BigDecimal packQty, int productId, BigDecimal totalQty)
    {
        EVCItemVnLiteDBAdapter.getInstance().decreaseTotalQtyByReturnQty(evcId2, unitQty, packQty, productId, evcType, totalQty);
    }

}
