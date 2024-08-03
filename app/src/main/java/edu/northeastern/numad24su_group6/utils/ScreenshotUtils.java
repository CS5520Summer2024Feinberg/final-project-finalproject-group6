package edu.northeastern.numad24su_group6.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ScreenshotUtils {

    private static ScreenshotUtils instance;

    private ScreenshotUtils() {
        // Private constructor to prevent instantiation
    }

    public static ScreenshotUtils getInstance() {
        if (instance == null) {
            synchronized (ScreenshotUtils.class) {
                if (instance == null) {
                    instance = new ScreenshotUtils();
                }
            }
        }
        return instance;
    }

    public Bitmap takeScreenshot(Activity activity) {
        View rootView = activity.getWindow().getDecorView().getRootView();
        rootView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public Uri saveScreenshot(Context context, Bitmap bitmap) {
        File screenshotFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "screenshot_" + System.currentTimeMillis() + ".png");

        try (FileOutputStream fos = new FileOutputStream(screenshotFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return FileProvider.getUriForFile(context, context.getPackageName() + ".provider", screenshotFile);
    }

    public void shareScreenshot(Context context, Uri uri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(shareIntent, "Share Screenshot"));
    }
}
