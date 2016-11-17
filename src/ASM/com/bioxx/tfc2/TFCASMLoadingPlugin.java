package com.bioxx.tfc2;

import java.io.File;
import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

import com.bioxx.tfc2.asm.transform.ModuleEntityRenderer;
import com.bioxx.tfc2.asm.transform.ModuleFood;
import com.bioxx.tfc2.asm.transform.ModuleWorldGen;
import squeek.asmhelper.com.bioxx.tfc2.ObfHelper;

@TransformerExclusions({ "com.bioxx.tfc2.asm", "squeek" })
public class TFCASMLoadingPlugin implements IFMLLoadingPlugin
{
	public static boolean runtimeDeobf;
	public static File location;

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{
				ModuleEntityRenderer.class.getName(), ModuleWorldGen.class.getName(), ModuleFood.class.getName()
		};
	}

	@Override
	public String getModContainerClass() {
		return TFC2Core.class.getName();
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		ObfHelper.setObfuscated((Boolean) data.get("runtimeDeobfuscationEnabled"));
		//ObfHelper.setRunsAfterDeobfRemapper(true);
		runtimeDeobf = (Boolean) data.get("runtimeDeobfuscationEnabled");
		location = (File) data.get("coremodLocation");
	}

}
