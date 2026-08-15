package net.cfauto.cypress_optifabric.mixin;


import org.spongepowered.asm.mixin.Mixin;

import ext.client.gui.widget.ButtonSelect;
import net.cfauto.cypress_optifabric.gui.OptipineScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
	@Inject(method = "init", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 6, shift = At.Shift.AFTER))
	private void addOptipineButton(CallbackInfo ci) {
		this.buttons.add(new ButtonSelect(9, this.width / 8 + 104, this.height / 4 + 96 + 12, 80, 20, "Optipine", true));
	}

	@Inject(method = "buttonClicked", at = @At(value = "TAIL"))
	private void optipineButtonFunction(ButtonWidget button, CallbackInfo ci) {
		if(button.id == 9) {
			this.minecraft.openScreen(new OptipineScreen(this));
		}
	}
}
