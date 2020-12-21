package network;

import bitzero.server.extensions.data.BaseMsg;

import java.nio.ByteBuffer;

public class ResponseBuyResource extends BaseMsg {
    String type;
    int amount;
    public ResponseBuyResource(short error, String type, int amount){
        super(CmdDefine.BUY_RESOURCE, error);
        this.type = type;
        this.amount = amount;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        putStr(bf, type);
        bf.putInt(amount);
        return packBuffer(bf);
    }
}
