package com.varanegar.vaslibrary.action;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.customeractiontimemanager.CustomerActionTimeManager;
import com.varanegar.vaslibrary.manager.customeractiontimemanager.CustomerActions;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallReturnManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallReturnRequestManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerPrintCountManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.paymentmanager.PaymentManager;
import com.varanegar.vaslibrary.manager.printer.CancelInvoiceManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.call.CustomerCallReturnModel;
import com.varanegar.vaslibrary.model.call.CustomerCallReturnRequestModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallType;
import com.varanegar.vaslibrary.model.payment.PaymentModel;
import com.varanegar.vaslibrary.model.returnType.ReturnType;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.ui.dialog.ReturnTypeDialog;
import com.varanegar.vaslibrary.ui.fragment.CustomerSaveReturnFragment;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 3/14/2018.
 *
 * صفحه ثبت  برگشتی
 */

public class BaseReturnAction extends CheckDistanceAction {

    private List<PaymentModel> paymentsList;
    private PaymentManager paymentManager;
    public BaseReturnAction(MainVaranegarActivity activity, ActionsAdapter adapter, UUID selectedId) {
        super(activity, adapter, selectedId);
        icon = R.drawable.ic_thumb_down_white_24dp;
    }

    @Override
    public String getName() {
        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales))
            return getActivity().getString(R.string.return_request);
        else
            return getActivity().getString(R.string.save_return);
    }

    @Nullable
    @Override
    public String isEnabled() {
        String error = super.isEnabled();
        if (error != null)
            return error;

        if (getCallManager().isDataSent(getCalls(), null))
            return getActivity().getString(R.string.customer_operation_is_sent_already);

        if (canNotEditOperationAfterPrint())
            return getActivity().getString(R.string.can_not_edit_customer_operation_after_print);

        List<UUID> enabledReturnTypes = new CustomerCallReturnManager(getActivity()).getEnabledReturnTypes(getSelectedId());

        if (enabledReturnTypes.size() == 0) {
            if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
                return getActivity().getString(R.string.return_is_disabled) + ". " + getActivity().getString(R.string.there_is_no_request);
            else
                return getActivity().getString(R.string.return_is_disabled) + " ";
        }

        return null;


    }

    @Override
    public boolean isDone() {
        CustomerCallReturnManager returnManager = new CustomerCallReturnManager(getActivity());
        List<CustomerCallReturnModel> returnModels = returnManager.getReturnCalls(getSelectedId(), null, null);
        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist) && returnModels.size() > 0)
            return true;

        List<UUID> enabledTypes = returnManager.getEnabledReturnTypes(getSelectedId());

        paymentManager = new PaymentManager(getActivity());
        paymentsList = paymentManager.listPayments(getSelectedId());
        boolean withRefFromRequest = Linq.exists(enabledTypes, new Linq.Criteria<UUID>() {
            @Override
            public boolean run(UUID item) {
                return item.equals(ReturnType.WithRefFromRequest);
            }
        });
        boolean withoutRefFromRequest = Linq.exists(enabledTypes, new Linq.Criteria<UUID>() {
            @Override
            public boolean run(UUID item) {
                return item.equals(ReturnType.WithoutRefFromRequest);
            }
        });

        if (!withoutRefFromRequest && !withRefFromRequest && returnModels.size() > 0)
            return true;

        CustomerCallReturnRequestManager requestManager = new CustomerCallReturnRequestManager(getActivity());
        List<CustomerCallReturnRequestModel> withRefRequests = requestManager.getCustomerCallReturns(getSelectedId(), true);
        List<CustomerCallReturnRequestModel> withoutRefRequests = requestManager.getCustomerCallReturns(getSelectedId(), false);
        CustomerCallManager callManager = new CustomerCallManager(getActivity());
        if (withRefRequests.size() > 0) {
            boolean hasReturn = callManager.hasReturnCall(getCalls(), true, true);
            if (!hasReturn)
                return false;
        }
        if (withoutRefRequests.size() > 0) {
            boolean hasReturn = callManager.hasReturnCall(getCalls(), false, true);
            if (!hasReturn)
                return false;
        }

        return returnModels.size() > 0;

    }

    @Override
    public void run() {
        boolean hasPayment = Linq.exists(getCalls(), new Linq.Criteria<CustomerCallModel>() {
            @Override
            public boolean run(CustomerCallModel item) {
                return item.CallType == CustomerCallType.Payment;
            }
        });

        if (hasPayment || paymentsList.size()>0) {
            CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
            dialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  //  delete();
                    try {
                        paymentManager.deleteAllPayments(getSelectedId());
                        showOptions();
                    } catch (DbException e) {
                        e.printStackTrace();
                    } catch (ValidationException e) {
                        e.printStackTrace();
                    }

                }
            });
            dialog.setNegativeButton(R.string.cancel, null);
            dialog.setTitle(R.string.warning);
            dialog.setIcon(Icon.Warning);
            dialog.setMessage(R.string.payable_amount_will_change_check_payments);
            dialog.show();
        } else
            showOptions();
    }

    private void showOptions() {
        SysConfigManager sysConfigManager = new SysConfigManager(getActivity());
        SysConfigModel isRef = sysConfigManager.read(ConfigKey.AllowReturnWithRef, SysConfigManager.cloud);
        SysConfigModel noRef = sysConfigManager.read(ConfigKey.AllowReturnWithoutRef, SysConfigManager.cloud);
        SysConfigModel ReplacementRegistration = sysConfigManager.read(ConfigKey.ReplacementRegistration, SysConfigManager.cloud);
        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            if (SysConfigManager.compare(isRef, true) && SysConfigManager.compare(noRef, true) || SysConfigManager.compare(ReplacementRegistration, true)) {
                ReturnTypeDialog dialog = new ReturnTypeDialog();
                dialog.onTypeSelected = new ReturnTypeDialog.OnTypeSelected() {
                    @Override
                    public void run(UUID returnType, boolean isFromRequest) {
                        letsGo(getSelectedId(), returnType.equals(ReturnType.WithRef), false, returnType.equals(ReturnType.WithRefReplacementRegistration), returnType.equals(ReturnType.WithoutRefReplacementRegistration));
                    }
                };
                dialog.show(getActivity().getSupportFragmentManager(), "ReturnTypeDialog");
            } else if (SysConfigManager.compare(isRef, true) && SysConfigManager.compare(noRef, false))
                letsGo(getSelectedId(), true, false, false, false);
            else if (SysConfigManager.compare(isRef, false) && SysConfigManager.compare(noRef, true))
                letsGo(getSelectedId(), false, false, false, false);
        } else {
            CustomerCallReturnManager callReturnManager = new CustomerCallReturnManager(getActivity());
            List<UUID> enabledTypes = callReturnManager.getEnabledReturnTypes(getSelectedId());
            boolean withRef;
            boolean withoutRef;
            boolean withRefFromRequest;
            boolean withoutRefFromRequest;

            withRef = Linq.exists(enabledTypes, new Linq.Criteria<UUID>() {
                @Override
                public boolean run(UUID item) {
                    return item.equals(ReturnType.WithRef);
                }
            });
            withoutRef = Linq.exists(enabledTypes, new Linq.Criteria<UUID>() {
                @Override
                public boolean run(UUID item) {
                    return item.equals(ReturnType.WithoutRef);
                }
            });
            withRefFromRequest = Linq.exists(enabledTypes, new Linq.Criteria<UUID>() {
                @Override
                public boolean run(UUID item) {
                    return item.equals(ReturnType.WithRefFromRequest);
                }
            });
            withoutRefFromRequest = Linq.exists(enabledTypes, new Linq.Criteria<UUID>() {
                @Override
                public boolean run(UUID item) {
                    return item.equals(ReturnType.WithoutRefFromRequest);
                }
            });

            if (!withRef && !withoutRef && !withRefFromRequest && withoutRefFromRequest) {
                letsGo(getSelectedId(), false, true, false, false);
            } else if (!withRef && !withoutRef && withRefFromRequest && !withoutRefFromRequest) {
                letsGo(getSelectedId(), true, true, false, false);
            } else if (!withRef && withoutRef && !withRefFromRequest && !withoutRefFromRequest) {
                letsGo(getSelectedId(), false, false, false, false);
            } else if (withRef && !withoutRef && !withRefFromRequest && !withoutRefFromRequest) {
                letsGo(getSelectedId(), true, false, false, false);
            } else {
                ReturnTypeDialog dialog = new ReturnTypeDialog();
                Bundle bundle = new Bundle();
                bundle.putString("f4c5f58c-c422-4186-b5d0-5693de811403", getSelectedId().toString());
                dialog.setArguments(bundle);
                dialog.onTypeSelected = new ReturnTypeDialog.OnTypeSelected() {
                    @Override
                    public void run(UUID returnType, boolean isFromRequest) {
                        letsGo(getSelectedId(), returnType.equals(ReturnType.WithRef), isFromRequest, returnType.equals(ReturnType.WithRefReplacementRegistration), returnType.equals(ReturnType.WithoutRefReplacementRegistration));
                    }
                };
                dialog.show(getActivity().getSupportFragmentManager(), "ReturnTypeDialog");
            }
        }
    }


    @Nullable
    @Override
    public UUID getTaskUniqueId() {
        return UUID.fromString("E74D0C38-1DD5-4326-A887-826B35AF1293");
    }

    @Nullable
    @Override
    protected String isMandatory() {
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            boolean lackOfVisit = Linq.exists(getCalls(), new Linq.Criteria<CustomerCallModel>() {
                @Override
                public boolean run(CustomerCallModel item) {
                    return item.CallType == CustomerCallType.LackOfVisit;
                }
            });
            if (lackOfVisit)
                return null;

            CustomerCallReturnManager returnManager = new CustomerCallReturnManager(getActivity());
            List<UUID> returnTypes = returnManager.getEnabledReturnTypes(getSelectedId());
            if (returnTypes.size() == 0)
                return checkCustomerStatus();

            boolean withRefFromRequest = Linq.exists(returnTypes, new Linq.Criteria<UUID>() {
                @Override
                public boolean run(UUID item) {
                    return item.equals(ReturnType.WithRefFromRequest);
                }
            });
            boolean withoutRefFromRequest = Linq.exists(returnTypes, new Linq.Criteria<UUID>() {
                @Override
                public boolean run(UUID item) {
                    return item.equals(ReturnType.WithoutRefFromRequest);
                }
            });

            if (withoutRefFromRequest || withRefFromRequest) {
                CustomerCallReturnRequestManager manager = new CustomerCallReturnRequestManager(getActivity());
                List<CustomerCallReturnRequestModel> requestModels = manager.getCustomerCallReturns(getSelectedId(), null);
                if (requestModels.size() == 0)
                    return checkCustomerStatus();

                for (final CustomerCallReturnRequestModel request :
                        requestModels) {
                    boolean exists = Linq.exists(getCalls(), new Linq.Criteria<CustomerCallModel>() {
                        @Override
                        public boolean run(CustomerCallModel call) {
                            if (call.CallType == CustomerCallType.SaveReturnRequestWithoutRef
                                    || call.CallType == CustomerCallType.SaveReturnRequestWithRef) {
                                if (request.UniqueId == null)
                                    return false;
                                return request.UniqueId.toString().equals(call.ExtraField1);
                            }
                            return false;
                        }
                    });
                    if (!exists) {
                        return getActivity().getString(R.string.there_is_an_undecided_return_request);
                    }
                }
                if (requestModels.size() == 0)
                    return checkCustomerStatus();
            }
            return checkCustomerStatus();

        } else {
            return checkCustomerStatus();
        }
    }

    private String checkCustomerStatus() {
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            boolean hasDistCall = getCallManager().hasDistCall(getCalls());
            boolean lackOfDelivery = getCallManager().isCompleteLackOfDelivery(getCalls());
            boolean returnDelivery = getCallManager().isCompleteReturnDelivery(getCalls());
            boolean isLackOfVisit = getCallManager().isLackOfVisit(getCalls());
            if (isLackOfVisit || hasDistCall || lackOfDelivery || returnDelivery)
                return null;
            else
                return getActivity().getString(R.string.customer_status_is_uknown);
        } else {
            boolean hasOrder = getCallManager().hasOrderOrReturnCall(getCalls());
            boolean isLackOfVisit = getCallManager().isLackOfVisit(getCalls());
            boolean isLackOfOrder = getCallManager().isLackOfOrder(getCalls());
            if (isLackOfOrder || isLackOfVisit || hasOrder)
                return null;
            else
                return getActivity().getString(R.string.customer_status_is_uknown);
        }
    }

    private void letsGo(@NonNull UUID customerId, boolean isRef, boolean isFromRequest, boolean isRefReplacementRegistration, boolean isWithoutRefReplacementRegistration) {
        CustomerSaveReturnFragment saveReturnFragment = new CustomerSaveReturnFragment();
        saveReturnFragment.setArguments(customerId, isRef, isFromRequest, isRefReplacementRegistration, isWithoutRefReplacementRegistration);
        getActivity().pushFragment(saveReturnFragment);
    }


    private void delete() {
        final CustomerPrintCountManager customerPrintCountManager = new CustomerPrintCountManager(getActivity());
        final CustomerCallManager callManager = new CustomerCallManager(getActivity());
        try {
            if (getPrintCounts() > 0) {
                CancelInvoiceManager cancelInvoiceManager = new CancelInvoiceManager(getActivity());
                cancelInvoiceManager.addCancelInvoice(getSelectedId());
                customerPrintCountManager.resetCount(getSelectedId());
            }
            callManager.removeAllCalls(getSelectedId());
            callManager.removeCalls(getSelectedId());
            new CustomerActionTimeManager(getActivity()).delete(getSelectedId(), CustomerActions.CustomerCallEnd);
        } catch (DbException e) {
            e.printStackTrace();
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }
}
