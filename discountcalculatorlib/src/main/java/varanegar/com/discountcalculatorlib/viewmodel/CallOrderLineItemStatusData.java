package varanegar.com.discountcalculatorlib.viewmodel;

import java.math.BigDecimal;

/**
 * Created by m.aghajani on 1/26/2016.
 */
public class CallOrderLineItemStatusData {

    private String evcId;
    private int productId;
    private int rowOrder;
    private int discountId;
    private int disGroup;
    private BigDecimal addAmount;
    private BigDecimal supAmount;
    private BigDecimal discount;

    public CallOrderLineItemStatusData()
    {}

    public CallOrderLineItemStatusData(String evcId, int productId, int rowOrder, int discountId, int disGroup
                    , BigDecimal addAmount, BigDecimal supAmount, BigDecimal discount)
    {
        this.evcId = evcId;
        this.productId = productId;
        this.rowOrder = rowOrder;
        this.discountId = discountId;
        this.disGroup = disGroup;
        this.addAmount = addAmount;
        this.supAmount = supAmount;
        this.discount = discount;
    }

    //region getter and setter methods
    public String getEvcId() {
        return evcId;
    }

    public void setEvcId(String evcId) {
        this.evcId = evcId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getRowOrder() {
        return rowOrder;
    }

    public void setRowOrder(int rowOrder) {
        this.rowOrder = rowOrder;
    }

    public int getDiscountId() {
        return discountId;
    }

    public void setDiscountId(int discountId) {
        this.discountId = discountId;
    }

    public int getDisGroup() {
        return disGroup;
    }

    public void setDisGroup(int disGroup) {
        this.disGroup = disGroup;
    }

    public BigDecimal getAddAmount() {
        return addAmount;
    }

    public void setAddAmount(BigDecimal addAmount) {
        this.addAmount = addAmount;
    }

    public BigDecimal getSupAmount() {
        return supAmount;
    }

    public void setSupAmount(BigDecimal supAmount) {
        this.supAmount = supAmount;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }
    //endregion getter and setter methods
}
