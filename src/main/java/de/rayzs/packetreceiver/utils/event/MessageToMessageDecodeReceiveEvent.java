package de.rayzs.packetreceiver.utils.event;

import org.bukkit.event.*;
import de.rayzs.packetreceiver.utils.player.PacketPlayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class MessageToMessageDecodeReceiveEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();
    private final PacketPlayer packetPlayer;
    private final Channel channel;
    private final Object packet;
    private final String packetName;
    private final ByteBuf byteBuf;

    private boolean cancelled = false;

    public MessageToMessageDecodeReceiveEvent(final PacketPlayer packetPlayer, final Object packet, final ByteBuf byteBuf) {
        this.packetPlayer = packetPlayer;
        this.packet = packet;
        this.packetName = packet.getClass().getSimpleName();
        this.channel = packetPlayer.getChannel();
        this.byteBuf = byteBuf;
    }

    public MessageToMessageDecodeReceiveEvent(final Channel channel, final Object packet, final ByteBuf byteBuf) {
        this.packetPlayer = null;
        this.packet = packet;
        this.packetName = packet.getClass().getSimpleName();
        this.channel = channel;
        this.byteBuf = byteBuf;
    }

    @Override public void setCancelled(final boolean shouldCancelled) { this.cancelled = shouldCancelled; }

    @Override public HandlerList getHandlers() { return handlerList; }
    @Override public boolean isCancelled() { return this.cancelled; }
    public static HandlerList getHandlerList() { return handlerList; }
    public Object getPacket() { return this.packet; }
    public PacketPlayer getPacketPlayer() { return this.packetPlayer; }
    public String getPacketName() { return this.packetName; }
    public Channel getChannel() { return this.channel; }
    public ByteBuf getByteBuf() { return byteBuf; }
    public boolean isPlayer() { return this.packetPlayer != null; }
}