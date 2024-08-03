package io.github.riicarus.poppingvisual.controller;

import io.github.riicarus.poppingvisual.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * room controller
 *
 * @author Riicarus
 * @create 2024-8-3 17:44
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/room")
@Slf4j
public class RoomController {

    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/{roomId}/{roomName}")
    public String createRoom(@PathVariable String roomId, @PathVariable String roomName) {
        roomService.createRoom(roomId, roomName);
        return "success";
    }

}
