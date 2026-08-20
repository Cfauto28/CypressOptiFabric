package net.cfauto.cypress_optifabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.cfauto.cypress_optifabric.impl.IGameOptionsProvider;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.vertex.Tesselator;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.PrintWriter;

@Mixin(GameOptions.class)
public class GameOptionsMixin implements IGameOptionsProvider {
	@Unique
	public boolean asyncGen = false;

	@Inject(method = "set(II)V", at = @At("HEAD"))
	public void initSetOptionValue(int option, int value, CallbackInfo ci) {
		if(option == 1000) {
			this.asyncGen = !this.asyncGen;
		}

		if(option == 1003) {
			Tesselator.USE_VBO = !Tesselator.USE_VBO;
		}
	}

	@Unique
	public boolean getAsyncGen() {
		return asyncGen;
	}

	/**
	 * @author FMG793
	 * @reason OptiPine options
	 */
	@Inject(method = "getAsString(I)Ljava/lang/String;", at = @At("RETURN"))
	public String getAsString(int option, CallbackInfoReturnable<String> cir) {
		return option == 1000 ? "Async Populator: " + (this.asyncGen ? "ON" : "OFF") : (option == 1001 ? "Pack Worlds..." : (option == 1002 ? "Unpack Worlds..." : (option == 1003 ? "VBOs: " + (Tesselator.USE_VBO ? "ON" : "OFF") : (option == 1004 ? "Optipine Settings..." : cir.getReturnValue()))));
	}

	@Inject(method = "save", at = @At(value = "INVOKE", target = "Ljava/io/PrintWriter;println(Ljava/lang/String;)V", shift = At.Shift.AFTER, ordinal = 19), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void saveOptions(CallbackInfo ci, PrintWriter printWriter, int i) {
		printWriter.println("[# OPTIPINE SETTINGS #]");
		printWriter.println("asyncGen:" + this.asyncGen);
		printWriter.println("vbos:" + Tesselator.USE_VBO);
	}

	@Inject(method = "load", at = @At(value = "INVOKE", target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z"))
	private void loadOptions(CallbackInfo ci, @Local(ordinal = 0) String[] strings) {
		if(strings[0].equals("asyncGen")) {
			this.asyncGen = strings[1].equals("true");
		}

		if(strings[0].equals("vbos")) {
			Tesselator.USE_VBO = strings[1].equals("true");
		}
	}

}
