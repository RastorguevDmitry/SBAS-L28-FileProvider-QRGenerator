package com.rdi.shareqr.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.annotation.MainThread;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.rdi.shareqr.generator.Encoder;
import com.rdi.shareqr.generator.ErrorCorrectionLevel;
import com.rdi.shareqr.generator.QRCode;
import com.rdi.shareqr.generator.WriterException;

public class QrGenerator {
    public static final int SIZE = 1024;
    private static QrGenerator ourInstance;


    public static QrGenerator getInstance(File rootDir) {
        if (ourInstance == null) {
            ourInstance = new QrGenerator(rootDir);
        }
        return ourInstance;
    }


    private final File mRootDir;
    private final Map<String, QrLiveData> mDataMap = new HashMap<>();
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private QrGenerator(File rootDir) {
        mRootDir = rootDir;
    }

    @MainThread
    public QrLiveData generate(String text) {
        QrLiveData data = mDataMap.get(text);
        if (data == null) {
            data = new QrLiveData();
            mDataMap.put(text, data);
            mExecutor.submit(new QrGenerator.Generator(mRootDir, text, data));
        }
        return data;
    }

    private static class Generator implements Runnable {

        public static final String QR = "qr";
        public static final String PNG = ".png";
        private final File mRootDir;
        private final String mText;
        private final QrLiveData mQrLiveData;
        private final Paint mPaint = new Paint();

        private Generator(File rootDir, String text, QrLiveData qrLiveData) {
            mRootDir = rootDir;
            mText = text;
            mQrLiveData = qrLiveData;

            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.BLACK);
            mPaint.setAntiAlias(false);
        }

        @Override
        public void run() {
            Bitmap bitmap = Bitmap.createBitmap(SIZE, SIZE, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);

            try {
                QRCode qr = Encoder.encode(mText, ErrorCorrectionLevel.Q);

                int wSize = SIZE / qr.getMatrix().getWidth();
                int hSize = SIZE / qr.getMatrix().getHeight();

                canvas.drawColor(Color.WHITE);
                for (int w = 0; w < qr.getMatrix().getWidth(); w++) {
                    for (int h = 0; h < qr.getMatrix().getHeight(); h++) {
                        if (qr.getMatrix().get(w, h) > 0) {
                            int left = w * wSize;
                            int top = h * hSize;
                            int right = (w + 1) * wSize;
                            int bottom = (h + 1) * hSize;
                            canvas.drawRect(left, top, right, bottom, mPaint);
                        }
                    }
                }

                File file = nextFile();
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                        stream);
                stream.flush();
                stream.close();
                QrData result = new QrData(bitmap, file);
                mQrLiveData.postValue(result);

            } catch (WriterException | FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private File nextFile() {
            File file = new File(mRootDir, QR + PNG);
            int counter = 0;
            while (file.exists()) {
                counter++;
                file = new File(mRootDir, QR + counter + PNG);
            }
            return file;
        }
    }

}
