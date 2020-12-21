package network;

import bitzero.server.extensions.data.BaseMsg;
import model.BarrackData;
import model.PlayerInfo;
import model.TroopInfo;
import module.GameManager;
import network.CmdDefine;

import java.nio.ByteBuffer;
import java.util.Date;

public class ResponseAddTrainingTroop extends BaseMsg {
    private GameManager gameManager;
    private int barrackID;

    public ResponseAddTrainingTroop(short error, GameManager gameManager, int barrackID) {
        super(CmdDefine.POST_ADD_TRAINING_TROOP, error);
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
        bf.putInt(gameManager.getPlayerInfo().getElixir());
        bf.putInt(barrackID);
        bf.putLong(new Date().getTime() - barrackData.getStartTime());
        bf.putInt(barrackData.getTrainingTroops().size());
        for (TroopInfo troop : barrackData.getTrainingTroops()) {
            putStr(bf, troop.getCategory());
            bf.putInt(troop.getLevel());
            bf.putInt(troop.getQuantity());
        }

        return packBuffer(bf);
    }
}
