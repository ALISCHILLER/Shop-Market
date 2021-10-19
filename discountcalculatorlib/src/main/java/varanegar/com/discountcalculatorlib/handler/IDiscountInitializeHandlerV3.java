package varanegar.com.discountcalculatorlib.handler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import varanegar.com.discountcalculatorlib.utility.enumerations.ApplicationType;
import varanegar.com.discountcalculatorlib.viewmodel.OwnerKeysViewModel;

/**
 * Created by A.Razavi on 11/15/2017.
 */

public interface IDiscountInitializeHandlerV3 {
    /**************************************************************/
    /* Batabase */
    /**************************************************************/
    //SQLiteDatabase getMainDb();
    String getMainDbAttachQuery();
    String getDiscountDbPath();
    String getInitQuery();

    /**************************************************************/
    /* GlobalValue */
    /**************************************************************/
    ApplicationType getApplicationType();
    int getBackOffice();
    Context getContext();



    /*******************/
    /* online */
    /*******************/
    boolean getCalcOnLine();
    String getServerUrl();
    OwnerKeysViewModel getOwnerKeys();

    /*******************/
    /* offline */
    /*******************/
    int getDigtsAfterDecimalDigits();
    int getDigtsAfterDecimalForCPrice();
    boolean getPrizeCalcType();
    int getBackOfficeVersion();

    int getDcRef();
    int getRoundType();

    /**************************************************************/
    /* Queries */
    /**************************************************************/
    String getFillDiscountQuery();
    String getFillDiscountConditionQuery();
    String getFillDiscountItemQuery();
    String getFillDiscountDisSaleQuery();
    String getFillCPriceQuery();
    String getFillPriceHistory();
    String getFillDisAccQuery();
    String getFillOrderPrizeQuery(int orderId);
    String getFillDiscountDisSaleVnLtQuery();

    String getFillCustomerQuery();
    String getFillCustomerSellPayTypeQuery();
    String getFillCustomerMainSubTypeQuery();
    String getFillCustomerOldInvoiceHeaderQuery();
    String getFillCustomerOldInvoiceDetailQuery();
    String getFillCustomerBoGroupQuery();

    String getFillProductQuery();
    String getFillProductUnitQuery();
    String getFillProductBoGroupQuery();
    String getFillProductMainSubTypeQuery();
    String getFillProductPackageQuery();
    String getFillProductPackageItemQuery();
    String getFillTourProductQuery();
    String getFillProductTaxInfoQuery();

    String getFillFreeReasonQuery();
    String getFillDisSalePrizePackageSDSQuery();

    String getFillOrderTypeQuery();
    String getFillDisTypeQuery();

    String getAppId();

    //new
    String getFillGoodsNosaleQuery();
    String getFillGoodsFixUnitQuery();
    String getFillDiscountGoodQuery();
    String getFillPaymentUsancesQuery();
    String getFillRetSaleHdrQuery();
    String getFillRetSaleItemQuery();
}
