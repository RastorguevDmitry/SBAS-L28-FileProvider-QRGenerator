package com.rdi.shareqr.model;

import android.graphics.Bitmap;

import java.io.File;

public class QrData {

    private final Bitmap mQrBitmaop;
    private final File mFile;

    public QrData(Bitmap qrBitmaop, File file) {
        mQrBitmaop = qrBitmaop;
        mFile = file;
    }

    public Bitmap getQrBitmaop() {
        return mQrBitmaop;
    }

    public File getFile() {
        return mFile;
    }
}
