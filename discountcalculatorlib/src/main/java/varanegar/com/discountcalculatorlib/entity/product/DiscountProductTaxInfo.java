package varanegar.com.discountcalculatorlib.entity.product;

import java.math.BigDecimal;

public class DiscountProductTaxInfo
{

	public int productId;

	public BigDecimal taxPercent ;

	public BigDecimal chargePerent;


	public DiscountProductTaxInfo(int productId, double taxPercent,
                                  double chargePerent)
	{
		this.productId = productId;
		this.taxPercent = new BigDecimal(taxPercent);
		this.chargePerent = new BigDecimal(chargePerent);
	}

	public DiscountProductTaxInfo()
	{
	}
}
