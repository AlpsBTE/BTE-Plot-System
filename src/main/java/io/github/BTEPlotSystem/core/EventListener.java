package github.BTEPlotSystem.core;

import github.BTEPlotSystem.core.menus.CompanionMenu;
import github.BTEPlotSystem.core.menus.ReviewMenu;
import github.BTEPlotSystem.core.plots.PlotHandler;
import github.BTEPlotSystem.utils.Utils;
import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

public class EventListener extends SpecialBlocks implements Listener {

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event){
        event.setJoinMessage(null);
        event.getPlayer().teleport(Utils.getSpawnPoint());



        if (!event.getPlayer().getInventory().contains(CompanionMenu.getItem())){
            event.getPlayer().getInventory().setItem(8, CompanionMenu.getItem());
        }
        if (event.getPlayer().hasPermission("alpsbte.review")){
            if (!event.getPlayer().getInventory().contains(ReviewMenu.getItem())){
                event.getPlayer().getInventory().setItem(7, ReviewMenu.getItem());
            }
        }



        if(!event.getPlayer().hasPlayedBefore()) {
            try {
                PreparedStatement statement = DatabaseConnection.prepareStatement("INSERT INTO players (uuid, name) VALUES (?, ?)");
                statement.setString(1, event.getPlayer().getUniqueId().toString());
                statement.setString(2, event.getPlayer().getName());
                statement.execute();
            } catch (SQLException ex) {
                Bukkit.getLogger().log(Level.SEVERE, "Could not add player [" + event.getPlayer().getName() + "] to database!", ex);
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) throws SQLException {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)){
            if (event.getItem() != null && event.getItem().equals(CompanionMenu.getItem())){
                new CompanionMenu(event.getPlayer());
            } else if (event.getItem() != null && event.getItem().equals(ReviewMenu.getItem())){
                event.getPlayer().performCommand("review");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        PlotHandler.unloadPlot(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
        PlotHandler.unloadPlot(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event){
        if (event.getCurrentItem() != null && event.getCurrentItem().equals(CompanionMenu.getItem())){
            event.setCancelled(true);
        }
        if (event.getCurrentItem() != null && event.getCurrentItem().equals(ReviewMenu.getItem())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onlPlayerItemDropEvent(PlayerDropItemEvent event){
        if(event.getItemDrop() != null && event.getItemDrop().getItemStack().equals(CompanionMenu.getItem())) {
            event.setCancelled(true);
        }
        if(event.getItemDrop() != null && event.getItemDrop().getItemStack().equals(ReviewMenu.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBlockPlaceEvent(BlockPlaceEvent event) {
        if(event.canBuild()) {
            ItemStack item = event.getItemInHand();

            if(item.isSimilar(SeamlessSandstone)) {
                event.getBlockPlaced().setTypeIdAndData(43, (byte) 9, true);
            } else if(item.isSimilar(SeamlessStone)) {
                event.getBlockPlaced().setTypeIdAndData(43, (byte) 8, true);
            } else if(item.isSimilar(MushroomStem)) {
                event.getBlockPlaced().setTypeIdAndData(99, (byte) 10, true);
            } else if(item.isSimilar(LightBrownMushroom)) {
                event.getBlockPlaced().setTypeIdAndData(99, (byte) 0, true);
            } else if(item.isSimilar(BarkOakLog)) {
                event.getBlockPlaced().setTypeIdAndData(17, (byte) 12, true);
            } else if(item.isSimilar(BarkSpruceLog)) {
                event.getBlockPlaced().setTypeIdAndData(17, (byte) 13, true);
            } else if(item.isSimilar(BarkBirchLog)) {
                event.getBlockPlaced().setTypeIdAndData(17, (byte) 14, true);
            } else if(item.isSimilar(BarkJungleLog)) {
                event.getBlockPlaced().setTypeIdAndData(17, (byte) 15, true);
            } else if(item.isSimilar(BarkAcaciaLog)) {
                event.getBlockPlaced().setTypeIdAndData(162, (byte) 12, true);
            } else if(item.isSimilar(BarkDarkOakLog)) {
                event.getBlockPlaced().setTypeIdAndData(162, (byte) 13, true);
            }
        }
    }

    @EventHandler
    public void onDatabaseLoad(DatabaseLoadEvent event) {
       Utils.headDatabaseAPI = new HeadDatabaseAPI();
    }
}
