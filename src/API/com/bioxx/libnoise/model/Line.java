/* Copyright (C) 2011 Garrett Fleenor

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation; either version 3.0 of the License, or (at
 your option) any later version.

 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 License (COPYING.txt) for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 This is a port of libnoise ( http://libnoise.sourceforge.net/index.html ).  Original implementation by Jason Bevins

 */

package com.bioxx.libnoise.model;

import com.bioxx.libnoise.exception.NoModuleException;
import com.bioxx.libnoise.module.Module;

public class Line {
	// A flag that specifies whether the value is to be attenuated
	// (moved toward 0.0) as the ends of the line segment are approached.
	public boolean attenuate = true;

	// A pointer to the noise module used to generate the output values.
	Module module;

	/**
	 * @param module The noise module that is used to generate the output
	 *            values.
	 */
	public Line(Module module) {
		if (module == null)
			throw new IllegalArgumentException("module cannot be null");
		this.module = module;
	}

	/**
	 * Returns the noise module that is used to generate the output values.
	 * 
	 * @returns A reference to the noise module.
	 * @pre A noise module was passed to the SetModule() method.
	 */
	public Module getModule() {
		return module;
	}

	/**
	 * Sets the noise module that is used to generate the output values.
	 * 
	 * @param module The noise module that is used to generate the output
	 *            values.
	 * 
	 *            This noise module must exist for the lifetime of this object,
	 *            until you pass a new noise module to this method.
	 */
	public void setModule(Module module) {
		if (module == null)
			throw new IllegalArgumentException("module cannot be null");
		this.module = module;
	}

	/**
	 * Returns the output value from the noise module given the one-dimensional
	 * coordinate of the specified input value located on the line
	 * 
	 * @param p The distance along the line segment (ranges from 0.0 to 1.0)
	 * @return The output value from the noise module.
	 * 
	 * */
	public double getValue(double p) 
	{
		if (module == null)
			throw new NoModuleException();

		double value = module.GetValue(p, 0, 0);

		if (attenuate)
		{
			return p * (1.0 - p) * 4 * value;
		}
		else
		{
			return value;
		}
	}
}
