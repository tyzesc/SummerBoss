package tyze.bukkit.summerboss;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class BossDamagedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Integer damage;

    public BossDamagedEvent(Player player, Integer damage) {
        this.player = player;
        this.damage = damage;
    }

    public Player getPlayer() {
        return player;
    }

    public Integer getDamage() {
        return damage;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}