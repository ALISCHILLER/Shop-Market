package com.varanegar.vaslibrary.ui.fragment;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.questionnaire.EPollResultViewModel;
import com.varanegar.vaslibrary.manager.questionnaire.QuestionnaireAnswerManager;
import com.varanegar.vaslibrary.manager.questionnaire.QuestionnaireCustomerViewManager;
import com.varanegar.vaslibrary.manager.questionnaire.QuestionnaireLineOptionManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireCustomerViewModel;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireDemandType;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireLineOptionModel;
import com.varanegar.vaslibrary.model.user.UserModel;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

public class EPollFragment extends VisitFragment {
    private static final int PERMISSION_REQUEST_CODE = 12213;
    private final static int FILE_CHOOSER_RESULT_CODE = 2;
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private UUID customerId;
    private UUID questionnaireId;
    private UUID questionId;
    public String ePollSFormURL;
    public String queryString;
    public String url;
    WebView webViewShowPoll;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private UUID lineId;

    public void setArguments(UUID customerId, UUID questionnaireId, UUID questionId) {
        addArgument("0c91c445-4c34-4162-a6b5-17ff2ec1644b", customerId.toString());
        addArgument("79403496-ce59-4533-931e-e983eee6f75d", questionnaireId.toString());
        addArgument("42e8af20-c095-4b5b-9189-86a09c5d8d97", questionId.toString());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            UserModel userModel = UserManager.readFromFile(getContext());
            customerId = UUID.fromString(getArguments().getString("0c91c445-4c34-4162-a6b5-17ff2ec1644b"));
            CustomerModel customerModel = new CustomerManager(getContext()).getItem(customerId);
            questionnaireId = UUID.fromString(getArguments().getString("79403496-ce59-4533-931e-e983eee6f75d"));
            questionId = UUID.fromString(getArguments().getString("42e8af20-c095-4b5b-9189-86a09c5d8d97"));
            QuestionnaireLineOptionManager questionnaireLineOptionManager = new QuestionnaireLineOptionManager(getContext());
            List<QuestionnaireLineOptionModel> lineModels = questionnaireLineOptionManager.getQuestionnaireLineOptions(questionId);
            QuestionnaireLineOptionModel lineOptionModel = lineModels.get(0);
            lineId = lineOptionModel.QuestionnaireLineUniqueId;
            if (lineOptionModel.Title.startsWith("\""))
                lineOptionModel.Title = lineOptionModel.Title.substring(1);
            ePollSFormURL = lineOptionModel.Title;
            queryString = "&customerCode=" + customerModel.CustomerCode
                    + "&customerName=" + customerModel.CustomerName
                    + "&customerAddress=" + customerModel.Address
                    + "&customerTel=" + customerModel.Phone
                    + "&customerMobile=" + customerModel.Mobile
                    + "&visitorName=" + userModel.UserName
                    + "&VisitorId=" + userModel.BackOfficeId
                    + "&Param1=" + new TourManager(getContext()).loadTour().TourNo  //Param1 of 5 or QuestionNumber
                    + "&Param2=" + questionnaireId  //Param2 of 5 or QuestionNumber
                    + "&Param3=" + customerModel.CustomerCode  //Param3 of 5 or QuestionNumber
                    + "&Param4=" + userModel.BackOfficeId;  //Param4 of 5 or QuestionNumber
            url = ePollSFormURL + "?AndroidWebView=true" + queryString;
        } catch (Exception ex) {
            Timber.e(ex);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_REQUEST_CODE)
            setup();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                    super.onActivityResult(requestCode, resultCode, data);
                    return;
                }
                Uri[] results = null;
                if (resultCode == RESULT_OK) {
                    if (data == null) {
                        if (mCameraPhotoPath != null) {
                            results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                        }
                    } else {
                        String dataString = data.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }

                String filePath = "";
                if (results != null) {

                    filePath = getPath6(getActivity(), results[0]);
                }

                if (!filePath.equals("")) {
                    String newPath = filePath;
                    Uri newEesult = Uri.fromFile(new File(newPath));

                    results[0] = newEesult;
                    mFilePathCallback.onReceiveValue(results);
                } else {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = null;
            } catch (Exception ex) {
                mFilePathCallback.onReceiveValue(null);
                mFilePathCallback = null;
            }
        }
        /*else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            try {
                if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                    super.onActivityResult(requestCode, resultCode, data);
                    return;
                }
                if (requestCode == FILECHOOSER_RESULTCODE) {
                    if (null == this.mUploadMessage) {
                        return;
                    }
                    Uri result = null;
                    try {
                        if (resultCode != RESULT_OK) {
                            result = null;
                        } else {
                            result = data == null ? mCapturedImageURI : data.getData();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "activity :" + e,
                                Toast.LENGTH_LONG).show();
                    }

                    String filePath = "";

                    try {

                        filePath = result.getPath();
                        String wholeID = DocumentsContract.getDocumentId(result);
                        String id = wholeID.split(":")[1];
                        String[] column = {MediaStore.Images.Media.DATA};
                        String sel = MediaStore.Images.Media._ID + "=?";
                        Cursor cursor = getContentResolver().
                                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        column, sel, new String[]{id}, null);

                        int columnIndex = cursor.getColumnIndex(column[0]);
                        if (cursor.moveToFirst()) {
                            filePath = cursor.getString(columnIndex);
                        }
                        cursor.close();
                    } catch (Exception ex) {
                    }
                    if (!filePath.equals("")) {
                        String newPath = filePath;
                        Uri newEesult = Uri.fromFile(new File(newPath));
                        mUploadMessage.onReceiveValue(newEesult);
                    } else {
                        mUploadMessage.onReceiveValue(null);
                    }
                    mUploadMessage = null;
                }
            } catch (Exception ex) {
                mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
            }
        }*/
    }

    public static String getPath6(final Context context, final Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } catch (Exception ex) {
            Timber.e("image", ex.toString());
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_epoll_layout, container, false);
        webViewShowPoll = view.findViewById(R.id.entryWebView);
        setup();
        return view;
    }

    private void setup() {
        boolean hasPermission = (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED);

        if (hasPermission)
            hasPermission = (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);

        if (hasPermission)
            hasPermission = (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);

        if (hasPermission)
            hasPermission = (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

        if (!hasPermission) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_CODE);
            return;
        }


        if (hasPermission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webViewShowPoll.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else {
                webViewShowPoll.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
            webViewShowPoll.clearCache(false);
            webViewShowPoll.getSettings().setBuiltInZoomControls(true);
            webViewShowPoll.getSettings().setSupportZoom(true);
            webViewShowPoll.getSettings().setDisplayZoomControls(false);
            webViewShowPoll.getSettings().setSupportMultipleWindows(false);
            webViewShowPoll.getSettings().setAllowFileAccess(true);
            webViewShowPoll.getSettings().setAppCacheEnabled(true);
            webViewShowPoll.getSettings().setDatabaseEnabled(true);
            webViewShowPoll.getSettings().setDomStorageEnabled(true);
            webViewShowPoll.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            webViewShowPoll.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webViewShowPoll.getSettings().setJavaScriptEnabled(true);
            webViewShowPoll.getSettings().setGeolocationEnabled(true);
            webViewShowPoll.getSettings().setGeolocationDatabasePath(getContext().getFilesDir().getPath());
            webViewShowPoll.getSettings().setDefaultTextEncodingName("utf-8");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                webViewShowPoll.getSettings().setAllowFileAccessFromFileURLs(true);
                webViewShowPoll.getSettings().setAllowUniversalAccessFromFileURLs(true);
            }

            webViewShowPoll.setWebChromeClient(new WebChromeClient() {
                // 5.0+ Devices
                public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (mFilePathCallback != null) {
                            mFilePathCallback.onReceiveValue(null);
                        }
                        mFilePathCallback = filePathCallback;
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                                takePictureIntent.putExtra("PhotoPath", photoFile.getAbsolutePath());
                            } catch (IOException ex) {
                                Timber.e("Unable to create Image File", ex);
                            }
                            if (photoFile != null) {
                                mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                                Uri contentUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName(), photoFile);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                        contentUri);
                            } else {
                                takePictureIntent = null;
                            }
                        }

                        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                        contentSelectionIntent.setType("image/*");
                        contentSelectionIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        Intent[] intentArray;

                        if (takePictureIntent != null) {
                            intentArray = new Intent[]{contentSelectionIntent};
                        } else {
                            intentArray = new Intent[0];
                        }
                        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        boolean onlyImage = fileChooserParams.isCaptureEnabled();
                        try {
                            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                            chooserIntent.putExtra(Intent.EXTRA_INTENT, takePictureIntent);
                            chooserIntent.putExtra(Intent.EXTRA_TITLE, "");
                            if (!onlyImage) {
                                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                            }
                            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
                        } catch (Exception ex) {
                            Timber.e("Unable to call Cammera", ex);
                        }
                        return true;
                    } else {
                        // before version 24
                        if (mFilePathCallback != null) {
                            mFilePathCallback.onReceiveValue(null);
                        }
                        mFilePathCallback = filePathCallback;
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                                takePictureIntent.putExtra("PhotoPath", photoFile.getAbsolutePath());
                            } catch (IOException ex) {
                                Timber.e("Unable to create Image File", ex);
                            }
                            if (photoFile != null) {
                                mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(photoFile));
                            } else {
                                takePictureIntent = null;
                            }
                        }

                        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                        contentSelectionIntent.setType("image/*");
                        Intent[] intentArray;

                        if (takePictureIntent != null) {
                            intentArray = new Intent[]{contentSelectionIntent};
                        } else {
                            intentArray = new Intent[0];
                        }

                        boolean onlyImage = false;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            onlyImage = fileChooserParams.isCaptureEnabled();
                        }

                        try {
                            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                            chooserIntent.putExtra(Intent.EXTRA_INTENT, takePictureIntent);
                            chooserIntent.putExtra(Intent.EXTRA_TITLE, "");
                            if (!onlyImage) {
                                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                            }
                            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
                        } catch (Exception ex) {
                            Timber.e("Unable to call Camera", ex);
                        }
                        return true;
                    }
                }

                @Override
                public void onPermissionRequest(final PermissionRequest request) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        try {
                            request.grant(request.getResources());
                        } catch (Exception e) {
                            Timber.e(e);
                        }
                }

                public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                    callback.invoke(origin, true, false);
                }

            });
            webViewShowPoll.addJavascriptInterface(new JavaScriptInterface(getContext()), "Android");
            try {
                webViewShowPoll.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                        super.onReceivedSslError(view, handler, error);
                    }

                    @Override
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        super.onReceivedError(view, request, error);
                    }

                    @Override
                    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                        super.onReceivedHttpError(view, request, errorResponse);
                    }

                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        super.onReceivedError(view, errorCode, description, failingUrl);
                        Timber.e(description);
                    }
                });
                webViewShowPoll.loadUrl(url);
            } catch (Throwable e) {
                Timber.e(e);
            }
        }
    }

    private void trySave(int formId, int answerId, boolean submit) {
        QuestionnaireAnswerManager questionnaireAnswerManager = new QuestionnaireAnswerManager(getContext());
        try {
            questionnaireAnswerManager.saveEPollAnswer(lineId, customerId, questionnaireId, new EPollResultViewModel(formId, answerId));
            List<QuestionnaireCustomerViewModel> questionnaires = new QuestionnaireCustomerViewManager(getContext()).getQuestionnaires(customerId, false);
            if (questionnaires.size() == 0 && submit) {
                saveCallAndExit();
            } else {
                boolean exist = Linq.exists(questionnaires, new Linq.Criteria<QuestionnaireCustomerViewModel>() {
                    @Override
                    public boolean run(QuestionnaireCustomerViewModel item) {
                        return item.DemandType == QuestionnaireDemandType.Mandatory;
                    }
                });
                if (exist) {
                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                    dialog.setIcon(Icon.Success);
                    dialog.setMessage(R.string.questionnaire_was_saved);
                    dialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getVaranegarActvity().popFragment();
                        }
                    });
                    dialog.show();
                } else if (submit) {
                    saveCallAndExit();
                }
            }
        } catch (Exception ex) {
            getVaranegarActvity().showSnackBar(R.string.error_saving_request, MainVaranegarActivity.Duration.Short);
        }
    }

    private void saveCallAndExit() {
        CustomerCallManager callManager = new CustomerCallManager(getContext());
        try {
            callManager.saveQuestionnaireCall(customerId);
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setIcon(Icon.Success);
            dialog.setMessage(R.string.all_questionnaires_finished);
            dialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getVaranegarActvity().popFragment();
                }
            });
            dialog.show();
        } catch (Exception e) {
            Timber.e(e);
            getVaranegarActvity().showSnackBar(R.string.error_saving_request, MainVaranegarActivity.Duration.Short);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        File imageStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES)
                , "ePollImages");

        if (!imageStorageDir.exists()) {
            imageStorageDir.mkdirs();
        }

        String imageFileName = "IMG_";

        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                imageStorageDir      /* directory */
        );
        return imageFile;
    }

    @NonNull
    @Override
    protected UUID getCustomerId() {
        return customerId;
    }


    public class JavaScriptInterface {

        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        public JavaScriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public boolean Submit(int formid, int answerId) {
            Timber.d("form id = " + formid);
            trySave(formid, answerId, true);
            return true;
        }


        @JavascriptInterface
        public boolean AutoSave(int formid, int answerId) {
            Timber.d("form id = " + formid);
            trySave(formid, answerId, false);
            return true;
        }

    }
}
