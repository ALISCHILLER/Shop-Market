package com.varanegar.vaslibrary.ui.report.report_new.orderReturn_report.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReturnDealerModel implements Parcelable {

    @SerializedName("dealerName")
    private String dealerName;
    @SerializedName("dealerCode")
    private String dealerCode;
    @SerializedName("productCountCa")
    private Double productCountCa;

    @SerializedName("dealersItems")
    private List<ReturnCustomerModel> customerModels;

    protected ReturnDealerModel(Parcel in) {
        dealerName = in.readString();
        dealerCode = in.readString();
        if (in.readByte() == 0) {
            productCountCa = null;
        } else {
            productCountCa = in.readDouble();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dealerName);
        dest.writeString(dealerCode);
        if (productCountCa == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(productCountCa);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReturnDealerModel> CREATOR = new Creator<ReturnDealerModel>() {
        @Override
        public ReturnDealerModel createFromParcel(Parcel in) {
            return new ReturnDealerModel(in);
        }

        @Override
        public ReturnDealerModel[] newArray(int size) {
            return new ReturnDealerModel[size];
        }
    };

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getDealerCode() {
        return dealerCode;
    }

    public void setDealerCode(String dealerCode) {
        this.dealerCode = dealerCode;
    }

    public Double getProductCountCa() {
        return productCountCa;
    }

    public void setProductCountCa(Double productCountCa) {
        this.productCountCa = productCountCa;
    }

    public List<ReturnCustomerModel> getCustomerModels() {
        return customerModels;
    }

    public void setCustomerModels(List<ReturnCustomerModel> customerModels) {
        this.customerModels = customerModels;
    }
}
