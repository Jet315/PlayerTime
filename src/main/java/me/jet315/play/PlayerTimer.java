package me.jet315.play;

import com.zaxxer.hikari.HikariDataSource;
import me.jet315.play.antiafk.AntiAFK;
import me.jet315.play.commands.CommandHandler;
import me.jet315.play.events.*;
import me.jet315.play.manager.Clock;
import me.jet315.play.manager.GUI;
import me.jet315.play.manager.PlayerManager;
import me.realized.tm.api.TMAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Jet on 25/04/2017.
 */
public class PlayerTimer extends JavaPlugin{

    private static PlayerTimer instance;
    private HikariDataSource hikari;
    private PlayerManager pm;
    public AntiAFK afk;
    public Clock clock;
    private GUI g;
    private String defaultRank;
    @Override
    public void onEnable() {
        instance = this;
        hikari = new HikariDataSource();
        g = new GUI();
        pm = new PlayerManager();
        afk = new AntiAFK();
        int timeout = getConfig().getInt("timeout");
        int commandDelay = getConfig().getInt("minutesPlayedToExecuteCommands");
        defaultRank = getConfig().getString("defaultRank");
        clock = new Clock(timeout,commandDelay,defaultRank,pm);


        Bukkit.getServer().getPluginManager().registerEvents(new JoinEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new LeaveEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new InvClick(getConfig().getString("serverName"),defaultRank), this);

        Bukkit.getServer().getPluginManager().registerEvents(new PreCommand(), this);
        //Bukkit.getServer().getPluginManager().registerEvents(new MineAFK(), this);

        getCommand("boosters").setExecutor(new CommandHandler());
        getCommand("timeplayed").setExecutor(new CommandHandler());
        createConfig();
        connectToDataBase();
        createTable();
        for(Player p : Bukkit.getOnlinePlayers()){
            getPM().loadPlayer(p);
        }

    }

    @Override
    public void onDisable() {
        for(Player p : Bukkit.getOnlinePlayers()){
            getPM().savePlayerOnDisable(p);
        }
        clock = null;
        pm = null;
        g = null;
        afk = null;
        instance = null;
        if (hikari != null)
            hikari.close();


    }

    private void connectToDataBase(){
        String ip = getConfig().getString("database.mySQLHost");
        String port = getConfig().getString("database.mySQLPort");
        String database = getConfig().getString("database.mySQLDatabase");
        String username = getConfig().getString("database.mySQLUser");
        String password = getConfig().getString("database.mySQLPassword");

        hikari.setJdbcUrl("jdbc:mysql://"+ip+":"+port+"/"+database);

        hikari.addDataSourceProperty("user", username);
        hikari.addDataSourceProperty("password", password);

    }


    private void createTable(){
        try(Connection connection = hikari.getConnection();
            Statement statement = connection.createStatement()){
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS PlayerTimerAndBoosters(unique_id CHAR(36), time int, boosters2 int,boosters3 int,boosters4 int,boosters5 int)");

            //Alter table if needed (I need to make the first row UNIQUE for the plugin to work, so I do this way)
            ResultSet result = statement.executeQuery("SELECT unique_id FROM PlayerTimerAndBoosters order by unique_id asc limit 1;");
            if(!result.next()){
                statement.executeUpdate("ALTER TABLE PlayerTimerAndBoosters ADD CONSTRAINT unique_id UNIQUE(unique_id)");
            }
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createConfig(){
            try {
                if (!getDataFolder().exists()) {
                    getDataFolder().mkdirs();
                }
                File file = new File(getDataFolder(), "config.yml");
                if (!file.exists()) {
                    getLogger().info("Config.yml not found, creating!");
                    saveDefaultConfig();
                } else {
                    getLogger().info("Config.yml found, loading!");
                }
            } catch (Exception e) {
                e.printStackTrace();


        }
    }

    public static PlayerTimer getInstance(){
        return instance;
    }

    public HikariDataSource getHikari(){
        return hikari;
    }

    public PlayerManager getPM() {
        return pm;
    }

    public GUI getG() {
        return g;
    }
}
