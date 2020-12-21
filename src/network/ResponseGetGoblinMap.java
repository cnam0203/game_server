package network;

import bitzero.server.extensions.data.BaseMsg;
import org.json.JSONObject;
import util.Util;

import java.nio.ByteBuffer;

/**
 * Created by CPU12845-local on 12/21/2020.
 */
public class ResponseGetGoblinMap extends BaseMsg {
    int level;
    JSONObject goblinJson;

    public ResponseGetGoblinMap(int level) {
        super(CmdDefine.GET_GOBLIN);
        this.level = level;
    }

    public void readConfigMap() {
        try {
            String path = "conf/GoblinMap/";
            String level = String.valueOf(this.level);
            goblinJson = new JSONObject(Util.readJsonFromFile(path + level + ".map"));
            System.out.println(goblinJson.toString());
        } catch (Exception err) {
            err.printStackTrace();
        }


    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        readConfigMap();
        bf.putInt(this.level);
        return packBuffer(bf);
    }
}
