package com.transerve.numberplaterecognition.UI.MainScreen;

import android.graphics.Bitmap;

/**
 * Created by tushar
 * Created on 15/06/18.
 */

class MainPresenter implements MainContract.Presenter {
    private MainContract.View view;

    MainPresenter(MainContract.View view) {

        this.view = view;
    }

    @Override
    public void captureImage() {
        view.openCamera();
    }

    @Override
    public void detectTextInImage(Bitmap imageBitmap) {
        view.startTextDetection(imageBitmap);
    }

    @Override
    public void processImage(Bitmap selectedImageBitmap) {
        view.startTextDetection(selectedImageBitmap);
    }
}
