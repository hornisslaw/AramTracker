package com.example.aramtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.aramtracker.leagueoflegends.LeagueOfLegendsApiImpl;
import com.example.aramtracker.leagueoflegends.MatchMakingRatingApiImpl;
import com.example.aramtracker.leagueoflegends.data.AramSummonerInfo;
import com.example.aramtracker.properties.Props;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LiveGameActivity extends AppCompatActivity {

    List listFirst = new ArrayList();
    ArrayAdapter adapter1;

    List listSecond = new ArrayList();
    ArrayAdapter adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_live_game);

        ListView listfirstView = (ListView)findViewById(R.id.teamFirst);
        ListView listSecondView = (ListView)findViewById(R.id.teamSecond);

        MatchMakingRatingApiImpl whatIsMyMMR = new MatchMakingRatingApiImpl();
        LeagueOfLegendsApiImpl leagueOfLegendsAPI = new LeagueOfLegendsApiImpl(new Props(getApplicationContext()));
        Intent i = getIntent();
        String nickName = i.getStringExtra("playerName");
        Map<Integer, List<String>> currentGameParticipants = leagueOfLegendsAPI.getCurrentGameParticipantsByNick(nickName);
//        for (Integer team : currentGameParticipants.keySet()) {
//            String key = team.toString();
//            String value = currentGameParticipants.get(team).toString();
//            for (String name : Objects.requireNonNull(currentGameParticipants.get(team))) {
//                try {
//                    AramSummonerInfo summonerInfo = whatIsMyMMR.getSummonerInfoByNick(name, "EUNE").orElseThrow(() -> new IllegalStateException("Error for user " + name));
//                    Toast.makeText(LiveGameActivity.this, summonerInfo.getNickname() + ": " + summonerInfo.getAramMMR(), Toast.LENGTH_SHORT).show();
//                } catch (IllegalStateException e) {
//                    e.printStackTrace();
//                    Toast.makeText(LiveGameActivity.this, "No info", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
        for (String name : Objects.requireNonNull(currentGameParticipants.get(100)))
        {
            AramSummonerInfo summonerInfo = whatIsMyMMR.getSummonerInfoByNick(name, "EUNE").orElseThrow(() -> new IllegalStateException("Error for user " + name));
            listFirst.add(summonerInfo.getNickname() + ": " + summonerInfo.getAramMMR());
        }

        for (String name : Objects.requireNonNull(currentGameParticipants.get(200)))
        {
            AramSummonerInfo summonerInfo = whatIsMyMMR.getSummonerInfoByNick(name, "EUNE").orElseThrow(() -> new IllegalStateException("Error for user " + name));
            listSecond.add(summonerInfo.getNickname() + ": " + summonerInfo.getAramMMR());
        }


        adapter1 = new ArrayAdapter(LiveGameActivity.this, android.R.layout.simple_list_item_1,listFirst);
        listfirstView.setAdapter(adapter1);

        adapter2 = new ArrayAdapter(LiveGameActivity.this, android.R.layout.simple_list_item_1,listSecond);
        listSecondView.setAdapter(adapter2);

    }
}