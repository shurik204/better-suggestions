package me.shurik.bettersuggestions.mixin;

import net.minecraft.entity.MarkerEntity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.EntityTrackerEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MarkerEntity.class)
public class MarkerEntityMixin {
    @Inject(at = @At("HEAD"), method = "createSpawnPacket", cancellable = true)
    public void createSpawnPacket(EntityTrackerEntry entityTrackerEntry, CallbackInfoReturnable<Packet<ClientPlayPacketListener>> info) {
        info.setReturnValue(new EntitySpawnS2CPacket((MarkerEntity) (Object) this, entityTrackerEntry));
    }
}