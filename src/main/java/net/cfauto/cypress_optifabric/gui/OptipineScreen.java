package net.cfauto.cypress_optifabric.gui;

import java.util.Random;

import ext.newblock.ExtNewBlock;
import net.minecraft.client.gui.screen.TitleScreen__LetterBlock;
import net.minecraft.client.options.GameOptions;
import net.cfauto.cypress_optifabric.ext.TitleScreen__LetterBlockExt;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.gui.widget.ButtonWidget;
import ext.client.gui.widget.ButtonSelect;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import ext.client.InputHandler;
import net.minecraft.client.render.block.BlockRenderer;
import net.minecraft.client.sound.SoundEngine;
import net.minecraft.client.render.Window;
import ext.world.WorldUnknownClass2;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class OptipineScreen extends Screen {
	private static final Random rand = new Random();
	String[] logoBlockLayers = new String[]{" *** *** *** *** *** *** *   * *** ", " * * * *  *   *  * *  *  **  * *   ", " * * ***  *   *  ***  *  * * * *** ", " * * *    *   *  *    *  *  ** *   ", " *** *    *  *** *   *** *   * *** "};
	private TitleScreen__LetterBlock[][] logoEffects;
	private Screen prevScreen;
	private int[] optionList = new int[]{1000, 1001, 1002, 1003};
	private int[] notUseableInWorld = new int[]{1001, 1002};

	public OptipineScreen(Screen screen) {
		this.prevScreen = screen;
	}

	@Override
	public void tick() {
		if(this.logoEffects != null) {
			for(int i1 = 0; i1 < this.logoEffects.length; ++i1) {
				for(int i2 = 0; i2 < this.logoEffects[i1].length; ++i2) {
					this.logoEffects[i1][i2].tick();
				}
			}
		}

	}

	@Override
	public void init() {
		this.buttons.clear();
		GameOptions gameSettings1 = this.minecraft.options;

		for(int i2 = 0; i2 < this.optionList.length; ++i2) {
			int i3 = this.optionList[i2];
			byte b4 = -55;
			this.buttons.add(new OptionButtonWidget(i3, this.width / 2 - 155 + i2 % 2 * 160, i2 + 24 * (i2 >> 1) - b4, gameSettings1.getAsString(i3)));
			if(this.minecraft.world != null) {
				for(int i5 = 0; i5 < this.notUseableInWorld.length; ++i5) {
					if(i3 == this.notUseableInWorld[i5]) {
						this.buttons.get(this.buttons.size() - 1).active = false;
						break;
					}
				}
			}
		}

		this.buttons.add(new ButtonSelect(-100, this.width / 4, this.height - 25, 200, 20, "Back", false));

		if(this.minecraft.world == null) {
			if(SoundEngine.soundSystem != null) {
				InputHandler.minecraft.soundEngine.playMusic("Juhry", true);
			}
		}
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if(button.id == -100) {
			this.minecraft.openScreen(this.prevScreen);
			
			if(this.minecraft.world == null) {
				if(SoundEngine.soundSystem != null) {
					InputHandler.minecraft.soundEngine.playMusic("mainmenu", true);
				}
			}
		}

		if(button.id >= 1000) {
			this.minecraft.options.set(button.id, 1);
			button.message = this.minecraft.options.getAsString(button.id);
		}

		if(button.id == 1001) {
			this.minecraft.packWorlds();
		}

		if(button.id == 1002) {
			this.minecraft.unpackWorlds();
		}

	}

	@Override
	public void render(int mouseX, int mouseY, float renderPartialTick) {
		this.drawBG("optipine-bg");
		GuiElement.fillGradient(0, this.height / 4 - 15, this.width, this.height, -536870912, -2146697188);
		GuiElement.fillGradient(0, 0, this.width, this.height / 4 + 40, 447997183, 11789567);
		this.drawLogo(renderPartialTick);
		super.render(mouseX, mouseY, renderPartialTick);
	}

	private void drawLogo(float f1) {
		int i2;
		if(this.logoEffects == null) {
			this.logoEffects = new TitleScreen__LetterBlock[this.logoBlockLayers[0].length()][this.logoBlockLayers.length];

			for(int i3 = 0; i3 < this.logoEffects.length; ++i3) {
				for(i2 = 0; i2 < this.logoEffects[i3].length; ++i2) {
					this.logoEffects[i3][i2] = new TitleScreen__LetterBlockExt(i3, i2);
				}
			}
		}

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		Window window = new Window(this.minecraft.width, this.minecraft.height);
		i2 = 120 * window.scale;
		GLU.gluPerspective(70.0F, (float)this.minecraft.width / (float)i2, 0.05F, 100.0F);
		GL11.glViewport(0, this.minecraft.height - i2, this.minecraft.width, i2);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glDepthMask(true);

		for(int i4 = 0; i4 < 3; ++i4) {
			GL11.glPushMatrix();
			GL11.glTranslatef(0.4F, 0.6F, -12.0F);
			if(i4 == 0) {
				GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
				GL11.glTranslatef(0.0F, -0.4F, 0.0F);
				GL11.glScalef(0.98F, 1.0F, 1.0F);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			}

			if(i4 == 1) {
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			}

			if(i4 == 2) {
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
			}

			GL11.glScalef(1.0F, -1.0F, 1.0F);
			GL11.glRotatef(15.0F, 1.0F, 0.0F, 0.0F);
			GL11.glScalef(0.79F, 0.9F, 0.3F);
			GL11.glTranslatef((float)(-this.logoBlockLayers[0].length()) * 0.5F, (float)(-this.logoBlockLayers.length) * 1.65F, 0.0F);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.minecraft.textureManager.load(WorldUnknownClass2.method_1_1562(WorldUnknownClass2.field_1_2243)));
			if(i4 == 0) {
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.minecraft.textureManager.load("/title/black.png"));
			}

			BlockRenderer blockRenderer = new BlockRenderer();

			for(int i6 = 0; i6 < this.logoBlockLayers.length; ++i6) {
				for(int i7 = 0; i7 < this.logoBlockLayers[i6].length(); ++i7) {
					char c8 = this.logoBlockLayers[i6].charAt(i7);
					if(c8 != 32) {
						GL11.glPushMatrix();
						TitleScreen__LetterBlock titleScreen__letterBlock = this.logoEffects[i7][i6];
						float f10 = (float)(titleScreen__letterBlock.lastY + (titleScreen__letterBlock.y - titleScreen__letterBlock.lastY) * (double)f1);
						float f11 = 1.0F;
						float f12 = 1.0F;
						float f13 = 0.0F;
						if(i4 == 0) {
							f11 = f10 * 0.04F + 1.0F;
							f12 = 1.0F / f11;
							f10 = 0.0F;
						}

						GL11.glTranslatef((float)i7, (float)i6, f10);
						GL11.glScalef(f11, f11, f11);
						GL11.glRotatef(f13, 0.0F, 1.0F, 0.0F);
						blockRenderer.method_1_2008(ExtNewBlock.SKY_FLAME_IN_GLASS, f12, true);
						GL11.glPopMatrix();
					}
				}
			}

			GL11.glPopMatrix();
		}

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();
		GL11.glViewport(0, 0, this.minecraft.width, this.minecraft.height);
		GL11.glEnable(GL11.GL_CULL_FACE);
	}

	static Random getRandom() {
		return rand;
	}
}
