package varanegar.com.merge;

import android.database.Cursor;

import com.varanegar.framework.database.DbAttachment;
import com.varanegar.framework.database.DbHandler;
import com.varanegar.framework.database.SQLiteConnectionString;
import com.varanegar.framework.database.TableMap;
import com.varanegar.framework.database.querybuilder.TableMismatchException;
import com.varanegar.framework.database.querybuilder.TableNotFoundException;

import timber.log.Timber;
import varanegar.com.discountcalculatorlib.utility.GlobalVariables;
import varanegar.com.vdmclient.VdmClient;
import varanegar.com.vdmclient.VdmInitializer;

/**
 * Created by A.Torabi on 7/16/2018.
 */

public class Initializer extends VdmInitializer {

    public Initializer(VdmClient client) {
        super(client);
    }

    @Override
    protected boolean attachDb(String databaseFileName) {
        try {
            SQLiteConnectionString destConnectionString = new SQLiteConnectionString(databaseFileName);
            DbHandler destDbHandler = new DbHandler(getContext(), destConnectionString);

            String discountDbName = GlobalVariables.getDiscountDbName();
            SQLiteConnectionString srcConnectionString = new SQLiteConnectionString(getContext(), discountDbName);
            DbHandler srcDbHandler = new DbHandler(getContext(), srcConnectionString);

            final boolean tsrc = true;
            final boolean tdest = true;

            DbAttachment dbAttachment = new DbAttachment(discountDbName);
            dbAttachment.addMapping(new TableMap("DiscountCustomerOldInvoiceDetail", "SaleItm").addColumn("PriceId", "PriceRef").addColumn("ProductId", "GoodsRef").addColumn("SaleId", "HdrRef").addColumn("ProductCtgrId", "Id").addAllColumns(srcDbHandler, destDbHandler, !tsrc, !tdest));
            dbAttachment.addMapping(new TableMap("DiscountOrderPrize", "OrderPrize").addAllColumns(srcDbHandler, destDbHandler, !tsrc, !tdest));
            dbAttachment.addMapping(new TableMap("DiscountDisAcc", "DisAcc").addAllColumns(srcDbHandler, destDbHandler, !tsrc, !tdest));
            dbAttachment.addMapping(new TableMap("DiscountTourProduct", "StockGoods").addColumn("ProductId", "GoodsRef").addAllColumns(srcDbHandler, destDbHandler, !tsrc, !tdest));
//            dbAttachment.addMapping(new TableMap("DiscountCustomer", "Customer").addColumn("AreaId", "AreaRef").addColumn("CustomerCategoryId", "CustCtgrRef").addColumn("CustomerLevelId", "CustLevelRef").addColumn("CustomerName", "CustName").addColumn("CustomerRemain", "CustomerRemAmount").addColumn("CustomerId", "CustRef").addColumn("DCRef", "DcRef").addColumn("StateId", "StateRef").addColumn("CustomerActivityId", "CustActRef").addAllColumns(srcDbHandler, destDbHandler, !tsrc, !tdest));
            dbAttachment.addMapping(new TableMap("DiscountCustomerOldInvoiceHeader", "SaleHdr").addColumn("SaleId", "Id").addAllColumns(srcDbHandler, destDbHandler, !tsrc, !tdest));
            dbAttachment.addMapping(new TableMap("DiscountProductUnit", "Package").addColumn("BackOfficeId", "Id").addColumn("ProductId", "GoodsRef").addColumn("ProductUnitId", "UnitRef").addColumn("Quantity", "Qty").addColumn("IsDefault", "DefaultForSale").addAllColumns(srcDbHandler, destDbHandler, !tsrc, !tdest));
            dbAttachment.addMapping(new TableMap("DiscountProductMainSubType", "GoodsMainSubType").addAllColumns(srcDbHandler, destDbHandler, !tsrc, !tdest));
            dbAttachment.addMapping(new TableMap("DiscountCustomerBoGroup", "CustomerGroup").addAllColumns(srcDbHandler, destDbHandler, !tsrc, !tdest));
            dbAttachment.addMapping(new TableMap("DiscountDisSalePrizePackageSDS", "DisSalePrizePackage").addColumn("ID", "Id").addAllColumns(srcDbHandler, destDbHandler, !tsrc, !tdest));
            dbAttachment.addMapping(new TableMap("DiscountCustomerMainSubType", "CustomerMainSubType").addAllColumns(srcDbHandler, destDbHandler, !tsrc, !tdest));
            dbAttachment.addMapping(new TableMap("DiscountProductBoGroup", "GoodsGroup").addColumn("ID", "Id").addColumn("ParentRef", "Parent").addAllColumns(srcDbHandler, destDbHandler, !tsrc, !tdest));
//            dbAttachment.addMapping(new TableMap("EVCItemStatutesSDS", "EvcItemStatute").addColumn("_id", "Id").addColumn("EVCItemRef", "EvcItemRef").addAllColumns(srcDbHandler, destDbHandler, !tsrc, !tdest));
            dbAttachment.addMapping(new TableMap("DiscountProduct", "Goods").addColumn("ProductCode", "GoodsCode").addColumn("ProductBoGroupId", "GoodsGroupRef").addColumn("ProductName", "GoodsName").addColumn("ProductWeight", "GoodsWeight").addColumn("ProductId", "Id").addColumn("ManufacturerId", "ManufacturerRef").addColumn("ShipTypeId", "ShipTypeRef").addColumn("BrandId", "BrandRef").addAllColumns(srcDbHandler, destDbHandler, !tsrc, !tdest));
            dbAttachment.addMapping(new TableMap("DiscountContractPriceSDS", "CPrice").addColumn("ID", "Id").addAllColumns(srcDbHandler, destDbHandler, !tsrc, !tdest));
            dbAttachment.addMapping(new TableMap("DiscountSDS4_19", "Discount").addColumn("ID", "Id").addColumn("goodsRef", "GoodsRef").addAllColumns(srcDbHandler, destDbHandler, !tsrc, !tdest));
            dbAttachment.addMapping(new TableMap("DiscountEvcPrize", "EvcPrize").addAllColumns(srcDbHandler, destDbHandler, tsrc, tdest));
//            dbAttachment.addMapping(new TableMap("EVCHeaderSDS", "EvcHeader").addColumn("DCCode", "DcCode").addColumn("DCRef", "DcRef").addColumn("DCSaleOfficeRef", "DcSaleOfficeRef").addColumn("EVCId", "EvcId").addColumn("EVCType", "EvcType").addColumn("StockDCCode", "StockDcCode").addColumn("StockDCRef", "StockDcRef").addColumn("_id", "Id").addColumn("Amount", "TotalAmount").addAllColumns(srcDbHandler, destDbHandler, !tsrc, !tdest));
            dbAttachment.addMapping(new TableMap("DiscountPriceHistory", "Price").addColumn("ID", "Id").addAllColumns(srcDbHandler, destDbHandler, !tsrc, !tdest));
            dbAttachment.addMapping(new TableMap("DiscountEvcPrizePackage", "EvcPrizePackage").addAllColumns(srcDbHandler, destDbHandler, !tsrc, !tdest));
            dbAttachment.addMapping(new TableMap("DiscountFreeReason", "FreeReason").addColumn("FreeReasonId", "Id").addAllColumns(srcDbHandler, destDbHandler, !tsrc, !tdest));
            dbAttachment.addMapping(new TableMap("DiscountGoodsPackageItem", "DiscountGoodsPackageItem").addColumn("ID", "Id").addAllColumns(srcDbHandler, destDbHandler, !tsrc, !tdest));
            destDbHandler.attach(dbAttachment, false);
            return true;
        } catch (TableMismatchException e) {
            Timber.e(e);
            Timber.e(e.listUnmappedColumns());
            return false;
        } catch (TableNotFoundException e) {
            Timber.e(e);
            return false;
        } catch (Exception e) {
            Timber.e(e);
            return false;
        }

    }


}
