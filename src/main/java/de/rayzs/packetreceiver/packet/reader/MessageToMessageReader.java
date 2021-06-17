package de.rayzs.packetreceiver.packet.reader;

import de.rayzs.packetreceiver.packet.*;
import io.netty.buffer.*;
import de.rayzs.packetreceiver.utils.event.MessageToMessageDecodeReceiveEvent;
import de.rayzs.packetreceiver.enums.ReflectionType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.bukkit.Bukkit;

import java.util.List;

public class MessageToMessageReader extends MessageToMessageDecoder {

    private final Reflection reflection;
    private final PacketReader packetReader;

    public MessageToMessageReader(final PacketReader packetReader) {
        this.reflection = new Reflection();
        this.packetReader = packetReader;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, Object object, List list) throws Exception {
        try {
            if (object == null || !channelHandlerContext.channel().isActive()) return;
            else list.add(object);

            final String packetName = object.getClass().getSimpleName();
            final ByteBuf byteBuf = Unpooled.buffer();
            final Class<?> packetDataSerializerClass = reflection.getNMSClass("PacketDataSerializer", ReflectionType.MINECRAFT);
            final Object packetDataSerializer = packetDataSerializerClass.getDeclaredConstructor(ByteBuf.class).newInstance(byteBuf);
            final Class<?> packetClass = reflection.getNMSClass(packetName, ReflectionType.MINECRAFT);

            final Object packet = packetClass.cast(object);
            if (!packetName.equals("PacketPlayInCustomPayload")) packetClass.getDeclaredMethod("b", packetDataSerializerClass).invoke(packet, packetDataSerializer);

            if (packetReader.hasPacketPlayer()) {
                final MessageToMessageDecodeReceiveEvent messageToMessageDecodeEvent = new MessageToMessageDecodeReceiveEvent(packetReader.getPacketPlayer(), object, byteBuf);
                Bukkit.getPluginManager().callEvent(messageToMessageDecodeEvent);
                if (messageToMessageDecodeEvent.isCancelled()) list.remove(object);
                return;
            }
            final MessageToMessageDecodeReceiveEvent messageToMessageDecodeEvent = new MessageToMessageDecodeReceiveEvent(channelHandlerContext.channel(), object, byteBuf);
            Bukkit.getPluginManager().callEvent(messageToMessageDecodeEvent);
            if (messageToMessageDecodeEvent.isCancelled()) list.remove(object);
        } catch (final Exception ignored) { }
    }
}