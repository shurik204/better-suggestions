package me.shurik.bettersuggestions.client.render.entity;

import me.shurik.bettersuggestions.client.render.state.MarkerEntityRenderState;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.MarkerEntity;

public class MarkerEntityRenderer extends EntityRenderer<MarkerEntity, MarkerEntityRenderState> {
    protected MarkerEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public MarkerEntityRenderState getRenderState() {
        return MarkerEntityRenderState.INSTANCE;
    }
}
