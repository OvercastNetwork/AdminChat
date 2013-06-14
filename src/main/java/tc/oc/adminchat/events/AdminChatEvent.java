package tc.oc.adminchat.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AdminChatEvent extends Event {
    private static HandlerList handlers = new HandlerList();

    private String player;
    private String message;
    private String prefix;

    public AdminChatEvent(String player, String message, String prefix) {
        super();
        this.player = player;
        this.message = message;
        this.prefix = prefix;
    }

    /** Gets the player that sent the message. */
    public String getPlayer() {
        return this.player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    /** Gets the message. */
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /** Gets the prefix. */
    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
