package com.varanegar.vaslibrary.ui.fragment.new_fragment.helpfragment;

import java.util.ArrayList;
import java.util.List;

public class HelpCategory {
    public String name;
    boolean isExpanded;
    public HelpCategory(){

    }
    public HelpCategory(String name,boolean isExpanded)
    {
        this.name = name;
        this.isExpanded = isExpanded;
    }
    public HelpCategory(String name)
    {
        this.name = name;
        this.isExpanded = isExpanded;
    }


    public List<HelpCategory> getItems(){
        List<HelpCategory> helpsCategories=new ArrayList<>();
        HelpCategory ca1=new HelpCategory("آموزش ویدئوی ویزیت",false);
        helpsCategories.add(ca1);
        return helpsCategories;
    }
}
