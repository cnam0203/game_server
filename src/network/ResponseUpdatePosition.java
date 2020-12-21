package network;

import bitzero.server.extensions.data.BaseMsg;
import model.PlayerInfo;
import network.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseUpdatePosition extends BaseMsg {

    public ResponseUpdatePosition(short error) {
        super(CmdDefine.UPDATE_OBJECT_POSITION, error);
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        return packBuffer(bf);
    }
}
