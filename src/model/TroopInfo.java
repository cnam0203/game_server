package model;

import config.GameConfig;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class TroopInfo {
    private String category;
    private int level;
    private int barrackID;
    private int quantity;
    private JSONObject infoConfig;

    public TroopInfo(String category, int level, int barrackID) {
        this.category = category;
        this.level = level;
        this.barrackID = barrackID;
        this.quantity = 1;
        getTroopInfoFromConfig();
    }

    public TroopInfo(TroopInfo troopInfo) {
        this.category = troopInfo.category;
        this.level = troopInfo.level;
        this.barrackID = troopInfo.barrackID;
        this.quantity = 1;
    }
    public int getBarrackID() {
        return barrackID;
    }

    public void setBarrackID(int barrackID) {
        this.barrackID = barrackID;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void addTroop() {
        this.quantity += 1;
    }

    public void removeTroop() {
        this.quantity -= 1;
    }

    public void setQuantity(int quantity) { this.quantity += quantity; }

    public void removeTroop(int quantity) {
        this.quantity = this.quantity - quantity;
    }

    public void print() {
        System.out.println(category + ": " + quantity);
    }

    public void getTroopInfoFromConfig() {
        try {
            infoConfig = GameConfig.getInstance().getTroopInfo(category, level);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getValueField(String field) {
        try {
            return infoConfig.getInt(field);
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }
    public int getHousingSpace() {
        try {
            return GameConfig.getInstance().getTroopBaseInfo(category).getInt("housingSpace");
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int getTrainingTime() {
        try {
            return GameConfig.getInstance().getTroopBaseInfo(category).getInt("trainingTime");
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int getTotalHousingSpace() {
        return getHousingSpace() * quantity;
    }
}
