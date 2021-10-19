package com.varanegar.vaslibrary.ui.drawer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.varanegar.framework.util.component.drawer.BaseDrawerItem;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.UserManager;

/**
 * Created by atp on 1/29/2017.
 */

public abstract class UserProfileDrawerItem extends BaseDrawerItem {

    public UserProfileDrawerItem(Context context) {
        super(context);
    }

    public UserProfileDrawerItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UserProfileDrawerItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onCreateView() {
        View view = inflate(getContext(), R.layout.drawer_profile_item, this);
        ((TextView) view.findViewById(R.id.user_name_text_view)).setText(UserManager.readFromFile(getContext()).UserName);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileDrawerItem.this.onClick();
            }
        });
    }

    protected abstract void onClick();
}
