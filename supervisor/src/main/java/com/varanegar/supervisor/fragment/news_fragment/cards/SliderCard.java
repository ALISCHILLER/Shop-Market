package com.varanegar.supervisor.fragment.news_fragment.cards;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.varanegar.supervisor.R;
import com.varanegar.supervisor.fragment.news_fragment.cards.utils.DecodeBitmapTask;

public class SliderCard extends RecyclerView.ViewHolder implements DecodeBitmapTask.Listener {
    private static int viewWidth = 0;
    private static int viewHeight = 0;

    private final ImageView imageView;

    private DecodeBitmapTask task;

    public SliderCard(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.image);
    }

    void setContent(@DrawableRes final int resId) {
        if (viewWidth == 0) {
            itemView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    viewWidth = itemView.getWidth();
                    viewHeight = itemView.getHeight();
                    loadBitmap(resId);
                }
            });
        } else {
            loadBitmap(resId);
        }
    }

    void clearContent() {
        if (task != null) {
            task.cancel(true);
        }
    }

    private void loadBitmap(@DrawableRes int resId) {
        task = new DecodeBitmapTask(itemView.getResources(), resId, viewWidth, viewHeight, this);
        task.execute();
    }

    @Override
    public void onPostExecuted(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

}
