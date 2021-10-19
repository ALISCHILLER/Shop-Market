package com.varanegar.framework.util.component.toolbar;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.varanegar.framework.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


/**
 * Created by A.Torabi on 8/22/2017.
 */

public class CuteButtonViewHolder extends RecyclerView.ViewHolder {
    private ImageView iconImageView;
    private ImageView disabledIconImageView;
    private TextView nameTextView;

    public CuteButtonViewHolder(View itemView) {
        super(itemView);
        {
            iconImageView = (ImageView) itemView.findViewById(R.id.icon_image_view);
            disabledIconImageView = (ImageView) itemView.findViewById(R.id.disabled_icon_image_view);
            nameTextView = (TextView) itemView.findViewById(R.id.name_text_view);
        }

    }

    public void enlarge() {
        nameTextView.setVisibility(VISIBLE);
    }

    public void compress() {
        nameTextView.setVisibility(GONE);
    }

    public void setTitle(@StringRes int title) {
        nameTextView.setText(title);
    }

    public void setIcon(@DrawableRes int icon) {
        if (icon != -1) {
            iconImageView.setImageResource(icon);
            disabledIconImageView.setImageResource(icon);
        }
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            iconImageView.setVisibility(VISIBLE);
            disabledIconImageView.setVisibility(GONE);
        } else {
            iconImageView.setVisibility(GONE);
            disabledIconImageView.setVisibility(VISIBLE);
        }
    }
}
