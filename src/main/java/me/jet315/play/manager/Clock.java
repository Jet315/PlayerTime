package me.jet315.play.manager;

import me.jet315.play.PlayerTimer;
import me.jet315.play.commands.CommandHandler;
import me.realized.tm.api.TMAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Random;

/**
 * Created by Jet on 25/04/2017.
 */
public class Clock {
    //Seconds timeout
    private int timeout = 60;
    //Minutes to how often the commands should run for a player
    private int runCommands = 10;
    private String defaultRank;
    private PlayerManager playerManager;

    //Booster information
    public int iterationsNeeded = 60;
    public boolean boosterActive = false;
    public String playerName;
    public int counter = 0;
    public int levelOfBooster = 0;
    public int tokensToGive = 0;
    public int tokensToGiveIfAFK = 0;


    /**
     * Constructor - Called when Class is initialised
     */
    public Clock(int timeout, int runCommands,String defaultRank,PlayerManager pm){
        this.timeout = timeout;
        this.runCommands = runCommands;
        this.defaultRank = defaultRank;
        this.playerManager =pm;
        startClock();
    }

    public  void startClock(){
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(PlayerTimer.getInstance(), new Runnable() {

            public void run() {
                if(boosterActive){

                    if(iterationsNeeded == counter){
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "broadcast &6" + levelOfBooster + "x Booster &chas worn off!");
                        boosterActive = false;
                        counter = 0;
                        levelOfBooster = 0;
                    }else{
                        counter++;
                    }
                }

                for(Player p : Bukkit.getOnlinePlayers()){
                    if(!playerManager.doesPlayerExist(p)) return;

                    playerManager.addPlayTime(p,timeout);

                    if(playerManager.getPlayTime(p)/60 % runCommands == 0) {
                        String[] time = CommandHandler.splitToComponentTimes(playerManager.getPlayTime(p));



                        //If no booster is active
                        if (levelOfBooster == 0) {
                            if (PlayerTimer.getInstance().afk.isAFK(p)) {
                                PlayerTimer.getInstance().afk.updateAFKPlayer(p);
                                TMAPI.addTokens(p, 5);
                                p.sendMessage(ChatColor.RED + "AFK - " + ChatColor.YELLOW + "+5 Tokens " + ChatColor.RED + " (÷2) " + ChatColor.GOLD + "Play Time: "+ChatColor.GREEN+ time[0] + " Days, " + time[1] +" Hours and " + time[2] + " Minutes");
                                continue;
                            }
                            PlayerTimer.getInstance().afk.updatePlayer(p);
                            TMAPI.addTokens(p, 10);
                            p.sendMessage(ChatColor.YELLOW + "+10 Tokens " + ChatColor.GOLD + "Play Time: " +ChatColor.GREEN+ time[0] + " Days, " + time[1] +" Hours and " + time[2] + " Minutes");
                            continue;
                        }
                        //If booster is active
                        //int tokensToGive = levelOfBooster * 10; (This is now done as the booster is activated, more efficient)
                        if (PlayerTimer.getInstance().afk.isAFK(p)) {
                            PlayerTimer.getInstance().afk.updateAFKPlayer(p);
                            TMAPI.addTokens(p, tokensToGiveIfAFK);
                            p.sendMessage(ChatColor.RED + "AFK - +" + ChatColor.YELLOW + tokensToGiveIfAFK + " Tokens" + ChatColor.RED + " (÷2) " + ChatColor.GREEN + "(" + levelOfBooster + "x Booster by " + ChatColor.GREEN + playerName + ")");
                            continue;
                        }
                        //Else not AFK and booster is active
                        PlayerTimer.getInstance().afk.updatePlayer(p);
                        TMAPI.addTokens(p, tokensToGive);
                        p.sendMessage(ChatColor.YELLOW + "+"+tokensToGive +" Tokens " +ChatColor.GREEN + "("+levelOfBooster+"x Booster by " + ChatColor.GREEN +playerName+") " +ChatColor.GOLD + "Play Time: " +ChatColor.GREEN+ time[0] + " Days, " + time[1] +" Hours and " + time[2] + " Minutes");
                        continue;

/*                        if(p.getPlayer().hasPermission("booster.5")){
                            if(PlayerTimer.getInstance().afk.isAFK(p)){
                                TMAPI.addTokens(p,25);
                                p.sendMessage(ChatColor.RED + "AFK - "+ ChatColor.YELLOW + "+25 Tokens " +ChatColor.RED +" (/2) " +ChatColor.GREEN + "(4x Booster by " + ChatColor.GREEN +playerName + ")");
                                continue;
                            }
                            TMAPI.addTokens(p,50);
                            p.sendMessage(ChatColor.YELLOW + "+50 Tokens " +ChatColor.GREEN + "(5x Booster by " + ChatColor.GREEN +playerName+") " +ChatColor.GOLD + "Play Time: " +ChatColor.GREEN+ time[0] + " Days, " + time[1] +" Hours and " + time[2] + " Minutes");
                            continue;
                        }
                        //Run commands
                        if(p.getPlayer().hasPermission("booster.4")){
                            if(PlayerTimer.getInstance().afk.isAFK(p)){
                                TMAPI.addTokens(p,20);
                                p.sendMessage(ChatColor.RED + "AFK - "+ ChatColor.YELLOW + "+20 Tokens " +ChatColor.RED +" (/2) " +ChatColor.GREEN + "(4x Booster by " + ChatColor.GREEN +playerName + ")");
                                continue;
                            }
                            TMAPI.addTokens(p,40);
                            p.sendMessage(ChatColor.YELLOW + "+40 Tokens " +ChatColor.GREEN + "(4x Booster by " + ChatColor.GREEN +playerName+") " +ChatColor.GOLD + "Play Time: " +ChatColor.GREEN+ time[0] + " Days, " + time[1] +" Hours and " + time[2] + " Minutes");
                            continue;
                        }
                        if(p.getPlayer().hasPermission("booster.3")){
                            if(PlayerTimer.getInstance().afk.isAFK(p)){
                                TMAPI.addTokens(p,15);
                                p.sendMessage(ChatColor.RED + "AFK - "+ ChatColor.YELLOW + "+15 Tokens " +ChatColor.RED +" (/2) " +ChatColor.GREEN + "(3x Booster by " + ChatColor.GREEN +playerName+ ")");
                                continue;
                            }
                            TMAPI.addTokens(p,30);
                            p.sendMessage(ChatColor.YELLOW + "+30 Tokens " +ChatColor.GREEN + "(3x Booster by " + ChatColor.GREEN +playerName+") " +ChatColor.GOLD + "Play Time: " +ChatColor.GREEN+ time[0] + " Days, " + time[1] +" Hours and " + time[2] + " Minutes");
                            continue;
                        }
                        if(p.hasPermission("booster.2")){
                            if(PlayerTimer.getInstance().afk.isAFK(p)){
                                TMAPI.addTokens(p,10);
                                p.sendMessage(ChatColor.RED + "AFK - "+ ChatColor.YELLOW + "+10 Tokens " +ChatColor.RED +" (/2) " +ChatColor.GREEN + "(2x Booster by " + ChatColor.GREEN +playerName+ ")");
                                continue;
                            }
                            TMAPI.addTokens(p,20);
                            p.sendMessage(ChatColor.YELLOW + "+20 Tokens " +ChatColor.GREEN + "(2x Booster) " +ChatColor.GOLD + "Play Time: " +ChatColor.GREEN+ time[0] + " Days, " + time[1] +" Hours and " + time[2] + " Minutes");
                            continue;
                        }
                        if(PlayerTimer.getInstance().afk.isAFK(p)){
                            TMAPI.addTokens(p,5);
                            p.sendMessage(ChatColor.RED + "AFK - "+ ChatColor.YELLOW + "+5 Tokens " +ChatColor.RED +" (/2) "+ ChatColor.GOLD + "Play Time: " +ChatColor.GREEN+ time[0] + " Days, " + time[1] +" Hours and " + time[2] + " Minutes");
                            continue;
                        }
                        TMAPI.addTokens(p,10);
                        p.sendMessage(ChatColor.YELLOW + "+10 Tokens "+ChatColor.GOLD + "Play Time: " + ChatColor.GREEN + "Days: " +ChatColor.GREEN+ time[0] + " Hours: " + time[1] + " Minutes: " + time[2]);
                        continue;

                        //Update on the 9th minute
                    }*/
                    }
                    //On the 9th minute check if they are AFK
/*                    if(playerManager.getPlayTime(p)/60 % runCommands == 9){
                        PlayerTimer.getInstance().afk.updatePlayer(p);
                    }*/
                }

            }
        }, 0L, 20L * timeout);

    }

}
