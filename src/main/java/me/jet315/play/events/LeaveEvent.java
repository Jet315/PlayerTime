package me.jet315.play.events;

import me.jet315.play.PlayerTimer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Jet on 25/04/2017.
 */
public class LeaveEvent implements Listener{

    @EventHandler
    public void qe(PlayerQuitEvent e){

        PlayerTimer.getInstance().getPM().savePlayer(e.getPlayer());
    }
}
