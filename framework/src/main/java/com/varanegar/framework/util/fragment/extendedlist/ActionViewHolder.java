package com.varanegar.framework.util.fragment.extendedlist;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.varanegar.framework.R;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;

import timber.log.Timber;

/**
 * Created by atp on 1/15/2017.
 */

class ActionViewHolder extends RecyclerView.ViewHolder {

    private final MainVaranegarActivity activity;
    private final ImageView doneImageView;
    private final ProgressBar progressBar;
    private ImageView actionImageViewDisabled;
    private ActionsAdapter adapter;
    private TextView actionNameTextView;
    private ImageView actionImageView;
    private LinearLayout linearLayoutParent;
    boolean isClickable = true;

    public ActionViewHolder(MainVaranegarActivity activity, View itemView, ActionsAdapter adapter) {
        super(itemView);
        this.activity = activity;
        this.adapter = adapter;
        actionNameTextView = itemView.findViewById(R.id.action_name_text_view);
        actionImageView = itemView.findViewById(R.id.action_icon_image_view);
        actionImageViewDisabled = itemView.findViewById(R.id.action_icon_image_view_disabled);
        doneImageView = itemView.findViewById(R.id.done_image_view);
        progressBar = itemView.findViewById(R.id.progress_view);
        linearLayoutParent = itemView.findViewById(R.id.linearLayoutParent);
    }

    public void bind(int position) {
        final Action action = adapter.getActions().get(position);
        if (action != null) {

            linearLayoutParent.setAnimation(null);
            linearLayoutParent.clearAnimation();
            if (action == Action.currentAction) {
                Context context = linearLayoutParent.getContext();
                Resources resources = context.getResources();
                linearLayoutParent.setBackgroundColor(resources.getColor(R.color.light_green));
                linearLayoutParent.startAnimation(AnimationUtils.loadAnimation(context, R.anim.bounce));
            } else
                linearLayoutParent.setBackgroundColor(Color.TRANSPARENT);

            if (action.icon != -1) {
                actionImageView.setImageResource(action.icon);
                actionImageViewDisabled.setImageResource(action.icon);
            }
            if (action.isRunning()) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
            actionNameTextView.setText(action.getName());
            if (adapter.isCollapsed())
                actionNameTextView.setVisibility(View.GONE);
            else
                actionNameTextView.setVisibility(View.VISIBLE);
            if (action.getIsDone()) {
                actionNameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_done_green_900_24dp, 0, 0, 0);
                if (adapter.isCollapsed())
                    doneImageView.setVisibility(View.VISIBLE);
                else
                    doneImageView.setVisibility(View.GONE);
            } else {
                actionNameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                if (adapter.isCollapsed())
                    doneImageView.setVisibility(View.INVISIBLE);
                else
                    doneImageView.setVisibility(View.GONE);
            }
            actionImageView.setEnabled(true);
            actionNameTextView.setEnabled(true);
            if (action.getIsEnabled() == null) {
                actionImageViewDisabled.setVisibility(View.GONE);
                if (!action.isRunning())
                    actionImageView.setVisibility(View.VISIBLE);
                else
                    actionImageView.setVisibility(View.GONE);
            } else {
                if (!action.isRunning())
                    actionImageViewDisabled.setVisibility(View.VISIBLE);
                else
                    actionImageViewDisabled.setVisibility(View.GONE);
                actionImageView.setVisibility(View.GONE);
            }
            final View.OnClickListener listener = v -> {
                boolean isRunning = Linq.exists(adapter.getActions(), Action::isRunning);
                if (isRunning)
                    return;

                if (isClickable) {
                    isClickable = false;
                    Timber.d("Action " + action.getClass().getName() + " clicked.");
                    if (!action.isRunning()) {
                        String error = action.getIsEnabled();
                        if (error == null) {
                            Timber.d("Action " + action.getClass().getName() + " run.");
                            action.run();
                        } else {
                            Timber.d("Action " + action.getClass().getName() + " is disabled, reason = " + error);
                            CuteMessageDialog dialog = new CuteMessageDialog(activity);
                            dialog.setMessage(error);
                            dialog.setTitle(R.string.error);
                            dialog.setIcon(Icon.Error);
                            dialog.setPositiveButton(R.string.ok, null);
                            dialog.show();
                        }
                    } else {
                        Timber.d("Action " + action.getClass().getName() + " ignored because it is running currently.");
                        activity.showSnackBar(R.string.operation_is_running, MainVaranegarActivity.Duration.Short);
                    }
                }
                new Handler().postDelayed(() -> isClickable = true, 500);

            };
            actionImageViewDisabled.setOnClickListener(listener);
            actionImageView.setOnClickListener(listener);
            actionNameTextView.setOnClickListener(listener);
            itemView.setOnClickListener(listener);

            View.OnLongClickListener longListener = v -> {
                boolean isRunning = Linq.exists(adapter.getActions(), Action::isRunning);
                if (isRunning)
                    return true;

                Timber.d("Action " + action.getClass().getName() + " long clicked.");
                if (!action.isRunning()) {
                    String error = action.getIsEnabled();
                    if (error == null) {
                        Timber.d("Action " + action.getClass().getName() + " run.");
                        action.runOnLongClick();
                    } else {
                        Timber.d("Action " + action.getClass().getName() + " is disabled, reason = " + error);
                        CuteMessageDialog dialog = new CuteMessageDialog(activity);
                        dialog.setMessage(error);
                        dialog.setTitle(R.string.error);
                        dialog.setIcon(Icon.Error);
                        dialog.setPositiveButton(R.string.ok, null);
                        dialog.show();
                    }
                    SystemClock.sleep(100);
                } else {
                    Timber.d("Action " + action.getClass().getName() + " ignored because it is running currently.");
                    activity.showSnackBar(R.string.operation_is_running, MainVaranegarActivity.Duration.Short);
                }
                return true;
            };
            actionNameTextView.setOnLongClickListener(longListener);
        }


    }

}
