//=======================================================
//Mod Client File
//=======================================================
package com.bioxx.tfc2;

import java.util.Arrays;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;

import com.google.common.eventbus.EventBus;

@MCVersion(value = "1.11.2")
public class TFC2Core extends DummyModContainer
{
	@Instance("tfc2_coremod")
	public static TFC2Core instance;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static CommonProxy proxy;


	public TFC2Core()
	{
		super(new ModMetadata());
		ModMetadata meta = getMetadata();
		meta.modId = "tfc2_coremod";
		meta.name = "TFC2[coremod]";
		meta.version = Reference.ModVersion;
		meta.credits = "";
		meta.authorList = Arrays.asList("Bioxx");
		meta.description = "";
		meta.url = "www.terrafirmacraft.com";
		meta.updateUrl = "";
		meta.screenshots = new String[0];
		meta.logoFile = "";
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		bus.register(this);
		return true;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) 
	{
		instance = this;

	}

	@EventHandler
	public void initialize(FMLInitializationEvent evt)
	{

	}

	@EventHandler
	public void modsLoaded(FMLPostInitializationEvent evt) 
	{

	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent evt)
	{

	}	

}
