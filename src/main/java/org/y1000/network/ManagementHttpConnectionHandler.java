package org.y1000.network;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.management.NotificationRequest;
import org.y1000.management.NotificationResponse;
import org.y1000.realm.RealmManager;
import org.y1000.util.Action;

@Slf4j
public final class ManagementHttpConnectionHandler extends AbstractHttpConnectionHandler {
    private final RealmManager manager;

    private final Action shutdownHandler;

    public ManagementHttpConnectionHandler(RealmManager manager, Action shutdownHandler) {
        this.manager = manager;
        this.shutdownHandler = shutdownHandler;
    }

    @Override
    Logger log() {
        return log;
    }

    private NotificationResponse handleNotify(String body) {
        NotificationRequest request = parseJson(body, NotificationRequest.class);
        manager.sendNotification(request.getContent());
        return new NotificationResponse(0);
    }

    private String handleKick(String body) {
        manager.testKick();
        return "Ok";
    }

    @Override
    protected Object handle(String type, String body) {
        return switch (type.toLowerCase()) {
            case "notify" -> handleNotify(body);
            case "kick" -> handleKick(body);
            case "shutdown" -> {
                shutdownHandler.invoke();
                yield "Ok";
            }
            default -> "Not supported type: " + type;
        };
    }
}
