package network;

import bitzero.server.extensions.data.BaseMsg;
import cmd.receive.user.RequestHarvest;
import config.ProcessingState;

import java.nio.ByteBuffer;

public class ResponseHarvest extends BaseMsg {
    String type;
    int id;
    int amount;
    int gold;
    int elixir;
    int remaining;

    public ResponseHarvest(short error, String type, int id, int amount, int gold, int elixir, int remaining) {
        super(CmdDefine.HARVEST, error);
        this.type = type;
        this.id = id;
        this.amount = amount;
        this.gold = gold;
        this.elixir = elixir;
        this.remaining = remaining;
        System.out.println("Response: " + type + " " + id + " " + amount + " " + gold + " " + elixir + " " + remaining);
    }

    public ResponseHarvest(short error, String type, int id){
        super(CmdDefine.HARVEST, error);
        this.type = type;
        this.id = id;
        this.amount = 0;
        this.gold = 0;
        this.elixir = 0;
        this.remaining = 0;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        putStr(bf, type);
        bf.putInt(id);
        bf.putInt(amount);
        bf.putInt(gold);
        bf.putInt(elixir);
        bf.putInt(remaining);
        return packBuffer(bf);
    }
}
