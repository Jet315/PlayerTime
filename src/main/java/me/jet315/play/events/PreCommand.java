package me.jet315.play.events;

import me.jet315.play.PlayerTimer;
import me.jet315.play.commands.CommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Created by Jet on 28/04/2017.
 */
public class PreCommand implements Listener {

    /**
     * Not suppose to use this command for logic, however a plugin is already using the /tokens
     * command and it looks like I cannot have two separate plugins using the same commands.
     * Using Asynchronously method as it needs a small delay before sending the token amount
     */
    @EventHandler
    public void pce(PlayerCommandPreprocessEvent e ){
        final Player p = e.getPlayer();
        if(e.getMessage().equalsIgnoreCase("/token") || e.getMessage().equalsIgnoreCase("/tokens")){
            Bukkit.getScheduler().runTaskAsynchronously(PlayerTimer.getInstance(), new Runnable() {
                @Override
                public void run() {
                    String[] time = CommandHandler.splitToComponentTimes(PlayerTimer.getInstance().getPM().getPlayTime(p));
                    p.sendMessage(ChatColor.GOLD + "You have played for: " +ChatColor.GREEN+ time[0] + " Days, " + time[1] +" Hours and " + time[2] + " Minutes");
                }
            });
        }
/*        if(e.getMessage().contains("warp")){
            if(e.getMessage().equalsIgnoreCase("/warp dungeon")){
                e.setCancelled(true);
                e.getPlayer().teleport(new Location(Bukkit.getWorld("spawnworld"),18.5,49,-422.5,-0,-3));
                return;
            }

            if(e.getMessage().equalsIgnoreCase("/warp spawn")){
                e.setCancelled(true);
                e.getPlayer().teleport(new Location(Bukkit.getWorld("spawnworld"),0,51.5,10.5,180,-1));
                return;
            }

            if(e.getMessage().equalsIgnoreCase("/warp shop") || e.getMessage().equalsIgnoreCase("/warp market")){
                e.setCancelled(true);
                e.getPlayer().teleport(new Location(Bukkit.getWorld("spawnworld"),-5,49.5,-4,142,-7));
                return;

            }

            if(e.getMessage().equalsIgnoreCase("/warp pvp")){
                e.setCancelled(true);
                e.getPlayer().teleport(new Location(Bukkit.getWorld("spawnworld"),-2,49.5,-28,-134,-10));
                return;

            }

            if(e.getMessage().equalsIgnoreCase("/warp end")){
                e.setCancelled(true);
                if(p.hasPermission("hyperdrive.serverwarp.End")){
                    p.teleport(new Location(Bukkit.getWorld("world_the_end"),0.5,63,0.5,180,30));
                }else{
                    e.getPlayer().sendMessage(ChatColor.RED + "No Permission - " + ChatColor.GREEN + "Purchase an ingame rank at /spawn to gain access to the end!");
                }
            }
        }*/



    }
}
