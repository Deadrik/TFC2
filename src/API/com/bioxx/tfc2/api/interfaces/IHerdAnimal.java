package com.bioxx.tfc2.api.interfaces;

import java.util.UUID;

public interface IHerdAnimal 
{
	public UUID getHerdUUID();
	public void setHerdUUID(UUID id);

	public IAnimalDef getAnimalDef();
	public void setAnimalDef(IAnimalDef def);
}
