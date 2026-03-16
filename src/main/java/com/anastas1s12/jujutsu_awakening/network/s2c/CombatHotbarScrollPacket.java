package com.anastas1s12.jujutsu_awakening.network.s2c;

import com.anastas1s12.jujutsu_awakening.JujutsuAwakening;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record CombatHotbarScrollPacket(int direction) implements CustomPacketPayload {
    public static final Type<CombatHotbarScrollPacket> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(JujutsuAwakening.MOD_ID, "combat_hotbar_scroll")
    );

    public static final StreamCodec<FriendlyByteBuf, CombatHotbarScrollPacket> CODEC = StreamCodec.of(
            (buf, packet) -> buf.writeInt(packet.direction),
            buf -> new CombatHotbarScrollPacket(buf.readInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}