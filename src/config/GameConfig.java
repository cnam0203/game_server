package config;

import com.google.gson.Gson;
import module.building.TownHall;
import org.json.JSONObject;
import util.Util;


public class GameConfig {
    private static final GameConfig INSTANCE = new GameConfig();
    private JSONObject building = new JSONObject();
    private JSONObject initGame = new JSONObject();
    private JSONObject troop = new JSONObject();

    private GameConfig() {
        try {
            String path = "conf/";
//            initGame = new JSONObject(Util.readJsonFromFile(path + "/InitGame.json"));
            initGame = new JSONObject(Util.readJsonFromFile(path + "/InitGame2.json"));
            // Read all config file
            for (String buildingName : Util.getAllBuildingName()) {
                String configPath = path + buildingName + ".json";
                building.accumulate(buildingName, new JSONObject(Util.readJsonFromFile(configPath)));
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static GameConfig getInstance() {
        return INSTANCE;
    }

    public JSONObject getInitGame() throws Exception {
        return new JSONObject(this.initGame.toString());
    }

    public JSONObject getBuildingInfo(String fileName, String type, int level) throws Exception {
        return building.getJSONObject(fileName).getJSONObject(type).getJSONObject(Integer.toString(level));
    }

    public JSONObject getTroopInfo(String type, int level) throws Exception {
        return building.getJSONObject("Troop").getJSONObject(type).getJSONObject(Integer.toString(level));
    }

    public int getSizeFromType(String type) throws Exception {
        String filename = Util.getNameByType(type);
        String level = "1";
        int size = building.getJSONObject(filename).getJSONObject(type).getJSONObject(level).getInt("width");
        return size;
    }
    public JSONObject getTroopBaseInfo(String type) throws Exception {
        return building.getJSONObject("TroopBase").getJSONObject(type);
    }
    public JSONObject getBuilding(){
        return building;
    }
}
