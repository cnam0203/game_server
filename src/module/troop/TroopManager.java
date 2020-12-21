package module.troop;

import model.TroopInfo;

import java.util.ArrayList;

public class TroopManager {
    private ArrayList<TroopInfo> trainingTroops;
    private ArrayList<TroopInfo> inMapTroops;

    public TroopManager() {
        trainingTroops = new ArrayList<>();
        inMapTroops = new ArrayList<>();
    }

    public void updateTroops(int quantity) {

    }

    public ArrayList<TroopInfo> getInMapTroops() {
        return inMapTroops;
    }
}
