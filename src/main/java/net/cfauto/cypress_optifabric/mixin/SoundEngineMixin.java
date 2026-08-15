package net.cfauto.cypress_optifabric.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.sound.SoundEngine;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {
	@Redirect(method = "method_1_1050()V",at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
	public int method_1_1050Ext(Random random, int value) {
		return random.nextInt(12000);
	}
	
	@Redirect(method = "tickMusic()V",at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
	public int tickMusicExt(Random random, int value) {
		return random.nextInt(12000);
	}
	
	@Redirect(method = "playMusic(Ljava/lang/String;Z)V",at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
	public int playMusicExt(Random random, int value) {
		return random.nextInt(12000);
	}
}
