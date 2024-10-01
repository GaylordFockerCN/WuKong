package com.p1nero.wukong.mixin;

import com.mojang.math.Quaternion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;

/**
 * 修复缩放问题
 */
@Mixin(value = OpenMatrix4f.class, remap = false)
public class OpenMatrix4fMixin {
    @Inject(method = "toQuaternion(Lyesman/epicfight/api/utils/math/OpenMatrix4f;)Lcom/mojang/math/Quaternion;",at = @At("RETURN"), cancellable = true)
    private static void removeScale(OpenMatrix4f matrix4f, CallbackInfoReturnable<Quaternion> cir){
        OpenMatrix4f newMatrix = new OpenMatrix4f(matrix4f);
        newMatrix = wukong$removeScale(newMatrix);
        float diagonal = newMatrix.m00 + newMatrix.m11 + newMatrix.m22;
        float w;
        float x;
        float y;
        float z;
        float y4;
        if (diagonal > 0.0F) {
            y4 = (float)(Math.sqrt(diagonal + 1.0F) * 2.0);
            w = y4 / 4.0F;
            x = (newMatrix.m21 - newMatrix.m12) / y4;
            y = (newMatrix.m02 - newMatrix.m20) / y4;
            z = (newMatrix.m10 - newMatrix.m01) / y4;
        } else if (newMatrix.m00 > newMatrix.m11 && newMatrix.m00 > newMatrix.m22) {
            y4 = (float)(Math.sqrt(1.0F + newMatrix.m00 - newMatrix.m11 - newMatrix.m22) * 2.0);
            w = (newMatrix.m21 - newMatrix.m12) / y4;
            x = y4 / 4.0F;
            y = (newMatrix.m01 + newMatrix.m10) / y4;
            z = (newMatrix.m02 + newMatrix.m20) / y4;
        } else if (newMatrix.m11 > newMatrix.m22) {
            y4 = (float)(Math.sqrt(1.0F + newMatrix.m11 - newMatrix.m00 - newMatrix.m22) * 2.0);
            w = (newMatrix.m02 - newMatrix.m20) / y4;
            x = (newMatrix.m01 + newMatrix.m10) / y4;
            y = y4 / 4.0F;
            z = (newMatrix.m12 + newMatrix.m21) / y4;
        } else {
            y4 = (float)(Math.sqrt(1.0F + newMatrix.m22 - newMatrix.m00 - newMatrix.m11) * 2.0);
            w = (newMatrix.m10 - newMatrix.m01) / y4;
            x = (newMatrix.m02 + newMatrix.m20) / y4;
            y = (newMatrix.m12 + newMatrix.m21) / y4;
            z = y4 / 4.0F;
        }
        cir.setReturnValue(new Quaternion(x, y, z, w));
    }

    @Unique
    private static OpenMatrix4f wukong$removeScale(OpenMatrix4f src) {
        float xScale = new Vec3f(src.m00, src.m01, src.m02).length();
        float yScale = new Vec3f(src.m10, src.m11, src.m12).length();
        float zScale = new Vec3f(src.m20, src.m21, src.m22).length();

        OpenMatrix4f copy = new OpenMatrix4f(src);
        copy.scale(1 / xScale, 1 / yScale, 1 / zScale);
        return copy;
    }

}
