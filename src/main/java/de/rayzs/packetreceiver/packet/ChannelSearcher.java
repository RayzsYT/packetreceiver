package de.rayzs.packetreceiver.packet;

import java.lang.reflect.*;
import java.util.*;

import de.rayzs.packetreceiver.enums.ReflectionType;
import de.rayzs.packetreceiver.plugin.PacketReceiver;
import org.bukkit.Bukkit;
import io.netty.channel.Channel;

public class ChannelSearcher extends Reflection {

    private final PacketReceiver packetReceiver;
    private List<?> networkManagers;
    private final Class<?> networkClass;
    private Channel newestChannel;
    private boolean shouldSearch;

    // Credits for the methode to invoke incoming channels:
    // https://www.spigotmc.org/resources/pingapi.3829/

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ChannelSearcher(final PacketReceiver packetReceiver) {
        this.shouldSearch = true;
        this.packetReceiver = packetReceiver;
        this.networkClass = getNMSClass("NetworkManager", ReflectionType.MINECRAFT);
        try {
            final Object server = Bukkit.getServer();
            final Object craftServer =getNMSClass("CraftServer", ReflectionType.CRAFTBUKKIT).cast(server);
            final Field console = craftServer.getClass().getDeclaredField("console"); console.setAccessible(true);
            final Class<?> minecraftServerClass = getNMSClass("MinecraftServer", ReflectionType.MINECRAFT);
            final Object castedMinecraftServer = minecraftServerClass.cast(console.get(craftServer));
            final Object minecraftServer = castedMinecraftServer.getClass().getMethod("getServer").invoke(castedMinecraftServer);
            final Object serverConnection = minecraftServer.getClass().getMethod("getServerConnection").invoke(minecraftServer);
            this.networkManagers = Collections.synchronizedList((List) getInvokedNetworkManager(serverConnection));
        } catch (Exception exception) { exception.printStackTrace(); }
        new Thread(() -> { while(shouldSearch) injectConnection(); }).start();
    }

    public void setShouldSearch(final boolean bool) { this.shouldSearch = bool; }

    protected void injectConnection() {
        try {
            Field field = getFirstFieldByType(networkClass, Channel.class);
            if(field == null) return;
            field.setAccessible(true);
            if(networkManagers.isEmpty()) return;
            for (Object manager : this.networkManagers) {
                final Channel channel = (Channel) field.get(manager);

                if(newestChannel == channel) return;

                newestChannel = channel;

                if(packetReceiver.hasPacketReader(channel)) continue;

                if (channel == null) return;
                if (channel.remoteAddress() == null) {
                    channel.close();
                    return;
                }

                packetReceiver.setPacketReader(channel, new PacketReader(channel));
            }
        } catch (final Exception ignored) { }
    }

    protected Object getInvokedNetworkManager(final Object serverConnection) {
        try {
            Method[] methods = serverConnection.getClass().getDeclaredMethods();
            for (final Method method : methods) {
                method.setAccessible(true);
                if (method.getReturnType() != List.class) continue;
                return method.invoke(null, serverConnection);
            }
        } catch (final Exception exception) { exception.printStackTrace(); }
        return null;
    }
}