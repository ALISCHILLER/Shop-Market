package com.varanegar.framework.util.fragment.extendedlist;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.R;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by atp on 1/15/2017.
 */

public abstract class ActionsAdapter extends RecyclerView.Adapter {
    private final SharedPreferences sharedPreferences;

    public List<Action> getActions() {
        return actions;
    }

    private boolean expanded = false;

    public void expand() {
        sharedPreferences.edit().putBoolean("expanded", true).apply();
        this.expanded = true;
        notifyDataSetChanged();
    }

    public void collapse() {
        sharedPreferences.edit().putBoolean("expanded", false).apply();
        this.expanded = false;
        notifyDataSetChanged();
    }

    public interface AdapterCallBack {
        void done();
    }

    public void setActions(@NonNull List<Action> actions, @NonNull final AdapterCallBack callBack) {
        this.actions = actions;
        new Thread(new Runnable() {
            @Override
            public void run() {
                VaranegarApplication.getInstance().resetElapsedTime("Actions Adapter");
                for (Action action :
                        ActionsAdapter.this.actions) {
                    action.refresh();
                }
                VaranegarApplication.getInstance().printElapsedTime("Actions Adapter --- Finished   ");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callBack.done();
                    }
                });
            }
        }).start();
    }

    private List<Action> actions = new ArrayList<>();


    public UUID getSelectedId() {
        return selectedId;
    }

    private UUID selectedId;

    public MainVaranegarActivity getActivity() {
        return activity;
    }

    private MainVaranegarActivity activity;

    public ActionsAdapter(MainVaranegarActivity activity, UUID selectedId) {
        this.activity = activity;
        this.selectedId = selectedId;
        sharedPreferences = activity.getSharedPreferences("actions_adapter", Context.MODE_PRIVATE);
        expanded = sharedPreferences.getBoolean("expanded", true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_list_action, parent, false);
        return new ActionViewHolder(activity, v, this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ActionViewHolder viewHolder = (ActionViewHolder) holder;
        viewHolder.bind(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return actions == null ? 0 : actions.size();
    }

    public void refresh(final AdapterCallBack callBack) {
        for (Action action :
                ActionsAdapter.this.actions) {
            action.disable();
        }
        update();
        new Thread(new Runnable() {
            @Override
            public void run() {
                VaranegarApplication.getInstance().resetElapsedTime("Actions Adapter");
                for (Action action :
                        ActionsAdapter.this.actions) {
                    action.refresh();
                }
                VaranegarApplication.getInstance().printElapsedTime("Actions Adapter --- Finished   ");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                        callBack.done();
                    }
                });
            }
        }).start();
    }

    public abstract void update();

    public boolean isCollapsed() {
        return !expanded;
    }
}
