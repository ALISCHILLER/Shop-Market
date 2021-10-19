package com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader.A910Payment;
/**
 * Created by saeede on 7/14/2021.
 */
public class BPMPaymentResult {

    private String versionName;                 /// Mandatory
    private String sessionId;     /// Mandatory, received from CallerApp
    private int applicationId;                  /// Mandatory, received from CallerApp
    private long transactionAmount;             /// Mandatory, received from CallerApp
    private long acquirerDiscountAmount;        /// Optional, received from SW.
    private int resultCode;                     /// Mandatory
    private String resultDescription;           /// Optional
    private int retrievalReferencedNumber;      /// Mandatory
    private long referenceID;                   /// Mandatory
    private String dateOfTransaction;           /// Mandatory
    private String timeOfTransaction;           /// Mandatory
    private String maskedCardNumber;            /// Mandatory
    private String BIN;                         /// Mandatory
    private String terminalID;                  /// Mandatory
    private String acquirerMessage;             /// Optional
    private String[] extras;                    /// Optional

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

    public long getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(long transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public long getAcquirerDiscountAmount() {
        return acquirerDiscountAmount;
    }

    public void setAcquirerDiscountAmount(long acquirerDiscountAmount) {
        this.acquirerDiscountAmount = acquirerDiscountAmount;
    }

    public String getResultDescription() {
        return resultDescription;
    }

    public void setResultDescription(String resultDescription) {
        this.resultDescription = resultDescription;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int transactionResult) {
        this.resultCode = transactionResult;
    }

    public int getRetrievalReferencedNumber() {
        return retrievalReferencedNumber;
    }

    public void setRetrievalReferencedNumber(int retrievalReferencedNumber) {
        this.retrievalReferencedNumber = retrievalReferencedNumber;
    }

    public long getReferenceID() {
        return referenceID;
    }

    public void setReferenceID(long referenceID) {
        this.referenceID = referenceID;
    }

    public String getDateOfTransaction() {
        return dateOfTransaction;
    }

    public void setDateOfTransaction(String dateOfTransaction) {
        this.dateOfTransaction = dateOfTransaction;
    }

    public String getTimeOfTransaction() {
        return timeOfTransaction;
    }

    public void setTimeOfTransaction(String timeOfTransaction) {
        this.timeOfTransaction = timeOfTransaction;
    }

    public String getMaskedCardNumber() {
        return maskedCardNumber;
    }

    public void setMaskedCardNumber(String maskedCardNumber) {
        this.maskedCardNumber = maskedCardNumber;
    }

    public String getBIN() {
        return BIN;
    }

    public void setBIN(String BIN) {
        this.BIN = BIN;
    }

    public String getTerminalID() {
        return terminalID;
    }

    public void setTerminalID(String terminalID) {
        this.terminalID = terminalID;
    }

    public String getAcquirerMessage() {
        return acquirerMessage;
    }

    public void setAcquirerMessage(String acquirerMessage) {
        this.acquirerMessage = acquirerMessage;
    }

    public String[] getExtras() {
        return extras;
    }

    public void setExtras(String[] extras) {
        this.extras = extras;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("");
        sb.append("Version Name: " + getVersionName() + "\n");
        sb.append("App Id: " + getApplicationId() + "\n");
        sb.append("session Id: " + (getSessionId()) + "\n");
        sb.append("Total Amount " + getTransactionAmount() + "\n");
        sb.append("acquirer Discount Amount: " + getAcquirerDiscountAmount() + "\n");
        sb.append("Transaction Result: " + getResultCode() + "\n");
        sb.append("Result Description: " + getResultDescription() + "\n");
        sb.append("Retrieval reference Number: " + getRetrievalReferencedNumber() + "\n");
        sb.append("Reference Id: " + getReferenceID() + "\n");
        sb.append("Date: " + getDateOfTransaction() + "\n");
        sb.append("Time: " + getTimeOfTransaction() + "\n");
        sb.append("Card Number: " + getMaskedCardNumber() + "\n");
        sb.append("BIN: " + getBIN() + "\n");
        sb.append("Terminal Id: " + getTerminalID() +"\n");
        sb.append("Acquirer Message: " + getAcquirerMessage() + "\n");
        if (extras !=  null) {
            int i = 1;
            for (String extra : extras) {
                sb.append("Extra" + i + " : " + extra + "\n");
            }
        }
        return sb.toString();
    }
}
