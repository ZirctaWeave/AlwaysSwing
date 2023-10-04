package me.zircta.alwaysswing;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovingObjectPosition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    public MovingObjectPosition objectMouseOver;
    @Shadow
    public EntityPlayerSP thePlayer;
    @Shadow
    public int leftClickCounter;

    @Inject(method = "clickMouse", at = @At(value = "TAIL"))
    public void onMouseClick(CallbackInfo ci) {
        if (leftClickCounter > 0) {
            if (objectMouseOver != null && objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && !objectMouseOver.entityHit.hitByEntity(thePlayer) && objectMouseOver.entityHit instanceof EntityLivingBase) {
                if (thePlayer.fallDistance > 0.0F && !thePlayer.onGround && !thePlayer.isOnLadder() && !thePlayer.isInWater() && !thePlayer.isPotionActive(Potion.blindness) && thePlayer.ridingEntity == null) {
                    thePlayer.onCriticalHit(objectMouseOver.entityHit);
                }
                if (EnchantmentHelper.getModifierForCreature(thePlayer.getHeldItem(), ((EntityLivingBase) objectMouseOver.entityHit).getCreatureAttribute()) > 0.0F) {
                    thePlayer.onEnchantmentCritical(objectMouseOver.entityHit);
                }
            }
            alwaysSwing$swingArm();
        }
    }

    @Unique
    private static void alwaysSwing$swingArm() {
        final Minecraft mc = Minecraft.getMinecraft();
        if (!mc.thePlayer.isSwingInProgress ||
                mc.thePlayer.swingProgressInt >= mc.thePlayer.getArmSwingAnimationEnd() / 2 ||
                mc.thePlayer.swingProgressInt < 0) {
            mc.thePlayer.swingProgressInt = -1;
            mc.thePlayer.isSwingInProgress = true;
        }
    }
}
