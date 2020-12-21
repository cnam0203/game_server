package model;

import config.GameConfig;
import module.building.ArmyCamp;
import util.Util;

import java.util.ArrayList;
import java.util.Date;

public class BarrackData {
    private int barrackID;
    private long startTime;
    private long deltaStartTime;
    private boolean isStopped;
    private ArrayList<TroopInfo> trainingTroops;

    public BarrackData(int barrackID) {
        this.barrackID = barrackID;
        trainingTroops = new ArrayList<>();
        startTime = new Date().getTime();
        isStopped = false;
    }

    public void updateStartTime(long time) {
        startTime = time;
    }

    public void updateStartTime() {
        startTime = new Date().getTime();
    }

    public long getFinishTime() {
        if (trainingTroops.size() == 0)
            return getStartTime();
        return getStartTime() + trainingTroops.get(0).getTrainingTime() * 1000L;
    }

    public int getBarrackID() {
        return barrackID;
    }

    public long getStartTime() {
        if (trainingTroops.size() == 0)
            return new Date().getTime();
        return startTime;
    }

    public long getDeltaStartTime() {
        return this.deltaStartTime;
    }
    public void setDeltaStartTime(long time) {
        this.deltaStartTime = time;
    }

    public void setIsStopped(boolean isStopped) {
        this.isStopped = isStopped;
    }
    public boolean isStopped() {
        return this.isStopped;
    }
    
    public ArrayList<TroopInfo> getTrainingTroops() {
        return trainingTroops;
    }

    public void addTrainingTroop(TroopInfo troopInfo) {
        if (trainingTroops.size() == 0)
            updateStartTime();
        Util.addTroopToArray(trainingTroops, troopInfo);
    }

    public TroopInfo removeTrainingTroop(String category) { 
        for (int i = 0; i < trainingTroops.size(); i++) {
            TroopInfo troop = trainingTroops.get(i);
            if (troop.getCategory().equals(category)) {
                if (troop.getQuantity() == 1) {
                    if (i == 0) {
                        updateStartTime();
                    }
                    trainingTroops.remove(troop);
                }
                else
                    troop.removeTroop();
                return troop;
            }
        }
        return null;
    }
    public TroopInfo removeTrainingTroops(String category, int quantity) {
        for (TroopInfo troop : trainingTroops) {
            if (troop.getCategory().equals(category)) {
                if (troop.getQuantity() < quantity)
                    return null;
                else if (troop.getQuantity() == quantity)
                    trainingTroops.remove(troop);
                else
                    troop.removeTroop(quantity);
                return new TroopInfo(troop);
            }
        }
        return null;
    }

    // Finish training so we remove first troop and add to Map
    public TroopInfo removeFirstTroop() {
        TroopInfo firstTroop = trainingTroops.get(0);
        updateStartTime(getFinishTime());
        if (firstTroop.getQuantity() == 1)
            trainingTroops.remove(firstTroop);
        else
            firstTroop.removeTroop();
        return new TroopInfo(firstTroop);
    }

    public boolean finishTrainingTroop(TroopInfo troop) {
        long currentTime = new Date().getTime();
        if (startTime + troop.getTrainingTime() < currentTime) {
            removeTrainingTroop(troop.getCategory());
            updateStartTime();
            return true;
        }
        return false;
    }

    public ArrayList<TroopInfo> quickTraining() {
        updateStartTime();
        ArrayList<TroopInfo> quickTraining = new ArrayList<>(trainingTroops);
        trainingTroops.clear();
        return quickTraining;
    }
}
