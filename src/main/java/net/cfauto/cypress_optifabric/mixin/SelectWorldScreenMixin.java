package net.cfauto.cypress_optifabric.mixin;

import java.awt.Color;
import java.io.File;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import ext.client.gui.widget.ButtonSelect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.SurvivalInteractionManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.DeleteWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

@Mixin(SelectWorldScreen.class)
public class SelectWorldScreenMixin extends Screen {
	@Shadow
	protected Screen parent;
	@Shadow
	private boolean selected;
	@Shadow
	public int field_1_3053;

	/**
	 * @author FMG793
	 * @reason McRegion Pack Button
	 */
	@Overwrite
	public void method_1_2031() {
		while(this.buttons.size() > 2) {
			this.buttons.remove(2);
		}

		int i1 = 14737632;
		int i2 = 16777120;

		this.buttons.add(new ButtonSelect(-3, this.width / 2 - 160, this.height / 6 + 80, "<"));
		int i11 = this.buttons.size() - 1;
		this.buttons.add(new ButtonSelect(-4, this.width / 2 + 110, this.height / 6 + 80, ">"));
		if(this.field_1_3053 == 0) {
			((ButtonWidget)this.buttons.get(i11)).active = false;
		}

		File file2 = Minecraft.getWorkingDirectory();

		for(int i3 = this.field_1_3053; i3 < this.field_1_3053 + 5; ++i3) {
			NbtCompound nbtCompound4 = World.getWorldData(file2, "World" + (i3 + 1));
			int i5 = i3 - this.field_1_3053;
			if(nbtCompound4 == null) {
				this.buttons.add(new ButtonWidget(i3, this.width / 2 - 100, this.height / 6 + 24 * i5, "- empty [World " + (i3 + 1) + "] -"));
			} else {
				File file6 = new File(file2, "/saves/World" + (i3 + 1) + "/excl_frail");
				boolean z7 = file6.exists();
				String string8 = "World " + (i3 + 1) + (z7 ? "\u00ac" : "");
				long j9 = nbtCompound4.getLong("SizeOnDisk");
				string8 = string8 + " (" + (float)(j9 / 1024L * 100L / 1024L) / 100.0F + " MB)";
				boolean z13 = false;
				if((new File(file2, "/saves/World" + (i5 + 1) + "/region")).exists()) {
					i1 = 16777120;
					i2 = Color.cyan.getRGB();
					z13 = true;
				} else {
					i1 = 14737632;
					i2 = 16777120;
				}
				this.buttons.add((new ButtonSelect(i3, this.width / 2 - 100, this.height / 6 + 24 * i5, 150, 20, string8, true)).method_1_1966(z7 ? 16728128 : i2, z7 ? 16552080 : i1));
				if(z13) {
					this.buttons.add(new ButtonSelect(i5, this.width / 2 + 50, this.height / 6 + 24 * i5, 50, 20, "Unpack", true));
				} else {
					this.buttons.add(new ButtonSelect(i5, this.width / 2 + 50, this.height / 6 + 24 * i5, 50, 20, "Pack", true));
				}
			}
		}

	}

	/**
	 * @author FMG793
	 * @reason McRegion Pack Button
	 */
	@Overwrite
	public void buttonClicked(ButtonWidget button) {
		if(button.active) {
			if(button.id > -1) {
				File file2 = new File(Minecraft.getWorkingDirectory(), "/saves/World" + (button.id + 1));
				if(button.message.equals("Pack")) { //Can't we use ids here?
					this.minecraft.pack(file2);
					this.method_1_2031();
				} else if(button.message.equals("Unpack")) {
					this.minecraft.unpack(file2);
					this.method_1_2031();
				} else {
					this.selectWorld(button.id + 1);
				}
			} else if(button.id == -1) {
				this.minecraft.openScreen(new DeleteWorldScreen(this));
			} else if(button.id == -2) {
				this.minecraft.openScreen(this.parent);
			} else if(button.id == -3) {
				if(this.field_1_3053 != 0) {
					this.field_1_3053 -= 5;
					this.method_1_2031();
				}
			} else if(button.id == -4) {
				this.field_1_3053 += 5;
				this.method_1_2031();
			}
		}

	}

	/**
	 * @author FMG793
	 * @reason McRegion Impl
	 */
	@Overwrite
	public void selectWorld(int id) {
		this.minecraft.openScreen((Screen)null);
		if(!this.selected) {

			this.selected = true;
			this.minecraft.interactionManager = new SurvivalInteractionManager(this.minecraft);
			File file2 = new File(Minecraft.getWorkingDirectory(), "/saves/World" + id + "/region");
			if(file2.exists()) {
				this.minecraft.startRegionWorld("World" + id);
			} else {
				this.minecraft.startGame("World" + id);
			}

			this.minecraft.openScreen((Screen)null);
		}

	}

}
