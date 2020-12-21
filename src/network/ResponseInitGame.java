package network;

import bitzero.server.extensions.data.BaseMsg;
import config.BuildingName;
import model.PlayerInfo;
import module.GameManager;
import module.MapDataObject;
import module.building.ClanCastle;
import module.building.Resource;
import util.Util;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class ResponseInitGame extends BaseMsg {
    private GameManager pGameManager;

    public ResponseInitGame(short error, GameManager pGameManager) {
        super(CmdDefine.GET_INIT_GAME, error);
        this.pGameManager = pGameManager;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        if (pGameManager == null) {
            System.out.println("pGameManager is null!");
            return packBuffer(bf);
        }
//        HashMap<String, Object> buildings = pInfo.mapObjectManager.buildings.getAllBuildings();
//        Object armyCamps = buildings.get(BuildingName.ARMY_CAMP);
//        ArrayList<ArmyCamp> a = (ArrayList) armyCamps;
//        System.out.println(a.get(0).getType());
        PlayerInfo pInfo = pGameManager.getPlayerInfo();
        bf.putInt(pInfo.getGold());
        bf.putInt(pInfo.getElixir());
        bf.putInt(pInfo.getCoin());
        bf.putInt(pInfo.getDarkElixir());
        int numTypeBuilding = 0;
        HashMap<String, ArrayList> buildings = pInfo.getBuildingContainer().getAllBuildings();

        for (String buildingName : Util.getAllBuildingName()) {
            if (!buildings.containsKey(buildingName)) continue;
            ArrayList obj = buildings.get(buildingName);
            if (obj.size() != 0)
                numTypeBuilding++;
        }
        bf.putInt(numTypeBuilding);
        for (String buildingName : Util.getAllBuildingName()) {
            if (!buildings.containsKey(buildingName)) continue;
            ArrayList<Object> mapObjects = buildings.get(buildingName);
            if (mapObjects.size() == 0) continue;
            putStr(bf, buildingName);
            bf.putInt(mapObjects.size());
//            System.out.println("*****" + " " + buildingName + " " + mapObjects.size());
            for (Object mapObject : mapObjects) {
                MapDataObject tempMapObj = ((MapDataObject) mapObject);
                putStr(bf, tempMapObj.getType());
                bf.putInt(tempMapObj.getId());
                bf.putInt(tempMapObj.getLevel());
                bf.putInt(tempMapObj.getDeltaProcessingStartTime());
                bf.putInt(tempMapObj.getPosX());
                bf.putInt(tempMapObj.getPosY());
                switch (buildingName) {
                    case BuildingName.CLAN_CASTLE:
                        bf.putInt(0);
                        break;
                    case BuildingName.RESOURCE:
                        bf.putInt(((Resource) mapObject).getMiningDeltaTime());
                        break;
                }
            }
        }
        return packBuffer(bf);
    }
}
