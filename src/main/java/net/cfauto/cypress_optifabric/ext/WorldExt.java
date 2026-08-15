package net.cfauto.cypress_optifabric.ext;

import java.io.File;
import java.util.Random;

import net.cfauto.cypress_optifabric.impl.IWorldProvider;
import net.minecraft.world.World;

public class WorldExt extends World implements IWorldProvider {
	
	public boolean region;

	public WorldExt(File dir, String saveName, boolean bool) {
		this(dir, saveName, (new Random()).nextLong(), bool);
	}

	public WorldExt(File baseDir, String levelName, long randomSeed, boolean z5) {
		super(baseDir, levelName, randomSeed);
		this.region = z5;
	}
	
	public boolean getRegion() {
		return region;
	}
}
