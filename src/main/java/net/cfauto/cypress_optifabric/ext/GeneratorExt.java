package net.cfauto.cypress_optifabric.ext;

import net.minecraft.world.World;
import net.minecraft.world.gen.Generator;
import net.minecraft.world.gen.chunk.OverworldChunkGenerator;

public class GeneratorExt extends Generator {

	public void place(OverworldChunkGenerator generator, World world, int chunkX, int chunkZ, short[] blocks) {
		int i6 = this.range;
		this.random.setSeed(world.seed);
		long j7 = this.random.nextLong() / 2L * 2L + 1L;
		long j9 = this.random.nextLong() / 2L * 2L + 1L;

		for(int i11 = chunkX - i6; i11 <= chunkX + i6; ++i11) {
			for(int i12 = chunkZ - i6; i12 <= chunkZ + i6; ++i12) {
				this.random.setSeed((long)i11 * j7 + (long)i12 * j9 ^ world.seed);
				this.place(world, i11, i12, chunkX, chunkZ, blocks);
			}
		}

	}

	protected void place(World world, int startChunkX, int startChunkZ, int chunkX, int chunkZ, short[] blocks) {
	}
}
