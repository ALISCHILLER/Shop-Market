package com.varanegar.vaslibrary.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.CuteAlertDialog;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.datetime.JalaliCalendar;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.selectionlistadapter.SelectionRecyclerAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.CustomerPathViewManager;
import com.varanegar.vaslibrary.manager.NoSaleReasonManager;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallInvoiceManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.locationmanager.LocationManager;
import com.varanegar.vaslibrary.manager.locationmanager.LogLevel;
import com.varanegar.vaslibrary.manager.locationmanager.LogType;
import com.varanegar.vaslibrary.manager.locationmanager.OnSaveLocation;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLogManager;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.LackOfVisitLocationViewModel;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.call.CustomerCallInvoiceModel;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallType;
import com.varanegar.vaslibrary.model.customerpathview.CustomerPathViewModel;
import com.varanegar.vaslibrary.model.location.LocationModel;
import com.varanegar.vaslibrary.model.noSaleReason.NoSaleReasonModel;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.model.user.UserModel;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

public class NonDeliverAllUnknownCustomersDialog extends CuteAlertDialog {
    SelectionRecyclerAdapter<NoSaleReasonModel> selectionAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onCreateContentView(LayoutInflater inflater,
                                       ViewGroup viewGroup,
                                       @Nullable Bundle savedInstanceState) {
        setTitle(R.string.please_select_a_reason_of_no_delivery);
        View view = inflater.inflate(R.layout.fragment_non_order, viewGroup, true);
        BaseRecyclerView nonOrderList = view.findViewById(R.id.non_order_list_view);
        NoSaleReasonManager manager = new NoSaleReasonManager(getContext());
        final List<NoSaleReasonModel> noSalesReasons = manager.getDistReturnReason();
        selectionAdapter = new SelectionRecyclerAdapter<>(
                getVaranegarActvity(),
                noSalesReasons,
                false);
        nonOrderList.setAdapter(selectionAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        dismissAllowingStateLoss();
    }

    @Override
    public void ok() {
        final NoSaleReasonModel noSaleReasonModel = selectionAdapter.getSelectedItem();
        if (noSaleReasonModel == null || noSaleReasonModel.UniqueId == null) {
            Toast.makeText(getContext(), R.string.please_select_a_reason_of_no_visit,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (getContext() == null || getActivity()==null) return;

        final CustomerCallManager callManager =
                new CustomerCallManager(getContext());
        CustomerPathViewManager customerPathViewManager =
                new CustomerPathViewManager(getContext());
        TourManager tourManager = new TourManager(getContext());
        TourModel tourModel = tourManager.loadTour();

        try {
            List<CustomerModel> customerModels =
                    new CustomerManager(getContext()).getAll();
            HashMap<UUID, CustomerModel> customerModelHashMap = new HashMap<>();
            for (CustomerModel customerModel : customerModels) {
                customerModelHashMap.put(customerModel.UniqueId, customerModel);
            }

            if (tourModel == null) {
                Toast.makeText(getContext(), R.string.tour_data_not_available,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            VaranegarApplication.getInstance().getDbHandler().beginTransaction();
            final List<CustomerPathViewModel> customerPathViewModels =
                    customerPathViewManager.getNotVisitedPathCustomers(tourModel.DayVisitPathId);
            for (CustomerPathViewModel customerPath : customerPathViewModels) {
                if (customerPath.UniqueId == null) continue;

                CustomerCallInvoiceManager callInvoiceManager =
                        new CustomerCallInvoiceManager(getContext());
                List<CustomerCallInvoiceModel> invoiceModels =
                        callInvoiceManager.getCustomerCallInvoices(customerPath.UniqueId);
                List<UUID> invoiceIds = Linq.map(invoiceModels, item -> item.UniqueId);

                //تغییر وضعیت
                callManager.saveCompleteLackOfDelivery(
                        customerPath.UniqueId,
                        invoiceIds,
                        noSaleReasonModel.UniqueId);

                //تایید عملیات
                callManager.addOrConfirmCall(CustomerCallType.OrderLackOfDelivery,
                        customerPath.UniqueId,
                        noSaleReasonModel.UniqueId.toString());

                //ذخیره لوکیشن
                final LocationManager locationManager = new LocationManager(getContext());
                final List<LocationModel> locationModels = new ArrayList<>();
                CustomerModel customerModel =
                        customerModelHashMap.get(customerPath.UniqueId);
                if (customerModel == null) continue;
                LackOfVisitLocationViewModel lackOfVisitLocationViewModel =
                        new LackOfVisitLocationViewModel();
                lackOfVisitLocationViewModel.CustomerId = customerModel.UniqueId;
                lackOfVisitLocationViewModel.eventData.Address = customerModel.Address;
                lackOfVisitLocationViewModel.eventData.CustomerName = customerModel.CustomerName;
                lackOfVisitLocationViewModel.eventData.CustomerCode = customerModel.CustomerCode;
                lackOfVisitLocationViewModel.eventData.Phone = customerModel.Phone;
                lackOfVisitLocationViewModel.eventData.PTime =
                        DateHelper.toString(new JalaliCalendar(),
                                DateFormat.MicrosoftDateTime);
                lackOfVisitLocationViewModel.eventData.Time =
                        DateHelper.toString(new GregorianCalendar(),
                                DateFormat.MicrosoftDateTime);
                lackOfVisitLocationViewModel.eventData.StoreName = customerModel.StoreName;
                lackOfVisitLocationViewModel.eventData.CustomerId = customerModel.UniqueId;
                lackOfVisitLocationViewModel.eventData.IsInVisitDayPath = true;
                TrackingLogManager.addLog(getActivity(),
                        LogType.ORDER_EVENT,
                        LogLevel.Info, " عدم تحویل برای مشتری " +
                                customerModel.CustomerCode +
                                " (" + customerModel.CustomerName + ")");
                locationManager.addTrackingPoint(
                        lackOfVisitLocationViewModel, new OnSaveLocation() {
                    @Override
                    public void onSaved(LocationModel location) {
                        locationModels.add(location);
                        if (locationModels.size() == customerPathViewModels.size()) {
                            final UserModel userModel =
                                    UserManager.readFromFile(getContext());
                            if (userModel != null && userModel.UniqueId != null)
                                locationManager.tryToSendItems(locationModels);
                            else
                                Timber.e("User is not logged in");
                        }
                    }

                    @Override
                    public void onFailed(String error) {
                        Timber.e(error);
                    }
                });
            }

            VaranegarApplication.getInstance().getDbHandler().setTransactionSuccessful();
            VaranegarApplication.getInstance().getDbHandler().endTransaction();
            dismiss();
            if (onDeliverStatusChanged != null)
                onDeliverStatusChanged.onChanged();
        } catch (Exception e) {
            Timber.e(e);
            VaranegarApplication.getInstance().getDbHandler().endTransaction();
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setTitle(R.string.error);
            dialog.setMessage(R.string.error_saving_request);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }

    @Override
    public void cancel() {

    }

    public NonDeliverAllUnknownCustomersDialog.OnDeliverStatusChanged onDeliverStatusChanged;

    public interface OnDeliverStatusChanged {
        void onChanged();
    }
}
