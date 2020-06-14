package com.projeto.biblianvi.BibliaVersoes;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask {

    private static final String TAG = "Download Task";
    private Context context;
    private String downloadUrl = "https://raw.githubusercontent.com/EzequielRibeiro/link/master/version/XXX",
            downloadFileName;
    private String packageName;
    private SharedPreferences sharedPref;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private FrameLayout frameLayout;
    private LinearLayout linearLayout;
    private String versionName;
    private TextView textViewTextBibleVersion;

    public DownloadTask(TextView textViewTextBibleVersion,String version, Context context, ProgressDialog progressDialog, SharedPreferences sharedPref) {
        packageName = context.getPackageName();
        this.sharedPref = sharedPref;
        this.progressDialog = progressDialog;
        String[] versionsBible = context.getResources().getStringArray(R.array.versoes);
        versionName = versionsBible[Integer.valueOf(version)];
        this.textViewTextBibleVersion = textViewTextBibleVersion;
        runTask(version);
    }


    public DownloadTask(TextView textViewTextBibleVersion,String version,Context context, ProgressBar progressBar,
                        FrameLayout frameLayout, LinearLayout linearLayout, SharedPreferences sharedPref) {
        packageName = context.getPackageName();
        this.sharedPref = sharedPref;
        this.progressBar = progressBar;
        this.frameLayout = frameLayout;
        this.linearLayout = linearLayout;
        String[] versionsBible = context.getResources().getStringArray(R.array.versoes);
        versionName = versionsBible[Integer.valueOf(version)];
        this.textViewTextBibleVersion = textViewTextBibleVersion;
        runTask(version);

    }

    private void runTask(String version) {

        switch (version) {
            case "0":
                downloadUrl = downloadUrl.replace("XXX", Utils.ACF_ZIP);
                downloadFileName = Utils.ACF_ZIP;
                break;
            case "1":
                downloadUrl = downloadUrl.replace("XXX", Utils.ARA_ZIP);
                downloadFileName = Utils.ARA_ZIP;
                break;
            case "2":
                downloadUrl = downloadUrl.replace("XXX", Utils.ARC_ZIP);
                downloadFileName = Utils.ARC_ZIP;
                break;
            case "3":
                downloadUrl = downloadUrl.replace("XXX", Utils.AS21_ZIP);
                downloadFileName = Utils.AS21_ZIP;
                break;
            case "4":
                downloadUrl = downloadUrl.replace("XXX", Utils.JFAA_ZIP);
                downloadFileName = Utils.JFAA_ZIP;
                break;
            case "5":
                downloadUrl = downloadUrl.replace("XXX", Utils.KJA_ZIP);
                downloadFileName = Utils.KJA_ZIP;
                break;
            case "6":
                downloadUrl = downloadUrl.replace("XXX", Utils.KJF_ZIP);
                downloadFileName = Utils.KJF_ZIP;
                break;
            case "7":
                downloadUrl = downloadUrl.replace("XXX", Utils.NAA_ZIP);
                downloadFileName = Utils.NAA_ZIP;
                break;
            case "8":
                downloadUrl = downloadUrl.replace("XXX", Utils.NBV_ZIP);
                downloadFileName = Utils.NBV_ZIP;
                break;
            case "9":
                downloadUrl = downloadUrl.replace("XXX", Utils.NTLH_ZIP);
                downloadFileName = Utils.NTLH_ZIP;
                break;
            case "10":
                downloadUrl = downloadUrl.replace("XXX", Utils.NVI_ZIP);
                downloadFileName = Utils.NVI_ZIP;
                break;
            case "11":
                downloadUrl = downloadUrl.replace("XXX", Utils.NVT_ZIP);
                downloadFileName = Utils.NVT_ZIP;
                break;
            case "12":
                downloadUrl = downloadUrl.replace("XXX", Utils.TB_ZIP);
                downloadFileName = Utils.TB_ZIP;
                break;

        }

        Log.e(TAG, downloadFileName);
        Log.e(TAG, downloadUrl);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            new DownloadingTask(version,progressBar, sharedPref).execute(packageName);
        } else {
            new DownloadingTask(version,progressDialog, sharedPref).execute(packageName);
        }


    }

    private class DownloadingTask extends AsyncTask<String, Context, Void> {

        File folderStorage = null;
        File outputFile = null;
        String packageName;
        ProgressDialog progressDialog;
        ProgressBar progressBar;
        SharedPreferences sharedPref;
        String version;

        public DownloadingTask(String version,ProgressBar progressBar, SharedPreferences sharedPref) {
            this.sharedPref = sharedPref;
            this.progressBar = progressBar;
            this.version = version;

        }

        public DownloadingTask(String version,ProgressDialog progressDialog ,SharedPreferences sharedPref){

            this.sharedPref = sharedPref;
            this.progressDialog = progressDialog;
            this.version = version;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {

            try {
                if (outputFile != null) {
                    Log.e(TAG, "Download Completed and Unzip");
                    Log.e(TAG,"sharedPrefPatch: " + sharedPref.getString("dataBasePatch","invalid"));
                    sharedPref.edit().putString("version",version).commit();
                    sharedPref.edit().putString("versionName",versionName).commit();
                    textViewTextBibleVersion.setText(versionName);

                } else {
                    //If download failed change button text
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Change button text again after 3sec
                        }
                    }, 3000);

                    Toast.makeText(context,"failed to download",Toast.LENGTH_LONG).show();

                }
            } catch (Exception e) {
                e.printStackTrace();
                //Change button text if exception occurs

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //download again enable button
                    }
                }, 3000);
                Log.e(TAG, "Download Failed with Exception - " + e.getLocalizedMessage());

            }

            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(String... arg0) {

            packageName = arg0[0];
            //Get File if SD card is present
            if (new CheckForSDCard().isSDCardPresent()) {

                folderStorage = new File(
                        Environment.getExternalStorageDirectory() + "/"
                                + Utils.DOWNLOAD_FOLDER_NAME);

                //If File is not present create directory
                if (!folderStorage.exists()) {
                    folderStorage.mkdir();
                    Log.e(TAG, "Directory Created.");
                }

            } else {

                folderStorage = new File(
                        Environment.getDataDirectory() + "/"
                                + Utils.DOWNLOAD_FOLDER_NAME);

                //If File is not present create directory
                if (!folderStorage.exists()) {
                    folderStorage.mkdir();
                    Log.e(TAG, "Directory Created.");
                }
            }

            outputFile = new File(folderStorage, downloadFileName);//Create Output file in Main File

            //Create New File if not present
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e(TAG, "File Created");

            }
            Log.e(TAG, outputFile.getAbsolutePath());

            InputStream inputStream = null;
            FileOutputStream outputStream = null;
            try {

                OkHttpClient  client = new OkHttpClient().newBuilder()
                        .readTimeout(60, TimeUnit.SECONDS) //set the read timeout
                        .connectTimeout(60, TimeUnit.SECONDS) //set the connect timeout
                        .build();

                Request request = new Request.Builder().url(downloadUrl).
                        build();
                Response response = client.newCall(request).execute();

                if (!response.isSuccessful()) {

                    Thread thread = new Thread(){
                        public void run(){
                            Looper.prepare();//Call looper.prepare()

                            Handler mHandler = new Handler() {
                                public void handleMessage(Message msg) {
                                    progressDialog.setMessage("Failed to download file Bible");

                                }
                            };
                            Looper.loop();
                        }
                    };
                    thread.start();
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    if (progressDialog != null) progressDialog.dismiss();
                    if (frameLayout != null && linearLayout != null)
                        frameLayout.removeView(linearLayout);

                    Toast.makeText(context, R.string.download_fail, Toast.LENGTH_LONG).show();
                   throw new IOException("Failed to download file: " + response);
                }else{
                    Log.e(TAG, "input:" + response.body().contentLength());
                    Log.e(TAG, "response msg: " + response.message());

                }

                inputStream = response.body().byteStream();

                outputStream = new FileOutputStream(outputFile);
                int totalCount = (int) response.body().contentLength();
                Log.e(TAG, "totalCount: " + totalCount);
                byte[] buffer = new byte[2 * 1024];
                int len;
                int readLen = 0;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                    readLen += len;
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                progressBar.setProgress((readLen * 100) / totalCount, true);
                            } else {
                                progressDialog.setProgress((readLen * 100) / totalCount);
                            }
                        }catch (ArithmeticException a){
                            Log.e(TAG,a.getMessage());
                        }
                }


            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (outputFile != null) {
                Log.e(TAG, "Unzip Started: " + outputFile.getAbsolutePath());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    new UnZip(outputFile.getAbsolutePath(), packageName, sharedPref, progressBar, frameLayout, linearLayout);
                } else {
                    new UnZip(outputFile.getAbsolutePath(), packageName, sharedPref, progressDialog);
                }

            }


            return null;
        }
    }

    //sse_es.zip  sse_es.db3
    public static class Utils {

        public static final String ACF_ZIP = "ACF.zip";
        public static final String NAME_ACF = "ACF.sqlite";

        public static final String ARA_ZIP = "ARA.zip";
        public static final String NAME_ARA = "ARA.sqlite";

        public static final String ARC_ZIP = "ARC.zip";
        public static final String NAME_ARC = "ARC.sqlite";

        public static final String AS21_ZIP = "AS21.zip";
        public static final String NAME_AS21 = "AS21.sqlite";

        public static final String JFAA_ZIP = "JFAA.zip";
        public static final String NAME_JFAA = "JFAA.sqlite";

        public static final String KJA_ZIP = "KJA.zip";
        public static final String NAME_KJA = "KJA.sqlite";

        public static final String KJF_ZIP = "KJF.zip";
        public static final String NAME_KJF = "KJF.sqlite";

        public static final String NAA_ZIP = "NAA.zip";
        public static final String NAME_NAA = "NAA.sqlite";

        public static final String NBV_ZIP = "NBV.zip";
        public static final String NAME_NBV = "NBV.sqlite";

        public static final String NTLH_ZIP = "NTLH.zip";
        public static final String NAME_NTLH = "NTLH.sqlite";

        public static final String NVI_ZIP = "NVI.zip";
        public static final String NAME_NVI = "NVI.sqlite";

        public static final String NVT_ZIP = "NVT.zip";
        public static final String NAME_NVT = "NVT.sqlite";

        public static final String TB_ZIP = "TB.zip";
        public static final String NAME_TB = "TB.sqlite";

        public static final String DOWNLOAD_FOLDER_NAME = "Download";


    }

    public static class CheckForSDCard {
        //Check If SD Card is present or not method
        public boolean isSDCardPresent() {
            return Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED);
        }
    }


}
