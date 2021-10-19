package com.varanegar.framework.base.questionnaire.controls;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.varanegar.framework.R;
import com.varanegar.framework.base.questionnaire.validator.AttachmentValidator;
import com.varanegar.framework.base.questionnaire.validator.ControlValidator;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 7/1/2017.
 */

public abstract class FormControl {
    @NonNull
    private Context context;
    public UUID uniqueId;
    private String title;
    private List<ControlValidator> validators = new ArrayList<>();
    @Nullable
    public UUID AttachmentId;
    private ImageView addAttachImageView;
    private ImageView deleteAttachImageView;
    private ImageView viewAttachImageView;
    private boolean hasAttachment;
    private boolean attachmentChanged;
    private CustomImageAttachment customImageAttachment;
    public String AttachmentPath;
    private View attachmentLayout;

    public void setCustomImageAttachment(CustomImageAttachment customImageAttachment) {
        this.customImageAttachment = customImageAttachment;
    }

    protected abstract boolean isValueChanged();

    public boolean isChanged() {
        return attachmentChanged || isValueChanged();
    }

    public void showAttachmentButton() {
        this.hasAttachment = true;
        if (addAttachImageView != null) {
            updateImageViews();
        }
    }

    public void hideAttachmentButton() {
        this.hasAttachment = false;
        if (addAttachImageView != null) {
            updateImageViews();
        }
    }

    public void hasAttachment(int numberOfOptions, boolean isMandatory) {
        this.hasAttachment = true;
        if (isMandatory)
            validators.add(new AttachmentValidator(numberOfOptions));
    }

    public void addValidator(ControlValidator validator) {
        if (validator != null)
            this.validators.add(validator);
    }

    public void addValidators(List<ControlValidator> validators) {
        if (validators != null)
            this.validators.addAll(validators);
    }

    public FormControl(@NonNull Context context, @NonNull String title, @NonNull UUID uniqueId) {
        this.context = context;
        this.title = title;
        this.uniqueId = uniqueId;
    }

    public View getView(final AppCompatActivity activity, ViewGroup parent) {
        View view = activity.getLayoutInflater().inflate(R.layout.form_control_layout, parent, false);
        TextView titleTextView = (TextView) view.findViewById(R.id.title_text_view);
        titleTextView.setText(title);
        attachmentLayout = view.findViewById(R.id.attachment_layout);
        if (hasAttachment)
            attachmentLayout.setVisibility(View.VISIBLE);
        else
            attachmentLayout.setVisibility(View.GONE);
        addAttachImageView = (ImageView) view.findViewById(R.id.add_attachment_image_view);
        deleteAttachImageView = (ImageView) view.findViewById(R.id.delete_attachment_image_view);
        viewAttachImageView = (ImageView) view.findViewById(R.id.view_attachment_image_view);
        updateImageViews();
        addAttachImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AttachmentId == null) {
                    attachFile(activity, AttachmentType.Image);
                }
            }
        });
        deleteAttachImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AttachmentId != null)
                    deleteAttachment();
            }
        });
        viewAttachImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AttachmentId != null)
                    viewAttachment(activity);
            }
        });
        FrameLayout contentView = view.findViewById(R.id.content_layout);
        onCreateContentView(activity, contentView);
        return view;
    }

    private void updateImageViews() {
        attachmentLayout.setVisibility(hasAttachment ? View.VISIBLE : View.GONE);
        if (AttachmentId == null) {
            addAttachImageView.setImageResource(R.drawable.ic_attach_file_green_900_24dp);
            deleteAttachImageView.setImageResource(R.drawable.ic_delete_forever_blue_grey_300_24dp);
            viewAttachImageView.setImageResource(R.drawable.ic_zoom_in_blue_grey_300_24dp);
        } else {
            addAttachImageView.setImageResource(R.drawable.ic_attach_file_blue_grey_300_24dp);
            deleteAttachImageView.setImageResource(R.drawable.ic_delete_forever_red_700_24dp);
            viewAttachImageView.setImageResource(R.drawable.ic_zoom_in_green_900_24dp);
        }
    }

    protected abstract void onCreateContentView(@NonNull AppCompatActivity activity, @NonNull ViewGroup parent);

    public String validate() {
        if (validators != null) {
            for (ControlValidator validator :
                    validators) {
                boolean success = validator.validate(this);
                if (!success)
                    return validator.getMessage(context);
            }
            return null;
        }
        return null;
    }

    public abstract void setError(String err);

    public abstract void clearError();

    @Nullable
    public abstract String serializeValue();

    public abstract void deserializeValue(@Nullable String value);

    public void attachFile(final AppCompatActivity activity, AttachmentType type) {
        if (type == AttachmentType.Image) {
            final AttachImageDialog dialog = new AttachImageDialog();
            dialog.onAttachment = new AttachImageDialog.OnAttachment() {
                @Override
                public void onDone() {
                    final Bitmap bitmap = dialog.getBitmap();
                    final UUID id = UUID.randomUUID();
                    final ProgressDialog progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage(context.getString(R.string.saving_picture));
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                if (customImageAttachment != null)
                                    AttachmentPath = customImageAttachment.saveImage(bitmap, id);
                                else {
                                    FileOutputStream out = context.openFileOutput(id.toString(), Context.MODE_PRIVATE);
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, out);
                                }
                                if (activity != null && !activity.isFinishing()) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.dismiss();
                                            dialog.dismiss();
                                            AttachmentId = id;
                                            attachmentChanged = true;
                                            updateImageViews();
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                Timber.e(e);
                                if (activity != null && !activity.isFinishing()) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.dismiss();
                                            dialog.dismiss();
                                            AttachmentId = null;
                                            attachmentChanged = true;
                                            updateImageViews();
                                            CuteMessageDialog dialog = new CuteMessageDialog(context);
                                            dialog.setTitle(R.string.error);
                                            dialog.setMessage(R.string.error_saving_attachment);
                                            dialog.setPositiveButton(R.string.ok, null);
                                            dialog.setIcon(Icon.Error);
                                            dialog.show();
                                        }
                                    });
                                }
                            }

                        }
                    });
                    thread.start();
                }
            };
            dialog.show(activity.getSupportFragmentManager(), "a60f2332-bc4e-4e22-ba82-1198525c4486");
        }
    }

    public void deleteAttachment() {
        if (AttachmentId != null) {
            if (customImageAttachment != null) {
                try {
                    customImageAttachment.deleteImage(AttachmentId);
                    AttachmentId = null;
                    attachmentChanged = true;
                    updateImageViews();
                } catch (Exception e) {
                    Timber.e(e);
                }
            } else {
                context.deleteFile(AttachmentId.toString());
            }
        }
    }

    public void viewAttachment(AppCompatActivity activity) {
        if (AttachmentId != null) {
            ShowImageAttachmentDialog dialog = new ShowImageAttachmentDialog();
            dialog.setImagePath(AttachmentPath);
            dialog.show(activity.getSupportFragmentManager(), "4899ce64-060c-47dd-88dd-3687eeafeab7");
        }

    }

    public void deserializeValidators(@Nullable String validators) {
        if (validators == null)
            return;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ControlValidator.class, new ControlValidatorDeserializer());
        Gson gson = gsonBuilder.create();
        List<ControlValidator> controlValidators = gson.fromJson(validators, new TypeToken<List<ControlValidator>>() {
        }.getType());
        addValidators(controlValidators);
    }

    public interface CustomImageAttachment {
        String saveImage(Bitmap bitmap, UUID attachmentId) throws Exception;

        void deleteImage(UUID attachmentId) throws Exception;
    }
}
