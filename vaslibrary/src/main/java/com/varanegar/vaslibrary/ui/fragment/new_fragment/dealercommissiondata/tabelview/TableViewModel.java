/*
 * MIT License
 *
 * Copyright (c) 2021 Evren Coşkun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.varanegar.vaslibrary.ui.fragment.new_fragment.dealercommissiondata.tabelview;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.ui.fragment.new_fragment.dealercommissiondata.tabelview.model.Cell;
import com.varanegar.vaslibrary.ui.fragment.new_fragment.dealercommissiondata.tabelview.model.ColumnHeader;
import com.varanegar.vaslibrary.ui.fragment.new_fragment.dealercommissiondata.tabelview.model.RowHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by evrencoskun on 4.02.2018.
 */

public class TableViewModel {

    // Columns indexes
    public static final int MOOD_COLUMN_INDEX = 3;
    public static final int GENDER_COLUMN_INDEX = 4;

    // Constant values for icons
    public static final int SAD = 1;
    public static final int HAPPY = 2;
    public static final int BOY = 1;
    public static final int GIRL = 2;

    // Constant size for dummy data sets
    private static final int COLUMN_SIZE = 12;
    private static final int ROW_SIZE = 4;

    // Drawables
    @DrawableRes
    private final int mBoyDrawable;
    @DrawableRes
    private final int mGirlDrawable;
    @DrawableRes
    private final int mHappyDrawable;
    @DrawableRes
    private final int mSadDrawable;

    public TableViewModel() {
        // initialize drawables
        mBoyDrawable = R.drawable.ic_male;
        mGirlDrawable = R.drawable.ic_female;
        mHappyDrawable = R.drawable.ic_happy;
        mSadDrawable = R.drawable.ic_sad;
    }

    @NonNull
    private List<RowHeader> getSimpleRowHeaderList() {
        List<RowHeader> list = new ArrayList<>();

            RowHeader header = new RowHeader(String.valueOf(1), "هدف");
            RowHeader header2 = new RowHeader(String.valueOf(2), "فروش");
            RowHeader header3 = new RowHeader(String.valueOf(3), "درصددستیابی");
            RowHeader header4 = new RowHeader(String.valueOf(4), "پرداخت");
            list.add(header);
            list.add(header2);
            list.add(header3);
            list.add(header4);


        return list;
    }

    /**
     * This is a dummy model list test some cases.
     */
    @NonNull
    private List<ColumnHeader> getRandomColumnHeaderList() {
        List<ColumnHeader> list = new ArrayList<>();


            ColumnHeader header = new ColumnHeader(String.valueOf(1), "رشته ای");
            ColumnHeader header2 = new ColumnHeader(String.valueOf(2), "لازانیا");
            ColumnHeader header3 = new ColumnHeader(String.valueOf(3), "آشیانه");
            ColumnHeader header4 = new ColumnHeader(String.valueOf(4), "جامبو");
            ColumnHeader header5 = new ColumnHeader(String.valueOf(5), "پودرکیک");
            ColumnHeader header6 = new ColumnHeader(String.valueOf(6), "آرد");
            ColumnHeader header7 = new ColumnHeader(String.valueOf(7), "فرمی");
            ColumnHeader header8 = new ColumnHeader(String.valueOf(8), "رشته آش ");
            ColumnHeader header9 = new ColumnHeader(String.valueOf(9), "CoverageRatePayment ");
            ColumnHeader header10 = new ColumnHeader(String.valueOf(10), "HitRatePayment ");
            ColumnHeader header11 = new ColumnHeader(String.valueOf(11), "LpscPayment ");
            ColumnHeader header12 = new ColumnHeader(String.valueOf(12), "جمع کل");
            list.add(header);
            list.add(header2);
            list.add(header3);
            list.add(header4);
            list.add(header5);
            list.add(header6);
            list.add(header7);
            list.add(header8);
            list.add(header9);
            list.add(header10);
            list.add(header11);
            list.add(header12);



        return list;
    }

    /**
     * This is a dummy model list test some cases.
     */
    @NonNull
    private List<List<Cell>> getCellListForSortingTest() {
        List<List<Cell>> list = new ArrayList<>();
        for (int i = 0; i < ROW_SIZE; i++) {
            List<Cell> cellList = new ArrayList<>();
            for (int j = 0; j < COLUMN_SIZE; j++) {
                Object text = "cell " + j + " " + i;

                Cell cell;
                if (i==0){
                    if (j==0){
                        cell=new Cell("1","12");
                        cellList.add(cell);
                    }
                    if (j==2){
                        cell=new Cell("1","12");
                        cellList.add(cell);
                    }
                    if (j==3){
                        cell=new Cell("1","12");
                        cellList.add(cell);
                    }
                    if (j==4){
                        cell=new Cell("1","12");
                        cellList.add(cell);
                    }
                    if (j==5){
                        cell=new Cell("1","12");
                        cellList.add(cell);
                    }
                    if (j==6){
                        cell=new Cell("1","12");
                        cellList.add(cell);
                    }
                    if (j==7){
                        cell=new Cell("1","12");
                        cellList.add(cell);
                    }
                    if (j==8){
                        cell=new Cell("1","12");
                        cellList.add(cell);
                    }
                }

            }
            list.add(cellList);
        }

        return list;
    }

    @DrawableRes
    public int getDrawable(int value, boolean isGender) {
        if (isGender) {
            return value == BOY ? mBoyDrawable : mGirlDrawable;
        } else {
            return value == SAD ? mSadDrawable : mHappyDrawable;
        }
    }

    @NonNull
    public List<List<Cell>> getCellList() {
        return getCellListForSortingTest();
    }

    @NonNull
    public List<RowHeader> getRowHeaderList() {
        return getSimpleRowHeaderList();
    }

    @NonNull
    public List<ColumnHeader> getColumnHeaderList() {
        return getRandomColumnHeaderList();
    }
}
