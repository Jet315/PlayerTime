package me.jet315.play.antiafk;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jet on 02/05/2017.
 */
public class AntiAFK {

    private HashMap<Player,Integer> afkList = new HashMap<>();
    private HashMap<Player,Integer> afkAmount = new HashMap<>();

    public boolean isAFK(Player p){

        if(afkList.containsKey(p)){
            if((int) p.getLocation().getYaw() == afkList.get(p)){
                return true;
            }

        }

        return false;
    }

    public void updatePlayer(Player p){
        if(afkAmount.containsKey(p)){
            afkAmount.remove(p);
        }
        afkList.put(p,(int) p.getLocation().getYaw());

    }

    public void updateAFKPlayer(Player p){
        if(afkAmount.containsKey(p)){
            afkAmount.put(p,afkAmount.get(p)+1);
            //AFK for 30 minutes
            if(afkAmount.get(p) == 3){
                p.performCommand("spawn");
                p.sendMessage(ChatColor.RED + "AFK - Teleported to Spawn");

                //Update their camera position as it would have now changed, otherwise next round it will not think they are AFK
                updatePlayer(p);
            }
            return;
        }
        afkAmount.put(p,1);

    }
}
