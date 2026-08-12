package br.com.clash.cwlexporter.model;

import lombok.Data;

import java.util.Map;

@Data
public class PlayerData {
    private String tag;
    private String name;
    private int mapPosition;
    private DayData war1;
    private DayData war2;
    private DayData war3;
    private DayData war4;
    private DayData war5;
    private DayData war6;
    private DayData war7;
    private int totalAttackStars;
    private double totalDefenseStars;
    private double totalStars;

    public PlayerData() {
    }

    public PlayerData(String tag, String name, Map<Integer, DayData> warData) {
        this.tag = tag;
        this.name = name;
        this.war1 = warData.getOrDefault(1, new DayData(0, 0));
        this.war2 = warData.getOrDefault(2, new DayData(0, 0));
        this.war3 = warData.getOrDefault(3, new DayData(0, 0));
        this.war4 = warData.getOrDefault(4, new DayData(0, 0));
        this.war5 = warData.getOrDefault(5, new DayData(0, 0));
        this.war6 = warData.getOrDefault(6, new DayData(0, 0));
        this.war7 = warData.getOrDefault(7, new DayData(0, 0));
        this.totalAttackStars = war1.getAttackStars() + war2.getAttackStars() + war3.getAttackStars() +
                war4.getAttackStars() + war5.getAttackStars() + war6.getAttackStars() + war7.getAttackStars();
        this.totalDefenseStars = war1.getDefenseStars() + war2.getDefenseStars() + war3.getDefenseStars() +
                war4.getDefenseStars() + war5.getDefenseStars() + war6.getDefenseStars() + war7.getDefenseStars();
        this.totalStars = this.totalAttackStars + this.totalDefenseStars;
    }
}
