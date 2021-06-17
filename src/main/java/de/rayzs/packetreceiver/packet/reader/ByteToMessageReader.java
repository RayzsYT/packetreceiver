package de.rayzs.packetreceiver.packet.reader;

import io.netty.buffer.*;
import io.netty.channel.*;
import de.rayzs.packetreceiver.utils.event.ByteToMessageDecodeReceiveEvent;
import de.rayzs.packetreceiver.packet.PacketReader;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.bukkit.Bukkit;

import java.util.List;

public class ByteToMessageReader extends ByteToMessageDecoder {

    private final PacketReader packetReader;

    public ByteToMessageReader(final PacketReader packetReader) { this.packetReader = packetReader; }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        final Channel channel = channelHandlerContext.channel();
        if (channel == null || !channel.isActive()) return;

        try {
            if (byteBuf instanceof EmptyByteBuf || byteBuf instanceof UnpooledUnsafeDirectByteBuf) list.add(byteBuf.readBytes(byteBuf.readableBytes()));

            if (packetReader.hasPacketPlayer()) {
                final ByteToMessageDecodeReceiveEvent byteToMessageDecodeEvent = new ByteToMessageDecodeReceiveEvent(packetReader.getPacketPlayer(), byteBuf);
                Bukkit.getPluginManager().callEvent(byteToMessageDecodeEvent);
                if (!byteToMessageDecodeEvent.isCancelled()) list.add(byteBuf.readBytes(byteBuf.readableBytes()));
                return;
            }

            final ByteToMessageDecodeReceiveEvent byteToMessageDecodeEvent = new ByteToMessageDecodeReceiveEvent(channelHandlerContext.channel(), byteBuf);
            Bukkit.getPluginManager().callEvent(byteToMessageDecodeEvent);
            if (!byteToMessageDecodeEvent.isCancelled()) list.add(byteBuf.readBytes(byteBuf.readableBytes()));
        } catch (final Exception exception) { exception.printStackTrace(); }
    }
}