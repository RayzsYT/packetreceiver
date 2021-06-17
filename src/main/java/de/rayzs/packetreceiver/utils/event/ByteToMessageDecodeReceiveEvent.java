package de.rayzs.packetreceiver.utils.event;

import org.bukkit.event.*;
import de.rayzs.packetreceiver.utils.player.PacketPlayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class ByteToMessageDecodeReceiveEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();
    private final PacketPlayer packetPlayer;
    private final Channel channel;
    private final ByteBuf byteBuf;

    private boolean cancelled = false;

    public ByteToMessageDecodeReceiveEvent(final Channel channel, final ByteBuf byteBuf) {
        this.packetPlayer = null;
        this.channel = channel;
        this.byteBuf = byteBuf;
    }

    public ByteToMessageDecodeReceiveEvent(final PacketPlayer packetPlayer, final ByteBuf byteBuf) {
        this.packetPlayer = packetPlayer;
        this.channel = packetPlayer.getChannel();
        this.byteBuf = byteBuf;
    }

    @Override public void setCancelled(final boolean shouldCancelled) { this.cancelled = shouldCancelled; }

    @Override public HandlerList getHandlers() { return handlerList; }
    @Override public boolean isCancelled() { return this.cancelled; }
    public static HandlerList getHandlerList() { return handlerList; }
    public PacketPlayer getPacketPlayer() { return this.packetPlayer; }
    public Channel getChannel() { return this.channel; }
    public ByteBuf getByteBuf() { return byteBuf; }
    public boolean isPlayer() { return this.packetPlayer != null; }
}