package com.bioxx.tfc2.asm.transform;

import net.minecraft.launchwrapper.IClassTransformer;

import com.bioxx.tfc2.ASMConstants;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import squeek.asmhelper.com.bioxx.tfc2.ASMHelper;
import squeek.asmhelper.com.bioxx.tfc2.ObfHelper;

public class ModuleWorldGen implements IClassTransformer 
{
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) 
	{
		ClassNode classNode = ASMHelper.readClassFromBytes(basicClass);

		if (transformedName.equals("com.pam.harvestcraft.blocks.blocks.BlockBaseGarden"))
		{
			String desc = ASMHelper.toMethodDescriptor("Z",ASMConstants.WORLD, ASMConstants.BLOCK_POS);
			MethodNode methodNode = ASMHelper.findMethodNodeOfClass(classNode, "func_176196_c", "canPlaceBlockAt", desc);

			if (methodNode != null)
			{
				addCanPlaceBlockOverride(classNode, methodNode);
			}
			else
				throw new RuntimeException("BlockBaseGarden: canPlaceBlockAt (func_176196_c) method not found");
		}
		else if (transformedName.equals("net.minecraft.block.BlockBush"))
		{
			String desc = ASMHelper.toMethodDescriptor("Z",ASMConstants.IBLOCKSTATE);
			MethodNode methodNode = ASMHelper.findMethodNodeOfClass(classNode, "func_185514_i", "canSustainBush", desc);

			if (methodNode != null)
			{
				addCanSustainPlantOverride(classNode, methodNode);
			}
			else
				throw new RuntimeException("BlockBaseGarden: canSustainBush (func_185514_i) method not found");
		}
		else if (transformedName.equals("com.pam.harvestcraft.worldgen.BushWorldWorldGen"))
		{
			String desc = ASMHelper.toMethodDescriptor("V", ASMConstants.RANDOM,"I", "I", ASMConstants.WORLD, ASMConstants.ICHUNKGENERATOR, ASMConstants.ICHUNKPROVIDER);
			MethodNode methodNode = ASMHelper.findMethodNodeOfClass(classNode, "generate", "generate", desc);

			if (methodNode != null)
			{
				InsnList list = new InsnList();
				methodNode.instructions.clear();
				list.add(new InsnNode(Opcodes.RETURN));
				methodNode.instructions.insert(list);
			}
			else
				throw new RuntimeException("BushWorldWorldGen: generate (generate) method not found");
		}
		else if (transformedName.equals("net.minecraft.world.gen.feature.WorldGenTallGrass") || 
				transformedName.equals("net.minecraft.world.gen.feature.WorldGenDoublePlant"))
		{
			String desc = ASMHelper.toMethodDescriptor("Z",ASMConstants.WORLD,ASMConstants.RANDOM,ASMConstants.BLOCK_POS);
			MethodNode methodNode = ASMHelper.findMethodNodeOfClass(classNode, "func_180709_b", "generate", desc);

			if (methodNode != null)
			{
				InsnList list = new InsnList();
				methodNode.instructions.clear();
				list.add(new InsnNode(Opcodes.ICONST_1));
				list.add(new InsnNode(Opcodes.IRETURN));
				methodNode.instructions.insert(list);
			}
		}

		return ASMHelper.writeClassToBytes(classNode);
	}


	private void nullifyWorldGen(ClassNode classNode, MethodNode method)
	{
		InsnList list = new InsnList();
		method.instructions.clear();
		if(!ObfHelper.isObfuscated())
		{
			list.add(new InsnNode(Opcodes.RETURN));
		}
		else
		{
			list.add(new InsnNode(Opcodes.RETURN));
		}
		method.instructions.insert(list);
	}

	private void addCanPlaceBlockOverride(ClassNode classNode, MethodNode method)
	{
		InsnList list = new InsnList();
		method.instructions.clear();
		if(!ObfHelper.isObfuscated())
		{
			list.add(new VarInsnNode(Opcodes.ALOAD, 1));
			list.add(new VarInsnNode(Opcodes.ALOAD, 2));
			list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/math/BlockPos", "down", ASMHelper.toMethodDescriptor(ASMConstants.BLOCK_POS), false));
			list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/bioxx/tfc2/ServerOverrides","isSoil",ASMHelper.toMethodDescriptor("Z",ASMConstants.WORLD, ASMConstants.BLOCK_POS), false));
			list.add(new InsnNode(Opcodes.IRETURN));
		}
		else
		{
			list.add(new VarInsnNode(Opcodes.ALOAD, 1));
			list.add(new VarInsnNode(Opcodes.ALOAD, 2));
			list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/math/BlockPos", "down", ASMHelper.toMethodDescriptor(ASMConstants.BLOCK_POS), false));
			list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/bioxx/tfc2/ServerOverrides","isSoil",ASMHelper.toMethodDescriptor("Z",ASMConstants.WORLD, ASMConstants.BLOCK_POS), false));
			list.add(new InsnNode(Opcodes.IRETURN));
		}
		method.instructions.insert(list);
	}

	private void addCanSustainPlantOverride(ClassNode classNode, MethodNode method)
	{
		InsnList list = new InsnList();
		method.instructions.clear();
		if(!ObfHelper.isObfuscated())
		{
			list.add(new VarInsnNode(Opcodes.ALOAD, 1));
			list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/bioxx/tfc2/Core", "isSoil", ASMHelper.toMethodDescriptor("Z",ASMConstants.IBLOCKSTATE), false));
			list.add(new InsnNode(Opcodes.IRETURN));
		}
		else
		{
			list.add(new VarInsnNode(Opcodes.ALOAD, 1));
			list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/bioxx/tfc2/Core", "isSoil", ASMHelper.toMethodDescriptor("Z",ASMConstants.IBLOCKSTATE), false));
			list.add(new InsnNode(Opcodes.IRETURN));
		}
		method.instructions.insert(list);
	}
}
