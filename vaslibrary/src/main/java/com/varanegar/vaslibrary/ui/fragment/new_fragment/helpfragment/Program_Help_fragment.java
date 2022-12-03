package com.varanegar.vaslibrary.ui.fragment.new_fragment.helpfragment;

import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.ui.fragment.new_fragment.videoPlayer.MediaPlayerFragment;
import com.varanegar.framework.ui.expandablelib.ExpandCollapseListener;
import com.varanegar.framework.ui.expandablelib.ExpandableLayout;
import com.varanegar.framework.ui.expandablelib.Section;

import java.util.ArrayList;
import java.util.List;

public class Program_Help_fragment extends VaranegarFragment {

    ExpandableLayout expandableLayout;
    String[] parents = new String[]{
            "آموزش ویدئوی ویزیت"
            , "لیست مشتریان (مخصوص ویزیتورها)"
            ,"آموزش ویدئوی توزیع"};


    Button btn;
    FloatingSearchView floating_search_view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(
                R.layout.layout_frgment_programer_help, container, false);
        expandableLayout=view.findViewById(R.id.el);
//        EditText editText = (EditText) view.findViewById(R.id.edittext);

        setexpandableLayout();
        expandableLayout.setExpandListener(new ExpandCollapseListener.ExpandListener<HelpCategory>() {
            @Override
            public void onExpanded(int parentIndex, HelpCategory parent, View view) {

                view.findViewById(R.id.arrow).setBackgroundResource(R.drawable.ic_icon_ionic_ios_arrow_up);
            }
        });


        expandableLayout.setCollapseListener(new ExpandCollapseListener.CollapseListener<HelpCategory>() {
            @Override
            public void onCollapsed(int parentIndex, HelpCategory parent, View view) {
                //Layout collapsed
                view.findViewById(R.id.arrow).setBackgroundResource(R.drawable.ic_icon_ionic_ios_arrow_down);
            }
        });


       floating_search_view =view.findViewById(R.id.floating_search_view);
        floating_search_view.setOnQueryChangeListener((oldQuery, newQuery) -> {

            //get suggestions based on newQuery

            //pass them on to the search view
            Log.e("TAG", "onSearchTextChanged:"+newQuery );
            expandableLayout.filterChildren(obj ->
                    ((Help) obj).name.toLowerCase().contains(newQuery.toString().toLowerCase()));
        });




//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                expandableLayout.filterChildren(obj ->
//                        ((Help) obj).name.toLowerCase().contains(s.toString().toLowerCase()));
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                expandableLayout.filterChildren(obj ->
//                        ((Help) obj).name.toLowerCase().contains(s.toString().toLowerCase()));
//            }
//        });


        return view;
    }


    private void setexpandableLayout(){
        expandableLayout.setRenderer(new ExpandableLayout.Renderer<HelpCategory, Help>() {
            @Override
            public void renderParent(View view, HelpCategory model, boolean isExpanded,
                                     int parentPosition) {

                ((TextView) view.findViewById(R.id.tvParent)).setText(model.name);
                LinearLayout relativeLayout=view.findViewById(R.id.relativeLayout);
                view.findViewById(R.id.arrow).setBackgroundResource(isExpanded ?
                        R.drawable.ic_icon_ionic_ios_arrow_up
                        : R.drawable.ic_icon_ionic_ios_arrow_down);

            }
            @Override
            public void renderChild(View view, Help model, int parentPosition, int childPosition) {
                TextView txt= view.findViewById(R.id.tvChild);
                txt.setText(model.name);

                txt.setOnClickListener(v ->{
                    MediaPlayerFragment fragment = new MediaPlayerFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("urlPlayer", model.url);
                    fragment.setArguments(bundle);
                    final MainVaranegarActivity activity = getVaranegarActvity();
                    if (activity == null || activity.isFinishing())
                        return;
                        activity.pushFragment(fragment);
                });

            }
        });





        List<HelpCategory> helpsCategories=new ArrayList<>();
        HelpCategory helpCategory=new HelpCategory();
        helpsCategories.addAll(helpCategory.getItems());

        Help help=new Help();

        HelpCategory HelpCategory=new HelpCategory();
        for (int i=0 ; i < helpsCategories.size();i++){
           List<Help> helplist= help.getItems(i);
            int item=getArguments().getInt("item_Expandable",0);
            if (item==i){
                helpsCategories.get(i).isExpanded=true;
            }
            expandableLayout.addSection(getSection(helpsCategories.get(i),helplist));

        }

    }

    public Section<HelpCategory, Help> getSection(HelpCategory helpsCategories,List<Help> helplist) {
        Section<HelpCategory, Help> section = new Section<>();
        section.parent = helpsCategories;
        section.children.addAll(helplist);
        section.expanded = true;
            return section;
    }

}
