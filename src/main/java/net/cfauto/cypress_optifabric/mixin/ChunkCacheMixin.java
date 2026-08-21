/*package net.cfauto.cypress_optifabric.mixin;

import net.cfauto.cypress_optifabric.region.EmptyChunk;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkCache;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkCache.class)
public class ChunkCacheMixin {
	@Redirect(method = "<init>", at = @At(value = "NEW", target = "net/minecraft/world/chunk/WorldChunk"))
	private WorldChunk mixin(World world, byte[] blocks, int chunkX, int chunkZ) {
		return new EmptyChunk(world, blocks, chunkX, chunkZ);
	}
}
*/