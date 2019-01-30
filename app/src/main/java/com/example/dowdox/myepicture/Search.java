package com.example.dowdox.myepicture;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;

public class Search extends AppCompatActivity {
    private Button search_btn;
    private EditText search_text;
    private LinearLayout layout;

    private String resImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        search_btn = (Button) findViewById(R.id.searchbutton);
        search_text = (EditText) findViewById(R.id.searchlabel);
        layout = (LinearLayout) findViewById(R.id.linearlayout2);

        search_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                clearLayout();
                getSearchImages();
                printImages();
            }
        });
    }

    public void clearLayout() {
        LinearLayout li = (LinearLayout)findViewById(R.id.linearlayout2);
        li.removeAllViews();
    }

    public void getSearchImages() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String search = (String) search_text.getText().toString();
                    final String encodedURL = URLEncoder.encode(search, "UTF-8");

                    OkHttpClient client = new OkHttpClient();
                    HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.imgur.com/3/gallery/search?q=" + encodedURL).newBuilder();
                    String url = urlBuilder.build().toString();
                    Request request = new Request.Builder()
                            .header("Content-Type", "application/x-www-form-urlencoded")
                            .header("Authorization", "Client-ID e8eeb2c02e493ed")
                            .url(url)
                            .build();
                    Response response = client.newCall(request).execute();
                    resImages = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void printImages() {
        try {
            int tablesize = 0;
            JSONObject obj1 = new JSONObject(resImages);
            JSONArray arr1 = obj1.getJSONArray("data");

            for (int i = 0; i < arr1.length(); ++i) {
                JSONObject obj2 = arr1.getJSONObject(i);
                if (obj2.has("images")) {
                    JSONArray arr2 = obj2.getJSONArray("images");
                    for (int j = 0; j < arr2.length(); ++j)
                        ++tablesize;
                }
            }

            String[] allImagesUrls = new String[tablesize];
            int n = 0;
            for (int i = 0; i < arr1.length(); ++i) {
                JSONObject obj2 = arr1.getJSONObject(i);
                if (obj2.has("images")) {
                    JSONArray arr2 = obj2.getJSONArray("images");
                    for (int j = 0; j < arr2.length(); ++j) {
                        JSONObject obj3 = arr2.getJSONObject(j);
                        allImagesUrls[n] = obj3.getString("link");
                        ++n;
                    }
                }
            }

            for (int i = 0; i < tablesize; ++i) {
                ImageView newImage = new ImageView(this);
                newImage.setLayoutParams(new android.view.ViewGroup.LayoutParams(720, 1000));
                newImage.setMaxHeight(50);
                newImage.setMaxWidth(50);
                Picasso.with(this).load(allImagesUrls[i]).into(newImage);
                layout.addView(newImage);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
