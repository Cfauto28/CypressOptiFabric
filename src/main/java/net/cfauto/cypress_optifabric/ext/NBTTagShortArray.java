package net.cfauto.cypress_optifabric.ext;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import net.minecraft.nbt.NbtElement;

public class NBTTagShortArray extends NbtElement {
	public short[] shortArray;

	public NBTTagShortArray() {
	}

	public NBTTagShortArray(short[] s1) {
		this.shortArray = s1;
	}

	public byte getType() {
		return (byte)11;
	}

	public String toString() {
		return "[" + this.shortArray.length + " shorts]";
	}

	public void write(DataOutput dataOutput) throws IOException {
		int i2 = this.shortArray.length;
		dataOutput.writeInt(i2);
		byte[] b3 = new byte[i2 * 2];
		ByteBuffer byteBuffer4 = ByteBuffer.wrap(b3).order(ByteOrder.BIG_ENDIAN);
		short[] s5 = this.shortArray;
		int i6 = s5.length;

		for(int i7 = 0; i7 < i6; ++i7) {
			short s8 = s5[i7];
			byteBuffer4.putShort(s8);
		}

		dataOutput.write(b3);
	}

	public void read(DataInput dataInput) throws IOException {
		int i2 = dataInput.readInt();
		this.shortArray = new short[i2];
		byte[] b3 = new byte[i2 * 2];
		dataInput.readFully(b3);
		ByteBuffer byteBuffer4 = ByteBuffer.wrap(b3).order(ByteOrder.BIG_ENDIAN);

		for(int i5 = 0; i5 < i2; ++i5) {
			this.shortArray[i5] = byteBuffer4.getShort();
		}

	}

	public static short[] convertByteArrayToShortArray(byte[] byteArray) {
	    if (byteArray == null) {
	        return null;
	    }

	    if (byteArray.length == 0) {
	        return new short[0];
	    }

	    short[] shortArray = new short[byteArray.length];

	    for (int i = 0; i < byteArray.length; i++) {
	        shortArray[i] = (short) (byteArray[i] & 0xFF);
	    }

	    return shortArray;
	}
}
