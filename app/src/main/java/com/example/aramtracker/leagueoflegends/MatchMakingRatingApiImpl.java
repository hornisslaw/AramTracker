package com.example.aramtracker.leagueoflegends;

import android.util.Log;

import com.example.aramtracker.leagueoflegends.data.AramSummonerInfo;
import com.jayway.jsonpath.JsonPath;

import java.util.Optional;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MatchMakingRatingApiImpl implements MatchMakingRatingAPI{

    private final String PARTIAL_LINK = ".whatismymmr.com/api/v1/summoner?name=";
    //private final String PLATFORM = "eune";

    @Override
    public Optional<AramSummonerInfo> getSummonerInfoByNick(String nick, String server) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://" + server + PARTIAL_LINK + nick)
                .build();

        Log.i("MMRInfo", "Requesting whatismymmr for summoner " + nick);
        try {
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            return Optional.ofNullable(processSummonerInfo(nick, result));
        } catch (Exception ex) {
            Log.w("MMRInfo", String.format("Whatismymmr for summoner %s failed", nick), ex);
        }
        return Optional.empty();
    }

    private AramSummonerInfo processSummonerInfo(String nick, String data) {

        try {
            String closestRank = JsonPath.read(data, "$.ARAM.closestRank");
            int aramMMR = JsonPath.read(data, "$.ARAM.avg");
            int standardDeviation = JsonPath.read(data, "$.ARAM.err");
            double percentile = JsonPath.read(data, "$.ARAM.percentile");
            return new AramSummonerInfo(nick, closestRank, aramMMR, standardDeviation, percentile);

        } catch (Exception ex) {
            Log.w("MMRInfo", "Missing stats for" + nick, ex);
        }
        return null;
        
    }
}
