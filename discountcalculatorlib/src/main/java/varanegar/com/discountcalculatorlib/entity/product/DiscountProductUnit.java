package varanegar.com.discountcalculatorlib.entity.product;

import java.math.BigDecimal;

public class DiscountProductUnit
{
	public int productId;
	public long productUnitId;
	public String productUnitName;
	public BigDecimal quantity;
	public int isForSales;
	public int isDefault;
	public int BackOfficeId;
	public int status;

	public DiscountProductUnit(int _ProductId, long _productUnitId, String _productUnitName, BigDecimal _quantity
			, int _isDefault, int _isForSales, int _BackOfficeId, int status)
	{
		this.productId = _ProductId;
		this.productUnitId = _productUnitId;
		this.productUnitName = _productUnitName;
		this.quantity = _quantity;
		this.isDefault = _isDefault;
		this.isForSales = _isForSales;
		this.BackOfficeId= _BackOfficeId;
		this.status = status;
	}

}
