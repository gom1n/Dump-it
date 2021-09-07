package com.dumpit.ffff;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.provider.MediaStore;
import android.net.Uri;
import android.text.TextUtils;

public class MediaScanner {
    private Context contexts;
    private static MediaScanner mediascans = null;
    private MediaScannerConnection mediascannerconnects;

    private String filePath;

    public static MediaScanner getInstance(Context context) {
        if (null == context)
            return null;

        if (null == mediascans)
            mediascans = new MediaScanner(context);
        return mediascans;
    }
        public static void releaseInstance() {
            if(null != mediascans) {
                mediascans = null;
            }
        }

        private MediaScanner (Context context) {

        contexts = context;
        filePath = "";

        MediaScannerConnection.MediaScannerConnectionClient mediaclient;
        mediaclient = new MediaScannerConnection.MediaScannerConnectionClient() {
            public void onMediaScannerConnected() {
                mediascannerconnects.scanFile(filePath, null);
            }
            public void onScanCompleted(String paths, Uri uri) {
                System.out.println("미디어 스캔 완료");
                mediascannerconnects.disconnect();
            }
        };
        mediascannerconnects = new MediaScannerConnection(contexts, mediaclient);
        }

        public void mediaScanning(final String path) {
        if(TextUtils.isEmpty(path))
            return;
        filePath = path;

        if(!mediascannerconnects.isConnected())
            mediascannerconnects.connect();
        }


    }





