package com.royalaviation.persona;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class uploadImage extends AppCompatActivity {

    private Button btnGallery, btnDetect, btnBack;
    private ImageView ivUserImage;
    private TextView tvText;
    private static final int GalleryPick = 1;
    private Uri ImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        getSupportActionBar().hide();
        btnGallery = findViewById(R.id.btnGallery);
        btnDetect = findViewById(R.id.btnDetect);
        btnBack = findViewById(R.id.btnSave);
        ivUserImage = findViewById(R.id.ivUpload);
        tvText = findViewById(R.id.tvResult);

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        btnDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectText();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateText();
            }
        });
    }

    private void validateText() {
        if (ImageUri == null) {
            Toast.makeText(getApplicationContext(), "Please select an image", Toast.LENGTH_SHORT).show();
        }
        if (tvText.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please press detect and then continue", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getApplicationContext(), createJournal.class);
            intent.putExtra("content", tvText.getText().toString());
            Toast.makeText(getApplicationContext(), tvText.getText().toString(), Toast.LENGTH_LONG).show();
            startActivity(intent);
        }
    }

    private void detectText() {

        TextRecognizer recognizer = new TextRecognizer.Builder(uploadImage.this).build();

        Bitmap bitmap = ((BitmapDrawable) ivUserImage.getDrawable()).getBitmap();
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<TextBlock> sparseArray = recognizer.detect(frame);
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < sparseArray.size(); i++) {
            TextBlock tx = sparseArray.get(i);
            String str = tx.getValue();
            stringBuilder.append(str);

        }
        tvText.setText(stringBuilder);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData();
            ivUserImage.setImageURI(ImageUri);
        }
    }
}