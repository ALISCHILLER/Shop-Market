package com.varanegar.supervisor.utill.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.text.method.ScrollingMovementMethod;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.supervisor.R;

public class BackMessageDialog extends Dialog {
    private Icon icon = Icon.Alert;
    private ButtonHandler positiveButtonHandler = null;
    private ButtonHandler negativeButtonHandler = null;
    private ButtonHandler neutralButtonHandler = null;
    private TextView positiveTextView;
    private TextView negativeTextView;
    private TextView neutralTextView;

    private String title = null;
    private String message = null;
    private String data = null;
    @StringRes
    private int titleId = 0;
    @StringRes
    private int messageId = 0;
    private TextView titleTextView;
    private TextView messageTextView;
    private TextView dataTextView;
    private ImageView iconImageView;

    public BackMessageDialog(@NonNull Context context) {
        super(context);
        setCanceledOnTouchOutside(false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_black_message);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.cute_message_dialog_animation;
        positiveTextView = (TextView) findViewById(R.id.positive_text_view);
        negativeTextView = (TextView) findViewById(R.id.negative_text_view);
        neutralTextView = (TextView) findViewById(R.id.neutral_text_view);
        setupButtons();
        titleTextView = (TextView) findViewById(R.id.title_text_view);
        messageTextView = (TextView) findViewById(R.id.message_text_view);
        dataTextView = (TextView) findViewById(R.id.data_text_view);
        setupText();
        iconImageView = (ImageView) findViewById(com.varanegar.framework.R.id.icon_image_view);
        iconImageView.setVisibility(View.GONE);
        titleTextView.setVisibility(View.GONE);
        boolean multipan = context.getResources().getBoolean(com.varanegar.framework.R.bool.multipane);
        boolean landscap = context.getResources().getBoolean(com.varanegar.framework.R.bool.is_landscape);
        if (!multipan && !landscap)
            miximize();
    }




    private void miximize() {
        Window window = getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        int width = size.x;
        window.setLayout((int) (width * 0.9), WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
        setupIcon();
    }


    public void setPositiveButton(@StringRes int text, View.OnClickListener onClickListener) {
        positiveButtonHandler = new ButtonHandler(text, onClickListener);
        setupButtons();
    }
    public void setNeutralButton(@StringRes int text, View.OnClickListener onClickListener) {
        neutralButtonHandler = new ButtonHandler(text, onClickListener);
        setupButtons();
    }

    public void setNegativeButton(@StringRes int text, View.OnClickListener onClickListener) {
        negativeButtonHandler = new ButtonHandler(text, onClickListener);
        setupButtons();
    }

    public void setTitle(@StringRes int title) {
        this.titleId = title;
        setupText();
    }

    public void setMessage(@StringRes int message) {
        this.messageId = message;
        setupText();
    }

    public void setTitle(String title) {
        this.title = title;
        setupText();
    }

    public void setMessage(String message) {
        this.message = message;
        setupText();
    }
    public void setData(String data) {
        this.data = data;
        setupText();
    }
    private void setupIcon() {
        if (iconImageView != null) {
            if (icon == Icon.Error)
                iconImageView.setImageResource(com.varanegar.framework.R.drawable.ic_error_red_96dp);
            else if (icon == Icon.Success)
                iconImageView.setImageResource(com.varanegar.framework.R.drawable.ic_check_green_96dp);
            else if (icon == Icon.Info)
                iconImageView.setImageResource(com.varanegar.framework.R.drawable.ic_info_teal_96dp);
            else if (icon == Icon.Warning)
                iconImageView.setImageResource(com.varanegar.framework.R.drawable.ic_error_amber_96dp);
            else if (icon == Icon.Alert)
                iconImageView.setImageResource(com.varanegar.framework.R.drawable.ic_error_yellow_96dp);
        }
    }


    private void setupText() {
        if (titleTextView != null) {
            if (title != null)
                titleTextView.setText(title);
            else if (titleId != 0)
                titleTextView.setText(titleId);
        }
        if (messageTextView != null) {
            if (message != null)
                messageTextView.setText(message);
            else if (messageId != 0)
                messageTextView.setText(messageId);
            messageTextView.setMovementMethod(new ScrollingMovementMethod());
        }

        if (dataTextView!=null){
            if (data != null)
                dataTextView.setText(data);
        }
    }



    private void setupButtons() {
        if (positiveTextView != null) {
            if (positiveButtonHandler != null) {
                positiveTextView.setVisibility(View.VISIBLE);
                positiveTextView.setText(positiveButtonHandler.text);
                positiveTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                        if (positiveButtonHandler.onClickListener != null)
                            positiveButtonHandler.onClickListener.onClick(v);
                    }
                });
            } else {
                positiveTextView.setVisibility(View.GONE);
            }
        }
        if (negativeTextView != null) {
            if (negativeButtonHandler != null) {
                negativeTextView.setVisibility(View.VISIBLE);
                negativeTextView.setText(negativeButtonHandler.text);
                negativeTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                        if (negativeButtonHandler.onClickListener != null)
                            negativeButtonHandler.onClickListener.onClick(v);
                    }
                });
            } else {
                negativeTextView.setVisibility(View.GONE);
            }
        }
        if (neutralTextView != null) {
            if (neutralButtonHandler != null) {
                neutralTextView.setVisibility(View.VISIBLE);
                neutralTextView.setText(neutralButtonHandler.text);
                neutralTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                        if (neutralButtonHandler.onClickListener != null)
                            neutralButtonHandler.onClickListener.onClick(v);
                    }
                });
            } else {
                neutralTextView.setVisibility(View.GONE);
            }
        }
    }
    public BackMessageDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BackMessageDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
    class ButtonHandler {
        public ButtonHandler(@StringRes int text, View.OnClickListener onClickListener) {
            this.text = text;
            this.onClickListener = onClickListener;
        }

        @StringRes
        int text;
        View.OnClickListener onClickListener;
    }
}
