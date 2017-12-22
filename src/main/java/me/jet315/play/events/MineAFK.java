package me.jet315.play.events;

import me.jet315.play.PlayerTimer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

/**
 * Created by Jet on 28/05/2017.
 */
public class MineAFK implements Listener {

/*    @EventHandler
    public void onMineEvent(BlockBreakEvent e){

        //This event is only used on SkyBlock to fix a few issues with abusing

        if(PlayerTimer.getInstance().afk.isAFK(e.getPlayer())){
            e.getPlayer().sendMessage(ChatColor.RED + "AFK Mining is not allowed");
            e.setCancelled(true);
        }
        if(e.getBlock().getType() == Material.MOB_SPAWNER && e.getPlayer().getWorld().getName().equalsIgnoreCase("world_nether")){
            e.getPlayer().sendMessage(ChatColor.RED +"You are unable to mine" + ChatColor.GOLD + " Spawners " + ChatColor.RED + "in the Nether");
            e.setCancelled(true);
        }
    }*/


    //Survival Server Only
    @EventHandler
    public void onEntityTP(EntityTeleportEvent e){
        if(e.getEntity() instanceof Wolf){
            if(e.getTo().distance(new Location(Bukkit.getWorld("world"),0.5,76,0.5)) < 20){
                e.setCancelled(true);
                return;
            }
            if(e.getTo().distance(new Location(Bukkit.getWorld("world"),9799,66,9704)) < 40){

                e.setCancelled(true);
                return;
            }
        }
    }

}
