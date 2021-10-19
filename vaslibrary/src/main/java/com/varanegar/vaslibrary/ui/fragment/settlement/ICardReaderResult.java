package com.varanegar.vaslibrary.ui.fragment.settlement;

import androidx.annotation.Nullable;

/**
 * Created by A.Torabi on 11/11/2018.
 */

public interface ICardReaderResult {
    void onSuccess(TransactionData td);

    void onFailure(@Nullable TransactionData td, String error);
}
