package net.cfauto.cypress_optifabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ext.world.gen.ThreadChunkPopulator;
import net.minecraft.client.gui.screen.GameMenuScreen;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin {
	@Inject(method = "buttonClicked(Lnet/minecraft/client/gui/widget/ButtonWidget;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setWorld(Lnet/minecraft/world/World;)V"))
	public void buttonClickedExt(CallbackInfo ci) {
		new ThreadChunkPopulator().field_1_3270.clear();
	}
}
