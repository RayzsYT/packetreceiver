package de.rayzs.packetreceiver.utils.player;

import de.rayzs.packetreceiver.packet.Reflection;
import io.netty.channel.Channel;
import org.bukkit.entity.Player;

public class PacketPlayer extends Reflection {

    private final Player player;

    public PacketPlayer(final Player player) { this.player = player; }

    public Player getPlayer() { return this.player; }
    public Channel getChannel() { return getChannel(this.player); }
}