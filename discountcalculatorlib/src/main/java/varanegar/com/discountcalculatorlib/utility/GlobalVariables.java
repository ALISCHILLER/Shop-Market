package varanegar.com.discountcalculatorlib.utility;


import varanegar.com.discountcalculatorlib.handler.IDiscountInitializeHandlerV3;
import varanegar.com.discountcalculatorlib.utility.enumerations.ApplicationType;
import varanegar.com.discountcalculatorlib.utility.enumerations.BackOfficeType;
import varanegar.com.discountcalculatorlib.utility.enumerations.BackOfficeVersion;
import varanegar.com.discountcalculatorlib.viewmodel.OwnerKeysViewModel;

public class GlobalVariables {

    /******************************************************/
    /*  BackOfficeType  */
    /******************************************************/
    private static IDiscountInitializeHandlerV3 initializeHandler;
    public static IDiscountInitializeHandlerV3 getDiscountInitializeHandler() {
        return initializeHandler;
    }

    public static void setDiscountInitializeHandler(IDiscountInitializeHandlerV3 initHandler) {
        initializeHandler = initHandler;
    }

    /******************************************************/
    /*  ApplicationType  */
    /******************************************************/
    private static ApplicationType applicationType;
    public static ApplicationType getApplicationType() {
        if (applicationType == null)
            return ApplicationType.DISTRIBUTION;
        return applicationType;
    }

    public static void setApplicationType(ApplicationType appType) {
        applicationType = appType;
    }
    /******************************************************/
    /*  BackOfficeType  */
    /******************************************************/
    private static BackOfficeType backOffice;
    public static BackOfficeType getBackOffice() {
        if (backOffice == null)
            return BackOfficeType.VARANEGAR;
        return backOffice;
    }

    public static void setBackOffice(int backOfficecode) {
        backOffice = BackOfficeType.getBackOfficeType(backOfficecode);
    }
    /******************************************************/

    private static Boolean isThirdParty;
    public static Boolean getIsThirdParty() {
        return isThirdParty;
    }
    public static void setIsThirdParty(boolean thirdParty) {
        isThirdParty = thirdParty;
    }

    /******************************************************/
    /*  BackOfficeVersion  */
    /******************************************************/
    private static BackOfficeVersion backOfficeversion;
    public static BackOfficeVersion getBackOfficeVersion() {
        if (backOfficeversion == null)
            return BackOfficeVersion.SDS19;
        return backOfficeversion;
    }

    public static void setBackOfficeVersion(int backOfficeVersionCode) {
        backOfficeversion = BackOfficeVersion.getBackOfficeVersion(backOfficeVersionCode);
    }


    /******************************************************/
    /******************************************************/
    /*  CalcOnline  */
    /******************************************************/
    private static boolean calcOnline;
    public static boolean isCalcOnline() {
        return calcOnline;
    }

    public static void setCalcOnline(boolean online) {
        calcOnline = online;
    }
    /******************************************************/
    /******************************************************/
    /*  ServerUrl  */
    /******************************************************/
    private static String serverUrl;
    public static String getServerUrl() { return serverUrl; }

    private static boolean calcDiscount;
    public static boolean getCalcDiscount() { return calcDiscount; }

    private static boolean calcSaleRestriction;
    public static boolean getCalcSaleRestriction() { return calcSaleRestriction; }

    private static boolean calcPaymentType;
    public static boolean getCalcPaymentType() { return calcPaymentType; }

    public static void setServerUrl(String url,  boolean CalcDiscount,boolean CalcSaleRestriction ,boolean CalcPaymentType) {
        String serverAddress = url;
        if (serverAddress.endsWith("/")){
            if (serverAddress.length() > 0 && serverAddress.charAt(serverAddress.length() - 1) == 'x') {
                serverAddress = serverAddress.substring(0, serverAddress.length() - 1);
            }
        }
        serverUrl = serverAddress;
        calcDiscount = CalcDiscount;
        calcSaleRestriction = CalcSaleRestriction;
        calcPaymentType = CalcPaymentType;
    }

    /******************************************************/
    /******************************************************/
    /*  OwnerKeysViewModel  */
    /******************************************************/
    private static OwnerKeysViewModel ownerKeysViewModel;
    public static OwnerKeysViewModel getOwnerKeys() {return ownerKeysViewModel;}
    public static void setOwnerKeys(OwnerKeysViewModel ownerKeys) {
        ownerKeysViewModel = ownerKeys;
    }
    /******************************************************/

    /******************************************************/
    /*  Database Directory Path  */
    /******************************************************/
    public static final String discountDbName = "DiscountDbV3";
    public static String getDiscountDbName() {
        return discountDbName+".db";
    }
    public static String getDiscountDbShortName() {
        return discountDbName;
    }

    private static String discountDbPath;
    public static String getDiscountDbPath() {
      return discountDbPath;
    }
    public static void setDiscountDbPath(String path) {
        discountDbPath = path;
    }

    /******************************************************/
    private static int dcRef;
    public static void setDcRef(int dcref) {
        dcRef = dcref;
    }
    public static int getDcRef() {
        return dcRef;
    }
    /******************************************************/
    private static int decimalDigits;
    public static void setDecimalDigits(int digits) {
        decimalDigits = digits;
    }
    public static int getDecimalDigits() {
        return decimalDigits;
    }
    /******************************************************/
    private static int digtsAfterDecimalForCPrice;
    public static void setDigtsAfterDecimalForCPrice(int digits) { digtsAfterDecimalForCPrice = digits; }
    public static int getDigtsAfterDecimalForCPrice() {
        return digtsAfterDecimalForCPrice;
    }
    /******************************************************/
    private static boolean prizeCalcType;
    public static void setPrizeCalcType(boolean prizeCalc) {
        prizeCalcType = prizeCalc;
    }
    public static boolean getPrizeCalcType() { return prizeCalcType;  }
    /******************************************************/
    private static int roundType;
    public static void setRoundType(int roundtype) {
        roundType = roundtype;
    }
    public static int getRoundType() { return roundType;  }
    /******************************************************/
    public static boolean pahpatStatus = false;
    public static void setPahpatStatus (boolean status) {
        pahpatStatus = status;
    }
    public static boolean getPahpatStatus () {
        return pahpatStatus;
    }
}
