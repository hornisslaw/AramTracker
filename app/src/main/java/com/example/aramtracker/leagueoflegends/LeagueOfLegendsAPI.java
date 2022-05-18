package com.example.aramtracker.leagueoflegends;

import com.example.aramtracker.leagueoflegends.data.AramMatchSummonerInfo;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import no.stelar7.api.r4j.pojo.lol.spectator.SpectatorParticipant;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;

public interface LeagueOfLegendsAPI {

    List<AramMatchSummonerInfo> getAramMatchInfosByChampion(String champion);

    List<AramMatchSummonerInfo> getAramMatchInfosByNickAndChampion(String nick, String champion);

    Map<Integer, List<String>> getCurrentGameParticipantsByNick(String nick);

    List<AramMatchSummonerInfo> getAramMatchInfoByChampion(List<AramMatchSummonerInfo> stats, Long championId);

    Boolean checkIfPlayerInLiveGame(String nick);

    Optional<Summoner> getSummonerStats(String nick);
}
