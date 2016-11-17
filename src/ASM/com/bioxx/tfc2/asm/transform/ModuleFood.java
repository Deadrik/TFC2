package com.bioxx.tfc2.asm.transform;

import java.util.ArrayList;

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

			String fieldFoodGroup = "foodGroup";
			String fieldEdible = "edible";
			String fieldExpirationTimer = "expirationTimer";

			classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC,"foodGroup",ASMHelper.toDescriptor(ASMConstants.ENUMFOODGROUP), null, "None"));
			classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC,"edible",ASMHelper.toDescriptor("Z"), null, 1));
			classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC,"expirationTimer",ASMHelper.toDescriptor("J"), null, new Long(72000L)));

			MethodNode getFoodGroupMethod = new MethodNode(Opcodes.ACC_PUBLIC,"getFoodGroup",ASMHelper.toMethodDescriptor(ASMConstants.ENUMFOODGROUP),null, null);
			getFoodGroupMethod.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
			getFoodGroupMethod.instructions.add(new FieldInsnNode(Opcodes.GETFIELD,classNode.name, fieldFoodGroup,ASMHelper.toDescriptor(ASMConstants.ENUMFOODGROUP)));
			getFoodGroupMethod.instructions.add(new InsnNode(Opcodes.ARETURN));
			classNode.methods.add(getFoodGroupMethod);

			this.tryAddFieldSetter(classNode, "setFoodGroup", fieldFoodGroup, ASMHelper.toDescriptor(ASMConstants.ENUMFOODGROUP));

			MethodNode onDecayedMethod = new MethodNode(Opcodes.ACC_PUBLIC,"onDecayed",ASMHelper.toMethodDescriptor(ObfHelper.toObfClassName(ASMConstants.ITEMSTACK), ObfHelper.toObfClassName(ASMConstants.ITEMSTACK),ObfHelper.toObfClassName(ASMConstants.WORLD), "I","I","I"),null, null);
			onDecayedMethod.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
			onDecayedMethod.instructions.add(new InsnNode(Opcodes.ARETURN));
			classNode.methods.add(onDecayedMethod);

			MethodNode isEdibleMethod = new MethodNode(Opcodes.ACC_PUBLIC,"getIsEdible",ASMHelper.toMethodDescriptor("Z", ObfHelper.toObfClassName(ASMConstants.ITEMSTACK)),null, null);
			isEdibleMethod.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
			isEdibleMethod.instructions.add(new FieldInsnNode(Opcodes.GETFIELD,classNode.name, fieldEdible,ASMHelper.toDescriptor("Z")));
			isEdibleMethod.instructions.add(new InsnNode(Opcodes.IRETURN));
			classNode.methods.add(isEdibleMethod);

			this.tryAddFieldSetter(classNode, "setIsEdible", fieldEdible, ASMHelper.toDescriptor("Z"));

			MethodNode getExpirationTimerMethod = new MethodNode(Opcodes.ACC_PUBLIC,"getExpirationTimer",ASMHelper.toMethodDescriptor("J", ObfHelper.toObfClassName(ASMConstants.ITEMSTACK)),null, null);
			getExpirationTimerMethod.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
			getExpirationTimerMethod.instructions.add(new FieldInsnNode(Opcodes.GETFIELD,classNode.name, fieldExpirationTimer,ASMHelper.toDescriptor("J")));
			getExpirationTimerMethod.instructions.add(new InsnNode(Opcodes.LRETURN));
			classNode.methods.add(getExpirationTimerMethod);

			this.tryAddFieldSetter(classNode, "setExpirationTimer", fieldExpirationTimer, ASMHelper.toDescriptor("J"));
			String methDesc = ASMHelper.toMethodDescriptor("V", ObfHelper.toObfClassName(ASMConstants.ITEM), ObfHelper.toObfClassName(ASMConstants.CREATIVETABS), "Ljava/util/List<"+ASMHelper.toDescriptor(ObfHelper.toObfClassName(ASMConstants.ITEMSTACK)+";>"));
			MethodNode addSubItemsMethod = new MethodNode(Opcodes.ACC_PUBLIC,"getSubItems",ASMHelper.toMethodDescriptor("V", ObfHelper.toObfClassName(ASMConstants.ITEM), ObfHelper.toObfClassName(ASMConstants.CREATIVETABS), ASMConstants.LIST),methDesc, null);
			AnnotationNode addSubAnnotation = new AnnotationNode("Lnet/minecraftforge/fml/relauncher/SideOnly;");
			addSubAnnotation.visitEnum("value", "Lnet/minecraftforge/fml/relauncher/Side;", "CLIENT");
			addSubItemsMethod.visibleAnnotations = new ArrayList<AnnotationNode>();
			addSubItemsMethod.visibleAnnotations.add(addSubAnnotation);
			addSubItemsMethod.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
			addSubItemsMethod.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
			addSubItemsMethod.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
			addSubItemsMethod.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/bioxx/tfc2/core/Food","getSubItems",ASMHelper.toMethodDescriptor("V",ObfHelper.toObfClassName(ASMConstants.ITEM), ObfHelper.toObfClassName(ASMConstants.CREATIVETABS), ASMConstants.LIST), false));
			addSubItemsMethod.instructions.add(new InsnNode(Opcodes.RETURN));
			classNode.methods.add(addSubItemsMethod);

			//We need to set the default values for our new fields
			MethodNode defaultConstructor = ASMHelper.findMethodNodeOfClass(classNode, "<init>", ASMHelper.toMethodDescriptor("V", "I", "F", "Z"));
			if(defaultConstructor != null)
			{
				InsnList toInject = new InsnList();
				toInject.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
				toInject.add(new FieldInsnNode(Opcodes.GETSTATIC,ASMHelper.toInternalClassName(ASMConstants.ENUMFOODGROUP), "None", ASMHelper.toDescriptor(ASMConstants.ENUMFOODGROUP))); // player param
				toInject.add(new FieldInsnNode(Opcodes.PUTFIELD, classNode.name, fieldFoodGroup, ASMHelper.toDescriptor(ASMConstants.ENUMFOODGROUP)));

				toInject.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
				toInject.add(new InsnNode(Opcodes.ICONST_1)); //true
				toInject.add(new FieldInsnNode(Opcodes.PUTFIELD, classNode.name, fieldEdible, ASMHelper.toDescriptor("Z")));

				toInject.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
				toInject.add(new LdcInsnNode(new Long(72000L))); //true
				toInject.add(new FieldInsnNode(Opcodes.PUTFIELD, classNode.name, fieldExpirationTimer, ASMHelper.toDescriptor("J")));

				AbstractInsnNode finalNode = ASMHelper.findLastInstructionWithOpcode(defaultConstructor, Opcodes.RETURN);
				defaultConstructor.instructions.insertBefore(finalNode, toInject);
			}
			else
				throw new RuntimeException("ItemFood: defaultConstructor(IFZ)V method not found");

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
