package io.github.riicarus.poppingvisual.ws.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Websocket message
 *
 * @author Riicarus
 * @create 2024-8-3 15:22
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public final class PoppingWebSocketMessage {

    public static final String SYSTEM_SENDER = "SYSTEM";

    /**
     * message operation
     */
    private WebSocketMessageOpEnum op;
    /**
     * message payload
     */
    private String content;
    /**
     * message sender's uid
     */
    private String senderId;
    /**
     * message send time
     */
    private long timestamp;

}
