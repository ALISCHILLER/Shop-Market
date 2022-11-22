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
        HelpCategory ca2=new HelpCategory("آموزش ویدئوی توزیع(مخصوص موزع)",false);
        HelpCategory ca3=new HelpCategory("آموزش صفحه لیست مشتریان(مخصوص ویزیتورها)",false);
        HelpCategory ca4=new HelpCategory("آموزش صفحه مشتریان(مخصوص ویزیتورها)",false);
        HelpCategory ca5=new HelpCategory("آموزش صفحه لیست مشتریان(مخصوص موزع)",false);
        HelpCategory ca6=new HelpCategory("آموزش صفحه مشتریان(مخصوص موزع)",false);

        helpsCategories.add(ca1);
        helpsCategories.add(ca2);
        helpsCategories.add(ca3);
        helpsCategories.add(ca4);
        helpsCategories.add(ca5);
        helpsCategories.add(ca6);
        return helpsCategories;
    }
}
