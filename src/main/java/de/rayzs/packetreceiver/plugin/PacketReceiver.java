package de.rayzs.packetreceiver.plugin;

import de.rayzs.packetreceiver.utils.json.JsonReader;
import de.rayzs.packetreceiver.utils.player.PacketPlayer;
import de.rayzs.packetreceiver.packet.*;
import de.rayzs.packetreceiver.plugin.listener.*;
import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;

public class PacketReceiver extends JavaPlugin {

    private static PacketReceiver instance;

    protected final String pluginVersion = "0.0.1";

    private boolean availableUpdate;
    private String serverVersion = "error";
    private ChannelSearcher channelSearcher;
    private HashMap<Channel, PacketReader> packetReaderHashMap;

    @Override
    public void onDisable() {
        channelSearcher.setShouldSearch(false);
    }

    @Override
    public void onEnable() {

        this.availableUpdate = false;

        getLogger().info("Searching for new updates...");
        availableUpdate = !new JsonReader("https://www.rayzs.de/packetreceiver/data.json").get("version").toString().equalsIgnoreCase(pluginVersion);
        if (availableUpdate) getLogger().info("This version is outdated! You can download the newest version on \"https://www.rayzs.de/packetreceiver\".");
        else getLogger().info("You're using the newest version!");
        if(availableUpdate) Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(player -> player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "PacketReceiver" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "You're using an outdated version! You can download the newest version here:\n" + ChatColor.RED + " -> " + ChatColor.UNDERLINE + ChatColor.ITALIC + "https://www.rayzs.de/packetreceiver"));

        instance = this;

        final String packageName = Bukkit.getServer().getClass().getPackage().getName();
        serverVersion = packageName.substring(packageName.lastIndexOf('.') + 1);

        final PluginManager pluginManager = Bukkit.getPluginManager();

        this.packetReaderHashMap = new HashMap<>();

        this.channelSearcher = new ChannelSearcher(this);

        pluginManager.registerEvents(new PlayerJoin(this), this);
        pluginManager.registerEvents(new PlayerQuit(this), this);

        Bukkit.getOnlinePlayers().forEach(player -> {
            final PacketPlayer packetPlayer = new PacketPlayer(player);
            final Channel channel = packetPlayer.getChannel();

            if (hasPacketReader(channel)) {
                getPacketReaderByChannel(channel).setPacketPlayer(packetPlayer);
            } else setPacketReader(channel, new PacketReader(packetPlayer));
        });
    }

    public void setPacketReader(final Channel channel, final PacketReader packetReader) { this.packetReaderHashMap.put(channel, packetReader); }
    public void removePacketReader(final Channel channel) { this.packetReaderHashMap.remove(channel); }

    public PacketReader getPacketReaderByChannel(final Channel channel) { return this.packetReaderHashMap.get(channel); }
    public boolean hasPacketReader(final Channel channel) { return getPacketReaderByChannel(channel) != null; }
    public String getServerVersion() { return serverVersion; }

    public boolean hasAvailableUpdate() { return availableUpdate; }

    public static PacketReceiver getInstance() { return instance; }
}