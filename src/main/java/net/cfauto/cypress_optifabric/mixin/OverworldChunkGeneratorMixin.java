package net.cfauto.cypress_optifabric.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import ext.block.ExtBlock;
import ext.newblock.ExtNewBlock;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.OverworldChunkGenerator;
import net.minecraft.world.gen.noise.PerlinNoise;

@Mixin(OverworldChunkGenerator.class)
public class OverworldChunkGeneratorMixin {
	@Shadow
	private Random random;
	@Shadow
	private PerlinNoise perlinNoise2;
	@Shadow
	private PerlinNoise perlinNoise3;
	@Shadow
	public PerlinNoise forestNoise;
	@Shadow
	private World world;
	@Shadow
	private double[] sandBuffer;
	@Shadow
	private double[] gravelBuffer;
	@Shadow
	private double[] depthBuffer;

	/**
	 * @author FMG793
	 * @reason Moved unnecessary low river code to the terrain generator, makes it less resource intensive
	 */
	@Overwrite
	public void buildSurfaces(int chunkX, int chunkZ, byte[] blocks) {
		byte b4 = (byte)ExtBlock.SAND.id;
		byte b5 = (byte)ExtBlock.SAND.id;
		byte b6 = 64;
		double d7 = 8.0D / 256D;
		this.sandBuffer = this.perlinNoise2.getRegion(this.sandBuffer, (double)(chunkX * 16), (double)(chunkZ * 16), 0.0D, 16, 16, 1, d7, d7, 1.0D);
		this.gravelBuffer = this.perlinNoise2.getRegion(this.gravelBuffer, (double)(chunkZ * 16), 109.0134D, (double)(chunkX * 16), 16, 1, 16, d7, 1.0D, d7);
		this.depthBuffer = this.perlinNoise3.getRegion(this.depthBuffer, (double)(chunkX * 16), (double)(chunkZ * 16), 0.0D, 16, 16, 1, d7 * 2.0D, d7 * 2.0D, d7 * 2.0D);

		for(int i9 = 0; i9 < 16; ++i9) {
			for(int i10 = 0; i10 < 16; ++i10) {
				boolean z11 = this.sandBuffer[i9 + i10 * 16] + this.random.nextDouble() * 0.2D > 0.0D;
				boolean z12 = this.gravelBuffer[i9 + i10 * 16] + this.random.nextDouble() * 0.2D > 3.0D;
				int i13 = (int)(this.depthBuffer[i9 + i10 * 16] / 3.0D + 3.0D + this.random.nextDouble() * 0.25D);
				int i14 = -1;
				byte b15;
				byte b16;
				if(this.world.sandCovered) {
					b15 = b4;
					b16 = b5;
				} else {
					b15 = (byte)ExtBlock.GRASS.id;
					b16 = (byte)ExtBlock.DIRT.id;
				}

				for(int i17 = 127; i17 >= 0; --i17) {
					int i18 = (i9 * 16 + i10) * 128 + i17;
					if(i17 >= 95 + this.random.nextInt(6) - 1 && blocks[i18] != 0) {
						blocks[i18] = (byte)ExtBlock.SNOW_BLOCK.id;

						for(int i19 = (int)(this.forestNoise.getValue((double)chunkX * 13.2D, (double)chunkZ * 13.2D) / 2.0D); i19 > 0; --i19) {
							if(i19 + i17 < 128 && i18 + i19 < blocks.length && blocks[i18 + i19] == 0) {
								blocks[i19 + i18] = (byte)ExtBlock.ICE.id;
							}
						}
					}

					if(i17 <= 3) {
						blocks[i18] = (byte)ExtBlock.WATER.id;
					}

					if(i17 <= this.random.nextInt(6) - 1) {
						blocks[i18] = (byte)ExtBlock.ELDERSTONE.id;
					} else {
						byte b20 = blocks[i18];
						if(b20 == 0) {
							i14 = -1;
						} else if(b20 == ExtBlock.STONE.id) {
							if(i14 == -1) {
								if(i13 <= 0) {
									b15 = 0;
									b16 = (byte)ExtBlock.STONE.id;
									if(i17 <= 3) {
										b16 = (byte)ExtNewBlock.LOW_RIVERBED.id;
									}
								} else if(i17 >= b6 - 4 && i17 <= b6 + 1) {
									b15 = (byte)ExtBlock.GRASS.id;
									b16 = (byte)ExtBlock.DIRT.id;
									if(i17 <= 3) {
										b16 = (byte)ExtNewBlock.LOW_WART.id;
									}

									if(this.world.sandCovered) {
										b15 = b4;
										b16 = b5;
									}

									if(z12) {
										b15 = 0;
									}

									if(z12) {
										b16 = (byte)ExtBlock.GRAVEL.id;
									}

									if(z11) {
										b15 = (byte)ExtBlock.SAND.id;
									}

									if(z11) {
										b16 = (byte)ExtBlock.SAND.id;
									}
								}

								if(i17 < b6 && b15 == 0) {
									b15 = (byte)ExtBlock.WATER.id;
								}

								i14 = i13;
								if(i17 >= b6 - 1) {
									blocks[i18] = b15;
								} else {
									blocks[i18] = b16;
								}
							} else if(i14 > 0) {
								--i14;
								blocks[i18] = b16;
							}
						}
					}
				}
			}
		}

	}
}
