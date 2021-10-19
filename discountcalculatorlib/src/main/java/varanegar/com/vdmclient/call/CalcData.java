package varanegar.com.vdmclient.call;

import java.util.List;


/**
 * Created by A.Torabi on 7/18/2018.
 */

public class CalcData {
    public List<ErrorInfo> Errors;
    public List<EvcHeader> EvcHeaders;
    public List<EvcItem> EvcItems;
    public List<EvcItemStatute> EvcItemStatute;


    public List<CustomerGroup> CustomerGroups;
    public List<CustomerMainSubType> CustomerMainSubTypes;
    public List<Customer> Customers;
    public List<GoodsFixUnit> GoodsFixUnits;
    public List<Goods> Goods;
    public List<GoodsMainSubType> GoodsMainSubTypes;
    public List<GoodsGroup> GoodsGroups;
    public List<Discount> Discounts;
    public List<DiscountGoods> DiscountGoods;
    public List<DiscountPrizeList> DiscountPrizeLists;
    public List<DiscountGoodsPackageItem> DiscountGoodsPackageItems;
    public List<FreeReason> FreeReasons;
    public List<CPrice> CPrices;
    public List<Price> Prices;
    public List<Package> Packages;
    public List<PaymentUsance> PaymentUsances;
    public List<GoodsNoSale> GoodsNoSales;
    public List<StockGoods> StockGoods;
    public List<OrderPrize> OrderPrizes;
    public List<SaleHdr> SaleHdrs;
    public List<SaleItm> SaleItms;
    public List<OrderHdr> OrderHdrs;
    public List<OrderItm> OrderItms;
    public List<DisSale> DisSales;
    public List<DisSalePrizePackage> DisSalePrizePackages;
    public List<DisAcc> DisAccs;
    public List<RetSaleHdr> RetSaleHdrs;
    public List<RetSaleItm> RetSaleItms;
    public int TempSaleDicountRef;
    public int SaleEVCID;
    public int NewSaleEVCID;
    public int CurrentEvcId;
    public String OrderNo;
    public int OrderRef;


    public GlobalVariable globalVariable;

    public List<EvcItem> SDSEvcItems;
    public List<EvcItemStatute> EvcSharpTempItemStatute;
    public List<EvcPeriodicDiscount> EvcPeriodicDiscounts;

    public List<EvcSkipDiscount> EvcSkipDiscount;
    public List<EvcSharpSummary> EvcSharpSummary; //#TmpTable2
    public List<EvcSharpSummaryFinal> EvcSharpSummaryFinal; //#TmpTable3
    public List<EvcSharpOrderPrize> EvcSharpOrderPrize;
    public List<EvcSharpGoodsPackageItem> EvcSharpGoodsPackageItem;
    public List<EvcSharpGoodsMainSubType> EvcSharpGoodsMainSubTypes;
    public List<EvcSharpGoodsDetail> EvcSharpGoodsDetail;
    public List<EvcSharpGoodsGroupTree> EvcSharpGoodsGroupTree;
    public List<EvcSharpGoodsGroupTreeFlat> EvcSharpGoodsGroupTreeFlat;
    public List<EvcSharpEvcItemStatute> EvcSharpEvcItemStatute; //@EVCItemStatutes
    public List<EvcSharpEvcItem> EvcSharpEvcItem;
    public List<EvcSharpDiscount> EvcSharpDiscount;
    public List<EvcSharpDiscountGood> EvcSharpDiscountGoods;
    public List<EvcSharpCustomerMainSubType> EvcSharpCustomerMainSubTypes;
    public List<EvcSharpPeriodicDiscountAll> EvcSharpPeriodicDiscountAlls;
    public List<EvcSharpAcceptedDiscount> EvcSharpAcceptedDiscountDirty;
    public List<EvcSharpAcceptedDiscount> EvcSharpAcceptedDiscount;
    public List<EVCSharp> EVCSharp;
    public List<EVCSharp> EVCSharpForDebug;
    public List<EvcPrizePackage> EvcPrizePackage;
    public List<EvcPrize> EvcPrize;
    public List<EvcSharpTmpGoodsPackage> EvcSharpTmpGoodsPackages;
    public List<EvcSharpTempCartonPrizeQty> EvcSharpTempCartonPrizeQtys;
    public List<EvcSharpRetItem> EvcSharpRetItems;
    public List<EvcSharpTempPrizePackageQty> EvcSharpTempPrizePackageQtys;
    public List<EvcSharpRemPrize> EvcSharpRemPrizes;
    public List<EvcSharpRemPrize> EvcSharpReturnPrizes;
    public List<EvcSharpRemPrize> EvcSharpDecreasePrizes;
    public List<EvcSharpMorePrize> EvcSharpMorePrizes;
    public List<CalcPriorityData> CalcPriorityDatas;


}
