package module.barrack;

import model.BarrackData;
import model.PlayerInfo;
import model.TroopInfo;
import module.MapObjectManager;
import module.building.ArmyCamp;
import module.building.Barrack;
import util.Util;

import java.util.ArrayList;
import java.util.Date;

public class BarrackManager {
//    private static final int NUMBER_BARRACKS = 4;
    private int NUMBER_BARRACKS;

    private ArrayList<BarrackData> barrackDatas;

    public BarrackManager(ArrayList<Barrack> barracks) {
        this.barrackDatas = new ArrayList<>();
//        NUMBER_BARRACKS = barracks.size();
        for (Barrack barrack : barracks) {
            BarrackData barrackData = new BarrackData(barrack.getId());
            barrackDatas.add(barrackData);
        }
    }

    public void print() {
        for (BarrackData barrackData : barrackDatas) {
            System.out.println("Barrack: " + barrackData.getBarrackID() + " | " + "Start: " + barrackData.getStartTime());
            for (int j = 0; j < barrackData.getTrainingTroops().size(); j++) {
                barrackData.getTrainingTroops().get(j).print();
            }
        }
    }

    public void setBarrackDatas(ArrayList<BarrackData> barrackDatas) {
        this.barrackDatas = barrackDatas;
    }
    public void addBarrackData(int id) {
        System.out.println("ADDDDDD");
//        NUMBER_BARRACKS += 1;
        BarrackData barrackData = new BarrackData(id);
        barrackDatas.add(barrackData);
    }
    // When user cancel building new barrack
    public void removeBarrack() {
        barrackDatas.remove(barrackDatas.size() - 1);
    }
    public ArrayList<BarrackData> getBarrackDatas () {
        return barrackDatas;
    }
    public ArrayList<TroopInfo> getTrainingTroopsByBarrackID(int barrackID) {
        return barrackDatas.get(barrackID).getTrainingTroops();
    }

    public void addOneTroop(TroopInfo newTroop) {
        barrackDatas.get(newTroop.getBarrackID()).addTrainingTroop(newTroop);
    }

    public TroopInfo removeTrainingTroop(String category, int level, int barrackID) {
        return barrackDatas.get(barrackID).removeTrainingTroop(category);
    }

    public void checkBarracks(ArrayList<Barrack> barracks, ArrayList<ArmyCamp> armyCamps, ArrayList<TroopInfo> troopsOnMap) {
//        ArrayList<TroopInfo> troops = new ArrayList<>();
        TroopInfo minSpaceTroop;
        do {
            minSpaceTroop = null;
            long minStartTime = new Date().getTime();
            for (int i = 0; i < barracks.size(); i++) {
                if (barracks.get(i).getProcessingStartTime() > 0) {
                    break;
                }
                BarrackData barrack = getBarrackDatas().get(i);
                if (barrack.getTrainingTroops().size() > 0) {
                    TroopInfo troop = barrack.getTrainingTroops().get(0);
                    if (checkCanAddTroopToMap(armyCamps, troopsOnMap, troop)) {
                        barrack.setIsStopped(false);
                        if (minStartTime > barrack.getFinishTime()) {
                            minStartTime = barrack.getFinishTime();
                            minSpaceTroop = troop;
                        }
                    }
                    else {
                        barrack.setIsStopped(true);
                    }
                }
            }
            if (minSpaceTroop != null) {
                TroopInfo troop = getBarrackDatas().get(minSpaceTroop.getBarrackID()).removeFirstTroop();
                Util.addTroopToArray(troopsOnMap, troop);
            }
        } while (minSpaceTroop != null);

//        for (TroopInfo _troop: troopsOnMap) {
//            System.out.println("___" + _troop.getCategory() + ": " + _troop.getQuantity());
//        }
//        System.out.println();
//        return troopsOnMap;
    }

    public TroopInfo removeTroopToAddToMap(int barrackID) {
        BarrackData barrackData = barrackDatas.get(barrackID);
        if (barrackData.getTrainingTroops().size() == 0)
            return null;
        TroopInfo troop = barrackData.getTrainingTroops().get(0);
        long currentTime = new Date().getTime();
        long finishTime = barrackData.getFinishTime();
        if (finishTime <= currentTime) {
            if (troop.getQuantity() == 1)
                barrackData.getTrainingTroops().remove(0);
            else
                troop.removeTroop();
            barrackData.updateStartTime(finishTime);
            return new TroopInfo(troop);
        }
        return null;
    }

    public boolean checkCanAddTroopToMap(ArrayList<ArmyCamp> armyCamps, ArrayList<TroopInfo> troopsOnMap, TroopInfo troop) {
        int totalCapacity = 0, currentCapacity = 0;
        for (ArmyCamp armyCamp : armyCamps) {
            totalCapacity += armyCamp.getValueField("capacity");
        }
        for (TroopInfo info : troopsOnMap) {
            currentCapacity += info.getTotalHousingSpace();
        }
//        System.out.println("Troop capacity: " + troop.getTotalHousingSpace());
//        System.out.println("totalCapacityTroop: " + totalCapacity);
//        System.out.println("Current capacity: " + currentCapacity);
        return (currentCapacity + troop.getTotalHousingSpace()) <= totalCapacity;
    }

    public boolean checkCanAddTrainingTroop(ArrayList<Barrack> barracks, TroopInfo troopInfo) {
        int totalCapacity = 0, currentCapacity = 0;
        for (Barrack barrack : barracks) {
            totalCapacity += barrack.getValueField("queueLength");
        }
        for (BarrackData barrack: barrackDatas) {
            for (TroopInfo troop: barrack.getTrainingTroops()) {
                currentCapacity += troop.getTotalHousingSpace();
            }
        }
//        System.out.println("CAPACITY: " + troopInfo.getTotalHousingSpace());
//        System.out.println("totalCapacityTroop: " + totalCapacity);
//        System.out.println("Current CAPACITY: " + currentCapacity);
        return (currentCapacity + troopInfo.getHousingSpace()) <= totalCapacity;
    }

    public ArrayList<TroopInfo> quickTrainingTroops(int barrackID) {
        return barrackDatas.get(barrackID).quickTraining();
    }

    public void checkBarrackIsUpgrading(ArrayList<Barrack> barracks) {
        try{
            for (int i = 0; i < barracks.size(); i++) {
                BarrackData barrack = getBarrackDatas().get(i);
                System.out.println("checkBarrackIsUpgrading: Barack_" + barrack.getBarrackID());
                if (barracks.get(i).getProcessingStartTime() > 0) {
                    barrack.setDeltaStartTime(barracks.get(i).getProcessingStartTime() - barrack.getStartTime());
                }
                else if (barrack.getDeltaStartTime() != 0) {
                    barrack.updateStartTime(new Date().getTime() - barrack.getDeltaStartTime());
                    barrack.setDeltaStartTime(0);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Barrack Error");
        }

    }
}
