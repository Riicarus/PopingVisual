package io.github.riicarus.poppingvisual.service.impl;

import io.github.riicarus.poppingvisual.inner.room.RoomInfo;
import io.github.riicarus.poppingvisual.inner.room.RoomManager;
import io.github.riicarus.poppingvisual.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * room service impl
 *
 * @author Riicarus
 * @create 2024-8-3 17:41
 * @since 1.0.0
 */
@Service
@Slf4j
public class RoomServiceImpl implements RoomService {
    @Override
    public void createRoom(String roomId, String roomName) {
        RoomManager.add(new RoomInfo(roomId, roomName));
    }
}
