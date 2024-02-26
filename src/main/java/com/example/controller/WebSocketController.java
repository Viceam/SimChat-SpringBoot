package com.example.controller;

import com.alibaba.druid.support.json.JSONUtils;
import com.example.pojo.MsgBody;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.example.util.WebSocketUtils.*;

//@RestController
@Controller
@ServerEndpoint("/websocket/{username}/{roomId}")
public class WebSocketController {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username, @PathParam("roomId") Integer roomId){

        ONLINE_USER_SESSIONS.put(username, session);
        if(!ROOM_USERS.containsKey(roomId)) {
            ROOM_USERS.put(roomId, new CopyOnWriteArraySet<>());
        }
        ROOM_USERS.get(roomId).add(username);

        String message = "欢迎用户[" + username + "] 来到聊天室" + roomId +"!";
        logger.info("用户登录：" + message);
//        sendMessageAll(message);
        sendMessageRoom(message, roomId);
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("username") String username, @PathParam("roomId") Integer roomId) throws JsonProcessingException {
        logger.info("发送消息："+ message);
        ObjectMapper objectMapper = new ObjectMapper();
        MsgBody msgBody = objectMapper.readValue(message, MsgBody.class);
//        sendMessageAll("[" + username + "] : " + message);
        sendMessageRoom("[" + username + "] : " + msgBody.getMsg(), roomId);
    }

    @OnError
    public void onError(Session session, Throwable throwable){
        try {
            session.close();
        } catch (IOException e) {
            logger.error("onError exception",e);
        }
        logger.info("Throwable msg "+throwable.getMessage());
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username, @PathParam("roomId") Integer roomId){
        //当前的Session 移除
        ONLINE_USER_SESSIONS.remove(username);
        ROOM_USERS.get(roomId).remove(username);

        //广播
//        sendMessageAll("用户[" + username + "] 离开聊天室了！");
        sendMessageRoom("用户[" + username + "] 离开了聊天室" + roomId, roomId);
        try {
            session.close();
        } catch (IOException e) {
            logger.error("onClose error",e);
        }
    }
}
