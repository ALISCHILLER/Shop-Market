package com.varanegar.vaslibrary.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.PopupFragment;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.BackupManager;
import com.varanegar.vaslibrary.manager.CustomerPathViewManager;
import com.varanegar.vaslibrary.manager.OnHandQtyManager;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.productorderviewmanager.ProductOrderViewManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallType;
import com.varanegar.vaslibrary.model.onhandqty.OnHandQty;
import com.varanegar.vaslibrary.model.onhandqty.OnHandQtyModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.print.SentTourInfoPrint.TourInfo;
import com.varanegar.vaslibrary.ui.dialog.ConnectionSettingDialog;
import com.varanegar.vaslibrary.ui.dialog.NonVisitAllUnknownCustomersDialog;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.onhandqty.HotSalesOnHandQtyApi;
import com.varanegar.vaslibrary.webapi.tour.TourStatus;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by A.Torabi on 11/14/2018.
 */

public abstract class SendTourFragment extends PopupFragment {

    private TextView statusTextView;
    List<String> statusList = new ArrayList<>();
    private boolean isSending;
    private TourManager tourManager;
    private boolean stopped;
    private CuteMessageDialog confirmDialog;
    private long lastTry;

    @Override
    public void onPause() {
        super.onPause();
        if (confirmDialog != null && confirmDialog.isShowing()) {
            MainVaranegarActivity activity = getVaranegarActvity();
            if (activity != null && !activity.isFinishing())
                confirmDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        MainVaranegarActivity activity = getVaranegarActvity();
        if (activity != null && !activity.isFinishing()) {
            if (tourManager.loadTour() == null)
                activity.putFragment(getTourReportFragment());
            else if (!tourManager.isTourSending())
                activity.putFragment(getCustomersFragment());
        }
    }

    protected abstract VaranegarFragment getCustomersFragment();

    protected abstract VaranegarFragment getTourReportFragment();

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tourManager = new TourManager(getContext());
        isSending = tourManager.isTourSending();

        if (!isSending && tourManager.isTourAvailable())
            tryToSendTour();

        SimpleToolbar simpleToolbar = view.findViewById(R.id.simple_toolbar);
        simpleToolbar.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        statusTextView = view.findViewById(R.id.status_text_view);
        statusTextView.setMovementMethod(new ScrollingMovementMethod());
        view.findViewById(R.id.send_tour).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timber.d("Send tour clicked");
                tryToSendTour();
            }
        });

        view.findViewById(R.id.cancel_tour).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timber.d("cancel sending tour clicked");
                if (isSending) {
                    Timber.d("Tour is sending");
                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                    dialog.setIcon(Icon.Warning);
                    dialog.setMessage(R.string.are_you_sure);
                    dialog.setNegativeButton(R.string.no, null);
                    dialog.setPositiveButton(R.string.yes, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Timber.d("Cancel tour send confirmed");
                            cancel();
                        }
                    });
                    dialog.show();
                }
            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                if (tourManager.isTourAvailable()) {
                    final TourInfo tourInfo = tourManager.createTourInfo();
                    if (tourInfo != null) {
                        MainVaranegarActivity activity = getVaranegarActvity();
                        if (activity != null && !activity.isFinishing() && isResumed()) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                                        ((PairedItems) view.findViewById(R.id.tour_no_paired_items)).setValue(String.valueOf(tourInfo.TourNo));
                                        ((PairedItems) view.findViewById(R.id.today_customers_count_paired_items)).setValue(String.valueOf(tourInfo.DayCustomersCount));
                                        ((PairedItems) view.findViewById(R.id.today_visited_customer_paired_items)).setValue(String.valueOf(tourInfo.DayVisitedCount));
                                        ((PairedItems) view.findViewById(R.id.today_ordered_customer_paired_items)).setValue(String.valueOf(tourInfo.DayOrderedCount));
                                        ((PairedItems) view.findViewById(R.id.today_lack_of_order_customer_paired_items)).setValue(String.valueOf(tourInfo.DayLackOfOrderCount));
                                        ((PairedItems) view.findViewById(R.id.lack_of_visit_customer_paired_items)).setValue(String.valueOf(tourInfo.DayLackOfVisitCount));
                                        ((PairedItems) view.findViewById(R.id.today_sum_of_ordered_paired_items)).setValue(HelperMethods.currencyToString(tourInfo.DayOrderSum));

                                        ((PairedItems) view.findViewById(R.id.total_customers_count_paired_items)).setValue(String.valueOf(tourInfo.TotalCustomersCount));
                                        ((PairedItems) view.findViewById(R.id.total_visited_customer_paired_items)).setValue(String.valueOf(tourInfo.TotalVisitedCount));
                                        ((PairedItems) view.findViewById(R.id.total_ordered_customer_paired_items)).setValue(String.valueOf(tourInfo.TotalOrderedCount));
                                        ((PairedItems) view.findViewById(R.id.total_lack_of_order_customer_paired_items)).setValue(String.valueOf(tourInfo.TotalLackOfOrderCount));
                                        ((PairedItems) view.findViewById(R.id.total_sum_of_ordered_paired_items)).setValue(HelperMethods.currencyToString(tourInfo.TotalOrderSum));

                                        view.findViewById(R.id.total_deliveries_customer_paired_items).setVisibility(View.GONE);
                                        view.findViewById(R.id.total_partial_deliveries_customer_paired_items).setVisibility(View.GONE);
                                        view.findViewById(R.id.total_lack_of_deliveries_customer_paired_items).setVisibility(View.GONE);
                                        view.findViewById(R.id.total_lack_of_visits_customer_paired_items).setVisibility(View.GONE);
                                        view.findViewById(R.id.total_return_customer_paired_items).setVisibility(View.GONE);

                                        ((PairedItems) view.findViewById(R.id.total_visit_time_paired_items)).setValue(tourInfo.VisitTime);
                                        ((PairedItems) view.findViewById(R.id.tour_time_paired_items)).setValue(tourInfo.TourTime);

                                        ProductOrderViewManager productOrderViewManager = new ProductOrderViewManager(getContext());
                                        boolean spd = productOrderViewManager.getSPD();
                                        if (spd)
                                            ((PairedItems) view.findViewById(R.id.spd_paired_items)).setValue(getString(R.string.check_sign));
                                        else
                                            ((PairedItems) view.findViewById(R.id.spd_paired_items)).setValue(getString(R.string.multiplication_sign));

                                        DecimalFormat df = new DecimalFormat("#.00");
                                        ((PairedItems) view.findViewById(R.id.visit_to_customer_paired_items)).setValue(df.format(tourInfo.DayVisitRatio) + " %");
                                    } else {
                                        ((PairedItems) view.findViewById(R.id.tour_no_paired_items)).setValue(String.valueOf(tourInfo.TourNo));
                                        view.findViewById(R.id.today_customers_count_paired_items).setVisibility(View.GONE);
                                        view.findViewById(R.id.today_visited_customer_paired_items).setVisibility(View.GONE);
                                        view.findViewById(R.id.today_ordered_customer_paired_items).setVisibility(View.GONE);
                                        view.findViewById(R.id.today_lack_of_order_customer_paired_items).setVisibility(View.GONE);
                                        view.findViewById(R.id.lack_of_visit_customer_paired_items).setVisibility(View.GONE);
                                        view.findViewById(R.id.today_sum_of_ordered_paired_items).setVisibility(View.GONE);
                                        view.findViewById(R.id.total_ordered_customer_paired_items).setVisibility(View.GONE);
                                        view.findViewById(R.id.total_lack_of_order_customer_paired_items).setVisibility(View.GONE);

                                        ((PairedItems) view.findViewById(R.id.total_customers_count_paired_items)).setValue(String.valueOf(tourInfo.TotalCustomersCount));
                                        ((PairedItems) view.findViewById(R.id.total_visited_customer_paired_items)).setValue(String.valueOf(tourInfo.TotalVisitedCount));

                                        ((PairedItems) view.findViewById(R.id.total_deliveries_customer_paired_items)).setValue(String.valueOf(tourInfo.DeliveriesCount));
                                        ((PairedItems) view.findViewById(R.id.total_partial_deliveries_customer_paired_items)).setValue(String.valueOf(tourInfo.PartialDeliveriesCount));
                                        ((PairedItems) view.findViewById(R.id.total_lack_of_deliveries_customer_paired_items)).setValue(String.valueOf(tourInfo.LackOfDeliveriesCount));
                                        ((PairedItems) view.findViewById(R.id.total_lack_of_visits_customer_paired_items)).setValue(String.valueOf(tourInfo.TotalLackOfVisitCount));
                                        ((PairedItems) view.findViewById(R.id.total_return_customer_paired_items)).setValue(String.valueOf(tourInfo.ReturnsCount));

                                        ((PairedItems) view.findViewById(R.id.total_sum_of_ordered_paired_items)).setValue(HelperMethods.currencyToString(tourInfo.TotalOrderSum));

                                        ((PairedItems) view.findViewById(R.id.total_visit_time_paired_items)).setValue(tourInfo.VisitTime);
                                        ((PairedItems) view.findViewById(R.id.tour_time_paired_items)).setValue(tourInfo.TourTime);


                                        view.findViewById(R.id.spd_paired_items).setVisibility(View.GONE);
                                        view.findViewById(R.id.spd_paired_items).setVisibility(View.GONE);
                                        view.findViewById(R.id.visit_to_customer_paired_items).setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }).start();

        View userNameTextView = view.findViewById(R.id.user_name_text_view);
        UserModel userModel = UserManager.readFromFile(getContext());
        if (userModel != null)
            ((TextView) userNameTextView).setText(userModel.UserName);

        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel serverAddressConfig = sysConfigManager.read(ConfigKey.ValidServerAddress, SysConfigManager.local);
        if (serverAddressConfig != null)
            ((TextView) view.findViewById(R.id.ip_text_view)).setText(serverAddressConfig.Value);
        SysConfigModel localServerAddressConfig = sysConfigManager.read(ConfigKey.LocalServerAddress, SysConfigManager.local);
        if (localServerAddressConfig != null)
            ((TextView) view.findViewById(R.id.local_ip_text_view)).setText(localServerAddressConfig.Value);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.send_tour_layout_fragment, container, false);
    }

    private void tryToSendTour() {
        if ((new Date().getTime() - lastTry) < 2000)
            return;
        addStatus(R.string.try_to_send_tour);
        Timber.d("Try to send tour");
        lastTry = new Date().getTime();
        if (isSending) {
            Timber.d("Tour is sending already");
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setIcon(Icon.Alert);
            dialog.setMessage(R.string.tour_is_sending);
            dialog.setTitle(R.string.alert);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        } else {
            if (!tourManager.isTourAvailable()) {
                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                dialog.setIcon(Icon.Alert);
                dialog.setMessage(R.string.tour_is_not_available);
                dialog.setTitle(R.string.alert);
                dialog.setPositiveButton(R.string.ok, null);
                dialog.show();
                return;
            }
            initSendTour();
        }
    }

    private void cancel() {
        if (tourManager.isTourSending()) {
            if (tourManager.isSendServiceConnected())
                tourManager.cancelSendingTour();
            else {
                tourManager.getTourStatus(new TourManager.ITourStatusCallback() {
                    @Override
                    public void onStatus(UUID status) {
                        if (status.equals(TourStatus.Sent) || status.equals(TourStatus.InProgress)) {
                            try {
                                tourManager.saveConfirm();
                                isSending = false;
                                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                                dialog.setIcon(Icon.Alert);
                                dialog.setMessage(R.string.sending_tour_canceled);
                                dialog.setPositiveButton(R.string.ok, null);
                                dialog.show();
                            } catch (IOException e) {
                                Timber.e(e);
                            }
                        } else {
                            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                            dialog.setTitle(R.string.error);
                            dialog.setIcon(Icon.Error);
                            dialog.setMessage(R.string.tour_is_sent);
                            dialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        BackupManager.exportData(getContext(), true);
                                        tourManager.deleteTourData();
                                        isSending = false;
                                    } catch (Exception e) {
                                        Timber.e(e);
                                    }
                                }
                            });
                            dialog.show();
                        }
                    }

                    @Override
                    public void onFailure() {
                        CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                        dialog.setTitle(R.string.tour_status_is_unknown);
                        dialog.setIcon(Icon.Error);
                        dialog.setMessage(R.string.sending_tour_is_interrupted_please_contact_administrator);
                        dialog.setPositiveButton(R.string.ok, null);
                        dialog.show();
                    }
                });
            }
        } else
            stopped = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        isSending = tourManager.isTourSending();
        readStatus();
    }

    private void clearStatus() {
        MainVaranegarActivity activity = getVaranegarActvity();
        if (activity != null)
            activity.getSharedPreferences("TOUR_STATUS_LIST", Context.MODE_PRIVATE).edit().clear().apply();

    }

    private void readStatus() {
        MainVaranegarActivity activity = getVaranegarActvity();
        if (activity != null) {
            SharedPreferences sp = activity.getSharedPreferences("TOUR_STATUS_LIST", Context.MODE_PRIVATE);
            Map<String, ?> allStatus = sp.getAll();
            int last = 0;
            for (String key :
                    allStatus.keySet()) {
                int idx = Integer.valueOf(key);
                if (idx > last)
                    last = idx;
            }
            statusList = new ArrayList<>();
            for (int i = 0; i <= last; i++) {
                statusList.add(sp.getString(String.valueOf(i), ""));
            }
            if (isResumed()) {
                statusTextView.setText("");
                for (String str :
                        statusList) {
                    statusTextView.append(str + "\n");
                }
            }
        }

    }

    void addStatus(@StringRes int status) {
        MainVaranegarActivity activity = getVaranegarActvity();
        if (activity != null && !activity.isFinishing())
            addStatus(activity.getString(status));
    }

    void addStatus(String status) {
        MainVaranegarActivity activity = getVaranegarActvity();
        if (activity != null && !activity.isFinishing()) {
            SharedPreferences.Editor editor = activity.getSharedPreferences("TOUR_STATUS_LIST", Context.MODE_PRIVATE).edit();
            editor.putString(String.valueOf(statusList.size()), status).apply();
            statusList.add(status);
            if (!activity.isFinishing() && isResumed()) {
                statusTextView.setText("");
                for (String str :
                        statusList) {
                    statusTextView.append(str + "\n");
                }
            }
        }

    }

    private void initSendTour() {
        CustomerPathViewManager customerPathViewManager = new CustomerPathViewManager(getContext());
        if (customerPathViewManager.hasIncompleteOperation() && !VaranegarApplication.is(VaranegarApplication.AppId.Contractor)) {
            final CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setTitle(R.string.alert);
            dialog.setIcon(Icon.Warning);
            dialog.setMessage(R.string.visit_incomplete_operation_customers);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        } else {
            isSending = true;
            final MainVaranegarActivity activity = getVaranegarActvity();
            if (activity == null || activity.isFinishing()) {
                isSending = false;
                return;
            }
            if (!Connectivity.isConnected(activity)) {
                ConnectionSettingDialog connectionSettingDialog = new ConnectionSettingDialog();
                connectionSettingDialog.show(activity.getSupportFragmentManager(), "ConnectionSettingDialog");
                isSending = false;
                return;
            }

            if (!tourManager.isTourAvailable()) {
                addStatus(R.string.tour_is_not_available);
                return;
            }
            addStatus(R.string.tour_file_loaded);
            if (tourManager.isTourSending()) {
                addStatus(R.string.tour_is_sending);
                return;
            }
            if (stopped) {
                stopped = false;
                isSending = false;
                addStatus(R.string.sending_tour_canceled);
                return;
            }
            isSending = true;
            addStatus(R.string.detecting_tour_status_in_server);
            tourManager.getTourStatus(new TourManager.ITourStatusCallback() {
                @Override
                public void onStatus(UUID status) {
                    if (isResumed()) {
                        if (status.equals(TourStatus.Sent) || status.equals(TourStatus.InProgress)) {
                            isSending = false;
                            addStatus(R.string.tour_is_ready_to_send);
                            showSendTourConfirm();
                        } else {
                            isSending = false;
                            addStatus(R.string.tour_is_not_in_sent_status_on_server);
                            showDeleteTourConfirm(TourStatus.getStatusName(getContext(), status));
                        }
                    }
                }

                @Override
                public void onFailure() {
                    isSending = false;
                    addStatus(R.string.server_did_not_respond);
                    if (isResumed())
                        showSendTourConfirm();
                }
            });
        }
    }

    private void showDeleteTourConfirm(String tourStatus) {
        CuteMessageDialog confirmDialog = new CuteMessageDialog(getContext());
        confirmDialog.setTitle(R.string.tour_is_not_in_sent_status_on_server);
        confirmDialog.setMessage(getContext().getString(R.string.tour_status_in_console_label) + " " + tourStatus + "\n" +
                getString(R.string.you_cant_send_tour_anymore) + "\n" +
                getString(R.string.do_you_want_to_delete_tour_data_this_is_not_reversable));
        confirmDialog.setIcon(Icon.Error);
        confirmDialog.setNeutralButton(R.string.cancel, null);
        confirmDialog.setPositiveButton(R.string.yes_i_take_responsibility, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.d("User accepted to delete data 1");
                CuteMessageDialog confirmDialog = new CuteMessageDialog(getContext());
                confirmDialog.setTitle(R.string.warning);
                confirmDialog.setMessage(getContext().getString(R.string.are_you_sure) + "\n" +
                        getString(R.string.all_orders_and_data_will_be_deleted));
                confirmDialog.setIcon(Icon.Error);
                confirmDialog.setNeutralButton(R.string.cancel, null);
                confirmDialog.setPositiveButton(R.string.yes_i_take_responsibility, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Timber.d("User accepted to delete data 2");
                        MainVaranegarActivity activity = getVaranegarActvity();
                        if (activity != null && !activity.isFinishing() && isResumed()) {
                            tourManager.deleteTourData();
                            activity.popFragment();
                        }
                    }
                });
                confirmDialog.show();
            }
        });
        confirmDialog.show();
    }

    private void showSendTourConfirm() {
        confirmDialog = new CuteMessageDialog(getContext());
        confirmDialog.setTitle(R.string.warning);
        confirmDialog.setMessage(R.string.after_sending_tour_you_do_can_not_change_anything);
        confirmDialog.setIcon(Icon.Alert);
        confirmDialog.setNeutralButton(R.string.cancel, null);
        confirmDialog.setPositiveButton(R.string.yes, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tourManager.isTourAvailable())
                    return;
                addStatus(R.string.populating_and_send_tour);
                CustomerPathViewManager customerPathViewManager = new CustomerPathViewManager(getContext());
                addStatus(R.string.checking_customer_status);
                if (customerPathViewManager.isAllCustomersOfPathVisited()) {
                    populateAndSendData();
                } else {
                    addStatus(R.string.some_customers_are_not_visited);
                    final CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                    dialog.setTitle(R.string.alert);
                    dialog.setIcon(Icon.Warning);
                    dialog.setMessage(R.string.visit_all_statuse);
                    dialog.setPositiveButton(R.string.ok, null);
                    if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                        dialog.setNegativeButton(R.string.non_visit_all_customers, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                NonVisitAllUnknownCustomersDialog nonVisitAllUnknownCustomersDialog = new NonVisitAllUnknownCustomersDialog();
                                nonVisitAllUnknownCustomersDialog.onVisitStatusChanged = new NonVisitAllUnknownCustomersDialog.OnVisitStatusChanged() {
                                    @Override
                                    public void onChanged() {
                                        dialog.dismiss();
                                        addStatus(R.string.populating_and_send_tour);
                                        populateAndSendData();
                                    }
                                };
                                nonVisitAllUnknownCustomersDialog.show(getActivity().getSupportFragmentManager(), "NonVisitActionDialog");

                            }
                        });
                    }
                    dialog.show();
                }
            }
        });
        confirmDialog.show();
    }

    private void populateAndSendData() {
        isSending = true;
        final MainVaranegarActivity activity = getVaranegarActvity();
        if (activity == null || activity.isFinishing()) {
            isSending = false;
            return;
        }
        addStatus(R.string.checking_internet);
        if (!Connectivity.isConnected(activity)) {
            ConnectionSettingDialog connectionSettingDialog = new ConnectionSettingDialog();
            connectionSettingDialog.show(activity.getSupportFragmentManager(), "ConnectionSettingDialog");
            isSending = false;
            return;
        }
        addStatus(R.string.internet_is_connected);
        if (VaranegarApplication.is(VaranegarApplication.AppId.HotSales)) {
            HotSalesOnHandQtyApi hotSalesOnHandQtyApi = new HotSalesOnHandQtyApi(getContext());
            TourModel tourModel = tourManager.loadTour();
            Call<List<OnHandQtyModel>> call;
            call = hotSalesOnHandQtyApi.renew(UserManager.readFromFile(getContext()).UniqueId.toString(), tourModel.UniqueId.toString());
            hotSalesOnHandQtyApi.runWebRequest(call, new WebCallBack<List<OnHandQtyModel>>() {
                @Override
                protected void onFinish() {

                }

                @Override
                protected void onSuccess(List<OnHandQtyModel> result, Request request) {
                    OnHandQtyManager onHandQtyManager = new OnHandQtyManager(getContext());
                    List<OnHandQtyModel> oldOnHandQtyModels = onHandQtyManager.getItems(new Query().from(OnHandQty.OnHandQtyTbl));
                    HashMap<UUID, BigDecimal> oldOnHandQty = new HashMap<>();
                    for (OnHandQtyModel onHandQtyModel :
                            oldOnHandQtyModels) {
                        oldOnHandQty.put(onHandQtyModel.ProductId, onHandQtyModel.OnHandQty.stripTrailingZeros());
                    }
                    HashMap<UUID, BigDecimal> newOnHandQty = new HashMap<>();
                    for (OnHandQtyModel onHandQtyModel :
                            result) {
                        newOnHandQty.put(onHandQtyModel.ProductId, onHandQtyModel.OnHandQty.stripTrailingZeros());
                    }
                    if (!oldOnHandQty.equals(newOnHandQty)) {
                        isSending = false;
                        MainVaranegarActivity activity = getVaranegarActvity();
                        if (activity != null && !activity.isFinishing()) {
                            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                            dialog.setTitle(R.string.error);
                            addStatus(R.string.sending_tour_failed_for_stock_conflict);
                            dialog.setMessage(R.string.sending_tour_failed_for_stock_conflict);
                            dialog.setPositiveButton(R.string.ok, null);
                            dialog.setIcon(Icon.Error);
                            dialog.show();
                        }
                    } else {
                        tourManager.verifyData(new TourManager.VerifyCallBack() {
                            @Override
                            public void onFailure(String error) {
                                isSending = false;
                                MainVaranegarActivity activity = getVaranegarActvity();
                                if (activity != null && !activity.isFinishing()) {
                                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                                    dialog.setTitle(R.string.error);
                                    addStatus(error);
                                    dialog.setMessage(error);
                                    dialog.setPositiveButton(R.string.ok, null);
                                    dialog.setIcon(Icon.Error);
                                    dialog.show();
                                }
                            }

                            @Override
                            public void onSuccess(final List<UUID> result) {
                                if (stopped) {
                                    stopped = false;
                                    isSending = false;
                                    addStatus(R.string.sending_tour_canceled);
                                    return;
                                }
                                if (result != null && result.size() > 0) {
                                    isSending = false;
                                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                                    dialog.setTitle(R.string.some_customer_data_is_not_saved);
                                    addStatus(R.string.some_customer_data_is_not_saved);
                                    CustomerManager customerManager = new CustomerManager(getContext());
                                    List<CustomerModel> customerModels = customerManager.getCustomers(result);
                                    String msg = getString(R.string.do_you_want_to_restore_these_customers);
                                    for (CustomerModel customerModel :
                                            customerModels) {
                                        msg += System.getProperty("line.separator") + customerModel.CustomerName;
                                        addStatus(customerModel.CustomerName);
                                    }
                                    Timber.d(msg);
                                    dialog.setMessage(msg);
                                    dialog.setPositiveButton(R.string.yes, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            CustomerCallManager customerCallManager = new CustomerCallManager(getContext());
                                            for (UUID customerId :
                                                    result) {
                                                try {
                                                    customerCallManager.removeCall(CustomerCallType.SendData, customerId);
                                                } catch (DbException e) {
                                                    Timber.e(e);
                                                }
                                            }
                                        }
                                    });
                                    dialog.setNeutralButton(R.string.no, null);
                                    dialog.setIcon(Icon.Error);
                                    dialog.show();
                                } else {
                                    tourManager.sendTour(new TourManager.TourCallBack() {
                                        @Override
                                        public void onSuccess() {
                                            isSending = false;
                                            addStatus(R.string.tour_sent);
                                            clearStatus();
                                            MainVaranegarActivity activity = getVaranegarActvity();
                                            if (activity != null && !activity.isFinishing()) {
                                                CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                                dialog.setMessage(activity.getString(R.string.tour_sent));
                                                dialog.setIcon(Icon.Success);
                                                dialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        back();
                                                    }
                                                });
                                                dialog.show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(String error) {
                                            isSending = false;
                                            MainVaranegarActivity activity = getVaranegarActvity();
                                            if (activity != null && !activity.isFinishing()) {
                                                addStatus(error);
                                                CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                                dialog.setTitle(R.string.error);
                                                dialog.setMessage(error);
                                                dialog.setIcon(Icon.Error);
                                                dialog.setPositiveButton(R.string.ok, null);
                                                dialog.show();
                                            }
                                        }

                                        @Override
                                        public void onProgressChanged(String progress) {
                                            addStatus(progress);
                                        }
                                    });
                                }
                            }
                        });
                    }
                }

                @Override
                protected void onApiFailure(ApiError error, Request request) {
                    isSending = false;
                    String err = WebApiErrorBody.log(error, getContext());
                    Timber.e(err);
                    MainVaranegarActivity activity = getVaranegarActvity();
                    if (activity != null && !activity.isFinishing()) {
                        CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                        dialog.setTitle(R.string.error);
                        addStatus(err);
                        dialog.setMessage(err);
                        dialog.setPositiveButton(R.string.ok, null);
                        dialog.setIcon(Icon.Error);
                        dialog.show();
                    }
                }

                @Override
                protected void onNetworkFailure(Throwable t, Request request) {
                    isSending = false;
                    Timber.e(t.getMessage());
                    MainVaranegarActivity activity = getVaranegarActvity();
                    if (activity != null && !activity.isFinishing()) {
                        CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                        dialog.setTitle(R.string.error);
                        addStatus(R.string.network_error);
                        dialog.setMessage(R.string.network_error);
                        dialog.setPositiveButton(R.string.ok, null);
                        dialog.setIcon(Icon.Error);
                        dialog.show();
                    }
                }
            });
        } else {
            tourManager.verifyData(new TourManager.VerifyCallBack() {
                @Override
                public void onFailure(String error) {
                    isSending = false;
                    MainVaranegarActivity activity = getVaranegarActvity();
                    if (activity != null && !activity.isFinishing()) {
                        CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                        dialog.setTitle(R.string.error);
                        addStatus(error);
                        dialog.setMessage(error);
                        dialog.setPositiveButton(R.string.ok, null);
                        dialog.setIcon(Icon.Error);
                        dialog.show();
                    }
                }

                @Override
                public void onSuccess(final List<UUID> result) {
                    if (stopped) {
                        stopped = false;
                        isSending = false;
                        addStatus(R.string.sending_tour_canceled);
                        return;
                    }
                    if (result != null && result.size() > 0) {
                        isSending = false;
                        CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                        dialog.setTitle(R.string.some_customer_data_is_not_saved);
                        addStatus(R.string.some_customer_data_is_not_saved);
                        CustomerManager customerManager = new CustomerManager(getContext());
                        List<CustomerModel> customerModels = customerManager.getCustomers(result);
                        String msg = getString(R.string.do_you_want_to_restore_these_customers);
                        for (CustomerModel customerModel :
                                customerModels) {
                            msg += System.getProperty("line.separator") + customerModel.CustomerName;
                            addStatus(customerModel.CustomerName);
                        }
                        Timber.d(msg);
                        dialog.setMessage(msg);
                        dialog.setPositiveButton(R.string.yes, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CustomerCallManager customerCallManager = new CustomerCallManager(getContext());
                                for (UUID customerId :
                                        result) {
                                    try {
                                        customerCallManager.removeCall(CustomerCallType.SendData, customerId);
                                    } catch (DbException e) {
                                        Timber.e(e);
                                    }
                                }
                            }
                        });
                        dialog.setNeutralButton(R.string.no, null);
                        dialog.setIcon(Icon.Error);
                        dialog.show();
                    } else {
                        tourManager.sendTour(new TourManager.TourCallBack() {
                            @Override
                            public void onSuccess() {
                                isSending = false;
                                addStatus(R.string.tour_sent);
                                clearStatus();
                                MainVaranegarActivity activity = getVaranegarActvity();
                                if (activity != null && !activity.isFinishing()) {
                                    CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                    dialog.setMessage(activity.getString(R.string.tour_sent));
                                    dialog.setIcon(Icon.Success);
                                    dialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            back();
                                        }
                                    });
                                    dialog.show();
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                isSending = false;
                                MainVaranegarActivity activity = getVaranegarActvity();
                                if (activity != null && !activity.isFinishing()) {
                                    addStatus(error);
                                    CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                    dialog.setTitle(R.string.error);
                                    dialog.setMessage(error);
                                    dialog.setIcon(Icon.Error);
                                    dialog.setPositiveButton(R.string.ok, null);
                                    dialog.show();
                                }
                            }

                            @Override
                            public void onProgressChanged(String progress) {
                                addStatus(progress);
                            }
                        });
                    }
                }
            });
        }
    }
}
