package com.example.aramtracker.leagueoflegends;

import com.example.aramtracker.leagueoflegends.data.AramSummonerInfo;

import java.util.Optional;

public interface MatchMakingRatingAPI {
    Optional<AramSummonerInfo> getSummonerInfoByNick(String nick, String server);
}

