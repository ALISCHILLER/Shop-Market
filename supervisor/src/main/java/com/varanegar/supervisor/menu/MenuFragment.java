package com.varanegar.supervisor.menu;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.supervisor.IMainPageFragment;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.webapi.OnHandQtyReportViewModel;
import com.varanegar.supervisor.webapi.OnHandQtyView;
import com.varanegar.supervisor.webapi.OnHandQtyViewModel;
import com.varanegar.supervisor.webapi.SupervisorApi;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;

import java.util.List;

import okhttp3.Request;
import timber.log.Timber;

public class MenuFragment extends IMainPageFragment {

    private ReportView reportView;
    private TextView errorTextView;
    private EditText searchEditText;
    private String keyWord;
    private List<OnHandQtyViewModel> mainList;
    private SimpleReportAdapter<OnHandQtyViewModel> adapter;
    private ImageView clearImageView;
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            if (text != null && !text.isEmpty())
            {
                clearImageView.setVisibility(View.VISIBLE);
                List<OnHandQtyViewModel> list = runFilter();
                adapter.clear();
                adapter.addAll(list);
            }
            else
                clearImageView.setVisibility(View.GONE);

        }
    };

    @Override
    protected View onCreateContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supervisor_menu_layout, container, false);
        searchEditText = view.findViewById(R.id.search_edit_text);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard();
                    List<OnHandQtyViewModel> list = runFilter();
                    adapter.clear();
                    adapter.addAll(list);
                }
                return true;
            }
        });

        searchEditText.addTextChangedListener(textWatcher);

        clearImageView = view.findViewById(R.id.clear_image_view);
        clearImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                searchEditText.setText("");
                adapter.clear();
                adapter.addAll(mainList);
            }
        });

        errorTextView = view.findViewById(R.id.error_text_view);
        reportView = view.findViewById(R.id.report_view);
        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });
        return view;
    }

    private void refresh() {
        Context context = getContext();
        if (context != null) {
            SysConfigManager sysConfigManager = new SysConfigManager(context);
            SysConfigModel deviceSettingsNo = sysConfigManager.read(ConfigKey.DeviceSettingNo, SysConfigManager.cloud);
            SupervisorApi supervisorApi = new SupervisorApi(context);
            startProgress(R.string.please_wait, R.string.connecting_to_the_server);
            supervisorApi.runWebRequest(supervisorApi.onHandQty(UserManager.readFromFile(context).UniqueId, SysConfigManager.getIntValue(deviceSettingsNo, 3)), new WebCallBack<OnHandQtyReportViewModel>() {
                @Override
                protected void onFinish() {
                    finishProgress();
                }

                @Override
                protected void onSuccess(OnHandQtyReportViewModel result, Request request) {
                    if (isResumed()) {
                        Activity activity = getActivity();
                        if (activity != null && !activity.isFinishing()) {
                            if (result.OnHandQty == null && result.OnHandQty.size() == 0) {
                                errorTextView.setVisibility(View.VISIBLE);
                                reportView.setVisibility(View.GONE);
                            } else {
                                errorTextView.setVisibility(View.GONE);
                                reportView.setVisibility(View.VISIBLE);
                            }
                            mainList = result.OnHandQty;
                            List<OnHandQtyViewModel> list = runFilter();
                            adapter = new SimpleReportAdapter<OnHandQtyViewModel>(getVaranegarActvity(), OnHandQtyViewModel.class) {
                                @Override
                                public void bind(ReportColumns columns, OnHandQtyViewModel entity) {
                                    columns.add(bind(entity, OnHandQtyView.ProductCode, getActivity().getString(R.string.product_code_label)).setWeight(1f).setFrizzed());
                                    columns.add(bind(entity, OnHandQtyView.ProductName, getActivity().getString(R.string.product_name_label)).setWeight(2f));
                                    columns.add(bind(entity, OnHandQtyView.OnHandQty, getActivity().getString(R.string.onhand_qty)).setWeight(1f));
                                    columns.add(bind(entity, OnHandQtyView.OnHandQtyAfterReserve, getActivity().getString(R.string.onhand_qty_after_reserve)).setWeight(1.5f));
                                    columns.add(bind(entity, OnHandQtyView.StockName, getActivity().getString(com.varanegar.vaslibrary.R.string.stock_name)).setWeight(1.5f));
                                }
                            };
                            adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
                            adapter.create(list, null);
                            reportView.setAdapter(adapter);
                        }
                    }
                }

                @Override
                protected void onApiFailure(ApiError error, Request request) {
                    if (isResumed()) {
                        Activity activity = getActivity();
                        if (activity != null && !activity.isFinishing()) {
                            errorTextView.setVisibility(View.VISIBLE);
                            reportView.setVisibility(View.GONE);
                            String err = WebApiErrorBody.log(error, getContext());
                            showErrorDialog(err);
                        }
                    }
                }

                @Override
                protected void onNetworkFailure(Throwable t, Request request) {
                    if (isResumed()) {
                        Activity activity = getActivity();
                        if (activity != null && !activity.isFinishing()) {
                            errorTextView.setVisibility(View.VISIBLE);
                            reportView.setVisibility(View.GONE);
                            Timber.e(t);
                            showErrorDialog(getContext().getString(R.string.error_connecting_to_server));
                        }
                    }
                }
            });
        }
    }

    private List<OnHandQtyViewModel> runFilter() {
        keyWord = searchEditText.getText().toString();
        if (keyWord != null && !keyWord.isEmpty()) {
            return Linq.findAll(mainList, new Linq.Criteria<OnHandQtyViewModel>() {
                @Override
                public boolean run(OnHandQtyViewModel item) {
                    String text = VasHelperMethods.persian2Arabic(keyWord);
                    text = VasHelperMethods.convertToEnglishNumbers(text);
                    String productName = item.ProductName == null ? "" : item.ProductName;
                    String productCode = item.ProductCode == null ? "" : item.ProductCode;
                    if (text != null)
                        return productName.contains(text) || productCode.contains(text);
                    else
                        return false;
                }
            });
        } else {
            return mainList;
        }
    }

    private void showErrorDialog(String error) {
        if (isResumed()) {
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setTitle(com.varanegar.vaslibrary.R.string.error);
            dialog.setMessage(error);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(com.varanegar.vaslibrary.R.string.ok, null);
            dialog.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

}
