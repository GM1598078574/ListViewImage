package com.gaomin.listviewimage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list_view);
        ImageAdapter2 adapter = new ImageAdapter2(this, 0, Images.imageUrls);
        listView.setAdapter(adapter);
    }
}
