package net.cfauto.cypress_optifabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.cfauto.cypress_optifabric.ext.NBTTagShortArray;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtEnd;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtShort;
import net.minecraft.nbt.NbtString;

@Mixin(NbtElement.class)
public class NbtElementMixin {

	@Overwrite
	public static NbtElement create(byte type) {
		switch(type) {
		case 0:
			return new NbtEnd();
		case 1:
			return new NbtByte();
		case 2:
			return new NbtShort();
		case 3:
			return new NbtInt();
		case 4:
			return new NbtLong();
		case 5:
			return new NbtFloat();
		case 6:
			return new NbtDouble();
		case 7:
			return new NbtByteArray();
		case 8:
			return new NbtString();
		case 9:
			return new NbtList();
		case 10:
			return new NbtCompound();
		case 11:
			return new NBTTagShortArray();
		default:
			return null;
		}
	}

	@Overwrite
	public static String getName(byte type) {
		switch(type) {
		case 0:
			return "TAG_End";
		case 1:
			return "TAG_Byte";
		case 2:
			return "TAG_Short";
		case 3:
			return "TAG_Int";
		case 4:
			return "TAG_Long";
		case 5:
			return "TAG_Float";
		case 6:
			return "TAG_Double";
		case 7:
			return "TAG_Byte_Array";
		case 8:
			return "TAG_String";
		case 9:
			return "TAG_List";
		case 10:
			return "TAG_Compound";
		case 11:
			return "TAG_Short_Array";
		default:
			return "UNKNOWN";
		}
	}
}
