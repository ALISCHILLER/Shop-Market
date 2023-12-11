package com.varanegar.vaslibrary.manager.paymentmanager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.enums.ThirdPartyPaymentTypes;
import com.varanegar.vaslibrary.manager.CustomerCallOrderOrderViewManager;
import com.varanegar.vaslibrary.manager.CustomerCallReturnLinesViewManager;
import com.varanegar.vaslibrary.manager.OldInvoiceHeaderViewManager;
import com.varanegar.vaslibrary.manager.OrderAmount;
import com.varanegar.vaslibrary.manager.ValidPayTypeManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallOrderManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.invoiceinfo.InvoicePaymentInfoManager;
import com.varanegar.vaslibrary.manager.paymentmanager.exceptions.CashPaymentException;
import com.varanegar.vaslibrary.manager.paymentmanager.exceptions.CheckPaymentException;
import com.varanegar.vaslibrary.manager.paymentmanager.exceptions.ControlPaymentException;
import com.varanegar.vaslibrary.manager.paymentmanager.exceptions.CreditPaymentException;
import com.varanegar.vaslibrary.manager.paymentmanager.exceptions.DiscountPaymentException;
import com.varanegar.vaslibrary.manager.paymentmanager.exceptions.DuplicatePaymentException;
import com.varanegar.vaslibrary.manager.paymentmanager.exceptions.PaymentNotFoundException;
import com.varanegar.vaslibrary.manager.paymentmanager.exceptions.PosPaymentException;
import com.varanegar.vaslibrary.manager.paymentmanager.exceptions.ThirdPartyControlPaymentChangedException;
import com.varanegar.vaslibrary.manager.paymentmanager.paymenttypes.CardPayment;
import com.varanegar.vaslibrary.manager.paymentmanager.paymenttypes.CashPayment;
import com.varanegar.vaslibrary.manager.paymentmanager.paymenttypes.CheckPayment;
import com.varanegar.vaslibrary.manager.paymentmanager.paymenttypes.CreditPayment;
import com.varanegar.vaslibrary.manager.paymentmanager.paymenttypes.DiscountPayment;
import com.varanegar.vaslibrary.manager.paymentmanager.paymenttypes.PaymentType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigMap;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderModel;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallType;
import com.varanegar.vaslibrary.model.invoiceinfo.InvoicePaymentInfoViewModel;
import com.varanegar.vaslibrary.model.oldinvoiceheaderview.OldInvoiceHeaderViewModel;
import com.varanegar.vaslibrary.model.payment.Payment;
import com.varanegar.vaslibrary.model.payment.PaymentModel;
import com.varanegar.vaslibrary.model.payment.PaymentModelRepository;
import com.varanegar.vaslibrary.model.paymentTypeOrder.PaymentTypeOrderModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.ui.fragment.settlement.CustomerPayment;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Created by A.Torabi on 12/9/2017.
 */

/**
 * محاسبه عملیات پرداخت
 * settlement Fragment
 * confirmAction
 */
public class PaymentManager extends BaseManager<PaymentModel> {
    public PaymentManager(@NonNull Context context) {
        super(context, new PaymentModelRepository());
    }
    private List<PaymentModel> paymentsList;

    /**
     * @param payment
     * @param customerId
     * @param infoViewModels
     * @throws DuplicatePaymentException
     * @throws ValidationException
     * @throws DbException
     */
    public void saveCheckPayment(@NonNull CheckPayment payment, @NonNull UUID customerId, @Nullable List<InvoicePaymentInfoViewModel> infoViewModels) throws DuplicatePaymentException, ValidationException, DbException {
        Query query = new Query();
        query.from(Payment.PaymentTbl)
                .whereAnd(Criteria.equals(Payment.CheckNumber, payment.CheckNumber)
                        .and(Criteria.equals(Payment.CheckAccountNumber, payment.CheckAccountNumber))
                        .and(Criteria.equals(Payment.PaymentType, PaymentType.Check)));
        PaymentModel paymentModel = getItem(query);
        if (paymentModel != null)
            throw new DuplicatePaymentException();
        UUID paymentId = UUID.randomUUID();
        paymentModel = new PaymentModel();
        paymentModel.UniqueId = paymentId;
        paymentModel.Amount = payment.Amount;
        paymentModel.BankId = payment.BankId;
        paymentModel.CityId = payment.CityId;
        paymentModel.ChqAccountName = payment.AccountName;
        paymentModel.ChqBranchName = payment.BranchName;
        paymentModel.ChqBranchCode = payment.BranchCode;
        paymentModel.CheckAccountNumber = payment.CheckAccountNumber;
        paymentModel.CheckNumber = payment.CheckNumber;
        paymentModel.SayadNumber = payment.SayadNumber;
        paymentModel.CustomerId = customerId;
        paymentModel.Date = payment.Date;
        paymentModel.ChqDate = payment.ChqDate;
        paymentModel.PaymentType = PaymentType.Check;
        paymentModel.RowNo = 5;
        insertOrUpdate(paymentModel);
        new CustomerCallManager(getContext()).removePaymentCall(customerId);
        if (infoViewModels != null) {
            InvoicePaymentInfoManager manager = new InvoicePaymentInfoManager(getContext());
            manager.addOrUpdate(infoViewModels, paymentId);
        }
    }

    /**
     * @param paymentId
     * @param payment
     * @param customerId
     * @param infoViewModels
     * @throws PaymentNotFoundException
     * @throws DuplicatePaymentException
     * @throws ValidationException
     * @throws DbException
     */
    public void updateCheckPayment(@NonNull UUID paymentId, @NonNull CheckPayment payment, @NonNull UUID customerId, @Nullable List<InvoicePaymentInfoViewModel> infoViewModels) throws PaymentNotFoundException, DuplicatePaymentException, ValidationException, DbException {
        PaymentModel paymentModel = getItem(paymentId);
        if (paymentModel == null)
            throw new PaymentNotFoundException();
        paymentModel.Amount = payment.Amount;
        paymentModel.BankId = payment.BankId;
        paymentModel.CityId = payment.CityId;
        paymentModel.ChqAccountName = payment.AccountName;
        paymentModel.ChqBranchName = payment.BranchName;
        paymentModel.ChqBranchCode = payment.BranchCode;
        paymentModel.CheckAccountNumber = payment.CheckAccountNumber;
        paymentModel.CheckNumber = payment.CheckNumber;
        paymentModel.SayadNumber = payment.SayadNumber;
        paymentModel.CustomerId = customerId;
        paymentModel.Date = payment.Date;
        paymentModel.ChqDate = payment.ChqDate;
        paymentModel.PaymentType = PaymentType.Check;
        paymentModel.RowNo = 5;

        Query query = new Query();
        query.from(Payment.PaymentTbl)
                .whereAnd(Criteria.equals(Payment.CheckNumber, payment.CheckNumber)
                        .and(Criteria.equals(Payment.CheckAccountNumber, payment.CheckAccountNumber))
                        .and(Criteria.equals(Payment.PaymentType, PaymentType.Check.toString()))
                        .and(Criteria.notEquals(Payment.UniqueId, paymentId.toString())));
        PaymentModel anotherPayment = getItem(query);
        if (anotherPayment != null)
            throw new DuplicatePaymentException();
        insertOrUpdate(paymentModel);
        new CustomerCallManager(getContext()).removePaymentCall(customerId);
        if (infoViewModels != null) {
            InvoicePaymentInfoManager manager = new InvoicePaymentInfoManager(getContext());
            manager.addOrUpdate(infoViewModels, paymentId);
        }
    }

    /**
     * @param payment
     * @param customerId
     * @param infoViewModels
     * @throws DuplicatePaymentException
     * @throws ValidationException
     * @throws DbException
     */
    public void saveCardPayment(@NonNull CardPayment payment, @NonNull UUID customerId, @Nullable List<InvoicePaymentInfoViewModel> infoViewModels) throws DuplicatePaymentException, ValidationException, DbException {
        Query query = new Query();
        query.from(Payment.PaymentTbl)
                .whereAnd(Criteria.equals(Payment.Ref, payment.Ref)
                        .and(Criteria.equals(Payment.PaymentType, PaymentType.Card.toString())));
        PaymentModel paymentModel = getItem(query);
        if (paymentModel != null)
            throw new DuplicatePaymentException();
        UUID paymentId = UUID.randomUUID();
        paymentModel = new PaymentModel();
        paymentModel.UniqueId = paymentId;
        paymentModel.CustomerId = customerId;
        paymentModel.Ref = payment.Ref;
        paymentModel.Date = payment.Date;
        paymentModel.ChqDate = payment.Date;
        paymentModel.Amount = payment.Amount;
        paymentModel.PaymentType = PaymentType.Card;
        paymentModel.RowNo = 4;
        insertOrUpdate(paymentModel);
        new CustomerCallManager(getContext()).removePaymentCall(customerId);
        if (infoViewModels != null) {
            Timber.i("InvoicePaymentInfo for customerId " + customerId + " for card payment. InvoicePaymentInfo.size=" + infoViewModels.size());
            for (InvoicePaymentInfoViewModel info :
                    infoViewModels) {
                Timber.i("InvoiceId " + info.InvoiceId + ", PaymentId " + paymentId + ", Amount " + info.PaidAmount);
            }
            InvoicePaymentInfoManager manager = new InvoicePaymentInfoManager(getContext());
            manager.addOrUpdate(infoViewModels, paymentId);
        }
    }

    /**
     * @param paymentId
     * @param payment
     * @param customerId
     * @param infoViewModels
     * @throws PaymentNotFoundException
     * @throws DuplicatePaymentException
     * @throws ValidationException
     * @throws DbException
     */
    public void updateCardPayment(@NonNull UUID paymentId, @NonNull CardPayment payment, @NonNull UUID customerId, @Nullable List<InvoicePaymentInfoViewModel> infoViewModels) throws PaymentNotFoundException, DuplicatePaymentException, ValidationException, DbException {
        PaymentModel paymentModel = getItem(paymentId);
        if (paymentModel == null)
            throw new PaymentNotFoundException();
        paymentModel.CustomerId = customerId;
        paymentModel.Ref = payment.Ref;
        paymentModel.Date = payment.Date;
        paymentModel.ChqDate = payment.Date;
        paymentModel.Amount = payment.Amount;
        paymentModel.PaymentType = PaymentType.Card;
        paymentModel.RowNo = 4;

        Query query = new Query();
        query.from(Payment.PaymentTbl)
                .whereAnd(Criteria.equals(Payment.Ref, payment.Ref)
                        .and(Criteria.equals(Payment.PaymentType, PaymentType.Card.toString()))
                        .and(Criteria.notEquals(Payment.UniqueId, paymentId.toString())));
        PaymentModel anotherPayment = getItem(query);
        if (anotherPayment != null)
            throw new DuplicatePaymentException();

        insertOrUpdate(paymentModel);
        new CustomerCallManager(getContext()).removePaymentCall(customerId);
        if (infoViewModels != null) {
            Timber.i("InvoicePaymentInfo for customerId " + customerId + " for card payment. InvoicePaymentInfo.size=" + infoViewModels.size());
            for (InvoicePaymentInfoViewModel info :
                    infoViewModels) {
                Timber.i("InvoiceId " + info.InvoiceId + ", PaymentId " + paymentId + ", Amount " + info.PaidAmount);
            }
            InvoicePaymentInfoManager manager = new InvoicePaymentInfoManager(getContext());
            manager.addOrUpdate(infoViewModels, paymentId);
        }

    }

    /**
     * Inserts or updates a cash payment. We have only one cash payment per customer
     *
     * @param payment
     * @param customerId
     * @param infoViewModels
     * @throws ValidationException
     * @throws DbException
     */
    public void saveCashPayment(@NonNull CashPayment payment, @NonNull UUID customerId, @Nullable List<InvoicePaymentInfoViewModel> infoViewModels) throws ValidationException, DbException {
        Query query = new Query();
        query.from(Payment.PaymentTbl)
                .whereAnd(Criteria.equals(Payment.CustomerId, customerId.toString())
                        .and(Criteria.equals(Payment.PaymentType, PaymentType.Cash.toString())));
        PaymentModel paymentModel = getItem(query);
        UUID paymentId = UUID.randomUUID();
        if (paymentModel == null) {
            paymentModel = new PaymentModel();
            paymentModel.UniqueId = paymentId;
        } else
            paymentId = paymentModel.UniqueId;
        paymentModel.CustomerId = customerId;
        paymentModel.Amount = payment.Amount;
        paymentModel.Date = payment.Date;
        paymentModel.PaymentType = PaymentType.Cash;
        paymentModel.RowNo = 1;
        insertOrUpdate(paymentModel);
        new CustomerCallManager(getContext()).removePaymentCall(customerId);
        if (infoViewModels != null) {
            InvoicePaymentInfoManager manager = new InvoicePaymentInfoManager(getContext());
            manager.addOrUpdate(infoViewModels, paymentId);
        }
    }

    /**
     * Inserts or updates a settlement discount payment. We have only one discount payment per customer
     *
     * @param payment
     * @param customerId
     * @param infoViewModels
     * @throws ValidationException
     * @throws DbException
     */
    public void saveDiscountPayment(@NonNull DiscountPayment payment, @NonNull UUID customerId, @Nullable List<InvoicePaymentInfoViewModel> infoViewModels) throws ValidationException, DbException {
        Query query = new Query();
        query.from(Payment.PaymentTbl)
                .whereAnd(Criteria.equals(Payment.CustomerId, customerId.toString())
                        .and(Criteria.equals(Payment.PaymentType, PaymentType.Discount.toString())));
        PaymentModel paymentModel = getItem(query);
        UUID paymentId = UUID.randomUUID();
        if (paymentModel == null) {
            paymentModel = new PaymentModel();
            paymentModel.UniqueId = paymentId;
        } else
            paymentId = paymentModel.UniqueId;

        paymentModel.CustomerId = customerId;
        paymentModel.Amount = payment.Amount;
        paymentModel.Date = payment.Date;
        paymentModel.PaymentType = PaymentType.Discount;
        paymentModel.RowNo = 2;
        insertOrUpdate(paymentModel);
        new CustomerCallManager(getContext()).removePaymentCall(customerId);
        if (infoViewModels != null) {
            InvoicePaymentInfoManager manager = new InvoicePaymentInfoManager(getContext());
            manager.addOrUpdate(infoViewModels, paymentId);
        }
    }

    /**
     * @param payment
     * @param customerId
     * @param infoViewModels
     * @throws ValidationException
     * @throws DbException
     */
    public void saveCreditPayment(@NonNull CreditPayment payment, @NonNull UUID customerId, @Nullable List<InvoicePaymentInfoViewModel> infoViewModels) throws ValidationException, DbException {
        Query query = new Query();
        query.from(Payment.PaymentTbl)
                .whereAnd(Criteria.equals(Payment.CustomerId, customerId.toString())
                        .and(Criteria.equals(Payment.PaymentType, PaymentType.Credit.toString())));
        PaymentModel paymentModel = getItem(query);
        UUID paymentId = UUID.randomUUID();
        if (paymentModel == null) {
            paymentModel = new PaymentModel();
            paymentModel.UniqueId = paymentId;
        } else
            paymentId = paymentModel.UniqueId;
        paymentModel.CustomerId = customerId;
        paymentModel.Amount = payment.Amount;
        paymentModel.Date = payment.Date;
        paymentModel.PaymentType = PaymentType.Credit;
        paymentModel.RowNo = 3;
        insertOrUpdate(paymentModel);
        new CustomerCallManager(getContext()).removePaymentCall(customerId);
        if (infoViewModels != null) {
            InvoicePaymentInfoManager manager = new InvoicePaymentInfoManager(getContext());
            manager.addOrUpdate(infoViewModels, paymentId);
        }
    }

    public List<PaymentModel> listPayments(@NonNull UUID customerId) {
        Query query = new Query();
        query.from(Payment.PaymentTbl).whereAnd(Criteria.equals(Payment.CustomerId, customerId.toString()))
                .orderByDescending(Payment.RowNo);
        return getItems(query);
    }

    public List<PaymentModel> listCheckPayments(@NonNull UUID customerId) {
        Query query = new Query();
        query.from(Payment.PaymentTbl).whereAnd(Criteria.equals(Payment.CustomerId, customerId.toString())).whereAnd(Criteria.equals(Payment.PaymentType, PaymentType.Check.toString()));
        return getItems(query);
    }

    public List<PaymentModel> listPosPayments(@NonNull UUID customerId) {
        Query query = new Query();
        query.from(Payment.PaymentTbl).whereAnd(Criteria.equals(Payment.CustomerId, customerId.toString())).whereAnd(Criteria.equals(Payment.PaymentType, PaymentType.Card.toString()));
        return getItems(query);
    }

    @Nullable
    public PaymentModel getPaymentById(UUID paymentId, UUID paymentType) {
        Query query = new Query().from(Payment.PaymentTbl).whereAnd(Criteria.equals(Payment.UniqueId, paymentId.toString())
                .and(Criteria.equals(Payment.PaymentType, paymentType.toString())));
        return getItem(query);
    }

    @Nullable
    public PaymentModel getCashPayment(UUID customerId) {
        Query query = new Query().from(Payment.PaymentTbl).whereAnd(Criteria.equals(Payment.CustomerId, customerId.toString())
                .and(Criteria.equals(Payment.PaymentType, PaymentType.Cash.toString())));
        return getItem(query);
    }

    @Nullable
    public List<PaymentModel> getCardPayments(UUID customerId) {
        Query query = new Query().from(Payment.PaymentTbl).whereAnd(Criteria.equals(Payment.CustomerId, customerId.toString())
                .and(Criteria.equals(Payment.PaymentType, PaymentType.Card.toString())));
        return getItems(query);
    }

    @Nullable
    public PaymentModel getDiscountPayment(UUID customerId) {
        Query query = new Query().from(Payment.PaymentTbl).whereAnd(Criteria.equals(Payment.CustomerId, customerId.toString())
                .and(Criteria.equals(Payment.PaymentType, PaymentType.Discount.toString())));
        return getItem(query);
    }

    public PaymentModel getCreditPayment(UUID customerId) {
        Query query = new Query().from(Payment.PaymentTbl).whereAnd(Criteria.equals(Payment.CustomerId, customerId.toString())
                .and(Criteria.equals(Payment.PaymentType, PaymentType.Credit.toString())));
        return getItem(query);
    }

    @NonNull
    public Currency getTotalPaid(UUID customerId) {
        List<PaymentModel> paymentsList = listPayments(customerId);
        Currency totalPayment = Currency.ZERO;
        for (PaymentModel paymentModel : paymentsList) {
            if (paymentModel.Amount != null)
                totalPayment = totalPayment.add(paymentModel.Amount);
        }
        return totalPayment;
    }

    public void deleteAllPayments(final UUID customerId) throws DbException, ValidationException {
        delete(Criteria.equals(Payment.CustomerId, customerId.toString()));
        CustomerCallManager callManager = new CustomerCallManager(getContext());
        callManager.removePaymentCall(customerId);
    }

    public void deletePayment(@NonNull UUID paymentId, @NonNull UUID customerId) throws DbException, ValidationException {
        delete(Criteria.equals(Payment.UniqueId, paymentId.toString()));
        CustomerCallManager callManager = new CustomerCallManager(getContext());
        callManager.removePaymentCall(customerId);
    }

    public List<PaymentModel> getCustomerPayments(@NonNull UUID customerId) {
        Query query = new Query();
        query.from(Payment.PaymentTbl).whereAnd(Criteria.equals(Payment.CustomerId, customerId.toString()));
        return getItems(query);
    }

    public CustomerPayment calculateCustomerPayment(@NonNull UUID customerId) {
        List<OldInvoiceHeaderViewModel> selectedOldInvoices = new ArrayList<>();
        Currency oldInvoiceAmount = Currency.ZERO;
        OldInvoiceHeaderViewManager oldInvoiceHeaderViewManager = new OldInvoiceHeaderViewManager(getContext());
        List<OldInvoiceHeaderViewModel> notSettledOldInvoices = oldInvoiceHeaderViewManager.getNotSettledInvoices(customerId);
        for (final OldInvoiceHeaderViewModel oldInvoiceHeaderViewModel :
                notSettledOldInvoices) {
            if (oldInvoiceHeaderViewModel.RemAmount != null && oldInvoiceHeaderViewModel.HasPayment) {
                oldInvoiceAmount = oldInvoiceAmount.add(oldInvoiceHeaderViewModel.RemAmount);
                selectedOldInvoices.add(oldInvoiceHeaderViewModel);
            }
        }
        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
            return new CustomerPayment(null, null, oldInvoiceAmount, selectedOldInvoices);
        } else {
            CustomerCallOrderOrderViewManager customerCallOrderOrderViewManager = new CustomerCallOrderOrderViewManager(getContext());
            Currency invoiceAmount = customerCallOrderOrderViewManager.calculateTotalAmountOfAllOrders(customerId, true);
            Currency returnAmount = new CustomerCallReturnLinesViewManager(getContext()).calculateTotalAmount(customerId, null).NetAmount;
            return new CustomerPayment(invoiceAmount, returnAmount, oldInvoiceAmount, selectedOldInvoices);
        }
    }

    public String checkMinMaxOrderAndInvoiceAmount(CustomerPayment customerPayment, @NonNull ConfigMap configMap, UUID callOrderId) {
        String errorMessage = "";
        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
            try {
                CustomerCallOrderOrderViewManager customerCallOrderOrderViewManager = new CustomerCallOrderOrderViewManager(getContext());
                OrderAmount orderAmount = customerCallOrderOrderViewManager.calculateTotalAmount(callOrderId);
                int itemCount = customerCallOrderOrderViewManager.getLinesCount(callOrderId);
                if (configMap.compare(ConfigKey.MinimumOrderAmount, orderAmount.TotalAmount) > 0 && !(configMap.get(ConfigKey.MinimumOrderAmount).Value.equals(String.valueOf("0"))))
                    errorMessage = errorMessage + getString(R.string.minimum_order_amount) + " " + configMap.get(ConfigKey.MinimumOrderAmount).Value + " " + getString(R.string.your_order) + " " + orderAmount.TotalAmount + "\n";
                else if (configMap.compare(ConfigKey.MaximumOrderAmount, orderAmount.TotalAmount) < 0 && !(configMap.get(ConfigKey.MaximumOrderAmount).Value.equals(String.valueOf("0"))))
                    errorMessage = errorMessage + getString(R.string.maximum_order_amount) + " " + configMap.get(ConfigKey.MaximumOrderAmount).Value + " " + getString(R.string.your_order) + " " + orderAmount.TotalAmount + "\n";
                if (configMap.compare(ConfigKey.MinimumOrderItemCount, itemCount) > 0 && !(configMap.get(ConfigKey.MinimumOrderItemCount).Value.equals(String.valueOf("0"))))
                    errorMessage = errorMessage + getString(R.string.minimum_order_item_count) + " " + configMap.get(ConfigKey.MinimumOrderItemCount).Value + " " + getString(R.string.your_order) + " " + itemCount + "\n";
                else if (configMap.compare(ConfigKey.MaximumOrderItemCount, itemCount) < 0 && !(configMap.get(ConfigKey.MaximumOrderItemCount).Value.equals(String.valueOf("0"))))
                    errorMessage = errorMessage + getString(R.string.maximum_order_item_count) + " " + configMap.get(ConfigKey.MaximumOrderItemCount).Value + " " + getString(R.string.your_order) + " " + itemCount + "\n";
            } catch (ParseException e) {
                Timber.e(e);
                errorMessage = errorMessage + getString(R.string.invalid_min_order_amount);
            } catch (ConfigMap.NullConfigException e) {
                Timber.e(e);
                errorMessage = errorMessage + getString(R.string.invalid_max_order_amount);
            }
        } else if (VaranegarApplication.is(VaranegarApplication.AppId.HotSales)) {
            try {
                Currency totalAmount = customerPayment.getInvoiceAmount();
                if (configMap.compare(ConfigKey.MinimumFactorAmount, totalAmount) > 0 && !(configMap.get(ConfigKey.MinimumFactorAmount).Value.equals(String.valueOf("0"))))
                    errorMessage = errorMessage + getString(R.string.minimum_invoice_amount) + " " + configMap.get(ConfigKey.MinimumFactorAmount).Value + " " + getString(R.string.your_order) + " " + totalAmount + "\n";
                else if (configMap.compare(ConfigKey.MaximumFactorAmount, totalAmount) < 0 && !(configMap.get(ConfigKey.MaximumFactorAmount).Value.equals(String.valueOf("0"))))
                    errorMessage = errorMessage + getString(R.string.maximum_invoice_amount) + " " + configMap.get(ConfigKey.MaximumFactorAmount).Value + " " + getString(R.string.your_order) + " " + totalAmount + "\n";
            } catch (ParseException e) {
                Timber.e(e);
                errorMessage = errorMessage + getString(R.string.invalid_min_order_amount);
            } catch (ConfigMap.NullConfigException e) {
                Timber.e(e);
                errorMessage = errorMessage + getString(R.string.invalid_min_order_amount);
            }
        }
        return errorMessage;
    }

    public void controlPayments(@NonNull CustomerModel customerModel, @Nullable List<CustomerCallOrderModel> customerCallOrderModels, CustomerPayment customerPayment, ConfigMap sysConfigMap) throws ControlPaymentException {
        ValidPayTypeManager validPayTypeManager = new ValidPayTypeManager(getContext());
        Set<UUID> validPaymentTypes = validPayTypeManager.getValidPayTypes(customerCallOrderModels, customerPayment.getSelectedOldInvoices(), sysConfigMap.compare(ConfigKey.AllowSurplusInvoiceSettlement, true));
        boolean checkCredit = false;
        boolean checkDebit = false;
        List<PaymentTypeOrderModel> paymentTypeOrderModels = validPayTypeManager.getPaymentTypeOrders(customerCallOrderModels, customerPayment.getSelectedOldInvoices());
        for (PaymentTypeOrderModel paymentTypeOrderModel : paymentTypeOrderModels) {
            checkCredit |= paymentTypeOrderModel.CheckCredit;
            checkDebit |= paymentTypeOrderModel.CheckDebit;
        }

        if (!validPaymentTypes.contains(PaymentType.Cash) && getCashPayment(customerModel.UniqueId) != null)
            throw new CashPaymentException(getContext().getString(R.string.cash_type_is_not_valid));

        List<PaymentModel> checkPaymentList = listCheckPayments(customerModel.UniqueId);
        if (!validPaymentTypes.contains(PaymentType.Check) && checkPaymentList != null && checkPaymentList.size() > 0)
            throw new CheckPaymentException(getContext().getString(R.string.check_type_is_not_vali));

        List<PaymentModel> posPaymentList = listPosPayments(customerModel.UniqueId);
        if (!validPaymentTypes.contains(PaymentType.Card) && posPaymentList != null && posPaymentList.size() > 0)
            throw new PosPaymentException(getContext().getString(R.string.pos_type_is_not_valid));

        if ((sysConfigMap.compare(ConfigKey.AllowDiscountOnSettlement, false) || !(validPaymentTypes.contains(PaymentType.Discount))) && getDiscountPayment(customerModel.UniqueId) != null)
            throw new DiscountPaymentException(getContext().getString(R.string.discount_type_is_not_valid));

        String error = controlCreditsAndDebits(sysConfigMap, customerPayment, customerModel, checkCredit, checkDebit);
        if (error != null && !error.isEmpty())
            throw new CreditPaymentException(error);
    }

    public void thirdPartyControlPayments(@NonNull CustomerModel customerModel, @Nullable List<CustomerCallOrderModel> customerCallOrderModels, CustomerPayment customerPayment, boolean checkPayments) throws ControlPaymentException, ThirdPartyControlPaymentChangedException {
        if (checkPayments) {
            if (paymentsBecomesGreaterThanTotal(customerModel.UniqueId))
                throw new ControlPaymentException(getString(R.string.paid_amount_can_not_be_greater_than_total_amount));
        }
        if (customerCallOrderModels != null && customerCallOrderModels.size() > 0) {
            CashCheckReceiptModel payedCashCheck = getCashAndCheckPayments(customerModel.UniqueId, customerPayment);
            PaymentManager paymentManager = new PaymentManager(getContext());
            List<PaymentModel> paymentModels = paymentManager.getCustomerPayments(customerModel.UniqueId);
            List<PaymentTypeOrderModel> paymentTypeOrderModels = new ValidPayTypeManager(getContext()).getPaymentTypeOrders(customerCallOrderModels, null);
            CustomerCallOrderModel customerCall = customerCallOrderModels.get(0);
            PaymentTypeOrderModel paymentTypeOrderModel = paymentTypeOrderModels.get(0);
            if (checkPayments) {
                if (paymentModels.size() > 0 || (paymentTypeOrderModel.BackOfficeId.equalsIgnoreCase(ThirdPartyPaymentTypes.PT01.toString()))) {
                    CashCheckReceiptModel validCashCheck = calcCashAndCheckValidAmount(customerModel.UniqueId);
                    if (payedCashCheck.Cash.compareTo(validCashCheck.Cash) < 0)
                        throw new ControlPaymentException(getString(R.string.min_valid_cash) + " " + validCashCheck.Cash + "\n" +
                                getString(R.string.chash_payed) + " " + payedCashCheck.Cash + "\n" +
                                getString(R.string.remian_cash) + " " + validCashCheck.Cash.subtract(payedCashCheck.Cash));

                    Currency minCheckPay = validCashCheck.Check.subtract(payedCashCheck.Cash.subtract(validCashCheck.Cash));
                    if (payedCashCheck.Check.compareTo(minCheckPay) < 0)
                        throw new ControlPaymentException(getString(R.string.min_valid_check_and_cash) + " " + validCashCheck.Cash.add(validCashCheck.Check) + "\n" +
                                getString(R.string.cash_check_payed) + " " + payedCashCheck.Cash.add(payedCashCheck.Check) + "\n" +
                                getString(R.string.remain_cash_and_check) + " " + (validCashCheck.Cash.add(validCashCheck.Check).subtract(payedCashCheck.Cash.add(payedCashCheck.Check))));
                }
            }
            CustomerCallManager callManager = new CustomerCallManager(getContext());
            List<CustomerCallModel> calls = callManager.loadCalls(customerModel.UniqueId);
            if (!callManager.hasDeliveryCall(calls, null, null))
                return;
            if (!paymentTypeOrderModel.BackOfficeId.equalsIgnoreCase(ThirdPartyPaymentTypes.PT01.toString())) {
                double finalUsanceDay = 0;
                Currency totalPayableAmount = paymentManager.calculateCustomerPayment(customerModel.UniqueId).getTotalAmount(false);

                if (paymentModels.size() > 0 || totalPayableAmount.compareTo(Currency.ZERO) <= 0) {
                    finalUsanceDay = calculateUsancePay(customerModel.UniqueId);
                } else
                    finalUsanceDay = getUsanceRef(customerCallOrderModels) + 1;

                /**
                 * آنی بدون چک
                 * تبدیل نوع پرداخت به PT01
                 */



                paymentsList = paymentManager.listPayments(customerModel.UniqueId);
                if(finalUsanceDay==1 &&  payedCashCheck.Check.compareTo(Currency.ZERO)<=0 && paymentsList.size()>0 ) {//آنی
                    if (!paymentTypeOrderModel.BackOfficeId.equalsIgnoreCase(ThirdPartyPaymentTypes.PT01.toString())){
                        throw new ThirdPartyControlPaymentChangedException(getString(R.string.payment_type_changed), ThirdPartyPaymentTypes.PT01, null,null);
                    }
                }
                else if (finalUsanceDay <= getUsanceDay(customerCallOrderModels)) {//راس نقد
                    if (!paymentTypeOrderModel.BackOfficeId.equalsIgnoreCase(ThirdPartyPaymentTypes.PTCA.toString()))
                        throw new ThirdPartyControlPaymentChangedException(getString(R.string.payment_type_changed), ThirdPartyPaymentTypes.PTCA, null,null);
                } else if (finalUsanceDay <= getUsanceRef(customerCallOrderModels)) {//راس عرف
                    if (!paymentTypeOrderModel.BackOfficeId.equalsIgnoreCase(ThirdPartyPaymentTypes.PTCH.toString())) {
                        if (paymentTypeOrderModel.BackOfficeId.equalsIgnoreCase(ThirdPartyPaymentTypes.PTCA.toString()))
                            throw new ThirdPartyControlPaymentChangedException(getString(R.string.payment_type_changed), ThirdPartyPaymentTypes.PTCH,
                                    customerCall.PinCode2,"pin2");
                        else
                            throw new ThirdPartyControlPaymentChangedException(getString(R.string.payment_type_changed), ThirdPartyPaymentTypes.PTCH, null,null);
                    }
                } else {
                    if (!paymentTypeOrderModel.BackOfficeId.equalsIgnoreCase(ThirdPartyPaymentTypes.PT02.toString())) {
                        if (paymentModels.size() > 0)
                            throw new ThirdPartyControlPaymentChangedException(getString(R.string.payment_usance_can_not_more_than) + getUsanceRef(customerCallOrderModels)
                                    + " " + getString(R.string.be), null, null,null);
                        else
                            throw new ThirdPartyControlPaymentChangedException(getString(R.string.payment_type_changed), ThirdPartyPaymentTypes.PT02,
                                    customerCall.PinCode,"pin1");
                    }
                }
            }
        }
    }

    public void thirdPartyControlPaymentsAfterEvc(@NonNull CustomerModel customerModel, @Nullable List<CustomerCallOrderModel> customerCallOrderModels, CustomerPayment customerPayment) throws ControlPaymentException {
        if (paymentsBecomesGreaterThanTotal(customerModel.UniqueId))
            throw new ControlPaymentException(getString(R.string.paid_amount_can_not_be_greater_than_total_amount));
        if (customerCallOrderModels != null && customerCallOrderModels.size() > 0) {
            CashCheckReceiptModel payedCashCheck = getCashAndCheckPayments(customerModel.UniqueId, customerPayment);
            PaymentManager paymentManager = new PaymentManager(getContext());
            List<PaymentModel> paymentModels = paymentManager.getCustomerPayments(customerModel.UniqueId);
            List<PaymentTypeOrderModel> paymentTypeOrderModels = new ValidPayTypeManager(getContext()).getPaymentTypeOrders(customerCallOrderModels, null);
            PaymentTypeOrderModel paymentTypeOrderModel = paymentTypeOrderModels.get(0);
            if (paymentModels.size() > 0 || (paymentTypeOrderModel.BackOfficeId.equalsIgnoreCase(ThirdPartyPaymentTypes.PT01.toString()))) {
                CashCheckReceiptModel validCashCheck = calcCashAndCheckValidAmount(customerModel.UniqueId);
                if (payedCashCheck.Cash.compareTo(validCashCheck.Cash) < 0)
                    throw new ControlPaymentException(getString(R.string.min_valid_cash) + " " + validCashCheck.Cash + "\n" +
                            getString(R.string.chash_payed) + " " + payedCashCheck.Cash + "\n" +
                            getString(R.string.remian_cash) + " " + validCashCheck.Cash.subtract(payedCashCheck.Cash));
                Currency minCheckPay = validCashCheck.Check.subtract(payedCashCheck.Cash.subtract(validCashCheck.Cash));
                if (payedCashCheck.Check.compareTo(minCheckPay) < 0)
                    throw new ControlPaymentException(getString(R.string.min_valid_check_and_cash) + " " + validCashCheck.Cash.add(validCashCheck.Check) + "\n" +
                            getString(R.string.cash_check_payed) + " " + payedCashCheck.Cash.add(payedCashCheck.Check) + "\n" +
                            getString(R.string.remain_cash_and_check) + " " + (validCashCheck.Cash.add(validCashCheck.Check).subtract(payedCashCheck.Cash.add(payedCashCheck.Check))));
            }
        }
    }


    private String controlCreditsAndDebits(ConfigMap sysConfigMap, CustomerPayment customerPayment, CustomerModel customer, boolean checkCredit, boolean checkDebit) {
        CashCheckReceiptModel payedCashCheck = getCashAndCheckPayments(customer.UniqueId, customerPayment);
        CashCheckReceiptModel validCashCheck = calcCashAndCheckValidAmount(customer.UniqueId);
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());

        /**
         * Dealer Advance Credit Control
         */
        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist) && sysConfigMap.compare(ConfigKey.DealerAdvanceCreditControl, true) && !(sysConfigMap.isNullOrEmpty(ConfigKey.DealerCreditDetails)) && ((payedCashCheck.Receipt.compareTo(Currency.ZERO) == 1) || (payedCashCheck.Check.compareTo(Currency.ZERO) == 1))) {
            return (getString(R.string.just_cash_and_pos) + "\n" + sysConfigMap.getStringValue(ConfigKey.DealerCreditDetails, ""));
        }

        /**
         * Customer Advance Credit Control
         */
        SysConfigModel advancedCreditControlConfig = sysConfigManager.read(ConfigKey.AdvancedCreditControl, SysConfigManager.cloud);
        if (SysConfigManager.compare(advancedCreditControlConfig, true) && customer.ErrorMessage != null && !customer.ErrorMessage.isEmpty() && ((payedCashCheck.Receipt.compareTo(Currency.ZERO) == 1) || (payedCashCheck.Check.compareTo(Currency.ZERO) == 1))) {
            return getString(R.string.just_cash_and_pos) + "\n" + customer.ErrorMessage;
        }

        /**
         * Valid Credit and Debit Control
         */
        if (payedCashCheck.Cash.compareTo(validCashCheck.Cash) == -1) {
            return (getString(R.string.min_valid_cash) + " " + validCashCheck.Cash + "\n" +
                    getString(R.string.chash_payed) + " " + payedCashCheck.Cash + "\n" +
                    getString(R.string.remian_cash) + " " + validCashCheck.Cash.subtract(payedCashCheck.Cash));
        }

        Currency minCheckPay = validCashCheck.Check.subtract(payedCashCheck.Cash.subtract(validCashCheck.Cash));
        if (payedCashCheck.Check.compareTo(minCheckPay) < 0) {
            return (getString(R.string.min_valid_check_and_cash) + " " + validCashCheck.Cash.add(validCashCheck.Check) + "\n" +
                    getString(R.string.cash_check_payed) + " " + payedCashCheck.Cash.add(payedCashCheck.Check) + "\n" +
                    getString(R.string.remain_cash_and_check) + " " + minCheckPay);
        }

        /**
         * Customer RemainCredit and RemainDebit Control
         */
        BackOfficeType backOfficeType = null;
        try {
            backOfficeType = sysConfigManager.getBackOfficeType();
        } catch (UnknownBackOfficeException e) {
            return getString(R.string.unknow_backoffice_in_control_credits);
        }
        if (backOfficeType == BackOfficeType.Rastak) {
            CustomerCallOrderOrderViewManager customerCallOrderOrderViewManager = new CustomerCallOrderOrderViewManager(getContext());
            Currency invoiceAmount = customerCallOrderOrderViewManager.calculateTotalAmountOfAllOrders(customer.UniqueId, true);
            if (checkDebit && checkCustomerDebit(customer)) {
                Currency debit = (customer.RemainDebit.compareTo(Currency.ZERO) >= 0) ? customer.RemainDebit : Currency.ZERO;
                if ((invoiceAmount.subtract(payedCashCheck.Cash.add(payedCashCheck.Check)).compareTo(debit) > 0))
                    return (getString(R.string.credit_remain_) + " " + customer.RemainDebit + "\n" + getString(R.string.receipt_payed_for_invoices) + " " + (invoiceAmount.subtract(payedCashCheck.Cash.add(payedCashCheck.Check))));
            }
        } else {
            if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                CustomerCallManager callManager = new CustomerCallManager(getContext());
                List<CustomerCallModel> calls = callManager.loadCalls(customer.UniqueId);
                if (!callManager.hasDeliveryCall(calls, null, null))
                    return null;
            }
            if (checkCredit && checkCustomerCredit(customer) && checkDebit && checkCustomerDebit(customer)) {
                Currency creditAndDebit = ((customer.RemainCredit.add(customer.RemainDebit)).compareTo(Currency.ZERO) >= 0) ? (customer.RemainCredit.add(customer.RemainDebit)) : Currency.ZERO;
                if ((payedCashCheck.Receipt.add(payedCashCheck.Check)).compareTo(creditAndDebit) > 0)
                    return (getString(R.string.remain_creidt_and_debit) + " " + (customer.RemainCredit.add(customer.RemainDebit)) + "\n" + getString(R.string.receipt_and_check_payed) + " " + (payedCashCheck.Receipt.add(payedCashCheck.Check)));
            } else if (checkCredit && checkCustomerCredit(customer)) {
                Currency credit = (customer.RemainCredit.compareTo(Currency.ZERO) >= 0) ? customer.RemainCredit : Currency.ZERO;
                if ((payedCashCheck.Receipt.add(payedCashCheck.Check)).compareTo(credit) > 0)
                    return (getString(R.string.debit_remain_credit) + " " + customer.RemainCredit + "\n" + getString(R.string.receipt_and_check_payed) + " " + (payedCashCheck.Receipt.add(payedCashCheck.Check)));
            } else if (checkDebit && checkCustomerDebit(customer)) {
                Currency debit = (customer.RemainDebit.compareTo(Currency.ZERO) >= 0) ? customer.RemainDebit : Currency.ZERO;
                if ((payedCashCheck.Receipt.add(payedCashCheck.Check)).compareTo(debit) > 0)
                    return (getString(R.string.credit_remain_credit) + " " + customer.RemainDebit + "\n" + getString(R.string.receipt_and_check_payed) + " " + (payedCashCheck.Receipt.add(payedCashCheck.Check)));
            }
        }

        return null;
    }

    /**
     * calc check and receipt valid amount according to customers invoices
     *
     * @return
     */
    private CashCheckReceiptModel calcCashAndCheckValidAmount(UUID customerId) {
        CashCheckReceiptModel cashCheckReceiptModel = new CashCheckReceiptModel();
        CustomerCallOrderManager customerCallOrderManager = new CustomerCallOrderManager(getContext());
        // list of customer invoices
        List<CustomerCallOrderModel> customerCallOrderModels = customerCallOrderManager.getCustomerCallOrders(customerId);
        CustomerCallManager customerCallManager = new CustomerCallManager(getContext());
        List<CustomerCallModel> callModels = customerCallManager.loadCalls(customerId);
        for (final CustomerCallOrderModel customerCallOrderModel : customerCallOrderModels) {
            boolean checkOrder = true;
            if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                checkOrder = Linq.exists(callModels, new Linq.Criteria<CustomerCallModel>() {
                    @Override
                    public boolean run(CustomerCallModel item) {
                        boolean checkOrder = item.CallType == CustomerCallType.OrderDelivered || item.CallType == CustomerCallType.OrderPartiallyDelivered;
                        checkOrder = checkOrder && customerCallOrderModel.UniqueId.toString().equals(item.ExtraField1);
                        return checkOrder;
                    }
                });
            }
            if (checkOrder) {
                ValidPayTypeManager validPayTypeManager = new ValidPayTypeManager(getContext());
                // valid pay type ids for every invoice
                Set<UUID> ids = validPayTypeManager.getValidPayTypes(customerCallOrderModel);
                CustomerCallOrderOrderViewManager customerCallOrderOrderViewManager = new CustomerCallOrderOrderViewManager(getContext());
                if ((ids.contains(PaymentType.Cash) || (ids.contains(PaymentType.Card))) && !ids.contains(PaymentType.Check) && !ids.contains(PaymentType.Recipt))
                    cashCheckReceiptModel.Cash = cashCheckReceiptModel.Cash.add(customerCallOrderOrderViewManager.calculateTotalAmount(customerCallOrderModel.UniqueId).NetAmount).setScale(2,BigDecimal.ROUND_HALF_DOWN);
                if (ids.contains(PaymentType.Check) && !ids.contains(PaymentType.Recipt))
                    cashCheckReceiptModel.Check = cashCheckReceiptModel.Check.add(customerCallOrderOrderViewManager.calculateTotalAmount(customerCallOrderModel.UniqueId).NetAmount).setScale(2,BigDecimal.ROUND_HALF_DOWN);
            }
        }
        Currency returnAmount = new CustomerCallReturnLinesViewManager(getContext()).calculateTotalAmount(customerId, null).NetAmount;
        if (returnAmount.compareTo(cashCheckReceiptModel.Cash) == 1) {
            cashCheckReceiptModel.Check = cashCheckReceiptModel.Check.subtract(returnAmount.subtract(cashCheckReceiptModel.Cash)).setScale(2,BigDecimal.ROUND_HALF_DOWN);
            cashCheckReceiptModel.Cash = Currency.ZERO;

        } else {
            cashCheckReceiptModel.Cash = cashCheckReceiptModel.Cash.subtract(returnAmount).setScale(2,BigDecimal.ROUND_HALF_DOWN);
        }
        return cashCheckReceiptModel;
    }

    /**
     * calc payed check and receipt amount
     *
     * @return
     */
    private CashCheckReceiptModel getCashAndCheckPayments(UUID customerId, CustomerPayment customerPayment) {
        CashCheckReceiptModel cashCheckReceiptModel = new CashCheckReceiptModel();
        PaymentManager paymentManager = new PaymentManager(getContext());
        // calc Cash amounts
        PaymentModel cash = paymentManager.getCashPayment(customerId);
        PaymentModel discount = paymentManager.getDiscountPayment(customerId);
        PaymentModel credit = paymentManager.getCreditPayment(customerId);
        if (cash != null)
            cashCheckReceiptModel.Cash = cash.Amount;
        if (discount != null)
            cashCheckReceiptModel.Cash = cashCheckReceiptModel.Cash.add(discount.Amount);
        if (credit != null)
            cashCheckReceiptModel.Cash = cashCheckReceiptModel.Cash.add(credit.Amount);
        List<PaymentModel> posModels = paymentManager.listPosPayments(customerId);
        for (PaymentModel paymentModel : posModels)
            cashCheckReceiptModel.Cash = cashCheckReceiptModel.Cash.add(paymentModel.Amount);

        // calc check amounts
        List<PaymentModel> checkModels = paymentManager.listCheckPayments(customerId);
        for (PaymentModel paymentModel : checkModels)
            cashCheckReceiptModel.Check = cashCheckReceiptModel.Check.add(paymentModel.Amount);
        // calc receipt amount
        Currency receiptAmount = customerPayment.getTotalAmount(false).subtract(paymentManager.getTotalPaid(customerId));
        if (receiptAmount.compareTo(Currency.ZERO) == -1)
            receiptAmount = Currency.ZERO;
        cashCheckReceiptModel.Receipt = receiptAmount;
        return cashCheckReceiptModel;
    }

    private String getString(@StringRes int resId) {
        return getContext().getString(resId);
    }

    private boolean checkCustomerCredit(CustomerModel customer) {
        return (customer.checkCredit
                && (customer.InitCredit.compareTo(Currency.ZERO) != 0));
    }

    private boolean checkCustomerDebit(CustomerModel customer) {
        return (customer.checkDebit
                && (customer.InitDebit.compareTo(Currency.ZERO) != 0));
    }

    public double calculateUsancePay(UUID customerId) {
        Currency usanceTotal = Currency.ZERO;
        Currency sumPayment = Currency.ZERO;
        PaymentModel cash = getCashPayment(customerId);
        List<PaymentModel> cards = getCardPayments(customerId);
        List<PaymentModel> checks = listCheckPayments(customerId);
        if (cash != null) {
            usanceTotal = usanceTotal.add(cash.Amount);
            sumPayment = sumPayment.add(cash.Amount);
        }
        if (cards != null && !cards.isEmpty()) {
            for (PaymentModel cardModel :
                    cards) {
                usanceTotal = usanceTotal.add(cardModel.Amount);
                sumPayment = sumPayment.add(cardModel.Amount);
            }
        }
        if (checks != null && !checks.isEmpty()) {
            Calendar currentTime = Calendar.getInstance();
            currentTime.set(Calendar.HOUR_OF_DAY, 0);
            currentTime.set(Calendar.MINUTE, 0);
            currentTime.set(Calendar.SECOND, 0);
            currentTime.set(Calendar.MILLISECOND, 0);
            for (PaymentModel checkModel :
                    checks) {
                long checkDay = 0;
                if (checkModel.ChqDate.getTime() > currentTime.getTime().getTime())
                    checkDay = TimeUnit.DAYS.convert(checkModel.ChqDate.getTime() - currentTime.getTime().getTime(), TimeUnit.MILLISECONDS);
                usanceTotal = usanceTotal.add(checkModel.Amount.multiply(new Currency(checkDay)));
                sumPayment = sumPayment.add(checkModel.Amount);
            }
        }
        return sumPayment.equals(Currency.ZERO) ? 0 : (HelperMethods.currencyToDouble(usanceTotal)) / (HelperMethods.currencyToDouble(sumPayment));
    }

    public double getUsanceDay(UUID customerId) {
        CustomerCallOrderManager customerCallOrderManager = new CustomerCallOrderManager(getContext());
        List<CustomerCallOrderModel> customerCallOrderModels = customerCallOrderManager.getCustomerCallOrders(customerId);
        if (customerCallOrderModels != null && customerCallOrderModels.size() > 0) {
            CustomerCallOrderModel customerCall = customerCallOrderModels.get(0);
            return customerCall.CashDuration;
        }
        return 0;
    }

    public double getUsanceDay(@Nullable List<CustomerCallOrderModel> customerCallOrderModels) {
        if (customerCallOrderModels != null && customerCallOrderModels.size() > 0) {
            CustomerCallOrderModel customerCall = customerCallOrderModels.get(0);
            return customerCall.CashDuration;
        }
        return 0;
    }

    public double getUsanceRef(UUID customerId) {
        CustomerCallOrderManager customerCallOrderManager = new CustomerCallOrderManager(getContext());
        List<CustomerCallOrderModel> customerCallOrderModels = customerCallOrderManager.getCustomerCallOrders(customerId);
        if (customerCallOrderModels != null && customerCallOrderModels.size() > 0) {
            CustomerCallOrderModel customerCall = customerCallOrderModels.get(0);
            return customerCall.CheckDuration;
        }
        return 0;
    }

    public Currency getTotalAmountNutCash(UUID customerId) {
        CustomerCallOrderManager customerCallOrderManager = new CustomerCallOrderManager(getContext());
        List<CustomerCallOrderModel> customerCallOrderModels = customerCallOrderManager.getCustomerCallOrders(customerId);
        if (customerCallOrderModels != null && customerCallOrderModels.size() > 0) {
            CustomerCallOrderModel customerCall = customerCallOrderModels.get(0);
            return customerCall.TotalAmountNutCash;
        }
        return null;
    }

    /**
     * گرفتن دیتای مبلغ آنی
     * @param customerId
     * @return
     */
    public Currency getTotalAmountNutImmediate(UUID customerId) {
        CustomerCallOrderManager customerCallOrderManager = new CustomerCallOrderManager(getContext());
        List<CustomerCallOrderModel> customerCallOrderModels = customerCallOrderManager.getCustomerCallOrders(customerId);
        if (customerCallOrderModels != null && customerCallOrderModels.size() > 0) {
            CustomerCallOrderModel customerCall = customerCallOrderModels.get(0);
            return customerCall.TotalAmountNutImmediate;
        }
        return null;
    }

    public Currency getTotalAmountNutCheque(UUID customerId) {
        CustomerCallOrderManager customerCallOrderManager = new CustomerCallOrderManager(getContext());
        List<CustomerCallOrderModel> customerCallOrderModels = customerCallOrderManager.getCustomerCallOrders(customerId);
        if (customerCallOrderModels != null && customerCallOrderModels.size() > 0) {
            CustomerCallOrderModel customerCall = customerCallOrderModels.get(0);
            return customerCall.TotalAmountNutCheque;
        }
        return null;
    }

    public double getUsanceRef(@Nullable List<CustomerCallOrderModel> customerCallOrderModels) {
        if (customerCallOrderModels != null && customerCallOrderModels.size() > 0) {
            CustomerCallOrderModel customerCall = customerCallOrderModels.get(0);
            return customerCall.CheckDuration;
        }
        return 0;
    }

    private boolean paymentsBecomesGreaterThanTotal(UUID customerId) {
        PaymentManager paymentManager = new PaymentManager(getContext());
        Currency total = paymentManager.getTotalPaid(customerId);
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.AllowSurplusInvoiceSettlement, SysConfigManager.cloud);
        if (SysConfigManager.compare(sysConfigModel, false)) {
            return total.compareTo(paymentManager.calculateCustomerPayment(customerId).getTotalAmount(false)) > 0;
        } else return false;
    }
}
