package network;

import bitzero.server.extensions.data.BaseMsg;

import java.nio.ByteBuffer;

public class ResponseIncreaseResource extends BaseMsg {
    String type;
    int amount;

    public ResponseIncreaseResource(String type, int amount) {
        super(CmdDefine.INCREASE_RESOURCE);
        this.type = type;
        this.amount = amount;
        System.out.println("Response Increase Resource: " + type + " " + amount);
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        putStr(bf, type);
        bf.putInt(amount);
        return packBuffer(bf);
    }
}
