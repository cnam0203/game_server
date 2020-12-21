package cmd.receive.user;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class RequestIncreaseResource extends BaseCmd {
    private String typeResource;

    public RequestIncreaseResource(DataCmd data) {
        super(data);
        unpackData();
        // TODO Auto-generated constructor stub
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            typeResource = readString(bf);
            System.out.println("Increase Resource with type: " + typeResource);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTypeResource() {
        return typeResource;
    }
}
