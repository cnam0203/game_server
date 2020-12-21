package network;

import bitzero.server.extensions.data.BaseMsg;
import model.BarrackData;
import model.PlayerInfo;
import model.TroopInfo;
import module.GameManager;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;

public class ResponseFinishTrainingTroop extends BaseMsg {
    private GameManager gameManager;
    private int barrackID;

    public ResponseFinishTrainingTroop(short error, GameManager gameManager, int barrackID) {
        super(CmdDefine.POST_FINISH_TRAINING_TROOP, error);
        this.gameManager = gameManager;
        this.barrackID = barrackID;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        if (gameManager == null) {
            System.out.println("GameManager is null!");
            return packBuffer(bf);
        }

        BarrackData barrackData = gameManager.getBarrackManager().getBarrackDatas().get(barrackID);
        ArrayList<TroopInfo> troopsOnMap = gameManager.getPlayerInfo().getTroopsOnMap();

        bf.putInt(gameManager.getPlayerInfo().getElixir());
        bf.putInt(barrackID);
        putBoolean(bf, barrackData.isStopped());
        bf.putLong(new Date().getTime() - barrackData.getStartTime());
        bf.putInt(barrackData.getTrainingTroops().size());
        for (TroopInfo troop : barrackData.getTrainingTroops()) {
            putStr(bf, troop.getCategory());
            bf.putInt(troop.getLevel());
            bf.putInt(troop.getQuantity());
        }

        bf.putInt(troopsOnMap.size());
        for (TroopInfo troop : troopsOnMap) {
            putStr(bf, troop.getCategory());
            bf.putInt(troop.getLevel());
            bf.putInt(troop.getQuantity());
        }

        return packBuffer(bf);
    }
}
