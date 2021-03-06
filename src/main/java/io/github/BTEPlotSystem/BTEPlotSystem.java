package github.BTEPlotSystem;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.onarandombox.MultiverseCore.MultiverseCore;
import github.BTEPlotSystem.commands.*;
import github.BTEPlotSystem.core.DatabaseConnection;
import github.BTEPlotSystem.core.EventListener;
import github.BTEPlotSystem.core.holograms.EventHologram;
import github.BTEPlotSystem.core.holograms.HolographicDisplay;
import github.BTEPlotSystem.core.holograms.ParkourLeaderboard;
import github.BTEPlotSystem.core.holograms.ScoreLeaderboard;
import github.BTEPlotSystem.utils.PortalManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.MenuFunctionListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class BTEPlotSystem extends JavaPlugin {
    private static BTEPlotSystem plugin;

    private static MultiverseCore multiverseCore;

    private FileConfiguration leaderboardConfig;
    private FileConfiguration navigatorConfig;
    private FileConfiguration config;
    private File configFile;

    private final static List<HolographicDisplay> holograms = new ArrayList<>();

    @Override
    public void onEnable() {
        plugin = this;
        multiverseCore = (MultiverseCore) getServer().getPluginManager().getPlugin("Multiverse-Core");

        reloadConfig();

        // Connect to Database
        DatabaseConnection.ConnectToDatabase();

        // Add Listener
        this.getServer().getPluginManager().registerEvents(new EventListener(), plugin);
        this.getServer().getPluginManager().registerEvents(new MenuFunctionListener(), plugin);

        // Add Commands
        this.getCommand("plot").setExecutor(new CMDPlot());
        this.getCommand("generateplot").setExecutor(new CMDGeneratePlot());
        this.getCommand("finish").setExecutor(new CMDFinish());
        this.getCommand("abandon").setExecutor(new CMDAbandon());

        this.getCommand("companion").setExecutor(new CMDCompanion());
        this.getCommand("review").setExecutor(new CMDReview());

        this.getCommand("tpp").setExecutor(new CMDTpp());
        this.getCommand("spawn").setExecutor(new CMDSpawn());

        this.getCommand("sethologram").setExecutor(new CMDSetHologramPosition());
        this.getCommand("reloadhologram").setExecutor(new CMDReloadHolograms());


        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // Set holograms
        holograms.addAll(Arrays.asList(
                new ScoreLeaderboard(),
                new ParkourLeaderboard(),
                new EventHologram()
        ));
        holograms.forEach(Thread::start);

        new PortalManager().start();

        getLogger().log(Level.INFO, "Successfully enabled BTEPlotSystem plugin.");
    }

    public static BTEPlotSystem getPlugin() {
        return plugin;
    }
    public static MultiverseCore getMultiverseCore() { return multiverseCore; }

    public static List<HolographicDisplay> getHolograms() {
        return holograms;
    }

    @Override
    public void reloadConfig() {
        try{
            leaderboardConfig = YamlConfiguration.loadConfiguration(new File(Bukkit.getPluginManager().getPlugin("LeakParkour").getDataFolder(), "history.yml"));
        } catch (Exception ex){
            ex.printStackTrace();
        }

        configFile = new File(getDataFolder(), "config.yml");
        if (configFile.exists()) {
            config = YamlConfiguration.loadConfiguration(configFile);
        } else {
            // Look for default configuration file
            Reader defConfigStream = new InputStreamReader(this.getResource("defaultConfig.yml"), StandardCharsets.UTF_8);

            config = YamlConfiguration.loadConfiguration(defConfigStream);
        }
        saveConfig();
    }

    @Override
    public FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    public FileConfiguration getLeaderboardConfig() {
        try{
            leaderboardConfig = YamlConfiguration.loadConfiguration(new File(Bukkit.getPluginManager().getPlugin("LeakParkour").getDataFolder(), "history.yml"));
        } catch (Exception ex){
            Bukkit.getLogger().log(Level.SEVERE, "An error occurred while reading config file!", ex);
        }
        return leaderboardConfig;
    }

    public FileConfiguration getNavigatorConfig() {
        try {
            System.out.println(Bukkit.getPluginManager().getPlugin("AlpsBTE-Navigator").getDataFolder().getAbsolutePath());
            navigatorConfig = YamlConfiguration.loadConfiguration(new File(Bukkit.getPluginManager().getPlugin("AlpsBTE-Navigator").getDataFolder(), "config.yml"));
        } catch (Exception ex) {
            Bukkit.getLogger().log(Level.SEVERE, "An error occurred while reading config file!", ex);
        }
        return navigatorConfig;
    }

    @Override
    public void saveConfig() {
        if (config == null || configFile == null) {
            return;
        }

        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
        }
    }

    public void connectPlayer(Player player, String server) {
        try{
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("ConnectOther");
            out.writeUTF(player.getName());
            out.writeUTF(server);
            player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
        } catch (Exception ex){
            getLogger().log(Level.WARNING, "Could not connect player [" + player + "] to " + server, ex);
        }
    }
}
