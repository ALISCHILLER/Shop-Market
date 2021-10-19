package com.varanegar.vaslibrary.manager.invoiceinfo;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.model.invoiceinfo.InvoicePaymentInfoModel;
import com.varanegar.vaslibrary.model.invoiceinfo.InvoicePaymentInfoModelRepository;
import com.varanegar.vaslibrary.model.invoiceinfo.InvoicePaymentInfoViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/26/2018.
 */

public class InvoicePaymentInfoManager extends BaseManager<InvoicePaymentInfoModel> {
    public InvoicePaymentInfoManager(@NonNull Context context) {
        super(context, new InvoicePaymentInfoModelRepository());
    }

    public void addOrUpdate(List<InvoicePaymentInfoViewModel> infoViewModels, @NonNull UUID paymentId) throws ValidationException, DbException {
        if (infoViewModels.size() == 0)
            return;

        List<InvoicePaymentInfoModel> infoModels = new ArrayList<>();
        for (InvoicePaymentInfoViewModel infoViewModel :
                infoViewModels) {
            InvoicePaymentInfoModel infoModel = new InvoicePaymentInfoModel();
            infoModel.Amount = infoViewModel.PaidAmount;
            infoModel.InvoiceId = infoViewModel.InvoiceId;
            infoModel.PaymentId = paymentId;
            if (paymentId.equals(infoViewModel.PaymentId) || infoViewModel.PaymentId == null)
                infoModel.UniqueId = infoViewModel.UniqueId != null ? infoViewModel.UniqueId : UUID.randomUUID();
            else
                infoModel.UniqueId = UUID.randomUUID();
            infoModels.add(infoModel);
        }
        insertOrUpdate(infoModels);
    }
}
