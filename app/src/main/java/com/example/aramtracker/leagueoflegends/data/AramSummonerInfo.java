package com.example.aramtracker.leagueoflegends.data;

public class AramSummonerInfo {
    private final String nickname;
    private final String closestRank;
    private final int aramMMR;
    private final int standardDeviation;
    private final double percentile;

    public AramSummonerInfo(String nickname, String closestRank, int aramMMR, int standardDeviation, double percentile) {
        this.nickname = nickname;
        this.closestRank = closestRank;
        this.aramMMR = aramMMR;
        this.standardDeviation = standardDeviation;
        this.percentile = percentile;
    }

    public String getNickname() {
        return nickname;
    }

    public String getClosestRank() {
        return closestRank;
    }

    public int getAramMMR() {
        return aramMMR;
    }

    public int getStandardDeviation() {
        return standardDeviation;
    }

    public double getPercentile() {
        return percentile;
    }
}
