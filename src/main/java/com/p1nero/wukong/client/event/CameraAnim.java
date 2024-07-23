package com.p1nero.wukong.client.event;


import com.p1nero.wukong.WukongMoveset;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.ClientEngine;

@Mod.EventBusSubscriber(modid = WukongMoveset.MOD_ID, value = Dist.CLIENT)
public class CameraAnim {

    private static final CameraAnim CAMERA_ANIM = new CameraAnim();
    private static final Vec3f AIMING_CORRECTION = new Vec3f(-1.5F, 0.0F, 1.25F);
    private int zoomMaxCount = 20;

    private boolean aiming;
    private int zoomCount;

    public CameraAnim getInstance() {
        return CAMERA_ANIM;
    }

    private CameraAnim(){
        
    }

    public void zoomIn() {
        this.aiming = true;
        this.zoomCount = this.zoomCount == 0 ? 1 : this.zoomCount;
//        this.zoomOutTimer = 0;
    }

    public void zoomOut(int timer) {
        this.aiming = false;
//        this.zoomOutTimer = timer;
    }



    @SubscribeEvent
    public static void cameraSetupEvent(EntityViewRenderEvent.CameraSetup event) {
        if (CAMERA_ANIM.zoomCount > 0) {
            CAMERA_ANIM.setRangedWeaponThirdPerson(event, Minecraft.getInstance().options.getCameraType(), event.getPartialTicks());

//            if (CAMERA_ANIM.zoomOutTimer > 0) {
//                CAMERA_ANIM.zoomOutTimer--;
//            } else {
//                CAMERA_ANIM.zoomCount = CAMERA_ANIM.aiming ? CAMERA_ANIM.zoomCount + 1 : CAMERA_ANIM.zoomCount - 1;
//            }

            CAMERA_ANIM.zoomCount = Math.min(CAMERA_ANIM.zoomMaxCount, CAMERA_ANIM.zoomCount);
        }

//        CAMERA_ANIM.correctCamera(event, (float)event.getPartialTicks());
    }

    private void setRangedWeaponThirdPerson(EntityViewRenderEvent.CameraSetup event, CameraType pov, double partialTicks) {
        if (ClientEngine.getInstance().getPlayerPatch() == null) {
            return;
        }

        Camera cameraAnim = event.getCamera();
        Entity entity = Minecraft.getInstance().getCameraEntity();
        Vec3 vector = cameraAnim.getPosition();
        double totalX = vector.x();
        double totalY = vector.y();
        double totalZ = vector.z();

        if (pov == CameraType.THIRD_PERSON_BACK) {
            double posX = vector.x();
            double posY = vector.y();
            double posZ = vector.z();
            double entityPosX = entity.xOld + (entity.getX() - entity.xOld) * partialTicks;
            double entityPosY = entity.yOld + (entity.getY() - entity.yOld) * partialTicks + entity.getEyeHeight();
            double entityPosZ = entity.zOld + (entity.getZ() - entity.zOld) * partialTicks;
            float intpol = pov == CameraType.THIRD_PERSON_BACK ? ((float) zoomCount / (float) zoomMaxCount) : 0;
            Vec3f interpolatedCorrection = new Vec3f(AIMING_CORRECTION.x * intpol, AIMING_CORRECTION.y * intpol, AIMING_CORRECTION.z * intpol);
            OpenMatrix4f rotationMatrix = ClientEngine.getInstance().getPlayerPatch().getMatrix((float)partialTicks);
            Vec3f rotateVec = OpenMatrix4f.transform3v(rotationMatrix, interpolatedCorrection, null);
            double d3 = Math.sqrt((rotateVec.x * rotateVec.x) + (rotateVec.y * rotateVec.y) + (rotateVec.z * rotateVec.z));
            double smallest = d3;
            double d00 = posX + rotateVec.x;
            double d11 = posY - rotateVec.y;
            double d22 = posZ + rotateVec.z;

            for (int i = 0; i < 8; ++i) {
                float f = (float) ((i & 1) * 2 - 1);
                float f1 = (float) ((i >> 1 & 1) * 2 - 1);
                float f2 = (float) ((i >> 2 & 1) * 2 - 1);
                f = f * 0.1F;
                f1 = f1 * 0.1F;
                f2 = f2 * 0.1F;
                HitResult raytraceresult = Minecraft.getInstance().level.clip(new ClipContext(new Vec3(entityPosX + f, entityPosY + f1, entityPosZ + f2), new Vec3(d00 + f + f2, d11 + f1, d22 + f2), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));

                if (raytraceresult != null) {
                    double d7 = raytraceresult.getLocation().distanceTo(new Vec3(entityPosX, entityPosY, entityPosZ));
                    if (d7 < smallest) {
                        smallest = d7;
                    }
                }
            }

            float dist = d3 == 0 ? 0 : (float) (smallest / d3);
            totalX += rotateVec.x * dist;
            totalY -= rotateVec.y * dist;
            totalZ += rotateVec.z * dist;
        }

        cameraAnim.setPosition(totalX, totalY, totalZ);
    }

}