package com.transerve.numberplaterecognition.UI.MainScreen;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
import com.transerve.numberplaterecognition.R;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    private ImageView imageMain;
    private MainContract.Presenter presenter;
    private Bitmap selectedImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new MainPresenter(this);
        imageMain = findViewById(R.id.main_image);

    }

    public void OnClickCaptureImage(View view) {

        presenter.captureImage();
    }

    public void OnClickReadImage(View view) {
        if (selectedImageBitmap == null) {
            showMessage("Image is not captured yet");
        } else {
            presenter.processImage(selectedImageBitmap);
        }
    }


    @Override
    public void openCamera() {
        ((TextView) findViewById(R.id.main_text_vehicle_number)).setText("");
        startActivityForResult(new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE), 0);
    }

    @Override
    public void startTextDetection(Bitmap imageBitmap) {

        FirebaseApp.initializeApp(MainActivity.this);

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);

        FirebaseVisionTextDetector detector = FirebaseVision.getInstance()
                .getVisionTextDetector();

        Task<FirebaseVisionText> result =
                detector.detectInImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                readText(firebaseVisionText);
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        showError("Text Read Failed with Error" + e.getLocalizedMessage());
                                    }
                                });


    }


    private void readText(FirebaseVisionText firebaseVisionText) {

        for (FirebaseVisionText.Block block : firebaseVisionText.getBlocks()) {
            String text = block.getText();
            Log.i("NumberPlate", text);
            ((TextView) findViewById(R.id.main_text_vehicle_number)).setText(text);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (data.getExtras() == null) {
                Toast.makeText(this, "Error in capturing Image", Toast.LENGTH_SHORT).show();
                return;
            }

            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");

            if (imageBitmap == null) {
                Toast.makeText(this, "Error in capturing Image", Toast.LENGTH_SHORT).show();
                return;
            }

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(imageBitmap, 300, 300, true);
            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            imageMain.setImageBitmap(rotatedBitmap);
            this.selectedImageBitmap = rotatedBitmap;

            presenter.detectTextInImage(imageBitmap);
        }
    }

    // region

    private void showError(String errorMessage) {
        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void showMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
    // endregion
}
