package net.cfauto.cypress_optifabric.region.regiondata;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RegionFileCache {
	private static final Map cache = new HashMap();

	public static synchronized RegionFile getRegionFile(File file0, int i1, int i2, boolean z3) {
		File file4 = new File(file0, "region");
		File file5 = new File(file4, "r." + (i1 >> 5) + "." + (i2 >> 5) + ".mcr");
		Reference reference6 = (Reference)cache.get(file5);
		if(reference6 != null && reference6.get() != null && !z3) {
			return (RegionFile)reference6.get();
		} else {
			if(!file4.exists()) {
				file4.mkdirs();
			}

			RegionFile regionFile7 = new RegionFile(file5);
			cache.put(file5, new SoftReference(regionFile7));
			return regionFile7;
		}
	}

	public static synchronized void clear() {
		Iterator iterator0 = cache.values().iterator();

		while(iterator0.hasNext()) {
			Reference reference1 = (Reference)iterator0.next();

			try {
				if(reference1.get() != null) {
					((RegionFile)reference1.get()).close();
				}
			} catch (IOException iOException3) {
				iOException3.printStackTrace();
			}
		}

		cache.clear();
	}

	public static int getSizeDelta(File file0, int i1, int i2) {
		RegionFile regionFile3 = getRegionFile(file0, i1, i2, false);
		return regionFile3.getSizeDelta();
	}

	public static DataInputStream getChunkDataInputStream(File file0, int i1, int i2) {
		RegionFile regionFile3 = getRegionFile(file0, i1, i2, false);
		return regionFile3.getChunkDataInputStream(i1 & 31, i2 & 31);
	}

	public static DataOutputStream getChunkDataOutputStream(File file0, int i1, int i2) {
		RegionFile regionFile3 = getRegionFile(file0, i1, i2, false);
		return regionFile3.getChunkDataOutputStream(i1 & 31, i2 & 31);
	}
}
