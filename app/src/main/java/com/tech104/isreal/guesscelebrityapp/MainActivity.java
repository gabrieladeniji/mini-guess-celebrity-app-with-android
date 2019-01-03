package com.tech104.isreal.guesscelebrityapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebsPics;
    ArrayList<String> celebNames;
    int correctPosition;
    int choosenCeleb;
    int[] nameOptions = new int[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        this.getCelebsInfo();
        this.runAll();
    }

    public void chooseCelebrity(View view) {

        Log.i("Correct Answer", celebNames.get(nameOptions[correctPosition]));

        if( Integer.parseInt(view.getTag().toString()) == correctPosition ) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_LONG).show();
            this.runAll();
        } else {
            Toast.makeText(this, "Wrong, the correct answer is " + celebNames.get(nameOptions[correctPosition]), Toast.LENGTH_LONG).show();
            this.runAll();
        }
    }

    public void runAll() {
        this.generateRandomOptions();
        this.setCelebsImage();
        this.setOptionsToButtons();
    }

    public void generateRandomOptions() {
        Random rand = new Random();
        choosenCeleb = rand.nextInt(celebNames.size());
        correctPosition = rand.nextInt(4);
        int wrongPosition;
        for(int i=0; i<4; i++) {
            if(i == correctPosition) {
                nameOptions[i] = choosenCeleb;
            } else {
                wrongPosition = rand.nextInt(celebNames.size());
                while( wrongPosition == correctPosition) {
                    wrongPosition = rand.nextInt(celebNames.size());
                }
                nameOptions[i] = wrongPosition;
            }
        }
    }

    public void setOptionsToButtons() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        for(int i=0; i<linearLayout.getChildCount(); i++){
            View element = linearLayout.getChildAt(i);
            if (element instanceof Button) {
                Button button = (Button)element;
                button.setText( celebNames.get(nameOptions[i-1]) );
            }
        }
    }

    public void setCelebsImage() {
        LoadCelebImage task = new LoadCelebImage();
        try {
            Bitmap celebBitMap = task.execute(celebsPics.get(nameOptions[correctPosition])).get();
            ImageView imageView = (ImageView) findViewById(R.id.celeImageView);
            imageView.setImageBitmap(celebBitMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    // Threads

    public void getCelebsInfo() {
        DownloadCelebrityInfo celeb = new DownloadCelebrityInfo();
        try {
            String result = celeb.execute("http://www.posh24.se/kandisar").get();
            Pattern p = Pattern.compile("<img src=\"(.*?)\" alt=\"(.*?)\"/>");
            Matcher m = p.matcher(result);
            //
            celebNames = new ArrayList<String>();
            celebsPics = new ArrayList<String>();
            while(m.find()) {
                celebsPics.add( m.group(1) );
                celebNames.add( m.group(2) );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class DownloadCelebrityInfo extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls) {
            try {
                String result = "";
                URL url = new URL(urls[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1){
                    char c = (char) data;
                    result += c;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Failed";
        }
    }

    public class LoadCelebImage extends AsyncTask<String, Void, Bitmap>{
        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream is = httpURLConnection.getInputStream();
                Bitmap bitMapImage = BitmapFactory.decodeStream(is);
                return bitMapImage;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
