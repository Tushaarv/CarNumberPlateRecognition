package com.transerve.numberplaterecognition.UI.MainScreen;

import android.graphics.Bitmap;

/**
 * Created by tushar
 * Created on 15/06/18.
 */

public interface MainContract {

    interface View {

        void openCamera();

        void startTextDetection(Bitmap imageBitmap);
    }

    interface Presenter {

        void captureImage();

        void detectTextInImage(Bitmap imageBitmap);

        void processImage(Bitmap selectedImageBitmap);
    }
}
