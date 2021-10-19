package com.varanegar.vaslibrary.manager.image;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.image.LogoModel;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.imageapi.ImageApi;
import com.varanegar.vaslibrary.webapi.imageapi.LogoApi;

import java.util.List;
import java.util.Objects;

import okhttp3.Request;
import okhttp3.ResponseBody;
import timber.log.Timber;

/**
 * Created by e.hashemzadeh on 6/11/2019.
 */

public class LogoManager {
    public static final String ImagesFolder = "images";
    Context context;

    public LogoManager(@NonNull Context context) {
        this.context = context;
    }

    public void sync(@NonNull final UpdateCall updateCall) {
        context.getSharedPreferences("Logo", Context.MODE_PRIVATE).edit().clear().apply();
        LogoApi api = new LogoApi(context);
        api.runWebRequest(api.logoInfo(ImageType.Logo.toString()), new WebCallBack<List<LogoModel>>() {

            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(final List<LogoModel> result, Request request) {
                if (result!=null && result.size()>0) {
                    LogoManager logoManager = new LogoManager(context);
                    logoManager.downloadImage(result.get(0), new LogoDownloadCallBack() {
                        @Override
                        public void downloaded() {
                            Timber.d("Logo downloaded");
                        }

                        @Override
                        public void notFound() {
                            Timber.d("Logo notFound");
                        }

                        @Override
                        public void apiFailure() {
                            Timber.d("Logo apiFailure");
                        }

                        @Override
                        public void networkFailure() {
                            Timber.d("Logo networkFailure");
                        }

                        @Override
                        public void saveFailure() {
                            Timber.d("Logo saveFailure");
                        }

                        @Override
                        public void finished() {
                            saveLogoInfo(result.get(0));
                        }
                    });
                    Timber.d("Logo updated");
                    updateCall.success();
                } else {
                    Timber.d("Logo doesn't exist");
                    updateCall.success();
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, context);
                updateCall.failure(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t);
                updateCall.failure(context.getString(R.string.network_error));
            }
        });
    }

    public interface LogoDownloadCallBack {
        void downloaded();

        void notFound();

        void apiFailure();

        void networkFailure();

        void saveFailure();

        void finished();
    }

    public void downloadImage(final LogoModel logoModel, @Nullable final LogoDownloadCallBack callBack) {
        ImageApi api = new ImageApi(context);
        api.runWebRequest(api.downloadImage(logoModel.ImageType, logoModel.TokenId.toString(), logoModel.ImageName), new WebCallBack<ResponseBody>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(ResponseBody result, Request request) {
                try {
                    ImageManager imageManager = new ImageManager(context);
                    imageManager.writeResponseBodyToStorage(result, logoModel.ImageType, logoModel.TokenId.toString(), logoModel.ImageName);
                    if (callBack != null) {
                        callBack.downloaded();
                        callBack.finished();
                    }
                } catch (Exception e) {
                    if (callBack != null) {
                        callBack.saveFailure();
                        callBack.finished();
                    }
                    Timber.e(e);
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                if (error.getStatusCode() == 404) {
                    Timber.e("Logo " + request.url().toString() + " not found!");
                    if (callBack != null) {
                        callBack.notFound();
                        callBack.finished();
                    }
                } else {
                    WebApiErrorBody.log(error, context);
                    if (callBack != null) {
                        callBack.apiFailure();
                        callBack.finished();
                    }
                }
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                if (callBack != null) {
                    callBack.networkFailure();
                    callBack.finished();
                }
            }
        });
    }



    public void saveLogoInfo (LogoModel logoModel) {
        SharedPreferences.Editor sp = context.getSharedPreferences("Logo", Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String logoJson = gson.toJson(logoModel);
        sp.putString("Logo", logoJson);
        sp.apply();
    }

    public Bitmap getLogoBitmap () {
        Bitmap bitmap;
        String logoPath = getLogoPath();
        if (logoPath.equals(""))
            return null;
        else
            bitmap = BitmapFactory.decodeFile(logoPath);
        return bitmap;
    }

    public String getLogoPath () {
        String logoPath;

        SharedPreferences sp = context.getSharedPreferences("Logo", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String logoJson = sp.getString("Logo", "");
        LogoModel logoModel = gson.fromJson(logoJson, LogoModel.class);
        if (logoModel!=null) {
            String path = HelperMethods.getExternalFilesDir(context, ImagesFolder).getAbsolutePath();
            logoPath = path + "/" + logoModel.ImageType + "/" + logoModel.TokenId.toString() + "/" + logoModel.ImageName;
        } else
            logoPath="";

        return logoPath;
    }

}
