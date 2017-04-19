package com.bioxx.tfc2.asm.transform;

import java.util.Iterator;

import net.minecraft.launchwrapper.IClassTransformer;

import com.bioxx.tfc2.asm.ASMHelper;
import com.bioxx.tfc2.asm.ObfHelper;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ModuleVanillaReplacement implements IClassTransformer 
{
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) 
	{
		if(basicClass == null)
			return null;

		ClassNode classNode = ASMHelper.readClassFromBytes(basicClass);

		if (transformedName.equals("net.minecraft.item.Item"))
		{
			String desc = ASMHelper.toMethodDescriptor("V");
			MethodNode methodNode = ASMHelper.findMethodNodeOfClass(classNode, "u", "registerItems", desc);

			if (methodNode != null)
			{
				InsnList list = new InsnList();
				AbstractInsnNode node0 = getLineNumber(methodNode.instructions, 1566);
				node0 = ASMHelper.find(methodNode.instructions, new IntInsnNode(Opcodes.SIPUSH, 337));
				node0 = node0.getNext().getNext();
				AbstractInsnNode newNode = new TypeInsnNode(Opcodes.NEW, gi("com/bioxx/tfc2/items/ItemClayBall"));
				methodNode.instructions.insert(node0, newNode);
				methodNode.instructions.remove(node0);

				node0 = newNode.getNext().getNext();
				MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESPECIAL, gi("com/bioxx/tfc2/items/ItemClayBall"), "<init>", ASMHelper.toMethodDescriptor("V"), false);
				methodNode.instructions.insert(node0, method);
				methodNode.instructions.remove(node0);
			}
			else
			{
				String msg = "Unable to replace vanilla clay_ball item";
				for(MethodNode m : classNode.methods)
				{
					msg += m.name+", ";
				}
				throw new RuntimeException(msg);
			}
		}
		/*else if (transformedName.equals("net.minecraft.world.gen.feature.WorldGenTallGrass") || 
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
		}*/

		return ASMHelper.writeClassToBytes(classNode);
	}

	private AbstractInsnNode getLineNumber(InsnList list, int line)
	{
		Iterator<AbstractInsnNode> iter = list.iterator();
		while(iter.hasNext())
		{
			AbstractInsnNode node = iter.next();
			if(node instanceof LineNumberNode)
			{
				if(((LineNumberNode)node).line == line)
					return node;
			}
		}
		return null;
	}

	private String gi(String s)
	{
		return ObfHelper.getInternalClassName(s);
	}
}
