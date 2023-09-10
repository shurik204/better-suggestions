package me.shurik.bettersuggestions.client.utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static me.shurik.bettersuggestions.client.Client.INSTANCE;

@Environment(EnvType.CLIENT)
public class ClientUtils {
    @Nullable
    public static Entity getEntityByUUID(ClientWorld world, UUID uuid) {
        for (Entity entity : world.getEntities()) {
            if (entity.getUuid().equals(uuid)) {
                return entity;
            }
        }
        return null;
    }

    @Nullable
    public static Entity getEntityByUUID(String uuid) {
        return getEntityByUUID(INSTANCE.world, UUID.fromString(uuid));
    }

    public static boolean entityExists(ClientWorld world, int id) {
        return world.getEntityById(id) != null;
    }

    public static boolean entityExists(int id) {
        return entityExists(INSTANCE.world, id);
    }

    @Nullable
    public static Entity getCrosshairTargetEntity() {
        return INSTANCE.crosshairTarget != null && INSTANCE.crosshairTarget.getType() == HitResult.Type.ENTITY ? ((EntityHitResult) INSTANCE.crosshairTarget).getEntity() : null;
    }
}