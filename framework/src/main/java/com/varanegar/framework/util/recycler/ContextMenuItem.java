package com.varanegar.framework.util.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.varanegar.framework.R;

/**
 * Created by A.Torabi on 7/25/2017.
 */
public abstract class ContextMenuItem extends ContextMenuItemRaw {

    public abstract String getName(int position);

    @DrawableRes
    public abstract int getIcon(int position);

    public abstract void run(int position);

    @Nullable
    @Override
    public View onCreateView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_option_item, parent, false);
        ImageView iconImageView = (ImageView) view.findViewById(R.id.option_icon_image_view);
        TextView nameTextView = (TextView) view.findViewById(R.id.option_name_text_view);
        if (getIcon(position) != 0) {
            iconImageView.setImageResource(getIcon(position));
        }
        nameTextView.setText(getName(position));
        view.setOnClickListener(v -> {
            if (getContextItemDialog() != null)
                getContextItemDialog().dismissAllowingStateLoss();
            run(position);
        });
        return view;
    }

}
