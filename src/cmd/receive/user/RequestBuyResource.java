package cmd.receive.user;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class RequestBuyResource extends BaseCmd {
    private String typeResource;
    private int amount;

    public RequestBuyResource(DataCmd data) {
        super(data);
        unpackData();
        // TODO Auto-generated constructor stub
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            this.typeResource = readString(bf);
            this.amount = readInt(bf);
            System.out.println("BuyResource: type: " + typeResource + ", amount = " + amount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTypeResource() {
        return typeResource;
    }

    public int getAmount() {
        return amount;
    }
}
