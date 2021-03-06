package github.BTEPlotSystem.commands;

import github.BTEPlotSystem.core.menus.CompanionMenu;
import github.BTEPlotSystem.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDTpp implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            if(sender.hasPermission("alpsbte.tpp")) {
                Player player = (Player) sender;
                try {
                    Player targetPlayer = player.getServer().getPlayer(args[0]);
                    player.teleport(targetPlayer);
                    player.sendMessage(Utils.getInfoMessageFormat("Teleporting to player..."));

                    player.getInventory().setItem(8, CompanionMenu.getItem());
                } catch (Exception ignore) {
                    player.sendMessage(Utils.getErrorMessageFormat("Usage: /tpp <Player>"));
                }
            }
        }
        return true;
    }
}
