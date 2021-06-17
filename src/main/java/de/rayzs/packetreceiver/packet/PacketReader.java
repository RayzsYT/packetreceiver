package de.rayzs.packetreceiver.packet;

import de.rayzs.packetreceiver.packet.reader.*;
import de.rayzs.packetreceiver.utils.player.PacketPlayer;
import io.netty.channel.Channel;

public class PacketReader {

    private final Channel channel;
    private PacketPlayer packetPlayer;

    public PacketReader(final Channel channel) {
        this.channel = channel;
        this.packetPlayer = null;
        inject();
    }

    public PacketReader(final PacketPlayer packetPlayer) {
        this.channel = packetPlayer.getChannel();
        this.packetPlayer = packetPlayer;
        inject();
    }

    public void setPacketPlayer(final PacketPlayer packetPlayer) { this.packetPlayer = packetPlayer; }

    public PacketPlayer getPacketPlayer() { return packetPlayer; }

    public boolean hasPacketPlayer() { return packetPlayer != null; }

    public void inject() {
       messageDuplexInjector();
       messageDecoderInjector();
       byteDecoderInjector();
    }

    protected void messageDuplexInjector() {
        try {
            channel.pipeline().addBefore("packet_handler", "pr-messageDuplex", new ChannelDuplexHandlerReader(this));
        }catch (Exception exception) {
            channel.pipeline().remove("pr-messageDuplex");
            messageDuplexInjector();
        }
    }

    protected void messageDecoderInjector() {
        try {
            channel.pipeline().addAfter("decoder", "pr-messageDecoder", new MessageToMessageReader(this));
        }catch (Exception exception) {
            channel.pipeline().remove("pr-messageDecoder");
            messageDecoderInjector();
        }
    }

    protected void byteDecoderInjector() {
        try {
            if (channel.pipeline().get("decompress") != null) {
                channel.pipeline().addAfter("decompress", "pr-byteDecoder", new ByteToMessageReader(this));
                return;
            }
            channel.pipeline().addAfter("splitter", "pr-byteDecoder", new ByteToMessageReader(this));
        }catch (Exception exception) {
            channel.pipeline().remove("pr-byteDecoder");
            byteDecoderInjector();
        }
    }
}