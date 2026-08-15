package net.cfauto.cypress_optifabric.region;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.cfauto.cypress_optifabric.region.regiondata.RegionFile;
import net.cfauto.cypress_optifabric.region.regiondata.RegionFileCache;

public class RegionTool {
	private static boolean isConsole = false;

	public static void main(String[] string0) {
		System.out.println("ran");
		if(string0.length != 2 && string0.length != 3) {
			System.out.println("exited");
			exitUsage();
		}

		if(System.console() != null) {
			isConsole = true;
		}

		byte b2 = 0;
		if(string0[0].equalsIgnoreCase("unpack")) {
			b2 = 1;
		} else if(string0[0].equalsIgnoreCase("pack")) {
			b2 = 2;
		}

		if(b2 == 0) {
			exitUsage();
		}

		File file1;
		if(!(file1 = new File(string0[1])).exists() || !file1.isDirectory()) {
			exit("error: " + file1.getPath() + " is not a directory");
		}

		File file3 = file1;
		if(string0.length == 3 && !(file3 = new File(string0[2])).isDirectory()) {
			file3.mkdirs();
		}

		if(b2 == 1) {
			unpack(file1, file3);
		} else if(b2 == 2) {
			pack(file1, file3);
		}

	}

	public static void unpack(File file0, File file1) {
		File file2 = new File(file0, "region");
		if(!file2.exists()) {
			exit("error: region directory not found");
		}

		HashSet hashSet3 = null;
		if(file0 != file1) {
			hashSet3 = new HashSet();
		}

		Pattern pattern4 = Pattern.compile("r\\.(-?[0-9]+)\\.(-?[0-9]+).mcr");
		File[] file5 = file2.listFiles();
		int i6 = file5.length;

		for(int i7 = 0; i7 < i6; ++i7) {
			File file8 = file5[i7];
			Matcher matcher9;
			if(file8.isFile() && (matcher9 = pattern4.matcher(file8.getName())).matches()) {
				unpackRegionFile(file1, file8, matcher9);
				if(hashSet3 != null) {
					hashSet3.add(file8);
				}
			}
		}

		if(hashSet3 != null) {
			copyDir(file0, file1, hashSet3);
		}

	}

	public static void pack(File file0, File file1) {
		new File(file0, "region");
		HashSet hashSet3 = null;
		if(file0 != file1) {
			hashSet3 = new HashSet();
		}

		Pattern pattern4 = Pattern.compile("c\\.(-?[0-9a-z]+)\\.(-?[0-9a-z]+).dat");
		Pattern pattern5 = Pattern.compile("[0-9a-z]|1[0-9a-r]");
		int i6 = 0;
		int i7 = 0;
		File[] file8 = file0.listFiles();
		int i9 = file8.length;

		for(int i10 = 0; i10 < i9; ++i10) {
			File file11 = file8[i10];
			if(file11.isDirectory() && pattern5.matcher(file11.getName()).matches()) {
				File[] file12 = file11.listFiles();
				int i13 = file12.length;

				for(int i14 = 0; i14 < i13; ++i14) {
					File file15 = file12[i14];
					if(file15.isDirectory() && pattern5.matcher(file15.getName()).matches()) {
						File[] file16 = file15.listFiles();
						int i17 = file16.length;

						for(int i18 = 0; i18 < i17; ++i18) {
							File file19 = file16[i18];
							Matcher matcher20 = pattern4.matcher(file19.getName());
							if(matcher20.matches()) {
								if(packChunk(file1, file19, matcher20)) {
									++i6;
								} else {
									++i7;
								}

								if(hashSet3 != null) {
									hashSet3.add(file19);
								}
							}

							if(isConsole) {
								System.out.print("\rpacked " + i6 + " chunks" + (i7 > 0 ? ", skipped " + i7 + " older ones" : ""));
							}
						}
					}
				}
			}
		}

		if(isConsole) {
			System.out.print("\r");
		}

		System.out.println("packed " + i6 + " chunks" + (i7 > 0 ? ", skipped " + i7 + " older ones" : ""));
		if(hashSet3 != null) {
			copyDir(file0, file1, hashSet3);
		}

	}

	private static boolean packChunk(File file0, File file1, Matcher matcher2) {
		int i4 = Integer.parseInt(matcher2.group(1), 36);
		int i3;
		RegionFile regionFile5 = RegionFileCache.getRegionFile(file0, i4, i3 = Integer.parseInt(matcher2.group(2), 36), true);
		byte[] b6 = new byte[4096];
		int i7 = 0;

		try {
			DataInputStream dataInputStream8 = new DataInputStream(new GZIPInputStream(new FileInputStream(file1)));

			DataOutputStream dataOutputStream9;
			for(dataOutputStream9 = regionFile5.getChunkDataOutputStream(i4 & 31, i3 & 31); i7 != -1; i7 = dataInputStream8.read(b6)) {
				dataOutputStream9.write(b6, 0, i7);
			}

			dataOutputStream9.close();
			return true;
		} catch (IOException iOException10) {
			iOException10.printStackTrace();
			return false;
		}
	}

	private static void unpackRegionFile(File file0, File file1, Matcher matcher2) {
		long j3 = file1.lastModified();
		RegionFile regionFile5 = new RegionFile(file1);
		String string6 = file1.getName();
		int i7 = Integer.parseInt(matcher2.group(1));
		int i8 = Integer.parseInt(matcher2.group(2));
		int i9 = 0;
		int i10 = 0;

		for(int i11 = 0; i11 < 32; ++i11) {
			for(int i12 = 0; i12 < 32; ++i12) {
				DataInputStream dataInputStream13 = regionFile5.getChunkDataInputStream(i11, i12);
				if(dataInputStream13 != null) {
					int i14 = i11 + (i7 << 5);
					int i15 = i12 + (i8 << 5);
					String string16 = "c." + Integer.toString(i14, 36) + "." + Integer.toString(i15, 36) + ".dat";
					File file17 = new File(file0, Integer.toString(i14 & 63, 36));
					if(!(file17 = new File(file17, Integer.toString(i15 & 63, 36))).exists()) {
						file17.mkdirs();
					}

					file17 = new File(file17, string16);
					byte[] b18 = new byte[4096];
					int i19 = 0;
					if(file17.lastModified() > j3) {
						++i10;
					} else {
						try {
							DataOutputStream dataOutputStream20;
							for(dataOutputStream20 = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(file17))); i19 != -1; i19 = dataInputStream13.read(b18)) {
								dataOutputStream20.write(b18, 0, i19);
							}

							dataOutputStream20.close();
							++i9;
						} catch (IOException iOException21) {
							iOException21.printStackTrace();
						}
					}

					if(isConsole) {
						System.out.print("\r" + string6 + ": unpacked " + i9 + " chunks" + (i10 > 0 ? ", skipped " + i10 + " newer ones" : ""));
					}
				}
			}
		}

		if(isConsole) {
			System.out.print("\r");
		}

		System.out.println(string6 + ": unpacked " + i9 + " chunks" + (i10 > 0 ? ", skipped " + i10 + " newer ones" : ""));
	}

	private static void copyDir(File file0, File file1, Set set2) {
		byte[] b3 = new byte[4096];
		File[] file4 = file0.listFiles();
		int i5 = file4.length;

		for(int i6 = 0; i6 < i5; ++i6) {
			File file7 = file4[i6];
			if(file7.isDirectory()) {
				copyDir(file7, new File(file1, file7.getName()), set2);
			} else if(!set2.contains(file7)) {
				try {
					File file8 = new File(file1, file7.getName());
					file1.mkdirs();
					FileOutputStream fileOutputStream9 = new FileOutputStream(file8);
					FileInputStream fileInputStream10 = new FileInputStream(file7);

					for(int i11 = 0; i11 != -1; i11 = fileInputStream10.read(b3)) {
						fileOutputStream9.write(b3, 0, i11);
					}
				} catch (IOException iOException12) {
					iOException12.printStackTrace();
				}
			}
		}

	}

	private static void exitUsage() {
		exit("regionTool: converts between chunks and regions\nusage: java -jar RegionTool.jar [un]pack <world directory> [target directory]");
	}

	private static void exit(String string0) {
		System.err.println(string0);
		System.exit(1);
	}
}
