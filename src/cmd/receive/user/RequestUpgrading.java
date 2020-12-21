package cmd.receive.user;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class RequestUpgrading extends BaseCmd {
    private String type;
    private int id;

    public RequestUpgrading(DataCmd data) {
        super(data);
        unpackData();
        // TODO Auto-generated constructor stub
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            type = readString(bf);
            id = readInt(bf);
            System.out.println("Upgrade: " + type + ", id = " + id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }
}
