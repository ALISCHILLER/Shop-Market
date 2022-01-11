package com.varanegar.supervisor.customers;

import android.Manifest;
import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.PairedItemsSpinner;
import com.varanegar.framework.util.component.SearchBox;
import com.varanegar.framework.util.recycler.ItemContextView;
import com.varanegar.framework.util.report.CustomViewHolder;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.supervisor.IMainPageFragment;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.VisitorFilter;
import com.varanegar.vaslibrary.manager.questionnaire.QuestionnaireCustomerViewManager;
import com.varanegar.vaslibrary.manager.questionnaire.QuestionnaireManager;
import com.varanegar.vaslibrary.model.customer.SupervisorCustomer;
import com.varanegar.vaslibrary.model.customer.SupervisorCustomerModel;
import com.varanegar.supervisor.model.VisitorManager;
import com.varanegar.supervisor.model.VisitorModel;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.model.customer.SupervisorCustomerModelRepository;
import com.varanegar.vaslibrary.model.customer.SupervisorFullCustomer;
import com.varanegar.vaslibrary.model.customer.SupervisorFullCustomerModel;
import com.varanegar.vaslibrary.model.customer.SupervisorFullCustomerModelRepository;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireCustomerViewModel;
import com.varanegar.vaslibrary.ui.fragment.QuestionnaireFragment;

import java.util.List;

import timber.log.Timber;

public class CustomersFullFragment  extends IMainPageFragment {
    private static final int REQUEST_PHONE_CALL = 90;
    private ReportView customersReportView;
    private EditText searchEditText;
    private long last_text_edit;
    Handler handler = new Handler();
    private long delay = 200;
    private String keyWord;
    private ImageView clearSearchImageView;
    private SimpleReportAdapter<SupervisorFullCustomerModel> adapter;
    private TextView errorTextView;
    private PairedItemsSpinner<VisitorModel> visitorNameSpinner;

    @Override
    protected View onCreateContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supervisor_customer_list_layout, container, false);
        visitorNameSpinner = view.findViewById(R.id.visitor_name_paired_items);
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
        Query query = new Query().from(SupervisorFullCustomer.SupervisorFullCustomerTbl);
        if (keyWord != null && !keyWord.isEmpty()) {
            String key = VasHelperMethods.convertToEnglishNumbers(keyWord);
            key = VasHelperMethods.persian2Arabic(key);
            final String finalKey = key;
            query = new Query().from(SupervisorFullCustomer.SupervisorFullCustomerTbl)
                    .whereAnd(Criteria.contains(SupervisorFullCustomer.CustomerCode, finalKey)
                    .or(Criteria.contains(SupervisorFullCustomer.CustomerName, finalKey))
                    .or(Criteria.contains(SupervisorFullCustomer.StoreName, finalKey)));
        }
        VisitorModel visitorModel = VisitorFilter.read(getContext());
        if (visitorModel.UniqueId != null)
            query = query.whereAnd(Criteria.equals(SupervisorFullCustomer.dealerUniqueId, visitorModel.UniqueId));

        if (errorTextView != null) {
            adapter = new SimpleReportAdapter<SupervisorFullCustomerModel>(this, SupervisorFullCustomerModel.class) {
                class PhoneCustomerViewHoder extends CustomViewHolder<SupervisorFullCustomerModel> {

                    @Override
                    public void onBind(View view, final SupervisorFullCustomerModel entity) {
                        ImageView callImageView = null;

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

                                    questionnaireManager.calculateCustomerQuestionnaire(entity.customerUniqueId);

                                    QuestionnaireCustomerViewManager questionnaireCustomerViewManager =
                                            new QuestionnaireCustomerViewManager(getContext());

                                    List<QuestionnaireCustomerViewModel> questionnaireCustomerViewModels
                                            = questionnaireCustomerViewManager.getQuestionnaires(entity.UniqueId);

                                    if (questionnaireCustomerViewModels.size() == 0) {

                                        getVaranegarActvity().
                                                showSnackBar(com.varanegar.vaslibrary.R.string.no_questionnaire_for_this_customer,
                                                MainVaranegarActivity.Duration.Short);

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
                public void bind(ReportColumns columns, SupervisorFullCustomerModel entity) {
                    bindRowNumber(columns);
                    columns.add(bind(entity, SupervisorFullCustomer.CustomerCode, getString(R.string.customer_code)).setFrizzed());
                    columns.add(bind(entity, SupervisorFullCustomer.CustomerName, getString(R.string.customer_name)));
                    columns.add(bind(entity, SupervisorFullCustomer.StoreName, getString(R.string.store_name)));
                    if (isLandscape()) {
                        columns.add(bind(entity, SupervisorFullCustomer.Phone, "").setCustomViewHolder(new PhoneCustomerViewHoder()).setWeight(1.7f));
                    }
                    columns.add(bind(entity, SupervisorFullCustomer.Phone, getString(R.string.phone_number)));
                    columns.add(bind(entity, SupervisorFullCustomer.Mobile, getString(R.string.mobile_label)));
                    columns.add(bind(entity, SupervisorFullCustomer.Address, getString(R.string.address)).setWeight(3));

                    columns.add(bind(entity, SupervisorFullCustomer.NationalCode, getString(R.string.national_id_label)).sendToDetail());
                    columns.add(bind(entity, SupervisorFullCustomer.CustomerActivity, getString(R.string.customer_activity)).sendToDetail());
                    columns.add(bind(entity, SupervisorFullCustomer.CustomerLevel, getString(R.string.customer_level)).sendToDetail());
                    columns.add(bind(entity, SupervisorFullCustomer.customerCategoryUniqueId, getString(R.string.customer_category)).sendToDetail());
//                    columns.add(bind(entity, SupervisorFullCustomer.PathTitle, getString(R.string.path_title)).sendToDetail());
                    columns.add(bind(entity, SupervisorFullCustomer.DealerName, getString(R.string.dealer_name_label)).sendToDetail());
                    if (!isLandscape()) {
                        columns.add(bind(entity, SupervisorFullCustomer.Phone, "").setCustomViewHolder(new PhoneCustomerViewHoder()));
                    }
                }

                @Override
                protected ItemContextView<SupervisorFullCustomerModel> onCreateContextView() {
//                    CustomerSummaryContextView contextView = new CustomerSummaryContextView(getAdapter(), getContext());
//                    return contextView;
                    return null;
                }
            };
            adapter.create(new SupervisorFullCustomerModelRepository(), query, null);
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
        refreshAdapter();
    }

}