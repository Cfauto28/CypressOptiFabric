package net.cfauto.cypress_optifabric.impl;

import java.io.File;

public interface IMinecraftProvider {
	default void unpackWorlds() {
		throw new AbstractMethodError();
	}

	default void packWorlds() {
		throw new AbstractMethodError();
	}

	default void startRegionWorld(String string1) {
		throw new AbstractMethodError();
	}

	default void pack(File file1) {
		throw new AbstractMethodError();
	}
	default void unpack(File file1) {
		throw new AbstractMethodError();
	}
}
