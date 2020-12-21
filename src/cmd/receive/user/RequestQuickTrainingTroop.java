package cmd.receive.user;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class RequestQuickTrainingTroop extends BaseCmd {
    public String category;
    public int gCoin;
    public int barrackID;

    public RequestQuickTrainingTroop(DataCmd data) {
        super(data);
        unpackData();
        // TODO Auto-generated constructor stub
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            barrackID = readInt(bf);
            gCoin = readInt(bf);
//            barrackID -= 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
