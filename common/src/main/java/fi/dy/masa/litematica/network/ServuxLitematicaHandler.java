package fi.dy.masa.litematica.network;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import io.netty.buffer.Unpooled;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import fi.dy.masa.malilib.network.IPluginChannelHandler;
import fi.dy.masa.malilib.network.PacketSplitter;
import fi.dy.masa.litematica.Litematica;
import fi.dy.masa.litematica.data.EntitiesDataStorage;

public abstract class ServuxLitematicaHandler implements IPluginChannelHandler
{
    private final static ServuxLitematicaHandler INSTANCE = new ServuxLitematicaHandler()
    {
    };
    public static ServuxLitematicaHandler getInstance() { return INSTANCE; }

    public static final Identifier CHANNEL_ID = new Identifier("servux", "litematics");

    private boolean servuxRegistered;
    private int failures = 0;
    private static final int MAX_FAILURES = 4;

    @Override
    public List<Identifier> getChannels()
    {
        return Collections.singletonList(CHANNEL_ID);
    }

    @Override
    public void onPacketReceived(PacketByteBuf buf)
    {
        ServuxLitematicaPacket packet = ServuxLitematicaPacket.fromPacket(buf);

        if (packet == null || packet.getType() == null)
        {
            return;
        }

        switch (packet.getType())
        {
            case PACKET_S2C_METADATA ->
            {
                if (EntitiesDataStorage.getInstance().receiveServuxMetadata(packet.getCompound()))
                {
                    this.servuxRegistered = true;
                }
            }
            case PACKET_S2C_BLOCK_NBT_RESPONSE_SIMPLE ->
            {
                if (this.servuxRegistered)
                {
                    EntitiesDataStorage.getInstance().handleBlockEntityData(packet.getPos(), packet.getCompound(), null);
                }
            }
            case PACKET_S2C_ENTITY_NBT_RESPONSE_SIMPLE ->
            {
                if (this.servuxRegistered)
                {
                    EntitiesDataStorage.getInstance().handleEntityData(packet.getEntityId(), packet.getCompound());
                }
            }
            case PACKET_S2C_NBT_RESPONSE_DATA ->
            {
                if (!this.servuxRegistered)
                {
                    return;
                }

                try
                {
                    this.handleBulkData(packet.getBuffer().readVarInt(), (NbtCompound) packet.getBuffer().readNbt(new NbtTagSizeTracker(Long.MAX_VALUE)));
                }
                catch (Exception e)
                {
                    Litematica.LOGGER.error("ServuxLitematicaHandler#onPacketReceived(): Entity Data: error reading fullBuffer [{}]", e.getLocalizedMessage());
                }
            }
            default -> Litematica.LOGGER.warn("ServuxLitematicaHandler#onPacketReceived(): received unhandled packetType {} of size {} bytes.", packet.getPacketType(), packet.getTotalSize());
        }
    }

    private void handleBulkData(final int type, @Nullable NbtCompound nbt)
    {
        if (nbt == null || nbt.isEmpty())
        {
            return;
        }

        String task = nbt.getString("Task");
        Litematica.debugLog("handleBulkData: received task: {}", task);

        EntitiesDataStorage.getInstance().handleBulkEntityData(type, nbt);
    }

    public void reset(Identifier channel)
    {
        if (channel.equals(CHANNEL_ID) && this.servuxRegistered)
        {
            this.servuxRegistered = false;
            this.failures = 0;
        }
    }

    public void resetFailures(Identifier channel)
    {
        if (channel.equals(CHANNEL_ID) && this.failures > 0)
        {
            this.failures = 0;
        }
    }

    public void encodeClientData(ServuxLitematicaPacket packet)
    {
        ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();

        if (networkHandler == null)
        {
            return;
        }

        if (packet.getType().equals(ServuxLitematicaPacket.Type.PACKET_C2S_NBT_RESPONSE_START))
        {
            PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
            buffer.writeVarInt(packet.getTransactionId());
            buffer.writeNbt(packet.getCompound());
            PacketSplitter.send(networkHandler, CHANNEL_ID, buffer);
        }
        else
        {
            PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
            packet.toPacket(buffer);
            PacketSplitter.send(networkHandler, CHANNEL_ID, buffer);
        }
    }

    public void onPacketFailure()
    {
        this.servuxRegistered = false;
        this.failures = 0;
    }
}
