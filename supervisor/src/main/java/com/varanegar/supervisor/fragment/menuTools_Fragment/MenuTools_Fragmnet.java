package com.varanegar.supervisor.fragment.menuTools_Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.varanegar.framework.ui.scrollercard.DiscreteScrollView;
import com.varanegar.framework.ui.scrollercard.InfiniteScrollAdapter;
import com.varanegar.framework.ui.scrollercard.transform.ScaleTransformer;
import com.varanegar.supervisor.IMainPageFragment;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.fragment.menuTools_Fragment.model.Item;
import com.varanegar.supervisor.fragment.menuTools_Fragment.model.ItemStation;
import com.varanegar.supervisor.fragment.news_fragment.News_Fragment;

import java.util.List;

public class MenuTools_Fragmnet extends IMainPageFragment implements
        DiscreteScrollView.ScrollStateChangeListener<MenuToolsAdapter.ViewHolder>,
        DiscreteScrollView.OnItemChangedListener<MenuToolsAdapter.ViewHolder>,
        MenuToolsAdapter.ItemClickListener,
        View.OnClickListener {
    private List<Item> items;


   private TextView item_name;
    private DiscreteScrollView cityPicker;
    private InfiniteScrollAdapter<?> infiniteAdapter;

    @Override
    protected View onCreateContentView(@NonNull LayoutInflater inflater,
                                       @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_menutools_layout,container,false);
        item_name=view.findViewById(R.id.item_name);
        items= ItemStation.get().getItem();
        cityPicker =view.findViewById(R.id.item_cards);
        cityPicker.setSlideOnFling(true);
        MenuToolsAdapter menuToolsAdapter=new MenuToolsAdapter(getActivity(),items);
        infiniteAdapter = InfiniteScrollAdapter.wrap(menuToolsAdapter);
        cityPicker.setAdapter(menuToolsAdapter);
        cityPicker.addOnItemChangedListener(this);
        menuToolsAdapter.addItemClickListener(this);
        cityPicker.addScrollStateChangeListener(this);
        cityPicker.scrollToPosition(2);
        cityPicker.setItemTransitionTimeMillis(110);
        cityPicker.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());
        return view;
    }
    @Override
    public void onItemClick(int position) {
        News_Fragment news_fragment=new News_Fragment();
        getVaranegarActvity().pushFragment(news_fragment);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onScrollStart(@NonNull MenuToolsAdapter.ViewHolder currentItemHolder, int adapterPosition) {

    }

    @Override
    public void onScrollEnd(@NonNull MenuToolsAdapter.ViewHolder currentItemHolder, int adapterPosition) {

    }

    @Override
    public void onScroll(float scrollPosition,
                         int currentPosition, int newPosition,
                         @Nullable MenuToolsAdapter.ViewHolder currentHolder,
                         @Nullable MenuToolsAdapter.ViewHolder newCurrent) {


    }
    @Override
    public void onCurrentItemChanged(@Nullable MenuToolsAdapter.ViewHolder viewHolder, int adapterPosition) {
        int positionInDataSet = infiniteAdapter.getRealPosition(adapterPosition);
        onItemChanged(items.get(positionInDataSet));
    }

    private void onItemChanged(Item item) {
        item_name.setText(item.getNameItem());
    }


}
