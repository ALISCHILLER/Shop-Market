package varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite;

import android.content.ContentValues;
import android.database.Cursor;
import java.math.BigDecimal;
import java.util.Locale;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerOldInvoiceHeaderDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.handler.vnlite.PromotionGetRetExtraValueVnLiteV3;
import varanegar.com.discountcalculatorlib.utility.enumerations.EVCType;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallOrderData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallReturnData;


public class EVCHeaderVnLiteDBAdapter  extends DiscountBaseDataAdapter
{

	public final String KEY_CUSTCALLS_ROWID = "_id";
    public final String KEY_REF_ID = "RefId";
	public final String KEY_DISCOUNT_AMOUNT = "DiscountAmount";
	public final String KEY_ADD_AMOUNT = "AddAmount";
	public final String KEY_ORDER_YPE = "OrderType";
	public final String KEY_PAY_TYPE = "PayType";
	public final String KEY_STOCKDC_REF = "StockDCRef";
	public final String KEY_DC_REF = "DCRef";
	public final String KEY_DIS_TYPE = "DisType";
    public final String KEY_ACC_YEAR = "AccYear";
    public final String KEY_DC_SALES_OFFICE_REF = "DCSaleOfficeRef";
    public final String KEY_CALL_ID = "CallId";
    public final String KEY_EVC_ID = "EvcId";
    public final String KEY_EVC_TAX_AMOUNT = "Tax";
    public final String KEY_EVC_CHARGE_AMOUNT = "Charge";
    public final String KEY_CUSTOMERID_REF = "CustRef";
    public final String KEY_AMOUNT = "Amount";
    public final String KEY_NET_AMOUNT = "NetAmount";
	public final String KEY_EVC_TYPE = "EVCType";

	public static final String DATABASE_TABLE = "EVCHeaderVnLite";


	private static EVCHeaderVnLiteDBAdapter instance;

	public EVCHeaderVnLiteDBAdapter()
	{
	}
	
	public static EVCHeaderVnLiteDBAdapter getInstance()
	{
		
		if(instance == null)
		{
			instance = new EVCHeaderVnLiteDBAdapter();
		}
		return instance;
	}
	 

	public void deleteEVCHeadersById(String evcId)
	{
		db.delete(DATABASE_TABLE, "EVCId='" + evcId + "'", null);
	}
	
	public String getEvcId(int customerId)
	{
		String sql = " select EVCId from " + DATABASE_TABLE + " where Custref ='" + customerId + "'";
		Cursor  c = db.rawQuery(sql, null);
		if(c != null && c.moveToFirst())
		{
			return c.getString(0);
		}
		else
			return null;
	}

	public long  saveEVCHeader(DiscountCallOrderData call, String evcId, int disTypeId)
	{
		ContentValues values = new ContentValues();

		values.put(KEY_ORDER_YPE, -1);
		values.put(KEY_PAY_TYPE, call.invoicePaymentTypeId);
		values.put(KEY_DIS_TYPE, disTypeId);
		values.put(KEY_CUSTOMERID_REF, call.customerId);
		values.put(KEY_CALL_ID, call.callUniqueId);
		values.put(KEY_EVC_ID, evcId);
		values.put(KEY_EVC_TYPE, 1);//Default value for hotsale
		values.put(KEY_DC_REF, call.DCRef);
		values.put("DateOf", call.saleDate);
		long id = db.insert(DATABASE_TABLE, null, values);

		return id;
	}

	public void saveEVCHeader(DiscountCallReturnData call, String evcId1, String evcId2, EVCType evcTypeId) {

		String sql = "INSERT INTO " + DATABASE_TABLE
				+ " (EVCID, CallId, RefID, discountAmount, AddAmount,  OrderType, PayType, StockDCRef, DCRef,DisType,   CustRef,\n"
				+ " AccYear, DCSaleOfficeRef, EVCType,  Charge,Tax, DateOf)"

				+ " SELECT '%S' , '%S' , " + call.returnRefId + ", Dis1, Add1, OrderType, BuyTypeId, StockId, DCRef, DisType, CustomerId, \n"
				+ " AccYear, DCSaleOfficeRef "+"," + evcTypeId.value() + ",  Charge, Tax, SaleDate \n"
				+ " FROM " + DiscountCustomerOldInvoiceHeaderDBAdapter.DATABASE_TABLE
				+ " WHERE SaleId = " + call.returnRefId;


/*		StockId,DateOf, SalesmanId,	OprDate,	centerId*/
		/*SaleDate, DealerId,*/


		//Main
		db.execSQL(String.format(Locale.ENGLISH,sql, evcId1, call.callUniqueId));

		//Clone
		db.execSQL(String.format(Locale.ENGLISH, sql, evcId2, call.callUniqueId));
	}

    public void updateChargeTax(String evcId)
    {
        Cursor c = getTaxChargeSum(evcId);
        if(c != null & c.moveToFirst())
        {
            String sql = "update " + EVCHeaderVnLiteDBAdapter.DATABASE_TABLE +
					" set tax = " + c.getInt(0) + ", charge =" + c.getInt(1) + " where EvcId='" + evcId + "'";
            db.execSQL(sql);
        }
    }

    public Cursor getCalcPriority(String evcId)
    {
        Cursor c = null;
        String sql = " SELECT DISTINCT D.CalcPriority \n" +
                " FROM " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " EI \n" +
                " INNER JOIN " + EVCHeaderVnLiteDBAdapter.DATABASE_TABLE +" E ON E.EvcID  = EI.EVCRef \n" +
                " INNER JOIN " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE + " ES  ON ES.EVCItemRef =EI._id \n" + //in main sp its ES.EVCDetailID= EI.EVCDetailID
                " INNER JOIN " + DiscountVnLiteDBAdapter.DATABASE_TABLE + " D ON D.PromotionDetailId = ES.DisRef \n" +						   //i dont know what will happen !!!!
                " WHERE EI.EVCRef = '" + evcId +"' AND E.DisType <> '4' AND D.IsPrize IN ('2','3','4') \n" +
                " ORDER BY D.CalcPriority ";
        c = db.rawQuery(sql, null);
        return c;
    }

    public void applyStatuteOnEVC(String evcId)
    {
        String sql = "UPDATE " + DATABASE_TABLE +
                " SET DiscountAmount = IFNULL(" + DATABASE_TABLE + ".DiscountAmount , 0) + " +
                "                       (SELECT IFNULL(SUM(ES.DisAmount) , 0)  " +
                "                           FROM " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " EI " +
                "                           INNER JOIN " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE + " ES ON ES.EVCItemRef = EI._id " +
                "                           INNER JOIN " + DiscountVnLiteDBAdapter.DATABASE_TABLE + " D ON D.PromotionDetailId = ES.DisRef " +
                "                           WHERE EI.EVCRef = '" + evcId + "')   " +
                "  , AddAmount = IFNULL(" + DATABASE_TABLE + ".AddAmount , 0) + " +
                "                       (SELECT IFNULL(SUM(ES.AddAmount) , 0)   " +
                "                           FROM " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " EI " +
                "                           INNER JOIN " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE + " ES ON ES.EVCItemRef = EI._id " +
                "                           INNER JOIN " + DiscountVnLiteDBAdapter.DATABASE_TABLE + " D ON D.PromotionDetailId = ES.DisRef " +
                "                           WHERE EI.EVCRef = '" + evcId + "') " +
				"  , Amount = (SELECT IFNULL(SUM(EI.Amount), 0) FROM " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " EI WHERE EI.EVCRef = '" + evcId + "') " +
				"  , NetAmount = (SELECT IFNULL(SUM(EI.AmountNut), 0) FROM " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " EI WHERE EI.EVCRef = '" + evcId + "') " +
                " WHERE EVCId='" + evcId + "'";
        db.execSQL(sql);
    }

	public Cursor getEVCDataToSend(String evcId)
	{
		String sql = "";
		sql = "select Amount, NetAmount, DiscountAmount as discount, Tax, Charge, PayType, AddAmount from " + DATABASE_TABLE + " where evcId ='" + evcId + "'";

		return  db.rawQuery(sql, null);
	}

    private Cursor getTaxChargeSum(String evcId)
    {
		String sql = "select sum(tax) as taxTotal, sum(charge) as chargeTotal from "+ EVCItemVnLiteDBAdapter.DATABASE_TABLE  +" where EvcRef='" + evcId  + "'";
        return db.rawQuery(sql,  null);
    }
	
	public void clearAllData()
	{
		db.delete(DATABASE_TABLE, null, null);
	}

	public String getEVCValuesForReturn(String evcId)
	{
		int evcType, disType;
		evcType = disType = 0;
		String query = "SELECT " + KEY_EVC_TYPE + ", " + KEY_DIS_TYPE + " FROM " + DATABASE_TABLE +" WHERE EvcId = '" + evcId + "'";

		if(db != null) {

			Cursor c = db.rawQuery(query, null);
			if (c != null && c.moveToFirst()) {
				evcType = c.getInt(0);
				disType = c.getInt(1);
			}
		}

		return evcType + "," + disType;
	}

	public int getDisType(String evcId)
	{
		int disType = 0;
		String query = "SELECT " + KEY_DIS_TYPE +
				" FROM " + DATABASE_TABLE +" WHERE EvcId = '" + evcId + "'";

		if(db != null) {

			Cursor c = db.rawQuery(query, null);
			if (c != null && c.moveToFirst()) {
				disType = c.getInt(0);
			}
		}

		return disType;
	}

	public PromotionGetRetExtraValueVnLiteV3.EvcHeaderData getReturnEvcHeaderData(String evcId)
	{
		PromotionGetRetExtraValueVnLiteV3.EvcHeaderData result = null;
		String query = "SELECT DisType, DiscountAmount, AddAmount FROM " + DATABASE_TABLE + " WHERE EvcId = '" + evcId + "'";
		Cursor c = db.rawQuery(query, null);

		if(c != null && c.moveToFirst())
		{
			result = new PromotionGetRetExtraValueVnLiteV3.EvcHeaderData(c.getInt(0), new BigDecimal(c.getDouble(1)), new BigDecimal(c.getDouble(2)));
		}

		return result;
	}

	public void updateEVCHeaderForSellReturn(String evcId, int evcType, double dis, double add)
	{
		String query = "UPDATE " + DATABASE_TABLE + " SET EVCType = " + evcType
				+ ", DiscountAmount = " + dis
				+ ", AddAmount = " + add
				+ "  WHERE EvcId = '" + evcId + "'";
		db.execSQL(query);

	}

	public void resetEvc(String evcId)
	{
		String query = "UPDATE " + DATABASE_TABLE
				+ " SET DiscountAmount = 0 "
				+ ", AddAmount = 0"
				+ "  WHERE EvcId = '" + evcId + "'";
		db.execSQL(query);

	}

	public void updateEVCType(String evcId, int evcType)
	{
		db.execSQL("UPDATE " + DATABASE_TABLE + " SET EVCType = " + evcType + " WHERE EvcId = '" + evcId + "'");
	}
	public void updateByItems(String evcId)
	{
		String sql = "UPDATE " + DATABASE_TABLE +
				"  Set AddAmount = (Select SUM(AddAmount) FROM " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " WHERE EVCid='" + evcId + "' ),\n" +
				" DiscountAmount = (Select SUM(Discount) FROM " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " WHERE EVCid='" + evcId + "' )\n" +
				" WHERE EVCId='" + evcId + "'";
		db.execSQL(sql);
	}

}
	 
