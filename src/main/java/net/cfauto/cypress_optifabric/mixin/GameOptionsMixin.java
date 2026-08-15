package net.cfauto.cypress_optifabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.cfauto.cypress_optifabric.impl.IGameOptionsProvider;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.vertex.Tesselator;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.PrintWriter;

@Mixin(GameOptions.class)
public class GameOptionsMixin implements IGameOptionsProvider {
	@Shadow
	public float musicVolume;
	@Shadow
	public float soundVolume;
	@Shadow
	public float mouseSensitivity;
	@Shadow
	public boolean invertMouseY;
	@Shadow
	public int viewDistance = 2;
	@Shadow
	public boolean viewBobbing;
	@Shadow
	public boolean anaglyph;
	@Shadow
	public boolean fpsLimit;
	@Shadow
	public int graphicsMode;
	@Shadow
	public boolean pauseOnUnfocus;
	@Shadow
	public float fovMod;
	@Shadow
	public int difficulty;
	@Shadow
	public boolean shadersOn;
	@Shadow
	public int shadersMotionBlur;
	@Shadow
	public boolean shadersDOF;
	@Shadow
	public boolean shadersSSR;
	@Shadow
	public boolean shadersBloom;
	@Shadow
	public boolean shadersAA;
	@Final
	@Shadow
	private static String[] RENDER_DISTANCE_SETTINGS;
	@Final
	@Shadow
	private static String[] DIFFICULTY_SETTINGS;
	@Final
	@Shadow
	private static String[] VISUALS_SETTINGS;
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
	 * Needs simplification
	 */
	@Overwrite
	public String getAsString(int option) {
		return option == 0 ? "Music: " + (this.musicVolume > 0.0F ? (int)(this.musicVolume * 100.0F) + "%" : "OFF") : (option == 1 ? "Sound: " + (this.soundVolume > 0.0F ? (int)(this.soundVolume * 100.0F) + "%" : "OFF") : (option == 2 ? "Invert mouse: " + (this.invertMouseY ? "ON" : "OFF") : (option == 3 ? (this.mouseSensitivity == 0.0F ? "Sensitivity: *yawn*" : (this.mouseSensitivity == 1.0F ? "Sensitivity: HYPERSPEED!!!" : "Sensitivity: " + (int)(this.mouseSensitivity * 200.0F) + "%")) : (option == 4 ? "Render distance: " + RENDER_DISTANCE_SETTINGS[this.viewDistance] : (option == 5 ? "View bobbing: " + (this.viewBobbing ? "ON" : "OFF") : (option == 6 ? "3d anaglyph: " + (this.anaglyph ? "ON" : "OFF") : (option == 7 ? "Vertical sync: " + (this.fpsLimit ? "ON" : "OFF") : (option == 8 ? "Difficulty: " + DIFFICULTY_SETTINGS[this.difficulty] : (option == 9 ? "Graphics: " + VISUALS_SETTINGS[this.graphicsMode] : (option == 10 ? "Pause on unfocus: " + (this.pauseOnUnfocus ? "ON" : "OFF") : (option == 11 ? "FOV: " + ((double)this.fovMod > 0.5D ? "+" : "") + (int)(((double)this.fovMod - 0.5D) * 80.0D) : (option == 12 ? "Shaders: " + (this.shadersOn ? "ON" : "OFF") : (option == 100 ? "Edit controls..." : (option == 101 ? "Set Visuals..." : (option == 20 ? "Sh. Motion Blur: " + (this.shadersMotionBlur == 2 ? "QUALITY" : (this.shadersMotionBlur == 1 ? "PERF." : "OFF")) : (option == 21 ? "Sh. Depth of Field: " + (this.shadersDOF ? "ON" : "OFF") : (option == 22 ? "Sh. Anti Aliasing: " + (this.shadersAA ? "ON" : "OFF") : (option == 23 ? "Sh. Bloom: " + (this.shadersBloom ? "ON" : "OFF") : (option == 24 ? "Sh. SSR (BROKEN): " + (this.shadersSSR ? "ON" : "OFF") : (option == 1000 ? "Async Populator: " + (this.asyncGen ? "ON" : "OFF") : (option == 1001 ? "Pack Worlds..." : (option == 1002 ? "Unpack Worlds..." : (option == 1003 ? "VBOs: " + (Tesselator.USE_VBO ? "ON" : "OFF") : (option == 1004 ? "Optipine Settings..." : ""))))))))))))))))))))))));
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
