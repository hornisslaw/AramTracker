package com.example.aramtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class FavouritesListActivity extends AppCompatActivity {

    List list = new ArrayList();
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_favourites_list);

        ListView listView = (ListView) findViewById(R.id.favouritesList);

        adapter = new ArrayAdapter(FavouritesListActivity.this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),
                        MainActivity.class);
                //Paper.book("MMR").read(nickName, mmr);
                //Paper.book("closestRank").read(read, closestRank);
                String arr[] = list.get(i).toString().split(" ", 2);

                String baseRank = arr[0];
                String stage = arr[1];
                intent.putExtra("playerName", arr[0]);
                intent.putExtra("playerServer", arr[1]);
                startActivity(intent);
            }
        });

    }
}