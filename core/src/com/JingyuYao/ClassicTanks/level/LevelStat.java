package com.JingyuYao.ClassicTanks.level;

import com.JingyuYao.ClassicTanks.objects.Tank;

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
                + "Play time: " + (float) levelDuration / 1000000000f + "\n"
                + "Life left: " + lifeLeft + "\n";
    }
}
