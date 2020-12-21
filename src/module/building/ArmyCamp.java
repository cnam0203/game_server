package module.building;

import config.BuildingName;
import config.GameConfig;
import module.MapDataObject;
import org.json.JSONException;

public class ArmyCamp extends MapDataObject {
    private int amount;
    public ArmyCamp() {
        super();
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getValueField(String field) {
        try {
            return GameConfig.getInstance().getBuildingInfo("ArmyCamp", this.type, this.level).getInt(field);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
