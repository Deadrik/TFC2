package com.bioxx.tfc2.asm.transform;

import java.util.Iterator;

import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.IClassTransformer;

import com.bioxx.tfc2.asm.ASMConstants;
import com.bioxx.tfc2.asm.ASMHelper;
import com.bioxx.tfc2.asm.ObfHelper;
import com.bioxx.tfc2.core.Food;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ModuleInventory implements IClassTransformer 
{
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) 
	{
		if(basicClass == null)
			return null;

		ClassNode classNode = ASMHelper.readClassFromBytes(basicClass);

		if (transformedName.equals("net.minecraft.entity.player.InventoryPlayer"))
		{
			String desc = ASMHelper.toMethodDescriptor("Z",ObfHelper.toObfClassName(ASMConstants.ITEMSTACK), ObfHelper.toObfClassName(ASMConstants.ITEMSTACK));
			MethodNode methodNode = ASMHelper.findMethodNodeOfClass(classNode, "b", "stackEqualExact", desc);

			if (methodNode != null)
			{
				InsnList list = new InsnList();
				methodNode.instructions.clear();
				list.add(new VarInsnNode(Opcodes.ALOAD, 1));
				list.add(new VarInsnNode(Opcodes.ALOAD, 2));
				list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/bioxx/tfc2/core/InventoryPlayerTFC","stackEqualExact",ASMHelper.toMethodDescriptor("Z",ObfHelper.toObfClassName(ASMConstants.ITEMSTACK), ObfHelper.toObfClassName(ASMConstants.ITEMSTACK)), false));
				list.add(new InsnNode(Opcodes.IRETURN));
				methodNode.instructions.insert(list);
			}
			else
			{
				throw new RuntimeException("Unable to replace stackEqualExact in InventoryPlayer");
			}

			desc = ASMHelper.toMethodDescriptor("I",ObfHelper.toObfClassName(ASMConstants.ITEMSTACK));
			methodNode = ASMHelper.findMethodNodeOfClass(classNode, "g", "storePartialItemStack", desc);

			if (methodNode != null)
			{
				InsnList list = new InsnList();

				list.add(new VarInsnNode(Opcodes.ALOAD, 1));
				list.add(new VarInsnNode(Opcodes.ALOAD, 5));
				list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/bioxx/tfc2/core/Food","mergeFood",ASMHelper.toMethodDescriptor(ObfHelper.toObfClassName(ASMConstants.ITEMSTACK),ObfHelper.toObfClassName(ASMConstants.ITEMSTACK), ObfHelper.toObfClassName(ASMConstants.ITEMSTACK)), false));
				list.add(new VarInsnNode(Opcodes.ASTORE, 5));

				AbstractInsnNode node0 = new MethodInsnNode(Opcodes.INVOKEVIRTUAL,gi(ASMConstants.ITEMSTACK),ObfHelper.isObfuscated()? "f" : "grow",ASMHelper.toMethodDescriptor("V","I"), false);
				node0 = ASMHelper.find(methodNode.instructions, node0);
				node0 = node0.getPrevious().getPrevious().getPrevious();
				methodNode.instructions.insertBefore(node0, list);
			}
			else
			{
				throw new RuntimeException("Unable to replace storePartialItemStack in InventoryPlayer");
			}
		}

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

	public boolean stackEqualExact(ItemStack stack1, ItemStack stack2)
	{
		return Food.areEqual(stack1, stack2);
	}
}
