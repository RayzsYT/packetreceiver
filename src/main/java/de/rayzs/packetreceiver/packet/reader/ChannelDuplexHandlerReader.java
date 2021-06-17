package de.rayzs.packetreceiver.packet.reader;

import de.rayzs.packetreceiver.utils.event.WritePacketEvent;
import de.rayzs.packetreceiver.packet.PacketReader;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.Bukkit;

public class ChannelDuplexHandlerReader extends ChannelDuplexHandler {

    private final PacketReader packetReader;

    public ChannelDuplexHandlerReader(final PacketReader packetReader) {
        this.packetReader = packetReader;
    }

    @Override
    public void write(ChannelHandlerContext channelHandlerContext, Object object, ChannelPromise channelPromise) throws Exception {
        if(packetReader.hasPacketPlayer()) {
            final WritePacketEvent channelWritePacketEvent = new WritePacketEvent(packetReader.getPacketPlayer(), object, channelPromise);
            Bukkit.getPluginManager().callEvent(channelWritePacketEvent);
            if(!channelPromise.isCancelled()) super.write(channelHandlerContext, object, channelPromise);
            return;
        }

        final WritePacketEvent channelWritePacketEvent = new WritePacketEvent(channelHandlerContext.channel(), object, channelPromise);
        Bukkit.getPluginManager().callEvent(channelWritePacketEvent);
        if(!channelPromise.isCancelled()) super.write(channelHandlerContext, object, channelPromise);
    }
}