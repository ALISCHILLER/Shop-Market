package com.varanegar.vaslibrary.manager.questionnaire;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.questionnaire.FormAdapter;
import com.varanegar.framework.base.questionnaire.controls.FormControl;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.gson.VaranegarGsonBuilder;
import com.varanegar.framework.network.listeners.ApiException;
import com.varanegar.framework.network.listeners.InterruptedException;
import com.varanegar.framework.network.listeners.NetworkException;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.image.ImageManager;
import com.varanegar.vaslibrary.manager.image.ImageType;
import com.varanegar.vaslibrary.manager.picture.ImageViewModel;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireAnswer;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireAnswerModel;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireAnswerModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.imageapi.ImageApi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 11/1/2017.
 */

public class QuestionnaireAnswerManager extends BaseManager<QuestionnaireAnswerModel> {
    public QuestionnaireAnswerManager(@NonNull Context context) {
        super(context, new QuestionnaireAnswerModelRepository());
    }

    public List<QuestionnaireAnswerModel> getLines(UUID customerId, UUID questionnaireId) {
        Query query = new Query().from(QuestionnaireAnswer.QuestionnaireAnswerTbl)
                .whereAnd(Criteria.equals(QuestionnaireAnswer.CustomerId, customerId.toString())
                        .and(Criteria.equals(QuestionnaireAnswer.QuestionnaireId, questionnaireId.toString())));
        return getItems(query);
    }

    public void saveEPollAnswer(@NonNull UUID lineId, @NonNull UUID customerId, @NonNull UUID questionnaireId, EPollResultViewModel result) throws ValidationException, DbException {
        List<QuestionnaireAnswerModel> oldAnswers = getLines(customerId, questionnaireId);
        List<QuestionnaireAnswerModel> newAnswers = new ArrayList<>();
        QuestionnaireAnswerModel answerModel = getLine(oldAnswers, lineId);
        if (answerModel == null) {
            answerModel = new QuestionnaireAnswerModel();
            answerModel.QuestionnaireLineId = lineId;
            answerModel.CustomerId = customerId;
            answerModel.QuestionnaireId = questionnaireId;
            answerModel.UniqueId = UUID.randomUUID();
        }
        answerModel.Value = VaranegarGsonBuilder.build().create().toJson(result);
        newAnswers.add(answerModel);
        if (newAnswers.size() > 0)
            insertOrUpdate(newAnswers);
    }

    public void saveAnswers(@NonNull FormAdapter formAdapter, @NonNull UUID customerId, @NonNull UUID questionnaireId) throws ValidationException, DbException {
        List<QuestionnaireAnswerModel> oldAnswers = getLines(customerId, questionnaireId);
        List<QuestionnaireAnswerModel> newAnswers = new ArrayList<>();
        for (FormControl control :
                formAdapter.getControls()) {
            QuestionnaireAnswerModel answerModel = getLine(oldAnswers, control.uniqueId);
            if (answerModel == null) {
                answerModel = new QuestionnaireAnswerModel();
                answerModel.QuestionnaireLineId = control.uniqueId;
                answerModel.CustomerId = customerId;
                answerModel.QuestionnaireId = questionnaireId;
                answerModel.UniqueId = UUID.randomUUID();
            }
            answerModel.Value = control.serializeValue();
            if (answerModel.AttachmentId != control.AttachmentId) {
                if (answerModel.AttachmentId != null) {
                    try {
                        File file = getContext().getFileStreamPath(answerModel.AttachmentId.toString());
                        file.delete();
                    } catch (Exception ex) {
                        Timber.e(ex);
                    }
                }
                answerModel.AttachmentId = control.AttachmentId;
            }
            newAnswers.add(answerModel);
        }
        if (newAnswers.size() > 0)
            insertOrUpdate(newAnswers);
    }

    @Nullable
    public QuestionnaireAnswerModel getLine(@NonNull List<QuestionnaireAnswerModel> answers,
                                            @NonNull final UUID questionnaireLineId) {
        QuestionnaireAnswerModel answerModel = Linq.findFirst(answers, new Linq.Criteria<QuestionnaireAnswerModel>() {
            @Override
            public boolean run(QuestionnaireAnswerModel item) {
                return item.QuestionnaireLineId.equals(questionnaireLineId);
            }
        });
        return answerModel;
    }

    public void deleteAnswers(UUID customerId) throws DbException, ValidationException {
        delete(Criteria.equals(QuestionnaireAnswer.CustomerId, customerId.toString()));
        new QuestionnaireCustomerManager(getContext()).resetQuestionnaire(customerId);
    }

    public void deleteAnswers(UUID customerId, UUID questionnaireId) throws DbException {
        delete(Criteria.equals(QuestionnaireAnswer.CustomerId, customerId.toString())
                .and(Criteria.equals(QuestionnaireAnswer.QuestionnaireId, questionnaireId.toString())));
    }

    private void uploadPictures(List<QuestionnaireAnswerModel> answers, final UpdateCall call) {
        ImageManager imageManager = new ImageManager(getContext());
        final List<ImageViewModel> imageViewModels = new ArrayList<>();
        if (answers.size() > 0) {
            for (QuestionnaireAnswerModel answer :
                    answers) {
                CustomerCallManager callManager = new CustomerCallManager(getContext());
                List<CustomerCallModel> calls = callManager.loadCalls(answer.CustomerId);
                if (!callManager.isDataSent(calls, null)) {
                    ImageViewModel imageViewModel = new ImageViewModel();
                    imageViewModel.imageId = answer.AttachmentId.toString();
                    imageViewModel.token = answer.AttachmentId.toString();
                    imageViewModel.imageType = ImageType.QuestionnaireAttachments;
                    imageViewModel.isDefault = false;
                    String filePath = imageManager.getImagePath(answer.CustomerId, answer.AttachmentId + ".jpg", ImageType.QuestionnaireAttachments);
                    File file = new File(filePath);
                    if (file.exists()) {
                        imageViewModel.file = file;
                        imageViewModels.add(imageViewModel);
                    }
                }
            }
            if (imageViewModels.size() > 0) {
                final Handler handler = new Handler();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int sentItems = 0;
                        ImageApi imageApi = new ImageApi(getContext());
                        for (ImageViewModel imageViewModel :
                                imageViewModels) {
                            try {
                                imageApi.runWebRequest(imageApi.postImage(imageViewModel));
                                Timber.d("image " + imageViewModel.imageId + " was sent");
                                imageViewModel.file.delete();
                                sentItems++;
                                if (sentItems == imageViewModels.size()) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            call.success();
                                        }
                                    });
                                }
                            } catch (ApiException e1) {
                                Context context = getContext();
                                if (context != null) {
                                    final String err = WebApiErrorBody.log(e1.getApiError(), context);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            call.failure(err);
                                        }
                                    });
                                }
                                return;
                            } catch (NetworkException e1) {
                                Timber.e(e1);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Context context = getContext();
                                        if (context != null)
                                            call.failure(context.getString(R.string.connection_to_server_failed));
                                    }
                                });
                                return;
                            } catch (InterruptedException e) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        call.failure(getContext().getString(R.string.sending_images_canceled));
                                    }
                                });
                                return;
                            }
                        }
                    }
                }).start();
            } else {
                call.success();
            }
        } else {
            call.success();
        }

    }

    public void uploadCustomerPictures(final UpdateCall call) {
        List<QuestionnaireAnswerModel> answers = getAllAttachments();
        uploadPictures(answers, call);
    }

    public void uploadCustomerPictures(UUID customerId, final UpdateCall call) {
        List<QuestionnaireAnswerModel> answers = getAllAttachments(customerId);
        uploadPictures(answers, call);
    }

    public void uploadCustomerPictures(List<UUID> customerIds, final UpdateCall call) {
        List<QuestionnaireAnswerModel> answers = getAllAttachments(customerIds);
        uploadPictures(answers, call);
    }

    public List<QuestionnaireAnswerModel> getAllAttachments() {
        Query query = new Query();
        query.from(QuestionnaireAnswer.QuestionnaireAnswerTbl)
                .whereAnd(Criteria.notIsNull(QuestionnaireAnswer.AttachmentId));
        return getItems(query);
    }

    public List<QuestionnaireAnswerModel> getAllAttachments(UUID customerId) {
        Query query = new Query();
        query.from(QuestionnaireAnswer.QuestionnaireAnswerTbl)
                .whereAnd(Criteria.notIsNull(QuestionnaireAnswer.AttachmentId)
                        .and(Criteria.equals(QuestionnaireAnswer.CustomerId, customerId.toString())));
        return getItems(query);
    }

    public List<QuestionnaireAnswerModel> getAllAttachments(List<UUID> customerIds) {
        Query query = new Query();
        query.from(QuestionnaireAnswer.QuestionnaireAnswerTbl)
                .whereAnd(Criteria.notIsNull(QuestionnaireAnswer.AttachmentId)
                        .and(Criteria.in(QuestionnaireAnswer.CustomerId, customerIds)));
        return getItems(query);
    }

}
