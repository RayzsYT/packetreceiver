package de.rayzs.packetreceiver.packet;

import de.rayzs.packetreceiver.enums.ReflectionType;
import de.rayzs.packetreceiver.plugin.PacketReceiver;
import io.netty.channel.Channel;
import org.bukkit.entity.Player;
import java.lang.reflect.Field;

public class Reflection {

    private final String version;

    public Reflection() { version = PacketReceiver.getInstance().getServerVersion(); }

    // START
    // Credits for the methode to get the first field by his type:
    // https://www.spigotmc.org/resources/pingapi.3829/
    public Field getFirstFieldByType(final Class<?> clazz, final Class<?> type) {
        Field[] arrayOfField = clazz.getDeclaredFields();
        for (final Field field : arrayOfField) {
            field.setAccessible(true);
            if (field.getType() == type) return field;
        }
        return null;
    }
    // END

    // The rest of this class here is from spigotmc ^^
    public void setValue(final Object object, final String name, final Object value) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(object, value);
        } catch (final Exception exception) { exception.printStackTrace(); }
    }

    public Object getValue(final Object object, final String name) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(object);
        } catch (final Exception exception) { exception.printStackTrace(); }
        return null;
    }

    public Class<?> getNMSClass(final String name, final ReflectionType reflectionType) {
        String path = "";
        if (reflectionType.equals(ReflectionType.MINECRAFT))
            path = "net.minecraft.server.";
        if (reflectionType.equals(ReflectionType.CRAFTBUKKIT))
            path = "org.bukkit.craftbukkit.";
        try {
            return Class.forName(path + version + "." + name);
        } catch (final ClassNotFoundException ignored) {
        } catch (final Exception exception) { exception.printStackTrace(); }
        return null;
    }

    public void sendPacket(final Player player, final Object packet) {
        try {
            final Object nmsPlayer = player.getClass().getMethod("getHandle").invoke(player);
            final Object playerConnection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet", ReflectionType.MINECRAFT))
                    .invoke(playerConnection, packet);
        } catch (final Exception exception) { exception.printStackTrace(); }
    }

    public Channel getChannel(final Player player) {
        try {
            final Object nmsPlayer = player.getClass().getMethod("getHandle").invoke(player);
            final Object playerConnection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
            final Object networkManager = playerConnection.getClass().getField("networkManager").get(playerConnection);
            return (Channel) networkManager.getClass().getField("channel").get(networkManager);
        } catch (final Exception exception) { exception.printStackTrace(); }
        return null;
    }

    public String getVersion() {
        return version;
    }
}