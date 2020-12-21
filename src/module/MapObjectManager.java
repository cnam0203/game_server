package module;

import cmd.receive.user.*;
import config.BuildingName;
import config.GameConfig;
import config.Global;
import config.ProcessingState;
import model.PlayerInfo;
import module.building.*;
import network.DemoHandler;
import network.ResponseConstructBuilding;
import network.ResponseHarvest;
import org.json.JSONObject;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import sun.misc.Request;
import util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MapObjectManager {

    private HashMap<String, ArrayList> buildings;
    private boolean[][] positionMatrix;

    public MapObjectManager(BuildingContainer buildingContainer, boolean[][] positionMatrix) {
        this.buildings = buildingContainer.getAllBuildings();
        this.positionMatrix = positionMatrix;
    }

    public short updateObjectPosition(RequestUpdateObjectPosition request) {
        if (!checkValidFileName(request.getType())) {
            System.out.println("Result Update Object Position: INVALID_FILENAME");
            return ProcessingState.INVALID_FILENAME;
        }

        if (!checkValidType(request.getType())) {
            System.out.println("Result Update Object Position: INVALID_TYPE");
            return ProcessingState.INVALID_TYPE;
        }

        // TOW_1 0 20 20
        int size = 0;
        try {
            size = GameConfig.getInstance().getSizeFromType(request.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }

        MapDataObject mapDataObject = findMapDataObject(request.getType(), request.getId());
        if (mapDataObject == null) {
            System.out.println("Result Update Object Position: INVALID_ID");
            return ProcessingState.INVALID_ID;
        }


        // check valid moving
        return checkValidMoving(mapDataObject, request.getPosX(), request.getPosY(), size);
    }

    public int[] processHarvest(RequestHarvest request, PlayerInfo playerInfo) {
        if (!checkValidFileName(request.getType())) {
            System.out.println("Result Harvest: INVALID FILENAME");
            return new int[]{ProcessingState.INVALID_FILENAME};
        }

        if (!checkValidType(request.getType())) {
            System.out.println("Result Harvest: INVALID_TYPE");
            return new int[]{ProcessingState.INVALID_TYPE};
        }

        MapDataObject mapDataObject = findMapDataObject(request.getType(), request.getId());

        if (mapDataObject == null) {
            System.out.println("Result Harvest: INVALID_ID");
            return new int[]{ProcessingState.INVALID_ID};
        }

        Resource resourceObject = (Resource) mapDataObject;

        String filename = Util.getNameByType(request.getType());
        if (!filename.equals(BuildingName.RESOURCE)) {
            System.out.println("Result Harvest: OBJECT_IS_NOT_RESOURCE");
            return new int[]{ProcessingState.OBJECT_IS_NOT_RESOURCE};
        }

        if (mapDataObject.getProcessingStartTime() != -1) {
            System.out.println("Result Harvest: BUILDING_IS_UPGRADING");
            return new int[]{ProcessingState.BUILDING_IS_UPGRADING};
        }

        // harvest
        int miningAmount = resourceObject.getMiningAmount();

        // check if over full storage user
        String typeNameResource = "";
        try {
            typeNameResource = GameConfig.getInstance().getBuildingInfo(BuildingName.RESOURCE, request.getType(), 1).getString("type");
        } catch (Exception e) {
            e.printStackTrace();
        }

        int storageCapacity = getResourceStorage(typeNameResource);
        int townHallCapacity = getTownHallStorage(typeNameResource);
        int currentResourceAmount = 0;

        if (request.getType().equals("RES_1")) {
            currentResourceAmount = playerInfo.getGold();
        } else if (request.getType().equals("RES_2")) {
            currentResourceAmount = playerInfo.getElixir();
        }

        double remaining = 0;
        long timeConsume = 0;

        if (currentResourceAmount == storageCapacity + townHallCapacity) {
            System.out.println("Result Harvest: FULL_CAPACITY");
            return new int[]{ProcessingState.FULL_CAPACITY};
        }

        if (currentResourceAmount + miningAmount > storageCapacity + townHallCapacity) {
            remaining = currentResourceAmount + miningAmount - storageCapacity - townHallCapacity;
            miningAmount = storageCapacity + townHallCapacity - currentResourceAmount;
            // calculate miningStartTime again
            try {
                long productivity = GameConfig.getInstance().getBuildingInfo(
                        filename, request.getType(), resourceObject.getLevel()
                ).getInt("productivity");
                timeConsume = (long) Math.floor((remaining / productivity) * 3600 * 1000);
                resourceObject.setMiningStartTime(System.currentTimeMillis() - timeConsume);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            resourceObject.setMiningStartTime(System.currentTimeMillis());
        }


        System.out.println("Mining Amount: " + miningAmount + ", Remain Amount: " + (int) remaining);
        System.out.println(" ");
        if (request.getType().equals("RES_1")) {
            playerInfo.setGold(playerInfo.getGold() + miningAmount);
        } else if (request.getType().equals("RES_2")) {
            playerInfo.setElixir(playerInfo.getElixir() + miningAmount);
        }

        System.out.println("Result Harvest: HARVEST SUCCESS");
        return new int[]{ProcessingState.HARVEST_SUCCESS, miningAmount, (int) (timeConsume / 1000)};
    }

    public short processUpgrading(RequestUpgrading request, PlayerInfo playerInfo) {
        if (!checkValidFileName(request.getType())) {
            System.out.println("Result Process Upgrading: INVALID_FILENAME");
            return ProcessingState.INVALID_FILENAME;
        }

        if (!checkValidType(request.getType())) {
            System.out.println("Result Process Upgrading: INVALID_TYPE");
            return ProcessingState.INVALID_TYPE;
        }

        MapDataObject mapDataObject = findMapDataObject(request.getType(), request.getId());
        if (mapDataObject == null) {
            System.out.println("Result Process Upgrading: INVALID_ID");
            return ProcessingState.INVALID_ID;
        }

        // Check level of townHall
        if (!checkRequiredTownHallLevel(mapDataObject.getType(), mapDataObject.getLevel())) {
            System.out.println("Result Process Upgrading: TOWNHALL_LEVEL_REQUIRED_STATE");
            return ProcessingState.TOWNHALL_LEVEL_REQUIRED_STATE;
        }

        // Check enough resource
        if (!checkEnoughResource(mapDataObject.getType(), mapDataObject.getLevel(), playerInfo)) {
            System.out.println("Result Process Upgrading: NOT_ENOUGH_RESOURCE");
            return ProcessingState.NOT_ENOUGH_RESOURCE;
        }

        // Check free builder
        Builder freeBuilder = getFreeBuilder();

        if (freeBuilder == null) {
            System.out.println("Result Process Upgrading: NOT_FREE_BUILDER_STATE");
            return ProcessingState.NOT_FREE_BUILDER_STATE;
        } else {
            processUpgradingLogic(freeBuilder, mapDataObject, playerInfo);
            System.out.println("Result Process Upgrading: SATISFIED_ALL_REQUIRED_STATE");
            return ProcessingState.SATISFIED_ALL_REQUIRED_STATE;
        }
    }

    public short processFinishUpgrading(RequestUpgrading request) {
        if (!checkValidFileName(request.getType())) {
            System.out.println("Result Finish Upgrading: INVALID_FILENAME");
            return ProcessingState.INVALID_FILENAME;
        }

        if (!checkValidType(request.getType())) {
            System.out.println("Result Finish Upgrading: INVALID_TYPE");
            return ProcessingState.INVALID_TYPE;
        }

        MapDataObject mapDataObject = findMapDataObject(request.getType(), request.getId());

        if (mapDataObject == null) {
            System.out.println("Result Finish Upgrading: INVALID_ID");
            return ProcessingState.INVALID_ID;
        }

        int timeToUpgrade = Util.getTimeAmountToUpgrade(request.getType(), mapDataObject.getLevel());
        System.out.println("CurrentTime: " + System.currentTimeMillis());
        System.out.println("StartTime + TimeToUpgrade: " + mapDataObject.getProcessingStartTime() + timeToUpgrade * 1000);
        if (mapDataObject.getProcessingStartTime() + timeToUpgrade * 1000 < System.currentTimeMillis()) {
            return processObjectAfterFinish(mapDataObject);
        } else {
            System.out.println("Result Finish Upgrading: UPGRADING_IS_NOT_FINISH");
            return ProcessingState.UPGRADING_IS_NOT_FINISH;
        }
    }

    public short processCancelUpgrading(RequestUpgrading request, PlayerInfo playerInfo) {
        if (!checkValidFileName(request.getType())) {
            System.out.println("Result Cancel Upgrading: INVALID_FILENAME");
            return ProcessingState.INVALID_FILENAME;
        }

        if (!checkValidType(request.getType())) {
            System.out.println("Result Cancel Upgrading: INVALID_TYPE");
            return ProcessingState.INVALID_TYPE;
        }

        MapDataObject mapDataObject = findMapDataObject(request.getType(), request.getId());
        if (mapDataObject == null) {
            System.out.println("Result Cancel Upgrading: INVALID_ID");
            return ProcessingState.INVALID_ID;
        }

        int timeToUpgrade = Util.getTimeAmountToUpgrade(request.getType(), mapDataObject.getLevel());
        System.out.println("Processing Start Time: " + mapDataObject.getProcessingStartTime());
        System.out.println("Time To Upgrade: " + timeToUpgrade * 1000);
        if (mapDataObject.getProcessingStartTime() + timeToUpgrade * 1000 > System.currentTimeMillis()) {
            int requiredGoldAmount = Util.getRequiredGoldAmount(mapDataObject.getType(), mapDataObject.getLevel());
            int requiredElixirAmount = Util.getRequiredElixirAmount(mapDataObject.getType(), mapDataObject.getLevel());

            String typeNameResource = "";  // It can be "gold" or "elixir"
            if (requiredGoldAmount != 0) {
                typeNameResource = Global.GOLD;
            } else {
                typeNameResource = Global.ELIXIR;
            }

            int storageCapacity = getResourceStorage(typeNameResource);
            int townHallCapacity = getTownHallStorage(typeNameResource);
            System.out.println("Full Capacity: " + (storageCapacity + townHallCapacity));

            if (requiredGoldAmount != 0) {
                System.out.println("Current Gold Resource: " + (playerInfo.getGold() + requiredGoldAmount / 2));
                if (playerInfo.getGold() + requiredGoldAmount / 2 > storageCapacity + townHallCapacity) {
                    System.out.println("Result Cancel Upgrading: RESTORE_AMOUNT_TOO_LARGE");
                    return ProcessingState.RESTORE_AMOUNT_TOO_LARGE;
                }

                System.out.println("Restore 1/2 gold");
                mapDataObject.setProcessingStartTime(-1);
                playerInfo.setGold(playerInfo.getGold() + requiredGoldAmount / 2);
            } else {
                System.out.println("Current Elixir Amount: " + (playerInfo.getElixir() + requiredElixirAmount / 2));
                if (playerInfo.getElixir() + requiredElixirAmount / 2 > storageCapacity + townHallCapacity) {
                    System.out.println("Result Cancel Upgrading: RESTORE_AMOUNT_TOO_LARGE");
                    return ProcessingState.RESTORE_AMOUNT_TOO_LARGE;
                }

                System.out.println("Restore 1/2 elixir");
                mapDataObject.setProcessingStartTime(-1);
                playerInfo.setElixir(playerInfo.getElixir() + requiredElixirAmount / 2);
            }
            if (mapDataObject.getLevel() == 0){
                removeMapDataObject(mapDataObject.getType(), mapDataObject.getId());
            }
            freeBuilder(request.getType(), request.getId());
            setMiningStartTimeIfResource(mapDataObject);
            System.out.println("Result Cancel Upgrading: CANCEL_SUCCESS");
            return ProcessingState.CANCEL_SUCCESS;
        } else {
            System.out.println("Result Cancel Upgrading: OBJECT_IS_NOT_UPGRADING");
            return ProcessingState.OBJECT_IS_NOT_UPGRADING;
        }
    }

    public short processFinishPayCoin(RequestUpgrading request, PlayerInfo playerInfo) {

        if (!checkValidFileName(request.getType())) {
            System.out.println("Result Finish Pay Coin: INVALID_FILENAME");
            return ProcessingState.INVALID_FILENAME;
        }

        if (!checkValidType(request.getType())) {
            System.out.println("Result Finish Pay Coin: INVALID_TYPE");
            return ProcessingState.INVALID_TYPE;
        }

        MapDataObject mapDataObject = findMapDataObject(request.getType(), request.getId());
        if (mapDataObject == null) {
            System.out.println("Result Finish Pay Coin: INVALID_ID");
            return ProcessingState.INVALID_ID;
        }

        int timeToUpgrade = Util.getTimeAmountToUpgrade(request.getType(), mapDataObject.getLevel());
        double timeRemaining = (mapDataObject.getProcessingStartTime() + timeToUpgrade * 1000 - System.currentTimeMillis());
        if (timeRemaining >= 0) {
            int currentCoin = playerInfo.getCoin();
            int coinPay = (int) Math.ceil(timeRemaining / 1000 / 600);
            System.out.println("Coin need to pay: " + coinPay);

            if (currentCoin - coinPay >= 0) {
                playerInfo.setCoin(currentCoin - coinPay);
                return processObjectAfterFinish(mapDataObject);
            } else {
                System.out.println("Result Finish Pay Coin: NOT_ENOUGH_COIN");
                return ProcessingState.NOT_ENOUGH_COIN;
            }
        } else {
            System.out.println("Result Finish Pay Coin: UPGRADING_IS_FINISHED");
            return ProcessingState.UPGRADING_IS_FINISHED;
        }
    }

    public short processConstructBuilding(RequestConstructBuilding request, PlayerInfo playerInfo) {
        if (!checkValidFileName(request.getType())) {
            System.out.println("Result Construct Building: INVALID_FILENAME");
            return ProcessingState.INVALID_FILENAME;
        }

        if (!checkValidType(request.getType())) {
            System.out.println("Result Construct Building: INVALID_TYPE");
            return ProcessingState.INVALID_TYPE;
        }

        int size = 0;
        try {
            size = GameConfig.getInstance().getSizeFromType(request.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean validPosition = checkValidPosition(request.getPosX(), request.getPosY(), size, true);
        if (!validPosition) {
            System.out.println("Result Construct Building: INVALID_POSITION");
            return ProcessingState.INVALID_POSITION;
        }

        String filename = Util.getNameByType(request.getType());
        if (!filename.equals(BuildingName.BUILDER_HUT)){

            boolean validConstructable = checkValidConstructable(request.getType());
            if (!validConstructable) {
                System.out.println("Result Construct Building: UNCONSTRUCTABLE_BUILDING");
                return ProcessingState.UNCONSTRUCTABLE_BUILDING;
            }

            boolean validRequiredLevel = checkRequiredTownHallLevel(request.getType(), 0);
            if (!validRequiredLevel) {
                System.out.println("Result Construct Building: INVALID_REQUIRED_LEVEL");
                return ProcessingState.INVALID_REQUIRED_LEVEL;
            }

            boolean validLimitBuilding = checkValidLimitBuilding(request.getType());
            if (!validLimitBuilding) {
                System.out.println("Result Construct Building: BUILDING_IS_LIMITED");
                return ProcessingState.BUILDING_IS_LIMITED;
            }

            boolean enoughResource = checkEnoughResource(request.getType(), 0, playerInfo);
            if (!enoughResource) {
                System.out.println("Result Construct Building: NOT_ENOUGH_RESOURCE");
                return ProcessingState.NOT_ENOUGH_RESOURCE;
            }

            Builder freeBuilder = getFreeBuilder();
            if (freeBuilder == null) {
                System.out.println("Result Construct Building: NOT_FREE_BUILDER_STATE");
                return ProcessingState.NOT_FREE_BUILDER_STATE;
            } else {
                createMapDataObject(request, playerInfo, freeBuilder);
                System.out.println("Result Construct Building: SATISFIED_ALL_REQUIRED_STATE");
                return ProcessingState.CONSTRUCT_SUCCESS;
            }
        }else{
            boolean enoughCoin = checkEnoughCoinForBuilderHut(request.getType(), playerInfo);
            if (enoughCoin){
                createBuilderHutObject(request, playerInfo);
                System.out.println("Result Construct Builder Hut: SATISFIED_ALL_REQUIRED_STATE");
                return ProcessingState.CONSTRUCT_SUCCESS;
            }else{
                System.out.println("Result Construct Builder Hut: NOT_ENOUGH_RESOURCE");
                return ProcessingState.NOT_ENOUGH_RESOURCE;
            }
        }

    }

    private void createBuilderHutObject(RequestConstructBuilding request, PlayerInfo playerInfo){
        try {
            Class buildingClass = Class.forName("module.building.BuilderHut");
            Object mapObject = buildingClass.newInstance();
            int id = this.buildings.get(BuildingName.BUILDER_HUT).size();
            request.setId(id);

            Util.setInitMapObject(mapObject, request.getPosX(), request.getPosY(), 1, request.getType(), request.getId());
            MapDataObject mapDataObject = (MapDataObject) mapObject;

            fillBuildingPosition(request.getType(), request.getPosX(), request.getPosY(), true);
            playerInfo.getBuildingContainer().addBuilding(mapDataObject);

            int payCoin = GameConfig.getInstance().getBuildingInfo(BuildingName.BUILDER_HUT, request.getType(), request.getId() + 1).getInt("coin");
            int currentCoin = playerInfo.getCoin();
            playerInfo.setCoin(currentCoin - payCoin);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void createMapDataObject(RequestConstructBuilding request, PlayerInfo playerInfo, Builder freeBuilder){
        try {
            String filename = Util.getNameByType(request.getType());
            Class buildingClass = Class.forName("module.building." + filename);
            Object mapObject = buildingClass.newInstance();
            int id = findIdByType(request.getType());
            request.setId(id);
            Util.setInitMapObject(mapObject, request.getPosX(), request.getPosY(), 0, request.getType(), request.getId());

            MapDataObject mapDataObject = (MapDataObject) mapObject;

            fillBuildingPosition(request.getType(), request.getPosX(), request.getPosY(), true);
            playerInfo.getBuildingContainer().addBuilding(mapDataObject);
            processUpgradingLogic(freeBuilder, mapDataObject, playerInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public short processObjectAfterFinish(MapDataObject mapDataObject) {
        String type = mapDataObject.getType();
        int id = mapDataObject.getId();
        freeBuilder(type, id);
        String filename = Util.getNameByType(type);
        if (filename.equals(BuildingName.OBSTACLE)) {
            removeMapDataObject(type, id);
            System.out.println("Result Finish Upgrading: REMOVING_OBSTACLE_SUCCESS");
            return ProcessingState.REMOVING_OBSTACLE_SUCCESS;
        } else {
            mapDataObject.setLevel(mapDataObject.getLevel() + 1);
            setMiningStartTimeIfResource(mapDataObject);
            mapDataObject.setProcessingStartTime(-1);
            System.out.println("Result Finish Upgrading: FINISH_UPGRADING_SUCCESS");
            return ProcessingState.FINISH_UPGRADING_SUCCESS;
        }
    }

    private void processUpgradingLogic(Builder freeBuilder, MapDataObject mapDataObject, PlayerInfo playerInfo){
        // Reduce resource
        int requiredGoldAmount = Util.getRequiredGoldAmount(mapDataObject.getType(), mapDataObject.getLevel());
        int requiredElixirAmount = Util.getRequiredElixirAmount(mapDataObject.getType(), mapDataObject.getLevel());
        playerInfo.setGold(playerInfo.getGold() - requiredGoldAmount);
        playerInfo.setElixir(playerInfo.getElixir() - requiredElixirAmount);

        if (mapDataObject instanceof Wall){
            mapDataObject.setLevel(mapDataObject.getLevel() + 1);
        }else{
            freeBuilder.setAvailable(false);
            freeBuilder.setCurrentBuildingWorking(mapDataObject);
            mapDataObject.setProcessingStartTime(System.currentTimeMillis());
        }

    }

    public short processBuyResource(RequestBuyResource request, PlayerInfo playerInfo){
        String typeResource = request.getTypeResource();
        if (!typeResource.equals(Global.GOLD) && !typeResource.equals(Global.ELIXIR)){
            System.out.println("Result Buy Resource: INVALID_TYPE_RESOURCE");
            return ProcessingState.INVALID_TYPE_RESOURCE;
        }

        if (!checkValidCapacityBuyResource(typeResource, request.getAmount(), playerInfo)){
            System.out.println("Result Buy Resource: BUY_RESOURCE_OVER_CAPACITY");
            return ProcessingState.BUY_RESOURCE_OVER_CAPACITY;
        }

        int currentCoin = playerInfo.getCoin();
        int coinPay = (int)Math.ceil(request.getAmount() / 100);
        if (coinPay > currentCoin){
            System.out.println("Result Buy Resource: NOT_ENOUGH_COIN");
            return ProcessingState.NOT_ENOUGH_COIN;
        }else{
            int currentAmount = playerInfo.getResource(typeResource);
            playerInfo.setCoin(currentCoin - coinPay);
            playerInfo.setResource(typeResource, currentAmount + request.getAmount());
            System.out.println("Result Buy Resource: BUY_RESOURCE_SUCCESS");
            return ProcessingState.BUY_RESOURCE_SUCCESS;
        }
    }

    public int processIncreaseResource(RequestIncreaseResource request, PlayerInfo playerInfo){
        if (request.getTypeResource().equals(Global.COIN)){
            playerInfo.setCoin(playerInfo.getCoin() + 10000);
            return playerInfo.getCoin();
        }else {
            int fullCapacity = getResourceStorage(request.getTypeResource()) + getTownHallStorage(request.getTypeResource());
            playerInfo.setResource(request.getTypeResource(), fullCapacity);
            return fullCapacity;
        }
    }

    public short checkValidMoving(MapDataObject mapDataObject, int posX, int posY, int size) {
        // remove position in matrix
        fillBuildingPosition(mapDataObject.getType(), mapDataObject.getPosX(), mapDataObject.getPosY(), false);
        // check valid moving
        boolean validPosition = checkValidPosition(posX, posY, size, true);

        if (validPosition) {
            // valid moving
            fillBuildingPosition(mapDataObject.getType(), posX, posY, true);
            mapDataObject.posX = posX;
            mapDataObject.posY = posY;
            System.out.println("Result Update Object Position: VALID_MOVING");
            return ProcessingState.VALID_MOVING;
        } else {
            // un-valid moving -> restore
            fillBuildingPosition(mapDataObject.getType(), mapDataObject.getPosX(), mapDataObject.getPosY(), true);
            System.out.println("Result Update Object Position: INVALID_MOVING");
            return ProcessingState.INVALID_MOVING;
        }
    }

    private void fillBuildingPosition(String type, int posX, int posY, boolean value) {
        int size = 0;
        try {
            size = GameConfig.getInstance().getSizeFromType(type);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = posY; i < posY + size; i++) {
            for (int j = posX; j < posX + size; j++) {
                positionMatrix[i][j] = value;
            }
        }
    }

    private boolean checkEnoughResource(String type, int level, PlayerInfo playerInfo) {
        int requiredGoldAmount = Util.getRequiredGoldAmount(type, level);
        int requiredElixirAmount = Util.getRequiredElixirAmount(type, level);

        System.out.println("requiredGoldAmount: " + requiredGoldAmount + ", requiredElixirAmount: " + requiredElixirAmount);
        System.out.println("currentGold: " + playerInfo.getGold() + ", currentElixir: " + playerInfo.getElixir());

        if (requiredGoldAmount != 0) {
            int currentGold = playerInfo.getGold();
            return currentGold >= requiredGoldAmount;
        } else {
            int currentElixir = playerInfo.getElixir();
            return currentElixir >= requiredElixirAmount;
        }

    }

    private int findIdByType(String type){
        String filename = Util.getNameByType(type);

        int maxId = 0;
        ArrayList buildingList = this.buildings.get(filename);
        for (Object object: buildingList){
            MapDataObject mapDataObject = (MapDataObject) object;
            if (mapDataObject.getId() > maxId){
                maxId = mapDataObject.getId();
            }
        }

        boolean[] idList = new boolean[maxId + 2];
        for (Object object: buildingList){
            MapDataObject mapDataObject = (MapDataObject) object;
            idList[mapDataObject.getId()] = true;
        }

        for (int i = 0; i < idList.length; i++) {
            if (!idList[i]) {
                return i;
            }
        }
        return 0;
    }

    private boolean checkRequiredTownHallLevel(String type, int level) {
        TownHall townHall = (TownHall) findMapDataObject("TOW", 0);
        int townHallLevel = townHall.getLevel();
        int townHallRequiredLevel = Util.getTownHallLevelRequired(type, level);
        System.out.println("TownHallLevel: " + townHallLevel + ", TownHallRequiredLevel: " + townHallRequiredLevel);
        return townHallLevel >= townHallRequiredLevel;
    }

    private boolean checkValidPosition(int posX, int posY, int size, boolean value) {
        for (int i = posY; i < posY + size; i++) {
            for (int j = posX; j < posX + size; j++) {
                if (positionMatrix[i][j] == value) {
                    return false;
                }
            }
        }
        return true;
    }

    private Builder getFreeBuilder() {
        ArrayList builderHutList = this.buildings.get(BuildingName.BUILDER_HUT);
        for (Object object : builderHutList) {
            BuilderHut builderHut = (BuilderHut) object;
            Builder builder = builderHut.getBuilder();
            if (builder.isAvailable()) {
                return builder;
            }
        }
        return null;
    }

    private boolean checkValidConstructable(String type) {
        boolean isConstructable = false;
        try {
            isConstructable = GameConfig.getInstance().getBuildingInfo(BuildingName.TOWN_HALL, "TOW_1", 1).has(type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isConstructable;
    }

    public MapDataObject findMapDataObject(String type, int id) {
        String filename = Util.getNameByType(type);
        ArrayList mapDataObjectList = this.buildings.get(filename);

        for (Object object : mapDataObjectList) {
            MapDataObject mapDataObject = (MapDataObject) object;
            if (mapDataObject.id == id) {
                return mapDataObject;
            }
        }
        return null;
    }

    private boolean removeMapDataObject(String type, int id) {
        String filename = Util.getNameByType(type);
        ArrayList objectList = this.buildings.get(filename);
        for (Object object : objectList) {
            MapDataObject mapDataObject = (MapDataObject) object;
            if (mapDataObject.getId() == id && mapDataObject.getType().equals(type)) {
                fillBuildingPosition(mapDataObject.getType(), mapDataObject.getPosX(), mapDataObject.getPosY(), false);
                objectList.remove(object);
                System.out.println("Remove successful Object with type: " + type + ", id: " + id);
                return true;
            }
        }
        return false;
    }

    private int getResourceStorage(String typeNameResource) {

        int fullCapacity = 0;
        try {
            ArrayList storageObjectList = this.buildings.get(BuildingName.STORAGE);
            for (Object object : storageObjectList) {

                Storage storageObject = (Storage) object;
                String typeNameStorage = "";
                typeNameStorage = GameConfig.getInstance().getBuildingInfo(BuildingName.STORAGE, storageObject.getType(), 1).getString("type");
                if (typeNameResource.equals(typeNameStorage)) {
                    if (storageObject.getLevel() == 0){
                        continue;
                    }
                    int capacity = GameConfig.getInstance().getBuildingInfo(BuildingName.STORAGE, storageObject.getType(), storageObject.getLevel()).getInt("capacity");
                    fullCapacity += capacity;
                } else {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fullCapacity;
    }

    private int getTownHallStorage(String typeNameResource) {
        int capacity = 0;
        try {
            ArrayList storageObjectList = this.buildings.get(BuildingName.TOWN_HALL);
            TownHall townHallObject = (TownHall) storageObjectList.get(0);
            if (typeNameResource.equals(Global.GOLD)) {
                capacity = GameConfig.getInstance().getBuildingInfo(
                        BuildingName.TOWN_HALL, townHallObject.getType(), townHallObject.getLevel()
                ).getInt("capacityGold");
            } else if (typeNameResource.equals(Global.ELIXIR)) {
                capacity = GameConfig.getInstance().getBuildingInfo(
                        BuildingName.TOWN_HALL, townHallObject.getType(), townHallObject.getLevel()
                ).getInt("capacityElixir");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return capacity;
    }

    private void freeBuilder(String type, int id) {
        ArrayList builderHutList = this.buildings.get(BuildingName.BUILDER_HUT);
        for (Object object : builderHutList) {
            BuilderHut builderHut = (BuilderHut) object;
            Builder builder = builderHut.getBuilder();
            MapDataObject mapDataObject = builder.getCurrentBuildingWorking();
            if (mapDataObject != null) {
                if (mapDataObject.getType().equals(type) && mapDataObject.getId() == id) {
                    builder.setAvailable(true);
                    builder.setCurrentBuildingWorking(null);
                }
            }
        }
    }

    private void setMiningStartTimeIfResource(MapDataObject mapDataObject) {
        String filename = Util.getNameByType(mapDataObject.getType());
        if (filename.equals(BuildingName.RESOURCE)) {
            Resource resource = (Resource) mapDataObject;
            resource.setMiningStartTime(System.currentTimeMillis() - (resource.getProcessingStartTime() - resource.getMiningStartTime()));
        }
    }

    private boolean checkValidFileName(String type) {
        String filename = Util.getNameByType(type);
        return !filename.equals(Global.ERROR_TYPE);
    }

    private boolean checkValidType(String type) {
        String filename = Util.getNameByType(type);
        JSONObject building = GameConfig.getInstance().getBuilding();
        try {
            return building.getJSONObject(filename).has(type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkValidLimitBuilding(String type){
        String filename = Util.getNameByType(type);
        ArrayList ObjectList = this.buildings.get(filename);
        int buildingNumber = 0;
        for (Object object: ObjectList){
            MapDataObject mapDataObject = (MapDataObject) object;
            if (mapDataObject.getType().equals(type)){
                buildingNumber += 1;
            }
        }
        TownHall townHall = (TownHall) this.buildings.get(BuildingName.TOWN_HALL).get(0);
        int level = townHall.getLevel();

        try{
            int buildingLimit = GameConfig.getInstance().getBuildingInfo(BuildingName.TOWN_HALL, "TOW_1", level).getInt(type);
            System.out.println("Building type: " + filename + " have " + buildingNumber + " with limit: " + buildingLimit);
            return buildingLimit > buildingNumber;

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkValidCapacityBuyResource(String typeResource, int amount, PlayerInfo playerInfo){
        int maxCapacity = getResourceStorage(typeResource) + getTownHallStorage(typeResource);

        if (typeResource.equals(Global.GOLD)){
            int currentGold = playerInfo.getGold();
            if (currentGold + amount > maxCapacity){
                return false;
            }
        }else{
            int currentElixir = playerInfo.getElixir();
            if (currentElixir + amount > maxCapacity){
                return false;
            }
        }
        return true;
    }

    private boolean checkEnoughCoinForBuilderHut(String type, PlayerInfo playerInfo){
        int builderHutNumber = this.buildings.get(BuildingName.BUILDER_HUT).size();
        try {
            int payCoin = GameConfig.getInstance().getBuildingInfo(BuildingName.BUILDER_HUT, type, builderHutNumber + 1).getInt("coin");
            int currentCoin = playerInfo.getCoin();
            return currentCoin >= payCoin;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkValidLevel(String type, int level) {
        String filename = Util.getNameByType(type);
        JSONObject building = GameConfig.getInstance().getBuilding();
        try {
            return building.getJSONObject(filename).getJSONObject(type).has(Integer.toString(level));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public HashMap<String, ArrayList> getBuildings() {
        return buildings;
    }
}
