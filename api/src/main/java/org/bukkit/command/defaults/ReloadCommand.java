package org.bukkit.command.defaults;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand extends BukkitCommand {
    public ReloadCommand(@NotNull String name) {
        super(name);
        this.description = "Reloads the server configuration and plugins";
        this.usageMessage = "/reload [permissions]"; // Paper
        this.setPermission("bukkit.command.reload");
        this.setAliases(Arrays.asList("rl"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String currentAlias, @NotNull String[] args) { // Paper
        if (!testPermission(sender)) return true;

        // Paper start - Reload permissions.yml & require confirm
        boolean confirmed = System.getProperty("LetMeReload") != null;
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("permissions")) {
                Bukkit.getServer().reloadPermissions();
                Command.broadcastCommandMessage(sender, net.kyori.adventure.text.Component.text("Permissions successfully reloaded.", net.kyori.adventure.text.format.NamedTextColor.GREEN));
                return true;
            } else if ("confirm".equalsIgnoreCase(args[0])) {
                confirmed = true;
            } else {
                Command.broadcastCommandMessage(sender, net.kyori.adventure.text.Component.text("Usage: " + usageMessage, net.kyori.adventure.text.format.NamedTextColor.RED));
                return true;
            }
        }
        if (!confirmed) {
            Command.broadcastCommandMessage(sender, net.kyori.adventure.text.Component.text("Are you sure you wish to reload your server? Doing so may cause bugs and memory leaks. It is recommended to restart instead of using /reload. To confirm, please type ", net.kyori.adventure.text.format.NamedTextColor.RED).append(net.kyori.adventure.text.Component.text("/reload confirm", net.kyori.adventure.text.format.NamedTextColor.YELLOW)));
            return true;
        }
        // Paper end

        Command.broadcastCommandMessage(sender, ChatColor.RED + "Please note that this command is not supported and may cause issues when using some plugins.");
        Command.broadcastCommandMessage(sender, ChatColor.RED + "If you encounter any issues please use the /stop command to restart your server.");
        Bukkit.reload();
        Command.broadcastCommandMessage(sender, ChatColor.GREEN + "Reload complete.");

        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return java.util.Collections.singletonList("permissions"); // Paper
    }
}
