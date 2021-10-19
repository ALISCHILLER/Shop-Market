package com.varanegar.vaslibrary.ui.fragment.settlement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

/**
 * Created by Elnaz on 6/7/2020.
 */

public interface IOnActivityResultListener {
    void onReceiveResult(Context context, int requestCode, int resultCode,@Nullable Intent data,@Nullable Bundle bundle);
}
