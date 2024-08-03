package io.github.riicarus.poppingvisual.inner.room;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * room info
 *
 * @author Riicarus
 * @create 2024-8-3 17:55
 * @since 1.0.0
 */
@AllArgsConstructor
@Getter
public class RoomInfo {

    private String id;
    private String name;
    private final List<String> userList = new CopyOnWriteArrayList<>();

}
