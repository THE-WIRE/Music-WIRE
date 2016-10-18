package com.example.adikr.musicplayer2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = new Intent("playMusic");
        startActivity(i);
        finish();
    }
    public void showNotification(View view){
        new MyNotification(this);
        finish();
    }
}
