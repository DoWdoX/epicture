package com.example.dowdox.myepicture;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class Display extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private String access_token = LogIn.getAccess_token();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        try {
            printUserImages();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    protected void printUserImages() throws InterruptedException {
        final String[] userImages = {null};
        int parser = 0;
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    userImages[0] = getUserImageJSON();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        thread.join();
        try {
            JSONObject json = new JSONObject(userImages[0]);
            JSONArray arr = json.getJSONArray("data");
            String[] userImagesUrls = new String[arr.length()];
            for (int i = 0; i < arr.length(); ++i) {
                JSONObject obj = arr.getJSONObject(i);
                userImagesUrls[parser] = obj.getString("link");
                parser++;
            }
            parser = 0;
            /*LinearLayout layout = (LinearLayout) findViewById(R.id.linearlayout1);
            int w = 50;
            int h = 50;
            for (parser = 0; parser < arr.length(); parser++) {
                ImageView newImage = new ImageView(this);
                newImage.setLayoutParams(new android.view.ViewGroup.LayoutParams(720, 1000));
                newImage.setMaxHeight(50);
                newImage.setMaxWidth(50);
                Picasso.with(this).load(userImagesUrls[parser]).into(newImage);
                layout.addView(newImage);
                h += 100;
                w += 100;
            }*/
        }
        catch (Exception ignored) {
        }
    }
    protected String getUserImageJSON() throws IOException {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.imgur.com/3/account/me/images").newBuilder();
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .header("Authorization", "Bearer " + access_token)
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        String res = response.body().string();
        return (res);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}