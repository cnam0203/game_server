package module.building;

import config.BuildingName;
import config.GameConfig;
import module.MapDataObject;
import util.Util;

public class Resource extends MapDataObject {
    private long miningStartTime;

    public Resource() {
        super();
        this.miningStartTime = System.currentTimeMillis(); // milisecond
    }

    public int getMiningDeltaTime() {
        long currentTime = System.currentTimeMillis();
        if (this.processingStartTime != -1){
            int timeToUpgrade = Util.getTimeAmountToUpgrade(this.type, this.level);
            if (this.processingStartTime + timeToUpgrade <= currentTime){
                return (int)(this.processingStartTime - this.miningStartTime);
            }else{
                return (int)(currentTime - this.miningStartTime - timeToUpgrade);
            }
        }
        return (int)(currentTime - this.miningStartTime);
    }

    public int getMiningAmount(){
        long currentTime = System.currentTimeMillis();
        long deltaMiningTime = (currentTime - this.miningStartTime);

        System.out.println(deltaMiningTime + " deltaMiningTime");

        String filename = Util.getNameByType(this.type);

        try {
            int productivity = GameConfig.getInstance().getBuildingInfo(filename, this.type, this.level).getInt("productivity");
            int miningAmountMax = GameConfig.getInstance().getBuildingInfo(filename, this.type, this.level).getInt("capacity");
            long miningAmount = deltaMiningTime * productivity / (3600 * 1000);

            // check if over storage of resource
            if (miningAmount >= miningAmountMax){

                miningAmount = miningAmountMax;
            }
            return (int) miningAmount;

        }catch (Exception e){
            e.printStackTrace();
        }

        System.out.println("Get mining Amount failed");
        return 0;

    }

    public void setMiningStartTime(long miningStartTime) {
        this.miningStartTime = miningStartTime;
    }

    public long getMiningStartTime() {
        return miningStartTime;
    }
}
