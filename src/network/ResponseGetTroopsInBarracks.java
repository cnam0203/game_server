package network;

import bitzero.server.extensions.data.BaseMsg;
import model.BarrackData;
import model.PlayerInfo;
import model.TroopInfo;
import module.GameManager;
import module.barrack.BarrackManager;

import java.nio.ByteBuffer;
import java.util.Date;

public class ResponseGetTroopsInBarracks extends BaseMsg {
    private GameManager pGameManager;

    public ResponseGetTroopsInBarracks(short error, GameManager pGameManager) {
        super(CmdDefine.GET_TROOPS_IN_BARRACKS, error);
        this.pGameManager = pGameManager;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        if (pGameManager == null) {
            System.out.println("pGameManager is null!");
            return packBuffer(bf);
        }

        BarrackManager barrackManager = pGameManager.getBarrackManager();

        bf.putInt(barrackManager.getBarrackDatas().size());
        for (BarrackData barrack : barrackManager.getBarrackDatas()) {
            bf.putInt(barrack.getBarrackID());
            putBoolean(bf, barrack.isStopped());
            bf.putLong(new Date().getTime() - barrack.getStartTime());
            bf.putInt(barrack.getTrainingTroops().size());
            for (TroopInfo troop : barrack.getTrainingTroops()) {
                putStr(bf, troop.getCategory());
                bf.putInt(troop.getLevel());
                bf.putInt(troop.getQuantity());
            }
        }

        return packBuffer(bf);
    }
}
