package module;

import model.BarrackData;
import model.PlayerInfo;
import model.TroopInfo;
import module.barrack.BarrackManager;
import module.building.ArmyCamp;
import module.troop.TroopManager;

import java.util.ArrayList;
import java.util.Date;

public class GameManager {
    private PlayerInfo playerInfo;

    private BarrackManager barrackManager;
    private TroopManager troopManager;
    private MapObjectManager mapObjectManager;

    public GameManager(PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
        this.troopManager = new TroopManager();
        this.barrackManager = new BarrackManager(playerInfo.getBuildingContainer().barracks);
//        this.barrackManager = new BarrackManager(4);
        this.mapObjectManager = new MapObjectManager(playerInfo.getBuildingContainer(), playerInfo.getPositionMatrix());
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    public BarrackManager getBarrackManager() {
        return barrackManager;
    }

    public TroopManager getTroopManager() {
        return troopManager;
    }

    public MapObjectManager getMapObjectManager() { return mapObjectManager; }

    public void setBarrackDatas(PlayerInfo userInfo) {
        userInfo.setBarrackDatas(barrackManager.getBarrackDatas());
    }

    public void mapBarrackDataToBarrackManager() {
        if (playerInfo.getBarrackDatas().isEmpty())
            return;
        barrackManager.setBarrackDatas(playerInfo.getBarrackDatas());
    }

    public boolean checkCanAddTrainingTroop(String category, int level, int barrackID) {
        if (playerInfo.getBuildingContainer().barracks.get(barrackID).getProcessingStartTime() > 0)
            return false;
        TroopInfo troopInfo = new TroopInfo(category, level, barrackID);
        return barrackManager.checkCanAddTrainingTroop(playerInfo.getBuildingContainer().barracks, troopInfo);
    }

    public boolean checkCanAddTroopToMap(String category, int level, int barrackID) {
        if (playerInfo.getBuildingContainer().barracks.get(barrackID).getProcessingStartTime() > 0)
            return false;
        TroopInfo troopInfo = new TroopInfo(category, level, barrackID);
        ArrayList<ArmyCamp> armyCamps = playerInfo.getBuildingContainer().armyCamps;
        ArrayList<TroopInfo> currentTroopsOnMap = playerInfo.getTroopsOnMap();
        BarrackData barrackData = barrackManager.getBarrackDatas().get(barrackID);
//        System.out.println( new Date().getTime());
//        System.out.println( barrackData.getFinishTime());
        if (barrackData.getFinishTime() > new Date().getTime()) {
            return false;
        }
        boolean canAdd = barrackManager.checkCanAddTroopToMap(armyCamps, currentTroopsOnMap, troopInfo);
        barrackData.setIsStopped(!canAdd);
        return canAdd;
    }

    public void checkBarracks() {
        checkBarrackIsUpgrading();
        barrackManager.checkBarracks(playerInfo.getBuildingContainer().barracks, playerInfo.getBuildingContainer().armyCamps, playerInfo.getTroopsOnMap());
//        for (TroopInfo _troop: playerInfo.getTroopsOnMap()) {
//            System.out.println("___" + _troop.getCategory() + ": " + _troop.getQuantity());
//        }
//        barrackManager.print();
    }

    public void checkBarrackIsUpgrading() {
        barrackManager.checkBarrackIsUpgrading(playerInfo.getBuildingContainer().barracks);
    }

    public void quickTrainingTroops(int gCoin, int barrackID) {
        ArrayList<TroopInfo> troopInfos = barrackManager.quickTrainingTroops(barrackID);
        playerInfo.minusCoin(gCoin);
        playerInfo.addTroopsToMap(troopInfos);
    }
}
