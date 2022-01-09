package com.varanegar.vaslibrary.util.multispinnerfilter;


import java.util.List;

public interface MultiSpinnerListener {
	void onItemsSelected(List<KeyPairBoolData> selectedItems);
	void onClear();
}
