package com.bioxx.tfc2.asm.transform;

import java.util.ArrayList;
import java.util.List;

import com.bioxx.tfc2.asm.ClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * This Transformer replaces the call to the vanilla addRainParticles() method in EntityRenderer.updateRenderer() 
 * with our own version that uses TFC weather.
 * @author Bioxx
 *
 */
public class TF_EntityRenderer extends ClassTransformer
{

	public TF_EntityRenderer()
	{
		mcpClassName = "net.minecraft.client.renderer.EntityRenderer";
		obfClassName = "bnz";

		List<InstrSet> nodes = new ArrayList<InstrSet>();
		InsnList list = new InsnList();

		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/EntityRenderer", "random", "Ljava/util/Random;"));
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/EntityRenderer", "rendererUpdateCount", "I"));
		list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/bioxx/tfc2/rendering/WeatherRenderer","addRainParticles","(Ljava/util/Random;I)V", false));
		list.add(new InsnNode(Opcodes.RETURN));
		nodes.add(new InstrSet(list, 1, InstrOpType.Replace));
		this.mcpMethodNodes.put("addRainParticles | ()V", new Patch(nodes, PatchOpType.Modify));

		nodes = new ArrayList<InstrSet>();
		list = new InsnList();

		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "bnz", "j", "Ljava/util/Random;"));
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "bnz", "m", "I"));
		list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/bioxx/tfc2/rendering/WeatherRenderer","addRainParticles","(Ljava/util/Random;I)V", false));
		list.add(new InsnNode(Opcodes.RETURN));
		nodes.add(new InstrSet(list, 1, InstrOpType.Replace));
		this.obfMethodNodes.put("p | ()V", new Patch(nodes, PatchOpType.Modify));
	}
}