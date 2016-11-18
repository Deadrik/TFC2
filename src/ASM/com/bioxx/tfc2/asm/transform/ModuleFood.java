package com.bioxx.tfc2.asm.transform;

import net.minecraft.launchwrapper.IClassTransformer;

import com.bioxx.tfc2.ASMConstants;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import squeek.asmhelper.com.bioxx.tfc2.ASMHelper;
import squeek.asmhelper.com.bioxx.tfc2.ObfHelper;

public class ModuleFood implements IClassTransformer 
{
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) 
	{
		ClassNode classNode = ASMHelper.readClassFromBytes(basicClass);

		if (transformedName.equals("net.minecraft.item.Item"))
		{
			String desc = ASMHelper.toMethodDescriptor("V",ObfHelper.toObfClassName(ASMConstants.ITEMSTACK), ObfHelper.toObfClassName(ASMConstants.PLAYER), ASMConstants.LIST, "Z");
			MethodNode methodNode = ASMHelper.findMethodNodeOfClass(classNode, "a", "addInformation", desc);

			if (methodNode != null)
			{
				addInformationHook(classNode, methodNode);
			}
			else
			{
				//throw new RuntimeException("Item: addInformation (func_77624_a) method not found");
				String msg = "Item: addInformation ("+ desc +") method not found! | ";
				for(MethodNode m : classNode.methods)
				{
					msg += m.name+"("+ m.desc +")"+", ";
				}
				throw new RuntimeException(msg);
			}

			return ASMHelper.writeClassToBytes(classNode);
		}
		else if (transformedName.equals("net.minecraft.item.ItemFood"))
		{
			classNode.interfaces.add(ASMHelper.toInternalClassName("com.bioxx.tfc2.api.interfaces.IFood"));


			MethodNode onDecayedMethod = new MethodNode(Opcodes.ACC_PUBLIC,"onDecayed",ASMHelper.toMethodDescriptor(ObfHelper.toObfClassName(ASMConstants.ITEMSTACK), ObfHelper.toObfClassName(ASMConstants.ITEMSTACK),ObfHelper.toObfClassName(ASMConstants.WORLD), "I","I","I"),null, null);
			onDecayedMethod.instructions.add(new InsnNode(Opcodes.ACONST_NULL));
			onDecayedMethod.instructions.add(new InsnNode(Opcodes.ARETURN));
			classNode.methods.add(onDecayedMethod);

			return ASMHelper.writeClassToBytes(classNode);
		}
		else if (transformedName.equals("net.minecraft.item.ItemFishFood"))
		{
			MethodNode methodNode = ASMHelper.findMethodNodeOfClass(classNode, "a", "getSubItems", ASMHelper.toMethodDescriptor("V",ObfHelper.toObfClassName(ASMConstants.ITEM), ObfHelper.toObfClassName(ASMConstants.CREATIVETABS), ASMConstants.LIST));

			if (methodNode != null)
			{
				AbstractInsnNode finalNode = ASMHelper.findLastInstructionWithOpcode(methodNode, Opcodes.RETURN);
				InsnList toInject = new InsnList();
				toInject.add(new VarInsnNode(Opcodes.ALOAD, 3));
				toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/bioxx/tfc2/core/Food","addDecayTimerForCreative",ASMHelper.toMethodDescriptor("V",ASMConstants.LIST), false));
				methodNode.instructions.insertBefore(finalNode, toInject);
			}
			else
				throw new RuntimeException("ItemFishFood: getSubItems (5_a) method not found");

			return ASMHelper.writeClassToBytes(classNode);
		}

		return basicClass;
	}

	private void addInformationHook(ClassNode classNode, MethodNode method)
	{
		InsnList list = new InsnList();
		list.add(new VarInsnNode(Opcodes.ALOAD, 1));
		list.add(new VarInsnNode(Opcodes.ALOAD, 2));
		list.add(new VarInsnNode(Opcodes.ALOAD, 3));
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/bioxx/tfc2/core/Food","addInformation",ASMHelper.toMethodDescriptor("V",ASMConstants.ITEMSTACK, ASMConstants.PLAYER, ASMConstants.LIST, ASMConstants.ITEM), false));


		method.instructions.insert(list);
	}

	private boolean tryAddFieldGetter(ClassNode classNode, String methodName, String fieldName, String fieldDescriptor)
	{
		String methodDescriptor = ASMHelper.toMethodDescriptor(fieldDescriptor);
		if (ASMHelper.findMethodNodeOfClass(classNode, methodName, methodDescriptor) != null)
			return false;

		MethodVisitor mv = classNode.visitMethod(Opcodes.ACC_PUBLIC, methodName, methodDescriptor, null, null);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, ASMHelper.toInternalClassName(classNode.name), fieldName, fieldDescriptor);
		mv.visitInsn(Type.getType(fieldDescriptor).getOpcode(Opcodes.IRETURN));
		mv.visitMaxs(0, 0);
		return true;
	}

	private boolean tryAddFieldSetter(ClassNode classNode, String methodName, String fieldName, String fieldDescriptor)
	{
		String methodDescriptor = ASMHelper.toMethodDescriptor("V", fieldDescriptor);
		if (ASMHelper.findMethodNodeOfClass(classNode, methodName, methodDescriptor) != null)
			return false;

		MethodVisitor mv = classNode.visitMethod(Opcodes.ACC_PUBLIC, methodName, methodDescriptor, null, null);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Type.getType(fieldDescriptor).getOpcode(Opcodes.ILOAD), 1);
		mv.visitFieldInsn(Opcodes.PUTFIELD, ASMHelper.toInternalClassName(classNode.name), fieldName, fieldDescriptor);
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(0, 0);
		return true;
	}
}
