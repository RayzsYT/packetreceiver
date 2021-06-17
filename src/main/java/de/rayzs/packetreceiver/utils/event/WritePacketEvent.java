package de.rayzs.packetreceiver.utils.event;

import de.rayzs.packetreceiver.utils.player.PacketPlayer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPromise;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WritePacketEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();
    private final PacketPlayer packetPlayer;
    private final Channel channel;
    private final Object packet;
    private final String packetName;
    private final ChannelPromise channelPromise;

    private boolean cancelled = false;

    public WritePacketEvent(final PacketPlayer packetPlayer, final Object packet, final ChannelPromise channelPromise) {
        this.packetPlayer = packetPlayer;
        this.packet = packet;
        this.packetName = packet.getClass().getSimpleName();
        this.channel = packetPlayer.getChannel();
        this.channelPromise = channelPromise;
    }

    public WritePacketEvent(final Channel channel, final Object packet, final ChannelPromise channelPromise) {
        this.packetPlayer = null;
        this.packet = packet;
        this.packetName = packet.getClass().getSimpleName();
        this.channel = channel;
        this.channelPromise = channelPromise;
    }

    @Override public void setCancelled(final boolean shouldCancelled) { this.cancelled = shouldCancelled; }

    @Override public HandlerList getHandlers() { return handlerList; }
    @Override public boolean isCancelled() { return this.cancelled; }
    public static HandlerList getHandlerList() { return handlerList; }
    public Object getPacket() { return this.packet; }
    public PacketPlayer getPacketPlayer() { return this.packetPlayer; }
    public String getPacketName() { return this.packetName; }
    public Channel getChannel() { return this.channel; }
    public ChannelPromise getChannelPromise() { return channelPromise; }
    public boolean isPlayer() { return this.packetPlayer != null; }
}