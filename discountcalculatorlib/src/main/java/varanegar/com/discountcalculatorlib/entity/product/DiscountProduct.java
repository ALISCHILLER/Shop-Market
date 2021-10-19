package varanegar.com.discountcalculatorlib.entity.product;


import android.os.Parcel;
import android.os.Parcelable;
import java.math.BigDecimal;
import java.util.ArrayList;

import varanegar.com.discountcalculatorlib.utility.enumerations.ProductType;

public class DiscountProduct implements Parcelable
{

	public int productId;
	public String productCode;
	public String productName;
	public int productSubGroupId;
	public long smallUnitId;
	public String smallUnitName;
	public BigDecimal smallUnitQty;
	public long largeUnitId;
	public String largeUnitName;
	public BigDecimal largeUnitQty;
	public String Description;
	public int productTypeId;
	public int ManufacturerId;
	public int ProductBoGroupId;
	public int BrandId;
	public int ProductCtgrId;
	public BigDecimal charge;
	public BigDecimal tax;
	public BigDecimal ProductWeight;
	public int cartonType;
	public long midUnitId;
	public String midUnitName;
	public int midUnitQty;

	public ArrayList<DiscountProductUnit> unitsInfo = new ArrayList<DiscountProductUnit>();

	public boolean isBulk()
	{
		return productTypeId == ProductType.BULK.value();
	}
	public DiscountProduct()
	{
	}

	//use for ReturnProduct
	public DiscountProduct(int _productId, String _productCode, String _productName, int _productSubGroupId,
                           long _smallUnitId, String _smallUnitName, BigDecimal _smallUnitQty, long _largeUnitId, String _largeUnitName,
                           BigDecimal _largeUnitQty, String _Description, int productTypeId, int productBoGroupId)
	{
		this.productId = _productId;
		this.productCode = _productCode;
		this.productName = _productName;
		this.productSubGroupId = _productSubGroupId;
		this.smallUnitId = _smallUnitId;
		this.smallUnitName = _smallUnitName;
		this.smallUnitQty = _smallUnitQty;
		this.largeUnitId = _largeUnitId;
		this.largeUnitName = _largeUnitName;
		this.largeUnitQty = _largeUnitQty;
		this.Description = _Description;
		this.productTypeId = productTypeId;
		this.ProductBoGroupId = productBoGroupId;
	}

	public DiscountProduct(int _productId, String _productCode, String _productName, int _productSubGroupId,
                           long _smallUnitId, String _smallUnitName, BigDecimal _smallUnitQty, long _largeUnitId, String _largeUnitName,
                           BigDecimal _largeUnitQty, String _Description, int _ManufacturerId, int _ProductBoGroupId, int _BrandId,
                           int _ProductCtgrId, int _ProductTypeId, BigDecimal _charge, BigDecimal _tax, BigDecimal _ProductWeight, int _cartonType)
	{
		this.productId = _productId;
		this.productCode = _productCode;
		this.productName = _productName;
		this.productSubGroupId = _productSubGroupId;
		this.smallUnitId = _smallUnitId;
		this.smallUnitName = _smallUnitName;
		this.smallUnitQty = _smallUnitQty;
		this.largeUnitId = _largeUnitId;
		this.largeUnitName = _largeUnitName;
		this.largeUnitQty = _largeUnitQty;
		this.Description = _Description;
		this.productTypeId = _ProductTypeId;
		this.ManufacturerId = _ManufacturerId;
		this.ProductBoGroupId = _ProductBoGroupId;
		this.BrandId=_BrandId;
		this.ProductCtgrId = _ProductCtgrId;
		this.charge = _charge;
		this.tax = _tax;
		this.ProductWeight = _ProductWeight;
		this.cartonType = _cartonType;
	}

	//region Parceling part

	public DiscountProduct(Parcel in)
	{
        //************ in order we wrote data in writeToParcel we read it here ************
        productId = in.readInt();
        productCode = in.readString();
        productName = in.readString();
        productSubGroupId = in.readInt();
        smallUnitId = in.readLong();
        smallUnitName = in.readString();
        smallUnitQty = new BigDecimal(in.readDouble());
        largeUnitId = in.readLong();
        largeUnitName = in.readString();
        largeUnitQty = new BigDecimal(in.readDouble());
        Description = in.readString();
        productTypeId = in.readInt();
        ManufacturerId = in.readInt();
        ProductBoGroupId = in.readInt();
        BrandId = in.readInt();
        ProductCtgrId = in.readInt();
        charge = new BigDecimal(in.readDouble());
        tax = new BigDecimal(in.readDouble());
        ProductWeight = new BigDecimal(in.readDouble());
        cartonType = in.readInt();
    }

    @Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeInt(productId);
		dest.writeString(productCode == null ? "" : productCode);
		dest.writeString(productName == null ? "" : productName);
		dest.writeInt(productSubGroupId);
		dest.writeLong(smallUnitId);
		dest.writeString(smallUnitName == null ? "" : smallUnitName);
		dest.writeDouble(smallUnitQty == null ? 0 : smallUnitQty.doubleValue());
		dest.writeLong(largeUnitId);
		dest.writeString(largeUnitName == null ? "" : largeUnitName);
		dest.writeDouble(largeUnitQty == null ? 0 : largeUnitQty.doubleValue());
		dest.writeString(Description == null ? "" : Description);
		dest.writeInt(productTypeId);
		dest.writeInt(ManufacturerId);
		dest.writeInt(ProductBoGroupId);
		dest.writeInt(BrandId);
		dest.writeInt(ProductCtgrId);
		dest.writeDouble(charge == null ? 0 : charge.doubleValue());
		dest.writeDouble(tax == null ? 0 : tax.doubleValue());
		dest.writeDouble(ProductWeight == null ? 0 : ProductWeight.doubleValue());
		dest.writeInt(cartonType);
	}

    public static final Creator<DiscountProduct> CREATOR = new Creator<DiscountProduct>() {
        @Override
        public DiscountProduct createFromParcel(Parcel in) {
            return new DiscountProduct(in);
        }

        @Override
        public DiscountProduct[] newArray(int size) {
            return new DiscountProduct[size];
        }
    };

	//endregion Parceling part
}
