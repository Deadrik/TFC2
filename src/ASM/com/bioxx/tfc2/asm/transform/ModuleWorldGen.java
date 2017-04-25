package com.bioxx.tfc2.asm.transform;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.bioxx.tfc2.ServerOverrides;
import com.bioxx.tfc2.asm.ASMConstants;
import com.bioxx.tfc2.asm.ASMHelper;
import com.bioxx.tfc2.asm.ObfHelper;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ModuleWorldGen implements IClassTransformer 
{
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) 
	{
		if(basicClass == null)
			return null;

		ClassNode classNode = ASMHelper.readClassFromBytes(basicClass);


		if (transformedName.equals("net.minecraft.block.BlockBush"))
		{
			String desc = ASMHelper.toMethodDescriptor("Z",ObfHelper.getInternalClassName(ASMConstants.IBLOCKSTATE));
			MethodNode methodNode = ASMHelper.findMethodNodeOfClass(classNode, "i", "canSustainBush", desc);

			if (methodNode != null)
			{
				addCanSustainPlantOverride(classNode, methodNode);
			}
			else
				throw new RuntimeException("BlockBaseGarden: canSustainBush (i) method not found");
		}
		else if (transformedName.contains("com.pam.harvestcraft"))
		{
			this.transformHrvestcraft(transformedName, classNode);
		}
		else if (transformedName.equals("net.minecraft.world.gen.feature.WorldGenTallGrass") || 
				transformedName.equals("net.minecraft.world.gen.feature.WorldGenDoublePlant"))
		{
			String desc = ASMHelper.toMethodDescriptor("Z",ObfHelper.toObfClassName(ASMConstants.WORLD),ObfHelper.toObfClassName(ASMConstants.RANDOM),ObfHelper.toObfClassName(ASMConstants.BLOCK_POS));
			MethodNode methodNode = ASMHelper.findMethodNodeOfClass(classNode, "b", "generate", desc);

			if (methodNode != null)
			{
				InsnList list = new InsnList();
				methodNode.instructions.clear();
				list.add(new InsnNode(Opcodes.ICONST_1));
				list.add(new InsnNode(Opcodes.IRETURN));
				methodNode.instructions.insert(list);
			}
			else
				throw new RuntimeException("WorldGenTallGrass | WorldGenDoublePlant: generate (b) method not found");
		}

		return ASMHelper.writeClassToBytes(classNode);
	}

	private void transformHrvestcraft(String name, ClassNode classNode)
	{
		if (name.equals("com.pam.harvestcraft.blocks.blocks.BlockBaseGarden"))
		{
			String desc = ASMHelper.toMethodDescriptor("Z",ASMConstants.WORLD, ASMConstants.BLOCK_POS);//Note to self, don't attempt to obf the class names when overwriting a non vanilla method from another mod
			MethodNode methodNode = ASMHelper.findMethodNodeOfClass(classNode, "checkSoilBlock", "checkSoilBlock", desc);

			if (methodNode != null)
			{
				addCanPlaceBlockOverride(classNode, methodNode);
			}
			else
				throw new RuntimeException("BlockBaseGarden: checkSoilBlock(" +ObfHelper.getInternalClassName(ASMConstants.WORLD) + "," +ObfHelper.getInternalClassName(ASMConstants.BLOCK_POS) +") method not found");
		}
		else if (name.equals("com.pam.harvestcraft.worldgen.BushWorldGen"))
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
			{
				String msg = "BushWorldGen: generate (generate) method not found! | ";
				for(MethodNode m : classNode.methods)
				{
					msg += m.name+", ";
				}
				throw new RuntimeException(msg);
			}
		}
	}

	private String gi(String s)
	{
		return ObfHelper.getInternalClassName(s);
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

		list.add(new VarInsnNode(Opcodes.ALOAD, 1));
		list.add(new VarInsnNode(Opcodes.ALOAD, 2));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,	ObfHelper.isObfuscated() ? "co" : ObfHelper.getInternalClassName(ASMConstants.BLOCK_POS), ObfHelper.isObfuscated() ? "c" : "down", ASMHelper.toMethodDescriptor(ObfHelper.isObfuscated() ? "co" :ObfHelper.toObfClassName(ASMConstants.BLOCK_POS)), false));
		list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/bioxx/tfc2/ServerOverrides","isSoil",ASMHelper.toMethodDescriptor("Z",ObfHelper.toObfClassName(ASMConstants.WORLD), ObfHelper.toObfClassName(ASMConstants.BLOCK_POS)), false));
		list.add(new InsnNode(Opcodes.IRETURN));
		method.instructions.insert(list);
	}

	private boolean checkSoilBlock(World world, BlockPos pos) {

		return ServerOverrides.isSoil(world, pos.down());
	}

	private void addCanSustainPlantOverride(ClassNode classNode, MethodNode method)
	{
		InsnList list = new InsnList();
		method.instructions.clear();
		list.add(new VarInsnNode(Opcodes.ALOAD, 1));
		list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/bioxx/tfc2/Core", "isSoil", ASMHelper.toMethodDescriptor("Z",ObfHelper.toObfClassName(ASMConstants.IBLOCKSTATE)), false));
		list.add(new InsnNode(Opcodes.IRETURN));
		method.instructions.insert(list);
	}
}
