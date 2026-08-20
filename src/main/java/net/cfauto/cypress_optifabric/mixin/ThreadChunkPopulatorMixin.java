package net.cfauto.cypress_optifabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import ext.client.InputHandler;
import ext.world.gen.ThreadChunkPopulator;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ThreadChunkPopulator.class, priority = 990)
public class ThreadChunkPopulatorMixin {
	@Shadow
	public static boolean conf_asyncGen;

	@ModifyExpressionValue(method = "AddPopulateOperation", at = @At(value = "FIELD", target = "Lext/world/gen/ThreadChunkPopulator;conf_asyncGen:Z", opcode = Opcodes.GETSTATIC))
	private static boolean mixin(boolean original) {
		return conf_asyncGen || InputHandler.minecraft.options.getAsyncGen();
	}

}
