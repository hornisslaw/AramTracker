package com.example.aramtracker;

import static java.util.stream.Collectors.toList;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aramtracker.leagueoflegends.LeagueOfLegendsAPI;
import com.example.aramtracker.leagueoflegends.LeagueOfLegendsApiImpl;
import com.example.aramtracker.leagueoflegends.MatchMakingRatingAPI;
import com.example.aramtracker.leagueoflegends.MatchMakingRatingApiImpl;
import com.example.aramtracker.leagueoflegends.data.AramMatchSummonerInfo;
import com.example.aramtracker.leagueoflegends.data.AramSummonerInfo;
import com.example.aramtracker.properties.Props;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import org.json.JSONException;

import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.paperdb.Paper;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;

public class SearchActivity extends AppCompatActivity {

    private Button btnFind;
    private EditText editTextName;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        Paper.init(getApplicationContext());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_search);

        EditText playerName = (EditText) findViewById(R.id.editTextTextPersonName);
        Spinner playerServer = (Spinner) findViewById(R.id.playerServerSpinner);

        Button trackPlayer = (Button) findViewById(R.id.buttonFind);
        trackPlayer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        MainActivity.class);
                if (!playerName.getText().toString().isEmpty()) {
                    i.putExtra("playerName", playerName.getText().toString());
                    i.putExtra("playerServer", playerServer.getSelectedItem().toString());
                    startActivity(i);
                } else {
                    Toast.makeText(SearchActivity.this, "Insert Nickname!", Toast.LENGTH_SHORT).show();
                }
            }});

        Button fav = (Button) findViewById(R.id.favourites);
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        FavouritesListActivity.class);
                startActivity(i);
            }});

        // DISPLAY LIVE GAME PLAYERS MMR
//        MatchMakingRatingApiImpl whatIsMyMMR = new MatchMakingRatingApiImpl();
//        LeagueOfLegendsApiImpl leagueOfLegendsAPI = new LeagueOfLegendsApiImpl(new Props(getApplicationContext()));
//        Map<Integer, List<String>> currentGameParticipants = leagueOfLegendsAPI.getCurrentGameParticipantsByNick("Christian Díor");
//        for (Integer team : currentGameParticipants.keySet()) {
////                            String key = team.toString();
////                            String value = currentGameParticipants.get(team).toString();
//            for (String name : Objects.requireNonNull(currentGameParticipants.get(team))) {
//                try {
//                    AramSummonerInfo summonerInfo = whatIsMyMMR.getSummonerInfoByNick(name).orElseThrow(() -> new IllegalStateException("Error for user " + name));
//                    Toast.makeText(SearchActivity.this, summonerInfo.getNickname() + ": " + summonerInfo.getAramMMR(), Toast.LENGTH_SHORT).show();
//                } catch (IllegalStateException e) {
////                    e.printStackTrace();
//                    Toast.makeText(SearchActivity.this, "No info", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }


        // AVG DMG FOR PLAYER AND CHAMPION
//        String nick = "koncaty3K";
//        String champion = "Sona";
//        LeagueOfLegendsAPI leagueOfLegendsAPI = new LeagueOfLegendsApiImpl(new Props(getApplicationContext()));
//        List<AramMatchSummonerInfo> stats = leagueOfLegendsAPI.getAramMatchInfosByNickAndChampion(nick, champion);
//        OptionalDouble avgDamage = stats.stream()
//                .mapToLong(AramMatchSummonerInfo::getTotalDamageDealtToChampions)
//                .average();
//        Toast.makeText(SearchActivity.this, "User:  " + nick + " | Champion: " + champion + " AVG DMG: " + avgDamage , Toast.LENGTH_SHORT).show();

        // IF IN LIVE GAME
//        LeagueOfLegendsAPI leagueOfLegendsAPI = new LeagueOfLegendsApiImpl(new Props(getApplicationContext()));
//        String nnn = "FirmaReported";
//        if (leagueOfLegendsAPI.checkIfPlayerInLiveGame(nnn)) {
//            Toast.makeText(SearchActivity.this, "Player " + nnn + " in live game", Toast.LENGTH_SHORT).show();
//        }
//
        // AVG DMG PER CHAMPION
//        String nick = "koncaty3K";
//        LeagueOfLegendsApiImpl leagueOfLegendsAPI = new LeagueOfLegendsApiImpl(new Props(getApplicationContext()));
//        //To będzie wyciągane z bazy
//        List<AramMatchSummonerInfo> aramMatchSummonerInfo = leagueOfLegendsAPI.getAramMatchesInfo(nick);
//        Map<String, Long> championsWithIds = leagueOfLegendsAPI.getChampionAndChampionsIds();
//        for (String champ: championsWithIds.keySet()) {
//            List<AramMatchSummonerInfo> statsForChampion = leagueOfLegendsAPI.getAramMatchInfoByChampion(aramMatchSummonerInfo, championsWithIds.get(champ));
//            long totalGames = statsForChampion.size();
//            OptionalDouble avgDmg = statsForChampion.stream()
//                    .mapToLong(AramMatchSummonerInfo::getTotalDamageDealtToChampions)
//                    .average();
//            OptionalDouble avgDuration = statsForChampion.stream()
//                    .mapToLong(AramMatchSummonerInfo::getGameDuration)
//                    .average();
//            long wins = statsForChampion.stream().filter(AramMatchSummonerInfo::isWin).count();
//
//            if (avgDmg.isPresent() && totalGames > 0 && avgDuration.isPresent()) {
//                double damagePerMinute = avgDmg.getAsDouble() / (avgDuration.getAsDouble() / 60.0);
//                double winratio = 1.0*wins / totalGames ;
//                DecimalFormat df = new DecimalFormat("0.00");
//                Toast.makeText(SearchActivity.this, " Champion: " + champ + " dmg/min: " + df.format(damagePerMinute), Toast.LENGTH_SHORT).show();
//                Toast.makeText(SearchActivity.this, " Champion: " + champ + " winratio " + df.format(winratio), Toast.LENGTH_SHORT).show();
//            }
//        }
    }
}