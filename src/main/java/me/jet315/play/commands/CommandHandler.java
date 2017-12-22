package me.jet315.play.commands;

import me.jet315.play.PlayerTimer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Jet on 25/04/2017.
 */
public class CommandHandler implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        /**
         * Class is based around the commands that I need, will change if I upload to Spigot
         */
        /**
         * USE Tokensmanager API and delete tokenmanager commands from plugin.yml
         */

        if (cmd.getName().equalsIgnoreCase("timeplayed")) {
            if (args.length == 0) {
                if (sender instanceof Player) {
                    String[] time = splitToComponentTimes(PlayerTimer.getInstance().getPM().getPlayTime((Player) sender));
                    sender.sendMessage(ChatColor.GOLD + "You have played for: " + ChatColor.GREEN + time[0] + " Days, " + time[1] + " Hours and " + time[2] + " Minutes");
                }

            }else if(args.length == 1){
                if(sender.hasPermission("timeplayed.other")){
                    String playerAsString = args[0];
                    OfflinePlayer player = Bukkit.getOfflinePlayer(playerAsString);
                    if(player.hasPlayedBefore()){
                        String[] time = splitToComponentTimes(PlayerTimer.getInstance().getPM().getTimeOfPlayer(player.getUniqueId()));
                        sender.sendMessage(ChatColor.GREEN + "" + playerAsString + ChatColor.GOLD + " has played for: " + ChatColor.GREEN + time[0] + " Days, " + time[1] + " Hours and " + time[2] + " Minutes");
                    }else{
                        sender.sendMessage(ChatColor.RED + playerAsString + " is either an incorrect entry, or has not played the server for the last few weeks");
                    }
                }else{
                    sender.sendMessage(ChatColor.RED + "You do not have permission to check other players time");
                }

            }else if(args.length == 2){
                if(args[0].equalsIgnoreCase("purge")){
                    if(sender.hasPermission("timeplayed.purgestats")){
                        int deleteTime = Integer.parseInt(args[1]);
                        PlayerTimer.getInstance().getPM().purgeData(deleteTime);
                        sender.sendMessage(ChatColor.RED + "Data successfully purged from " + deleteTime + " Seconds ago");
                    }
                }
            }
            return false;

            //This bit not very clean, needs reworking
        } else if (cmd.getName().equalsIgnoreCase("boosters")) {
            if (args.length == 0) {
                if (sender instanceof Player) {
                    PlayerTimer.getInstance().getG().openGUI(((Player) sender).getPlayer());
                } else {
                    sender.sendMessage(ChatColor.GOLD + "Command: /booster <player> <number (number of boosters)> <number (Booster multiplier)> ");

                    return true;
                }

            }else if (args.length == 2){
                if(sender.hasPermission("boosters.give.any")){
                    final int booster = Integer.parseInt(args[0]);
                    final int timeInMinutes = Integer.parseInt(args[1]);

                    PlayerTimer.getInstance().clock.levelOfBooster = booster;
                    PlayerTimer.getInstance().clock.iterationsNeeded = timeInMinutes;
                    PlayerTimer.getInstance().clock.boosterActive = true;

                    //The number of tokens to give per 10 minutes
                    PlayerTimer.getInstance().clock.tokensToGive = 10*booster;
                    PlayerTimer.getInstance().clock.tokensToGiveIfAFK = 5*booster;

                    PlayerTimer.getInstance().clock.playerName = sender.getName();
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "bdispatch bungee alert &6" + sender.getName() + " &ahas just activated a &d"+booster+"x Token Booster &6on &c" + PlayerTimer.getInstance().getConfig().getString("serverName"));
                    if(sender instanceof Player){
                        PlayerTimer.getInstance().getG().celebrateGUI((Player) sender);
                    }
                    return true;
                }
            } else if (args.length == 3) {

                if (sender.hasPermission("boosters.give.any")) {

                    final String target = args[0];
                    final int amount = Integer.parseInt(args[1]);
                    final int boosterToEdit = Integer.parseInt(args[2]);
                    OfflinePlayer p = Bukkit.getOfflinePlayer(target);
                    PlayerTimer.getInstance().getPM().boosterEdit(p.getUniqueId(), amount,boosterToEdit);
                    sender.sendMessage(ChatColor.GREEN + "Sent booster successfully");
                }

            }else{
                if (sender.hasPermission("boosters.give.any")) {
                    sender.sendMessage(ChatColor.GOLD + "Command: /booster <player> <number (number of boosters)> <number (Booster multiplier)> ");
                }
            }

                }
                return false;
            }


    public static String[] splitToComponentTimes(int seconds) {
        String[] timePlayed = new String[3];

        int daysUnFormatted = seconds/86400;

        int hoursUnFormatted = seconds/3600 - ( daysUnFormatted *24);

        int minsUnFormatted = seconds/60 - (( hoursUnFormatted * 60) + (daysUnFormatted*1440));


        if(daysUnFormatted <= 9){
            String days = "0" + daysUnFormatted;
            timePlayed[0] = days;
        }else{
            String days = Integer.toString(daysUnFormatted);
            timePlayed[0] = days;

        }
        if(hoursUnFormatted <= 9){
            String hours = "0" + hoursUnFormatted;
            timePlayed[1] = hours;
        }else{
            String hours = Integer.toString(hoursUnFormatted);
            timePlayed[1] = hours;
        }

        if(minsUnFormatted <= 9){
            String mins = "0" + minsUnFormatted;
            timePlayed[2] = mins;
        }else{
            String mins = Integer.toString(minsUnFormatted);
            timePlayed[2] = mins;
        }

        return timePlayed;
    }
}

