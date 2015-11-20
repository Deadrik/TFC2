package com.bioxx.tfc2.asm.transform;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.bioxx.tfc2.asm.ClassTransformer;

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
		obfClassName = "cji";

		List<InstrSet> nodes = new ArrayList<InstrSet>();
		InsnList list = new InsnList();
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/EntityRenderer", "random", "Ljava/util/Random;"));
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/EntityRenderer", "rendererUpdateCount", "I"));
		list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/bioxx/tfc2/rendering/WeatherRenderer","addRainParticles","(Ljava/util/Random;I)V", false));
		nodes.add(new InstrSet(list, 197, InstrOpType.Replace));
		this.mcpMethodNodes.put("updateRenderer | ()V", new Patch(nodes, PatchOpType.Modify));

		nodes = new ArrayList<InstrSet>();
		list = new InsnList();
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "cji", "j", "Ljava/util/Random;"));
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "cji", "m", "I"));
		list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/bioxx/tfc2/rendering/WeatherRenderer","addRainParticles","(Ljava/util/Random;I)V", false));
		nodes.add(new InstrSet(list, 197, InstrOpType.Replace));
		this.obfMethodNodes.put("e | ()V", new Patch(nodes, PatchOpType.Modify));
	}
}