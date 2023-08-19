package me.shurik.bettersuggestions.client.utils;

import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import static me.shurik.bettersuggestions.client.BetterSuggestionsModClient.CLIENT;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.EnvType;

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

    public static Entity getEntityByUUID(String uuid) {
        return getEntityByUUID(CLIENT.world, UUID.fromString(uuid));
    }

    public static boolean entityExists(ClientWorld world, int id) {
        return world.getEntityById(id) != null;
    }

    public static boolean entityExists(int id) {
        return entityExists(CLIENT.world, id);
    }

    @Nullable
    public static Entity getCrosshairTargetEntity() {
        return CLIENT.crosshairTarget != null && CLIENT.crosshairTarget.getType() == HitResult.Type.ENTITY ? ((EntityHitResult) CLIENT.crosshairTarget).getEntity() : null;
    }
}