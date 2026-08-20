package net.cfauto.cypress_optifabric.ext;

import java.util.Random;

import ext.block.ExtBlock;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class CaveWorldCarverExt extends GeneratorExt {
	protected void carveRoom(int chunkX, int chunkZ, short[] blocks, double x, double y, double z) {
		this.carveTunnel(chunkX, chunkZ, blocks, x, y, z, 1.0F + this.random.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5D);
	}

	protected void carveTunnel(int chunkX, int chunkZ, short[] blocks, double x, double y, double z, float baseWidth, float yaw, float pitch, int tunnel, int tunnelCount, double widthHeightRatio) {
		double d17 = (double)(chunkX * 16 + 8);
		double d19 = (double)(chunkZ * 16 + 8);
		float f21 = 0.0F;
		float f22 = 0.0F;
		Random random23 = new Random(this.random.nextLong());
		if(tunnelCount <= 0) {
			int i24 = this.range * 16 - 16;
			tunnelCount = i24 - random23.nextInt(i24 / 4);
		}

		boolean z60 = false;
		if(tunnel == -1) {
			tunnel = tunnelCount / 2;
			z60 = true;
		}

		int i25 = random23.nextInt(tunnelCount / 2) + tunnelCount / 4;

		for(boolean z26 = random23.nextInt(6) == 0; tunnel < tunnelCount; ++tunnel) {
			double d27 = 1.5D + (double)(MathHelper.sin((float)tunnel * (float)Math.PI / (float)tunnelCount) * baseWidth * 1.0F);
			double d29 = d27 * widthHeightRatio;
			float f31 = MathHelper.cos(pitch);
			float f32 = MathHelper.sin(pitch);
			x += (double)(MathHelper.cos(yaw) * f31);
			y += (double)f32;
			z += (double)(MathHelper.sin(yaw) * f31);
			if(z26) {
				pitch *= 0.92F;
			} else {
				pitch *= 0.7F;
			}

			pitch += f22 * 0.1F;
			yaw += f21 * 0.1F;
			f22 *= 0.9F;
			f21 *= 0.75F;
			f22 += (random23.nextFloat() - random23.nextFloat()) * random23.nextFloat() * 2.0F;
			f21 += (random23.nextFloat() - random23.nextFloat()) * random23.nextFloat() * 4.0F;
			if(!z60 && tunnel == i25 && baseWidth > 1.0F) {
				this.carveTunnel(chunkX, chunkZ, blocks, x, y, z, random23.nextFloat() * 0.5F + 0.5F, yaw - (float)Math.PI / 2F, pitch / 3.0F, tunnel, tunnelCount, 1.0D);
				this.carveTunnel(chunkX, chunkZ, blocks, x, y, z, random23.nextFloat() * 0.5F + 0.5F, yaw + (float)Math.PI / 2F, pitch / 3.0F, tunnel, tunnelCount, 1.0D);
				return;
			}

			if(z60 || random23.nextInt(4) != 0) {
				double d33 = x - d17;
				double d35 = z - d19;
				double d37 = (double)(tunnelCount - tunnel);
				double d39 = (double)(baseWidth + 2.0F + 16.0F);
				if(d33 * d33 + d35 * d35 - d37 * d37 > d39 * d39) {
					return;
				}

				if(x >= d17 - 16.0D - d27 * 2.0D && z >= d19 - 16.0D - d27 * 2.0D && x <= d17 + 16.0D + d27 * 2.0D && z <= d19 + 16.0D + d27 * 2.0D) {
					int i41 = MathHelper.floor(x - d27) - chunkX * 16 - 1;
					int i42 = MathHelper.floor(x + d27) - chunkX * 16 + 1;
					int i43 = MathHelper.floor(y - d29) - 1;
					int i44 = MathHelper.floor(y + d29) + 1;
					int i45 = MathHelper.floor(z - d27) - chunkZ * 16 - 1;
					int i46 = MathHelper.floor(z + d27) - chunkZ * 16 + 1;
					if(i41 < 0) {
						i41 = 0;
					}

					if(i42 > 16) {
						i42 = 16;
					}

					if(i43 < 1) {
						i43 = 1;
					}

					if(i44 > 120) {
						i44 = 120;
					}

					if(i45 < 0) {
						i45 = 0;
					}

					if(i46 > 16) {
						i46 = 16;
					}

					boolean z47 = false;

					int i48;
					int i49;
					for(i48 = i41; !z47 && i48 < i42; ++i48) {
						for(int i50 = i45; !z47 && i50 < i46; ++i50) {
							for(int i51 = i44 + 1; !z47 && i51 >= i43 - 1; --i51) {
								i49 = (i48 * 16 + i50) * 128 + i51;
								if(i51 >= 0 && i51 < 128) {
									if(blocks[i49] == ExtBlock.FLOWING_WATER.id || blocks[i49] == ExtBlock.WATER.id) {
										z47 = true;
									}

									if(i51 != i43 - 1 && i48 != i41 && i48 != i42 - 1 && i50 != i45 && i50 != i46 - 1) {
										i51 = i43;
									}
								}
							}
						}
					}

					if(!z47) {
						for(i48 = i41; i48 < i42; ++i48) {
							double d61 = ((double)(i48 + chunkX * 16) + 0.5D - x) / d27;

							for(i49 = i45; i49 < i46; ++i49) {
								double d52 = ((double)(i49 + chunkZ * 16) + 0.5D - z) / d27;
								int i54 = (i48 * 16 + i49) * 128 + i44;
								boolean z55 = false;

								for(int i56 = i44 - 1; i56 >= i43; --i56) {
									double d57 = ((double)i56 + 0.5D - y) / d29;
									if(d57 > -0.7D && d61 * d61 + d57 * d57 + d52 * d52 < 1.0D) {
										short b59 = blocks[i54];
										if(b59 == ExtBlock.GRASS.id) {
											z55 = true;
										}

										if(b59 == ExtBlock.STONE.id || b59 == ExtBlock.DIRT.id || b59 == ExtBlock.GRASS.id) {
											if(i56 < 10) {
												blocks[i54] = (short)ExtBlock.FLOWING_LAVA.id;
											} else {
												blocks[i54] = 0;
												if(z55 && blocks[i54 - 1] == ExtBlock.DIRT.id) {
													blocks[i54 - 1] = (short)ExtBlock.GRASS.id;
												}
											}
										}
									}

									--i54;
								}
							}
						}

						if(z60) {
							break;
						}
					}
				}
			}
		}

	}

	protected void place(World world, int startChunkX, int startChunkZ, int chunkX, int chunkZ, short[] blocks) {
		int i7 = this.random.nextInt(this.random.nextInt(this.random.nextInt(40) + 1) + 1);
		if(this.random.nextInt(15) != 0) {
			i7 = 0;
		}

		for(int i8 = 0; i8 < i7; ++i8) {
			double d9 = (double)(startChunkX * 16 + this.random.nextInt(16));
			double d11 = (double)this.random.nextInt(this.random.nextInt(120) + 8);
			double d13 = (double)(startChunkZ * 16 + this.random.nextInt(16));
			int i15 = 1;
			if(this.random.nextInt(4) == 0) {
				this.carveRoom(chunkX, chunkZ, blocks, d9, d11, d13);
				i15 += this.random.nextInt(4);
			}

			for(int i16 = 0; i16 < i15; ++i16) {
				float f17 = this.random.nextFloat() * (float)Math.PI * 2.0F;
				float f18 = (this.random.nextFloat() - 0.5F) * 2.0F / 8.0F;
				float f19 = this.random.nextFloat() * 2.0F + this.random.nextFloat();
				this.carveTunnel(chunkX, chunkZ, blocks, d9, d11, d13, f19, f17, f18, 0, 0, 1.0D);
			}
		}

	}
}
