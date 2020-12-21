package network;

import bitzero.server.extensions.data.BaseMsg;

import java.nio.ByteBuffer;

public class ResponseConstructBuilding extends BaseMsg {
    String type;
    int id;
    public ResponseConstructBuilding(short error, String type, int id){
        super(CmdDefine.CONSTRUCT_BUILDING, error);
        this.type = type;
        this.id = id;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        putStr(bf, type);
        bf.putInt(id);
        return packBuffer(bf);
    }
}
