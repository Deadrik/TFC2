package com.bioxx.tfc2.asm.transform;

import net.minecraft.launchwrapper.IClassTransformer;

import com.bioxx.tfc2.ASMConstants;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import squeek.asmhelper.com.bioxx.tfc2.ASMHelper;
import squeek.asmhelper.com.bioxx.tfc2.ObfHelper;

public class ModuleEntityRenderer implements IClassTransformer 
{

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) 
	{
		ClassNode classNode = ASMHelper.readClassFromBytes(basicClass);

		if (!transformedName.equals(ASMConstants.ENTITY_RENDERER))
		{
			return ASMHelper.writeClassToBytes(classNode);
		}

		MethodNode methodNode = ASMHelper.findMethodNodeOfClass(classNode, "p", "addRainParticles", ASMHelper.toMethodDescriptor("V"));

		if (methodNode != null)
		{
			addRainParticlesHook(classNode, methodNode);
			return ASMHelper.writeClassToBytes(classNode);
		}
		else
			throw new RuntimeException("EntityRenderer: addRainParticles (p) method not found");
	}

	private void addRainParticlesHook(ClassNode classNode, MethodNode method)
	{
		InsnList list = new InsnList();

		if(!ObfHelper.isObfuscated())
		{
			list.add(new VarInsnNode(Opcodes.ALOAD, 0));
			list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/EntityRenderer", "random", "Ljava/util/Random;"));
			list.add(new VarInsnNode(Opcodes.ALOAD, 0));
			list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/EntityRenderer", "rendererUpdateCount", "I"));
			list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/bioxx/tfc2/rendering/WeatherRenderer","addRainParticles","(Ljava/util/Random;I)V", false));
			list.add(new InsnNode(Opcodes.RETURN));
		}
		else
		{
			list.add(new VarInsnNode(Opcodes.ALOAD, 0));
			list.add(new FieldInsnNode(Opcodes.GETFIELD, "bnz", "j", "Ljava/util/Random;"));
			list.add(new VarInsnNode(Opcodes.ALOAD, 0));
			list.add(new FieldInsnNode(Opcodes.GETFIELD, "bnz", "m", "I"));
			list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/bioxx/tfc2/rendering/WeatherRenderer","addRainParticles","(Ljava/util/Random;I)V", false));
			list.add(new InsnNode(Opcodes.RETURN));
		}
		method.instructions.insert(list);
	}

}
