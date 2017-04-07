package com.bioxx.tfc2.asm.transform;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.nbt.NBTTagCompound;

import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.interfaces.IFoodStatsTFC;
import com.bioxx.tfc2.api.types.EnumFoodGroup;
import com.bioxx.tfc2.asm.ASMConstants;
import com.bioxx.tfc2.asm.ASMHelper;
import com.bioxx.tfc2.asm.ObfHelper;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public class ModuleFood implements IClassTransformer 
{
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) 
	{
		if(basicClass == null)
			return null;
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
				TFC.log.warn(msg);
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

			String methDesc = ASMHelper.toMethodDescriptor("V", ObfHelper.toObfClassName(ASMConstants.ITEM), ObfHelper.toObfClassName(ASMConstants.CREATIVETABS), ObfHelper.getDescriptor(ObfHelper.toObfClassName(ASMConstants.NONNULLLIST), ObfHelper.toObfClassName(ASMConstants.ITEMSTACK)));
			MethodNode addSubItemsMethod = new MethodNode(Opcodes.ACC_PUBLIC,"getSubItems",ASMHelper.toMethodDescriptor("V", ObfHelper.toObfClassName(ASMConstants.ITEM), ObfHelper.toObfClassName(ASMConstants.CREATIVETABS), ObfHelper.toObfClassName(ASMConstants.NONNULLLIST)),methDesc, null);
			AnnotationNode addSubAnnotation = new AnnotationNode("Lnet/minecraftforge/fml/relauncher/SideOnly;");
			addSubAnnotation.visitEnum("value", "Lnet/minecraftforge/fml/relauncher/Side;", "CLIENT");
			addSubItemsMethod.visibleAnnotations = new ArrayList<AnnotationNode>();
			addSubItemsMethod.visibleAnnotations.add(addSubAnnotation);
			addSubItemsMethod.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
			addSubItemsMethod.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
			addSubItemsMethod.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
			addSubItemsMethod.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/bioxx/tfc2/core/Food","getSubItems",ASMHelper.toMethodDescriptor("V",ObfHelper.toObfClassName(ASMConstants.ITEM), ObfHelper.toObfClassName(ASMConstants.CREATIVETABS), ObfHelper.toObfClassName(ASMConstants.NONNULLLIST)), false));
			addSubItemsMethod.instructions.add(new InsnNode(Opcodes.RETURN));
			classNode.methods.add(addSubItemsMethod);

			return ASMHelper.writeClassToBytes(classNode);
		}
		else if (transformedName.equals("net.minecraft.item.ItemFishFood"))
		{
			MethodNode methodNode = ASMHelper.findMethodNodeOfClass(classNode, "a", "getSubItems", 
					ASMHelper.toMethodDescriptor("V",ObfHelper.toObfClassName(ASMConstants.ITEM), ObfHelper.toObfClassName(ASMConstants.CREATIVETABS), ObfHelper.toObfClassName(ASMConstants.NONNULLLIST)));

			if (methodNode != null)
			{
				AbstractInsnNode finalNode = ASMHelper.findLastInstructionWithOpcode(methodNode, Opcodes.RETURN);
				InsnList toInject = new InsnList();
				toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ObfHelper.getInternalClassName(ASMConstants.MINECRAFT),ObfHelper.chooseObf("z","getMinecraft"),ASMHelper.toMethodDescriptor(ObfHelper.getInternalClassName(ASMConstants.MINECRAFT)), false));
				toInject.add(new FieldInsnNode(Opcodes.GETFIELD, ObfHelper.getInternalClassName(ASMConstants.MINECRAFT), ObfHelper.chooseObf("f","world"), ASMHelper.toDescriptor(ObfHelper.getInternalClassName(ASMConstants.WORLDCLIENT))));
				toInject.add(new VarInsnNode(Opcodes.ALOAD, 3));
				toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/bioxx/tfc2/core/Food","addDecayTimerForCreative",ASMHelper.toMethodDescriptor("V", ObfHelper.toObfClassName(ASMConstants.WORLD),ASMConstants.LIST), false));
				methodNode.instructions.insertBefore(finalNode, toInject);
			}
			else
				TFC.log.warn("ItemFishFood: getSubItems (a) method not found");

			return ASMHelper.writeClassToBytes(classNode);
		}
		else if (transformedName.equals("net.minecraft.util.FoodStats"))
		{
			classNode.interfaces.add(ASMHelper.toInternalClassName("com.bioxx.tfc2.api.interfaces.IFoodStatsTFC"));

			String fieldNutritionMap = "nutritionMap";
			String fieldWaterLevel = "waterLevel";
			classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, fieldNutritionMap,"Ljava/util/HashMap;", "Ljava/util/HashMap<Lcom/bioxx/tfc2/api/types/EnumFoodGroup;Ljava/lang/Float;>;", null));
			tryAddFieldGetter(classNode, "getNutritionMap", fieldNutritionMap, "Ljava/util/HashMap;");
			classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, fieldWaterLevel,"F", null, null));
			tryAddFieldGetter(classNode, "getWaterLevel", fieldWaterLevel, "F");
			tryAddFieldSetter(classNode, "setWaterLevel", fieldWaterLevel, "F");

			for (MethodNode method : classNode.methods)
			{
				if (method.name.equals("<init>"))
				{
					MethodNode defaultConstructor = ASMHelper.findMethodNodeOfClass(classNode, "<init>", ASMHelper.toMethodDescriptor("V"));
					if(defaultConstructor != null)
					{
						InsnList toInject = new InsnList();

						toInject.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
						toInject.add(new TypeInsnNode(Opcodes.NEW, "java/util/HashMap"));
						toInject.add(new InsnNode(Opcodes.DUP));
						toInject.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,"java/util/HashMap", "<init>", "()V", false));
						toInject.add(new FieldInsnNode(Opcodes.PUTFIELD, classNode.name, fieldNutritionMap, "Ljava/util/HashMap;"));

						toInject.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
						toInject.add(new LdcInsnNode(new Float(20f)));
						toInject.add(new FieldInsnNode(Opcodes.PUTFIELD, classNode.name, fieldWaterLevel, ASMHelper.toDescriptor("F")));

						//Set Grain default Value
						toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
						toInject.add(new FieldInsnNode(Opcodes.GETFIELD, classNode.name, fieldNutritionMap, "Ljava/util/HashMap;"));
						toInject.add(new FieldInsnNode(Opcodes.GETSTATIC, ASMHelper.toInternalClassName(ASMConstants.ENUMFOODGROUP), "Grain", ASMHelper.toDescriptor(ASMConstants.ENUMFOODGROUP)));
						toInject.add(new LdcInsnNode(new Float(20f)));
						toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false));
						toInject.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false));
						toInject.add(new InsnNode(Opcodes.POP));

						//Set Veg default Value
						toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
						toInject.add(new FieldInsnNode(Opcodes.GETFIELD, classNode.name, fieldNutritionMap, "Ljava/util/HashMap;"));
						toInject.add(new FieldInsnNode(Opcodes.GETSTATIC, ASMHelper.toInternalClassName(ASMConstants.ENUMFOODGROUP), "Vegetable", ASMHelper.toDescriptor(ASMConstants.ENUMFOODGROUP)));
						toInject.add(new LdcInsnNode(new Float(20f)));
						toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false));
						toInject.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false));
						toInject.add(new InsnNode(Opcodes.POP));

						//Set Fruit default Value
						toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
						toInject.add(new FieldInsnNode(Opcodes.GETFIELD, classNode.name, fieldNutritionMap, "Ljava/util/HashMap;"));
						toInject.add(new FieldInsnNode(Opcodes.GETSTATIC, ASMHelper.toInternalClassName(ASMConstants.ENUMFOODGROUP), "Fruit", ASMHelper.toDescriptor(ASMConstants.ENUMFOODGROUP)));
						toInject.add(new LdcInsnNode(new Float(20f)));
						toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false));
						toInject.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false));
						toInject.add(new InsnNode(Opcodes.POP));

						//Set Protein default Value
						toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
						toInject.add(new FieldInsnNode(Opcodes.GETFIELD, classNode.name, fieldNutritionMap, "Ljava/util/HashMap;"));
						toInject.add(new FieldInsnNode(Opcodes.GETSTATIC, ASMHelper.toInternalClassName(ASMConstants.ENUMFOODGROUP), "Protein", ASMHelper.toDescriptor(ASMConstants.ENUMFOODGROUP)));
						toInject.add(new LdcInsnNode(new Float(20f)));
						toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false));
						toInject.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false));
						toInject.add(new InsnNode(Opcodes.POP));

						//Set Dairy default Value
						toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
						toInject.add(new FieldInsnNode(Opcodes.GETFIELD, classNode.name, fieldNutritionMap, "Ljava/util/HashMap;"));
						toInject.add(new FieldInsnNode(Opcodes.GETSTATIC, ASMHelper.toInternalClassName(ASMConstants.ENUMFOODGROUP), "Dairy", ASMHelper.toDescriptor(ASMConstants.ENUMFOODGROUP)));
						toInject.add(new LdcInsnNode(new Float(20f)));
						toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false));
						toInject.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false));
						toInject.add(new InsnNode(Opcodes.POP));


						AbstractInsnNode finalNode = ASMHelper.findLastInstructionWithOpcode(defaultConstructor, Opcodes.RETURN);
						defaultConstructor.instructions.insertBefore(finalNode, toInject);
					}
					else
						TFC.log.warn("FoodStats: defaultConstructor()V method not found");
				}
			}

			MethodNode methodNode = ASMHelper.findMethodNodeOfClass(classNode, "a", "addStats", ASMHelper.toMethodDescriptor("V",ObfHelper.toObfClassName(ASMConstants.ITEM_FOOD), ObfHelper.toObfClassName(ASMConstants.ITEMSTACK)));

			if (methodNode != null)
			{
				AbstractInsnNode finalNode = ASMHelper.findLastInstructionWithOpcode(methodNode, Opcodes.INVOKEVIRTUAL);
				InsnList toInject = new InsnList();
				toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
				toInject.add(new VarInsnNode(Opcodes.ALOAD, 2));
				toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/bioxx/tfc2/core/Food","addNutrition",ASMHelper.toMethodDescriptor("V",ObfHelper.toObfClassName(ASMConstants.FOOD_STATS), ObfHelper.toObfClassName(ASMConstants.ITEMSTACK)), false));
				methodNode.instructions.insert(finalNode, toInject);
			}
			else
				TFC.log.warn("FoodStats: addStats (a) method not found");

			methodNode = ASMHelper.findMethodNodeOfClass(classNode, "a", "readNBT", ASMHelper.toMethodDescriptor("V",ObfHelper.toObfClassName(ASMConstants.NBTTAGCOMPOUND)));
			if (methodNode != null)
			{
				AbstractInsnNode finalNode = ASMHelper.findLastInstructionWithOpcode(methodNode, Opcodes.PUTFIELD);
				InsnList toInject = new InsnList();
				toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
				toInject.add(new TypeInsnNode(Opcodes.CHECKCAST, "com/bioxx/tfc2/api/interfaces/IFoodStatsTFC"));
				toInject.add(new VarInsnNode(Opcodes.ALOAD, 1));
				toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/bioxx/tfc2/asm/transform/ModuleFood","readNBT",ASMHelper.toMethodDescriptor("V","Lcom/bioxx/tfc2/api/interfaces/IFoodStatsTFC;", ObfHelper.toObfClassName(ASMConstants.NBTTAGCOMPOUND) ), false));
				methodNode.instructions.insert(finalNode, toInject);
			}
			else
				TFC.log.warn("FoodStats: readNBT (a) method not found");

			methodNode = ASMHelper.findMethodNodeOfClass(classNode, "b", "writeNBT", ASMHelper.toMethodDescriptor("V",ObfHelper.toObfClassName(ASMConstants.NBTTAGCOMPOUND)));
			if (methodNode != null)
			{
				AbstractInsnNode finalNode = ASMHelper.findLastInstructionWithOpcode(methodNode, Opcodes.INVOKEVIRTUAL);
				InsnList toInject = new InsnList();
				toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
				toInject.add(new TypeInsnNode(Opcodes.CHECKCAST, "com/bioxx/tfc2/api/interfaces/IFoodStatsTFC"));
				toInject.add(new VarInsnNode(Opcodes.ALOAD, 1));
				toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/bioxx/tfc2/asm/transform/ModuleFood","writeNBT",ASMHelper.toMethodDescriptor("V","Lcom/bioxx/tfc2/api/interfaces/IFoodStatsTFC;", ObfHelper.toObfClassName(ASMConstants.NBTTAGCOMPOUND) ), false));
				methodNode.instructions.insert(finalNode, toInject);
			}
			else
				TFC.log.warn("FoodStats: writeNBT (b) method not found");

			return ASMHelper.writeClassToBytes(classNode);
		}
		else if (transformedName.equals("com.pam.harvestcraft.blocks.BlockPamCrop"))
		{
			MethodNode methodNode = ASMHelper.findMethodNodeOfClass(classNode, "isSuitableSoilBlock", "isSuitableSoilBlock", ASMHelper.toMethodDescriptor("Z",ObfHelper.toObfClassName(ASMConstants.BLOCK)));
			if(methodNode != null)
			{
				AbstractInsnNode finalNode = ASMHelper.findLastInstructionWithOpcode(methodNode, Opcodes.GETSTATIC);
				methodNode.instructions.insert(finalNode, new FieldInsnNode(Opcodes.GETSTATIC,"com/bioxx/tfc2/TFCBlocks", "Farmland", ObfHelper.toObfClassName(ASMConstants.BLOCK)));
				methodNode.instructions.remove(finalNode);
			}
			else
				TFC.log.warn("BlockPamCrop: isSuitableSoilBlock method not found");
		}
		/*else if (transformedName.equals("net.minecraft.item.ItemSeedFood"))
		{
			MethodNode methodNode = ASMHelper.findMethodNodeOfClass(classNode, "<init>", "<init>", ASMHelper.toMethodDescriptor("V","I", "F", ObfHelper.toObfClassName(ASMConstants.BLOCK), ObfHelper.toObfClassName(ASMConstants.BLOCK)));
			if(methodNode != null)
			{
				AbstractInsnNode finalNode = ASMHelper.find(methodNode.instructions, new VarInsnNode(Opcodes.ALOAD, 4));
				methodNode.instructions.insert(finalNode, new FieldInsnNode(Opcodes.GETSTATIC,"com/bioxx/tfc2/TFCBlocks", "Farmland", ObfHelper.toObfClassName(ASMConstants.BLOCK)));
				methodNode.instructions.remove(finalNode);
			}
			else
				throw new RuntimeException("ItemSeedFood: <init>(IFLBlockBlock) method not found");
		}*/

		return basicClass;
	}

	public void init(int healAmount, float saturation, Block crops, Block soil)
	{
		if(soil == Blocks.FARMLAND)
			soil = TFCBlocks.Farmland;
	}

	private void addInformationHook(ClassNode classNode, MethodNode method)
	{
		InsnList list = new InsnList();
		list.add(new VarInsnNode(Opcodes.ALOAD, 1));
		list.add(new VarInsnNode(Opcodes.ALOAD, 2));
		list.add(new VarInsnNode(Opcodes.ALOAD, 3));
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/bioxx/tfc2/ClientOverrides","addInformation",ASMHelper.toMethodDescriptor("V",ASMConstants.ITEMSTACK, ASMConstants.PLAYER, ASMConstants.LIST, ASMConstants.ITEM), false));
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

	public static void readNBT(IFoodStatsTFC fs, NBTTagCompound nbt)
	{
		HashMap map = fs.getNutritionMap();
		map.put(EnumFoodGroup.Fruit, nbt.getFloat("fruitnutrition"));
		map.put(EnumFoodGroup.Vegetable, nbt.getFloat("vegetablenutrition"));
		map.put(EnumFoodGroup.Grain, nbt.getFloat("grainnutrition"));
		map.put(EnumFoodGroup.Protein, nbt.getFloat("proteinnutrition"));
		map.put(EnumFoodGroup.Dairy, nbt.getFloat("dairynutrition"));
		fs.setWaterLevel(nbt.getFloat("waterLevel"));
	}

	public static void writeNBT(IFoodStatsTFC fs, NBTTagCompound nbt)
	{
		nbt.setFloat("fruitnutrition", fs.getNutritionMap().get(EnumFoodGroup.Fruit));
		nbt.setFloat("vegetablenutrition", fs.getNutritionMap().get(EnumFoodGroup.Vegetable));
		nbt.setFloat("grainnutrition", fs.getNutritionMap().get(EnumFoodGroup.Grain));
		nbt.setFloat("proteinnutrition", fs.getNutritionMap().get(EnumFoodGroup.Protein));
		nbt.setFloat("dairynutrition", fs.getNutritionMap().get(EnumFoodGroup.Dairy));
		nbt.setFloat("waterLevel", fs.getWaterLevel());
	}
}
