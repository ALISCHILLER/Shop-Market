package varanegar.com.discountcalculatorlib.viewmodel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductUnitDBAdapter;
import varanegar.com.discountcalculatorlib.entity.product.DiscountProductUnit;
import varanegar.com.discountcalculatorlib.handler.PromotionHandlerV3;
import varanegar.com.discountcalculatorlib.utility.DiscountException;
import varanegar.com.discountcalculatorlib.utility.enumerations.EVCType;
import varanegar.com.discountcalculatorlib.utility.enumerations.ReturnType;
import varanegar.com.discountcalculatorlib.utility.enumerations.ReturnVisitStatus;

public class DiscountCallReturnData {

    public int customerId;
    public String returnUniqueId;
    public String callUniqueId;//
    public int returnRequestId;
    public String returnRequestDate;
    public String returnRequestNo;
    public int distId;
    public int returnType;
    public String returnTypeText;
    public BigDecimal totalRequestAmount;
    public BigDecimal totalRequestTax;
    public BigDecimal totalRequestCharge;
    public BigDecimal totalRequestDiscount;
    public BigDecimal totalRequestNetAmount;
    public BigDecimal totalReturnAmount;
    public BigDecimal totalReturnDiscount;
    public BigDecimal totalReturnAddAmount;
    public BigDecimal totalReturnCharge;
    public BigDecimal totalReturnTax;
    public BigDecimal totalReturnNetAmount;
    public int visitStatusId;
    public String visitStatusText;
    public String visitRejectReason;
    public int localPaperNo;
    public int returnRefId;
    public String returnRefNo;
    public String returnRefDate;
    public UUID returnReasonId;
    public String returnReason;
    public int dealerId;
    public String comment;
    public boolean IsNew;
    public int DCRef;
    public int SaleOfficeRef;


    public Map<String, DiscountCallReturnLineData> callReturnLineItemData;
    public ArrayList<DiscountCallReturnLineData> callReturnLineItemDataWithPromo;
    public ArrayList<DiscountCallReturnLineData> callReturnLineItemDataWithPromoBeforeChange;


    public DiscountCallReturnData()
    {

    }

    public DiscountCallReturnData(String callUniqueId, String returnUniqueId, String returnTypeText, int returnType, int distId, int returnRequestId
            , String returnRequestDate, String returnRequestNo, BigDecimal totalRequestAmount, BigDecimal totalRequestTax, BigDecimal totalRequestCharge
            , BigDecimal totalRequestDiscount, BigDecimal totalRequestNetAmount, BigDecimal totalReturnAmount, BigDecimal totalReturnDiscount
            , BigDecimal totalReturnCharge, BigDecimal totalReturnTax, BigDecimal totalReturnNetAmount, int visitStatusId, String visitStatusText
            , String visitRejectReason, int localPaperNo, int customerId, int returnRefId, String returnRefNo, String returnRefDate, UUID returnReasonId, String returnReason
            , int dealerId, String comment, boolean IsNew, int DCRef, int SaleOfficeRef, BigDecimal totalReturnAddAmount)
    {
        this.callUniqueId = callUniqueId;
        this.returnUniqueId = returnUniqueId;
        this.returnTypeText = returnTypeText;
        this.returnType = returnType;
        this.distId = distId;
        this.returnRequestId = returnRequestId;
        this.returnRequestDate = returnRequestDate;
        this.returnRequestNo = returnRequestNo;
        this.totalRequestAmount = totalRequestAmount;
        this.totalRequestTax = totalRequestTax;
        this.totalRequestCharge = totalRequestCharge;
        this.totalRequestDiscount = totalRequestDiscount;
        this.totalRequestNetAmount = totalRequestNetAmount;
        this.totalReturnAmount = totalReturnAmount;
        this.totalReturnDiscount = totalReturnDiscount;
        this.totalReturnCharge = totalReturnCharge;
        this.totalReturnTax = totalReturnTax;
        this.totalReturnNetAmount = totalReturnNetAmount;
        this.visitStatusId = visitStatusId;
        this.visitStatusText = visitStatusText;
        this.visitRejectReason = visitRejectReason;
        this.localPaperNo = localPaperNo;
        this.customerId = customerId;
        this.returnRefId = returnRefId;
        this.returnRefNo = returnRefNo;
        this.returnReason = returnReason;
        this.returnReasonId = returnReasonId;
        this.dealerId = dealerId;
        this.comment = comment;
        this.IsNew = IsNew;
        this.DCRef = DCRef;
        this.SaleOfficeRef = SaleOfficeRef;
        this.returnRefDate = returnRefDate;

        this.totalReturnAddAmount = totalReturnAddAmount;
    }

    //region Validation methods

    public boolean isConfirmed()
    {
        if(visitStatusId == ReturnVisitStatus.UNKNOWN.value())
            return false;
        else
            return true;
    }


    public boolean hasRequest()
    {
        if(this.returnType == ReturnType.NOREF.value()
                || this.returnType == ReturnType.WITHREF.value())
            return true;
        else
            return false;
    }

    public boolean hasLine()
    {
        boolean result = false;
        for(DiscountCallReturnLineData lineData : callReturnLineItemData.values())
        {
            if(lineData.returnTotalQty.compareTo(BigDecimal.ZERO) == 1)
            {
                result = true;
                break;
            }
        }

        return result;
    }

    private boolean checkResultQty(DiscountCallReturnData mainReturn, List<DiscountCallReturnLineData> promoReturnList)
    {
        boolean result = true;

        LinkedHashMap<Integer, BigDecimal> mainReturnProducts = new LinkedHashMap<>();
        LinkedHashMap<Integer, BigDecimal> promoReturnProducts = new LinkedHashMap<>();

        for(DiscountCallReturnLineData lineData : mainReturn.callReturnLineItemData.values())
        {
            if(mainReturnProducts.containsKey(lineData.productId))
            {
                mainReturnProducts.put(lineData.productId, mainReturnProducts.get(lineData.productId).add(lineData.returnTotalQty));
            }
            else
            {
                mainReturnProducts.put(lineData.productId, lineData.returnTotalQty);
            }
        }

        for(DiscountCallReturnLineData lineData : promoReturnList)
        {
            if(promoReturnProducts.containsKey(lineData.productId))
            {
                promoReturnProducts.put(lineData.productId, promoReturnProducts.get(lineData.productId).add(lineData.returnTotalQty));
            }
            else
            {
                promoReturnProducts.put(lineData.productId, lineData.returnTotalQty);
            }
        }

        if(mainReturnProducts.size() != promoReturnProducts.size())
            result = false;


        for(LinkedHashMap.Entry<Integer, BigDecimal> mainValues : mainReturnProducts.entrySet()) {
            if (result) {
                    if (mainValues.getValue().compareTo(promoReturnProducts.get(mainValues.getKey())) != 0) {
                        result = false;
                        break;
                    }
            }
        }

        return result;
    }

    private boolean hasPrizeItem(Map<Integer, List<DiscountCallReturnLineData>> items)
    {
        boolean result = false;

        for (Map.Entry<Integer, List<DiscountCallReturnLineData>> lines :items.entrySet())
        {
            for (DiscountCallReturnLineData lineData : lines.getValue())
            if(lineData.isFreeItem == 1) {
                result = true;
                break;
            }
        }

        return result;
    }

    //endregion Validation methods

    //region Calculation methods

//    private void CalcValues()
//    {
//        BigDecimal sumTotalReturnAmount, sumTotalReturnDiscount, sumTotalReturnAddAmount, sumTotalReturnCharge, sumTotalReturnTax, sumTotalReturnNetAmount;
//        sumTotalReturnAmount = sumTotalReturnDiscount = sumTotalReturnAddAmount = sumTotalReturnCharge = sumTotalReturnTax = sumTotalReturnNetAmount = BigDecimal.ZERO;
//
//        for(DiscountCallReturnLineData lineData : callReturnLineItemDataWithPromo)
//        {
//            if(this.returnType == ReturnType.WITHREF.value() || this.returnType == ReturnType.WITHREF_WITHOUT_ORDER.value())
//                lineData.calcValues();
//
//            sumTotalReturnAmount = sumTotalReturnAmount.add(lineData.totalReturnAmount);
//            sumTotalReturnDiscount = sumTotalReturnDiscount.add(lineData.totalReturnDiscount);
//            sumTotalReturnAddAmount = sumTotalReturnAddAmount.add(lineData.totalReturnAddAmount);
//            sumTotalReturnCharge = sumTotalReturnCharge.add(lineData.totalReturnCharge);
//            sumTotalReturnTax = sumTotalReturnTax.add(lineData.totalReturnTax);
//            sumTotalReturnNetAmount = sumTotalReturnNetAmount.add(lineData.totalReturnNetAmount);
//        }
//
//        this.totalReturnAmount = sumTotalReturnAmount;
//        this.totalReturnDiscount = sumTotalReturnDiscount;
//        this.totalReturnAddAmount = sumTotalReturnAddAmount;
//        this.totalReturnCharge = sumTotalReturnCharge;
//        this.totalReturnTax = sumTotalReturnTax;
//        this.totalReturnNetAmount = sumTotalReturnNetAmount;
//    }

//
//
//    private void calcPromotion(OnNewReturnPrizeAddListener listener) throws DiscountException
//    {
//
//        final Map<Integer, DiscountCallReturnData> seperatedItems = seperateByReferenceId();
//        ArrayList<DiscountCallReturnData> copyItemms = new ArrayList<>(seperatedItems.values());
//        final Map<Integer, List<DiscountCallReturnLineData>> finalResult = new HashMap<>();
//
//        //TODO collect promotion results in finalResult and compare it with main
//        for (DiscountCallReturnData data : copyItemms)
//        {
//            DiscountCallReturnData sumOfReturns = getTotalOfWellAndWaste(data);
//            List<DiscountCallReturnLineData> result = PromotionHandlerV3.calcPromotion(sumOfReturns, EVCType.SELLRETURN);
//
//            finalResult.put(data.returnRefId , result);
//        }
//
//        if(hasPrizeItem(finalResult))
//        {
//            listener.onPrizeAdding(seperatedItems, finalResult);
//        }
//        else
//        {
//            fillWithPromo(seperatedItems, finalResult);
//            SetConfirmStatus();
//        }
//    }


    //endregion Calculation methods

    //region Action methods


//
//    public void SetConfirmStatus()
//    {
//        CalcValues();
//
//        this.visitStatusId = ReturnVisitStatus.RETURNED.value();
//        this.visitStatusText = ReturnVisitStatus.RETURNED.desc();
//    }

//    public void CancelConfirmedRequest()
//    {
//        if(this.callReturnLineItemDataWithPromo != null)
//            this.callReturnLineItemDataWithPromo.clear();
//
//        if(this.callReturnLineItemDataWithPromoBeforeChange != null)
//            this.callReturnLineItemDataWithPromoBeforeChange.clear();
//
//        CalcValues();
//
//        this.visitStatusId = ReturnVisitStatus.UNKNOWN.value();
//        this.visitStatusText = ReturnVisitStatus.UNKNOWN.desc();
//    }
//
//    public String addNewData(int productId, String productName, String productCode, BigDecimal unitPrice, long priceId, BigDecimal refQty, int returnRefId, String returnRefNo)
//    {
//        DiscountCallReturnLineData lineData = new DiscountCallReturnLineData(this.customerId, this.returnUniqueId, this.callUniqueId, productId, productName
//                                , productCode, unitPrice, priceId, refQty, returnRefId, returnRefNo, dealerId, returnRefDate);
//        this.callReturnLineItemData.put(lineData.returnLineUniqueId, lineData);
//
//        return lineData.returnLineUniqueId;
//    }

    public DiscountCallReturnLineData getCallReturnLineData(int productId, boolean nullIfMoreThanOne)
    {
        DiscountCallReturnLineData returnLineData = null;
        int foundItems = 0;

        for(DiscountCallReturnLineData lineData : callReturnLineItemData.values())
        {
            if(lineData.productId == productId )
            {
                returnLineData = lineData;
                foundItems ++;
            }
        }

        if(foundItems > 1 && nullIfMoreThanOne) returnLineData = null;
        return returnLineData;
    }
//
//    public DiscountCallReturnLineData getCallReturnLineData(int productId, int saleId, int productTypeId)
//    {
//        DiscountCallReturnLineData returnLineData = null;
//
//        for(DiscountCallReturnLineData lineData : callReturnLineItemData.values())
//        {
//            if(lineData.productId == productId && lineData.referenceId == saleId && lineData.returnProductTypeId == productTypeId)
//            {
//                returnLineData = lineData;
//                break;
//            }
//        }
//
//        return returnLineData;
//    }
//
//    public void fillWithPromo(Map<Integer, DiscountCallReturnData> seperatedCallReturns  , Map<Integer, List<DiscountCallReturnLineData>> promoLineList)
//    {
//        if(this.callReturnLineItemDataWithPromo == null)
//            this.callReturnLineItemDataWithPromo = new ArrayList<>();
//
//        this.callReturnLineItemDataWithPromo.clear();
//
//        List<DiscountCallReturnLineData> promoLines ;
//
//        for(Map.Entry<Integer, DiscountCallReturnData> seperatedCallReturn : seperatedCallReturns.entrySet()) {
//
//            promoLines = promoLineList.get(seperatedCallReturn.getKey());
//
//            for (DiscountCallReturnLineData lineData : promoLines) {
//
//                if (lineData.isFreeItem == 1 && lineData.returnTotalQty.compareTo(BigDecimal.ZERO) == 1) {
//
//                    this.callReturnLineItemDataWithPromo.add(lineData);
//                }
//            }
//
//            for (DiscountCallReturnLineData lineData : promoLines) {
//
//                if (lineData.isFreeItem == 0 && lineData.returnTotalQty.compareTo(BigDecimal.ZERO) == 1) {
//
//                    DiscountCallReturnLineData wellItem = seperatedCallReturn.getValue().getCallReturnLineData(lineData.productId, seperatedCallReturn.getValue().returnRefId, DiscountCallReturnLineData.ReturnProductTypeEnum.INTACT.value());
//                    DiscountCallReturnLineData wasteItem = seperatedCallReturn.getValue().getCallReturnLineData(lineData.productId, seperatedCallReturn.getValue().returnRefId, DiscountCallReturnLineData.ReturnProductTypeEnum.DEFECTIVE.value());
//
//                    if ((wellItem != null && wellItem.returnTotalQty.compareTo(BigDecimal.ZERO) == 1) && (wasteItem == null || wasteItem.returnTotalQty.compareTo(BigDecimal.ZERO) == 0))
//                    {
//
//                        lineData.returnProductTypeId = DiscountCallReturnLineData.ReturnProductTypeEnum.INTACT.value();
//                        this.callReturnLineItemDataWithPromo.add(lineData);
//
//                        wellItem.returnTotalQty = wellItem.returnTotalQty.subtract(lineData.returnTotalQty);
//                        wellItem.returnBigQty = wellItem.returnBigQty.subtract(lineData.returnBigQty);
//
//                    }
//                    else if ((wasteItem != null && wasteItem.returnTotalQty.compareTo(BigDecimal.ZERO) == 1) && (wellItem == null || wellItem.returnTotalQty.compareTo(BigDecimal.ZERO) == 0)) {
//
//                        lineData.returnProductTypeId = DiscountCallReturnLineData.ReturnProductTypeEnum.DEFECTIVE.value();
//                        this.callReturnLineItemDataWithPromo.add(lineData);
//
//                        wasteItem.returnTotalQty = wasteItem.returnTotalQty.subtract(lineData.returnTotalQty);
//                        wasteItem.returnBigQty = wasteItem.returnBigQty.subtract(lineData.returnBigQty);
//
//                    }
//                    else if (wellItem != null && wasteItem != null)
//                    {
//                        BigDecimal totalQty = wellItem.returnTotalQty.add(wasteItem.returnTotalQty);
//
//                        if(totalQty.compareTo(lineData.returnTotalQty) != 0)
//                        {
//                            if(wasteItem.returnTotalQty.compareTo(wellItem.returnTotalQty) == -1)
//                            {
//                                wellItem.returnTotalQty = lineData.returnTotalQty.subtract(wasteItem.returnTotalQty);
//
//                                DiscountProductUnit BigUnit = DiscountProductUnitDBAdapter.getInstance().getProductUnitById(wellItem.productId, wellItem.returnBigQtyId);
//                                if(wellItem.returnTotalQty.intValue() % BigUnit.quantity.intValue() == 0)
//                                {
//                                    wellItem.returnBigQty = wellItem.returnTotalQty.divide(BigUnit.quantity, 0, BigDecimal.ROUND_HALF_DOWN);
//                                }
//                                else
//                                {
//                                    DiscountProductUnit smallUnit = DiscountProductUnitDBAdapter.getInstance().getSmallUnit(wellItem.productId);
//
//                                    wellItem.returnBigQty = wellItem.returnTotalQty.divide(smallUnit.quantity, 0, RoundingMode.FLOOR);
//                                    wellItem.returnBigQtyId = smallUnit.productUnitId;
//                                    wellItem.returnBigQtyName = smallUnit.productUnitName;
//                                }
//                            }
//                            else
//                            {
//                                wasteItem.returnTotalQty = lineData.returnTotalQty.subtract(wellItem.returnTotalQty);
//
//                                DiscountProductUnit BigUnit = DiscountProductUnitDBAdapter.getInstance().getProductUnitById(wasteItem.productId, wasteItem.returnBigQtyId);
//                                if(BigUnit != null && wasteItem.returnTotalQty.intValue() % BigUnit.quantity.intValue() == 0)
//                                {
//                                    wasteItem.returnBigQty = wasteItem.returnTotalQty.divide(BigUnit.quantity, 0, BigDecimal.ROUND_HALF_DOWN);
//                                }
//                                else
//                                {
//                                    DiscountProductUnit smallUnit = DiscountProductUnitDBAdapter.getInstance().getSmallUnit(wellItem.productId);
//
//                                    wasteItem.returnBigQty = wasteItem.returnTotalQty.divide(smallUnit.quantity, 0, RoundingMode.FLOOR);
//                                    wasteItem.returnBigQtyId = smallUnit.productUnitId;
//                                    wasteItem.returnBigQtyName = smallUnit.productUnitName;
//                                }
//                            }
//                        }
//
//                        wellItem.priceId = wasteItem.priceId = lineData.priceId;
//                        wellItem.unitPrice = wasteItem.unitPrice = lineData.unitPrice;
//
//                        wellItem.totalReturnAdd1Amount = wellItem.returnTotalQty.multiply(lineData.totalReturnAdd1Amount).divide(totalQty, 2, RoundingMode.FLOOR);
//                        wellItem.totalReturnAdd2Amount = wellItem.returnTotalQty.multiply(lineData.totalReturnAdd2Amount).divide(totalQty, 2, RoundingMode.FLOOR);
//                        wellItem.totalReturnAddAmount = wellItem.totalReturnAdd1Amount.add(wellItem.totalReturnAdd2Amount);
//                        wellItem.totalReturnDis1 = wellItem.returnTotalQty.multiply(lineData.totalReturnDis1).divide(totalQty, 2, RoundingMode.FLOOR);
//                        wellItem.totalReturnDis2 = wellItem.returnTotalQty.multiply(lineData.totalReturnDis2).divide(totalQty, 2, RoundingMode.FLOOR);
//                        wellItem.totalReturnDis3 = wellItem.returnTotalQty.multiply(lineData.totalReturnDis3).divide(totalQty, 2, RoundingMode.FLOOR);
//                        wellItem.totalReturnDiscount = wellItem.totalReturnDis1.add(wellItem.totalReturnDis2).add(wellItem.totalReturnDis3);
//                        wellItem.totalReturnTax = wellItem.returnTotalQty.multiply(lineData.totalReturnTax).divide(totalQty, 2, RoundingMode.FLOOR);
//                        wellItem.totalReturnCharge = wellItem.returnTotalQty.multiply(lineData.totalReturnCharge).divide(totalQty, 2, RoundingMode.FLOOR);
//
//                        this.callReturnLineItemDataWithPromo.add(wellItem);
//
////                        wasteItem.totalReturnAddAmount = lineData.totalReturnAddAmount.subtract(wellItem.totalReturnAddAmount);
////                        wasteItem.totalReturnAdd1Amount = lineData.totalReturnAdd1Amount.subtract(wellItem.totalReturnAdd1Amount);
////                        wasteItem.totalReturnAdd2Amount = lineData.totalReturnAdd2Amount.subtract(wellItem.totalReturnAdd2Amount);
////                        wasteItem.totalReturnDiscount = lineData.totalReturnDiscount.subtract(wellItem.totalReturnDiscount);
////                        wasteItem.totalReturnDis1 = lineData.totalReturnDis1.subtract(wellItem.totalReturnDis1);
////                        wasteItem.totalReturnDis2 = lineData.totalReturnDis2.subtract(wellItem.totalReturnDis2);
////                        wasteItem.totalReturnDis3 = lineData.totalReturnDis3.subtract(wellItem.totalReturnDis3);
////                        wasteItem.totalReturnTax = lineData.totalReturnTax.subtract(wellItem.totalReturnTax);
////                        wasteItem.totalReturnCharge = lineData.totalReturnCharge.subtract(wellItem.totalReturnCharge);
//
//                        wasteItem.totalReturnAddAmount = wasteItem.returnTotalQty.multiply(lineData.totalReturnAddAmount).divide(totalQty, 2, RoundingMode.FLOOR);
//                        wasteItem.totalReturnAdd1Amount = wasteItem.returnTotalQty.multiply(lineData.totalReturnAdd1Amount).divide(totalQty, 2, RoundingMode.FLOOR);
//                        wasteItem.totalReturnAdd2Amount = wasteItem.returnTotalQty.multiply(lineData.totalReturnAdd2Amount).divide(totalQty, 2, RoundingMode.FLOOR);
//                        wasteItem.totalReturnDiscount = wasteItem.returnTotalQty.multiply(lineData.totalReturnDiscount).divide(totalQty, 2, RoundingMode.FLOOR);
//                        wasteItem.totalReturnDis1 = wasteItem.returnTotalQty.multiply(lineData.totalReturnDis1).divide(totalQty, 2, RoundingMode.FLOOR);
//                        wasteItem.totalReturnDis2 = wasteItem.returnTotalQty.multiply(lineData.totalReturnDis2).divide(totalQty, 2, RoundingMode.FLOOR);
//                        wasteItem.totalReturnDis3 = wasteItem.returnTotalQty.multiply(lineData.totalReturnDis3).divide(totalQty, 2, RoundingMode.FLOOR);
//                        wasteItem.totalReturnTax = wasteItem.returnTotalQty.multiply(lineData.totalReturnTax).divide(totalQty, 2, RoundingMode.FLOOR);
//                        wasteItem.totalReturnCharge = wasteItem.returnTotalQty.multiply(lineData.totalReturnCharge).divide(totalQty, 2, RoundingMode.FLOOR);
//
//                        this.callReturnLineItemDataWithPromo.add(wasteItem);
//                    }
//                }
//            }
//        }
//    }
//
//
//    private long getConcatId(int productId, int saleId , int returnProductTypeId)
//    {
//        return Long.valueOf(productId + "" + saleId + "" + returnProductTypeId);
//    }
//
//    private Map<Integer, DiscountCallReturnData> seperateByReferenceId()
//    {
//        Map<Integer, DiscountCallReturnData> seperatedItems = new HashMap<>();
//
//        for(DiscountCallReturnLineData lineData : this.callReturnLineItemData.values())
//        {
//            if(lineData.returnTotalQty.compareTo(BigDecimal.ZERO) == 1) {
//
//                if (!seperatedItems.containsKey(lineData.referenceId)) {
//
//                    DiscountCallReturnData returnData = new DiscountCallReturnData();
//                    returnData.callReturnLineItemData = new HashMap<>();
//
//                    returnData.customerId = lineData.customerId;
//                    returnData.returnRefId = lineData.referenceId;
//                    returnData.returnRefNo = lineData.referenceNo;
//                    returnData.returnRefDate = lineData.referenceDate;
//                    returnData.dealerId = lineData.dealerId;
//                    returnData.callUniqueId = this.callUniqueId;
//                    returnData.returnType = this.returnType;
//                    returnData.returnUniqueId = this.returnUniqueId;
//                    returnData.returnReasonId = lineData.returnReasonId;
//
//                    returnData.callReturnLineItemData.put(lineData.returnLineUniqueId, lineData.getCopy());
//                    seperatedItems.put(lineData.referenceId, returnData);
//
//                } else {
//                    seperatedItems.get(lineData.referenceId).callReturnLineItemData.put(lineData.returnLineUniqueId, lineData.getCopy());
//                }
//            }
//        }
//
//        return seperatedItems;
//    }

//    private DiscountCallReturnData getTotalOfWellAndWaste(DiscountCallReturnData returnData)
//    {
//        DiscountCallReturnData newCallReturn = returnData.getCopy();
//
//
//        ArrayList<DiscountCallReturnLineData> lineDataList = new ArrayList<>(newCallReturn.callReturnLineItemData.values());
//        Set<Integer> duplicatedProducts = new HashSet<>();
//
//        for(int i = 0; i<lineDataList.size() - 1; i++)
//        {
//            for(int j = i +1; j<lineDataList.size(); j++)
//            {
//                if(lineDataList.get(i).productId == lineDataList.get(j).productId)
//                {
//                    duplicatedProducts.add(lineDataList.get(i).productId);
//                    break;
//                }
//            }
//        }
//
//        for(Integer productId : duplicatedProducts)
//        {
//            DiscountCallReturnLineData sumLine = newCallReturn.getCallReturnLineData(productId, returnData.returnRefId, DiscountCallReturnLineData.ReturnProductTypeEnum.INTACT.value());
//            sumLine.returnTotalQty = sumLine.returnTotalQty
//                    .add(returnData.getCallReturnLineData(productId, returnData.returnRefId, DiscountCallReturnLineData.ReturnProductTypeEnum.DEFECTIVE.value()).returnTotalQty);
//
//            newCallReturn.getCallReturnLineData(productId, returnData.returnRefId, DiscountCallReturnLineData.ReturnProductTypeEnum.DEFECTIVE.value()).returnTotalQty = BigDecimal.ZERO;
//        }
//
//        return newCallReturn;
//    }

//    public DiscountCallReturnData getCopy()
//    {
//        DiscountCallReturnData copyReturn = new DiscountCallReturnData();
//
//        copyReturn.callUniqueId = callUniqueId;
//        copyReturn.returnUniqueId = returnUniqueId;
//        copyReturn.returnTypeText = returnTypeText;
//        copyReturn.returnType = returnType;
//        copyReturn.distId = distId;
//        copyReturn.returnRequestId = returnRequestId;
//        copyReturn.returnRequestDate = returnRequestDate;
//        copyReturn.returnRequestNo = returnRequestNo;
//        copyReturn.totalRequestAmount = totalRequestAmount;
//        copyReturn.totalRequestTax = totalRequestTax;
//        copyReturn.totalRequestCharge = totalRequestCharge;
//        copyReturn.totalRequestDiscount = totalRequestDiscount;
//        copyReturn.totalRequestNetAmount = totalRequestNetAmount;
//        copyReturn.totalReturnAmount = totalReturnAmount;
//        copyReturn.totalReturnDiscount = totalReturnDiscount;
//        copyReturn.totalReturnAddAmount = totalReturnAddAmount;
//        copyReturn.totalReturnCharge = totalReturnCharge;
//        copyReturn.totalReturnTax = totalReturnTax;
//        copyReturn.totalReturnNetAmount = totalReturnNetAmount;
//        copyReturn.visitStatusId = visitStatusId;
//        copyReturn.visitStatusText = visitStatusText;
//        copyReturn.visitRejectReason = visitRejectReason;
//        copyReturn.localPaperNo = localPaperNo;
//        copyReturn.customerId = customerId;
//        copyReturn.returnRefId = returnRefId;
//        copyReturn.returnRefNo = returnRefNo;
//        copyReturn.returnRefDate = returnRefDate;
//        copyReturn.returnReason = returnReason;
//        copyReturn.returnReasonId = returnReasonId;
//        copyReturn.dealerId = dealerId;
//        copyReturn.comment = comment;
//        copyReturn.IsNew = IsNew;
//        copyReturn.DCRef = DCRef;
//        copyReturn.SaleOfficeRef = SaleOfficeRef;
//
//        copyReturn.callReturnLineItemData = new HashMap<>();
//        for(DiscountCallReturnLineData lineData : this.callReturnLineItemData.values())
//        {
//            copyReturn.callReturnLineItemData.put(lineData.returnLineUniqueId, lineData.getCopy());
//        }
//
//        return copyReturn;
//    }
    //endregion Action methods

}
