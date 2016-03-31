package com.yokeyword.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.yokeyword.sample.city.PickCityActivity;
import com.yokeyword.sample.contact.PickContactActivity;

/**
 * Created by YoKeyword on 16/3/20.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_pick_city).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PickCityActivity.class));
            }
        });

        findViewById(R.id.btn_pick_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PickContactActivity.class));
            }
        });

        findViewById(R.id.btn_pick_city_large).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = PickCityActivity.getCallingIntent(MainActivity.this, true);
                startActivity(intent);
            }
        });
    }
}
