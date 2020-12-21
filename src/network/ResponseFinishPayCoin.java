package network;

import bitzero.server.extensions.data.BaseMsg;

import java.nio.ByteBuffer;

public class ResponseFinishPayCoin extends BaseMsg {
    String type;
    int id;

    public ResponseFinishPayCoin(short error, String type, int id) {
        super(CmdDefine.FINISH_PAY_COIN, error);
        this.type = type;
        this.id = id;
        System.out.println("Response with type: " + type + ", id: " + id);
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        putStr(bf, type);
        bf.putInt(id);
        return packBuffer(bf);
    }
}
