package com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader.A910Payment;
/**
 * Created by saeede on 7/14/2021.
 */
public class BPMPaymentData {
    public enum TransactionType{
        PURCHASE,
        PAYMENT,
        MULTIPAYMENT
    }
    public String versionName;                 /// Mandatory
    public String sessionId;                    /// Mandatory
    public int applicationId;                  /// Mandatory
    public long totalAmount;                   /// Mandatory
    public String payerId;                       /// Optional
    public String accountId;                   /// Complementary
    public TransactionType transactionType;    /// Mandatory
    public String paymentDetail;               /// Complementary
    public String merchantMessage;             /// Optional
    public String merchantAdditionalData;      /// Optional
    public String cardHolderMobile;            /// Optional
    public String[] extras;                    /// Optional
    public boolean printPaymentDetails = false;/// Optional, default = false

    public void setCardHolderMobile(String cardHolderMobile) {
        this.cardHolderMobile = cardHolderMobile;
    }

    private boolean saveDetail = false;         /// Optional, default = false

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getPaymentDetail() {
        return paymentDetail;
    }

    public void setPaymentDetail(String paymentDetail) {
        this.paymentDetail = paymentDetail;
    }

    public String getMerchantMessage() {
        return merchantMessage;
    }

    public void setMerchantMessage(String merchantMessage) {
        this.merchantMessage = merchantMessage;
    }

    /*public long getMerchantDiscountAmount() {
        return merchantDiscountAmount;
    }

    public void setMerchantDiscountAmount(long merchantDiscountAmount) {
        this.merchantDiscountAmount = merchantDiscountAmount;
    }*/

    public String getMerchantAdditionalData() {
        return merchantAdditionalData;
    }

    public void setMerchantAdditionalData(String merchantAdditionalData) {
        this.merchantAdditionalData = merchantAdditionalData;
    }

    public String[] getExtras() {
        return extras;
    }

    public void setExtras(String[] extras) {
        this.extras = extras;
    }

    public boolean isSaveDetail() {
        return saveDetail;
    }

    public void setSaveDetail(boolean saveDetail) {
        this.saveDetail = saveDetail;
    }
}
