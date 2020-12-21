package module.building;

import config.BuildingName;
import config.GameConfig;
import module.MapDataObject;

public class Barrack extends MapDataObject {
    public Barrack(){
        super();
    }

    public int getValueField(String field) {
        try {
            int level = 0;
            if (this.level == 0) level += 1;
            else level = this.level;
            return GameConfig.getInstance().getBuildingInfo("Barrack", this.type, level).getInt(field);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
