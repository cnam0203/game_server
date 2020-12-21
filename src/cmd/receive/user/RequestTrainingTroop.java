package cmd.receive.user;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class RequestTrainingTroop extends BaseCmd {
    public String category;
    public int level;
    public int barrackID;

    public RequestTrainingTroop(DataCmd data) {
        super(data);
        unpackData();
        // TODO Auto-generated constructor stub
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            category = readString(bf);
            level = readInt(bf);
            barrackID = readInt(bf);
//            barrackID -= 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
