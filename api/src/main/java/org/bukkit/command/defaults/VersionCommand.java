package org.bukkit.command.defaults;

import com.destroystokyo.paper.util.VersionFetcher; // Paper - version supplier
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

public class VersionCommand extends BukkitCommand {
    private VersionFetcher versionFetcher;
    private VersionFetcher getVersionFetcher() { // lazy load because unsafe isn't available at command registration
        if (versionFetcher == null) {
            versionFetcher = Bukkit.getUnsafe().getVersionFetcher();
        }

        return versionFetcher;
    }

    public VersionCommand(@NotNull String name) {
        super(name);

        this.description = "Gets the version of this server including any plugins in use";
        this.usageMessage = "/version [plugin name]";
        this.setPermission("bukkit.command.version");
        this.setAliases(Arrays.asList("ver", "about"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String currentAlias, @NotNull String[] args) {
        if (!testPermission(sender)) return true;

        if (args.length == 0) {
            //sender.sendMessage("This server is running " + Bukkit.getName() + " version " + Bukkit.getVersion() + " (Implementing API version " + Bukkit.getBukkitVersion() + ")"); // Paper - moved to setVersionMessage
            sendVersion(sender);
        } else {
            StringBuilder name = new StringBuilder();

            for (String arg : args) {
                if (name.length() > 0) {
                    name.append(' ');
                }

                name.append(arg);
            }

            String pluginName = name.toString();
            Plugin exactPlugin = Bukkit.getPluginManager().getPlugin(pluginName);
            if (exactPlugin != null) {
                describeToSender(exactPlugin, sender);
                return true;
            }

            boolean found = false;
            pluginName = pluginName.toLowerCase(java.util.Locale.ENGLISH);
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                if (plugin.getName().toLowerCase(java.util.Locale.ENGLISH).contains(pluginName)) {
                    describeToSender(plugin, sender);
                    found = true;
                }
            }

            if (!found) {
                sender.sendMessage("This server is not running any plugin by that name.");
                sender.sendMessage("Use /plugins to get a list of plugins.");
            }
        }
        return true;
    }

    private void describeToSender(@NotNull Plugin plugin, @NotNull CommandSender sender) {
        PluginDescriptionFile desc = plugin.getDescription();
        sender.sendMessage(ChatColor.GREEN + desc.getName() + ChatColor.WHITE + " version " + ChatColor.GREEN + desc.getVersion());

        if (desc.getDescription() != null) {
            sender.sendMessage(desc.getDescription());
        }

        if (desc.getWebsite() != null) {
            sender.sendMessage("Website: " + ChatColor.GREEN + desc.getWebsite());
        }

        if (!desc.getAuthors().isEmpty()) {
            if (desc.getAuthors().size() == 1) {
                sender.sendMessage("Author: " + getNameList(desc.getAuthors()));
            } else {
                sender.sendMessage("Authors: " + getNameList(desc.getAuthors()));
            }
        }

        if (!desc.getContributors().isEmpty()) {
            sender.sendMessage("Contributors: " + getNameList(desc.getContributors()));
        }
    }

    @NotNull
    private String getNameList(@NotNull final List<String> names) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < names.size(); i++) {
            if (result.length() > 0) {
                result.append(ChatColor.WHITE);

                if (i < names.size() - 1) {
                    result.append(", ");
                } else {
                    result.append(" and ");
                }
            }

            result.append(ChatColor.GREEN);
            result.append(names.get(i));
        }

        return result.toString();
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        Preconditions.checkArgument(sender != null, "Sender cannot be null");
        Preconditions.checkArgument(args != null, "Arguments cannot be null");
        Preconditions.checkArgument(alias != null, "Alias cannot be null");

        if (args.length == 1) {
            List<String> completions = new ArrayList<String>();
            String toComplete = args[0].toLowerCase(java.util.Locale.ENGLISH);
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                if (StringUtil.startsWithIgnoreCase(plugin.getName(), toComplete)) {
                    completions.add(plugin.getName());
                }
            }
            return completions;
        }
        return ImmutableList.of();
    }

    private final ReentrantLock versionLock = new ReentrantLock();
    private boolean hasVersion = false;
    private net.kyori.adventure.text.Component versionMessage = null; // Paper
    private final Set<CommandSender> versionWaiters = new HashSet<CommandSender>();
    private boolean versionTaskStarted = false;
    private long lastCheck = 0;

    private void sendVersion(@NotNull CommandSender sender) {
        if (hasVersion) {
            if (System.currentTimeMillis() - lastCheck > getVersionFetcher().getCacheTime()) { // Paper - use version supplier
                lastCheck = System.currentTimeMillis();
                hasVersion = false;
            } else {
                sender.sendMessage(versionMessage);
                return;
            }
        }
        versionLock.lock();
        try {
            if (hasVersion) {
                sender.sendMessage(versionMessage);
                return;
            }
            versionWaiters.add(sender);
            sender.sendMessage(net.kyori.adventure.text.Component.text("Checking version, please wait...", net.kyori.adventure.text.format.NamedTextColor.WHITE, net.kyori.adventure.text.format.TextDecoration.ITALIC)); // Paper
            if (!versionTaskStarted) {
                versionTaskStarted = true;
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        obtainVersion();
                    }
                }).start();
            }
        } finally {
            versionLock.unlock();
        }
    }

    private void obtainVersion() {
        String version = Bukkit.getVersion();
        // Paper start
        if (version.startsWith("null")) { // running from ide?
            setVersionMessage(net.kyori.adventure.text.Component.text("Unknown version, custom build?", net.kyori.adventure.text.format.NamedTextColor.YELLOW));
            return;
        }
        setVersionMessage(getVersionFetcher().getVersionMessage(version));
        /*
        if (version == null) version = "Custom";
        String[] parts = version.substring(0, version.indexOf(' ')).split("-");
        if (parts.length == 4) {
            int cbVersions = getDistance("craftbukkit", parts[3]);
            int spigotVersions = getDistance("spigot", parts[2]);
            if (cbVersions == -1 || spigotVersions == -1) {
                setVersionMessage("Error obtaining version information");
            } else {
                if (cbVersions == 0 && spigotVersions == 0) {
                    setVersionMessage("You are running the latest version");
                } else {
                    setVersionMessage("You are " + (cbVersions + spigotVersions) + " version(s) behind");
                }
            }

        } else if (parts.length == 3) {
            int cbVersions = getDistance("craftbukkit", parts[2]);
            if (cbVersions == -1) {
                setVersionMessage("Error obtaining version information");
            } else {
                if (cbVersions == 0) {
                    setVersionMessage("You are running the latest version");
                } else {
                    setVersionMessage("You are " + cbVersions + " version(s) behind");
                }
            }
        } else {
            setVersionMessage("Unknown version, custom build?");
        }
         */
        // Paper end
    }

    // Paper start
    private void setVersionMessage(final @NotNull net.kyori.adventure.text.Component msg) {
        lastCheck = System.currentTimeMillis();
        final net.kyori.adventure.text.Component message = net.kyori.adventure.text.TextComponent.ofChildren(
            net.kyori.adventure.text.Component.text("This server is running " + Bukkit.getName() + " version " + Bukkit.getVersion() + " (Implementing API version " + Bukkit.getBukkitVersion() + ")", net.kyori.adventure.text.format.NamedTextColor.WHITE),
            net.kyori.adventure.text.Component.newline(),
            msg
        );
        this.versionMessage = net.kyori.adventure.text.Component.text()
            .append(message)
            .hoverEvent(net.kyori.adventure.text.Component.text("Click to copy to clipboard", net.kyori.adventure.text.format.NamedTextColor.WHITE))
            .clickEvent(net.kyori.adventure.text.event.ClickEvent.copyToClipboard(net.kyori.adventure.text.serializer.plain.PlainComponentSerializer.plain().serialize(message)))
            .build();
        // Paper end
        versionLock.lock();
        try {
            hasVersion = true;
            versionTaskStarted = false;
            for (CommandSender sender : versionWaiters) {
                sender.sendMessage(versionMessage);
            }
            versionWaiters.clear();
        } finally {
            versionLock.unlock();
        }
    }

    private static int getDistance(@NotNull String repo, @NotNull String hash) {
        try {
            BufferedReader reader = Resources.asCharSource(
                    new URL("https://hub.spigotmc.org/stash/rest/api/1.0/projects/SPIGOT/repos/" + repo + "/commits?since=" + URLEncoder.encode(hash, "UTF-8") + "&withCounts=true"),
                    Charsets.UTF_8
            ).openBufferedStream();
            try {
                JsonObject obj = new Gson().fromJson(reader, JsonObject.class);
                return obj.get("totalCount").getAsInt();
            } catch (JsonSyntaxException ex) {
                ex.printStackTrace();
                return -1;
            } finally {
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
