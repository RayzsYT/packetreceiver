package de.rayzs.packetreceiver.plugin.listener;

import org.bukkit.event.*;
import de.rayzs.packetreceiver.utils.player.PacketPlayer;
import de.rayzs.packetreceiver.plugin.PacketReceiver;
import io.netty.channel.Channel;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    private final PacketReceiver packetReceiver;

    public PlayerQuit(final PacketReceiver packetReceiver) {
        this.packetReceiver = packetReceiver;
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent playerQuitEvent) {
        final Player player = playerQuitEvent.getPlayer();
        final PacketPlayer packetPlayer = new PacketPlayer(player);
        final Channel channel = packetPlayer.getChannel();

        if (!packetReceiver.hasPacketReader(channel)) return;
        packetReceiver.removePacketReader(channel);
    }
}