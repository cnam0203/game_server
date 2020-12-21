package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import config.GameConfig;
import config.Global;
import module.Builder;
import module.MapDataObject;
import module.MapObjectManager;
import module.building.*;
import org.json.JSONObject;
import util.Util;
import util.database.DataModel;

public class
PlayerInfo extends DataModel {
    private int id;
    private String name;
    private boolean[][] positionMatrix;
    private ArrayList<BarrackData> barrackDatas;
    private ArrayList<TroopInfo> troopsOnMap;

    private ArrayList<Builder> builders;
    private BuildingContainer buildingContainer;


    private int gold;
    private int coin;
    private int elixir;
    private int darkElixir;
    private int goblinLevel;

    public PlayerInfo(int _id, String _name) {
        super();
        id = _id;
        name = _name;
        goblinLevel = 1;
        barrackDatas = new ArrayList<>();
        troopsOnMap = new ArrayList<>();
        builders = new ArrayList<Builder>();
        buildingContainer = new BuildingContainer();

        positionMatrix = new boolean[Global.MAP_SIZE + 1][Global.MAP_SIZE + 1];
        initNewPlayer();
        initNewMap();
        initPositionMatrix();
    }

    public String toString() {
        return String.format("%s|%s", new Object[]{id, name});
    }

    private void initNewPlayer() {
        GameConfig config = GameConfig.getInstance();
        try {
            JSONObject playerInfo = config.getInitGame().getJSONObject(Global.PLAYER);
            this.gold = playerInfo.getInt(Global.GOLD);
            this.elixir = playerInfo.getInt(Global.ELIXIR);
            this.coin = playerInfo.getInt(Global.COIN);
//            this.gold = 2000;
//            this.elixir = 2400;
//            this.coin = 2000;
            this.darkElixir = playerInfo.getInt(Global.DARK_ELIXIR);
        } catch (Exception err) {
            System.out.println(err.toString());
        }
    }

    private void initNewMap(){
        GameConfig config = GameConfig.getInstance();
        try {
            JSONObject initGame = config.getInitGame();
            JSONObject map = initGame.getJSONObject(Global.MAP);
            JSONObject obs = initGame.getJSONObject(Global.OBS);
            for (String key : Util.getAllKeysJson(map)) {
                System.out.println(key);
                String buildingType = key;
                String buildingName = Util.getNameByType(buildingType);
                int posX = map.getJSONObject(buildingType).getInt(Global.POS_X);
                int posY = map.getJSONObject(buildingType).getInt(Global.POS_Y);
                int level = map.getJSONObject(buildingType).getInt(Global.LEVEL);
                Class buildingClass = Class.forName("module.building." + buildingName);
                Object mapObject = buildingClass.newInstance();
                Util.setInitMapObject(mapObject, posX, posY, level, buildingType, buildingContainer.getBuildingsLength(buildingType));
                buildingContainer.addBuilding(mapObject);

                if(buildingType.equals("AMC_1")) {
                    System.out.println("Add new Army Camp for testing");

                    ArmyCamp bar = new ArmyCamp();
                    bar.setId(1);
                    bar.setLevel(2);
                    bar.setPosX(25);
                    bar.setPosY(28);
                    bar.setProcessingStartTime(-1);
                    bar.setType("AMC_1");
                    buildingContainer.addBuilding(bar);

                    bar = new ArmyCamp();
                    bar.setId(2);
                    bar.setLevel(3);
                    bar.setPosX(25);
                    bar.setPosY(33);
                    bar.setProcessingStartTime(-1);
                    bar.setType("AMC_1");
                    buildingContainer.addBuilding(bar);
                }

                if(buildingType.equals("BDH_1")) {
//                    System.out.println("Add new builder hut for testing");
//                    BuilderHut bar = new BuilderHut();
//                    bar.setId(10);
//                    bar.setLevel(1);
//                    bar.setPosX(18);
//                    bar.setPosY(19);
//                    bar.setProcessingStartTime(-1);
//                    bar.setType("BDH_1");
//                    buildingContainer.addBuilding(bar);

//                    bar = new BuilderHut();
//                    bar.setId(20);
//                    bar.setLevel(1);
//                    bar.setPosX(18);
//                    bar.setPosY(17);
//                    bar.setProcessingStartTime(-1);
//                    bar.setType("BDH_1");
//                    buildingContainer.addBuilding(bar);
                }


                if(buildingType.equals("BAR_1")){

//                if(buildingType.equals("BAR_1")){
//                    System.out.println("Add new Barrack for testing");
//                    Barrack bar = new Barrack();
//                    bar.setId(1);
//                    bar.setLevel(2);
//                    bar.setPosX(25);
//                    bar.setPosY(9);
//                    bar.setProcessingStartTime(-1);
//                    bar.setType("BAR_1");
//                    buildingContainer.addBuilding(bar);

//                    bar = new Barrack();
//                    bar.setId(2);
//                    bar.setLevel(4);
//                    bar.setPosX(28);
//                    bar.setPosY(12);
//                    bar.setProcessingStartTime(-1);
//                    bar.setType("BAR_1");
//                    buildingContainer.addBuilding(bar);
                }


            }
            for (String key : Util.getAllKeysJson(obs)) {
                String buildingType = obs.getJSONObject(key).getString(Global.TYPE);
                int posX = obs.getJSONObject(key).getInt(Global.POS_X);
                int posY = obs.getJSONObject(key).getInt(Global.POS_Y);
                Obstacle mapObject = new Obstacle();
                Util.setInitMapObject(mapObject, posX, posY, 1, buildingType, buildingContainer.getBuildingsLength(buildingType));
                buildingContainer.addBuilding(mapObject);
            }
        } catch (Exception err) {
            System.out.println(err.toString());
        }
    }



    private void initPositionMatrix(){
        System.out.println("Init Position Matrix");
        HashMap<String, ArrayList> buildings = this.buildingContainer.getAllBuildings();
        for (String buildingName: Util.getAllBuildingName()){
            if (buildings.get(buildingName) == null){
                continue;
            }
            for (Object object: buildings.get(buildingName)){


                MapDataObject mapDataObject = (MapDataObject) object;
                String type = mapDataObject.getType();

                int size = 0;
                try{
                    size = GameConfig.getInstance().getSizeFromType(type);
                }catch (Exception e){
                    System.out.println("Size not found");
                    e.printStackTrace();
                }

                int posX = mapDataObject.getPosX();
                int posY = mapDataObject.getPosY();
                for (int i = posY; i < posY + size; i++){
                    for (int j = posX; j < posX + size; j++){
                        this.positionMatrix[i][j] = true;
                    }
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public String setName(String name) {
        this.name = name;
        return this.getName();
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public int getElixir() {
        return elixir;
    }

    public void setElixir(int elixir) {
        this.elixir = elixir;
    }

    public int getGoblinLevel() { return this.goblinLevel; }

    public void increaseGoblinLevel() { this.goblinLevel += 1; }

    public void setResource(String typeResource, int value){
        if (typeResource.equals(Global.GOLD)){
            this.setGold(value);
        }else if (typeResource.equals(Global.ELIXIR)){
            this.setElixir(value);
        }else if (typeResource.equals(Global.COIN)) {
            this.setCoin(value);
        }
    }

    public int getResource(String typeResource){
        if (typeResource.equals(Global.GOLD)){
            return this.getGold();
        }else if (typeResource.equals(Global.ELIXIR)){
            return this.getElixir();
        }else {
            return this.getCoin();
        }
    }

    public void minusElixir(int elixir) {
        this.elixir -= elixir;
    }
    public void addElixir(int elixir) {
        this.elixir += elixir;
    }

    public int getDarkElixir() {
        return darkElixir;
    }

    public void setDarkElixir(int darkElixir) {
        this.darkElixir = darkElixir;
    }

    public void setBarrackDatas(ArrayList<BarrackData> barrackDatas) {
        this.barrackDatas = barrackDatas;
    }
    public ArrayList<BarrackData> getBarrackDatas() {
        return barrackDatas;
    }

    public void addTroopsToMap(ArrayList<TroopInfo> troopsOnMap) {
        for (TroopInfo troop: troopsOnMap) {
            boolean isAdded = false;
            for (TroopInfo _troop: this.troopsOnMap) {
                if (troop.getCategory().equals(_troop.getCategory())) {
                    _troop.setQuantity(troop.getQuantity());
                    isAdded = true;
                }
//                else {
//
//                }
            }
            if (!isAdded)
                this.troopsOnMap.add(troop);
        }
//        this.troopsOnMap.addAll(troopsOnMap);
    }
    public void setTroopsOnMap(ArrayList<TroopInfo> troopsOnMap) {
        this.troopsOnMap = troopsOnMap;
    }
    public ArrayList<TroopInfo> getTroopsOnMap() {
        if (troopsOnMap == null)
            troopsOnMap = new ArrayList<>();
        return troopsOnMap;
    }

    public boolean[][] getPositionMatrix() {
        return positionMatrix;
    }

    public BuildingContainer getBuildingContainer() {
        return buildingContainer;
    }

    public void printPositionMatrix(){
        for (int i = 10; i < 20; i++){
            for (int j = 10; j < 20; j++){
                if (positionMatrix[i][j]){
                    System.out.print(1);
                }else{
                    System.out.print(0);
                }
            }
            System.out.println("");
        }
    }

    public void minusCoin(int gCoin) {
        coin -= gCoin;
    }

    public void addCoin(int gCoin) {
        coin += gCoin;
    }
}
