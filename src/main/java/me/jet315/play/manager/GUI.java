package me.jet315.play.manager;

import io.netty.util.concurrent.Future;
import me.jet315.play.PlayerTimer;
import me.jet315.play.commands.CommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.concurrent.FutureTask;

/**
 * Created by Jet on 25/04/2017.
 */
public class GUI {
    private Inventory inv,confirmInv2,confirmInv3,confirmInv4,confirmInv5,celebrateInv;
    private ItemStack booster2,booster3,booster4,booster5,close,confirm,decline,thank,noThank;
    public GUI(){
        //Inventory
        inv = Bukkit.createInventory(null,9, ChatColor.GREEN +""+ChatColor.BOLD+ "Boosters");

        //Close material
        close = new ItemStack(Material.BARRIER,1);

        //Second inventory, used for a "confirmation" to ensure the player wants to use his booster
        //Couldn't really think of a great way to create multiple similar inventories, will loop three times to create three inventories
            confirmInv2 = Bukkit.createInventory(null,9,ChatColor.RED +"Use 2x Booster?");
            confirmInv3 = Bukkit.createInventory(null,9,ChatColor.RED +"Use 3x Booster?");
            confirmInv4 = Bukkit.createInventory(null,9,ChatColor.RED +"Use 4x Booster?");
            confirmInv5 = Bukkit.createInventory(null,9,ChatColor.RED + "Use 5x Booster?");

            confirm = new ItemStack(Material.STAINED_GLASS_PANE,1,(short) 5);
            ItemMeta confirmMeta = confirm.getItemMeta();
            confirmMeta.setDisplayName(ChatColor.GREEN +""+ ChatColor.BOLD + "Confirm");
            confirm.setItemMeta(confirmMeta);

            decline = new ItemStack(Material.STAINED_GLASS_PANE,1,(short) 14);
            ItemMeta declineMeta = decline.getItemMeta();
            declineMeta.setDisplayName(ChatColor.RED +""+ChatColor.BOLD +"Decline");
            decline.setItemMeta(declineMeta);

            confirmInv2.setItem(1,confirm);
            confirmInv2.setItem(7,decline);

            confirmInv3.setItem(1,confirm);
            confirmInv3.setItem(7,decline);

            confirmInv4.setItem(1,confirm);
            confirmInv4.setItem(7,decline);

            confirmInv5.setItem(1,confirm);
            confirmInv5.setItem(7,decline);







    }

    public void openGUI(Player p){
        createGUI(p);
        p.openInventory(inv);
    }

    public void openConfirmationGUI(Player p,int confirmInv){
        if(confirmInv == 2){
            p.openInventory(confirmInv2);
        }
        if(confirmInv == 3){
            p.openInventory(confirmInv3);
        }
        if(confirmInv == 4){
            p.openInventory(confirmInv4);
        }
        if(confirmInv == 5){
            p.openInventory(confirmInv5);
        }
    }
    private void createGUI(Player p){

        int boosters[] = PlayerTimer.getInstance().getPM().getBoosters(p.getUniqueId());
        booster2 = new ItemStack(Material.STAINED_GLASS_PANE,1, (short) 1);
        booster3 = new ItemStack(Material.STAINED_GLASS_PANE,1, (short) 6);
        booster4 = new ItemStack(Material.STAINED_GLASS_PANE,1, (short) 14);
        booster5 = new ItemStack(Material.STAINED_GLASS_PANE,1, (short) 4);

        ArrayList<String> loreBoosters = new ArrayList<>();
        //Boosters lore (Text displayed)
        loreBoosters.add(ChatColor.GOLD + "You currently have");
        //2x Booster
        loreBoosters.add(""+ChatColor.GREEN + ChatColor.BOLD + boosters[0]+ (boosters[0] <= 0 ? "":"x"));
        loreBoosters.add(ChatColor.GOLD +"Boosters Available");
        ItemMeta metaBooster2 = booster2.getItemMeta();
        metaBooster2.setDisplayName(ChatColor.GOLD + ""+ChatColor.BOLD + "2x Boosters");
        metaBooster2.setLore(loreBoosters);
        booster2.setItemMeta(metaBooster2);
        inv.setItem(0,booster2);

        loreBoosters.clear();

        //3x Booster
        loreBoosters.add(ChatColor.LIGHT_PURPLE + "You currently have");
        loreBoosters.add(ChatColor.GREEN +""+ ChatColor.BOLD+ boosters[1] + (boosters[1] <= 0 ? "":"x"));
        loreBoosters.add(ChatColor.LIGHT_PURPLE +"Boosters Available");
        ItemMeta metaBooster3 = booster3.getItemMeta();
        metaBooster3.setDisplayName(ChatColor.LIGHT_PURPLE + ""+ChatColor.BOLD + "3x Boosters");
        metaBooster3.setLore(loreBoosters);
        booster3.setItemMeta(metaBooster3);
        inv.setItem(1,booster3);

        loreBoosters.clear();

        //4x Booster
        loreBoosters.add(ChatColor.RED + "You currently have");
        loreBoosters.add( ""+ChatColor.GREEN + ChatColor.BOLD+ boosters[2] + (boosters[2] <= 0 ? "":"x"));
        loreBoosters.add(ChatColor.RED +"Boosters Available");
        ItemMeta metaBooster4 = booster4.getItemMeta();
        metaBooster4.setDisplayName(ChatColor.RED + ""+ChatColor.BOLD + "4x Boosters");
        metaBooster4.setLore(loreBoosters);
        booster4.setItemMeta(metaBooster4);
        inv.setItem(2,booster4);

        loreBoosters.clear();

        //5x Booster
        loreBoosters.add(ChatColor.RED + "You currently have");
        loreBoosters.add( ""+ChatColor.GREEN + ChatColor.BOLD+ boosters[3] + (boosters[3] <= 0 ? "":"x"));
        loreBoosters.add(ChatColor.RED +"Boosters Available");
        ItemMeta metaBooster5 = booster5.getItemMeta();
        metaBooster5.setDisplayName(ChatColor.RED + ""+ChatColor.BOLD + "5x Boosters");
        metaBooster5.setLore(loreBoosters);
        booster5.setItemMeta(metaBooster5);
        inv.setItem(3,booster5);

        loreBoosters.clear();
        //Close option (Added lore)

        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(ChatColor.RED + "Close");
        loreBoosters.add(ChatColor.GOLD +"Play Time:");
        String[] timePlayed =  CommandHandler.splitToComponentTimes(PlayerTimer.getInstance().getPM().getPlayTime(p));
        loreBoosters.add(ChatColor.translateAlternateColorCodes('&', "&l&aD: " + timePlayed[0] + " &l&aH: " + timePlayed[1] + " &l&aM: " + timePlayed[2]));
        closeMeta.setLore(loreBoosters);
        close.setItemMeta(closeMeta);
        inv.setItem(8,close);
    }

    public void celebrateGUI(Player p){
        //Create GUI

        //Lore required
        ArrayList<String> thankLore = new ArrayList<>();
        thankLore.add(ChatColor.LIGHT_PURPLE + "You and " + p.getDisplayName());
        thankLore.add(ChatColor.LIGHT_PURPLE +"will receive " + ChatColor.GOLD + ChatColor.BOLD + "10 Tokens");

        ArrayList<String> dislikeLore = new ArrayList<>();
        dislikeLore.add(ChatColor.RED + "Don't thank " + p.getName());
        dislikeLore.add(ChatColor.RED +"will receive");
        dislikeLore.add(ChatColor.RED + "NOTHING");

        //Create inventory
        celebrateInv = Bukkit.createInventory(null,9,ChatColor.GREEN +"Thank "+ChatColor.GOLD+ p.getDisplayName() +"?");

        //Thank button
        thank = new ItemStack(Material.STAINED_GLASS_PANE,1,(short) 5);
        ItemMeta thankMeta = thank.getItemMeta();
        thankMeta.setDisplayName(ChatColor.GREEN +"Thank " + p.getDisplayName() + "?");
        thankMeta.setLore(thankLore);
        thank.setItemMeta(thankMeta);
        //Dislike button
        noThank = new ItemStack(Material.STAINED_GLASS_PANE,1,(short) 14);
        ItemMeta noThankMeta = noThank.getItemMeta();
        noThankMeta.setDisplayName(ChatColor.RED + "Dislike " + p.getDisplayName() +"?");
        noThankMeta.setLore(dislikeLore);
        noThank.setItemMeta(noThankMeta);

        celebrateInv.setItem(1,thank);
        celebrateInv.setItem(7,noThank);

        for(Player player : Bukkit.getOnlinePlayers()){
            if(player == p) continue;
            //If they are in one
            player.closeInventory();
            player.openInventory(celebrateInv);
        }
    }



}
