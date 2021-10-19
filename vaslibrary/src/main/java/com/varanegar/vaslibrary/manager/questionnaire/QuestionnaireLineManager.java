package com.varanegar.vaslibrary.manager.questionnaire;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.questionnaire.controls.BooleanControl;
import com.varanegar.framework.base.questionnaire.controls.FormControl;
import com.varanegar.framework.base.questionnaire.controls.NumberControl;
import com.varanegar.framework.base.questionnaire.controls.TextControl;
import com.varanegar.framework.base.questionnaire.controls.optionscontrol.OptionControl;
import com.varanegar.framework.base.questionnaire.controls.optionscontrol.RadioControl;
import com.varanegar.framework.base.questionnaire.controls.optionscontrol.SelectControl;
import com.varanegar.framework.base.questionnaire.controls.prioritycontrol.PriorityControl;
import com.varanegar.framework.base.questionnaire.controls.prioritycontrol.PriorityOption;
import com.varanegar.framework.base.questionnaire.validator.OptionAttachmentValidator;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.util.Linq;
import com.varanegar.vaslibrary.manager.image.ImageManager;
import com.varanegar.vaslibrary.manager.image.ImageType;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireAnswerModel;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireLine;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireLineModel;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireLineModelRepository;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireLineOptionModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 10/21/2017.
 */

public class QuestionnaireLineManager extends BaseManager<QuestionnaireLineModel> {
    public static class QuestionnaireLineTypeUniqueId {
        public static final UUID YesNO = UUID.fromString("C50E74EF-3CCA-42ED-ACAD-867965358287");
        public static final UUID Text = UUID.fromString("66E7EFF8-FD82-4A1D-9054-637AC1CB811A");
        public static final UUID Radio = UUID.fromString("75D0045A-1287-4C49-9550-B6308E5AC1AA");
        public static final UUID Select = UUID.fromString("E5435961-A64C-4075-9509-2DE55DF29154");
        public static final UUID Number = UUID.fromString("718BC877-6085-4326-8D23-DF09C46365E1");
        public static final UUID Priority = UUID.fromString("d379f9a5-741c-4ab1-b2e3-f4534bfecb2e");
        public static final UUID EPoll = UUID.fromString("b61c476b-a97e-43b6-9746-2b029e75d786");
    }

    public static class AttachmentTypeUniqueId {
        public static final UUID Mandatory = UUID.fromString("C35DAC36-986C-4A53-826C-6996D60C5348");
        public static final UUID Optional = UUID.fromString("326AC173-18C2-4D17-9600-B9DEB3FC5FA5");
    }

    public QuestionnaireLineManager(@NonNull Context context) {
        super(context, new QuestionnaireLineModelRepository());
    }

    public List<QuestionnaireLineModel> getLines(UUID questionnaireId) {
        return getItems(
                new Query().from(QuestionnaireLine.QuestionnaireLineTbl)
                        .whereAnd(Criteria.equals(QuestionnaireLine.QuestionnaireUniqueId, questionnaireId.toString()))
                        .orderByAscending(QuestionnaireLine.RowIndex)
        );
    }

    public QuestionnaireLineModel getLine(UUID lineId) {
        return getItem(
                new Query().from(QuestionnaireLine.QuestionnaireLineTbl)
                        .whereAnd(Criteria.equals(QuestionnaireLine.UniqueId, lineId.toString()))
        );
    }

    public List<FormControl> generateFormControls(UUID questionnaireId, final UUID customerId) {
        QuestionnaireAnswerManager questionnaireAnswerManager = new QuestionnaireAnswerManager(getContext());
        List<QuestionnaireAnswerModel> answers = questionnaireAnswerManager.getLines(customerId, questionnaireId);
        List<FormControl> controls = new ArrayList<>();
        List<QuestionnaireLineModel> lines = getLines(questionnaireId);
        if (lines.size() > 0) {
            for (final QuestionnaireLineModel line :
                    lines) {
                FormControl control = null;
                if (line.QuestionnaireLineTypeUniqueId.equals(QuestionnaireLineTypeUniqueId.Text)) {
                    control = new TextControl(getContext(), line.Title, line.UniqueId);
                } else if (line.QuestionnaireLineTypeUniqueId.equals(QuestionnaireLineTypeUniqueId.Number)) {
                    control = new NumberControl(getContext(), line.Title, line.UniqueId);
                } else if (line.QuestionnaireLineTypeUniqueId.equals(QuestionnaireLineTypeUniqueId.Radio)) {
                    QuestionnaireLineOptionManager manager = new QuestionnaireLineOptionManager(getContext());
                    List<OptionControl> options = manager.getQuestionnaireLineOptionControls(line.UniqueId);
                    control = new RadioControl(getContext(), options, line.Title, line.UniqueId);
                } else if (line.QuestionnaireLineTypeUniqueId.equals(QuestionnaireLineTypeUniqueId.Select)) {
                    QuestionnaireLineOptionManager manager = new QuestionnaireLineOptionManager(getContext());
                    List<OptionControl> options = manager.getQuestionnaireLineOptionControls(line.UniqueId);
                    control = new SelectControl(getContext(), options, line.Title, line.UniqueId);
                } else if (line.QuestionnaireLineTypeUniqueId.equals(QuestionnaireLineTypeUniqueId.YesNO)) {
                    control = new BooleanControl(getContext(), line.Title, line.UniqueId);
                } else if (line.QuestionnaireLineTypeUniqueId.equals(QuestionnaireLineTypeUniqueId.Priority)) {
                    QuestionnaireLineOptionManager manager = new QuestionnaireLineOptionManager(getContext());
                    List<PriorityOption> options = manager.getQuestionnaireLinePriorityControls(line.UniqueId);
                    control = new PriorityControl(getContext(), options, line.Title, line.UniqueId);
                }

                if (control != null) {
                    if (line.Validators != null && !line.Validators.isEmpty())
                        control.deserializeValidators(line.Validators);
                    QuestionnaireAnswerModel answer = questionnaireAnswerManager.getLine(answers, line.UniqueId);
                    if (line.HasAttachment) {
                        boolean isMandatory = line.AttachmentTypeUniqueId.equals(AttachmentTypeUniqueId.Mandatory);
                        control.hasAttachment(line.NumberOfOptions, isMandatory);
                    }
                    if (line.QuestionnaireLineTypeUniqueId.equals(QuestionnaireLineTypeUniqueId.Radio) ||
                            line.QuestionnaireLineTypeUniqueId.equals(QuestionnaireLineTypeUniqueId.Select)) {
                        QuestionnaireLineOptionManager manager = new QuestionnaireLineOptionManager(getContext());
                        List<QuestionnaireLineOptionModel> options = manager.getQuestionnaireLineOptions(line.UniqueId);
                        OptionAttachmentValidator optionAttachmentValidator = new OptionAttachmentValidator();
                        optionAttachmentValidator.MandatoryOptions = new ArrayList<>();
                        optionAttachmentValidator.MandatoryOptions = new ArrayList<>();
                        for (QuestionnaireLineOptionModel option :
                                options) {
                            if (AttachmentTypeUniqueId.Mandatory.equals(option.AnswerAttachmentUniqueId))
                                optionAttachmentValidator.MandatoryOptions.add(option.UniqueId);
                        }
                        control.addValidator(optionAttachmentValidator);
                    }
                    if (answer != null) {
                        control.deserializeValue(answer.Value);
                        control.AttachmentId = answer.AttachmentId;
                        control.AttachmentPath = new ImageManager(getContext()).getImagePath(customerId, answer.AttachmentId + ".jpg", ImageType.QuestionnaireAttachments);
                    }
                    control.setCustomImageAttachment(new FormControl.CustomImageAttachment() {

                        @Override
                        public String saveImage(Bitmap bitmap, UUID attachmentId) throws Exception {
                            ImageManager imageManager = new ImageManager(getContext());
                            return imageManager.saveImage(bitmap, 30, customerId, ImageType.QuestionnaireAttachments, attachmentId + ".jpg", true);
                        }

                        @Override
                        public void deleteImage(UUID attachmentId) throws Exception {
                            ImageManager imageManager = new ImageManager(getContext());
                            imageManager.deleteImage(customerId, attachmentId + ".jpg", ImageType.QuestionnaireAttachments);
                        }
                    });
                    controls.add(control);
                }
            }
        }
        return controls;
    }

    public String serialize(@NonNull QuestionnaireLineModel line, @Nullable QuestionnaireAnswerModel answer) {
        if (answer == null)
            return null;
        if (line.QuestionnaireLineTypeUniqueId.equals(QuestionnaireLineManager.QuestionnaireLineTypeUniqueId.Radio)
                || line.QuestionnaireLineTypeUniqueId.equals(QuestionnaireLineTypeUniqueId.Select)) {
            QuestionnaireLineOptionManager manager = new QuestionnaireLineOptionManager(getContext());
            List<OptionControl> options = manager.getQuestionnaireLineOptionControls(line.UniqueId);
            SelectControl control = new SelectControl(getContext(), options, line.Title, line.UniqueId);
            control.deserializeValue(answer.Value);
            options = Linq.findAll(control.options, new Linq.Criteria<OptionControl>() {
                @Override
                public boolean run(OptionControl item) {
                    return item.selected;
                }
            });
            String out = Linq.mapMerge(options, new Linq.Map<OptionControl, String>() {
                @Override
                public String run(OptionControl item) {
                    return item.name;
                }
            }, new Linq.Merge<String>() {
                @Override
                public String run(String item1, String item2) {
                    return item1 + "," + item2;
                }
            });
            return out;
        } else {
            return answer.Value;
        }
    }

    public String serializeOptions(@NonNull QuestionnaireLineModel line, @Nullable QuestionnaireAnswerModel answer) {
        if (answer == null)
            return null;
        if (line.QuestionnaireLineTypeUniqueId.equals(QuestionnaireLineManager.QuestionnaireLineTypeUniqueId.Radio)
                || line.QuestionnaireLineTypeUniqueId.equals(QuestionnaireLineTypeUniqueId.Select)) {
            QuestionnaireLineOptionManager manager = new QuestionnaireLineOptionManager(getContext());
            List<OptionControl> options = manager.getQuestionnaireLineOptionControls(line.UniqueId);
            SelectControl control = new SelectControl(getContext(), options, line.Title, line.UniqueId);
            control.deserializeValue(answer.Value);
            options = Linq.findAll(control.options, new Linq.Criteria<OptionControl>() {
                @Override
                public boolean run(OptionControl item) {
                    return item.selected;
                }
            });
            String out = Linq.mapMerge(options, new Linq.Map<OptionControl, String>() {
                @Override
                public String run(OptionControl item) {
                    return item.value;
                }
            }, new Linq.Merge<String>() {
                @Override
                public String run(String item1, String item2) {
                    return item1 + "," + item2;
                }
            });
            return out;
        } else {
            return null;
        }
    }

}
