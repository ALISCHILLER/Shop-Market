package com.varanegar.vaslibrary.manager.sysconfigmanager;

import android.graphics.Bitmap;

/**
 * Created by atp on 6/6/2017.
 */

public class ConfigKey {

    public static ConfigKey EnableGPS = new ConfigKey("EnableGPS");

    public static ConfigKey CompanyName = new ConfigKey("CompanyName");
    public static ConfigKey CompanyEmail = new ConfigKey("CompanyEmail");
    public static ConfigKey CompanyAddress = new ConfigKey("CompanyAddress");
    public static ConfigKey CompanyPhone = new ConfigKey("CompanyPhone");
    public static ConfigKey Mobile = new ConfigKey("Mobile");

    public static ConfigKey CatalogType = new ConfigKey("ShowCatalogBasedOn");
    public static ConfigKey CheckDistance = new ConfigKey("CheckDistance");
    public static ConfigKey MaxDistance = new ConfigKey("MaxDistance");
    public static ConfigKey LocalServerAddress = new ConfigKey("LocalServerAddress");
    public static ConfigKey ValidServerAddress = new ConfigKey("ValidServerAddress");
    public static ConfigKey BaseAddress = new ConfigKey("BaseAddress");
    public static ConfigKey SettingsId = new ConfigKey("SettingsId");
    public static ConfigKey CheckDebit = new ConfigKey("CheckDebit");
    public static ConfigKey CheckCredit = new ConfigKey("CheckCredit");
    public static ConfigKey MandatoryCustomerVisit = new ConfigKey("MandatoryCustomerVisit");
    public static ConfigKey AutoSynch = new ConfigKey("AutoSynch");
    public static ConfigKey ViewCustomerCardexReport = new ConfigKey("ViewCustomerCardexReport");
    public static ConfigKey ViewCustomerOpenInvoicesReport = new ConfigKey("ViewCustomerOpenInvoicesReport");
    public static ConfigKey ViewCustomerInvoicesReport = new ConfigKey("ViewCustomerInvoicesReport");
    public static ConfigKey ViewCustomerSaleHistoryReport = new ConfigKey("ViewCustomerSaleHistoryReport");
    public static ConfigKey ViewCustomerFinanceData = new ConfigKey("ViewCustomerFinanceData");
    public static ConfigKey AllowReturnWithoutRef = new ConfigKey("AllowReturnWithoutRef");
    public static ConfigKey AllowReturnWithRef = new ConfigKey("AllowReturnWithRef");
    public static ConfigKey AllowRegisterNewCustomer = new ConfigKey("AllowRegisterNewCustomer");
    public static ConfigKey AllowUpdatePrice = new ConfigKey("AllowUpdatePrice");
    public static ConfigKey OnlineSynch = new ConfigKey("OnlineSynch");
    public static ConfigKey VisitCustomersNotInPath = new ConfigKey("VisitCustomersNotInPath");
    public static ConfigKey AllowEditCustomer = new ConfigKey("AllowEditCustomer");
    public static ConfigKey OnlineRefreshStockLevel = new ConfigKey("OnlineRefreshStockLevel");
    public static ConfigKey CustomerStockCheckType = new ConfigKey("CustomerStockCheckType");
    public static ConfigKey CheckCustomerStock = new ConfigKey("CheckCustomerStock");
    public static ConfigKey ViewCustomerTargetReport = new ConfigKey("ViewCustomerTargetReport");
    public static ConfigKey UnSubmitCancellation = new ConfigKey("UnSubmitCancellation");

    public static ConfigKey EditCustomerMobile = new ConfigKey("EditCustomerMobile");
    public static ConfigKey EditCustomerPhone = new ConfigKey("EditCustomerPhone");
    public static ConfigKey EditCustomerICode = new ConfigKey("EditCustomerNationalCode");
    public static ConfigKey EditCustomerAddress = new ConfigKey("EditCustomerAddress");
    public static ConfigKey EditCustomerStoreName = new ConfigKey("EditCustomerStoreName");

    public static ConfigKey OwnerKeys = new ConfigKey("OwnerKeys");
    public static ConfigKey BackOfficeType = new ConfigKey("BackOfficeType");
    public static ConfigKey PrizeCalcType = new ConfigKey("prizeCalcType");
    public static ConfigKey ShowStockLevel = new ConfigKey("ShowStockLevel");
    public static ConfigKey OrderPointCheckType = new ConfigKey("OrderPointCheckType");
    public static ConfigKey CheckBarcode = new ConfigKey("CheckBarcode");
    public static ConfigKey SendInactiveCustomers = new ConfigKey("SendInactiveCustomers");
    public static ConfigKey TakeOrderFromInactiveCustomers = new ConfigKey("TakeOrderFromInactiveCustomers");
    public static ConfigKey AllowDiscountOnSettlement = new ConfigKey("AllowDiscountOnSettlement");

    public static ConfigKey DcRef = new ConfigKey("DcRef");
    public static ConfigKey DcCode = new ConfigKey("DcCode");
    public static ConfigKey SaleOfficeRef = new ConfigKey("SaleOfficeRef");
    public static ConfigKey SaleOfficeId = new ConfigKey("SaleOfficeId");
    public static ConfigKey StockDCRef = new ConfigKey("StockDCRef");
    public static ConfigKey StockDCCode = new ConfigKey("StockDCCode");
    public static ConfigKey DealerRef = new ConfigKey("DealerRef");
    public static ConfigKey DealerCode = new ConfigKey("DealerCode");
    public static ConfigKey SupervisorRef = new ConfigKey("SupervisorRef");
    public static ConfigKey SupervisorCode = new ConfigKey("SupervisorCode");
    public static ConfigKey RoundType = new ConfigKey("RoundType");
    public static ConfigKey DigtsAfterDecimalDigits = new ConfigKey("DigtsAfterDecimalDigits");
    public static ConfigKey DigtsAfterDecimalForCPrice = new ConfigKey("DigtsAfterDecimalForCPrice");
    public static ConfigKey DeviceTaskPriorityEnabled = new ConfigKey("DeviceTaskPriorityEnabled");
    public static ConfigKey DoubleRequestIsEnabled = new ConfigKey("DoubleRequestIsEnabled");
    public static ConfigKey IgnoreOldInvoice = new ConfigKey("IgnoreOldInvoice");
    public static ConfigKey PriceClassEnabled = new ConfigKey("PriceClassEnabled");
    public static ConfigKey OnliveEvc = new ConfigKey("OnliveEvc");
    public static ConfigKey DownloadOldInvoicePolicy = new ConfigKey("DownloadOldInvoicePolicy");
    public static ConfigKey FirstTimeAfterGetTour = new ConfigKey("FirstTimeAfterGetTour");
    public static ConfigKey PrintInvoice = new ConfigKey("PrintInvoice");

    //    PRINT
    public static ConfigKey PrintCustomerName = new ConfigKey("PrintCustomerName");
    public static ConfigKey PrintDealerMobile = new ConfigKey("PrintDealerMobile");
    public static ConfigKey PrintFollowUpNo = new ConfigKey("PrintFollowUpNo");
    public static ConfigKey PrintTotalDiscountAmount = new ConfigKey("PrintSettlementDiscount");
    public static ConfigKey PrintFinalCustomerRemain = new ConfigKey("PrintCustomerFinalRemainCredit");
    public static ConfigKey PrintTotalAmountPaid = new ConfigKey("PrintTotalPayedAmount");
    public static ConfigKey PrintDistributionCenterName = new ConfigKey("PrintDCName");
    public static ConfigKey PrintFactorTime = new ConfigKey("PrintInvoicePrintTime");
    public static ConfigKey PrintCustomerRemain = new ConfigKey("PrintCustomerRemainCredit");
    public static ConfigKey PrintCompanyAddress = new ConfigKey("PrintCompanyAddress");
    public static ConfigKey PrintCompanyTelephone = new ConfigKey("PrintCompanyPhone");
    public static ConfigKey PrintCustomerMobile = new ConfigKey("PrintCustomerMobile");
    public static ConfigKey PrintCompanyEmail = new ConfigKey("PrintCompanyEmail");
    public static ConfigKey PrintStoreName = new ConfigKey("PrintStoreName");
    public static ConfigKey PrintCompanyName = new ConfigKey("PrintCompanyName");
    public static ConfigKey PrintTourNo = new ConfigKey("PrintTourNo");
    public static ConfigKey PrintReturn = new ConfigKey("PrintReturn");
    public static ConfigKey PrintProductRow = new ConfigKey("PrintRowInsteadOfProductCode");
    public static ConfigKey PrintDealerName = new ConfigKey("PrintDealerName");
    public static ConfigKey PrintTotalDiscounts = new ConfigKey("PrintTotalDiscount");
    public static ConfigKey PrintGrossPurchases = new ConfigKey("PrintTotalAmount");
    public static ConfigKey PrintSumOfComplications = new ConfigKey("PrintTotalCharge");
    public static ConfigKey PrintItemsCount = new ConfigKey("PrintTotalQty");
    public static ConfigKey PrintPrize = new ConfigKey("PrintPrize");
    public static ConfigKey PrintProductPrice = new ConfigKey("PrintProductPrice");
    public static ConfigKey PrintTotalPurchaseAmount = new ConfigKey("PrintTotalNetAmount");
    public static ConfigKey PrintCounts = new ConfigKey("InvoicePrintLimit");
    public static ConfigKey PrintFactorMessage = new ConfigKey("PrintFactorMessage");
    public static ConfigKey PrintCustomerRemainWithThisInvoice = new ConfigKey("PrintCustomerRemainWithThisInvoice");
    //TODO
    public static ConfigKey PrintShowUnitBasedOn = new ConfigKey("PrintShowUnitBasedOn");

    public static ConfigKey DisplayunitbyBasedOn = new ConfigKey("DisplayunitbyBasedOn");
    public static ConfigKey PrintInvoiceMessage = new ConfigKey("PrintInvoiceMessage");
    public static ConfigKey PrintaddtaxforeEachinvoiceitem = new ConfigKey("PrintaddtaxforeEachinvoiceitem");
    public static ConfigKey PrintCustomerbalances = new ConfigKey("PrintCustomerbalances");

    public static ConfigKey PrintTourSummary = new ConfigKey("PrintTourSummary");
    public static ConfigKey PrintStockLevelsBasedOn = new ConfigKey("PrintStockLevelsBasedOn");
    public static ConfigKey AllowSurplusInvoiceSettlement = new ConfigKey("AllowSurplusInvoiceSettlement");
    public static ConfigKey MaxOrderAmount = new ConfigKey("maxOrderAmount");
    public static ConfigKey DealerAdvanceCreditControl = new ConfigKey("DealerAdvanceCreditControl");
    public static ConfigKey DealerCreditDetails = new ConfigKey("DealerCreditDetails");
    public static ConfigKey AllowReturnDamagedProduct = new ConfigKey("AllowReturnDamagedProduct");
    public static ConfigKey AllowReturnIntactProduct = new ConfigKey("AllowReturnIntactProduct");
    public static ConfigKey AllowShowPriceStockReport = new ConfigKey("AllowShowPriceStockReport");
    public static ConfigKey OpenInvoicesBasedOn = new ConfigKey("OpenInvoicesBasedOn");
    public static ConfigKey DistributionCenterName = new ConfigKey("DistributionCenterName");
    public static ConfigKey AllowCashWithoutAdvancedCreditControl = new ConfigKey("AllowCashWithoutAdvancedCreditControl");
    public static ConfigKey AllowSettlementWithCredit = new ConfigKey("AllowSettlementWithCredit");
    public static ConfigKey ShowNormalItmDetail = new ConfigKey("ShowNormalItmDetail");
    public static ConfigKey AdvancedCreditControl = new ConfigKey("AdvancedCreditControl");

    public static ConfigKey SettlementDiscountPercent = new ConfigKey("SettlementDiscountPercent");
    public static ConfigKey AllowableRoundSettlementDigit = new ConfigKey("AllowableRoundSettlementDigit");
    public static ConfigKey AllowableRoundSettlementDigitDist = new ConfigKey("AllowableRoundSettlementDigitDist");
    public static ConfigKey ServerLanguage = new ConfigKey("ServerLanguage");
    public static ConfigKey ReportsPeriod = new ConfigKey("ReportsPeriod");
    public static ConfigKey CustomerGeneralPrice = new ConfigKey("CustomerGeneralPrice");
    public static ConfigKey VisitingCustomersInOrderOfAttendingInRoute = new ConfigKey("VisitingCustomersInOrderOfAttendingInRoute");

    public static ConfigKey SetCustomerLocation = new ConfigKey("SetCustomerLocation");
    public static ConfigKey AllowEditSellReturnAmount = new ConfigKey("AllowEditSellReturnAmount");
    public static ConfigKey SellReturnAmountTypeId = new ConfigKey("SellReturnAmountTypeId");

    public static ConfigKey MinimumOrderAmount = new ConfigKey("MinimumOrderAmount");
    public static ConfigKey MaximumOrderAmount = new ConfigKey("MaximumOrderAmount");
    public static ConfigKey MinimumFactorAmount = new ConfigKey("MinimumFactorAmount");
    public static ConfigKey MaximumFactorAmount = new ConfigKey("MaximumFactorAmount");
    public static ConfigKey MinimumOrderItemCount = new ConfigKey("MinimumOrderItemCount");
    public static ConfigKey MaximumOrderItemCount = new ConfigKey("MaximumOrderItemCount");
    public static ConfigKey CustomerOwnerType = new ConfigKey("CustomerOwnerType");
    public static ConfigKey EditCustomerCityZone = new ConfigKey("EditCustomerCityZone");
    public static ConfigKey CustomerPostalCode = new ConfigKey("CustomerPostalCode");
    public static ConfigKey CustomerNationalCode = new ConfigKey("CustomerNationalCode");
    public static ConfigKey EditCustomerCity = new ConfigKey("EditCustomerCity");
    public static ConfigKey EditCustomerState = new ConfigKey("EditCustomerState");
    public static ConfigKey EditCustomerOwnerType = new ConfigKey("EditCustomerOwnerType");
    public static ConfigKey EditCustomerCounty = new ConfigKey("EditCustomerCounty");
    public static ConfigKey EditCustomerPostalCode = new ConfigKey("EditCustomerPostalCode");
    public static ConfigKey EditCustomerLevel = new ConfigKey("EditCustomerLevel");
    public static ConfigKey EditCustomerCategory = new ConfigKey("EditCustomerCategory");
    public static ConfigKey EditCustomerActivity = new ConfigKey("EditCustomerActivity");

    public static ConfigKey UserDialogPreference = new ConfigKey("UserDialogPreference");
    public static ConfigKey CustomerCategory = new ConfigKey("CustomerCategory");
    public static ConfigKey CustomerLevel = new ConfigKey("CustomerLevel");
    public static ConfigKey CustomerActivity = new ConfigKey("CustomerActivity");
    public static ConfigKey CustomerCityZone = new ConfigKey("CustomerCityZone");
    public static ConfigKey CustomerCity = new ConfigKey("CustomerCity");
    public static ConfigKey CustomerCounty = new ConfigKey("CustomerCounty");
    public static ConfigKey CustomerState = new ConfigKey("CustomerState");
    public static ConfigKey CustomerAddress = new ConfigKey("CustomerAddress");
    public static ConfigKey CustomerMobile = new ConfigKey("CustomerMobile");
    public static ConfigKey CustomerPhone = new ConfigKey("CustomerPhone");

    public static ConfigKey FreeStockRegistration = new ConfigKey("FreeStockRegistration");

    // NGT-2183 start and end time for distance control
    public static ConfigKey StartTimeControlDash = new ConfigKey("StartTimeControlDash");
    public static ConfigKey EndTimeControlDash = new ConfigKey("EndTimeControlDash");
    public static ConfigKey InventoryControl = new ConfigKey("InventoryControl");

    public static ConfigKey RefRetOrder = new ConfigKey("RefRetOrder");

    public static ConfigKey AllowEditSettlement = new ConfigKey("AllowEditSettlement");

    public static ConfigKey SellTypeStatusType = new ConfigKey("SellTypeStatusType");

    public static ConfigKey NewDiscount = new ConfigKey("NewDiscount");
    public static ConfigKey DeviceWorkingHour = new ConfigKey("DeviceWorkingHour");
    public static ConfigKey StartDeviceWorkingHour = new ConfigKey("StartDeviceWorkingHour");
    public static ConfigKey EndDeviceWorkingHour = new ConfigKey("EndDeviceWorkingHour");
    public static ConfigKey StartWorkingHour = new ConfigKey("StartWorkingHour");
    public static ConfigKey EndWorkingHour = new ConfigKey("EndWorkingHour");

    public static ConfigKey SimplePresale = new ConfigKey("SimplePresale");
    public static ConfigKey RecSysServer = new ConfigKey("RecSysServer");
    public static ConfigKey RecSysLicense = new ConfigKey("RecSysLicense");

    public static ConfigKey SystemPaymentTypeId = new ConfigKey("SystemPaymentTypeId");
    public static ConfigKey OrderValidationErrorType = new ConfigKey("OrderValidationErrorType");
    public static ConfigKey OrderBedLimit = new ConfigKey("OrderBedLimit");
    public static ConfigKey OrderAsnLimit = new ConfigKey("OrderAsnLimit");
    public static ConfigKey ReplacementRegistration = new ConfigKey("ReplacementRegistration");

    public static ConfigKey ReturnValidationErrorType = new ConfigKey("ReturnValidationErrorType");
    public static ConfigKey CustomizePrint = new ConfigKey("CustomizePrint");
    public static ConfigKey WaitingControl = new ConfigKey("WaitingControl");
    public static ConfigKey SupervisorGrs = new ConfigKey("SupervisorGrs");
    public static ConfigKey TrackingInterval = new ConfigKey("TrackingInterval");
    public static ConfigKey TrackingWaitTime = new ConfigKey("TrackingWaitTime");
    public static ConfigKey TrackingSmallestDisplacement = new ConfigKey("TrackingSmallestDisplacement");
    public static ConfigKey CancelRegistration = new ConfigKey("CancelRegistration");
    public static ConfigKey SettlementAllocation = new ConfigKey("SettlementAllocation");
    public static ConfigKey ShowInventoryMinusUnconfirmedRequests = new ConfigKey("ShowInventoryMinusUnconfirmedRequests");
    public static ConfigKey ApplyCurrentOrdersInInventory = new ConfigKey("ApplyCurrentOrdersInInventory");
    public static ConfigKey DistAutoSync = new ConfigKey("DistAutoSync");
    public static ConfigKey DistAllowSync = new ConfigKey("DistAllowSync");

    public static ConfigKey DeviceSettingNo = new ConfigKey("DeviceSettingNo");
    public static ConfigKey ScientificVisit = new ConfigKey("ScientificVisit");

    public static ConfigKey SendPromotionPreview = new ConfigKey("SendPromotionPreview");

    public static ConfigKey DealerInformationNotification = new ConfigKey("DealerInformationNotification");

    public static ConfigKey TrackingServerChk = new ConfigKey("TrackingServerChk");
    public static ConfigKey TrackingServerUrl = new ConfigKey("TrackingServerUrl");

    public static ConfigKey CheckSingleSendOperation = new ConfigKey("CheckSingleSendOperation");


    public String getKey() {
        return this.key;
    }

    private String key;

    private ConfigKey(String key) {
        this.key = key;
    }

}
