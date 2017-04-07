package com.bioxx.tfc2.asm.transform;

import net.minecraft.launchwrapper.IClassTransformer;

import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.asm.ASMConstants;
import com.bioxx.tfc2.asm.ASMHelper;
import com.bioxx.tfc2.asm.ObfHelper;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ModuleEntityRenderer implements IClassTransformer 
{

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) 
	{
		if(basicClass == null)
		{
			return null;
		}

		ClassNode classNode = ASMHelper.readClassFromBytes(basicClass);

		if (!transformedName.equals(ASMConstants.ENTITY_RENDERER))
		{
			return basicClass;
		}

		MethodNode methodNode = ASMHelper.findMethodNodeOfClass(classNode, "q", "addRainParticles", ASMHelper.toMethodDescriptor("V"));

		if (methodNode != null)
		{
			addRainParticlesHook(classNode, methodNode);
			return ASMHelper.writeClassToBytes(classNode);
		}
		else
			TFC.log.warn("EntityRenderer: addRainParticles (q) method not found");

		return basicClass;
	}

	private void addRainParticlesHook(ClassNode classNode, MethodNode method)
	{
		InsnList list = new InsnList();

		if(!ObfHelper.isObfuscated())
		{
			list.add(new VarInsnNode(Opcodes.ALOAD, 0));
			list.add(new FieldInsnNode(Opcodes.GETFIELD, ObfHelper.getInternalClassName(ASMConstants.ENTITY_RENDERER), "random", "Ljava/util/Random;"));
			list.add(new VarInsnNode(Opcodes.ALOAD, 0));
			list.add(new FieldInsnNode(Opcodes.GETFIELD, ObfHelper.getInternalClassName(ASMConstants.ENTITY_RENDERER), "rendererUpdateCount", "I"));
			list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/bioxx/tfc2/rendering/WeatherRenderer","addRainParticles","(Ljava/util/Random;I)V", false));
			list.add(new InsnNode(Opcodes.RETURN));
		}
		else
		{
			list.add(new VarInsnNode(Opcodes.ALOAD, 0));
			list.add(new FieldInsnNode(Opcodes.GETFIELD, ObfHelper.getInternalClassName(ASMConstants.ENTITY_RENDERER), "j", "Ljava/util/Random;"));
			list.add(new VarInsnNode(Opcodes.ALOAD, 0));
			list.add(new FieldInsnNode(Opcodes.GETFIELD, ObfHelper.getInternalClassName(ASMConstants.ENTITY_RENDERER), "m", "I"));
			list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/bioxx/tfc2/rendering/WeatherRenderer","addRainParticles","(Ljava/util/Random;I)V", false));
			list.add(new InsnNode(Opcodes.RETURN));
		}
		method.instructions.insert(list);
	}

}
