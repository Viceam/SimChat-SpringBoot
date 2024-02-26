package com.example.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketUtils {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketUtils.class);

    // 存储 websocket session username->session
    public static final Map<String, Session> ONLINE_USER_SESSIONS = new ConcurrentHashMap<>();

    // 存储 roomId->username
    public static final Map<Integer, Set<String>> ROOM_USERS = new ConcurrentHashMap<>();

    public static void sendMessage(Session session, String message) {
        if(session == null) return;
        final RemoteEndpoint.Basic basic = session.getBasicRemote();
        if (basic == null) {
            return;
        }
        try {
            basic.sendText(message);
        } catch (IOException e) {
            logger.error("sendMessage IOException ",e);
        }
    }

    public static void sendMessageAll(String message){
        ONLINE_USER_SESSIONS.forEach((user, session) -> sendMessage(session, message));
    }

    public static void sendMessageRoom(String message, Integer roomId) {
        Set<String> users = ROOM_USERS.get(roomId);
        for (String user : users) {
            Session session = ONLINE_USER_SESSIONS.get(user);
            sendMessage(session, message);
        }
    }
}
