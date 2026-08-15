package net.cfauto.cypress_optifabric.mixin;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.cfauto.cypress_optifabric.impl.IOptipineScreenProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.vertex.Tesselator;

@Mixin(Screen.class)
public class ScreenMixin implements IOptipineScreenProvider {
	@Shadow
	public Minecraft minecraft;
	@Shadow
	public int width;
	@Shadow
	public int height;

	@Unique
	public void drawBG(String string1) {
		if(this.minecraft.world != null) {
			GuiElement.fillGradient(0, 0, this.width, this.height, -536870912, -2146697188);
		} else {
			float f2 = (float)(System.currentTimeMillis() - this.minecraft.field_1_592) / 10000.0F;
			this.method_1_1489("/assets/cypressoptifabric/" + string1 + ".png", f2);
		}

	}

	@Unique
	public void method_1_1489(String s, float f1) {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_FOG);
		Tesselator tesselator2 = Tesselator.INSTANCE;
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.minecraft.textureManager.load(s));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f3 = 32.0F;
		tesselator2.method_1_1154(true);
		tesselator2.color(3815994);
		tesselator2.vertex(0.0D, (double)this.height, 0.0D, 0.0D, (double)((float)this.height / f3 + f1));
		tesselator2.vertex((double)this.width, (double)this.height, 0.0D, (double)((float)this.width / f3), (double)((float)this.height / f3 + f1));
		tesselator2.vertex((double)this.width, 0.0D, 0.0D, (double)((float)this.width / f3), (double)(0.0F + f1));
		tesselator2.vertex(0.0D, 0.0D, 0.0D, 0.0D, (double)(0.0F + f1));
		tesselator2.end();
	}
}
