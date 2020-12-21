package config;

public class ProcessingState {
    public static final int INVALID_OBJECT = 0;

    // Error
    public static final short INVALID_FILENAME = 100;
    public static final short INVALID_TYPE = 101;
    public static final short INVALID_ID = 102;

    public static final short TOWNHALL_LEVEL_REQUIRED_STATE = 1;
    public static final short NOT_ENOUGH_RESOURCE = 2;
    public static final short NOT_FREE_BUILDER_STATE = 3;
    public static final short SATISFIED_ALL_REQUIRED_STATE = 4;

    // Moving state
    public static final short VALID_MOVING = 0;
    public static final short INVALID_MOVING = 1;

    // Cancel state
    public static final short CANCEL_SUCCESS = 0;
    public static final short OBJECT_IS_NOT_UPGRADING = 1;
    public static final short RESTORE_AMOUNT_TOO_LARGE = 2;

    // Harvest state
    public static final short HARVEST_SUCCESS = 0;
    public static final short OBJECT_IS_NOT_RESOURCE = 1;
    public static final short BUILDING_IS_UPGRADING = 2;
    public static final short FULL_CAPACITY = 3;

    // Construct building
    public static final short CONSTRUCT_SUCCESS = 0;
    public static final short INVALID_POSITION = 1;
//    public static final short NOT_ENOUGH_RESOURCE = 2;
//    public static final short NOT_FREE_BUILDER_STATE = 3;
    public static final short UNCONSTRUCTABLE_BUILDING = 4;
    public static final short INVALID_REQUIRED_LEVEL = 5;
    public static final short BUILDING_IS_LIMITED = 6;

    // Finish Upgrading
    public static final short FINISH_UPGRADING_SUCCESS = 0;
    public static final short REMOVING_OBSTACLE_SUCCESS = 1;
    public static final short UPGRADING_IS_NOT_FINISH = 2;

    // Finish Pay Coin
//    public static final short FINISH_UPGRADING_SUCCESS = 0;
//    public static final short REMOVING_OBSTACLE_SUCCESS = 1;
    public static final short NOT_ENOUGH_COIN = 2;
    public static final short UPGRADING_IS_FINISHED = 3;

    // Buy resource
    public static final short BUY_RESOURCE_SUCCESS = 0;
    public static final short INVALID_TYPE_RESOURCE = 1;
//    public static final short NOT_ENOUGH_COIN = 2;
    public static final short BUY_RESOURCE_OVER_CAPACITY = 3;

}
