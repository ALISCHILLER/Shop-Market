package com.varanegar.supervisor.fragment.menuTools_Fragment.model;

import com.varanegar.supervisor.R;

import java.util.Arrays;
import java.util.List;

public class ItemStation {


    public static ItemStation get() {
        return new ItemStation();
    }

    private ItemStation() {
    }

    public List<Item> getItem() {
        return Arrays.asList(
                new Item("لیست پین کدها مشتریان", R.drawable.news),
                new Item("لیست درخواست ها", R.drawable.news),
                new Item("خبرنامه زرماکارون", R.drawable.news)
               );
    }
}
