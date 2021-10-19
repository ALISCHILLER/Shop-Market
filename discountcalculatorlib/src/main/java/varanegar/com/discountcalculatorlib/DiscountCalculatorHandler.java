package varanegar.com.discountcalculatorlib;

import android.app.Activity;
import android.content.Context;
import android.os.RemoteException;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import varanegar.com.discountcalculatorlib.callback.DiscountHandlerOrderCallback;
import varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.DiscountEvcPrizeDBAdapter;
import varanegar.com.discountcalculatorlib.handler.FillInitDataHandlerV3;
import varanegar.com.discountcalculatorlib.handler.IDiscountInitializeHandlerV3;
import varanegar.com.discountcalculatorlib.handler.PromotionHandlerV3;
import varanegar.com.discountcalculatorlib.utility.DiscountException;
import varanegar.com.discountcalculatorlib.utility.GlobalVariables;
import varanegar.com.discountcalculatorlib.utility.enumerations.BackOfficeType;
import varanegar.com.discountcalculatorlib.utility.enumerations.EVCType;
import varanegar.com.discountcalculatorlib.viewmodel.BeforChangeQtyViewModel;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallOrderData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallOrderLineData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallReturnData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallReturnLineData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountOrderPrizeViewModel;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountPrizeViewModel;
import varanegar.com.merge.BaseVdmCalculator;
import varanegar.com.vdmclient.NoConnectionException;
import varanegar.com.vdmclient.OnBind;
import varanegar.com.vdmclient.VdmCalculator;
import varanegar.com.vdmclient.VdmClient;
import varanegar.com.vdmclient.VdmInitializer;
import varanegar.com.vdmclient.call.CalcData;

/**
 * Created by A.Razavi on 11/14/2017.
 */

public class DiscountCalculatorHandler {


    /***************************************************/
    public static void init(IDiscountInitializeHandlerV3 initializeHandler, int orderId, ArrayList<BeforChangeQtyViewModel> beforeChange) {
        //DiscountDbHelper.init(initializeHandler.getContext());
        setGlobalVariables(initializeHandler);
        try {
            FillInitDataHandlerV3.initQuery(initializeHandler);
        } catch (Exception e) {

        }
        fillTourProductInitData(initializeHandler, beforeChange);
        fillOrderPrizeInitData(initializeHandler, orderId);
        fillOldInvoiceInitData(initializeHandler);
        fillNewCustomerInitData(initializeHandler);
    }

    public static void fillInitDataIfEmpty(IDiscountInitializeHandlerV3 initializeHandler) {
        if (initializeHandler != null) {
            if ((GlobalVariables.getBackOffice().equals(BackOfficeType.VARANEGAR) &&
                    DiscountDBAdapter.getInstance().getCount() == 0) ||
                    (GlobalVariables.getBackOffice().equals(BackOfficeType.RASTAK) &&
                            DiscountVnLiteDBAdapter.getInstance().getCount() == 0)
                    )
                fillInitData(initializeHandler);
        }
    }

    public static void fillInitData(IDiscountInitializeHandlerV3 initializeHandler, @Nullable VdmInitializer.InitCallback callback) {
        FillInitDataHandlerV3.fillInitData(initializeHandler, callback);
    }

    public static void fillInitData(IDiscountInitializeHandlerV3 initializeHandler) {
        FillInitDataHandlerV3.fillInitData(initializeHandler);
    }

    public static void fillCustomerInitData(IDiscountInitializeHandlerV3 initializeHandler) {
        FillInitDataHandlerV3.fillCustomerInitData(initializeHandler, false);
    }

    public static void fillProductInitData(IDiscountInitializeHandlerV3 initializeHandler) {
        FillInitDataHandlerV3.fillProductInitData(initializeHandler, false);
    }

    public static void fillTourProductInitData(IDiscountInitializeHandlerV3 initializeHandler, ArrayList<BeforChangeQtyViewModel> beforeChange) {
        if (initializeHandler != null)
            FillInitDataHandlerV3.fillTourProductInitData(initializeHandler, false, beforeChange);
    }

    public static void fillNewCustomerInitData(IDiscountInitializeHandlerV3 initializeHandler) {
        if (initializeHandler != null)
            FillInitDataHandlerV3.fillNewCustomerInitData(initializeHandler, false);
    }

    public static void fillOrderPrizeInitData(IDiscountInitializeHandlerV3 initializeHandler, int orderId) {
        if (initializeHandler != null) {
            FillInitDataHandlerV3.fillOrderPrizeInitData(initializeHandler, false, orderId);
        }
    }

    public static void fillOldInvoiceInitData(IDiscountInitializeHandlerV3 initializeHandler) {
        if (initializeHandler != null) {
            FillInitDataHandlerV3.fillOldInvoiceInitData(initializeHandler, false);
        }
    }

    private static void setGlobalVariables(IDiscountInitializeHandlerV3 initializeHandler) {
        if (initializeHandler != null) {
            GlobalVariables.setDiscountInitializeHandler(initializeHandler);
            GlobalVariables.setDcRef(initializeHandler.getDcRef());
            GlobalVariables.setDecimalDigits(initializeHandler.getDigtsAfterDecimalDigits());
            GlobalVariables.setDigtsAfterDecimalForCPrice(initializeHandler.getDigtsAfterDecimalForCPrice());
            GlobalVariables.setPrizeCalcType(initializeHandler.getPrizeCalcType());
            GlobalVariables.setRoundType(initializeHandler.getRoundType());
            GlobalVariables.setBackOffice(initializeHandler.getBackOffice());
            GlobalVariables.setBackOfficeVersion(initializeHandler.getBackOfficeVersion());
            GlobalVariables.setDiscountDbPath(initializeHandler.getDiscountDbPath());
            GlobalVariables.setApplicationType(initializeHandler.getApplicationType());
            GlobalVariables.setCalcOnline(initializeHandler.getCalcOnLine());
            //GlobalVariables.setServerUrl(initializeHandler.getServerUrl());
            GlobalVariables.setOwnerKeys(initializeHandler.getOwnerKeys());
        }
    }

    /***************************************************/
    public static void setOnlineOptions(String url,  boolean calcDiscount,boolean calcSaleRestriction ,boolean calcPaymentType) {
        GlobalVariables.setServerUrl(url,calcDiscount,calcSaleRestriction,calcPaymentType );

    }


    public static void calcPromotion(final DiscountCallOrderData discountCallOrderData, final int evcTypeCode, final DiscountHandlerOrderCallback callback) {
        final VdmClient client = new VdmClient(GlobalVariables.getDiscountInitializeHandler().getContext(), GlobalVariables.getDiscountInitializeHandler().getAppId());
        client.bind(new OnBind() {
            @Override
            public void connected() {
                BaseVdmCalculator vdmCalculator = new BaseVdmCalculator(client, discountCallOrderData);
                try {
                    vdmCalculator.calc(new VdmCalculator.CalcCallBack() {
                        @Override
                        public void onSuccess(CalcData calcData) {
                            callback.onSuccess(DiscountCallOrderData.fromCalcData(calcData));
                        }

                        @Override
                        public void onFailure(String error) {
                            callback.onFailure(error);
                        }
                    }, evcTypeCode);
                } catch (RemoteException e) {
                    callback.onFailure(GlobalVariables.getDiscountInitializeHandler().getContext().getString(R.string.connection_to_discount_module_failed));
                } catch (NoConnectionException e) {
                    callback.onFailure(GlobalVariables.getDiscountInitializeHandler().getContext().getString(R.string.vdm_service_is_not_available));
                } catch (IOException e) {
                    callback.onFailure(GlobalVariables.getDiscountInitializeHandler().getContext().getString(R.string.connection_to_discount_module_failed));
                }
            }

            @Override
            public void disConnected() {
                callback.onFailure(GlobalVariables.getDiscountInitializeHandler().getContext().getString(R.string.connection_to_discount_module_failed));
            }
        });
    }

    public static DiscountCallOrderData calcPromotion(final List<Integer> SelIds, DiscountCallOrderData discountCallOrderData, int evcTypeCode, Context context) throws DiscountException, InterruptedException {
        return PromotionHandlerV3.calcPromotion(SelIds, discountCallOrderData, EVCType.getByCode(evcTypeCode), context);
    }

    public static DiscountCallOrderData DoCalcEVC(DiscountCallOrderData callOrderData, int evcTypeCode) throws DiscountException {
        return PromotionHandlerV3.DoCalcEVC(callOrderData, EVCType.getByCode(evcTypeCode));
    }

    public static DiscountCallOrderData DoReCalcEVC(DiscountCallOrderData callOrderData, String evcId, int evcTypeCode, List<DiscountPrizeViewModel> discountPrizeViewModels ) throws DiscountException {
        return PromotionHandlerV3.DoReCalcEVC(callOrderData, evcId, EVCType.getByCode(evcTypeCode), discountPrizeViewModels );
    }
    public static DiscountCallOrderData DoReCalcEVC(DiscountCallOrderData callOrderData, String evcId, int evcTypeCode ) throws DiscountException {
        return PromotionHandlerV3.DoReCalcEVC(callOrderData, evcId, EVCType.getByCode(evcTypeCode), new ArrayList<DiscountPrizeViewModel>() );
    }

    public static void updateDiscountEvcPrize(ArrayList<DiscountOrderPrizeViewModel> list, int discountRef, int orderDiscountRef, int orderRef, String evcId) throws DiscountException {
        DiscountEvcPrizeDBAdapter.getInstance().updateDiscountEvcPrize(list, discountRef, orderDiscountRef, orderRef, evcId);
    }

    public static ArrayList<DiscountOrderPrizeViewModel> GetAllEvcPrizes() throws DiscountException {
        return DiscountEvcPrizeDBAdapter.getInstance().GetAllEvcPrizes();
    }

    public static List<DiscountCallReturnLineData> calcPromotion(DiscountCallReturnData callOrderData, int evcTypeCode) throws DiscountException {
        return PromotionHandlerV3.calcPromotion(callOrderData, EVCType.getByCode(evcTypeCode));
    }

    public static List<DiscountCallReturnLineData> calcNewPromotion(DiscountCallReturnData callOrderData, int evcTypeCode) throws DiscountException {
        return PromotionHandlerV3.calcNewPromotion(callOrderData, EVCType.getByCode(evcTypeCode));
    }
    /***************************************************/

}
