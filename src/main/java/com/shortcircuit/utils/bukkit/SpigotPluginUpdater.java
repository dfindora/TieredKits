package com.shortcircuit.utils.bukkit;

import com.shortcircuit.utils.Version;
import com.shortcircuit.utils.bukkit.command.PermissionUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class SpigotPluginUpdater implements Runnable, Listener
{
    private static final String query_url = "http://www.spigotmc.org/api/general.php";
    private final Plugin plugin;
    private final int resource_id;
    private boolean update_failed = false;
    private Version latest_version;

    public SpigotPluginUpdater(Plugin plugin, int resource_id)
    {
        this.plugin = plugin;
        this.resource_id = resource_id;
        plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, this, 0L);
    }

    public static Version query(Plugin plugin, int resource_id) throws IOException
    {
        try
        {
            plugin.getLogger().info("Checking Spigot for updates...");
            URL url = new URL("http://www.spigotmc.org/api/general.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.getOutputStream()
                      .write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource="
                              + resource_id).getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            Scanner scanner = new Scanner(connection.getInputStream());

            while (scanner.hasNextLine())
            {
                builder.append(scanner.nextLine());
            }

            Version remote_version = new Version(builder.toString());
            Version local_version = new Version(plugin.getDescription().getVersion());
            int diff = local_version.compareTo(remote_version);
            if (diff < 0)
            {
                plugin.getLogger().info("An update is available: " + remote_version);
                plugin.getLogger().info("Download the update at " + (plugin.getDescription().getWebsite() == null ?
                                                                     "https://www.spigotmc.org/resources/" + resource_id
                                                                                                                  : plugin
                                                                             .getDescription().getWebsite()));
                return remote_version;
            }
            else if (diff > 0)
            {
                plugin.getLogger().info("This plugin's version is newer than any public releases");
                plugin.getLogger().info("Are you a developer, or a time traveler?");
                return null;
            }
            else
            {
                plugin.getLogger().info("This plugin is up-to-date");
                return null;
            }
        }
        catch (IOException var9)
        {
            plugin.getLogger().warning("Failed to check Spigot for updates:");
            plugin.getLogger().warning(var9.getClass().getCanonicalName() + ": " + var9.getMessage());
            throw var9;
        }
    }

    public void run()
    {
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);

        try
        {
            this.latest_version = query(this.plugin, this.resource_id);
        }
        catch (IOException var2)
        {
            this.update_failed = true;
        }

    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void notify(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        if (this.latest_version != null && PermissionUtils.hasPermission(player, "updater.notify"))
        {
            if (this.update_failed)
            {
                player.sendMessage(ChatColor.RED + String
                        .format("[%1$s] Failed to check Spigot for updates. Please check the console for more information.",
                                this.plugin.getName()));
                return;
            }

            player.sendMessage(ChatColor.AQUA + String
                    .format("[%1$s] An update has been found: %2$s", this.plugin.getName(), this.latest_version));
            player.sendMessage(
                    ChatColor.AQUA + "Download the update at " + (this.plugin.getDescription().getWebsite() == null ?
                                                                  "https://www.spigotmc.org/resources/"
                                                                  + this.resource_id : this.plugin.getDescription()
                                                                                                  .getWebsite()));
        }

    }
}
