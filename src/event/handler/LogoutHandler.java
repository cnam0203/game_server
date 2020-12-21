package event.handler;

import bitzero.server.core.BZEventParam;
import bitzero.server.core.IBZEvent;
import bitzero.server.entities.User;
import bitzero.server.extensions.BaseServerEventHandler;

import model.PlayerInfo;
import module.GameManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.server.ServerConstant;

public class LogoutHandler extends BaseServerEventHandler {

    private final Logger logger = LoggerFactory.getLogger("LogoutHandler");

    public LogoutHandler() {
        super();
    }

    public void handleServerEvent(IBZEvent iBZEvent) {
        this.onLogOut((User) iBZEvent.getParameter(BZEventParam.USER));
    }

    private void onLogOut(User user) {
//        LogObject logObject = new LogObject(LogObject.ACTION_LOGOUT);
//        logObject.zingId = Long.valueOf((String) user.getProperty("zingId"));
//        logObject.zingName = (String) user.getProperty("zingName");
//        logObject.accountType = (Integer) user.getProperty("accountType");
//        logObject.openAccount = (String) user.getProperty("openAccount");
//        long creationTime = 0;
//        if (user.getSession() != null)
//            creationTime = System.currentTimeMillis() - user.getSession().getCreationTime();
//        logObject.quantity = Math.round(creationTime / 1000);
//        //System.out.println("Log logout = " + logObject.getLogMessage() + "\nCreation time = " + creationTime);
//        MetricLog.writeActionLog(logObject);
        GameManager gameManager = (GameManager) user.getProperty(ServerConstant.GAME_MANAGER);
        PlayerInfo userInfo = (PlayerInfo) user.getProperty(ServerConstant.PLAYER_INFO);
        gameManager.setBarrackDatas(userInfo);
//        userInfo.getBarrackManager().print();
        try {
            System.out.println("Log out");
            userInfo.saveModel(user.getId());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


}
