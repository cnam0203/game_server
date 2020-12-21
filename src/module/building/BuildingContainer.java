package module.building;

import config.BuildingName;
import util.Util;

import java.awt.event.WindowAdapter;
import java.util.ArrayList;
import java.util.HashMap;

public class BuildingContainer {
    public ArrayList<ArmyCamp> armyCamps;
    public ArrayList<Barrack> barracks;
    public ArrayList<BuilderHut> builderHuts;
    public ArrayList<ClanCastle> clanCastles;
    public ArrayList<Defence> defences;
    public ArrayList<Obstacle> obstacles;
    public ArrayList<Resource> resources;
    public ArrayList<Storage> storages;
    public ArrayList<TownHall> townHalls;
    public ArrayList<Wall> walls;

    public BuildingContainer(){
        armyCamps = new ArrayList<>();
        barracks = new ArrayList<>();
        builderHuts = new ArrayList<>();
        clanCastles = new ArrayList<>();
        defences = new ArrayList<>();
        obstacles = new ArrayList<>();
        resources = new ArrayList<>();
        storages = new ArrayList<>();
        townHalls = new ArrayList<>();
        walls = new ArrayList<>();
    }
    public void addBuilding(Object building){
        if (building instanceof ArmyCamp) armyCamps.add((ArmyCamp) building);
        if (building instanceof Barrack) barracks.add((Barrack) building);
        if (building instanceof BuilderHut) builderHuts.add((BuilderHut) building);
        if (building instanceof ClanCastle) clanCastles.add((ClanCastle) building);
        if (building instanceof Defence) defences.add((Defence) building);
        if (building instanceof Obstacle) obstacles.add((Obstacle) building);
        if (building instanceof Resource) resources.add((Resource) building);
        if (building instanceof Storage) storages.add((Storage) building);
        if (building instanceof TownHall) townHalls.add((TownHall) building);
        if (building instanceof Wall) walls.add((Wall) building);
    }
    public int getBuildingsLength(String type){
        String filename = Util.getNameByType(type);
        if (filename.equals(BuildingName.ARMY_CAMP)) return armyCamps.size();
        if (filename.equals(BuildingName.BARRACK)) return barracks.size();
        if (filename.equals(BuildingName.BUILDER_HUT)) return builderHuts.size();
        if (filename.equals(BuildingName.CLAN_CASTLE)) return clanCastles.size();
        if (filename.equals(BuildingName.DEFENCE)) return defences.size();
        if (filename.equals(BuildingName.OBSTACLE)) return obstacles.size();
        if (filename.equals(BuildingName.RESOURCE)) return resources.size();
        if (filename.equals(BuildingName.STORAGE)) return storages.size();
        if (filename.equals(BuildingName.TOWN_HALL)) return townHalls.size();
        if (filename.equals(BuildingName.WALL)) return walls.size();
        return 0;
    }
    public HashMap<String, ArrayList> getAllBuildings(){
        HashMap<String, ArrayList> buildings = new HashMap<>();
        buildings.put(BuildingName.ARMY_CAMP, armyCamps);
        buildings.put(BuildingName.BARRACK, barracks);
        buildings.put(BuildingName.BUILDER_HUT, builderHuts);
        buildings.put(BuildingName.CLAN_CASTLE, clanCastles);
        buildings.put(BuildingName.DEFENCE, defences);
        buildings.put(BuildingName.OBSTACLE, obstacles);
        buildings.put(BuildingName.RESOURCE, resources);
        buildings.put(BuildingName.STORAGE, storages);
        buildings.put(BuildingName.TOWN_HALL, townHalls);
        buildings.put(BuildingName.WALL, walls);
        return buildings;
    }
}
