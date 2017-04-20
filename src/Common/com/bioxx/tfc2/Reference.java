package com.bioxx.tfc2;

public class Reference
{
	public static final String ModID = "tfc2";
	public static final String ModName = "TFC2";

	public static final int VersionMajor = 0;
	public static final int VersionMinor = 1;
	public static final int VersionRevision = 10;

	public static final String ModVersion = VersionMajor+"."+VersionMinor+"."+VersionRevision;

	public static final String ModDependencies = "required-after:tfc2_coremod;after:harvestcraft";
	public static final String ModChannel = "TFC2";
	public static final String SERVER_PROXY_CLASS = "com.bioxx.tfc2.CommonProxy";
	public static final String CLIENT_PROXY_CLASS = "com.bioxx.tfc2.ClientProxy";

	public static final String AssetPath = "/assets/" + ModID + "/";
	public static final String AssetPathGui = "textures/gui/";

	public static String getResID()
	{
		return ModID + ":";
	}
}
