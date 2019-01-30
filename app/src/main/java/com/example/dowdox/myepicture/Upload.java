package com.example.dowdox.myepicture;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Base64;

public class Upload extends Activity {

    public static final int REQUEST_CODE_PICK_IMAGE = 1001;
    private String accessToken = LogIn.getAccess_token();
    private String picturePath = "";
    private String encodedString = "";
    private String query = null;
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private String name = "Title Image";
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Button choose = (Button) findViewById(R.id.choose);
        Button upload = (Button) findViewById(R.id.upload);
        choose.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
            }
        });
        upload.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                my_request();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PICK_IMAGE && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
        }
        file = new File(picturePath);
        try (FileInputStream imageInFile = new FileInputStream(file)) {
            byte imageData[] = new byte[(int) file.length()];
            imageInFile.read(imageData);
            encodedString = Base64.getEncoder().encodeToString(imageData);
            query = URLEncoder.encode(encodedString, "UTF-8");
            ImageView image = (ImageView) findViewById(R.id.imageView2);
            image.setVisibility(View.VISIBLE);
            image.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        } catch (FileNotFoundException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Upload.this);
            builder.setMessage("File not supported");
            AlertDialog alert = builder.create();
            alert.show();
        } catch (IOException ioe) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Upload.this);
            builder.setMessage("ERROR2");
            AlertDialog alert = builder.create();
            alert.show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void my_request() {
        EditText edit = (EditText) findViewById(R.id.editText);
        String named = edit.getText().toString();
        if (named.isEmpty()) {
            name = "untitled_image";
        } else {
            name = named;
        }
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.imgur.com/3/upload").newBuilder();
                String url = urlBuilder.build().toString();

                RequestBody requestBody = new MultipartBuilder()
                        .type(MultipartBuilder.FORM)
                        .addPart(
                                Headers.of("Content-Disposition", "form-data; name=\"title\""),
                                RequestBody.create(null, name))
                        .addPart(
                                Headers.of("Content-Disposition", "form-data; name=\"image\""),
                                RequestBody.create(MEDIA_TYPE_PNG, file))
                        .addPart(
                                Headers.of("images", "form-data; image=\"image\""),
                                RequestBody.create(null, query)
                        )
                        .build();

                Request request = new Request.Builder()
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .header("Authorization", "Bearer " + accessToken)
                        .url(url)
                        .post(requestBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    String rep = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(Upload.this);
        builder.setMessage("Image Uploaded");
        AlertDialog alert = builder.create();
        alert.show();
    }
}