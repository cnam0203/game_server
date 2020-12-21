package network;

import bitzero.server.extensions.data.BaseMsg;
import config.Global;

import java.nio.ByteBuffer;

public class ResponseCancelUpgrading extends BaseMsg {
    String type;
    int id;

    public ResponseCancelUpgrading(short result, String type, int id) {
        super(CmdDefine.CANCEL_UPGRADING, result);
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

