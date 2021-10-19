package varanegar.com.discountcalculatorlib.handler;

import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import timber.log.Timber;
import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerBoGroupDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerMainSubTypeDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerOldInvoiceDetailDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerOldInvoiceHeaderDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerSellPayTypeDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountDiscountGoodDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountGoodsFixUnitDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountGoodsNosaleDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountPaymentUsancesDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountRetSaleHdrDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountRetSaleItemDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountItemDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.v4_19.DisAccDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.v4_19.DiscountConditionDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.DisSalePrizePackageDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.DisSaleSDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.DisSaleVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.general.DiscountFreeReasonDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.price.DiscountContractPriceSDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.price.DiscountContractPriceVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.price.DiscountPriceHistoryDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountOrderPrizeDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductBoGroupDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductMainSubTypeDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductPackageDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductPackageItemDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductTaxInfoDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductUnitDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountTourProductDBAdapter;
import varanegar.com.discountcalculatorlib.helper.DiscountDbHelper;
import varanegar.com.discountcalculatorlib.utility.GlobalVariables;
import varanegar.com.discountcalculatorlib.utility.enumerations.BackOfficeType;
import varanegar.com.discountcalculatorlib.utility.enumerations.BackOfficeVersion;
import varanegar.com.discountcalculatorlib.viewmodel.BeforChangeQtyViewModel;
import varanegar.com.merge.InitializeVdm;
import varanegar.com.vdmclient.VdmInitializer;

/**
 * Created by A.Razavi on 11/15/2017.
 */

public class FillInitDataHandlerV3 {

    public static void fillInitData(IDiscountInitializeHandlerV3 initializeHandler,@Nullable VdmInitializer.InitCallback callback) {
        try {
            fillInitData(initializeHandler);
            new InitializeVdm().init(callback);
        } catch (Exception ex) {
            if (callback != null)
                callback.onFailure(ex.getMessage());
        }
    }

    public static void fillInitData(IDiscountInitializeHandlerV3 initializeHandler) {
        if (initializeHandler == null) return;
        //if (GlobalVariables.isCalcOnline()) return;
        SQLiteDatabase db = DiscountDbHelper.getDb();
        DiscountDbHelper.dbAttach(initializeHandler.getMainDbAttachQuery());
        cleanData();
        fillCustomerInitData(initializeHandler, true);
        fillOldInvoiceInitData(initializeHandler, true);
        fillProductInitData(initializeHandler, true);
        db.execSQL(initializeHandler.getFillFreeReasonQuery());
        db.execSQL(initializeHandler.getFillDiscountQuery());
        /*
        New tables
         */
        db.execSQL(initializeHandler.getFillGoodsNosaleQuery());
        db.execSQL(initializeHandler.getFillGoodsFixUnitQuery());
        db.execSQL(initializeHandler.getFillDiscountGoodQuery());
        db.execSQL(initializeHandler.getFillPaymentUsancesQuery());
        db.execSQL(initializeHandler.getFillRetSaleHdrQuery());
        db.execSQL(initializeHandler.getFillRetSaleItemQuery());
        /*
        End
         */
        if (!initializeHandler.getFillCPriceQuery().isEmpty())
            db.execSQL(initializeHandler.getFillCPriceQuery());

        if (GlobalVariables.getBackOffice().equals(BackOfficeType.VARANEGAR)) {

            if (GlobalVariables.getBackOfficeVersion() != BackOfficeVersion.SDS16) {
                db.execSQL(initializeHandler.getFillDiscountConditionQuery());
                if (!initializeHandler.getFillDisAccQuery().isEmpty())
                    db.execSQL(initializeHandler.getFillDisAccQuery());
                if (!initializeHandler.getFillOrderTypeQuery().isEmpty())
                    db.execSQL(initializeHandler.getFillOrderTypeQuery());
                if (!initializeHandler.getFillDisTypeQuery().isEmpty())
                    db.execSQL(initializeHandler.getFillDisTypeQuery());
            }
            db.execSQL(initializeHandler.getFillDiscountItemQuery());
            db.execSQL(initializeHandler.getFillDiscountDisSaleQuery());
            if (!initializeHandler.getFillPriceHistory().isEmpty())
                db.execSQL(initializeHandler.getFillPriceHistory());

            if (!initializeHandler.getFillDisSalePrizePackageSDSQuery().isEmpty())
                db.execSQL(initializeHandler.getFillDisSalePrizePackageSDSQuery());

        }
        if (GlobalVariables.getBackOffice().equals(BackOfficeType.RASTAK)) {
        }
        DiscountDbHelper.dbDetach(initializeHandler.getMainDbAttachQuery());


    }

    public static void fillCustomerInitData(IDiscountInitializeHandlerV3 initializeHandler, boolean attached) {
        if (initializeHandler == null) return;
        //if (GlobalVariables.isCalcOnline()) return;
        SQLiteDatabase db = DiscountDbHelper.getDb();
        if (!attached)
            DiscountDbHelper.dbAttach(initializeHandler.getMainDbAttachQuery());
        cleanCustomer();
        db.execSQL(initializeHandler.getFillCustomerQuery());
        db.execSQL(initializeHandler.getFillCustomerSellPayTypeQuery());

        if (!initializeHandler.getFillCustomerMainSubTypeQuery().isEmpty())
            db.execSQL(initializeHandler.getFillCustomerMainSubTypeQuery());
        if (!initializeHandler.getFillCustomerBoGroupQuery().isEmpty())
            db.execSQL(initializeHandler.getFillCustomerBoGroupQuery());
        if (!attached)
            DiscountDbHelper.dbDetach(initializeHandler.getMainDbAttachQuery());
    }

    public static void fillOldInvoiceInitData(IDiscountInitializeHandlerV3 initializeHandler, boolean attached) {
        if (initializeHandler == null) return;
        SQLiteDatabase db = DiscountDbHelper.getDb();
        if (!attached)
            DiscountDbHelper.dbAttach(initializeHandler.getMainDbAttachQuery());
        cleanOldInvoice();
        if (!initializeHandler.getFillCustomerOldInvoiceHeaderQuery().isEmpty())
            db.execSQL(initializeHandler.getFillCustomerOldInvoiceHeaderQuery());
        if (!initializeHandler.getFillCustomerOldInvoiceDetailQuery().isEmpty())
            db.execSQL(initializeHandler.getFillCustomerOldInvoiceDetailQuery());
        if (GlobalVariables.getBackOffice().equals(BackOfficeType.VARANEGAR)) {
            if (initializeHandler.getFillDiscountDisSaleQuery() != null && !initializeHandler.getFillDiscountDisSaleQuery().isEmpty()) {
                db.execSQL(initializeHandler.getFillDiscountDisSaleQuery());
            }
        }
        if (GlobalVariables.getBackOffice().equals(BackOfficeType.RASTAK)) {
            if (initializeHandler.getFillDiscountDisSaleVnLtQuery() != null && !initializeHandler.getFillDiscountDisSaleVnLtQuery().isEmpty())
                db.execSQL(initializeHandler.getFillDiscountDisSaleVnLtQuery());
        }
        if (!attached)
            DiscountDbHelper.dbDetach(initializeHandler.getMainDbAttachQuery());
    }

    public static void fillProductInitData(IDiscountInitializeHandlerV3 initializeHandler, boolean attached) {
        if (initializeHandler == null) return;
        //if (GlobalVariables.isCalcOnline()) return;
        SQLiteDatabase db = DiscountDbHelper.getDb();
        if (!attached)
            DiscountDbHelper.dbAttach(initializeHandler.getMainDbAttachQuery());
        cleanProduct();

        db.execSQL(initializeHandler.getFillProductQuery());
        db.execSQL(initializeHandler.getFillProductBoGroupQuery());
        db.execSQL(initializeHandler.getFillProductPackageQuery());
        db.execSQL(initializeHandler.getFillProductPackageItemQuery());
        db.execSQL(initializeHandler.getFillProductUnitQuery());
        if (GlobalVariables.getBackOffice().equals(BackOfficeType.RASTAK)) {
            if (!initializeHandler.getFillProductTaxInfoQuery().isEmpty())
                db.execSQL(initializeHandler.getFillProductTaxInfoQuery());
        } else {
            db.execSQL(initializeHandler.getFillProductMainSubTypeQuery());
        }
        if (!attached)
            DiscountDbHelper.dbDetach(initializeHandler.getMainDbAttachQuery());

        fillTourProductInitData(initializeHandler, attached, null);
    }

    public static void fillTourProductInitData(IDiscountInitializeHandlerV3 initializeHandler, boolean attached, ArrayList<BeforChangeQtyViewModel> beforeChange) {
        if (initializeHandler == null) return;
        SQLiteDatabase db = DiscountDbHelper.getDb();
        if (!attached)
            DiscountDbHelper.dbAttach(initializeHandler.getMainDbAttachQuery());
        DiscountTourProductDBAdapter.getInstance().clearAllData();

        if (GlobalVariables.getBackOffice().equals(BackOfficeType.VARANEGAR)) {
            if (initializeHandler.getFillTourProductQuery() != null && !initializeHandler.getFillTourProductQuery().isEmpty())
                db.execSQL(initializeHandler.getFillTourProductQuery());

            if (beforeChange != null) {
                for (BeforChangeQtyViewModel item : beforeChange) {
                    db.execSQL("UPDATE DiscountTourProduct\n" +
                            " SET SoldQty = SoldQty - " + item.qty +
                            " WHERE ProductId = " + item.productId);
                }
            }

            if (!attached)
                DiscountDbHelper.dbDetach(initializeHandler.getMainDbAttachQuery());
        }
    }

    public static void fillNewCustomerInitData(IDiscountInitializeHandlerV3 initializeHandler, boolean attached) {
        if (initializeHandler == null) return;
        SQLiteDatabase db = DiscountDbHelper.getDb();
        if (!attached)
            DiscountDbHelper.dbAttach(initializeHandler.getMainDbAttachQuery());
        if (initializeHandler.getFillCustomerQuery() != null && !initializeHandler.getFillCustomerQuery().isEmpty())
            db.execSQL(initializeHandler.getFillCustomerQuery() + " WHERE CustomerCode not in (SELECT cc.CustomerCode FROM DiscountCustomer cc) ");
        if (!attached)
            DiscountDbHelper.dbDetach(initializeHandler.getMainDbAttachQuery());
    }


    public static void fillOrderPrizeInitData(IDiscountInitializeHandlerV3 initializeHandler, boolean attached, int orderId) {
        if (initializeHandler == null) return;
        SQLiteDatabase db = DiscountDbHelper.getDb();
        if (!attached)
            DiscountDbHelper.dbAttach(initializeHandler.getMainDbAttachQuery());
        DiscountOrderPrizeDBAdapter.getInstance().clearAllData();

        if (GlobalVariables.getBackOffice().equals(BackOfficeType.VARANEGAR)) {
            if (initializeHandler.getFillOrderPrizeQuery(orderId) != null && !initializeHandler.getFillOrderPrizeQuery(orderId).isEmpty()) {
                db.execSQL(initializeHandler.getFillOrderPrizeQuery(orderId));
            }
            if (!attached)
                DiscountDbHelper.dbDetach(initializeHandler.getMainDbAttachQuery());
        }


    }

    public static void initQuery(IDiscountInitializeHandlerV3 initializeHandler) {
        if (initializeHandler == null) return;
        SQLiteDatabase db = DiscountDbHelper.getDb();
        if (initializeHandler.getInitQuery() != null && !initializeHandler.getInitQuery().isEmpty()) {
            String initQuery = initializeHandler.getInitQuery();
            String[] queries;
            if (initQuery.contains(";"))
                queries = initQuery.split(";");
            else {
                queries = new String[1];
                queries[0] = initQuery;
            }
            for (String item : queries) {
                try {
                    if (!item.isEmpty())
                        db.execSQL(item);
                } catch (Exception e) {
                    Timber.e(" initQuery : " + e.getStackTrace());
                }
            }

        }
    }

    private static void cleanCustomer() {
        DiscountCustomerSellPayTypeDBAdapter.getInstance().clearAllData();
        DiscountCustomerMainSubTypeDBAdapter.getInstance().clearAllData();
        DiscountCustomerDBAdapter.getInstance().clearAllData();
        DiscountCustomerBoGroupDBAdapter.getInstance().clearAllData();
    }

    private static void cleanOldInvoice() {
        DiscountCustomerOldInvoiceDetailDBAdapter.getInstance().clearAllData();
        DiscountCustomerOldInvoiceHeaderDBAdapter.getInstance().clearAllData();
        DisSaleSDSDBAdapter.getInstance().clearAllData();
        DisSaleVnLiteDBAdapter.getInstance().clearAllData();

        // added by ahad
        DisAccDBAdapter.getInstance().clearAllData();
        DiscountRetSaleHdrDBAdapter.getInstance().clearAllData();
        DiscountRetSaleItemDBAdapter.getInstance().clearAllData();
    }

    private static void cleanProduct() {
        DiscountProductBoGroupDBAdapter.getInstance().clearAllData();
        DiscountProductMainSubTypeDBAdapter.getInstance().clearAllData();
        DiscountProductPackageItemDBAdapter.getInstance().clearAllData();
        DiscountProductPackageDBAdapter.getInstance().clearAllData();
        DiscountProductUnitDBAdapter.getInstance().clearAllData();
        DiscountProductDBAdapter.getInstance().clearAllData();
        DiscountProductTaxInfoDBAdapter.getInstance().clearAllData();
        // added by ahad
        DiscountTourProductDBAdapter.getInstance().clearAllData();
    }

    private static void cleanData() {
        DiscountDBAdapter.getInstance().clearAllData();
        DiscountVnLiteDBAdapter.getInstance().clearAllData();
        DiscountConditionDBAdapter.getInstance().clearAllData();
        DiscountItemDBAdapter.getInstance().clearAllData();
        DisAccDBAdapter.getInstance().clearAllData();
        DisSaleSDSDBAdapter.getInstance().clearAllData();
        DiscountFreeReasonDBAdapter.getInstance().clearAllData();
        DiscountPriceHistoryDBAdapter.getInstance().clearAllData();
        DiscountContractPriceSDSDBAdapter.getInstance().clearAllData();
        DiscountContractPriceVnLiteDBAdapter.getInstance().clearAllData();
        DisSalePrizePackageDBAdapter.getInstance().clearAllData();

        // added by ahad
        DiscountPaymentUsancesDBAdapter.getInstance().clearAllData();
        DiscountDiscountGoodDBAdapter.getInstance().clearAllData();
        DiscountGoodsNosaleDBAdapter.getInstance().clearAllData();
        DiscountGoodsFixUnitDBAdapter.getInstance().clearAllData();
    }


}
