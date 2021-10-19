package varanegar.com.discountcalculatorlib.viewmodel;

public class CallOrderLineGoodPackageItemData {


    private String evcId;

    public String getOrderUniqueId() {
        return orderUniqueId;
    }

    public void setOrderUniqueId(String orderUniqueId) {
        this.orderUniqueId = orderUniqueId;
    }

    public String orderUniqueId;
    private int mainGoodsPackageItemRef;
    private int replaceGoodsPackageItemRef;
    private int discountRef;
    private int prizeQty;
    private int prizeCount;

    public CallOrderLineGoodPackageItemData()
    {}

    public String getEvcId() {
        return evcId;
    }

    public void setEvcId(String evcId) {
        this.evcId = evcId;
    }

    public int getMainGoodsPackageItemRef() {
        return mainGoodsPackageItemRef;
    }

    public void setMainGoodsPackageItemRef(int mainGoodsPackageItemRef) {
        this.mainGoodsPackageItemRef = mainGoodsPackageItemRef;
    }

    public int getReplaceGoodsPackageItemRef() {
        return replaceGoodsPackageItemRef;
    }

    public void setReplaceGoodsPackageItemRef(int replaceGoodsPackageItemRef) {
        this.replaceGoodsPackageItemRef = replaceGoodsPackageItemRef;
    }

    public int getDiscountRef() {
        return discountRef;
    }

    public void setDiscountRef(int discountRef) {
        this.discountRef = discountRef;
    }

    public int getPrizeQty() {
        return prizeQty;
    }

    public void setPrizeQty(int prizeQty) {
        this.prizeQty = prizeQty;
    }

    public int getPrizeCount() {
        return prizeCount;
    }

    public void setPrizeCount(int prizeCount) {
        this.prizeCount = prizeCount;
    }
}
