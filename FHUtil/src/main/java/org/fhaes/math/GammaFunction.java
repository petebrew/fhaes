package org.fhaes.math;

/*******************************************************************************
 * Copyright (C) 2013 Elena Velasquez and Peter Brewer
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 ******************************************************************************/

/**
 * GammaFunction Class. Implementation of Gamma function because it is not available in Apache Commons.
 * 
 * @author Elena
 */
public class GammaFunction {

	/**
	 * TODO
	 * 
	 * @param x
	 * @return
	 */
	public double st_gamma(double x) {

		return Math.sqrt(2 * Math.PI / x) * Math.pow((x / Math.E), x);
	}

	/**
	 * TODO
	 * 
	 * @param x
	 * @return
	 */
	public double la_gamma(double x) {

		double[] p = { 0.99999999999980993, 676.5203681218851, -1259.1392167224028, 771.32342877765313, -176.61502916214059,
				12.507343278686905, -0.13857109526572012, 9.9843695780195716e-6, 1.5056327351493116e-7 };

		int g = 7;

		if (x < 0.5)
		{
			return Math.PI / (Math.sin(Math.PI * x) * la_gamma(1 - x));
		}

		x -= 1;
		double a = p[0];
		double t = x + g + 0.5;

		for (int i = 1; i < p.length; i++)
		{
			a += p[i] / (x + i);
		}

		return Math.sqrt(2 * Math.PI) * Math.pow(t, x + 0.5) * Math.exp(-t) * a;
	}
}
