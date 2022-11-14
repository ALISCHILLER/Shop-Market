package com.varanegar.vaslibrary.manager.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.SystemClock;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.image.Image;
import com.varanegar.vaslibrary.model.image.ImageModel;
import com.varanegar.vaslibrary.model.image.ImageModelRepository;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.imageapi.ImageApi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by A.Torabi on 11/21/2018.
 */

public class ImageManager extends BaseManager<ImageModel> {
    public static final String ImagesFolder = "images";
    private Call<List<ImageModel>> call;

    public ImageManager(@NonNull Context context) {
        super(context, new ImageModelRepository());
    }

    public void sync(@NonNull final UpdateCall updateCall) {
        ImageApi api = new ImageApi(getContext());
        UserModel userModel = UserManager.readFromFile(getContext());
        call = api.pictureInfo("All", userModel.UniqueId.toString());
        api.runWebRequest(call, new WebCallBack<List<ImageModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<ImageModel> result, Request request) {
                try {
                    delete(Criteria.equals(Image.IsLocal, false));
                    if (result.size() > 0) {
                        insert(result);
                        Timber.d("List of images updated");
                        updateCall.success();
                    } else {
                        Timber.d("List of images was empty");
                        updateCall.success();
                    }
                } catch (DbException e) {
                    Timber.e(e, "List of images updated");
                    updateCall.failure(getContext().getString(R.string.error_deleting_old_data));
                } catch (ValidationException e) {
                    Timber.e(e, "List of images updated");
                    updateCall.failure(getContext().getString(R.string.data_error));
                }

            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                updateCall.failure(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t);
                updateCall.failure(getContext().getString(R.string.network_error));
            }
        });
    }

    public List<ImageModel> getItems(ImageType... types) {
        Query query = new Query().from(Image.ImageTbl);
        for (ImageType type :
                types) {
            query.whereOr(Criteria.equals(Image.ImageType, type.getName()));
        }
        return getItems(query);
    }

    /**
     * @param tokenId
     * @param imageType
     * @return returns the default image. If there is no default it will return the first image in finds.
     */
    public ImageModel getImage(UUID tokenId, ImageType imageType) {
        ImageModel imageModel = getItem(new Query().from(Image.ImageTbl).whereAnd(Criteria.equals(Image.TokenId, tokenId.toString())
                .and(Criteria.equals(Image.ImageType, imageType.getName())).and(Criteria.equals(Image.IsDefault, true))));
        if (imageModel == null)
            imageModel = getItem(new Query().from(Image.ImageTbl).whereAnd(Criteria.equals(Image.TokenId, tokenId.toString())
                    .and(Criteria.equals(Image.ImageType, imageType.getName()))));
        return imageModel;
    }

    public ImageModel getImage(UUID tokenId, String fileName, ImageType imageType) {
        return getItem(new Query().from(Image.ImageTbl).whereAnd(Criteria.equals(Image.TokenId, tokenId.toString())
                .and(Criteria.equals(Image.ImageType, imageType.getName())).and(Criteria.equals(Image.ImageFileName, fileName))));
    }

    @Nullable
    public String getImagePath(UUID tokenId, ImageType imageType) {
        if (tokenId == null)
            return null;
        ImageModel imageModel = getImage(tokenId, imageType);
        if (imageModel == null)
            return null;
        return getImagePath(imageModel);
    }

    @Nullable
    public String getImagePath(UUID tokenId, String fileName, ImageType imageType) {
        if (tokenId == null || fileName == null)
            return null;
        ImageModel imageModel = getImage(tokenId, fileName, imageType);
        if (imageModel == null)
            return null;
        return getImagePath(imageModel);
    }

    public String getImagePath(ImageModel imageModel) {
        if (imageModel == null)
            return null;
        String path = HelperMethods.getExternalFilesDir(getContext(), ImagesFolder).getAbsolutePath();
        path = path + "/" + imageModel.ImageType + "/" + imageModel.TokenId.toString() + "/" + imageModel.ImageFileName;
        return path;
    }

    public String saveImage(Bitmap bitmap, int quality, UUID tokenId, ImageType type,
                            String fileName, boolean isDefault) throws IOException,
            ValidationException, DbException {
        ImageModel imageModel = new ImageModel();
        imageModel.IsDefault = isDefault;
        imageModel.UniqueId = UUID.randomUUID();
        imageModel.ImageFileName = fileName;
        imageModel.TokenId = tokenId;
        imageModel.ImageType = type.getName();
        imageModel.IsLocal = true;

        String path = HelperMethods.getExternalFilesDir(getContext(), ImagesFolder).getAbsolutePath();
        File file = new File(path + "/" + type.getName() + "/");
        if (!file.exists())
            file.mkdirs();
        file = new File(path + "/" + type.getName() + "/" + tokenId);
        if (!file.exists())
            file.mkdirs();
        path = path + "/" + type.getName() + "/" + tokenId + "/" + fileName;

        FileOutputStream out = new FileOutputStream(path);
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
        if (getFileSize(path) <= 0) {
            Timber.e(encodeToBase64(bitmap, quality));
            File f = new File(path);
            if (f.exists())
                f.delete();
            throw new IOException(getContext().getString(R.string.error_in_saving_picture) + "\n" + getContext().getString(R.string.image_size_is_zero) + getContext().getString(R.string.please_try_again));
        } else {
            insertOrUpdate(imageModel);
        }
        return path;
    }

    public interface ImageDownloadCallBack {
        void downloaded(int count, int total);

        void notFound(int count, int total);

        void apiFailure(int count, int total);

        void networkFailure(int count, int total);

        void saveFailure(int count, int total);

        void total(int downloaded, int notFound, int apiFailure, int networkFailure, int saveFailure, int total);

        void finished(int total);
    }

    public void downloadImage(final ImageModel imageModel, @Nullable final ImageDownloadCallBack callBack) {
        ImageApi api = new ImageApi(getContext());
        api.runWebRequest(api.downloadImage(imageModel.ImageType, imageModel.TokenId.toString(), imageModel.ImageFileName), new WebCallBack<ResponseBody>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(ResponseBody result, Request request) {
                try {
                    writeResponseBodyToStorage(result, imageModel.ImageType, imageModel.TokenId.toString(), imageModel.ImageFileName);
                    if (callBack != null) {
                        callBack.downloaded(1, 1);
                        callBack.finished(1);
                    }
                } catch (Exception e) {
                    if (callBack != null) {
                        callBack.saveFailure(1, 1);
                        callBack.finished(1);
                    }
                    Timber.e(e);
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                if (error.getStatusCode() == 404) {
                    Timber.e("Album picture " + request.url().toString() + " not found!");
                    if (callBack != null) {
                        callBack.notFound(1, 1);
                        callBack.finished(1);
                    }
                } else {
                    WebApiErrorBody.log(error, getContext());
                    if (callBack != null) {
                        callBack.apiFailure(1, 1);
                        callBack.finished(1);
                    }
                }
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                if (callBack != null) {
                    callBack.networkFailure(1, 1);
                    callBack.finished(1);
                }
            }
        });
    }


    public void downloadImage(final List<ImageModel> images, final boolean downloadIfExists, @Nullable final ImageDownloadCallBack callBack) {
        if (images.size() == 0) {
            if (callBack != null) {
                callBack.finished(0);
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                final int[] streams = {0};
                final int total = images.size();
                final int[] downloaded = {0};
                final int[] notFound = {0};
                final int[] apiFailure = {0};
                final int[] networkFailure = {0};
                final int[] saveFailure = {0};
                int c = Runtime.getRuntime().availableProcessors() - 1;
                for (final ImageModel imageModel :
                        images) {
                    boolean skip = false;
                    if (!downloadIfExists) {
                        String imagePath = getImagePath(imageModel);
                        UpdateManager updateManager = new UpdateManager(getContext());
                        Date LastUpdate = updateManager.getImageInfoLog(imageModel.ImageFileName);
                        if (new File(imagePath).exists() && imageModel.LastUpdate != null && LastUpdate.after(imageModel.LastUpdate))
                            skip = true;
                    }
                    if (skip) {
                        downloaded[0]++;
                        if (callBack != null) {
                            callBack.downloaded(downloaded[0], total);
                            callBack.total(downloaded[0], notFound[0], apiFailure[0], networkFailure[0], saveFailure[0], total);
                            if (downloaded[0] + notFound[0] + apiFailure[0] + networkFailure[0] + saveFailure[0] == total)
                                callBack.finished(total);
                        }
                    } else {
                        while (streams[0] == c)
                            SystemClock.sleep(1000);
                        ImageApi api = new ImageApi(getContext());
                        streams[0]++;
                        api.runWebRequest(api.downloadImage(imageModel.ImageType, imageModel.TokenId.toString(), imageModel.ImageFileName), new WebCallBack<ResponseBody>() {
                            @Override
                            protected void onFinish() {
                                streams[0]--;
                            }

                            @Override
                            protected void onSuccess(ResponseBody result, Request request) {
                                try {
                                    UpdateManager updateManager = new UpdateManager(getContext());
                                    updateManager.saveImageInfoLog(imageModel.ImageFileName);
                                    writeResponseBodyToStorage(result, imageModel.ImageType, imageModel.TokenId.toString(), imageModel.ImageFileName);
                                    downloaded[0]++;
                                    if (callBack != null) {
                                        callBack.downloaded(downloaded[0], total);
                                        callBack.total(downloaded[0], notFound[0], apiFailure[0], networkFailure[0], saveFailure[0], total);
                                        if (downloaded[0] + notFound[0] + apiFailure[0] + networkFailure[0] + saveFailure[0] == total)
                                            callBack.finished(total);
                                    }
                                } catch (Exception e) {
                                    saveFailure[0]++;
                                    if (callBack != null) {
                                        callBack.saveFailure(saveFailure[0], total);
                                        callBack.total(downloaded[0], notFound[0], apiFailure[0], networkFailure[0], saveFailure[0], total);
                                        if (downloaded[0] + notFound[0] + apiFailure[0] + networkFailure[0] + saveFailure[0] == total)
                                            callBack.finished(total);
                                    }
                                    Timber.e(e);
                                }
                            }

                            @Override
                            protected void onApiFailure(ApiError error, Request request) {
                                if (error.getStatusCode() == 404) {
                                    Timber.e("Album picture " + request.url().toString() + " not found!");
                                    notFound[0]++;
                                    if (callBack != null) {
                                        callBack.notFound(notFound[0], total);
                                        callBack.total(downloaded[0], notFound[0], apiFailure[0], networkFailure[0], saveFailure[0], total);
                                        if (downloaded[0] + notFound[0] + apiFailure[0] + networkFailure[0] + saveFailure[0] == total)
                                            callBack.finished(total);
                                    }
                                } else {
                                    WebApiErrorBody.log(error, getContext());
                                    apiFailure[0]++;
                                    if (callBack != null) {
                                        callBack.apiFailure(apiFailure[0], total);
                                        callBack.total(downloaded[0], notFound[0], apiFailure[0], networkFailure[0], saveFailure[0], total);
                                        if (downloaded[0] + notFound[0] + apiFailure[0] + networkFailure[0] + saveFailure[0] == total)
                                            callBack.finished(total);
                                    }
                                }
                            }

                            @Override
                            protected void onNetworkFailure(Throwable t, Request request) {
                                networkFailure[0]++;
                                if (callBack != null) {
                                    callBack.networkFailure(networkFailure[0], total);
                                    callBack.total(downloaded[0], notFound[0], apiFailure[0], networkFailure[0], saveFailure[0], total);
                                    if (downloaded[0] + notFound[0] + apiFailure[0] + networkFailure[0] + saveFailure[0] == total)
                                        callBack.finished(total);
                                }
                            }
                        });
                    }
                }
            }
        }).start();
    }


    protected void writeResponseBodyToStorage(ResponseBody body, String type, String tokenId, String fileName) throws Exception {
        byte[] buffer = new byte[4096];
        InputStream inputStream = body.byteStream();
        String path = HelperMethods.getExternalFilesDir(getContext(), ImagesFolder).getAbsolutePath();
        File file = new File(path + "/" + type + "/");
        if (!file.exists())
            file.mkdirs();
        file = new File(path + "/" + type + "/" + tokenId);
        if (!file.exists())
            file.mkdirs();
        path = path + "/" + type + "/" + tokenId + "/" + fileName;
        OutputStream outputStream = new FileOutputStream(path);
        while (true) {
            int read = inputStream.read(buffer);
            if (read == -1) {
                break;
            }
            outputStream.write(buffer, 0, read);
        }
        outputStream.flush();
    }

    public static void copy(File src, File dst) throws Exception {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    public static int getFileSize(String path) {
        File file = new File(path);
        return Integer.parseInt(String.valueOf(file.length() / 1024));
    }

    public static Bitmap scale(Bitmap bitmapImage, int dstWidth) {
        int nh = (int) (bitmapImage.getHeight() * (((float) dstWidth) / bitmapImage.getWidth()));
        return Bitmap.createScaledBitmap(bitmapImage, dstWidth, nh, true);
    }

    public static void compress(String path, int quality, int dstWidth) throws Exception {
        try {
            copy(new File(path), new File(path + ".original"));
            byte[] buffer = new byte[4096];
            Bitmap bmp = BitmapFactory.decodeFile(path + ".original");
            Bitmap scaled = scale(bmp, dstWidth);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            scaled.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            InputStream in = new ByteArrayInputStream(bos.toByteArray());
            OutputStream outputStream = new FileOutputStream(path);
            while (true) {
                int read = in.read(buffer);
                if (read == -1) {
                    break;
                }
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            ExifInterface oldExif = new ExifInterface(path + ".original");
            String exifOrientation = oldExif.getAttribute(ExifInterface.TAG_ORIENTATION);
            if (exifOrientation != null) {
                ExifInterface newExif = new ExifInterface(path);
                newExif.setAttribute(ExifInterface.TAG_ORIENTATION, exifOrientation);
                newExif.saveAttributes();
            }
        } catch (OutOfMemoryError e) {
            Timber.e("Failed to compress file. Path is " + path + " and quality is " + quality);
            Timber.e(e);
        }
    }

    public boolean deleteImage(UUID tokenId, String fileName, ImageType imageType) throws DbException {
        ImageModel imageModel = getImage(tokenId, fileName, imageType);
        if (imageModel == null || imageModel.UniqueId == null)
            return false;
        String filePath = getImagePath(imageModel);
        File f = new File(filePath);
        if (!f.exists())
            return false;
        boolean deleted = f.delete();
        if (deleted && imageModel.IsLocal)
            delete(imageModel.UniqueId);
        return deleted;
    }

    public boolean deleteImage(ImageModel imageModel) throws DbException {
        if (imageModel == null || imageModel.UniqueId == null)
            return false;
        String filePath = getImagePath(imageModel);
        File f = new File(filePath);
        if (!f.exists())
            return false;
        boolean deleted = f.delete();
        if (deleted && imageModel.IsLocal)
            delete(imageModel.UniqueId);
        return deleted;
    }

    public static void deleteAllImages(Context context) {
        String path = HelperMethods.getExternalFilesDir(context, ImagesFolder).getAbsolutePath();
        File imagesFolder = new File(path);
        if (imagesFolder.exists())
            deleteRecursive(imagesFolder);
    }

    private static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    private static int getFileCount(String fileOrDirectory) {
        int count = 0;
        File f = new File(fileOrDirectory);
        if (f.isDirectory()) {
            for (File child : f.listFiles())
                count = count + getFileCount(child.getAbsolutePath());
            return count;
        } else if (f.exists())
            return 1;
        else return 0;
    }

    public int[] getFileCount(ImageType... imageTypes) {
        if (imageTypes.length == 0) {
            int[] counts = new int[1];
            counts[0] = getFileCount(HelperMethods.getExternalFilesDir(getContext(), ImageManager.ImagesFolder).getPath());
            return counts;
        }
        int[] counts = new int[imageTypes.length];
        for (int i = 0; i < counts.length; i++) {
            ImageType imageType = imageTypes[i];
            counts[i] = getFileCount(HelperMethods.getExternalFilesDir(getContext(), ImageManager.ImagesFolder).getPath() + "/" + imageType.getName());
        }
        return counts;
    }

    public void cancelSync() {
        if (call != null && !call.isCanceled() && call.isExecuted())
            call.cancel();
    }

    public static String encodeToBase64(Bitmap bitmap, int quality ) {
        Bitmap image = bitmap;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, quality, out);
        byte[] b = out.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
}
