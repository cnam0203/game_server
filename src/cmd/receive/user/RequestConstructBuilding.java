package cmd.receive.user;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class RequestConstructBuilding extends BaseCmd {
    private String type;
    private int id;
    private int posX;
    private int posY;

    public RequestConstructBuilding(DataCmd data) {
        super(data);
        unpackData();
        // TODO Auto-generated constructor stub
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            type = readString(bf);
            posX = readInt(bf);
            posY = readInt(bf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getType() {
        return type;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
