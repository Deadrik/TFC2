package com.bioxx.tfc2.api.events;

import net.minecraftforge.fml.common.eventhandler.Event;

import com.bioxx.jmapgen.IslandMap;

/**
 * This is called once per tick, per currently cached island.<br>
 * <br>
 * {@link #map} is the island beign ticked<br>
 * {@link #centerToUpdate} is the set of island parameters that has been chosen for this island before generation.<br>
 * <br>
 * This event is fired on the {@link com.bioxx.tfc2.api.Global#EVENT_BUS}.<br>
 * @author Bioxx
 *
 */
public class IslandUpdateEvent extends Event
{
	public final IslandMap map;

	public IslandUpdateEvent(IslandMap map)
	{
		this.map = map;
	}

}
