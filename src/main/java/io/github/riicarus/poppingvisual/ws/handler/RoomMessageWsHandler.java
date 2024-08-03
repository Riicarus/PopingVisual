package io.github.riicarus.poppingvisual.ws.handler;

import com.alibaba.fastjson.JSON;
import io.github.riicarus.poppingvisual.inner.room.RoomChannelTypeEnum;
import io.github.riicarus.poppingvisual.ws.message.PoppingWebSocketMessage;
import io.github.riicarus.poppingvisual.ws.message.WebSocketMessageOpEnum;
import io.github.riicarus.poppingvisual.inner.room.RoomChannelSessionManager;
import io.github.riicarus.poppingvisual.inner.room.WebSocketAttributeKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;

/**
 * handler for room's message channel
 *
 * @author Riicarus
 * @create 2024-8-3 16:37
 * @since 1.0.0
 */
@Component
@Slf4j
public class RoomMessageWsHandler extends TextWebSocketHandler {

    private static final RoomChannelTypeEnum CHANNEL_TYPE = RoomChannelTypeEnum.MESSAGE;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String roomId = (String) session.getAttributes().get(WebSocketAttributeKeys.ROOM_ID);
        String uid = (String) session.getAttributes().get(WebSocketAttributeKeys.UID);

        log.info("[message websocket] receive message: sessionId={}, roomId={}, uid={}, message={}",
                session.getId(), roomId, uid, message.getPayload());

        PoppingWebSocketMessage recvMsg = JSON.parseObject(message.getPayload(), PoppingWebSocketMessage.class);

        if (WebSocketMessageOpEnum.CLOSE.equals(recvMsg.getOp())) {
            try {
                session.close(CloseStatus.NORMAL);
            } catch (IOException e) {
                log.warn("close websocket session failed: cause={}", e.getMessage());
            }
            return;
        }

        PoppingWebSocketMessage sendMsg = new PoppingWebSocketMessage(
                WebSocketMessageOpEnum.NORMAL,
                recvMsg.getContent(),
                recvMsg.getSenderId(),
                System.currentTimeMillis()
        );
        RoomChannelSessionManager.sendToExclude(roomId, CHANNEL_TYPE, Collections.singletonList(uid), sendMsg);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String roomId = (String) session.getAttributes().get(WebSocketAttributeKeys.ROOM_ID);
        String uid = (String) session.getAttributes().get(WebSocketAttributeKeys.UID);
        String username = (String) session.getAttributes().get(WebSocketAttributeKeys.USERNAME);

        RoomChannelSessionManager.register(roomId, CHANNEL_TYPE, session);

        log.info("[message websocket] connection established: sessionId={}, roomId={}, uid={}",
                session.getId(), roomId, uid);

        PoppingWebSocketMessage msg = new PoppingWebSocketMessage(
                WebSocketMessageOpEnum.NORMAL,
                username + " enter the room",
                PoppingWebSocketMessage.SYSTEM_SENDER,
                System.currentTimeMillis()
        );
        RoomChannelSessionManager.sendToAll(roomId, CHANNEL_TYPE, msg);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String roomId = (String) session.getAttributes().get(WebSocketAttributeKeys.ROOM_ID);
        String uid = (String) session.getAttributes().get(WebSocketAttributeKeys.UID);
        String username = (String) session.getAttributes().get(WebSocketAttributeKeys.USERNAME);

        RoomChannelSessionManager.register(roomId, CHANNEL_TYPE, session);

        log.info("[message websocket] connection closed: sessionId={}, roomId={}, uid={}, cause={}",
                session.getId(), roomId, uid, status.getReason());

        RoomChannelSessionManager.unregister(roomId, CHANNEL_TYPE, session);

        PoppingWebSocketMessage msg = new PoppingWebSocketMessage(
                WebSocketMessageOpEnum.NORMAL,
                username + " exit the room",
                PoppingWebSocketMessage.SYSTEM_SENDER,
                System.currentTimeMillis()
        );
        RoomChannelSessionManager.sendToAll(roomId, CHANNEL_TYPE, msg);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        String roomId = (String) session.getAttributes().get(WebSocketAttributeKeys.ROOM_ID);
        String uid = (String) session.getAttributes().get(WebSocketAttributeKeys.UID);
        String username = (String) session.getAttributes().get(WebSocketAttributeKeys.USERNAME);

        RoomChannelSessionManager.register(roomId, CHANNEL_TYPE, session);

        log.info("[message websocket] connection transport error: sessionId={}, roomId={}, uid={}, cause={}",
                session.getId(), roomId, uid, exception.getMessage());

        RoomChannelSessionManager.unregister(roomId, CHANNEL_TYPE, session);

        PoppingWebSocketMessage msg = new PoppingWebSocketMessage(
                WebSocketMessageOpEnum.NORMAL,
                username + " exit the room",
                PoppingWebSocketMessage.SYSTEM_SENDER,
                System.currentTimeMillis()
        );
        RoomChannelSessionManager.sendToAll(roomId, CHANNEL_TYPE, msg);
    }
}
