package module;

public class Builder {
    private boolean isAvailable;
    private MapDataObject currentBuildingWorking;
    public Builder(){
        this.isAvailable = true;
        this.currentBuildingWorking = null;
    }


    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public MapDataObject getCurrentBuildingWorking() {
        return currentBuildingWorking;
    }

    public void setCurrentBuildingWorking(MapDataObject currentBuildingWorking) {
        this.currentBuildingWorking = currentBuildingWorking;
    }
}
