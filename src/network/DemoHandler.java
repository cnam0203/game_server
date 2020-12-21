package network;

import bitzero.server.core.IBZEvent;
import bitzero.server.entities.User;
import bitzero.server.extensions.BaseClientRequestHandler;
import bitzero.server.extensions.data.DataCmd;

import cmd.receive.user.*;

import config.ProcessingState;
import cmd.receive.user.RequestTrainingTroop;

import cmd.receive.user.RequestHarvest;
import cmd.receive.user.RequestUpdateObjectPosition;
import event.eventType.DemoEventParam;
import event.eventType.DemoEventType;
import model.PlayerInfo;

import model.TroopInfo;
import module.GameManager;
import module.MapDataObject;
import module.MapObjectManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import sun.plugin.perf.PluginRollup;
import util.Util;
import util.server.ServerConstant;

public class DemoHandler extends BaseClientRequestHandler {
    public static short DEMO_MULTI_IDS = 2000;

    /**
     * log4j level
     * ALL < DEBUG < INFO < WARN < ERROR < FATAL < OFF
     */
    private final Logger logger = LoggerFactory.getLogger("DemoHandler");

    public DemoHandler() {
        super();
    }

    /**
     * this method automatically loaded when run the program
     * register new event, so the core will dispatch event type to this class
     */
    public void init() {
        getParentExtension().addEventListener(DemoEventType.LOGIN_SUCCESS, this);
    }

    @Override
    /**
     * this method handle all client requests with cmdId in range [1000:2999]
     *
     */
    public void handleClientRequest(User user, DataCmd dataCmd) {
        try {
            switch (dataCmd.getId()) {
                // init when open game
                case CmdDefine.GET_INIT_GAME:
                    processGetInitGame(user);
                    break;
                case CmdDefine.GET_TROOPS_ON_MAP:
                    processGetTroopsOnMap(user);
                    break;
                case CmdDefine.GET_TROOPS_IN_BARRACKS:
                    processGetTroopsInBarracks(user);
                    break;
                case CmdDefine.POST_ADD_TRAINING_TROOP:
                    RequestTrainingTroop requestAdd = new RequestTrainingTroop(dataCmd);
                    processAddTrainingTroop(user, requestAdd);
                    break;
                case CmdDefine.POST_FINISH_TRAINING_TROOP:
                    RequestTrainingTroop requestFinish = new RequestTrainingTroop(dataCmd);
                    processFinishTrainingTroop(user, requestFinish);
                    break;
                case CmdDefine.POST_QUICK_TRAINING_TROOP:
                    RequestQuickTrainingTroop requestQuick = new RequestQuickTrainingTroop(dataCmd);
                    processQuickTrainingTroop(user, requestQuick);
                    break;
                case CmdDefine.POST_REMOVE_TRAINING_TROOP:
                    RequestTrainingTroop requestRemove = new RequestTrainingTroop(dataCmd);
                    processRemoveTrainingTroop(user, requestRemove);
                    break;
                case CmdDefine.UPDATE_OBJECT_POSITION:
                    RequestUpdateObjectPosition requestPosition = new RequestUpdateObjectPosition(dataCmd);
                    processUpdateObjectPosition(user, requestPosition);
                    break;

                case CmdDefine.HARVEST:
                    System.out.println("Harvest event");
                    RequestHarvest requestHarvest = new RequestHarvest(dataCmd);
                    processHarvest(user, requestHarvest);
                    break;

                case CmdDefine.UPGRADING_OBJECT:
                    System.out.println("Upgrading event");
                    RequestUpgrading requestUpgrading = new RequestUpgrading(dataCmd);
                    processUpgrading(user, requestUpgrading);
                    break;
                case CmdDefine.FINISH_UPGRADING_OBJECT:
                    System.out.println("Finish upgrading event");
                    RequestUpgrading requestFinishUpgrading = new RequestUpgrading(dataCmd);
                    processFinishUpgrading(user, requestFinishUpgrading);
                    break;
                case CmdDefine.CANCEL_UPGRADING:
                    System.out.println("Cancel upgrading event");
                    RequestUpgrading requestCancelUpgrading = new RequestUpgrading(dataCmd);
                    processCancelUpgrading(user, requestCancelUpgrading);
                    break;
                case CmdDefine.FINISH_PAY_COIN:
                    System.out.println("Finish pay coin event");
                    RequestUpgrading requestFinishPayCoin = new RequestUpgrading(dataCmd);
                    processFinishPayCoin(user, requestFinishPayCoin);
                    break;
                case CmdDefine.CONSTRUCT_BUILDING:
                    System.out.println("Construct building event");
                    RequestConstructBuilding requestConstructBuilding = new RequestConstructBuilding(dataCmd);
                    processConstructBuilding(user, requestConstructBuilding);
                    break;
                case CmdDefine.BUY_RESOURCE:
                    System.out.println("Construct building event");
                    RequestBuyResource requestBuyResource = new RequestBuyResource(dataCmd);
                    processBuyResource(user, requestBuyResource);
                    break;

                case CmdDefine.INCREASE_RESOURCE:
                    System.out.println("Increase Resource event");
                    RequestIncreaseResource requestIncreaseResource = new RequestIncreaseResource(dataCmd);
                    processIncreaseResource(user, requestIncreaseResource);
                    break;

                case CmdDefine.GET_GOBLIN:
                    System.out.println("Get goblin");
                    processGetGoblin(user);
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * events will be dispatch here
     */
    public void handleServerEvent(IBZEvent ibzevent) {
        if (ibzevent.getType() == DemoEventType.LOGIN_SUCCESS) {
            this.processUserLoginSuccess((User) ibzevent.getParameter(DemoEventParam.USER), (String) ibzevent.getParameter(DemoEventParam.NAME));
        }
    }

    private void processGetInitGame(User user) {
        try {
            GameManager gameManager = (GameManager) user.getProperty(ServerConstant.GAME_MANAGER);
//            PlayerInfo userInfo = (PlayerInfo) user.getProperty(ServerConstant.PLAYER_INFO);
            send(new ResponseInitGame(DemoError.SUCCESS.getValue(), gameManager), user);

            //userInfo.saveModel(user.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processGetTroopsOnMap(User user) {
        try {
            GameManager gameManager = (GameManager) user.getProperty(ServerConstant.GAME_MANAGER);
            send(new ResponseGetTroopsOnMap(DemoError.SUCCESS.getValue(), gameManager), user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processGetTroopsInBarracks(User user) {
        try {
            GameManager gameManager = (GameManager) user.getProperty(ServerConstant.GAME_MANAGER);
            send(new ResponseGetTroopsInBarracks(DemoError.SUCCESS.getValue(), gameManager), user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processAddTrainingTroop(User user, RequestTrainingTroop request) {
        try {
            GameManager gameManager = (GameManager) user.getProperty(ServerConstant.GAME_MANAGER);
            boolean canTrain = gameManager.checkCanAddTrainingTroop(request.category, request.level, request.barrackID);
            if (!canTrain) {
                send(new ResponseAddTrainingTroop(DemoError.ERROR.getValue(), gameManager, request.barrackID), user);
            } else {
                synchronized (this) {
                    TroopInfo newTroop = new TroopInfo(request.category, request.level, request.barrackID);
                    int trainingElixir = newTroop.getValueField("trainingElixir");
                    if (gameManager.getPlayerInfo().getElixir() < trainingElixir) {
                        send(new ResponseAddTrainingTroop(DemoError.ERROR.getValue(), gameManager, request.barrackID), user);
                    } else {
                        gameManager.getPlayerInfo().minusElixir(trainingElixir);
                        gameManager.getBarrackManager().addOneTroop(newTroop);
                        System.out.println("ADD TRAINING");
                        gameManager.getBarrackManager().print();
                        send(new ResponseAddTrainingTroop(DemoError.SUCCESS.getValue(), gameManager, request.barrackID), user);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processFinishTrainingTroop(User user, RequestTrainingTroop request) {
        try {
            GameManager gameManager = (GameManager) user.getProperty(ServerConstant.GAME_MANAGER);
            boolean canAddTroopToMap = gameManager.checkCanAddTroopToMap(request.category, request.level, request.barrackID);
            System.out.println("FINISH TRAINING");
            gameManager.getBarrackManager().print();
            if (!canAddTroopToMap) {
                send(new ResponseFinishTrainingTroop(DemoError.ERROR.getValue(), gameManager, request.barrackID), user);
            }
            else {
                if (gameManager.getPlayerInfo().getBuildingContainer().barracks.get(request.barrackID).getProcessingStartTime() < 0) {
                    TroopInfo troopInfo = gameManager.getBarrackManager().removeTroopToAddToMap(request.barrackID);
                    Util.addTroopToArray(gameManager.getPlayerInfo().getTroopsOnMap(), troopInfo);
                    send(new ResponseFinishTrainingTroop(DemoError.SUCCESS.getValue(), gameManager, request.barrackID), user);
                }
                else {
                    send(new ResponseFinishTrainingTroop(DemoError.ERROR.getValue(), gameManager, request.barrackID), user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void processQuickTrainingTroop(User user, RequestQuickTrainingTroop request) {
        try {
            GameManager gameManager = (GameManager) user.getProperty(ServerConstant.GAME_MANAGER);
            if (request.gCoin > gameManager.getPlayerInfo().getCoin()) {
                send(new ResponseQuickTrainingTroop(DemoError.ERROR.getValue(), gameManager, request.barrackID), user);
            }
            else {
                gameManager.quickTrainingTroops(request.gCoin, request.barrackID);
                send(new ResponseQuickTrainingTroop(DemoError.SUCCESS.getValue(), gameManager, request.barrackID), user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processRemoveTrainingTroop(User user, RequestTrainingTroop request) {
        try {
            GameManager gameManager = (GameManager) user.getProperty(ServerConstant.GAME_MANAGER);
            synchronized (this) {
                TroopInfo troop = gameManager.getBarrackManager().removeTrainingTroop(request.category, request.level, request.barrackID);

                if (troop == null) {
                    send(new ResponseRemoveTrainingTroop(DemoError.ERROR.getValue(), gameManager, request.barrackID), user);
                } else {
                    int trainingElixir = troop.getValueField("trainingElixir");
                    gameManager.getPlayerInfo().addElixir(trainingElixir);
                    send(new ResponseRemoveTrainingTroop(DemoError.SUCCESS.getValue(), gameManager, request.barrackID), user);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processUpdateObjectPosition(User user, RequestUpdateObjectPosition request) {
        try {
            GameManager gameManager = (GameManager) user.getProperty(ServerConstant.GAME_MANAGER);
            MapObjectManager mapObjectManager = gameManager.getMapObjectManager();
            short state = mapObjectManager.updateObjectPosition(request);

            send(new ResponseUpdatePosition(state), user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processHarvest(User user, RequestHarvest request) {
        try {
            GameManager gameManager = (GameManager) user.getProperty(ServerConstant.GAME_MANAGER);
            MapObjectManager mapObjectManager = gameManager.getMapObjectManager();
            PlayerInfo playerInfo = gameManager.getPlayerInfo();
            ResponseHarvest response;

            int[] dataHarvest = mapObjectManager.processHarvest(request, playerInfo);
            int state = dataHarvest[0];

            if (state == ProcessingState.HARVEST_SUCCESS) {
                int miningAmount = dataHarvest[1];
                int remainAmount = dataHarvest[2];
                response = new ResponseHarvest(ProcessingState.HARVEST_SUCCESS,
                        request.getType(),
                        request.getId(),
                        miningAmount,
                        playerInfo.getGold(),
                        playerInfo.getElixir(),
                        remainAmount);
            } else {
                response = new ResponseHarvest((short) state, request.getType(), request.getId());
            }
            send(response, user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processUpgrading(User user, RequestUpgrading request) {
        try {
            GameManager gameManager = (GameManager) user.getProperty(ServerConstant.GAME_MANAGER);
            MapObjectManager mapObjectManager = gameManager.getMapObjectManager();
            PlayerInfo playerInfo = gameManager.getPlayerInfo();

            short upgradingResult = mapObjectManager.processUpgrading(request, playerInfo);
            if (upgradingResult == ProcessingState.SATISFIED_ALL_REQUIRED_STATE && request.getType().contains("BAR")) {
                gameManager.checkBarrackIsUpgrading();
            }
            ResponseUpgrading response = new ResponseUpgrading(upgradingResult, request.getType(), request.getId());
            send(response, user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processFinishUpgrading(User user, RequestUpgrading request) {
        try {
            GameManager gameManager = (GameManager) user.getProperty(ServerConstant.GAME_MANAGER);
            MapObjectManager mapObjectManager = gameManager.getMapObjectManager();

            MapDataObject mapDataObject = mapObjectManager.findMapDataObject(request.getType(), request.getId());
            short finishUpgradingResult = mapObjectManager.processFinishUpgrading(request);

            if (finishUpgradingResult == ProcessingState.FINISH_UPGRADING_SUCCESS && request.getType().contains("BAR") && mapDataObject.getLevel() == 1) {

                gameManager.getBarrackManager().addBarrackData(request.getId());
                gameManager.checkBarrackIsUpgrading();
            }
            ResponseFinishUpgrading response = new ResponseFinishUpgrading(finishUpgradingResult, request.getType(), request.getId());
            send(response, user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processCancelUpgrading(User user, RequestUpgrading request) {
        try {
            GameManager gameManager = (GameManager) user.getProperty(ServerConstant.GAME_MANAGER);
            MapObjectManager mapObjectManager = gameManager.getMapObjectManager();
            PlayerInfo playerInfo = gameManager.getPlayerInfo();

//            gameManager.getBarrackManager().print();

            short state = mapObjectManager.processCancelUpgrading(request, playerInfo);
            if (state == ProcessingState.CANCEL_SUCCESS && request.getType().contains("BAR")) {
                gameManager.checkBarrackIsUpgrading();
            }
            ResponseCancelUpgrading response = new ResponseCancelUpgrading(state, request.getType(), request.getId());
            send(response, user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processFinishPayCoin(User user, RequestUpgrading request) {
        try {
            GameManager gameManager = (GameManager) user.getProperty(ServerConstant.GAME_MANAGER);
            MapObjectManager mapObjectManager = gameManager.getMapObjectManager();
            PlayerInfo playerInfo = gameManager.getPlayerInfo();

            MapDataObject mapDataObject = mapObjectManager.findMapDataObject(request.getType(), request.getId());

            short finishPayCoinResult = mapObjectManager.processFinishPayCoin(request, playerInfo);
            if ((finishPayCoinResult == ProcessingState.FINISH_UPGRADING_SUCCESS) && request.getType().contains("BAR") && mapDataObject.getLevel() == 1) {
                gameManager.getBarrackManager().addBarrackData(request.getId());
            }

            ResponseFinishPayCoin response = new ResponseFinishPayCoin(finishPayCoinResult, request.getType(), request.getId());
            send(response, user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processConstructBuilding(User user, RequestConstructBuilding request) {
        GameManager gameManager = (GameManager) user.getProperty(ServerConstant.GAME_MANAGER);
        MapObjectManager mapObjectManager = gameManager.getMapObjectManager();
        PlayerInfo playerInfo = gameManager.getPlayerInfo();

        short result = mapObjectManager.processConstructBuilding(request, playerInfo);
        ResponseConstructBuilding response = new ResponseConstructBuilding(result, request.getType(), request.getId());
        send(response, user);
    }

    private void processBuyResource(User user, RequestBuyResource request){
        GameManager gameManager = (GameManager) user.getProperty(ServerConstant.GAME_MANAGER);
        MapObjectManager mapObjectManager = gameManager.getMapObjectManager();
        PlayerInfo playerInfo = gameManager.getPlayerInfo();

        short result = mapObjectManager.processBuyResource(request, playerInfo);
        ResponseBuyResource response = new ResponseBuyResource(result, request.getTypeResource(), request.getAmount());
        send(response, user);
    }

    private void processIncreaseResource(User user, RequestIncreaseResource request){
        GameManager gameManager = (GameManager) user.getProperty(ServerConstant.GAME_MANAGER);
        MapObjectManager mapObjectManager = gameManager.getMapObjectManager();
        PlayerInfo playerInfo = gameManager.getPlayerInfo();

        int amount = mapObjectManager.processIncreaseResource(request, playerInfo);
        ResponseIncreaseResource response = new ResponseIncreaseResource(request.getTypeResource(), amount);
        send(response, user);
    }

    private void processUserLoginSuccess(User user, String name) {
        /**
         * process event
         */
        logger.warn("processUserLoginSuccess, name = " + name);
    }

    private void processGetGoblin(User user) {
        GameManager gameManager = (GameManager) user.getProperty(ServerConstant.GAME_MANAGER);
        PlayerInfo playerInfo = gameManager.getPlayerInfo();
        playerInfo.increaseGoblinLevel();
        ResponseGetGoblinMap response = new ResponseGetGoblinMap(playerInfo.getGoblinLevel());
        send(response, user);
    }

    public enum DemoError {
        SUCCESS((short) 0),
        ERROR((short) 1),
        PLAYER_INFO_NULL((short) 2),
        EXCEPTION((short) 3);

        private final short value;

        private DemoError(short value) {
            this.value = value;
        }

        public short getValue() {
            return this.value;
        }
    }
}
