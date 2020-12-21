package module;

public class MapDataObject {
    protected String type;
    protected int id;
    protected int level;
    protected long processingStartTime = -1;
    protected int posX;
    protected int posY;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getProcessingStartTime() {
        return processingStartTime;
    }

    public int getDeltaProcessingStartTime(){
        if (this.processingStartTime == -1){
            return -1;
        }else {
            return (int) (System.currentTimeMillis() - this.processingStartTime);
        }
    }

    public void setProcessingStartTime(long processingStartTime) {
        this.processingStartTime = processingStartTime;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public void setPoint(int x, int y){
        this.posX = x;
        this.posY = y;
    }
}
