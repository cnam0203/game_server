package network;

import bitzero.server.extensions.data.BaseMsg;

import java.nio.ByteBuffer;

public class ResponseUpgrading extends BaseMsg {
    String type;
    int id;

    public ResponseUpgrading(short error, String type, int id) {
        super(CmdDefine.UPGRADING_OBJECT, error);
        this.type = type;
        this.id = id;
        System.out.println("Response: " + type + " " + id);
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        putStr(bf, type);
        bf.putInt(id);
        return packBuffer(bf);
    }
}
