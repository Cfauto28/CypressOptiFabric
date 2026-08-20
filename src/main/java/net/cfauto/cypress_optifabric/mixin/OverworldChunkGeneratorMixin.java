package net.cfauto.cypress_optifabric.mixin;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import ext.block.ExtBlock;
import ext.client.gui.screen.cheat.ScreenDebugMenu;
import ext.newblock.ExtNewBlock;
import ext.world.freerun.FreerunWorld;
import net.cfauto.cypress_optifabric.ext.CaveWorldCarverExt;
import net.cfauto.cypress_optifabric.ext.GeneratorExt;
import net.cfauto.cypress_optifabric.ext.WorldChunkExt;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.OverworldChunkGenerator;
import net.minecraft.world.gen.noise.PerlinNoise;

@Mixin(OverworldChunkGenerator.class)
public abstract class OverworldChunkGeneratorMixin {
	@Shadow
	private Random random;

	@Shadow
	private World world;

	@Shadow
	private double[] heightMap;

	@Shadow
	private double[] sandBuffer;

	@Shadow
	private double[] gravelBuffer;

	@Shadow
	private double[] depthBuffer;

	@Shadow
	private PerlinNoise perlinNoise2;

	@Shadow
	public PerlinNoise forestNoise;

	@Shadow
	private PerlinNoise perlinNoise3;

	@Shadow
	private int field_1_2828;

	@Shadow
	private long field_1_2832;

	@Unique
	private GeneratorExt cave = new CaveWorldCarverExt();

	@Shadow
	abstract double[] generateHeightMap(double[] heightMap, int x, int y, int z, int sizeX, int sizeY, int sizeZ);

	@Overwrite
	public WorldChunk getChunk(int chunkX, int chunkZ) {
		if(Math.abs(System.currentTimeMillis() - this.field_1_2832) > 5000L) {
			SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("HH");
			this.field_1_2828 = Integer.parseInt(simpleDateFormat3.format(Calendar.getInstance().getTime()));
			this.field_1_2832 = System.currentTimeMillis();
		}

		boolean z6 = ScreenDebugMenu.dbg_conf_forceFracturedWorld || this.field_1_2828 > 22 || this.field_1_2828 < 5;
		this.random.setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L);
		short[] b4 = new short[32768];
		WorldChunkExt worldChunk5 = new WorldChunkExt(this.world, b4, chunkX, chunkZ);
		chunkX += z6 ? this.random.nextInt(2000) - this.random.nextInt(1000) : 0;
		chunkZ += z6 ? this.random.nextInt(2000) - this.random.nextInt(1000) : 0;
		if(!ScreenDebugMenu.dbg_conf_disableWorldgen && !(this.world instanceof FreerunWorld)) {
			this.buildTerrain(chunkX, chunkZ, b4);
			this.buildSurfaces(chunkX, chunkZ, b4);
			this.cave.place((OverworldChunkGenerator)(Object)this, this.world, chunkX, chunkZ, b4);
			worldChunk5.populateHeightMap();
		}

		return worldChunk5;
	}

	@Unique
	public void buildTerrain(int chunkX, int chunkZ, short[] blocks) {
		byte b4 = 4;
		byte b5 = 64;
		int i6 = b4 + 1;
		byte b7 = 17;
		int i8 = b4 + 1;
		this.heightMap = this.generateHeightMap(this.heightMap, chunkX * b4, 0, chunkZ * b4, i6, b7, i8);

		for(int i9 = 0; i9 < b4; ++i9) {
			for(int i10 = 0; i10 < b4; ++i10) {
				for(int i11 = 0; i11 < 16; ++i11) {
					double d12 = 0.125D;
					double d14 = this.heightMap[((i9 + 0) * i8 + i10 + 0) * b7 + i11 + 0];
					double d16 = this.heightMap[((i9 + 0) * i8 + i10 + 1) * b7 + i11 + 0];
					double d18 = this.heightMap[((i9 + 1) * i8 + i10 + 0) * b7 + i11 + 0];
					double d20 = this.heightMap[((i9 + 1) * i8 + i10 + 1) * b7 + i11 + 0];
					double d22 = (this.heightMap[((i9 + 0) * i8 + i10 + 0) * b7 + i11 + 1] - d14) * d12;
					double d24 = (this.heightMap[((i9 + 0) * i8 + i10 + 1) * b7 + i11 + 1] - d16) * d12;
					double d26 = (this.heightMap[((i9 + 1) * i8 + i10 + 0) * b7 + i11 + 1] - d18) * d12;
					double d28 = (this.heightMap[((i9 + 1) * i8 + i10 + 1) * b7 + i11 + 1] - d20) * d12;

					for(int i30 = 0; i30 < 8; ++i30) {
						double d31 = 0.25D;
						double d33 = d14;
						double d35 = d16;
						double d37 = (d18 - d14) * d31;
						double d39 = (d20 - d16) * d31;

						for(int i41 = 0; i41 < 4; ++i41) {
							int i42 = i41 + i9 * 4 << 11 | 0 + i10 * 4 << 7 | i11 * 8 + i30;
							short s43 = 128;
							double d44 = 0.25D;
							double d46 = d33;
							double d48 = (d35 - d33) * d44;

							for(int i50 = 0; i50 < 4; ++i50) {
								int i51 = 0;
								if(i11 * 8 + i30 < b5) {
									if(this.world.snowCovered && i11 * 8 + i30 >= b5 - 1) {
										i51 = ExtBlock.ICE.id;
									} else {
										i51 = ExtBlock.WATER.id;
									}

									if(this.world.snowCovered && i11 * 8 + i30 >= b5 - 1) {
										i51 = ExtBlock.SNOW_LAYER.id;
									} else {
										i51 = ExtBlock.SAND.id;
									}

									if(this.world.sandCovered && i11 * 8 + i30 >= b5 - 1) {
										i51 = ExtBlock.SAND.id;
									} else {
										i51 = ExtBlock.WATER.id;
									}
								}

								if(d46 > 0.0D) {
									i51 = ExtBlock.STONE.id;
								}

								blocks[i42] = (short)i51;
								i42 += s43;
								d46 += d48;
							}

							d33 += d37;
							d35 += d39;
						}

						d14 += d22;
						d16 += d24;
						d18 += d26;
						d20 += d28;
					}
				}
			}
		}

	}
	
	/**
	 * @author FMG793
	 * @reason Moved unnecessary low river code to the terrain generator, makes it less resource intensive
	 */
	@Unique
	public void buildSurfaces(int chunkX, int chunkZ, short[] blocks) {
		short b4 = (short)ExtBlock.SAND.id;
		short b5 = (short)ExtBlock.SAND.id;
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
				short b15;
				short b16;
				if(this.world.sandCovered) {
					b15 = b4;
					b16 = b5;
				} else {
					b15 = (short)ExtBlock.GRASS.id;
					b16 = (short)ExtBlock.DIRT.id;
				}

				for(int i17 = 127; i17 >= 0; --i17) {
					int i18 = (i9 * 16 + i10) * 128 + i17;
					if(i17 >= 95 + this.random.nextInt(6) - 1 && blocks[i18] != 0) {
						blocks[i18] = (short)ExtBlock.SNOW_BLOCK.id;

						for(int i19 = (int)(this.forestNoise.getValue((double)chunkX * 13.2D, (double)chunkZ * 13.2D) / 2.0D); i19 > 0; --i19) {
							if(i19 + i17 < 128 && i18 + i19 < blocks.length && blocks[i18 + i19] == 0) {
								blocks[i19 + i18] = (short)ExtBlock.ICE.id;
							}
						}
					}

					if(i17 <= 3) {
						blocks[i18] = (short)ExtBlock.WATER.id;
					}

					if(i17 <= this.random.nextInt(6) - 1) {
						blocks[i18] = (short)ExtBlock.ELDERSTONE.id;
					} else {
						short b20 = blocks[i18];
						if(b20 == 0) {
							i14 = -1;
						} else if(b20 == ExtBlock.STONE.id) {
							if(i14 == -1) {
								if(i13 <= 0) {
									b15 = 0;
									b16 = (short)ExtBlock.STONE.id;
									if(i17 <= 3) {
										b16 = (short)ExtNewBlock.LOW_RIVERBED.id;
									}
								} else if(i17 >= b6 - 4 && i17 <= b6 + 1) {
									b15 = (short)ExtBlock.GRASS.id;
									b16 = (short)ExtBlock.DIRT.id;
									if(i17 <= 3) {
										b16 = (short)ExtNewBlock.LOW_WART.id;
									}

									if(this.world.sandCovered) {
										b15 = b4;
										b16 = b5;
									}

									if(z12) {
										b15 = 0;
									}

									if(z12) {
										b16 = (short)ExtBlock.GRAVEL.id;
									}

									if(z11) {
										b15 = (short)ExtBlock.SAND.id;
									}

									if(z11) {
										b16 = (short)ExtBlock.SAND.id;
									}
								}

								if(i17 < b6 && b15 == 0) {
									b15 = (short)ExtBlock.WATER.id;
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
