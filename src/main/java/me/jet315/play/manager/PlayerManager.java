package me.jet315.play.manager;

import me.jet315.play.PlayerTimer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Created by Jet on 25/04/2017.
 */
public class PlayerManager {
    private static final String INSERT = "INSERT INTO PlayerTimerAndBoosters VALUES(?,?,?,?,?,?) ON DUPLICATE KEY UPDATE unique_id=?";
    private static final String SELECTTIME = "SELECT time FROM PlayerTimerAndBoosters WHERE unique_id=?";
    private static final String SELECTBOOSTER = "SELECT boosters2,boosters3,boosters4,boosters5 FROM PlayerTimerAndBoosters WHERE unique_id=?";

    private static final String SAVETIME = "UPDATE PlayerTimerAndBoosters SET time=? WHERE unique_id=?";
    private static final String SAVEBOOSTER2 = "UPDATE PlayerTimerAndBoosters SET boosters2=? WHERE unique_id=?";
    private static final String SAVEBOOSTER3 = "UPDATE PlayerTimerAndBoosters SET boosters3=? WHERE unique_id=?";
    private static final String SAVEBOOSTER4 = "UPDATE PlayerTimerAndBoosters SET boosters4=? WHERE unique_id=?";
    private static final String SAVEBOOSTER5 = "UPDATE PlayerTimerAndBoosters SET boosters5=? WHERE unique_id=?";

    private HashMap<UUID, Integer> playTime = new HashMap<>();
    private HashMap<UUID, int[]> boosters = new HashMap<>();

    /**
     * Executed methods when a player joins/leaves server
     */
    public void addPlayerTime(Player p, int amount) {

        playTime.put(p.getUniqueId(), amount);
    }

    public void removePlayerTime(Player p) {
        playTime.remove(p.getUniqueId());
    }

    public int getPlayTime(Player p) {
            return (playTime.get(p.getUniqueId()));

    }


    public void addPlayTime(Player p, int time) {
        playTime.put(p.getUniqueId(), playTime.get(p.getUniqueId()) + time);
    }

    //Need to be careful when using this, as this will only be valid straight after it has queried the database (Otherwise it might of changed)
    //In some instances however, I do get the value of the booster in different methods straight after each other - so it will be correct and save a uneeded query
    public int[] cashedBoosterValue(Player p) {
        return boosters.get(p.getUniqueId());
    }

    /**
     * Sometimes when getting or setting playertime a NPE occurs as there is a very
     * slight chance of the player not being in the list, this will fix the problem
     */

    public boolean doesPlayerExist(Player p){
        if(playTime.containsKey(p.getUniqueId())){
            return true;
        }
        return false;
    }
    public void loadPlayer(final Player p) {
        Bukkit.getScheduler().runTaskAsynchronously(PlayerTimer.getInstance(), new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        //Needed as if they join from one server straight to another, the other server may have not saved the data to the MySQL database
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Connection connection = PlayerTimer.getInstance().getHikari().getConnection();
                    PreparedStatement selectTime = connection.prepareStatement(SELECTTIME);

                    PreparedStatement insert = connection.prepareStatement(INSERT);


                    insert.setString(1, p.getUniqueId().toString());
                    insert.setInt(2, 0);
                    insert.setInt(3, 0);
                    insert.setInt(4, 0);
                    insert.setInt(5, 0);
                    insert.setInt(6, 0);
                    insert.setString(7, p.getUniqueId().toString());
                    insert.execute();

                    selectTime.setString(1, p.getUniqueId().toString());
                    ResultSet result = selectTime.executeQuery();
                    if (result.next()) addPlayerTime(p, result.getInt("time"));
                    result.close();

                    connection.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

            }
        });
    }

    public void savePlayer(final Player p) {
        Bukkit.getScheduler().runTaskAsynchronously(PlayerTimer.getInstance(), new Runnable() {
            @Override
            public void run() {
                try {
                    Connection connection = PlayerTimer.getInstance().getHikari().getConnection();
                    PreparedStatement statement = connection.prepareStatement(SAVETIME);

                    statement.setInt(1, getPlayTime(p));
                    statement.setString(2, p.getUniqueId().toString());
                    statement.execute();
                    connection.close();

                    removePlayerTime(p);
                    boosters.remove(p);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public int[] getBoosters(final UUID uuid) {

        ExecutorService executor = Executors.newCachedThreadPool();
        Future<int[]> future = executor.submit(new Callable<int[]>() {
            @Override
            public int[] call() throws Exception {
                try {
                    int[] numOfBoosters = new int[4];

                    Connection connection = PlayerTimer.getInstance().getHikari().getConnection();
                    PreparedStatement selectBooster = connection.prepareStatement(SELECTBOOSTER);
                    selectBooster.setString(1, uuid.toString());
                    ResultSet result = selectBooster.executeQuery();
                    if (result.next()) {
                        numOfBoosters[0] = result.getInt("boosters2");
                        numOfBoosters[1] = result.getInt("boosters3");
                        numOfBoosters[2] = result.getInt("boosters4");
                        numOfBoosters[3] = result.getInt("boosters5");
                        boosters.put(uuid, numOfBoosters);
                        result.close();
                        connection.close();
                        return numOfBoosters;
                    } else {
                        numOfBoosters[0] = -1;
                        numOfBoosters[1] = -1;
                        numOfBoosters[2] = -1;
                        numOfBoosters[3] = -1;
                        boosters.put(uuid, numOfBoosters);
                        result.close();
                        connection.close();
                        return numOfBoosters;
                    }

                } catch (SQLException e) {
                    e.printStackTrace();

                }

                return new int[0];
            }
        });

        try {
            return future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return new int[0];
    }


/*            @Override
            public void run() {
                try {
                    Connection connection = PlayerTimer.getInstance().getHikari().getConnection();
                    PreparedStatement selectBooster = connection.prepareStatement(SELECTBOOSTER);
                    selectBooster.setString(1, uuid.toString());
                    ResultSet result = selectBooster.executeQuery();
                    if (result.next()) {
                        boosters.put(uuid, result.getInt("boosters"));
                    } else {
                        boosters.put(uuid, -1);
                    }
                    result.close();
                    connection.close();
                } catch (SQLException e) {
                    boosters.put(uuid, -1);
                    e.printStackTrace();
                }


            }*/


    public void boosterEdit(final UUID uuid, final int amount, final int boosterToEdit) {

        Bukkit.getScheduler().runTaskAsynchronously(PlayerTimer.getInstance(), new Runnable() {
            @Override
            public void run() {
                try {
                    Connection connection = PlayerTimer.getInstance().getHikari().getConnection();
                    int result;
                    PreparedStatement statement;

                    if (boosterToEdit == 2) {
                        statement = connection.prepareStatement(SAVEBOOSTER2);
                        result = getBoosters(uuid)[0] + amount;
                    } else if (boosterToEdit == 3) {
                        statement = connection.prepareStatement(SAVEBOOSTER3);
                        result = getBoosters(uuid)[1] + amount;

                    } else if (boosterToEdit == 4) {
                        statement = connection.prepareStatement(SAVEBOOSTER4);
                        result = getBoosters(uuid)[2] + amount;
                    }else if(boosterToEdit == 5){
                        statement = connection.prepareStatement(SAVEBOOSTER5);
                        result = getBoosters(uuid)[3] + amount;
                    } else {
                        System.out.println("Error occured while editing booster (Wrong number on boosterToEdit)");
                        connection.close();
                        return;
                    }

                    statement.setInt(1, result);
                    statement.setString(2, uuid.toString());
                    statement.execute();

                    connection.close();
                } catch (SQLException e) {
                    Bukkit.getPlayer(uuid).sendMessage("An error has occurred while processing the booster. Please contact a staff member");
                    Bukkit.getPlayer(uuid).sendMessage("Exploiting the system will result in a permanent ban. (This message has been logged)");
                    System.out.println("SQL error when removing booster - " + Bukkit.getPlayer(uuid).getDisplayName());
                    e.printStackTrace();
                }

            }


        });


    }

    //Anyone with less than the time given will be deleted
    public void purgeData(final int time) {

        Bukkit.getScheduler().runTaskAsynchronously(PlayerTimer.getInstance(), new Runnable() {
            @Override
            public void run() {
                try {

                    Connection connection = PlayerTimer.getInstance().getHikari().getConnection();

                    PreparedStatement selectTime = connection.prepareStatement("DELETE FROM PlayerTimerAndBoosters WHERE time <= " + time);
                    selectTime.execute();

                    connection.close();


                } catch (SQLException e) {
                    e.printStackTrace();

                }

            }
        });

    }
    //Wont allow me to use a thread onServerDisable, so I have to use the main thread, so I made a new method
    public void savePlayerOnDisable(final Player p) {
                try {

                    Connection connection = PlayerTimer.getInstance().getHikari().getConnection();
                    PreparedStatement statement = connection.prepareStatement(SAVETIME);

                    statement.setInt(1, getPlayTime(p));
                    statement.setString(2, p.getUniqueId().toString());
                    statement.execute();
                    connection.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }


    }



    public int getTimeOfPlayer(final UUID playerUUID){
        ExecutorService executor = Executors.newCachedThreadPool();
        Future<Integer> future = executor.submit(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                try{
                    Connection connection = PlayerTimer.getInstance().getHikari().getConnection();
                    PreparedStatement selectTime = connection.prepareStatement(SELECTTIME);
                    selectTime.setString(1, playerUUID.toString());

                    ResultSet result = selectTime.executeQuery();

                    int playTime;
                    if (result.next()) {
                        playTime = result.getInt("time");
                        connection.close();
                        return playTime;
                    }
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return -1;
            }

        });
        try {
            return future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return -1;

    }
}
