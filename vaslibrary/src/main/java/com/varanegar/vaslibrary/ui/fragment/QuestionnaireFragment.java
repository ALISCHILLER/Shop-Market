package com.varanegar.vaslibrary.ui.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.questionnaire.QuestionnaireAnswerManager;
import com.varanegar.vaslibrary.manager.questionnaire.QuestionnaireCustomerManager;
import com.varanegar.vaslibrary.manager.questionnaire.QuestionnaireCustomerViewManager;
import com.varanegar.vaslibrary.manager.questionnaire.QuestionnaireLineManager;
import com.varanegar.vaslibrary.model.customer.SupervisorFullCustomer;
import com.varanegar.vaslibrary.model.customer.SupervisorFullCustomerModel;
import com.varanegar.vaslibrary.model.customer.SupervisorFullCustomerModelRepository;
import com.varanegar.vaslibrary.model.customercall.CustomerCallType;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireAnswerModel;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireCustomerModel;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireCustomerViewModel;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireDemandType;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireLineModel;
import com.varanegar.vaslibrary.model.sendAnswersQustion.SyncCustomerCallQuestionnaire;
import com.varanegar.vaslibrary.model.sendAnswersQustion.SyncGetCustomerCallModel;
import com.varanegar.vaslibrary.model.sendAnswersQustion.SyncGetCustomerQuestionnaireAnswerModel;
import com.varanegar.vaslibrary.model.sendAnswersQustion.SyncGetTourModel;
import com.varanegar.vaslibrary.ui.report.report_new.webApi.ReportApi;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerCallQuestionnaireAnswerViewModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerCallQuestionnaireViewModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerCallViewModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetTourViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Torabi on 7/3/2017.
 */

public class QuestionnaireFragment extends VisitFragment {
    private Boolean back_prees=false;
    private boolean cheack=false;
    private UUID customerId;
    private String _customer;
    private ProgressDialog progressDialog;
    private  SharedPreferences sharedconditionCustomer;
    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception ignored) {

            }
        }
    }

    public void setCustomerId(UUID customerId) {
        addArgument("f8c2abc4-c401-4f16-8f7d-1019e80574af", customerId.toString());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_quetionaire, container, false);

        sharedconditionCustomer = getVaranegarActvity().getSharedPreferences("QuestionCustomer", Context.MODE_PRIVATE);

        SimpleToolbar toolbar = (SimpleToolbar) view.findViewById(R.id.toolbar);
        toolbar.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VaranegarApplication.is(VaranegarApplication.AppId.Supervisor)){
                    cheack = sharedconditionCustomer.getBoolean(_customer, false);
                    if (!cheack) {
                        if (back_prees) {
                            Toast.makeText(getContext(), "لطفاپرسشنامه را ارسال کنید", Toast.LENGTH_SHORT).show();
                        } else {
                            getVaranegarActvity().popFragment();
                        }

                    } else {
                        getVaranegarActvity().popFragment();
                    }
                }else{
                    getVaranegarActvity().popFragment();
                }
            }
        });
        BaseRecyclerView formsRecyclerView = (BaseRecyclerView) view.findViewById(R.id.forms_recycler_view);
        customerId = UUID.fromString(getArguments().getString("f8c2abc4-c401-4f16-8f7d-1019e80574af"));
        _customer =getArguments().getString("f8c2abc4-c401-4f16-8f7d-1019e80574af");
        final CustomerCallManager callManager = new CustomerCallManager(getContext());
        Button sendButton = view.findViewById(R.id.send_questionnaire_btn);
        try {
            if (VaranegarApplication.is(VaranegarApplication.AppId.Supervisor)) {
                sendButton.setVisibility(View.VISIBLE);
                sendButton.setOnClickListener(view1 -> {
                    sendQuestion();
                });
            } else {
                sendButton.setVisibility(View.GONE);
                callManager.unConfirmAllCalls(customerId);
                callManager.removeCall(CustomerCallType.LackOfVisit, customerId);
            }
            QuestionnaireCustomerViewManager manager = new QuestionnaireCustomerViewManager(getContext());
            List<QuestionnaireCustomerViewModel> questionnaires = manager.getQuestionnaires(customerId);
            final BaseRecyclerAdapter<QuestionnaireCustomerViewModel> adapter = new BaseRecyclerAdapter<QuestionnaireCustomerViewModel>(getVaranegarActvity(), questionnaires) {
                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_questionaire_summary, parent, false);
                    return new QuestionnaireViewHolder(view, this, getContext());
                }
            };
            formsRecyclerView.setAdapter(adapter);
            return view;
        } catch (Exception ex) {
            Timber.e(ex);
            return null;
        }


    }

    @Override
    public void onBackPressed() {
        cheack = sharedconditionCustomer.getBoolean(_customer, false);
        if (VaranegarApplication.is(VaranegarApplication.AppId.Supervisor)) {
            if (!cheack) {
                if (back_prees) {
                    Toast.makeText(getContext(), "لطفاپرسشنامه را ارسال کنید", Toast.LENGTH_SHORT).show();
                } else {
                    super.onBackPressed();
                }
            } else {
                super.onBackPressed();
            }
        }else {
            super.onBackPressed();
        }


    }

    public void sendQuestion() {
        if (getContext() == null)
            return;

        Date date = new Date();
        SharedPreferences sharedPreferences =
                getContext().getSharedPreferences("SupervisorId",
                        Context.MODE_PRIVATE);
        UUID tourId = UUID.fromString(
                sharedPreferences.getString(
                        "SupervisorIduniqueId",
                        null));
        String endData = DateHelper.toString(date, DateFormat.Date, Locale.getDefault());
        SyncGetTourModel syncGetTourModel = new SyncGetTourModel();
        syncGetTourModel.setTourUniqueId(tourId);
        syncGetTourModel.setSendDate(date);
        try {
            syncGetTourModel.setApkVersion(
                    getContext()
                            .getPackageManager()
                            .getPackageInfo(getContext()
                                            .getApplicationInfo()
                                            .packageName,
                                    0)
                            .versionName
            );
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e);
            e.printStackTrace();
        }
        cheack = sharedconditionCustomer.getBoolean(_customer, false);

        SyncGetCustomerCallModel syncGetCustomerCallViewModel = new SyncGetCustomerCallModel();
        syncGetCustomerCallViewModel.setCustomerUniqueId(customerId);
        syncGetCustomerCallViewModel.setCallDate(date);
        syncGetCustomerCallViewModel.setCallPDate(endData);
        syncGetCustomerCallViewModel.setStartTime(date);
        syncGetCustomerCallViewModel.setStartPTime(endData);
        syncGetCustomerCallViewModel.setEndTime(date);
        syncGetCustomerCallViewModel.setEndPTime(endData);
        syncGetCustomerCallViewModel.setLatitude(51.48330420255661);
        syncGetCustomerCallViewModel.setLongitude(51.48330420255661);
        syncGetCustomerCallViewModel.setReceiveDate(date);
        syncGetCustomerCallViewModel.setReceivePDate(endData);
        syncGetCustomerCallViewModel.setVisitDuration(245000);
        syncGetCustomerCallViewModel.setCustomerCallQuestionnaires(
                (ArrayList<SyncCustomerCallQuestionnaire>) populateCustomerQuestionnaires(customerId)
        );
        List<SyncGetCustomerCallModel> calls = new ArrayList<>();
        calls.add(syncGetCustomerCallViewModel);
        syncGetTourModel.setCustomerCalls(calls);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(R.string.please_wait);
        progressDialog.setMessage(getContext().getString(R.string.downloading_data));
        progressDialog.show();
        ReportApi reportApi = new ReportApi(getContext());

        if (!cheack) {
            reportApi.runWebRequest(reportApi.savetourdata(syncGetTourModel), new WebCallBack<Void>() {
                @Override
                protected void onFinish() {
                    dismissProgressDialog();
                }

                @Override
                protected void onSuccess(Void result, Request request) {
                    back_prees = false;
                    sharedconditionCustomer.edit().putBoolean(_customer, true).apply();
                }

                @Override
                protected void onApiFailure(ApiError error, Request request) {
                    if (isResumed()) {
                        Activity activity = getActivity();
                        if (activity != null && !activity.isFinishing()) {

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
                            Timber.e(t);
                            showErrorDialog(getContext().getString(R.string.error_connecting_to_server));
                            dismissProgressDialog();
                        }
                    }
                }
            });
        }else {
            showErrorDialog("پرسش نامه قبلا ارسال شده است");
        }
    }

    @NonNull
    @Override
    protected UUID getCustomerId() {
        return customerId;
    }

    private class QuestionnaireViewHolder extends BaseViewHolder<QuestionnaireCustomerViewModel> {

        private final TextView title;
        private final ImageView doneImageView;
        private final ImageView cancelImageView;
        private final ImageView forceImageView;

        public QuestionnaireViewHolder(View itemView, BaseRecyclerAdapter<QuestionnaireCustomerViewModel> recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
            title = (TextView) itemView.findViewById(R.id.questionnaire_title_text_view);
            doneImageView = (ImageView) itemView.findViewById(R.id.done_image_view);
            cancelImageView = (ImageView) itemView.findViewById(R.id.cancel_image_view);
            forceImageView = (ImageView) itemView.findViewById(R.id.force_image_view);

            if (VaranegarApplication.is(VaranegarApplication.AppId.Supervisor)){
                cancelImageView.setVisibility(View.GONE);
            }
        }


        @Override
        public void bindView(final int position) {
            final QuestionnaireCustomerViewModel q = recyclerAdapter.get(position);
            title.setText(q.Title);
            if (q.HasAnswer) {
                doneImageView.setImageResource(R.drawable.ic_done_green_900_24dp);
                back_prees = true;
            }else
                doneImageView.setImageResource(R.drawable.ic_done_blue_grey_300_24dp);
            if (q.DemandType == QuestionnaireDemandType.Mandatory)
                forceImageView.setVisibility(View.VISIBLE);
            else
                forceImageView.setVisibility(View.INVISIBLE);
            if (q.NoAnswerReason == null)
                cancelImageView.setImageResource(R.drawable.ic_clear_blue_grey_300_24dp);
            else
                cancelImageView.setImageResource(R.drawable.ic_clear_red_900_24dp);
            doneImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!cheack) {
                        QuestionnaireCustomerManager questionnaireCustomerManager = new QuestionnaireCustomerManager(getContext());
                        QuestionnaireCustomerModel item = questionnaireCustomerManager.getCustomerQuestionnaire(q.CustomerId, q.QuestionnaireId);
                        item.NoAnswerReason = null;
                        try {
                            questionnaireCustomerManager.insertOrUpdate(item);

                            List<QuestionnaireLineModel> lines = new QuestionnaireLineManager(getContext()).getLines(item.QuestionnaireId);
                            QuestionnaireLineModel epoll = Linq.findFirst(lines, new Linq.Criteria<QuestionnaireLineModel>() {
                                @Override
                                public boolean run(QuestionnaireLineModel item) {
                                    return item.QuestionnaireLineTypeUniqueId.equals(QuestionnaireLineManager.QuestionnaireLineTypeUniqueId.EPoll);
                                }
                            });
                            if (epoll == null) {
                                QuestionnaireFormFragment fragment = new QuestionnaireFormFragment();
                                fragment.setArguments(q.CustomerId, q.QuestionnaireId);
                                getVaranegarActvity().pushFragment(fragment);
                            } else {
                                if (VaranegarApplication.is(VaranegarApplication.AppId.Supervisor)) {
                                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                                    dialog.setIcon(Icon.Info);
                                    dialog.setMessage(R.string.questionnaire_not_supported_in_supervisor);
                                    dialog.setPositiveButton(R.string.ok, null);
                                    dialog.show();
                                } else {
                                    EPollFragment fragment = new EPollFragment();
                                    fragment.setArguments(q.CustomerId, q.QuestionnaireId, epoll.UniqueId);
                                    getVaranegarActvity().pushFragment(fragment);
                                }
                            }
                        } catch (Exception ex) {
                            Timber.e(ex);
                            getVaranegarActvity().showSnackBar(R.string.error_saving_request, MainVaranegarActivity.Duration.Short);
                        }
                    }else {
                        getVaranegarActvity().showSnackBar("پرسشنامه ارسال شده است قابلیت ویراش موجود نیست", MainVaranegarActivity.Duration.Short);
                    }
                }
            });
            cancelImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    QuestionnaireNoAnswerDialog dialog = new QuestionnaireNoAnswerDialog();
                    dialog.onReasonSelected = new QuestionnaireNoAnswerDialog.OnReasonSelected() {
                        @Override
                        public void onDone() {
                            QuestionnaireAnswerManager questionnaireAnswerManager = new QuestionnaireAnswerManager(getContext());
                            try {
                                questionnaireAnswerManager.deleteAnswers(q.CustomerId, q.QuestionnaireId);
                                QuestionnaireCustomerViewManager manager = new QuestionnaireCustomerViewManager(getContext());
                                QuestionnaireCustomerViewModel updatedModel = manager.getCustomerQuestionnaire(q.CustomerId, q.QuestionnaireId);
                                q.NoAnswerReason = updatedModel.NoAnswerReason;
                                q.HasAnswer = updatedModel.HasAnswer;
                                recyclerAdapter.notifyDataSetChanged();
                                if (!VaranegarApplication.is(VaranegarApplication.AppId.Supervisor)) {
                                    List<QuestionnaireCustomerViewModel> models = manager.getQuestionnaires(q.CustomerId, false);
                                    if (models.size() == 0) {
                                        saveCall(q.CustomerId);
                                    } else {
                                        boolean exist = Linq.exists(models, new Linq.Criteria<QuestionnaireCustomerViewModel>() {
                                            @Override
                                            public boolean run(QuestionnaireCustomerViewModel item) {
                                                return item.DemandType == QuestionnaireDemandType.Mandatory && item.NoAnswerReason == null;
                                            }
                                        });
                                        if (exist) {
                                            removeCall(q.CustomerId);
                                        } else {
                                            saveCall(q.CustomerId);
                                        }
                                    }
                                }
                            } catch (Exception ex) {
                                Timber.e(ex);
                            }
                        }
                    };
                    dialog.setArguments(q.CustomerId, q.QuestionnaireId);
                    dialog.show(getChildFragmentManager(), "4de9d98d-09c5-4a1a-b8d3-963e67ae9ccc");
                }
            });
        }
    }

    private void removeCall(UUID customerId) {
        CustomerCallManager callManager = new CustomerCallManager(getContext());
        try {
            callManager.removeQuestionnaireCall(customerId);
        } catch (DbException e) {
            Timber.e(e);
        }
    }

    private void saveCall(UUID customerId) {
        CustomerCallManager callManager = new CustomerCallManager(getContext());
        try {
            callManager.saveQuestionnaireCall(customerId);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private List<SyncCustomerCallQuestionnaire> populateCustomerQuestionnaires(UUID customerId) {
        List<SyncCustomerCallQuestionnaire> syncGetCustomerCallQuestionnaireViewModels = new ArrayList<>();
        QuestionnaireCustomerViewManager questionnaireCustomerViewManager = new QuestionnaireCustomerViewManager(getContext());
        QuestionnaireAnswerManager questionnaireAnswerManager = new QuestionnaireAnswerManager(getContext());
        QuestionnaireLineManager questionnaireLineManager = new QuestionnaireLineManager(getContext());

        List<QuestionnaireCustomerViewModel> answeredQuestionnaires = questionnaireCustomerViewManager.getQuestionnaires(customerId, true);
        for (QuestionnaireCustomerViewModel questionnaireCustomerViewModel :
                answeredQuestionnaires) {
            SyncCustomerCallQuestionnaire syncCustomerCallQuestionnaire = new SyncCustomerCallQuestionnaire();
            syncCustomerCallQuestionnaire.questionnaireUniqueId = questionnaireCustomerViewModel.QuestionnaireId;
            List<QuestionnaireAnswerModel> questionnaireAnswerModels = questionnaireAnswerManager.getLines(customerId, questionnaireCustomerViewModel.QuestionnaireId);
            List<QuestionnaireLineModel> lines = questionnaireLineManager.getLines(questionnaireCustomerViewModel.QuestionnaireId);
            for (QuestionnaireLineModel line :
                    lines) {
                QuestionnaireAnswerModel answerModel = questionnaireAnswerManager.getLine(questionnaireAnswerModels, line.UniqueId);
                SyncGetCustomerQuestionnaireAnswerModel answer = new SyncGetCustomerQuestionnaireAnswerModel();
                answer.hasAttachments = answerModel.AttachmentId != null;
                answer.questionnaireLineUniqueId = line.UniqueId;
                answer.uniqueId = answerModel.AttachmentId == null ? UUID.randomUUID() : answerModel.AttachmentId;
                answer.customerCallQuestionnaireUniqueId = questionnaireCustomerViewModel.QuestionnaireId;
                answer.answer = questionnaireLineManager.serialize(line, answerModel);
                answer.options = questionnaireLineManager.serializeOptions(line, answerModel);
                syncCustomerCallQuestionnaire.answers.add(answer);
            }
            syncGetCustomerCallQuestionnaireViewModels.add(syncCustomerCallQuestionnaire);
        }
        return syncGetCustomerCallQuestionnaireViewModels;
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
}
