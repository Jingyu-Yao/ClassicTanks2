package com.JingyuYao.ClassicTanks;

/**
 * Created by Jingyu on 2/12/2015.
 */

/**
 * Contains statistics for a level used by {@code EndScreen} ans data saving.
 */
public class LevelStat {
    public int levelNumber;
    public int armoredKills;
    public int fastKills;
    public int dualKills;
    public int normalKills;
    public int barrageKills;
    public int wallKills;
    public long levelDuration; // Nano
    public Tank.TankType currentPlayerType;
    public int lifeLeft;
    public boolean won;

    @Override
    public String toString() {
        return (won ? "Level " + levelNumber + " complete..." : "You lost...") + "\n"
                + "Armor kills: " + armoredKills + "\n"
                + "Barrage kills: " + barrageKills + "\n"
                + "Dual kills: " + dualKills + "\n"
                + "Fast kills: " + fastKills + "\n"
                + "Normal kills: " + dualKills + "\n"
                + "Wall kills: " + wallKills + "\n"
                + "Play time: " + levelDuration + "\n"
                + "Life left: " + lifeLeft + "\n";
    }
}
