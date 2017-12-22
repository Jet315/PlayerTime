package me.jet315.play.events;

import me.jet315.play.PlayerTimer;
import me.realized.tm.api.TMAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;


/**
 * Created by Jet on 26/04/2017.
 */
public class InvClick implements Listener{

    private String server;
    private String defaultRank;
    public InvClick(String server,String defaultRank) {
        this.server = server;
        this.defaultRank = defaultRank;
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        //Check they are in inventory
        if (!e.getInventory().getName().contains("Booster") && (!e.getInventory().getName().contains("Thank")))
            return;

        //First null check to see if item exists
        if(e.getCurrentItem() == null) return;

           //Second (May click like air block this prevents that)
        if (e.getCurrentItem().getItemMeta() == null) return;

        //Third (May click a block not on the GUI so have to set canceled, then return
        if(e.getCurrentItem().getItemMeta().getDisplayName() == null){
            e.setCancelled(true);
            return;
        }

        //Will always cancel the event, as I want a custom thing to happen
        e.setCancelled(true);
        //Clicked on booster (I go through booster types)


        if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Boosters")) {
            int boosterMenu = 2;
            if(e.getCurrentItem().getItemMeta().getDisplayName().contains("2x")) boosterMenu = 2;
            if(e.getCurrentItem().getItemMeta().getDisplayName().contains("3x")) boosterMenu = 3;
            if(e.getCurrentItem().getItemMeta().getDisplayName().contains("4x")) boosterMenu = 4;
            if(e.getCurrentItem().getItemMeta().getDisplayName().contains("5x")) boosterMenu = 5;

            //I have just queried the database, so I don't think there is any need to re query it (When creating the GUI)
            if (PlayerTimer.getInstance().getPM().cashedBoosterValue((Player) e.getWhoClicked())[boosterMenu -2] > 0) {
                e.getWhoClicked().closeInventory();
                PlayerTimer.getInstance().getG().openConfirmationGUI((Player) e.getWhoClicked(),boosterMenu);
                return;
            } else {
                e.getWhoClicked().closeInventory();
                ((Player) e.getWhoClicked()).updateInventory();
                e.getWhoClicked().sendMessage(ChatColor.RED + "It appears you do not have any boosters available!");
                e.getWhoClicked().sendMessage(ChatColor.GOLD + "Purchase one at: shop.PlanetMine.org");
                return;
            }

        }
        //clicked on close during main interface
        if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Close")) {
            e.getWhoClicked().closeInventory();
            ((Player) e.getWhoClicked()).updateInventory();
            return;
        }


        //Confirm inventory
        if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Confirm")) {
            //Get the booster menu

            int boosterMenu = 2;

            if (PlayerTimer.getInstance().clock.boosterActive && PlayerTimer.getInstance().clock.levelOfBooster > boosterMenu) {
                e.getWhoClicked().sendMessage(ChatColor.GREEN + "Booster already active - Please wait until it has finished!");
                e.getWhoClicked().closeInventory();
                ((Player) e.getWhoClicked()).updateInventory();
                return;
            } else {
                if(PlayerTimer.getInstance().clock.boosterActive){
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "permissions group "+ defaultRank+ " unset booster." + PlayerTimer.getInstance().clock.levelOfBooster);
                }
                e.getWhoClicked().closeInventory();
                ((Player) e.getWhoClicked()).updateInventory();
                PlayerTimer.getInstance().getPM().boosterEdit(e.getWhoClicked().getUniqueId(), -1,boosterMenu);
                PlayerTimer.getInstance().clock.levelOfBooster = boosterMenu;
                PlayerTimer.getInstance().clock.iterationsNeeded = 60;
                PlayerTimer.getInstance().clock.boosterActive = true;

                //The number of tokens to give per 10 minutes
                PlayerTimer.getInstance().clock.tokensToGive = 10*boosterMenu;
                PlayerTimer.getInstance().clock.tokensToGiveIfAFK = 5*boosterMenu;

                PlayerTimer.getInstance().clock.playerName = ((Player) e.getWhoClicked()).getPlayer().getName();
                if(boosterMenu >2){
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "bdispatch bungee alert &6" + ((Player) e.getWhoClicked()).getDisplayName() + " &ahas just activated a &d"+boosterMenu+"x Token Booster &6on &c" + server);
                }
                PlayerTimer.getInstance().getG().celebrateGUI(((Player) e.getWhoClicked()).getPlayer());
                return;
            }
        }
        if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Decline")) {
            e.getWhoClicked().closeInventory();
            ((Player) e.getWhoClicked()).updateInventory();
            return;
        }


        //Celebrate buttons
        if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Thank")) {
            e.getWhoClicked().closeInventory();
            ((Player) e.getWhoClicked()).updateInventory();

            TMAPI.addTokens((Player) e.getWhoClicked(),10);
            e.getWhoClicked().sendMessage(ChatColor.GOLD + "+10 tokens " + ChatColor.GREEN + "Thanked " + PlayerTimer.getInstance().clock.playerName);
            Player p = Bukkit.getPlayer(PlayerTimer.getInstance().clock.playerName);

            if (p.isOnline()) {
                p.sendMessage(ChatColor.GOLD + "+10 tokens " + ChatColor.GREEN + e.getWhoClicked().getName() + " Thanked you");
                TMAPI.addTokens(p,10);
            }
            return;


        }
        if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Dislike")) {
            e.getWhoClicked().closeInventory();
            ((Player) e.getWhoClicked()).updateInventory();
            return;
        }


    }
}
