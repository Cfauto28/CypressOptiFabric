package net.cfauto.cypress_optifabric.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import ext.block.ExtBlock;
import ext.newblock.ExtNewBlock;
import ext.world.gen.GenLowRiver;
import net.minecraft.world.World;

@Mixin(GenLowRiver.class)
public class GenLowRiverMixin {
	@Shadow
	int chunkCoordX;
	@Shadow
	int chnkCoordZ;
	@Shadow
	Random rng;

	/**
	 * @author FMG793
	 * @reason Moved unnecessary low river code to the terrain generator, makes it less resource intensive
	 */
	@Overwrite
	public void DigSphere(World world, int i2, int i3, int i4, int i5) {
		if(i5 != 0) {
			for(int i6 = -i5; i6 <= i5; ++i6) {
				for(int i7 = -i5; i7 <= i5; ++i7) {
					for(int i8 = -i5; i8 <= i5; ++i8) {
						if(i6 * i6 + i7 * i7 + i8 * i8 <= i5 * i5 && (i2 + i6) / 16 - (this.chunkCoordX < 0 ? 1 : 0) == this.chunkCoordX && (i4 + i8) / 16 - (this.chnkCoordZ < 0 ? 1 : 0) == this.chnkCoordZ && i3 + i7 >= 1 && world.getBlock(i2 + i6, i3 + i7, i4 + i8) != ExtNewBlock.LOW_LILY.id) {
							boolean z9 = world.getBlock(i2 + i6, i3 + i7, i4 + i8) != ExtBlock.ELDERSTONE.id;
							if(i3 + i7 > 3 || z9 || !z9 && this.rng.nextInt(4) < 3) {
								if(i3 + i7 > 3) {
									world.setBlockQuietly(i2 + i6, i3 + i7, i4 + i8, 0);
								}

								if(i3 + i7 == 3 && this.rng.nextInt(80) == 0) {
									world.setBlockQuietly(i2 + i6, i3 + i7 + 1, i4 + i8, ExtNewBlock.LOW_LILY.id);
								}
							}
						}
					}
				}
			}
		}

	}
}
