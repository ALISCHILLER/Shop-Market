package com.varanegar.vaslibrary.ui.fragment.settlement;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.varanegar.framework.util.component.CuteAlertDialog;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.validation.ValidationListener;
import com.varanegar.framework.validation.Validator;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;

import java.util.UUID;

/**
 * Created by A.Torabi on 12/5/2017.
 */

public abstract class PaymentDialog extends CuteAlertDialog implements ValidationListener {
    private PaymentDialogCallBack paymentDialogCallBack;
    private Currency totalAmount;
    private UUID customerId;
    private UUID paymentId;
    private Currency remainedAmount;
    private Currency customerRemainAmount;

    protected void showErrorMessage(@StringRes int resId) {
        CuteMessageDialog dialog = new CuteMessageDialog(getContext());
        dialog.setPositiveButton(R.string.ok, null);
        dialog.setIcon(Icon.Error);
        dialog.setMessage(resId);
        dialog.setTitle(R.string.error);
        dialog.show();
    }

    protected void showErrorMessage(String message) {
        CuteMessageDialog dialog = new CuteMessageDialog(getContext());
        dialog.setPositiveButton(R.string.ok, null);
        dialog.setIcon(Icon.Error);
        dialog.setMessage(message);
        dialog.setTitle(R.string.error);
        dialog.show();
    }

    public void setCallBack(@NonNull PaymentDialogCallBack paymentDialogCallBack) {
        this.paymentDialogCallBack = paymentDialogCallBack;
    }

    /**
     * @param customerId  UniqueId of the customer
     * @param totalAmount The remaining amount that this payment should be done
     * @param paymentId   Set this parameter if you want to update a payment that is already saved
     */
    public void setArguments(@NonNull UUID customerId, @NonNull Currency totalAmount, @NonNull Currency remainedAmount, @Nullable UUID paymentId, @Nullable Currency customerRemain) {
        Bundle bundle = new Bundle();
        bundle.putString("cec9f4ef-eddb-4c82-acc8-bac150e84788", customerId.toString());
        bundle.putDouble("34283a5e-b848-440f-9e28-9b42a9fe6756", totalAmount.doubleValue());
        bundle.putDouble("8162e7f4-07f2-4b55-8b99-98489b925ec2", remainedAmount.doubleValue());
        if (paymentId != null)
            bundle.putString("6365c023-5c1f-42a5-b99c-736057ce96b7", paymentId.toString());
        if (customerRemain != null) {
            bundle.putBoolean("52ddf217-a5e0-40a5-9daa-d6b657020c18", true);
            bundle.putDouble("d03df068-e779-11ea-adc1-0242ac120002", customerRemain.doubleValue());
        } else
            bundle.putBoolean("52ddf217-a5e0-40a5-9daa-d6b657020c18", false);
        setArguments(bundle);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setSizingPolicy(SizingPolicy.Maximum);
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null)
            throw new NullPointerException("You have to set arguments");
        else {
            try {
                totalAmount = Currency.valueOf(bundle.getDouble("34283a5e-b848-440f-9e28-9b42a9fe6756"));
                remainedAmount = Currency.valueOf(bundle.getDouble("8162e7f4-07f2-4b55-8b99-98489b925ec2"));
                customerId = UUID.fromString(bundle.getString("cec9f4ef-eddb-4c82-acc8-bac150e84788"));
                String paymentIdString = bundle.getString("6365c023-5c1f-42a5-b99c-736057ce96b7");
                if (bundle.getBoolean("52ddf217-a5e0-40a5-9daa-d6b657020c18"))
                    customerRemainAmount = Currency.valueOf(bundle.getDouble("d03df068-e779-11ea-adc1-0242ac120002"));
                else
                    customerRemainAmount = null;
                if (paymentIdString != null)
                    paymentId = UUID.fromString(paymentIdString);
            } catch (Exception e) {
                throw new NullPointerException("You have to set arguments");
            }
        }
    }

    protected void runCallBack() {
        if (paymentDialogCallBack != null)
            paymentDialogCallBack.saved();
    }

    Validator validator = new Validator();

    public Currency getTotalAmount() {
        return totalAmount;
    }

    public Currency getRemainedAmount() {
        return remainedAmount;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public Currency getCustomerRemainAmount () {
        return customerRemainAmount;
    }
}
