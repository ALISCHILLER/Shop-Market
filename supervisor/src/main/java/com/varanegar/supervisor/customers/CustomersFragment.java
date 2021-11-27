package com.varanegar.supervisor.customers;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.PairedItemsSpinner;
import com.varanegar.framework.util.component.ProgressView;
import com.varanegar.framework.util.component.SearchBox;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.recycler.ItemContextView;
import com.varanegar.framework.util.report.CustomViewHolder;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.supervisor.DataManager;
import com.varanegar.supervisor.IMainPageFragment;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.VisitorFilter;
import com.varanegar.supervisor.model.ConditionModel;
import com.varanegar.supervisor.utill.dialog.BackMessageDialog;
import com.varanegar.supervisor.webapi.SupervisorApi;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.questionnaire.QuestionnaireCustomerViewManager;
import com.varanegar.vaslibrary.manager.questionnaire.QuestionnaireManager;
import com.varanegar.vaslibrary.model.customer.SupervisorCustomer;
import com.varanegar.vaslibrary.model.customer.SupervisorCustomerModel;
import com.varanegar.supervisor.model.VisitorManager;
import com.varanegar.supervisor.model.VisitorModel;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.model.customer.SupervisorCustomerModelRepository;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireCustomerViewModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.ui.fragment.QuestionnaireFragment;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.ResponseBody;
import timber.log.Timber;

/**
 * Created by A.Torabi on 7/8/2018.
 * supervisor
 * لیست نمایش مشتری در supervisor
 */

public class CustomersFragment extends IMainPageFragment {
    private static final int REQUEST_PHONE_CALL = 90;
    private ReportView customersReportView;
    private EditText searchEditText;
    private long last_text_edit;
    Handler handler = new Handler();
    private long delay = 200;
    private String keyWord;
    private ImageView clearSearchImageView;
    private SimpleReportAdapter<SupervisorCustomerModel> adapter;
    private TextView errorTextView;
    private PairedItemsSpinner<VisitorModel> visitorNameSpinner;
    private PairedItemsSpinner<String>  conditionCustomerSpinner;
    private ProgressView progressView;
    private Boolean message;
    @Override
    protected View onCreateContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supervisor_customer_list_layout, container, false);
        visitorNameSpinner = view.findViewById(R.id.visitor_name_paired_items);

        conditionCustomerSpinner=view.findViewById(R.id.customer_condition_paired_spinner);
        errorTextView = view.findViewById(R.id.error_text_view);
        clearSearchImageView = view.findViewById(R.id.clear_text);
        clearSearchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText("");
            }
        });
        searchEditText = view.findViewById(R.id.search_edit_text);
        customersReportView = view.findViewById(R.id.customers_report_view);
        searchEditText.addTextChangedListener(new TextWatcher() {
            String before = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final String after = s.toString();
                if (after.equals(before))
                    return;
                last_text_edit = System.currentTimeMillis();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if (System.currentTimeMillis() > (last_text_edit + delay)) {
                            String str = null;
                            if (after.length() > 1)
                                str = after;
                            keyWord = str;
                            MainVaranegarActivity activity = getVaranegarActvity();
                            if (activity != null && !activity.isFinishing()) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshAdapter();
                                    }
                                });
                            }
                        }
                    }
                }, delay + 10);

                if (after.isEmpty())
                    clearSearchImageView.setVisibility(View.GONE);
                else
                    clearSearchImageView.setVisibility(View.VISIBLE);
                before = after;
            }
        });

        return view;
    }

    private void refreshAdapter() {
        Query query = new Query().from(SupervisorCustomer.SupervisorCustomerTbl);
        if (keyWord != null && !keyWord.isEmpty()) {
            String key = VasHelperMethods.convertToEnglishNumbers(keyWord);
            key = VasHelperMethods.persian2Arabic(key);
            final String finalKey = key;
            query = new Query().from(SupervisorCustomer.SupervisorCustomerTbl).whereAnd(Criteria.contains(SupervisorCustomer.CustomerCode, finalKey)
                    .or(Criteria.contains(SupervisorCustomer.CustomerName, finalKey))
                    .or(Criteria.contains(SupervisorCustomer.StoreName, finalKey)));
        }
        VisitorModel visitorModel = VisitorFilter.read(getContext());
        String conditionCustomer = VisitorFilter.readconditionCustomer(getContext());
        if (visitorModel.UniqueId != null)
            query = query.whereAnd(Criteria.equals(SupervisorCustomer.DealerId, visitorModel.UniqueId));

        if (conditionCustomer.equals(getString(R.string.condition_block))){
            query = query.whereAnd(Criteria.equals(SupervisorCustomer.isActive,false));
        }else if (conditionCustomer.equals(getString(R.string.condition_unblock))){
            query = query.whereAnd(Criteria.equals(SupervisorCustomer.isActive,true));
        }
        if (errorTextView != null) {
            adapter = new SimpleReportAdapter<SupervisorCustomerModel>(this, SupervisorCustomerModel.class) {
                class PhoneCustomerViewHoder extends CustomViewHolder<SupervisorCustomerModel> {

                    @Override
                    public void onBind(View view, final SupervisorCustomerModel entity) {
                        ImageView callImageView = null;
                        ImageView customercondition=view.findViewById(R.id.customer_condition_image_view);
                        if (!entity.isPenddingChange) {
                            customercondition.setImageResource(R.drawable.ic_baseline_person_24);
                        }else if(entity.isPenddingChange) {
                            customercondition.setImageResource(R.drawable.ic_baseline_block_24);
                        }
                        customercondition.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (entity.isPenddingChange){
                                    message=false;
                                }else if (!entity.isActive){
                                   message=true;
                                }
                                BackMessageDialog builder = new BackMessageDialog(getActivity());
                                builder.setTitle(getActivity().getString(com.varanegar.vaslibrary.R.string.alert));

                                builder.setData("داده های ویرایش شده" + " \n "+ "نام فروشگاه:" +" "+ entity.StoreName+ "\n " +" شماره موبایل:"+entity.Mobile+" \n " +"شماره تلفن:"+entity.Phone+"\n"+"نام مشتری :"+entity.CustomerName +
                                        " \n "+"ادرس:"+entity.Address+"\n" +" درجه مشتری:"+ entity.CustomerLevel+"\n"+"کدپستی:"+entity.PostCode+"\n"+"نوع مشتری 2:"+entity.CustomerActivityId+"\n"+"نوع مشتری 1 :"+entity.CustomerCategoryId);
                                if (entity.isActive) {
                                    builder.setMessage(getActivity().getString(com.varanegar.vaslibrary.R.string.dialog_black ));
                                } else if (!entity.isActive){
                                    builder.setMessage(getActivity().getString(com.varanegar.vaslibrary.R.string.dialog_unblack));
                                }
                                    builder.setPositiveButton(com.varanegar.vaslibrary.R.string.yes, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        requsttoconditionCustomer(entity);

                                    }
                                });
                                builder.setNegativeButton(com.varanegar.vaslibrary.R.string.no, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                });
                                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialogInterface) {

                                    }
                                });
                                builder.show();
                            }
                        });

                        view.findViewById(R.id.phone_image_view).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    Activity activity = getActivity();
                                    if (activity.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                        activity.requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                                    } else {
                                        try {
                                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + entity.Phone));
                                            startActivity(intent);
                                        } catch (Exception ignored) {

                                        }
                                    }
                                } else {
                                    try {
                                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + entity.Phone));
                                        startActivity(intent);
                                    } catch (Exception ignored) {

                                    }
                                }
                            }
                        });

                        view.findViewById(R.id.mobile_image_view).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        Activity activity = getActivity();
                                        if (activity.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                            activity.requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                                        } else {
                                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + entity.Mobile));
                                            startActivity(intent);
                                        }
                                    } else {
                                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + entity.Mobile));
                                        startActivity(intent);
                                    }
                                } catch (Exception ignored) {

                                }
                            }
                        });

                        view.findViewById(R.id.Questionnaire_image_view).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                QuestionnaireManager questionnaireManager = new QuestionnaireManager(getContext());
                                try {
                                    questionnaireManager.calculateCustomerQuestionnaire(entity.UniqueId);
                                    QuestionnaireCustomerViewManager questionnaireCustomerViewManager = new QuestionnaireCustomerViewManager(getContext());
                                    List<QuestionnaireCustomerViewModel> questionnaireCustomerViewModels = questionnaireCustomerViewManager.getQuestionnaires(entity.UniqueId);
                                    if (questionnaireCustomerViewModels.size() == 0) {
                                        getVaranegarActvity().showSnackBar(com.varanegar.vaslibrary.R.string.no_questionnaire_for_this_customer, MainVaranegarActivity.Duration.Short);
                                    } else {
                                        QuestionnaireFragment fragment = new QuestionnaireFragment();
                                        fragment.setCustomerId(entity.UniqueId);
                                        getVaranegarActvity().pushFragment(fragment);
                                    }
                                } catch (Exception e) {
                                    Timber.e(e);
                                    getVaranegarActvity().showSnackBar(com.varanegar.vaslibrary.R.string.calculating_questionnaire_failed, MainVaranegarActivity.Duration.Short);
                                }
                            }
                        });


                    }

                    @Override
                    public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                        View view = inflater.inflate(R.layout.supervisor_report_phone_layput, parent, false);

                        return view;
                    }


                }

                @Override
                public void bind(ReportColumns columns, SupervisorCustomerModel entity) {
                    bindRowNumber(columns);
                    columns.add(bind(entity, SupervisorCustomer.CustomerCode, getString(R.string.customer_code)).setFrizzed());
                    columns.add(bind(entity, SupervisorCustomer.CustomerName, getString(R.string.customer_name)));
                    columns.add(bind(entity, SupervisorCustomer.newStoreName, getString(R.string.store_name)));
                    if (isLandscape()) {
                        columns.add(bind(entity, SupervisorCustomer.Phone, "").setCustomViewHolder(new PhoneCustomerViewHoder()).setWeight(2.2f));
                    }
                    columns.add(bind(entity, SupervisorCustomer.newPhone, getString(R.string.phone_number)));
                    columns.add(bind(entity, SupervisorCustomer.newMobile, getString(R.string.mobile_label)));
                    columns.add(bind(entity, SupervisorCustomer.newAddress, getString(R.string.address)).setWeight(3));
                    columns.add(bind(entity, SupervisorCustomer.NationalCode, getString(R.string.national_id_label)).sendToDetail());
                    columns.add(bind(entity, SupervisorCustomer.newCustomerActivityName, getString(R.string.customer_activity)).sendToDetail());
                    columns.add(bind(entity, SupervisorCustomer.newCustomerLevelName, getString(R.string.customer_level)).sendToDetail());
                    columns.add(bind(entity, SupervisorCustomer.CustomerCategory, getString(R.string.customer_category)).sendToDetail());
                    columns.add(bind(entity, SupervisorCustomer.PathTitle, getString(R.string.path_title)).sendToDetail());
                    columns.add(bind(entity, SupervisorCustomer.DealerName, getString(R.string.dealer_name_label)).sendToDetail());



                    if (!isLandscape()) {
                        columns.add(bind(entity, SupervisorCustomer.Phone, "").setCustomViewHolder(new PhoneCustomerViewHoder()));
                    }
                }

                @Override
                protected ItemContextView<SupervisorCustomerModel> onCreateContextView() {
                    CustomerSummaryContextView contextView = new CustomerSummaryContextView(getAdapter(), getContext());
                    return contextView;
                }
            };
            adapter.create(new SupervisorCustomerModelRepository(), query, null);
            adapter.setItemClickDelay(2000);
            customersReportView.setAdapter(adapter);

            if (adapter.size() == 0)
                errorTextView.setVisibility(View.VISIBLE);
            else
                errorTextView.setVisibility(View.GONE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        List<VisitorModel> visitorModels = new VisitorManager(getContext()).getAll();
        VisitorModel visitorModel = new VisitorModel();
        visitorModel.Name = getString(R.string.all_visitors);
        visitorModels.add(0, visitorModel);
        visitorNameSpinner.setup(getChildFragmentManager(), visitorModels, new SearchBox.SearchMethod<VisitorModel>() {
            @Override
            public boolean onSearch(VisitorModel item, String text) {
                String searchKey = VasHelperMethods.persian2Arabic(text);
                return item.Name.contains(searchKey);
            }
        });
        final VisitorModel selectedVisitor = VisitorFilter.read(getContext());
        if (selectedVisitor.UniqueId == null)
            visitorNameSpinner.selectItem(0);
        else {
            int index = Linq.findFirstIndex(visitorModels, new Linq.Criteria<VisitorModel>() {
                @Override
                public boolean run(VisitorModel item) {
                    return item.UniqueId != null && item.UniqueId.equals(selectedVisitor.UniqueId);
                }
            });
            if (index >= 0)
                visitorNameSpinner.selectItem(index);
        }
        visitorNameSpinner.setOnItemSelectedListener(new PairedItemsSpinner.OnItemSelectedListener<VisitorModel>() {
            @Override
            public void onItemSelected(int position, VisitorModel item) {
                VisitorFilter.save(getContext(), item);
                refreshAdapter();
            }
        });

        ConditionModel conditionModels=new ConditionModel();


        List<String>conditionModelList=new ArrayList<>();
        conditionModels.name=getString(R.string.condition_customer);
        conditionModelList.add(0,getString(R.string.condition_customer));
        conditionModelList.add(1,getString(R.string.condition_unblock));
        conditionModelList.add(2,getString(R.string.condition_block));
        conditionCustomerSpinner.setup(getChildFragmentManager(), conditionModelList, new SearchBox.SearchMethod<String>() {
            @Override
            public boolean onSearch(String item, String text) {
                String searchKey = VasHelperMethods.persian2Arabic(text);
                return item.contains(searchKey);
            }
        });
        conditionCustomerSpinner.setOnItemSelectedListener(new PairedItemsSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(int position, String item) {
                VisitorFilter.saveconditionCustomer(getContext(), item);
                refreshAdapter();
            }
        });

        refreshAdapter();
    }
    private void showErrorDialog(String err) {
        Context context = getContext();
        if (context != null) {
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setTitle(R.string.error);
            dialog.setIcon(Icon.Error);
            dialog.setMessage(err);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }
    private void showError(String error) {
        Context context = getContext();
        if (isResumed() && context != null) {
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setTitle(R.string.error);
            dialog.setMessage(error);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }

    private void requsttoconditionCustomer(SupervisorCustomerModel entity){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("درحال ارسال اطلاعات");
        progressDialog.show();
        SupervisorApi api = new SupervisorApi(getContext());
        api.runWebRequest(api.getCustomerState(entity.UniqueId,message)
                , new WebCallBack<ResponseBody>() {
                    @Override
                    protected void onFinish() {

                    }

                    @Override
                    protected void onSuccess(ResponseBody result, Request request) {
                        Log.e("CustomerFragment", String.valueOf(result));
                        TourResat();
                        progressDialog.dismiss();
                    }

                    @Override
                    protected void onApiFailure(ApiError error, Request request) {
                        String err = WebApiErrorBody.log(error, getContext());
                        showErrorDialog(err);
                        progressDialog.dismiss();
                    }

                    @Override
                    protected void onNetworkFailure(Throwable t, Request request) {
                        Timber.e(t);
                        showErrorDialog(getContext().getString(R.string.error_connecting_to_server));
                        progressDialog.dismiss();
                    }
        });
    }
    private void TourResat(){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.downloading_data));
        progressDialog.show();
        DataManager.getData(getContext(), new DataManager.Callback() {
            @Override
            public void onSuccess() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    try {
                        progressDialog.dismiss();
                        refreshAdapter();
                    } catch (Exception ignored) {

                    }
                }
            }

            @Override
            public void onError(String error) {
                showError(error);
                if (progressDialog != null && progressDialog.isShowing()) {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception ignored) {

                    }
                }
            }
        });

    }
}