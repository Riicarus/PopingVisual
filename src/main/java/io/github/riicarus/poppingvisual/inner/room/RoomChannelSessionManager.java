package io.github.riicarus.poppingvisual.inner.room;

import com.alibaba.fastjson.JSON;
import io.github.riicarus.poppingvisual.ws.message.PoppingWebSocketMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * manager room channel's user session
 *
 * @author Riicarus
 * @create 2024-8-3 15:41
 * @since 1.0.0
 */
@Slf4j
public class RoomChannelSessionManager {

    /**
     * Map&lt;RoomId, EnumMap&lt;RoomChannelTypeEnum, ConcurrentHashmap&lt;Uid, WebSocketSession&gt;&gt;&gt;
     */
    private static final Map<String, EnumMap<RoomChannelTypeEnum, ConcurrentHashMap<String, WebSocketSession>>>
            ROOM_CHANNEL_SESSION_MAP = new ConcurrentHashMap<>();
    private static final Map<String, EnumMap<RoomChannelTypeEnum, AtomicInteger>>
            ROOM_CHANNEL_USER_COUNT_MAP = new ConcurrentHashMap<>();

    private RoomChannelSessionManager() {
    }

    /**
     * register session to the given room's channel, it's key is uid
     *
     * @param roomId room id
     * @param channelType room channel type
     * @param session user websocket session
     */
    public static void register(String roomId, RoomChannelTypeEnum channelType, WebSocketSession session) {
        checkRoom(roomId);
        String uid = (String) session.getAttributes().get(WebSocketAttributeKeys.UID);
        ROOM_CHANNEL_SESSION_MAP
                .computeIfAbsent(roomId, k -> new EnumMap<>(RoomChannelTypeEnum.class))
                .computeIfAbsent(channelType, k -> new ConcurrentHashMap<>())
                .put(uid, session);
        ROOM_CHANNEL_USER_COUNT_MAP
                .computeIfAbsent(roomId, k -> new EnumMap<>(RoomChannelTypeEnum.class))
                .computeIfAbsent(channelType, k -> new AtomicInteger(0))
                .getAndIncrement();
    }

    /**
     * unregister session from the given room's channel, it's key is uid
     *
     * @param roomId room id
     * @param channelType room channel type
     * @param session user websocket session
     */
    public static void unregister(String roomId, RoomChannelTypeEnum channelType, WebSocketSession session) {
        checkRoom(roomId);
        String uid = (String) session.getAttributes().get(WebSocketAttributeKeys.UID);
        ROOM_CHANNEL_SESSION_MAP.get(roomId).get(channelType).remove(uid);
        ROOM_CHANNEL_USER_COUNT_MAP.get(roomId).get(channelType).getAndDecrement();
    }

    /**
     * get session from the given room's channel by uid
     *
     * @param roomId room id
     * @param channelType room channel type
     * @param uid user id
     * @return user's websocket session
     */
    public static WebSocketSession getSession(String roomId, RoomChannelTypeEnum channelType, String uid) {
        checkRoom(roomId);
        return ROOM_CHANNEL_SESSION_MAP.get(roomId).get(channelType).get(uid);
    }

    /**
     * send websocket message to the given user in the given room's channel
     *
     * @param roomId room id
     * @param channelType room channel type
     * @param uid user id
     * @param message websocket message
     */
    public static void sendTo(String roomId, RoomChannelTypeEnum channelType, String uid, PoppingWebSocketMessage message) {
        checkRoom(roomId);
        String msg = JSON.toJSONString(message);
        try {
            getSession(roomId, channelType, uid).sendMessage(new TextMessage(msg));
        } catch (IOException e) {
            logMsgSendError(msg, e);
        }
    }
    /**
     * send websocket message to all the given users in the given room's channel
     *
     * @param roomId room id
     * @param channelType room channel type
     * @param uidList user id list
     * @param message websocket message
     */
    public static void sendTo(String roomId, RoomChannelTypeEnum channelType, List<String> uidList, PoppingWebSocketMessage message) {
        checkRoom(roomId);
        String msg = JSON.toJSONString(message);
        for (String uid : uidList) {
            try {
                getSession(roomId, channelType, uid).sendMessage(new TextMessage(msg));
            } catch (IOException e) {
                logMsgSendError(msg, e);
            }
        }
    }

    /**
     * send websocket message to all except the given users in the given room's channel
     *
     * @param roomId room id
     * @param channelType room channel type
     * @param uidList user id list who would not receive the message
     * @param message websocket message
     */
    public static void sendToExclude(String roomId, RoomChannelTypeEnum channelType, List<String> uidList, PoppingWebSocketMessage message) {
        String msg = JSON.toJSONString(message);
        for (Map.Entry<String, WebSocketSession> sessionEntry : ROOM_CHANNEL_SESSION_MAP.get(roomId).get(channelType).entrySet()) {
            if (!uidList.contains(sessionEntry.getKey())) {
                try {
                    sessionEntry.getValue().sendMessage(new TextMessage(msg));
                } catch (IOException e) {
                    logMsgSendError(msg, e);
                }
            }
        }
    }

    /**
     * send websocket message to all users in the given room's channel
     *
     * @param roomId room id
     * @param channelType room channel type
     * @param message websocket message
     */
    public static void sendToAll(String roomId, RoomChannelTypeEnum channelType, PoppingWebSocketMessage message) {
        String msg = JSON.toJSONString(message);
        for (WebSocketSession s : ROOM_CHANNEL_SESSION_MAP.get(roomId).get(channelType).values()) {
            try {
                s.sendMessage(new TextMessage(msg));
            } catch (IOException e) {
                logMsgSendError(msg, e);
            }
        }
    }

    /**
     * get user count in the given room's channel
     *
     * @param roomId room id
     * @param channelType room channel type
     * @return user count in the given room's channel
     */
    public static int getRoomChannelUserCount(String roomId, RoomChannelTypeEnum channelType) {
        return ROOM_CHANNEL_USER_COUNT_MAP.get(roomId).get(channelType).get();
    }

    private static void logMsgSendError(String message, Throwable t) {
        log.error("can not send msg: cause={}, msg={}", message, t.getMessage());
    }

    private static void checkRoom(String roomId) {
        if (!RoomManager.exist(roomId)) {
            throw new IllegalArgumentException("no such room: roomId=" + roomId);
        }
    }
}
