package com.example.aramtracker;

import static android.R.color.holo_green_dark;
import static android.R.color.holo_red_light;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aramtracker.leagueoflegends.LeagueOfLegendsAPI;
import com.example.aramtracker.leagueoflegends.LeagueOfLegendsApiImpl;
import com.example.aramtracker.leagueoflegends.MatchMakingRatingApiImpl;
import com.example.aramtracker.leagueoflegends.data.AramMatchSummonerInfo;
import com.example.aramtracker.leagueoflegends.data.AramSummonerInfo;
import com.example.aramtracker.properties.Props;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.OptionalDouble;

import io.paperdb.Paper;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;

    List list = new ArrayList();
    ArrayAdapter adapter;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Paper.init(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = getIntent();
        String nickName = i.getStringExtra("playerName");
        String server = i.getStringExtra("playerServer");

        TextView addToFav = (TextView) findViewById(R.id.addToFav);
        addToFav.setText("Add +");
        addToFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dodanie do bazy
//                Paper.book("MMR").write(nickName, mmr)
//                Paper.book("closestRank").read(nickName, closestRank)
                Toast.makeText(MainActivity.this, nickName + " -> was added to favourites", Toast.LENGTH_SHORT).show();
            }
        });

        TextView nickNameTextView = (TextView) findViewById(R.id.nickName);
        nickNameTextView.setText(nickName);

        TextView rankNameTextView = (TextView) findViewById(R.id.rankName);

        ImageView rankView = (ImageView) findViewById(R.id.rank);

        TextView liveStatus = (TextView) findViewById(R.id.liveStatus);

        TextView mmrView = (TextView) findViewById(R.id.mmrTextView);

        ListView listView = (ListView) findViewById(R.id.listView);

        MatchMakingRatingApiImpl whatIsMyMMR = new MatchMakingRatingApiImpl();
        try {
            AramSummonerInfo summonerInfo = whatIsMyMMR.getSummonerInfoByNick(nickName, server.toLowerCase(Locale.ROOT)).orElseThrow(() -> new IllegalStateException("Error for user " + nickName));
            String imageUri = "@drawable/" + parseRank(summonerInfo.getClosestRank());

            int imageResource = getResources().getIdentifier(imageUri, null, getPackageName());
            Drawable res = getResources().getDrawable(imageResource);
            rankView.setImageDrawable(res);
            rankNameTextView.setText(summonerInfo.getClosestRank());

            mmrView.setText(Integer.toString(summonerInfo.getAramMMR()));
            LeagueOfLegendsAPI leagueOfLegendsAPI = new LeagueOfLegendsApiImpl(new Props(getApplicationContext()));
            String status = "LIVE";
            liveStatus.setVisibility(View.INVISIBLE);
            if (leagueOfLegendsAPI.checkIfPlayerInLiveGame(nickName)) {
//                status = "LIVE";
//                liveStatus.setText(status);
                liveStatus.setVisibility(View.VISIBLE);
                liveStatus.setTextColor(holo_green_dark);
                liveStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getApplicationContext(),
                                LiveGameActivity.class);
                        i.putExtra("playerName", nickName);
                        startActivity(i);
                    }
                });
            }
            liveStatus.setText(status);

            LeagueOfLegendsApiImpl leagueOfLegendsAPI2 = new LeagueOfLegendsApiImpl(new Props(getApplicationContext()));
            List<AramMatchSummonerInfo> aramMatchSummonerInfo = leagueOfLegendsAPI2.getAramMatchesInfo(nickName);
            Map<String, Long> championsWithIds = leagueOfLegendsAPI2.getChampionAndChampionsIds();
            for (String champ : championsWithIds.keySet()) {
                List<AramMatchSummonerInfo> statsForChampion = leagueOfLegendsAPI2.getAramMatchInfoByChampion(aramMatchSummonerInfo, championsWithIds.get(champ));
                long totalGames = statsForChampion.size();
                OptionalDouble avgDmg = statsForChampion.stream()
                        .mapToLong(AramMatchSummonerInfo::getTotalDamageDealtToChampions)
                        .average();
                OptionalDouble avgDuration = statsForChampion.stream()
                        .mapToLong(AramMatchSummonerInfo::getGameDuration)
                        .average();
                long wins = statsForChampion.stream().filter(AramMatchSummonerInfo::isWin).count();
                if (avgDmg.isPresent() && totalGames > 0 && avgDuration.isPresent()) {
                    double winratio = (1.0 * wins / totalGames) * 100;
                    double damagePerMinute = avgDmg.getAsDouble() / (avgDuration.getAsDouble() / 60.0);
                    DecimalFormat df = new DecimalFormat("0.00");
                    list.add(champ + " Avg dmg/min: " + df.format(damagePerMinute) + " Winratio: " + df.format(winratio) + "%");
                }
            }

            adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, list);
            listView.setAdapter(adapter);

        } catch (IllegalStateException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "No stats for user: " + nickName, Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
//
//        bottomNavigationView = findViewById(R.id.bottom_navigation);
//
//        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                MatchMakingRatingApiImpl whatIsMyMMR = new MatchMakingRatingApiImpl();
//
//                switch (item.getItemId()) {
//                    case R.id.nav_search :
//                        selectorFragment = new SearchFragment();
//                        String nick = "Bodziuguard";
//                        try {
//                            AramSummonerInfo summonerInfo = whatIsMyMMR.getSummonerInfoByNick(nick).orElseThrow(() -> new IllegalStateException("Error for user " + nick));
//                            Toast.makeText(MainActivity.this, summonerInfo.getNickname() + ": " + summonerInfo.getClosestRank(), Toast.LENGTH_SHORT).show();
//                        } catch (IllegalStateException e) {
//                            e.printStackTrace();
//                            Toast.makeText(MainActivity.this, "No stats for user: " + nick, Toast.LENGTH_SHORT).show();
//                        }
//                        break;
//                    case R.id.nav_favorites:
//                        selectorFragment = new FavoriteFragment();
//                        LeagueOfLegendsAPI leagueOfLegendsAPI = new LeagueOfLegendsApiImpl(new Props(getApplicationContext()));
//                        Map<Integer, List<String>> currentGameParticipants = leagueOfLegendsAPI.getCurrentGameParticipantsByNick("Duke Alimony");
//                        for (Integer team: currentGameParticipants.keySet()) {
////                            String key = team.toString();
////                            String value = currentGameParticipants.get(team).toString();
//                            for (String name: currentGameParticipants.get(team)) {
//                                try {
//                                    AramSummonerInfo summonerInfo = whatIsMyMMR.getSummonerInfoByNick(name).orElseThrow(() -> new IllegalStateException("Error for user " + name));
//                                    Toast.makeText(MainActivity.this, summonerInfo.getNickname() + ": " + summonerInfo.getClosestRank(), Toast.LENGTH_SHORT).show();
//                                } catch (IllegalStateException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                        break;
//                    default:
//                        break;
//
//                }
//                if (selectorFragment != null) {
//                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
//                }
//                return true;
//            }
//        });
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchFragment()).commit();
//
//
//    }


    }

    private String parseRank(String rank) {
        String arr[] = rank.split(" ", 2);

        String baseRank = arr[0];
        String stage = arr[1];

        if ("challenger".equalsIgnoreCase(baseRank))
            return "challenger";
        if ("grandmaster".equalsIgnoreCase(baseRank))
            return "grandmaster";
        if ("master".equalsIgnoreCase(baseRank))
            return "master";
        if ("diamond".equalsIgnoreCase(baseRank))
            return "diamond";
        if ("platinum".equalsIgnoreCase(baseRank))
            return "platinum";
        if ("gold".equalsIgnoreCase(baseRank))
            return "gold";
        if ("silver".equalsIgnoreCase(baseRank))
            return "silver";
        if ("bronze".equalsIgnoreCase(baseRank))
            return "bronze";
        if ("iron".equalsIgnoreCase(baseRank))
            return "iron";
        return "";
    }

}