package network;

import bitzero.server.extensions.data.BaseMsg;
import config.BuildingName;
import model.PlayerInfo;
import model.TroopInfo;
import module.GameManager;
import module.MapDataObject;
import module.building.ClanCastle;
import module.building.Resource;
import util.Util;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class ResponseGetTroopsOnMap extends BaseMsg {
    private GameManager pGameManager;

    public ResponseGetTroopsOnMap(short error, GameManager pGameManager) {
        super(CmdDefine.GET_TROOPS_ON_MAP, error);
        this.pGameManager = pGameManager;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        if (pGameManager == null) {
            System.out.println("pGameManager is null!");
            return packBuffer(bf);
        }

        PlayerInfo pInfo = pGameManager.getPlayerInfo();
        System.out.println(pInfo.getTroopsOnMap().size());
        bf.putInt(pInfo.getTroopsOnMap().size());
        for (TroopInfo troop: pInfo.getTroopsOnMap()) {
            putStr(bf, troop.getCategory());
            bf.putInt(troop.getLevel());
            bf.putInt(troop.getQuantity());
        }
        return packBuffer(bf);
    }
}
