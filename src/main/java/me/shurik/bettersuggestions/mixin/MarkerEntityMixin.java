package me.shurik.bettersuggestions.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.MarkerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;

@Mixin(MarkerEntity.class)
public class MarkerEntityMixin {
    @Inject(at = @At("HEAD"), method = "createSpawnPacket", cancellable = true)
    public void createSpawnPacket(CallbackInfoReturnable<Packet<ClientPlayPacketListener>> info) {
        info.setReturnValue(new EntitySpawnS2CPacket((MarkerEntity) (Object) this));
    }
}