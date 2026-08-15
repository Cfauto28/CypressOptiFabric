package net.cfauto.cypress_optifabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ext.client.gui.GuiOptionsUnknownClass1;
import net.cfauto.cypress_optifabric.gui.OptipineScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public class OptionsScreenMixin extends Screen {
	@Shadow
	public GuiOptionsUnknownClass1[] field_1_2986 = new GuiOptionsUnknownClass1[]{new GuiOptionsUnknownClass1("AUDIO", new int[]{0, 1}), new GuiOptionsUnknownClass1("CONTROLS", new int[]{2, 3, 100}), new GuiOptionsUnknownClass1("GAME", new int[]{8, 5, 10, 1004}), new GuiOptionsUnknownClass1("GRAPHICS", new int[]{4, 6, 7, 9, 11, 12, 101}), new GuiOptionsUnknownClass1("ADV.GRAPHICS", new int[]{20, 21, 22, 23, 24})};

	@Inject(method = "buttonClicked", at = @At(value = "TAIL"))
	private void optipineButtonFunctionality(ButtonWidget button, CallbackInfo ci) {
		if(button.id == 1004) {
			this.minecraft.openScreen(new OptipineScreen(this));
		}
	}
}
