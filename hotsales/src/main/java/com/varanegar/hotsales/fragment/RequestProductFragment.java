package com.varanegar.hotsales.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.SearchBox;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.component.toolbar.CuteButton;
import com.varanegar.framework.util.component.toolbar.CuteToolbar;
import com.varanegar.framework.util.recycler.ContextMenuItem;
import com.varanegar.framework.util.report.ReportAdapter;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.framework.validation.ConstraintViolation;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.hotsales.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.ProductManager;
import com.varanegar.vaslibrary.manager.ProductType;
import com.varanegar.vaslibrary.manager.ProductUnitViewManager;
import com.varanegar.vaslibrary.manager.productrequest.RequestLineManager;
import com.varanegar.vaslibrary.manager.productrequest.RequestLineQtyManager;
import com.varanegar.vaslibrary.manager.productrequest.RequestLineViewManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.RequestItemLines.RequestLineModel;
import com.varanegar.vaslibrary.model.RequestItemLines.RequestLineQtyModel;
import com.varanegar.vaslibrary.model.RequestItemLines.RequestLineView;
import com.varanegar.vaslibrary.model.RequestItemLines.RequestLineViewModel;
import com.varanegar.vaslibrary.model.RequestItemLines.RequestLineViewModelRepository;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.calculator.CalculatorHelper;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;
import com.varanegar.vaslibrary.ui.dialog.ConnectionSettingDialog;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.ping.PingApi;
import com.varanegar.vaslibrary.webapi.product.RequestProductApi;
import com.varanegar.vaslibrary.webapi.tour.SyncGetRequestLineModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetTourViewModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Request;
import okhttp3.ResponseBody;
import timber.log.Timber;

/**
 * Created by A.Torabi on 3/25/2018.
 */

public class RequestProductFragment extends VaranegarFragment {
    private CuteToolbar toolbar;
    private ReportAdapter<RequestLineViewModel> requestAdapter;
    private boolean isEnabled;
    private SimpleToolbar simpleToolbar;

    private void setTitle(String title) {
        if (simpleToolbar != null)
            simpleToolbar.setTitle(title);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_product, container, false);
        simpleToolbar = (SimpleToolbar) view.findViewById(R.id.simple_toolbar);
        simpleToolbar.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        requestAdapter = new SimpleReportAdapter<RequestLineViewModel>(getVaranegarActvity(), RequestLineViewModel.class) {
            @Override
            public void bind(ReportColumns columns, RequestLineViewModel entity) {
                bindRowNumber(columns);
                columns.add(bind(entity, RequestLineView.ProductCode, getString(R.string.product_code)).setSortable().setFrizzed());
                columns.add(bind(entity, RequestLineView.ProductName, getString(R.string.product_name)).setWeight(2).setSortable().setFrizzed());
                columns.add(bind(entity, RequestLineView.Qty, getString(R.string.qty)));
                columns.add(bind(entity, RequestLineView.TotalQty, getString(R.string.total_qty)).calcTotal().setSortable());
                columns.add(bind(entity, RequestLineView.UnitPrice, getString(R.string.unit_price)).setSortable());
                columns.add(bind(entity, RequestLineView.TotalPrice, getString(R.string.total_price)).calcTotal().setSortable());
            }

        };
        requestAdapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
        requestAdapter.create(new RequestLineViewModelRepository(), RequestLineViewManager.getLines(BigDecimal.ZERO), savedInstanceState);
        requestAdapter.addOnItemClickListener(new ContextMenuItem() {
            @Override
            public boolean isAvailable(int position) {
                return true;
            }

            @Override
            public String getName(int posiotn) {
                return getString(R.string.edit);
            }

            @Override
            public int getIcon(int position) {
                return R.drawable.ic_mode_edit_black_24dp;
            }

            @Override
            public void run(int position) {
                final RequestLineViewModel selectedItem = requestAdapter.get(position);
                RequestCalculatorForm requestCalculatorForm = new RequestCalculatorForm();
                try {
                    final ProductModel productModel = new ProductManager(getContext()).getItem(selectedItem.UniqueId);
                    if (productModel == null)
                        throw new NullPointerException("Product id " + selectedItem.UniqueId + " not found");
                    CalculatorHelper calculatorHelper = new CalculatorHelper(getContext());
                    List<RequestLineQtyModel> requestLineQtyModels = new ArrayList<>();
                    RequestLineQtyManager requestLineQtyManager = new RequestLineQtyManager(getContext());
                    requestLineQtyModels = requestLineQtyManager.getQtyLines(selectedItem.RequestLineUniqueId);
                    RequestLineManager requestLineManager = new RequestLineManager(getContext());
                    RequestLineModel requestLineModel = requestLineManager.getRequestLine(productModel.UniqueId);
                    BaseUnit bulkUnit = calculatorHelper.getBulkQtyUnit(requestLineModel);
                    requestCalculatorForm.setArguments(productModel, calculatorHelper.generateCalculatorUnits(productModel.UniqueId, requestLineQtyModels, bulkUnit, ProductType.isForSale));
                    requestCalculatorForm.onCalcFinish = new RequestCalculatorForm.OnCalcFinish() {
                        @Override
                        public void run(List<DiscreteUnit> discreteUnits, BaseUnit bulkUnit) {
                            onAddItem(selectedItem, discreteUnits, bulkUnit);
                        }
                    };
                    requestCalculatorForm.show(getChildFragmentManager(), "9d0ab16e-09b2-4bc4-8e6e-e6ef7252ee43");
                } catch (ProductUnitViewManager.UnitNotFoundException e) {
                    getVaranegarActvity().showSnackBar(R.string.no_unit_for_product, MainVaranegarActivity.Duration.Short);
                    Timber.e(e);
                }
            }
        });
        requestAdapter.addOnItemClickListener(new ContextMenuItem() {
            @Override
            public boolean isAvailable(int position) {
                return requestAdapter.get(position).RequestLineUniqueId != null;
            }

            @Override
            public String getName(int posiotn) {
                return getString(R.string.remove);
            }

            @Override
            public int getIcon(int position) {
                return R.drawable.ic_delete_forever_black_24dp;
            }

            @Override
            public void run(int position) {
                final RequestLineViewModel selectedItem = requestAdapter.get(position);
                RequestLineManager requestLineManager = new RequestLineManager(getContext());
                try {
                    requestLineManager.deleteProduct(selectedItem.UniqueId);
                    requestAdapter.refresh();
                } catch (DbException e) {
                    Timber.e(e);
                }

            }
        });
        ReportView reportView = (ReportView) view.findViewById(R.id.report_view);
        reportView.setAdapter(requestAdapter);
        Date date = new UpdateManager(getContext()).getLog(UpdateKey.ProductRequest);
        isEnabled = date.equals(UpdateManager.MIN_DATE);
        if (isEnabled)
            setTitle(getString(R.string.product_request));
        else
            setTitle(getString(R.string.product_request) + " (" + getString(R.string.is_sent) + ") ");
        requestAdapter.setEnabled(isEnabled);
        setupToolbarButtons(view);
        return view;
    }

    private void setupToolbarButtons(View view) {
        toolbar = (CuteToolbar) view.findViewById(R.id.options_toolbar);

        CuteButton sendButton = new CuteButton();
        sendButton.setEnabled(new CuteButton.IIsEnabled() {
            @Override
            public boolean run() {
                return requestAdapter.size() > 0 && isEnabled;
            }
        });
        sendButton.setTitle(R.string.send_request);
        sendButton.setIcon(com.varanegar.vaslibrary.R.drawable.ic_send_request_black_36dp);
        sendButton.setOnClickListener(new CuteButton.OnClickListener() {
            @Override
            public void onClick() {
                if (requestAdapter.size() == 0) {
                    CuteMessageDialog alert = new CuteMessageDialog(getContext());
                    alert.setIcon(Icon.Alert);
                    alert.setTitle(R.string.error);
                    alert.setMessage(R.string.request_is_empty);
                    alert.setPositiveButton(R.string.ok, null);
                    alert.show();
                } else
                    trySend();
            }
        });

        CuteButton searchButton = new CuteButton();
        searchButton.setOnClickListener(new CuteButton.OnClickListener() {
            @Override
            public void onClick() {
                final SearchBox<RequestLineViewModel> searchBox = new SearchBox<>();
                searchBox.setTitle(getString(R.string.select_product));
                searchBox.setRepository(new RequestLineViewModelRepository(), new SearchBox.SearchQuery() {
                    @Override
                    public Query onSearch(String text) {
                        return RequestLineViewManager.searchQuery(text, null);
                    }
                });
                searchBox.setOnItemSelectedListener(new SearchBox.OnItemSelectedListener<RequestLineViewModel>() {
                    @Override
                    public void run(int position, final RequestLineViewModel selectedItem) {
                        searchBox.dismiss();
                        final RequestLineViewModel requestLineViewModel = Linq.findFirst(requestAdapter.getAdapter().getItems(), new Linq.Criteria<RequestLineViewModel>() {
                            @Override
                            public boolean run(RequestLineViewModel item) {
                                return item.UniqueId.equals(selectedItem.UniqueId);
                            }
                        });
                        List<RequestLineQtyModel> requestLineQtyModels = new ArrayList<>();
                        RequestLineQtyManager requestLineQtyManager = new RequestLineQtyManager(getContext());
                        if (requestLineViewModel != null) {
                            requestLineQtyModels = requestLineQtyManager.getQtyLines(requestLineViewModel.RequestLineUniqueId);
                        }
                        RequestCalculatorForm requestCalculatorForm = new RequestCalculatorForm();
                        try {
                            final ProductModel productModel = new ProductManager(getContext()).getItem(selectedItem.UniqueId);
                            if (productModel == null)
                                throw new NullPointerException("Product id " + selectedItem.UniqueId + " not found");
                            CalculatorHelper calculatorHelper = new CalculatorHelper(getContext());
                            RequestLineManager requestLineManager = new RequestLineManager(getContext());
                            RequestLineModel requestLineModel = requestLineManager.getRequestLine(productModel.UniqueId);
                            BaseUnit bulkUnit = calculatorHelper.getBulkQtyUnit(requestLineModel);
                            requestCalculatorForm.setArguments(productModel, calculatorHelper.generateCalculatorUnits(productModel.UniqueId, requestLineQtyModels, bulkUnit, ProductType.isForSale));
                            requestCalculatorForm.onCalcFinish = new RequestCalculatorForm.OnCalcFinish() {
                                @Override
                                public void run(List<DiscreteUnit> discreteUnits, BaseUnit bulkUnit) {
                                    onAddItem(requestLineViewModel, discreteUnits, bulkUnit);
                                }
                            };
                            requestCalculatorForm.show(getChildFragmentManager(), "9d0ab16e-09b2-4bc4-8e6e-e6ef7252ee43");
                        } catch (ProductUnitViewManager.UnitNotFoundException e) {
                            getVaranegarActvity().showSnackBar(R.string.no_unit_for_product, MainVaranegarActivity.Duration.Short);
                            Timber.e(e);
                        }
                    }
                });
                searchBox.show(getChildFragmentManager(), "1f86f768-643e-46f3-8183-d8e8c4e7e94b");
            }
        });
        searchButton.setIcon(R.drawable.ic_search_white_36dp);
        searchButton.setTitle(R.string.fast_search);
        searchButton.setEnabled(new CuteButton.IIsEnabled() {
            @Override
            public boolean run() {
                return isEnabled;
            }
        });

        CuteButton listButton = new CuteButton();
        listButton.setTitle(R.string.product_list);
        listButton.setIcon(R.drawable.ic_view_list_white_36dp);
        listButton.setEnabled(new CuteButton.IIsEnabled() {
            @Override
            public boolean run() {
                return isEnabled;
            }
        });
        listButton.setOnClickListener(new CuteButton.OnClickListener() {
            @Override
            public void onClick() {
                RequestProductGroupFragment fragment = new RequestProductGroupFragment();
                getVaranegarActvity().pushFragment(fragment);
            }
        });

        CuteButton cancelBtn = new CuteButton();
        cancelBtn.setEnabled(new CuteButton.IIsEnabled() {
            @Override
            public boolean run() {
                return requestAdapter.size() > 0 && isEnabled;
            }
        });
        cancelBtn.setOnClickListener(new CuteButton.OnClickListener() {
            @Override
            public void onClick() {
                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                dialog.setPositiveButton(R.string.yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RequestLineManager requestLineManager = new RequestLineManager(getContext());
                        try {
                            long affectedRows = requestLineManager.deleteAllLines();
                            Timber.d(affectedRows + " items removed from request line");
                            getVaranegarActvity().popFragment();
                        } catch (DbException e) {
                            Timber.e(e);
                        }

                    }
                });
                dialog.setNeutralButton(R.string.no, null);
                dialog.setIcon(Icon.Warning);
                dialog.setTitle(R.string.warning);
                dialog.setMessage(R.string.you_are_deleting_request_are_you_sure);
                dialog.show();
            }
        });

        cancelBtn.setTitle(R.string.cancel);
        cancelBtn.setIcon(R.drawable.ic_cancel_black_36dp);

        List<CuteButton> buttons = new ArrayList<>();
        buttons.add(searchButton);
        buttons.add(listButton);
        buttons.add(cancelBtn);
        buttons.add(sendButton);
        toolbar.setButtons(buttons);
    }

    private void onAddItem(RequestLineViewModel requestLineViewModel, List<DiscreteUnit> discreteUnits, BaseUnit bulkUnit) {
        RequestLineManager requestLineManager = new RequestLineManager(getContext());
        try {
            requestLineManager.addOrUpdateQty(discreteUnits, bulkUnit);
            refresh();
        } catch (ValidationException e) {
            Timber.e(e);
        } catch (DbException e) {
            Timber.e(e);
        }



    }

    private void refresh() {
        requestAdapter.refresh();
        UpdateManager updateManager = new UpdateManager(getContext());
        Date date = updateManager.getLog(UpdateKey.ProductRequest);
        isEnabled = date.equals(UpdateManager.MIN_DATE);
        requestAdapter.setEnabled(isEnabled);
        toolbar.refresh();
        if (isEnabled)
            setTitle(getString(R.string.product_request));
        else
            setTitle(getString(R.string.product_request) + " (" + getString(R.string.is_sent) + ") ");
    }

    private void trySend() {
        CuteMessageDialog dialog = new CuteMessageDialog(getContext());
        dialog.setPositiveButton(R.string.yes, new View.OnClickListener() {
            ProgressDialog progressDialog;

            private void showProgressDialog() {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage(getActivity().getString(R.string.sending_request_product));
                    progressDialog.setCancelable(false);
                }
                progressDialog.show();
            }

            private void dismissProgressDialog() {
                if (getActivity().isFinishing())
                    return;
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }

            @Override
            public void onClick(View view) {
                RequestLineViewManager requestLineManager = new RequestLineViewManager(getContext());
                final List<SyncGetRequestLineModel> syncGetRequestLineModels = requestLineManager.getSyncRequestLine();
                if (syncGetRequestLineModels != null) {
                    TourManager tourManager = new TourManager(getActivity());
                    TourModel tourModel = tourManager.loadTour();
                    final SyncGetTourViewModel syncGetTourViewModel = new SyncGetTourViewModel(getContext(), tourModel.UniqueId , tourModel.TourNo);
                    syncGetTourViewModel.RequestItemLines = syncGetRequestLineModels;
                    if (!Connectivity.isConnected(getActivity())) {
                        ConnectionSettingDialog connectionSettingDialog = new ConnectionSettingDialog();
                        connectionSettingDialog.show(getActivity().getSupportFragmentManager(), "ConnectionSettingDialog");
                        return;
                    }
                    PingApi pingApi = new PingApi();
                    showProgressDialog();
                    pingApi.refreshBaseServerUrl(getActivity(), new PingApi.PingCallback() {
                        @Override
                        public void done(String ipAddress) {
                            SysConfigManager sysConfigManager = new SysConfigManager(getActivity());
                            SysConfigModel settingsId = sysConfigManager.read(ConfigKey.SettingsId, SysConfigManager.local);
                            RequestProductApi requestProductApi = new RequestProductApi(getContext());
                            requestProductApi.runWebRequest(requestProductApi.saveRequestData(syncGetTourViewModel, settingsId.Value), new WebCallBack<ResponseBody>() {
                                @Override
                                protected void onFinish() {

                                }

                                @Override
                                protected void onSuccess(ResponseBody result, Request request) {
                                    dismissProgressDialog();
                                    if (!getActivity().isFinishing()) {
                                        CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                                        dialog.setMessage(R.string.sent_request_report);
                                        dialog.setTitle(com.varanegar.vaslibrary.R.string.done);
                                        dialog.setIcon(Icon.Success);
                                        dialog.setPositiveButton(com.varanegar.vaslibrary.R.string.ok, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                new UpdateManager(getContext()).addLog(UpdateKey.ProductRequest);
                                                refresh();
                                            }
                                        });
                                        dialog.show();
                                    }
                                }

                                @Override
                                protected void onApiFailure(ApiError error, Request request) {
                                    dismissProgressDialog();
                                    String err = WebApiErrorBody.log(error, getContext());
                                    if (!getActivity().isFinishing()) {
                                        CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                                        dialog.setMessage(err);
                                        dialog.setTitle(com.varanegar.vaslibrary.R.string.error);
                                        dialog.setIcon(Icon.Error);
                                        dialog.setPositiveButton(com.varanegar.vaslibrary.R.string.ok, null);
                                        dialog.show();
                                    }
                                }

                                @Override
                                protected void onNetworkFailure(Throwable t, Request request) {
                                    if (!getActivity().isFinishing()) {
                                        dismissProgressDialog();
                                        CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                                        dialog.setMessage(t.getMessage());
                                        dialog.setTitle(com.varanegar.vaslibrary.R.string.error);
                                        dialog.setIcon(Icon.Error);
                                        dialog.setPositiveButton(com.varanegar.vaslibrary.R.string.ok, null);
                                        dialog.show();
                                    }
                                }
                            });
                        }

                        @Override
                        public void failed() {
                            if (!getActivity().isFinishing()) {
                                dismissProgressDialog();
                                CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                                dialog.setPositiveButton(com.varanegar.vaslibrary.R.string.ok, null);
                                dialog.setTitle(com.varanegar.vaslibrary.R.string.error);
                                dialog.setMessage(com.varanegar.vaslibrary.R.string.network_error);
                                dialog.setIcon(Icon.Error);
                                dialog.show();
                            }
                        }
                    });
                }
            }
        });
        dialog.setNeutralButton(R.string.no, null);
        dialog.setIcon(Icon.Warning);
        dialog.setTitle(R.string.warning);
        dialog.setMessage(R.string.you_are_sending_product_request_are_you_sure);
        dialog.show();
    }
}
