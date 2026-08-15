package net.cfauto.cypress_optifabric.ext;

import java.util.Random;

import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.TitleScreen__LetterBlock;

public class TitleScreen__LetterBlockExt extends TitleScreen__LetterBlock {

	public TitleScreen__LetterBlockExt(int y, int x) {
		super(new TitleScreen(), y, x);
		this.y = this.lastY = (double)(10 + x) + new Random().nextDouble() * 32.0D + (double)y;
	}
}
