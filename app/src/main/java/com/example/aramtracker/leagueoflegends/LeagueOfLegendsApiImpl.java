package com.example.aramtracker.leagueoflegends;

import static java.util.stream.Collectors.toList;

import com.example.aramtracker.leagueoflegends.data.AramMatchSummonerInfo;
import com.example.aramtracker.leagueoflegends.retryer.SynchronizedRetryer;
import com.example.aramtracker.properties.Props;
import com.jayway.jsonpath.JsonPath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.paperdb.Paper;
import no.stelar7.api.r4j.basic.cache.impl.MemoryCacheProvider;
import no.stelar7.api.r4j.basic.calling.DataCall;
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;
import no.stelar7.api.r4j.basic.constants.api.regions.RegionShard;
import no.stelar7.api.r4j.impl.R4J;
import static no.stelar7.api.r4j.basic.constants.api.regions.RegionShard.EUROPE;
import static no.stelar7.api.r4j.basic.constants.types.lol.GameQueueType.ARAM;
import static no.stelar7.api.r4j.basic.constants.types.lol.MatchlistMatchType.NORMAL;

import android.util.Log;

import no.stelar7.api.r4j.pojo.lol.match.v5.LOLMatch;
import no.stelar7.api.r4j.pojo.lol.match.v5.MatchParticipant;
import no.stelar7.api.r4j.pojo.lol.spectator.SpectatorGameInfo;
import no.stelar7.api.r4j.pojo.lol.spectator.SpectatorParticipant;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LeagueOfLegendsApiImpl implements LeagueOfLegendsAPI{

    private final static String CHAMPION_INFO_LINK = "https://ddragon.leagueoflegends.com/cdn/12.8.1/data/en_US/champion.json";

    private static Map<String, List<AramMatchSummonerInfo>> cache = new HashMap<>();
    private final R4J riotNewAPI ;
    private final SynchronizedRetryer retryer;
    private final String championData;

    public LeagueOfLegendsApiImpl(Props props) {
        this.retryer = new SynchronizedRetryer();
        this.riotNewAPI  = new R4J(props.getApiCredentials());
        this.championData = fetchChampionsIds();
        DataCall.setCacheProvider(new MemoryCacheProvider());

    }

    @Override
    public List<AramMatchSummonerInfo> getAramMatchInfosByChampion(String champion) {
        long championId = getChampionIdByName(champion);
        return cache.values().stream()
                .flatMap(Collection::stream)
                .filter(stats -> stats.getChampionId() == championId)
                .collect(toList());
    }

    @Override
    public List<AramMatchSummonerInfo> getAramMatchInfosByNickAndChampion(String nick, String champion) {
        List<AramMatchSummonerInfo> aramStats = getAramMatchesInfo(nick);
        long championId = getChampionIdByName(champion);
        return aramStats.stream()
                .filter(stats -> stats.getChampionId() == championId)
                .collect(toList());
    }

    @Override
    public Map<Integer, List<String>> getCurrentGameParticipantsByNick(String nick) {
        Map<Integer, List<String>> currentGameParticipants = new HashMap<>();

        try {
            String id = getSummonerStats(nick)
                    .map(Summoner::getSummonerId)
                    .orElseThrow(() -> new IllegalStateException("Couldn't find id of user with nick:" + nick));

            SpectatorGameInfo spectatorGameInfo = getCurrentGame(id).orElseThrow(() -> new IllegalStateException("Couldn't find live game for summoner: " + nick));
            currentGameParticipants = spectatorGameInfo.getParticipants()
                    .stream()
                    .collect(Collectors.groupingBy(p -> p.getTeam().getValue(),
                            Collectors.mapping(SpectatorParticipant::getSummonerName, toList())));

        } catch (Exception ex) {
            Log.w("LolCurrentGame","Exception during collecting current game participants of user: " + nick, ex);
            ex.printStackTrace();
        }

        return currentGameParticipants;
    }

    @Override
    public List<AramMatchSummonerInfo> getAramMatchInfoByChampion(List<AramMatchSummonerInfo> aramStats, Long championId) {
        return aramStats.stream()
                .filter(stats -> stats.getChampionId() == championId)
                .collect(toList());
    }

    @Override
    public Boolean checkIfPlayerInLiveGame(String nick) {
        try {
            String id = getSummonerStats(nick)
                    .map(Summoner::getSummonerId)
                    .orElseThrow(() -> new IllegalStateException("Couldn't find id of user with nick:" + nick));

            SpectatorGameInfo spectatorGameInfo = getCurrentGame(id).orElseThrow(() -> new IllegalStateException("Couldn't find live game for summoner: " + nick));
            return true;
        } catch (Exception ex) {
            Log.w("LolCurrentGame","Exception during collecting current game participants of user: " + nick, ex);
            ex.printStackTrace();
        }

        return false;
    }

    public List<AramMatchSummonerInfo> getAramMatchesInfo(String nick) {
        List<AramMatchSummonerInfo> aramStats = new ArrayList<>();
        if (Paper.book().contains(nick)) {
            return Paper.book().read(nick);
        }
        try {
            String puuId = getSummonerStats(nick)
                    .map(Summoner::getPUUID)
                    .orElseThrow(() -> new IllegalStateException("Couldn't find accountPuuid of user with nick:" + nick));
            int beginIndex = 1;
            int count = 100;
            List<String> matchIds = riotNewAPI.getLoLAPI().getMatchAPI().getMatchList(EUROPE, puuId, ARAM, NORMAL, beginIndex, count, null, null);
            while (!matchIds.isEmpty()) {
                Log.i("LolMatchesInfo", String.format("Processing matchlist %d-%d for user %s", beginIndex, beginIndex + count, nick));
                aramStats.addAll(processMatches(puuId, matchIds));
                beginIndex += count;
                matchIds = riotNewAPI.getLoLAPI().getMatchAPI().getMatchList(EUROPE, puuId, ARAM, NORMAL, beginIndex, count, null, null);
            }
        } catch (Exception ex) {
            Log.w("LolMatchesInfo","Exception during processing match list of user: " + nick, ex);
        }
        Log.i("LolMatchesInfo", String.format("Processed %d games of user %s", aramStats.size(), nick));
        Paper.book().write(nick, aramStats);
        return aramStats;
    }

    private List<AramMatchSummonerInfo> processMatches(String puuId, List<String> matchIds) {
        List<AramMatchSummonerInfo> aramStats = new ArrayList<>();
        for (String matchId : matchIds) {
            try {
                Thread.sleep(50);
                Log.i("LolProcessMatches", "Processing match " + matchId);
                LOLMatch match = getMatch(EUROPE, matchId)
                        .orElseThrow(() -> new IllegalStateException("Match with id: " + matchId + " not found"));
                MatchParticipant participant = match.getParticipants().stream()
                        .filter(p -> p.getPuuid().equals(puuId))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Participant " + puuId + " not found in match " + matchId));
                boolean win = participant.didWin();
                long championId = participant.getChampionId();
                long gameDuration = match.getGameDuration();
                long dmgDealtToChamps = participant.getTotalDamageDealtToChampions();
                aramStats.add(new AramMatchSummonerInfo(championId, dmgDealtToChamps, gameDuration, win));
            } catch (Exception ex) {
                Log.w("LolProcessMatches", String.format("Match %s processing failed", matchId), ex);
            }
        }
        return aramStats;
    }

    private Optional<SpectatorGameInfo> getCurrentGame(String summonerId){
        return retryer.callWithRetries(() -> riotNewAPI.getLoLAPI().getSpectatorAPI().getCurrentGame(LeagueShard.EUN1, summonerId));
    }

    private Optional<LOLMatch> getMatch(RegionShard regionShard, String matchId) {
        return retryer.callWithRetries(() -> riotNewAPI.getLoLAPI().getMatchAPI().getMatch(regionShard, matchId));
    }

    @Override
    public Optional<Summoner> getSummonerStats(String nick) {
        return retryer.callWithRetries(() -> riotNewAPI.getLoLAPI().getSummonerAPI().getSummonerByName(LeagueShard.EUN1, nick));
    }

    private long getChampionIdByName(String name) {
        try {
            String key = JsonPath.read(championData, "$.data." + name + ".key");
            return Long.parseLong(key);
        } catch (Exception ex) {
            Log.w("LolChampionId", "Can't get id for champion name: " + name, ex);
        }
        return -1;
    }

    public Map<String, Long> getChampionAndChampionsIds(){
        Map<String, Long> championsAndChampionsIds = new HashMap<>();

        Set<String> championsNames = getChampionsNames();
        for (String championName: championsNames.toArray(new String[0])) {
            championsAndChampionsIds.put(championName, getChampionIdByName(championName));
            Log.i("LolGetChampions", "Getting id for champion " + championName);
        }

        return championsAndChampionsIds;

    }

    private String fetchChampionsIds(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(CHAMPION_INFO_LINK)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String result = response.body().string();
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        throw new IllegalStateException();
    }

    public Set<String> getChampionsNames() {
        Set<String> data = null;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(CHAMPION_INFO_LINK)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String result = response.body().string();
            data = JsonPath.read(result, "$.data.keys()");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }
}
