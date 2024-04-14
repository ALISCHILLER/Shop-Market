package varanegar.com.discountcalculatorlib.handler;

import android.content.Context;

import com.google.gson.Gson;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.network.gson.VaranegarGsonBuilder;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;
import varanegar.com.discountcalculatorlib.R;
import varanegar.com.discountcalculatorlib.api.CalcOnlinePromotionAPI;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerOldInvoiceDetailDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.DiscountEvcPrizeDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCHeaderSDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCItemSDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCItemStatutesSDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCTempReturnItemSummaryFinalSDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCHeaderVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCItemStatutesVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCItemVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCTempReturnItemSummaryFinalVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.handler.sds.PromotionDoEVCBySpecialValueV3;
import varanegar.com.discountcalculatorlib.handler.sds.PromotionDoEVCByStatuteTableSDSV3;
import varanegar.com.discountcalculatorlib.handler.sds.PromotionFillEVCSDSV3;
import varanegar.com.discountcalculatorlib.handler.vnlite.PromotionCalcChargeTaxVnLiteV3;
import varanegar.com.discountcalculatorlib.handler.vnlite.PromotionDecreaseEVCItemByQtyVnLiteV3;
import varanegar.com.discountcalculatorlib.handler.vnlite.PromotionDoEVCBySpecialValueVnLiteV3;
import varanegar.com.discountcalculatorlib.handler.vnlite.PromotionDoEVCByStatuteTableVnLiteV3;
import varanegar.com.discountcalculatorlib.handler.vnlite.PromotionFillEVCVnLiteV3;
import varanegar.com.discountcalculatorlib.handler.vnlite.PromotionGetRetExtraValueVnLiteV3;
import varanegar.com.discountcalculatorlib.helper.BaseUnitData;
import varanegar.com.discountcalculatorlib.helper.DiscountDbHelper;
import varanegar.com.discountcalculatorlib.helper.VasHelperMethodsData;
import varanegar.com.discountcalculatorlib.utility.DiscountException;
import varanegar.com.discountcalculatorlib.utility.GlobalVariables;
import varanegar.com.discountcalculatorlib.utility.enumerations.BackOfficeType;
import varanegar.com.discountcalculatorlib.utility.enumerations.EVCType;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallOrderData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallOrderDataOnline;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallOrderLineData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallOrderLineDataOnline;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallReturnData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallReturnLineData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountOrderPrizeViewModel;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountOutputOnline;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountPrizeViewModel;
import varanegar.com.discountcalculatorlib.viewmodel.InvoiceLineQtyModelData;
import varanegar.com.discountcalculatorlib.viewmodel.PreSaleEvcHeaderViewModel;
import varanegar.com.discountcalculatorlib.viewmodel.ProductUnitModelData;

import static varanegar.com.discountcalculatorlib.Global.hasChoicePrize;
import static varanegar.com.discountcalculatorlib.Global.orderPrize;
import static varanegar.com.discountcalculatorlib.Global.totallyError;
import static varanegar.com.vdmclient.call.GlobalVariable.usanceDay;

public class PromotionHandlerV3 {


    public PromotionHandlerV3() {
    }


    public static DiscountCallOrderData calcPromotion(final List<Integer> SelIds,
                                                      DiscountCallOrderData discountCallOrderData,
                                                      EVCType evcType,
                                                      Context context,
                                                      List<ProductUnitModelData> productUnitModelData
                                                      ) throws DiscountException, InterruptedException {
//        try {
        if (GlobalVariables.getBackOffice().equals(BackOfficeType.VARANEGAR)) {
            if (GlobalVariables.isCalcOnline()) {
                discountCallOrderData = PromotionHandlerV3.calcPromotionOnlineSDS(SelIds,
                        discountCallOrderData, context,productUnitModelData);
            }  else
                discountCallOrderData = PromotionHandlerV3.calcPromotionSDS(discountCallOrderData, evcType);
        } else {
            discountCallOrderData = PromotionHandlerV3.calcPromotionVnLite(discountCallOrderData, evcType);
        }
        return discountCallOrderData;
//        } catch (DiscountException dex) {
//            throw dex;
//        } catch (Exception ex) {
//            throw new DiscountException(ex.getMessage());
//        }
    }

    public static List<DiscountCallReturnLineData> calcPromotion(DiscountCallReturnData discountCallReturnData, EVCType evcType) throws DiscountException {
        try {
            if (GlobalVariables.getBackOffice().equals(BackOfficeType.VARANEGAR)) {
                return PromotionHandlerV3.calcPromotionSDS(discountCallReturnData, evcType);
            } else {
                return PromotionHandlerV3.calcPromotionVnLite(discountCallReturnData, evcType);
            }
        } catch (DiscountException dex) {
            dex.printStackTrace();
            throw dex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DiscountException(ex.getMessage());
        }
    }

    public static List<DiscountCallReturnLineData> calcNewPromotion(DiscountCallReturnData discountCallReturnData, EVCType evcType) throws DiscountException {
        try {
            if (GlobalVariables.getBackOffice().equals(BackOfficeType.VARANEGAR)) {
                return PromotionHandlerV3.calcNewPromotionSDS(discountCallReturnData, evcType);
            } else {
                return PromotionHandlerV3.calcPromotionVnLite(discountCallReturnData, evcType);
            }
        } catch (DiscountException dex) {
            dex.printStackTrace();
            throw dex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DiscountException(ex.getMessage());
        }
    }
    /* ***************************************************************************
     * VnLite
     * **************************************************************************** */

    public static DiscountCallOrderData calcPromotionVnLite(DiscountCallOrderData discountCallOrderData, EVCType evcType) throws DiscountException {
        DiscountDbHelper.getDb();
        if (discountCallOrderData.callOrderLineItemData != null && discountCallOrderData.callOrderLineItemData.size() > 0) {
            String evcId = PromotionFillEVCVnLiteV3.fillEVCV(discountCallOrderData, 1);
            calcPromoIntralVnLite(evcId, discountCallOrderData.callUniqueId, discountCallOrderData.saleDate, evcType);
            discountCallOrderData = EVCItemVnLiteDBAdapter.getInstance().fillCallLineWithPromo(evcId, discountCallOrderData);
            discountCallOrderData.callOrderEvcItemStatutes = EVCItemStatutesVnLiteDBAdapter.getInstance().getEvcItemStatuteDataToSendVnLite(evcId);
        }
        return discountCallOrderData;
    }

    public static List<DiscountCallReturnLineData> calcPromotionVnLite(DiscountCallReturnData discountCallReturnData, EVCType evcType) throws DiscountException {
        List<DiscountCallReturnLineData> result = null;

        if (discountCallReturnData.callReturnLineItemData != null && discountCallReturnData.callReturnLineItemData.size() > 0) {
            String evcId1, evcId2;
            String[] temp = PromotionFillEVCVnLiteV3.fillEVCV(discountCallReturnData, EVCType.SELLRETURN).split(":");
            evcId1 = temp[0];
            evcId2 = temp[1];

            PromotionDecreaseEVCItemByQtyVnLiteV3.DecreaseEVCItem(discountCallReturnData, evcId1, evcId2);
            PromotionGetRetExtraValueVnLiteV3.getRetExtraValue(evcId1, evcId2, discountCallReturnData.callUniqueId, Integer.parseInt(discountCallReturnData.returnRefNo), discountCallReturnData.returnRefDate);
            result = EVCTempReturnItemSummaryFinalVnLiteDBAdapter.getInstance().getCallWithPromo(evcId2, discountCallReturnData);

        }
        return result;
    }

    public static void calcPromoIntralVnLite(String evcId, String callUniqueId, String saleDate, EVCType evcType) throws DiscountException {
        int disType = EVCHeaderVnLiteDBAdapter.getInstance().getDisType(evcId);
        if (disType == 10)
            PromotionGetRetExtraValueVnLiteV3.resetEVC(evcId);
        else if (disType == 1) {
            PromotionDoEVCByStatuteTableVnLiteV3.doEVCByStatuteTable(evcId, callUniqueId, evcType.value(), saleDate);
        } else if (disType == 30)
            PromotionDoEVCByStatuteTableVnLiteV3.doEVCByStatuteTable(evcId, callUniqueId, evcType.value(), saleDate);
        else if (disType == 40) {
            PromotionDoEVCByStatuteTableVnLiteV3.doEVCByStatuteTable(evcId, callUniqueId, evcType.value(), saleDate);
            PromotionDoEVCBySpecialValueVnLiteV3.doEVCBySpecialValue(evcId);
        }
        if (disType == 1) /*براي فاكتور آزاد تخفيفات و جوايز حساب نشود*/
            PromotionCalcChargeTaxVnLiteV3.calcChargeTax(evcId);

    }

    /**
     * Api پیش نمایش
     * تخفیف جوایز
     * برای برنامه توزیه یا موزع
     *
     * @param orderPrizeList
     * @param discountCallOrderData
     * @param context
     * @return
     * @throws InterruptedException
     */
    public static DiscountCallOrderData     distCalcPromotionOnlineSDS(
            final List<DiscountOrderPrizeViewModel> orderPrizeList,
            final DiscountCallOrderData discountCallOrderData,
            final Context context,
            String SalePDate,
            String  DocPDate,
            String  ZTERM,
            String saleNoSDS,
            ArrayList<DiscountCallOrderLineData> orderProduct,
            List<InvoiceLineQtyModelData> invoiceLineQtyModelData,
            List<ProductUnitModelData> productUnitModelData
            ) throws InterruptedException {
        final String[] errorMessage = {null};
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Timber.d("calcPromotionOnline : Thread started ");
                CalcOnlinePromotionAPI calcPromotionAPI = new CalcOnlinePromotionAPI();
                PreSaleEvcHeaderViewModel onlineData = discountCallOrderData.toOnlineDist(orderPrizeList);
                try {
                    // do for Third Party SAP
                    if (GlobalVariables.getIsThirdParty()) {
                        int custRefLength = onlineData.CustRef.length();
                        StringBuilder finalCustRef = new StringBuilder();
                        for (int i = 0; i < 10 - custRefLength; i++) {
                            finalCustRef.append("0");
                        }
                        finalCustRef.append(onlineData.CustRef);
                        onlineData.CustRef = finalCustRef.toString();
                        for (DiscountCallOrderLineDataOnline item : onlineData.PreSaleEvcDetails) {
                            int goodsRefLength = item.GoodsRef.length();
                            StringBuilder finalGoodsRef = new StringBuilder();
                            for (int i = 0; i < 18 - goodsRefLength; i++) {
                                finalGoodsRef.append("0");
                            }
                            finalGoodsRef.append(item.GoodsRef);
                            item.GoodsRef = finalGoodsRef.toString();
                        }
                    }
                    totallyError = "";
                    orderPrize = new ArrayList<>();
                    hasChoicePrize = false;
                    onlineData.SalePDate=SalePDate;
                    onlineData.DocPDate=DocPDate;
                    onlineData.ZTERM=ZTERM;
                    onlineData.DELIVERYID=saleNoSDS;



                    double d = 0;
                    if (invoiceLineQtyModelData != null && productUnitModelData != null)
                        for (int i = 0; i < onlineData.PreSaleEvcDetails.size(); i++) {
                            for (int j = 0; j < invoiceLineQtyModelData.size(); j++) {
                                if (onlineData.PreSaleEvcDetails.get(i).OrderLineId.equals(invoiceLineQtyModelData.get(j).OrderLineUniqueId)) {
                                    for (int x = 0; x < productUnitModelData.size(); x++) {
                                        if (productUnitModelData.get(x).ProductId.equals(invoiceLineQtyModelData.get(j).UnitUniqueId)) {
                                            if (productUnitModelData.get(x).UnitName.equals(invoiceLineQtyModelData.get(j).Vrkme)) {

                                                d = onlineData.
                                                        PreSaleEvcDetails.get(i).TotalQty
                                                        .divide(BigDecimal.valueOf(productUnitModelData.get(x).ConvertFactor), RoundingMode.HALF_EVEN).doubleValue();

                                                if (d % 1 == 0) {
                                                    onlineData.PreSaleEvcDetails.get(i).TotalQty =
                                                            BigDecimal.valueOf(d);
                                                    onlineData.PreSaleEvcDetails.get(i).Unit = productUnitModelData.get(x).UnitName;
                                                } else {
                                                    break;
                                                }
                                            }
                                        }

                                    }
                                    if (d % 1 != 0) {
                                        for (int x = 0; x < productUnitModelData.size(); x++) {
                                            if (productUnitModelData.get(x).ProductId.equals(invoiceLineQtyModelData.get(j).UnitUniqueId)) {
                                                if (invoiceLineQtyModelData.get(j).Vrkme.equals("KAR")) {
                                                    if (productUnitModelData.get(x).UnitName.equals("SHL")) {
                                                        d = onlineData.
                                                                PreSaleEvcDetails.get(i).TotalQty
                                                                .divide(BigDecimal.valueOf(productUnitModelData.get(x).ConvertFactor)).doubleValue();
                                                        if (d % 1 == 0) {
                                                            onlineData.PreSaleEvcDetails.get(i).TotalQty =
                                                                    BigDecimal.valueOf(d);
                                                            onlineData.PreSaleEvcDetails.get(i).Unit = productUnitModelData.get(x).UnitName;
                                                            break;
                                                        } else {
                                                            onlineData.PreSaleEvcDetails.get(i).Unit = "EA";
                                                        }
                                                    } else if (productUnitModelData.get(x).UnitName.equals("EA")) {
                                                        onlineData.PreSaleEvcDetails.get(i).Unit = "EA";
                                                    }
                                                } else if (invoiceLineQtyModelData.get(j).Vrkme.equals("SHL")) {
                                                    onlineData.PreSaleEvcDetails.get(i).Unit = "EA";
                                                }
                                            }
                                        }
                                    }


                                }
                            }
                        }

                    if (invoiceLineQtyModelData == null && productUnitModelData == null) {
                        for (int i = 0; i < onlineData.PreSaleEvcDetails.size(); i++) {
                            onlineData.PreSaleEvcDetails.get(i).Unit = "EA";
                        }
                    }
                    Call<DiscountOutputOnline> call = calcPromotionAPI.getDistOnlinePromotion(onlineData,
                            GlobalVariables.getCalcDiscount(),
                            GlobalVariables.getCalcSaleRestriction(),
                            GlobalVariables.getCalcPaymentType()
                    );
                    Response<DiscountOutputOnline> response = call.execute();
                    DiscountOutputOnline result = response.body();
                    Timber.d("online promotion result: ", VaranegarGsonBuilder.build().create().toJson(result));
                    if (result == null) {
                        Timber.d("calcPromotionOnline : Web service finished with result null");
                        return;
                    }
                    if (result.message != null && result.errorCode == 3001) {
                        errorMessage[0] = "3001";
                        hasChoicePrize = true;
                        orderPrize = result.orderPrize;
                    } else if (result.message != null && result.message.length() > 0) {
                        errorMessage[0] = result.message;
                    } else {
                        Timber.d("calcPromotionOnline : Web service finished");
                        Timber.d("calcPromotionOnline : Result contains " + result.items.size() + " items");
                        String paymentId = result.paymentUsanceId == null ? "null" : result.paymentUsanceId.toString();
                        Timber.d("calcPromotionOnline : paymentUsanceId " + paymentId);
                        discountCallOrderData.SetFromOnline(result, discountCallOrderData.callOrderLineItemData);
                    }
                } catch (IOException e) {
                    Timber.e(e);
                    errorMessage[0] = context.getString(R.string.network_access_error);
//                        if (progressDialog!= null && progressDialog.isShowing())
//                            progressDialog.dismiss();
                }
                Timber.d("calcPromotionOnline : Thread finished");
            }
        });
        Timber.d("calcPromotionOnline : Starting web service thread");
        thread.start();
        Timber.d("calcPromotionOnline : Webservice started");
        thread.join();
        Timber.d("calcPromotionOnline : Thread finished");
        if (errorMessage[0] != null)
            throw new DiscountException(errorMessage[0]);
        return discountCallOrderData;
    }

    /* ***************************************************************************
     * SDS
     * **************************************************************************** */
    public static DiscountCallOrderData calcPromotionOnlineSDS(final List<Integer> SelIds,
                                                               final DiscountCallOrderData discountCallOrderData,
                                                               final Context context,
                                                               List<ProductUnitModelData> productUnitModelData
                                                               )
            throws DiscountException, InterruptedException {
        final String[] errorMessage = {null};
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Timber.d("calcPromotionOnline : Thread started ");
                CalcOnlinePromotionAPI calcPromotionAPI = new CalcOnlinePromotionAPI();
                DiscountCallOrderDataOnline onlineData = discountCallOrderData.ToOnline();
                // do for Third Party SAP
                if (GlobalVariables.getIsThirdParty()) {
                    int custRefLength = onlineData.CustRef.length();
                    StringBuilder finalCustRef = new StringBuilder();
                    for (int i = 0; i < 10 - custRefLength; i++) {
                        finalCustRef.append("0");
                    }
                    finalCustRef.append(onlineData.CustRef);
                    onlineData.CustRef = finalCustRef.toString();
                    for (DiscountCallOrderLineDataOnline item : onlineData.PreSaleEvcDetails) {
                        int goodsRefLength = item.GoodsRef.length();
                        StringBuilder finalGoodsRef = new StringBuilder();
                        for (int i = 0; i < 18 - goodsRefLength; i++) {
                            finalGoodsRef.append("0");
                        }
                        finalGoodsRef.append(item.GoodsRef);
                        item.GoodsRef = finalGoodsRef.toString();
                    }
                }


                onlineData.SelIds = SelIds;
                onlineData.OrderDate = DateHelper.toString(new Date(), DateFormat.Date, Locale.US);

                if(onlineData.PaymentUsanceRef!=null && !onlineData.PaymentUsanceRef.isEmpty())
                    onlineData.ZTERM = onlineData.PaymentUsanceRef;
                else
                    onlineData.ZTERM = "PT01";



                if (productUnitModelData!=null)
                    Collections.sort(productUnitModelData, new Comparator<ProductUnitModelData>() {
                        @Override
                        public int compare(ProductUnitModelData o1, ProductUnitModelData o2) {
                            return Double.compare(o1.getConvertFactor(), o2.getConvertFactor());
                        }
                    });

                double d;

                if (productUnitModelData!=null)
                    for (int i=0;i < onlineData.PreSaleEvcDetails.size();i++){
                        String productCode= onlineData.PreSaleEvcDetails.get(i).GoodsRef
                                .replaceAll("^0+", "");
                        for (int j=0;j<productUnitModelData.size();j++){
                            if (productCode.equals(productUnitModelData.get(j).ProductCode)){
                                d = onlineData.
                                        PreSaleEvcDetails.get(i).TotalQty
                                        .divide(BigDecimal.valueOf(productUnitModelData.get(j).ConvertFactor),
                                                RoundingMode.HALF_EVEN).doubleValue();

                                if (p) {
                                    onlineData.PreSaleEvcDetails.get(i).TotalQty =
                                            BigDecimal.valueOf(d);
                                    onlineData.PreSaleEvcDetails.get(i).Unit =
                                            productUnitModelData.get(j).UnitName;
                                }
                            }
                        }
                    }
                else
                    for (int i=0;i < onlineData.PreSaleEvcDetails.size();i++){
                        onlineData.PreSaleEvcDetails.get(i).Unit ="EA";
                    }

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd",Locale.ENGLISH);
                String formattedDate = formatter.format(new Date());

                System.out.println(formattedDate);
                onlineData.DocPDate=formattedDate.replace("/", "");
                onlineData.SalePDate=formattedDate.replace("/", "");
                try {
                    Call<DiscountOutputOnline> call = calcPromotionAPI.getPromotion(onlineData,
                            GlobalVariables.getCalcDiscount(),
                            GlobalVariables.getCalcSaleRestriction(),
                            GlobalVariables.getCalcPaymentType()
                    );
                    Response<DiscountOutputOnline> response = call.execute();
                    DiscountOutputOnline result = response.body();
                    Timber.d("online promotion body: ", VaranegarGsonBuilder.build().create().toJson(onlineData));
                    Timber.d("online promotion result: ", VaranegarGsonBuilder.build().create().toJson(result));
                    if (result == null) {
                        Timber.d("calcPromotionOnline : Web service finished with result null");
                        return;
                    }

                    if (result.usanceDay != 0) {
                        usanceDay = result.usanceDay;
                    }
                    if (result.message != null && result.message.length() > 0) {
                        errorMessage[0] = result.message;
                    } else {
                        Timber.d("calcPromotionOnline : Web service finished");
                        if (result != null) {
                            Timber.d("calcPromotionOnline : Result contains " + result.items.size() + " items");
                            Timber.d("calcPromotionOnline : Result = " + new Gson().toJson(result));
                            String paymentId = result.paymentUsanceId == null ? "null" : result.paymentUsanceId.toString();
                            Timber.d("calcPromotionOnline : paymentUsanceId " + paymentId);
                            if (result.usanceDay != 0)
                                discountCallOrderData.UsanceDay = result.usanceDay;
                            if (result.paymentUsanceRef != null)
                                discountCallOrderData.PaymentUsanceRef = result.paymentUsanceRef;
                            discountCallOrderData.SetFromOnline(result, discountCallOrderData.callOrderLineItemData);
                        } else
                            Timber.d("calcPromotionOnline : result is null");
                    }
//                        if (progressDialog!= null && progressDialog.isShowing())
//                            progressDialog.dismiss();
                } catch (IOException e) {
                    Timber.e(e);
                    errorMessage[0] = context.getString(R.string.network_access_error);
//                        if (progressDialog!= null && progressDialog.isShowing())
//                            progressDialog.dismiss();
                }
                Timber.d("calcPromotionOnline : Thread finished");
            }
        });
        Timber.d("calcPromotionOnline : Starting web service thread");
        thread.start();
        Timber.d("calcPromotionOnline : Webservice started");
        thread.join();
        Timber.d("calcPromotionOnline : Thread finished");
        if (errorMessage[0] != null)
            throw new DiscountException(errorMessage[0]);
        return discountCallOrderData;
//        } catch (InterruptedException e) {
//            throw new DiscountException(context.getString(R.string.error_calculating_discount));
//        }
    }

    private static DiscountCallOrderData calcPromotionSDS(DiscountCallOrderData discountCallOrderData, EVCType evcType) throws DiscountException {
        DiscountDbHelper.getDb();

        if (discountCallOrderData.callOrderLineItemData != null && discountCallOrderData.callOrderLineItemData.size() > 0) {
            String evcId = varanegar.com.discountcalculatorlib.handler.sds.PromotionFillEVCSDSV3.fillEVC(discountCallOrderData, evcType);
            calcPromoIntral(evcId, discountCallOrderData.callUniqueId, discountCallOrderData.orderId, evcType.value(), discountCallOrderData.saleDate, discountCallOrderData.OrderType + "", discountCallOrderData.saleId, discountCallOrderData.orderNo, discountCallOrderData.DCRef);
            EVCItemSDSDBAdapter.getInstance().updateProductPriceOnEVCItem(discountCallOrderData, evcId);
            discountCallOrderData = EVCHeaderSDSDBAdapter.getInstance().fillCallWithPromo(evcId, discountCallOrderData);

            Timber.d("calcPromotionSDS : getEvcItemStatuteDataToSend started ");
            EVCItemSDSDBAdapter.getInstance().updateByOrderPrize();

            discountCallOrderData.callOrderEvcItemStatutes = EVCItemStatutesSDSDBAdapter.getInstance().getEvcItemStatuteDataToSend(evcId);

            discountCallOrderData.discountEvcPrize = DiscountEvcPrizeDBAdapter.GetEvcPrizesByOrder(evcId);
        }
        //DiscountDbHelper.close();
        return discountCallOrderData;

    }

    public static DiscountCallOrderData DoCalcEVC(DiscountCallOrderData discountCallOrderData, EVCType evcType) throws DiscountException {
        DiscountDbHelper.getDb();

        if (discountCallOrderData.callOrderLineItemData != null && discountCallOrderData.callOrderLineItemData.size() > 0) {
            String evcId = varanegar.com.discountcalculatorlib.handler.sds.PromotionFillEVCSDSV3.fillEVC(discountCallOrderData, evcType);
            EVCItemSDSDBAdapter.getInstance().updateProductPriceOnEVCItem(discountCallOrderData, evcId);
            calcPromoIntral(evcId, discountCallOrderData.callUniqueId, discountCallOrderData.orderId, evcType.value(), discountCallOrderData.saleDate, discountCallOrderData.OrderType + "", discountCallOrderData.saleId, discountCallOrderData.orderNo, discountCallOrderData.DCRef);
            discountCallOrderData.evcRef = evcId;
        }
        //DiscountDbHelper.close();
        return discountCallOrderData;
    }

    public static DiscountCallOrderData DoReCalcEVC(DiscountCallOrderData discountCallOrderData, String evcId, EVCType evcType, List<DiscountPrizeViewModel> discountPrizeViewModels) throws DiscountException {
        DiscountDbHelper.getDb();

        EVCItemSDSDBAdapter.getInstance().DeletePrize(evcId);
        EVCItemSDSDBAdapter.getInstance().UpdateToZero(evcId);
        DiscountEvcPrizeDBAdapter.getInstance().resetEvcPrize(evcId, discountCallOrderData.orderId, discountPrizeViewModels);

        int disType = EVCHeaderSDSDBAdapter.getInstance().getDisType(evcId);
        if (disType == 3) {
            EVCHeaderSDSDBAdapter.getInstance().UpdateFromOldInvoice(evcId, discountCallOrderData.saleId);
        } else {
            EVCHeaderSDSDBAdapter.getInstance().UpdateToZero(evcId);
        }

        EVCItemStatutesSDSDBAdapter.getInstance().deleteUnAffectedEVCItemStatutes(evcId);
        PromotionFillEVCSDSV3.clearTempEVCData();

        DiscountEvcPrizeDBAdapter.getInstance().updateQtyUnit(evcId);
        calcPromoIntral(evcId, discountCallOrderData.callUniqueId, discountCallOrderData.orderId, evcType.value(), discountCallOrderData.saleDate, discountCallOrderData.OrderType + "", discountCallOrderData.saleId, discountCallOrderData.orderNo, discountCallOrderData.DCRef);

        discountCallOrderData = EVCHeaderSDSDBAdapter.getInstance().fillCallWithPromo(evcId, discountCallOrderData);
        Timber.d("DoReCalcEVC : getEvcItemStatuteDataToSend started ");
        discountCallOrderData.callOrderEvcItemStatutes = EVCItemStatutesSDSDBAdapter.getInstance().getEvcItemStatuteDataToSend(evcId);

        if (discountCallOrderData.totalOrderAmount != null) //توزیع باشد
            for (DiscountCallOrderLineData item : discountCallOrderData.callLineItemDataWithPromo) {
                if (item.requestTotalQty != null && item.invoiceTotalQty.compareTo(item.requestTotalQty) > 0) {
                    throw new DiscountException("سرجمع کالاهای جایزه از مقدار حواله بیشتر خواهد شد، امکان ثبت فاکتور وجود ندارد! ");
                }
            }

        //DiscountDbHelper.close();
        return discountCallOrderData;

    }

    private static List<DiscountCallReturnLineData> calcPromotionSDS(DiscountCallReturnData discountCallReturnData, EVCType evcType) throws DiscountException {
        List<DiscountCallReturnLineData> result = null;
        if (discountCallReturnData.callReturnLineItemData != null && discountCallReturnData.callReturnLineItemData.size() > 0) {
            String evcId1, evcId2;
            String[] temp = varanegar.com.discountcalculatorlib.handler.sds.PromotionFillEVCSDSV3.fillEVC(discountCallReturnData, evcType, false).split(":");
            evcId1 = temp[0];
            evcId2 = temp[1];

            varanegar.com.discountcalculatorlib.handler.sds.PromotionDecreaseEVCItemByQtyV3.DecreaseEVCItem(discountCallReturnData, evcId1, evcId2);

            //TODO Asal orderId, orderNo ????
            //returndate = discountCallReturnData.returnRefDate;

            varanegar.com.discountcalculatorlib.handler.sds.PromotionGetRetExtraValueV3.getRetExtraValue(evcId1, evcId2, discountCallReturnData.callUniqueId, 0,
                    EVCHeaderSDSDBAdapter.getInstance().getOldInvoiceReturnDate(discountCallReturnData.returnRefId), discountCallReturnData.returnRefId, 0, discountCallReturnData.DCRef);
            result = EVCTempReturnItemSummaryFinalSDSDBAdapter.getInstance().getCallWithPromo(evcId2, discountCallReturnData);

        }

        return result;
    }

    /*sle.usp_CreateRetSaleByPercent*/
    private static List<DiscountCallReturnLineData> calcNewPromotionSDS(DiscountCallReturnData discountCallReturnData, EVCType evcType) throws DiscountException {
        List<DiscountCallReturnLineData> result = null;
        if (discountCallReturnData.callReturnLineItemData != null && discountCallReturnData.callReturnLineItemData.size() > 0) {
           /*
            String evcId1, evcId2;
            String[] temp = varanegar.com.discountcalculatorlib.handler.sds.PromotionFillEVCSDSV3.fillEVC(discountCallReturnData, evcType, true).split(":");
            evcId1 = temp[0];
            evcId2 = temp[1];


            varanegar.com.discountcalculatorlib.handler.sds.PromotionDecreaseEVCItemByQtyV3.DecreaseEVCItem(discountCallReturnData, evcId1, evcId2);
            */
            DiscountCustomerOldInvoiceDetailDBAdapter.getInstance().fillTempSaleItm(discountCallReturnData.returnRefId);
            DiscountCustomerOldInvoiceDetailDBAdapter.getInstance().fillTempSaleDetail();

            EVCItemSDSDBAdapter.getInstance().fillTempRetItm(discountCallReturnData);
            EVCItemSDSDBAdapter.getInstance().fillRetQtySplit();

            DiscountCustomerOldInvoiceDetailDBAdapter.getInstance().updateTempSaleItm();
            DiscountCustomerOldInvoiceDetailDBAdapter.getInstance().updateTempSaleItm2();
            result = EVCTempReturnItemSummaryFinalSDSDBAdapter.getInstance().getNewCallWithPromo(discountCallReturnData);

        }
        return result;
    }

    /*SLE.usp_DoEVC*/
    public static void calcPromoIntral(String evcId, String uniqueId, int orderId, int evcType, String orderDate, String orderTypeId, int saleId, int orderNo, int dcRef) throws DiscountException {
        Timber.d("calcPromotionSDS : calcPromoIntral started ");
        int disType = EVCHeaderSDSDBAdapter.getInstance().getDisType(evcId);
        Timber.d("calcPromotionSDS : disType : " + disType);
        if (disType == 2) {
            PromotionDoEVCByStatuteTableSDSV3.doEVCByStatuteTable(evcId, uniqueId, orderId, disType, orderDate, evcType, orderTypeId, saleId, orderNo, dcRef);
        } else if (disType == 3) {
            PromotionDoEVCBySpecialValueV3.doEVCBySpecialValue(evcId);
        } else if (disType == 4) {
            PromotionDoEVCBySpecialValueV3.doEVCBySpecialValue(evcId);
            PromotionDoEVCByStatuteTableSDSV3.doEVCByStatuteTable(evcId, uniqueId, orderId, disType, orderDate, evcType, orderTypeId, saleId, orderNo, dcRef);
        }

        if (evcType != 2) {
            varanegar.com.discountcalculatorlib.handler.sds.PromotionCalcChargeTaxSDSV3.calcChargeTax(evcId);
        }
    }

    /* *************************************************************************** */

}
