package io.github.riicarus.poppingvisual.inner.room;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * room manager
 *
 * @author Riicarus
 * @create 2024-8-3 17:54
 * @since 1.0.0
 */
@Slf4j
public final class RoomManager {

    private static final Map<String, RoomInfo> ROOM_MAP = new ConcurrentHashMap<>();
    private static final AtomicInteger ROOM_COUNT = new AtomicInteger(0);

    private RoomManager() {
    }

    public static void add(RoomInfo room) {
        ROOM_MAP.put(room.getId(), room);
        ROOM_COUNT.getAndIncrement();
    }

    public static void remove(String roomId) {
        if (ROOM_MAP.remove(roomId) != null) {
            ROOM_COUNT.getAndDecrement();
        }
    }

    public static RoomInfo get(String roomId) {
        return ROOM_MAP.get(roomId);
    }

    public static boolean exist(String roomId) {
        return ROOM_MAP.containsKey(roomId);
    }

    public static int getRoomCount() {
        return ROOM_COUNT.get();
    }

}
