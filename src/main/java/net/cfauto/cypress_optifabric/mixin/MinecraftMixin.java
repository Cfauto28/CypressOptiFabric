package net.cfauto.cypress_optifabric.mixin;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import ext.util.ExtLogger;
import net.cfauto.cypress_optifabric.ext.WorldExt;
import net.cfauto.cypress_optifabric.impl.IMinecraftProvider;
import net.cfauto.cypress_optifabric.region.RegionTool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.Window;
import net.minecraft.client.render.texture.TextureManager;
import net.minecraft.client.render.vertex.Tesselator;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin implements IMinecraftProvider {
	@Shadow
	public World world;
	@Shadow
	private static File workingDirectory;
	@Shadow
	public int width;
	@Shadow
	public int height;
	@Shadow
	public TextRenderer textRenderer;
	@Shadow
	public TextureManager textureManager;
	@Shadow
	public GameOptions options;
	@Unique
	private String status = "";

	@Unique
	public void packWorlds() {
		this.setLoadStatus("Fetching saves...");
		File file1 = new File(workingDirectory, "saves");
		File[] file2 = file1.listFiles(new FilenameFilter() {
			public boolean accept(File file1, String string2) {
				return string2.startsWith("World");
			}
		});
		int i3 = file2.length;

		for(int i4 = 0; i4 < i3; ++i4) {
			File file5 = new File(workingDirectory, "saves/World" + (i4 + 1));
			if(!file5.exists()) {
				for(int i6 = 0; i6 < Integer.MAX_VALUE; ++i6) {
					file5 = new File(workingDirectory, "saves/World" + (i4 + 1 + i6));
					if(file5.exists()) {
						break;
					}
				}
			}

			this.pack(file5);
		}

	}

	@Unique
	public void pack(File file1) {
		if((new File(file1, "/region")).exists()) {
			this.setLoadStatus("Already converted" + file1.getName() + ", skipping!");
		} else {
			this.setLoadStatus("Converting " + file1.getName() + " to McRegion...");
			System.out.println("Packing World: " + file1.getName());
			RegionTool.pack(file1, file1);
		}

	}

	@Unique
	public void unpackWorlds() {
		this.setLoadStatus("Fetching saves...");
		File file1 = new File(workingDirectory, "saves");
		File[] file2 = file1.listFiles(new FilenameFilter() {
			public boolean accept(File file1, String string2) {
				return string2.startsWith("World");
			}
		});
		int i3 = file2.length;

		for(int i4 = 0; i4 < i3; ++i4) {
			File file5 = new File(workingDirectory, "saves/World" + (i4 + 1));
			if(!file5.exists()) {
				for(int i6 = 0; i6 < Integer.MAX_VALUE; ++i6) {
					file5 = new File(workingDirectory, "saves/World" + (i4 + 1 + i6));
					if(file5.exists()) {
						break;
					}
				}
			}

			this.unpack(file5);
		}

	}

	@Unique
	public void unpack(File file1) {
		File file2 = new File(file1, "/region");
		if(!file2.exists()) {
			this.setLoadStatus(file1.getName() + "isnt McRegion, skipping!");
		} else {
			this.setLoadStatus("Converting " + file1.getName() + " to the Alpha Save Format...");
			System.out.println("Unpacking World: " + file1.getName());
			RegionTool.unpack(file1, file1);
			String[] string3 = file2.list();
			String[] string4 = string3;
			int i5 = string3.length;

			for(int i6 = 0; i6 < i5; ++i6) {
				String string7 = string4[i6];
				File file8 = new File(file2, string7);
				file8.delete();
			}

			file2.delete();
		}

	}

	@Unique
	public void setLoadStatus(String string1) {
		try {
			this.status = string1;
			this.waitScreen();
		} catch (LWJGLException lWJGLException3) {
			lWJGLException3.printStackTrace();
		}

	}

	@Shadow
	public void method_1_546() {} //Why is this even here

	@Unique
	public void waitScreen() throws LWJGLException {
		Window scaledResolution1 = new Window(this.width, this.height);
		int i2 = scaledResolution1.getWidth();
		int i3 = scaledResolution1.getHeight();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, (double)i2, (double)i3, 0.0D, 1000.0D, 3000.0D);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
		GL11.glViewport(0, 0, this.width, this.height);
		GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
		Tesselator tessellator4 = Tesselator.INSTANCE;
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureManager.load("/assets/cypressoptifabric/optipine.png"));
		tessellator4.method_1_1154(true);
		tessellator4.color(0xFFFFFF);
		tessellator4.vertex(0.0D, (double)this.height, 0.0D, 0.0D, 0.0D);
		tessellator4.vertex((double)this.width, (double)this.height, 0.0D, 0.0D, 0.0D);
		tessellator4.vertex((double)this.width, 0.0D, 0.0D, 0.0D, 0.0D);
		tessellator4.vertex(0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
		tessellator4.end();
		short s5 = 256;
		short s6 = 256;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		tessellator4.color(0xFFFFFF);
		this.draw((this.width / 2 - s5) / 2, (this.height / 2 - s6) / 2, 0, 0, s5, s6);
		this.textRenderer.draw(this.status, 10, 10, 0xFFFFFF);
		this.method_1_546();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		Display.setVSyncEnabled(this.options.fpsLimit);
		Display.swapBuffers();
	}

	@Shadow
	public void setWorld(World world) {}

	@Shadow
	public void draw(int integer1, int integer2, int integer3, int integer4, int integer5, int integer6) {}

	@Shadow
	public void method_1_530(World world, String string2) {}

	@Shadow
	public static File getWorkingDirectory() {
		return null;
	}

	@Unique
	public void startRegionWorld(String string1) {
		this.setWorld((World)null);
		System.gc();
		WorldExt world2 = new WorldExt(new File(getWorkingDirectory(), "saves"), string1, true);
		ExtLogger.info("Attempting to load world " + world2.saveDir.toPath() + "...");

		try {
			Files.write(Paths.get("./qfile", new String[0]), string1.getBytes(), new OpenOption[0]);
		} catch (IOException iOException4) {
		}

		if(world2.isNew) {
			this.method_1_530(world2, "Generating level");
		} else {
			this.method_1_530(world2, "Loading level");
		}

	}

	@Redirect(method = "startGame", at = @At(value = "NEW", target = "net/minecraft/world/World"))
	private World extWorldRedirect(File dir, String saveName) {
		return new WorldExt(new File(getWorkingDirectory(), "saves"), saveName, false);
	}

	@Unique
	private synchronized void updateLighting() {
		if(this.world.lightUpdates.size() + this.world.getBlocklightingToUpdate().size() != 0) {
			this.world.doLightUpdatesExt();
		}

	}

	@ModifyExpressionValue(
		method = "prepareWorld(Ljava/lang/String;)V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;doLightUpdates()Z")
	)
	public boolean prepareWorldExt(boolean original) {
		return false;
	}

	@Inject(method = "prepareWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;doLightUpdates()Z"))
	private void mixin1(String saveName, CallbackInfo ci) {
		this.updateLighting();
	}

	@ModifyExpressionValue(
		method = "run()V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;doLightUpdates()Z")
	)
	public boolean runExt(boolean original) {
		return false;
	}

	@Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;doLightUpdates()Z"))
	private void mixin2(CallbackInfo ci) {
		this.updateLighting();
	}
}
