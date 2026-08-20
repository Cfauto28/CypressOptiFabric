package net.cfauto.cypress_optifabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import net.cfauto.cypress_optifabric.impl.IChunkNibbleStorageProvider;
import net.minecraft.world.chunk.ChunkNibbleStorage;

@Mixin(ChunkNibbleStorage.class)
public class ChunkNibbleStorageMixin implements IChunkNibbleStorageProvider {

	@Overwrite
	public int get(int x, int y, int z) {
		int i4 = x << 11 | z << 7 | y;
		int i5 = i4 >> 1;
		int i6 = i4 & 1;
		return i6 == 0 ? this.getData()[i5] & 15 : this.getData()[i5] >> 4 & 15;
	}

	@Overwrite
	public void set(int x, int y, int z, int value) {
		int i5 = x << 11 | z << 7 | y;
		int i6 = i5 >> 1;
		int i7 = i5 & 1;
		if(i7 == 0) {
			this.getData()[i6] = (short)(this.getData()[i6] & 240 | value & 15);
		} else {
			this.getData()[i6] = (short)(this.getData()[i6] & 15 | (value & 15) << 4);
		}

	}

	@Overwrite
	public boolean hasData() {
		return this.getData() != null;
	}
}
