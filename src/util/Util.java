package util;

import config.BuildingName;
import config.BuildingType;
import config.GameConfig;
import config.Global;
import model.TroopInfo;
import module.MapDataObject;
import module.building.Storage;
import module.building.TownHall;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Util {

    public static String readJsonFromFile(String file) throws Exception{
        String json = readFileAsString(file);
        return json;
    }
    public static String readFileAsString(String file) throws Exception
    {
        return new String(Files.readAllBytes(Paths.get(file)));
    }
    public static ArrayList<String> getAllKeysJson(JSONObject jObj){
        ArrayList<String> keys = new ArrayList<String>();
        Iterator iKey = jObj.keys();
        while(iKey.hasNext()){
            String key = (String) iKey.next();
            keys.add(key);
        }
        return keys;
    }
    public static ArrayList<String> getAllBuildingName(){
        ArrayList<String> buildNames = new ArrayList<String>();
        Field[] fields = BuildingName.class.getDeclaredFields();
        try{
            for (Field field : fields) {
                String buildingName = field.get(BuildingName.class).toString();
                buildNames.add(buildingName);
            }
        } catch (Exception err){
            err.printStackTrace();
        };
        return buildNames;
    }

    public static String getNameByType(String type){
        type = type.split("_")[0];
        switch (type){
            case BuildingType.AMC:{
                return BuildingName.ARMY_CAMP;
            }
            case BuildingType.BAR:{
                return BuildingName.BARRACK;
            }
            case BuildingType.CLC:{
                return BuildingName.CLAN_CASTLE;
            }
            case BuildingType.LAB:{
                return BuildingName.LABORATORY;
            }
            case BuildingType.OBS:{
                return BuildingName.OBSTACLE;
            }
            case BuildingType.RES:{
                return BuildingName.RESOURCE;
            }
            case BuildingType.STO:{
                return BuildingName.STORAGE;
            }
            case BuildingType.TOW:{
                return BuildingName.TOWN_HALL;
            }
            case BuildingType.TRA:{
                return BuildingName.TRAP;
            }
            case BuildingType.WAL:{
                return BuildingName.WALL;
            }
            case BuildingType.DEF:{
                return BuildingName.DEFENCE;
            }
            case BuildingType.BDH:{
                return BuildingName.BUILDER_HUT;
            }
            default:
                return Global.ERROR_TYPE;
        }
    }

    public static String getTypeByName(String name){
        switch (name){
            case BuildingName.ARMY_CAMP:{
                return BuildingType.AMC;
            }
            case BuildingName.BARRACK:{
                return BuildingType.BAR;
            }
            case BuildingName.CLAN_CASTLE:{
                return BuildingType.CLC;
            }
            case BuildingName.LABORATORY:{
                return BuildingType.LAB;
            }
            case BuildingName.OBSTACLE:{
                return BuildingType.OBS;
            }
            case BuildingName.RESOURCE:{
                return BuildingType.RES;
            }
            case BuildingName.STORAGE:{
                return BuildingType.STO;
            }
            case BuildingName.TOWN_HALL:{
                return BuildingType.TOW;
            }
            case BuildingName.TRAP:{
                return BuildingType.TRA;
            }
            case BuildingName.WALL:{
                return BuildingType.WAL;
            }
            case BuildingName.DEFENCE:{
                return BuildingType.DEF;
            }
            case BuildingName.BUILDER_HUT:{
                return BuildingType.BDH;
            }
        }
        return "";
    }

    public static int getTownHallLevelRequired(String type, int level){
        int townHallLevelRequired = 0;
        try{
            String fileName = Util.getNameByType(type);
            if (fileName.equals(BuildingName.OBSTACLE)){
                return 0;
            }else {
                if (GameConfig.getInstance().getBuildingInfo(fileName, type, level + 1).has("townHallLevelRequired")) {
                    townHallLevelRequired = GameConfig.getInstance().getBuildingInfo(fileName, type, level + 1).getInt("townHallLevelRequired");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return townHallLevelRequired;
    }

    public static int getRequiredGoldAmount(String type, int level){
        int requiredGold = 0;
        try{
            String filename = Util.getNameByType(type);
            if (filename.equals(BuildingName.OBSTACLE)){
                requiredGold = GameConfig.getInstance().getBuildingInfo(filename, type, level).getInt("gold");
            }else {
                if (GameConfig.getInstance().getBuildingInfo(filename, type, level + 1).has("gold")) {
                    requiredGold = GameConfig.getInstance().getBuildingInfo(filename, type, level + 1).getInt("gold");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return requiredGold;
    }

    public static int getRequiredElixirAmount(String type, int level){
        int requiredElixir = 0;
        try{
            String filename = Util.getNameByType(type);
            if (filename.equals(BuildingName.OBSTACLE)){
                requiredElixir = GameConfig.getInstance().getBuildingInfo(filename, type, level).getInt("elixir");
            }else {
                if (GameConfig.getInstance().getBuildingInfo(filename, type, level + 1).has("elixir")) {
                    requiredElixir = GameConfig.getInstance().getBuildingInfo(filename, type, level + 1).getInt("elixir");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return requiredElixir;
    }

    public static int getTimeAmountToUpgrade(String type, int level){
        int timeAmount = 0;
        try {
            String filename = Util.getNameByType(type);
            if (filename.equals(BuildingName.OBSTACLE)){
                timeAmount = GameConfig.getInstance().getBuildingInfo(filename, type, level).getInt("buildTime");
            }else{
                if (GameConfig.getInstance().getBuildingInfo(filename, type, level + 1).has("buildTime")){
                    timeAmount = GameConfig.getInstance().getBuildingInfo(filename, type, level + 1).getInt("buildTime");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return timeAmount;
    }

    public static ArrayList<TroopInfo> addTroopToArray(ArrayList<TroopInfo> troops, TroopInfo troop) {
        boolean isAdded = false;
        for (TroopInfo _troop : troops) {
            if (_troop.getCategory().equals(troop.getCategory())) {
                _troop.addTroop();
                isAdded = true;
            }
        }
        if (!isAdded) {
            troops.add(troop);
        }
        return troops;
    }

    public static void setInitMapObject(Object mapObject, int posX, int posY, int level, String buildingType, int id) {
        ((MapDataObject) mapObject).setPoint(posX, posY);
        ((MapDataObject) mapObject).setLevel(level);
        ((MapDataObject) mapObject).setType(buildingType);
        ((MapDataObject) mapObject).setId(id);
    }
}
