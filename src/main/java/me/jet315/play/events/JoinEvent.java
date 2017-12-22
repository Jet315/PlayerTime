package me.jet315.play.events;

import me.jet315.play.PlayerTimer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

/**
 * Created by Jet on 25/04/2017.
 */
public class JoinEvent implements Listener{

    @EventHandler
    public void pje(PlayerJoinEvent e){
        PlayerTimer.getInstance().getPM().loadPlayer(e.getPlayer());
    }
}
