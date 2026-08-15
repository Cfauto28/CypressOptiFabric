package net.cfauto.cypress_optifabric.region.regiondata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class RegionFile {
	static final int CHUNK_HEADER_SIZE = 5;
	private static final byte[] emptySector = new byte[4096];
	private final File fileName;
	private RandomAccessFile file;
	private final int[] offsets = new int[1024];
	private ArrayList sectorFree;
	private int sizeDelta;
	private long lastModified = 0L;

	public RegionFile(File file1) {
		this.fileName = file1;
		this.debugln("REGION LOAD " + this.fileName);
		this.sizeDelta = 0;

		try {
			if(file1.exists()) {
				this.lastModified = file1.lastModified();
			}

			this.file = new RandomAccessFile(file1, "rw");
			int i3;
			if(this.file.length() < 4096L) {
				for(i3 = 0; i3 < 1024; ++i3) {
					this.file.writeInt(0);
				}

				this.sizeDelta += 4096;
			}

			if((this.file.length() & 4095L) != 0L) {
				for(i3 = 0; (long)i3 < (this.file.length() & 4095L); ++i3) {
					this.file.write(0);
				}
			}

			i3 = (int)this.file.length() / 4096;
			this.sectorFree = new ArrayList(i3);

			int i2;
			for(i2 = 0; i2 < i3; ++i2) {
				this.sectorFree.add(true);
			}

			this.sectorFree.set(0, false);
			this.file.seek(0L);

			for(i2 = 0; i2 < 1024; ++i2) {
				int i4;
				this.offsets[i2] = i4 = this.file.readInt();
				if(i4 != 0 && (i4 >> 8) + (i4 & 255) <= this.sectorFree.size()) {
					for(int i5 = 0; i5 < (i4 & 255); ++i5) {
						this.sectorFree.set((i4 >> 8) + i5, false);
					}
				}
			}
		} catch (IOException iOException6) {
			iOException6.printStackTrace();
		}

	}

	public long lastModified() {
		return this.lastModified;
	}

	public synchronized int getSizeDelta() {
		int i1 = this.sizeDelta;
		this.sizeDelta = 0;
		return i1;
	}

	private void debug(String string1) {
	}

	private void debugln(String string1) {
		this.debug(string1 + "\n");
	}

	private void debug(String string1, int i2, int i3, String string4) {
		this.debug("REGION " + string1 + " " + this.fileName.getName() + "[" + i2 + "," + i3 + "] = " + string4);
	}

	private void debug(String string1, int i2, int i3, int i4, String string5) {
		this.debug("REGION " + string1 + " " + this.fileName.getName() + "[" + i2 + "," + i3 + "] " + i4 + "B = " + string5);
	}

	private void debugln(String string1, int i2, int i3, String string4) {
		this.debug(string1, i2, i3, string4 + "\n");
	}

	public synchronized DataInputStream getChunkDataInputStream(int i1, int i2) {
		if(this.outOfBounds(i1, i2)) {
			this.debugln("READ", i1, i2, "out of bounds");
			return null;
		} else {
			try {
				int i3 = this.getOffset(i1, i2);
				if(i3 == 0) {
					return null;
				} else {
					int i4 = i3 >> 8;
					int i5 = i3 & 255;
					if(i4 + i5 > this.sectorFree.size()) {
						this.debugln("READ", i1, i2, "invalid sector");
						return null;
					} else {
						this.file.seek((long)(i4 * 4096));
						int i6 = this.file.readInt();
						if(i6 > 4096 * i5) {
							this.debugln("READ", i1, i2, "invalid length: " + i6 + " > 4096 * " + i5);
							return null;
						} else {
							byte b7 = this.file.readByte();
							byte[] b8;
							DataInputStream dataInputStream9;
							if(b7 == 1) {
								b8 = new byte[i6 - 1];
								this.file.read(b8);
								dataInputStream9 = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(b8)));
								return dataInputStream9;
							} else if(b7 == 2) {
								b8 = new byte[i6 - 1];
								this.file.read(b8);
								dataInputStream9 = new DataInputStream(new InflaterInputStream(new ByteArrayInputStream(b8)));
								return dataInputStream9;
							} else {
								this.debugln("READ", i1, i2, "unknown version " + b7);
								return null;
							}
						}
					}
				}
			} catch (IOException iOException10) {
				this.debugln("READ", i1, i2, "exception");
				return null;
			}
		}
	}

	public DataOutputStream getChunkDataOutputStream(int i1, int i2) {
		return this.outOfBounds(i1, i2) ? null : new DataOutputStream(new DeflaterOutputStream(new RegionFile.ChunkBuffer(i1, i2)));
	}

	protected synchronized void write(int i1, int i2, byte[] b3, int i4) {
		try {
			int i5 = this.getOffset(i1, i2);
			int i6 = i5 >> 8;
			int i7 = i5 & 255;
			int i8 = (i4 + 5) / 4096 + 1;
			if(i8 >= 256) {
				return;
			}

			if(i6 != 0 && i7 == i8) {
				this.debug("SAVE", i1, i2, i4, "rewrite");
				this.write(i6, b3, i4);
			} else {
				int i10;
				for(i10 = 0; i10 < i7; ++i10) {
					this.sectorFree.set(i6 + i10, true);
				}

				i10 = this.sectorFree.indexOf(true);
				int i11 = 0;
				int i9;
				if(i10 != -1) {
					for(i9 = i10; i9 < this.sectorFree.size(); ++i9) {
						if(i11 != 0) {
							int i10000;
							if(((Boolean)this.sectorFree.get(i9)).booleanValue()) {
								++i11;
								i10000 = i11;
							} else {
								i10000 = 0;
							}

							i11 = i10000;
						} else if(((Boolean)this.sectorFree.get(i9)).booleanValue()) {
							i10 = i9;
							i11 = 1;
						}

						if(i11 >= i8) {
							break;
						}
					}
				}

				if(i11 >= i8) {
					this.debug("SAVE", i1, i2, i4, "reuse");
					i6 = i10;
					this.setOffset(i1, i2, i10 << 8 | i8);

					for(i9 = 0; i9 < i8; ++i9) {
						this.sectorFree.set(i6 + i9, false);
					}

					this.write(i6, b3, i4);
				} else {
					this.debug("SAVE", i1, i2, i4, "grow");
					this.file.seek(this.file.length());
					i6 = this.sectorFree.size();

					for(i9 = 0; i9 < i8; ++i9) {
						this.file.write(emptySector);
						this.sectorFree.add(false);
					}

					this.sizeDelta += 4096 * i8;
					this.write(i6, b3, i4);
					this.setOffset(i1, i2, i6 << 8 | i8);
				}
			}
		} catch (IOException iOException12) {
			iOException12.printStackTrace();
		}

	}

	private void write(int i1, byte[] b2, int i3) throws IOException {
		this.debugln(" " + i1);
		this.file.seek((long)(i1 * 4096));
		this.file.writeInt(i3 + 1);
		this.file.writeByte(2);
		this.file.write(b2, 0, i3);
	}

	private boolean outOfBounds(int i1, int i2) {
		return i1 < 0 || i1 >= 32 || i2 < 0 || i2 >= 32;
	}

	private int getOffset(int i1, int i2) throws IOException {
		return this.offsets[i1 + i2 * 32];
	}

	private void setOffset(int i1, int i2, int i3) throws IOException {
		this.offsets[i1 + i2 * 32] = i3;
		this.file.seek((long)((i1 + i2 * 32) * 4));
		this.file.writeInt(i3);
	}

	public void close() throws IOException {
		this.file.close();
	}

	class ChunkBuffer extends ByteArrayOutputStream {
		private int x;
		private int z;

		public ChunkBuffer(int i2, int i3) {
			super(8096);
			this.x = i2;
			this.z = i3;
		}

		public void close() {
			RegionFile.this.write(this.x, this.z, this.buf, this.count);
		}
	}
}
