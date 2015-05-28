package com.bioxx.tfc2;

import java.io.File;

import net.minecraftforge.fml.common.FMLCommonHandler;

public class CommonProxy
{
	public void registerRenderInformation()
	{
		// NOOP on server
	}

	public File getMinecraftDir()
	{
		return FMLCommonHandler.instance().getMinecraftServerInstance().getFile("");/*new File(".");*/
	}

}
