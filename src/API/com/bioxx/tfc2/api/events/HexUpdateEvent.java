package com.bioxx.tfc2.api.events;

import net.minecraftforge.fml.common.eventhandler.Event;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.graph.Center;

/**
 * This is called right after the parameters have been created and before the island itself is generated.<br>
 * <br>
 * {@link #map} is the set of island parameters that has been chosen for this island before generation.<br>
 * {@link #centerToUpdate} is the set of island parameters that has been chosen for this island before generation.<br>
 * <br>
 * This event is fired on the {@link com.bioxx.tfc2.api.Global#EVENT_BUS}.<br>
 * @author Bioxx
 *
 */
public class HexUpdateEvent extends Event
{
	public final IslandMap map;
	public final Center centerToUpdate;

	public HexUpdateEvent(IslandMap map, Center centerToUpdate)
	{
		this.map = map;
		this.centerToUpdate = centerToUpdate;
	}

}
