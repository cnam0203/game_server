package module.building;

import config.BuildingName;
import module.Builder;
import module.MapDataObject;

public class BuilderHut extends MapDataObject {
    private static int numId = 0;
    private Builder builder;
    public BuilderHut(){
        this.id = numId;
        this.builder = new Builder();
        this.numId ++;
    }
    public static int getNumId(){
        return numId;
    }
    public Builder getBuilder(){
        return this.builder;
    }
}
