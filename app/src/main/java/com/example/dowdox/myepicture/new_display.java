package com.example.dowdox.myepicture;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class new_display extends AppCompatActivity {

    private String access_token = LogIn.getAccess_token();
    private ImageView image_view;
    private int parser;
    private int max_size;
    private String[] userImagesUrls;
    public String[] userImageId;
    private Boolean[] userImageFav;
    private Context context = this;
    private MenuItem favItemNav;
    private BottomNavigationView navigation;
    private int tour = 0;
    private boolean filter_or_not;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.prec:
                    if (parser == 0)
                        parser = max_size - 1;
                    else
                        parser--;
                    boolean no_fav = true;
                    int limit = 0;
                    if (filter_or_not == true) {
                        limit = parser + 1;
                        if (limit == max_size)
                            limit = 0;
                        while (parser != limit && userImageFav[parser] != true) {
                            if (parser - 1 == -1)
                                parser = max_size;
                            parser -= 1;
                        }
                        int tmp = limit;
                        if (tmp - 1 == -1)
                            tmp = max_size;
                        if (parser == limit && userImageFav[tmp] == true)
                            no_fav = false;
                        else if (parser == limit && userImageFav[parser] == false)
                            no_fav = true;
                        else if (parser == limit && userImageFav[tmp + 1] == false)
                            no_fav = true;
                        else
                            no_fav = false;
                    }
                    if (no_fav == true && filter_or_not == true) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Pas De Favoris, Filtre Désactivé");
                        AlertDialog alert = builder.create();
                        alert.show();
                        parser = limit - 1;
                        filter_or_not = false;
                    }
                    if (userImageFav[parser] == true)
                        favItemNav.setTitle("Retirer Des Favoris");
                    else
                        favItemNav.setTitle("Ajouter Aux Favoris");
                    Picasso.with(context).load(userImagesUrls[parser]).into(image_view);
                    tour += 1;
                    return false;
                case R.id.fav:
                    if (tour != 0) {
                        requestFav();
                        String str1;
                        String str2;
                        favItemNav = navigation.getMenu().findItem(R.id.fav);
                        if (favItemNav.getTitle() == "Ajouter Aux Favoris") {
                            str1 = "Retirer Des Favoris";
                            str2 = "Image Ajoutée Aux Favoris";
                        } else {
                            str1 = "Ajouter Aux Favoris";
                            str2 = "Image Retirée Des Favoris";
                        }
                        favItemNav.setTitle(str1);
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(str2);
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                    return true;
                case R.id.next:
                    if (parser == max_size - 1)
                        parser = 0;
                    else
                        parser++;
                    no_fav = true;
                    limit = 0;
                    if (filter_or_not == true) {
                        limit = parser - 1;
                        if (limit == -1)
                            limit = max_size - 1;
                        while (parser != limit && userImageFav[parser] != true) {
                            if (parser + 1 == max_size)
                                parser = -1;
                            parser += 1;
                        }
                        int tmp = limit;
                        if (tmp + 1 == max_size)
                            tmp = -1;
                        if (parser == limit && userImageFav[tmp] == true)
                            no_fav = false;
                        else if (parser == limit && userImageFav[parser] == false)
                            no_fav = true;
                        else if (parser == limit && userImageFav[tmp + 1] == false)
                            no_fav = true;
                        else
                            no_fav = false;
                    }
                    if (no_fav == true && filter_or_not == true) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Pas De Favoris, Filtre Désactivé");
                        AlertDialog alert = builder.create();
                        alert.show();
                        parser = limit + 1;
                        filter_or_not = false;
                    }
                    if (userImageFav[parser] == true)
                        favItemNav.setTitle("Retirer Des Favoris");
                    else
                        favItemNav.setTitle("Ajouter Aux Favoris");
                    Picasso.with(context).load(userImagesUrls[parser]).into(image_view);
                    tour += 1;
                    return false;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_display);
        filter_or_not = false;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.filter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String apply;
                if (filter_or_not == true) {
                    filter_or_not = false;
                    apply = "Filtre Favoris Désactivé";
                } else {
                    filter_or_not = true;
                    apply = "Filtre Favoris Activé";
                }
                Snackbar.make(view, apply, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        image_view = (ImageView) findViewById(R.id.imageView3);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);

        try {
            printUserImages();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.fav);
        favItemNav = navigation.getMenu().findItem(R.id.fav);
    }

    protected void printUserImages() throws InterruptedException {
        final String[] userImages = {null};
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
            max_size = arr.length();
            userImagesUrls = new String[max_size];
            userImageId = new String[max_size];
            userImageFav = new Boolean[max_size];
            for (int i = 0; i < max_size; i++) {
                JSONObject obj = arr.getJSONObject(i);
                userImagesUrls[i] = obj.getString("link");
                userImageId[i] = obj.getString("id");
                userImageFav[i] = obj.getBoolean("favorite");
                parser++;
            }
            parser = 0;
            image_view = (ImageView) findViewById(R.id.imageView3);
            Picasso.with(this).load(userImagesUrls[parser]).into(image_view);
            if (userImageFav[parser] == true)
                favItemNav.setTitle("Retirer Des Favoris");
            else
                favItemNav.setTitle("Ajouter Aux Favoris");
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

    protected void requestFav() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.imgur.com/3/image/" + userImageId[parser] + "/favorite").newBuilder();
                    String url = urlBuilder.build().toString();
                    RequestBody requestBody = new MultipartBuilder()
                            .type(MultipartBuilder.FORM)
                            .addPart(
                                    Headers.of("Content-Disposition", "form-data; name=\"title\""),
                                    RequestBody.create(null, "new_upload"))
                            .build();
                    Request request = new Request.Builder()
                            .header("Authorization", "Bearer " + access_token)
                            .url(url)
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String resImages = response.body().string();
                    String strRes = response.body().string();
                    JSONObject obj1 = new JSONObject(resImages);
                    Boolean success = obj1.getBoolean("success");
                    if (success) {
                        if (userImageFav[parser]) {
                            userImageFav[parser] = false;
                        }
                        else {
                            userImageFav[parser] = true;
                        }
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(new_display.this);
                        builder.setMessage("Image Ajoutée Aux Favoris Fail");
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
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

}
