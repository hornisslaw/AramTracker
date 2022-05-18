package com.example.aramtracker.leagueoflegends.data;

public class AramMatchSummonerInfo {
    private final long championId;
    private final long totalDamageDealtToChampions;
    private final long gameDuration;
    private final boolean win;

    public AramMatchSummonerInfo(long championId, long totalDamageDealtToChampions, long gameDuration, boolean didWin) {
        this.championId = championId;
        this.totalDamageDealtToChampions = totalDamageDealtToChampions;
        this.gameDuration = gameDuration;
        this.win = didWin;
    }

    public long getChampionId() {
        return championId;
    }

    public long getTotalDamageDealtToChampions() {
        return totalDamageDealtToChampions;
    }

    public long getGameDuration() {
        return gameDuration;
    }

    public boolean isWin() {
        return win;
    }
}
