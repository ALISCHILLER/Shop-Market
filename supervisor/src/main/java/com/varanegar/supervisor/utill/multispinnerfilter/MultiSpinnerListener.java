package com.varanegar.supervisor.utill.multispinnerfilter;


import java.util.List;

public interface MultiSpinnerListener {
	void onItemsSelected(List<KeyPairBoolData> selectedItems);
	void onClear();
}
