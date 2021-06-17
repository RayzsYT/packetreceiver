package de.rayzs.packetreceiver.plugin.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.*;
import de.rayzs.packetreceiver.utils.player.PacketPlayer;
import de.rayzs.packetreceiver.packet.PacketReader;
import de.rayzs.packetreceiver.plugin.PacketReceiver;
import io.netty.channel.Channel;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    private final PacketReceiver packetReceiver;

    public PlayerJoin(final PacketReceiver packetReceiver) {
        this.packetReceiver = packetReceiver;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent playerJoinEvent) {
        final Player player = playerJoinEvent.getPlayer();
        final PacketPlayer packetPlayer = new PacketPlayer(player);
        final Channel channel = packetPlayer.getChannel();

        if(packetReceiver.hasPacketReader(channel)) packetReceiver.removePacketReader(channel);
        Bukkit.getScheduler().scheduleSyncDelayedTask(packetReceiver, () -> packetReceiver.setPacketReader(channel, new PacketReader(packetPlayer)));

        if(!packetReceiver.hasAvailableUpdate()) return; if(!player.isOp()) return;
        Bukkit.getScheduler().runTaskLater(packetReceiver, () -> player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "PacketReceiver" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "You're using an outdated version! You can download the newest version here:\n" + ChatColor.RED + " -> " + ChatColor.UNDERLINE + ChatColor.ITALIC + "https://www.rayzs.de/packetreceiver"), 20);
    }
}