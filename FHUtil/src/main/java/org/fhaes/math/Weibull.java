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

import java.util.ArrayList;

/**
 * Weibull Class. Class for calculating various statistical measured based on the Weibull distribution.
 * 
 * @author Elena Velasquez
 */
public class Weibull {

	private static double[] wparam;

	/**
	 * TODO
	 * 
	 * @param data
	 */
	public Weibull(ArrayList<Double> data) {

		wparam = Weibull.generateParameters(data);
	}

	/**
	 * TODO
	 * 
	 * @param data
	 * @return
	 */
	private static double[] generateParameters(ArrayList<Double> data) {

		//
		// Initialization of local variables.
		//
		Double[] Ddatap;
		double[] datap;
		double[] wparam;
		double tolerance = 0.0001;
		double mnew = 0.0;
		int maxitr = 50;
		int niter = 1;
		double m = 1.0;
		double x0 = 0.0;
		double lgxi = 0;
		double slnx = 0;
		double tt = Math.abs(m - mnew);

		wparam = new double[2];
		//
		// loading of the data: converting the ArrayList into an Array
		//
		Ddatap = new Double[data.size()];
		Ddatap = data.toArray(Ddatap);
		datap = new double[data.size()];
		for (int k = 0; k < Ddatap.length; k++)
		{
			datap[k] = Ddatap[k].doubleValue();

		}
		// log.debug("here is the data : "+ datap);
		//
		// Beging calculation of all the terms in the equations to be solve
		//
		for (int j = 0; j < datap.length; j++)
		{
			lgxi = Math.log(datap[j]);
			// log.debug("here is the log of each data point: " +
			// lgxi);
			slnx = slnx + lgxi;
		}
		//
		// Begin the iteration of m(k+1)=m(k)-f(x)/f'(x)
		//
		double denom = 0.05;
		while ((tt > tolerance) && (niter <= maxitr) && (denom > 0.01))
		{
			double sxm = 0.0;
			double sxmlnx = 0.0;
			double sxmlnx2 = 0.0;
			double xitom = 0.0;

			for (int i = 0; i < datap.length; i++)
			{
				xitom = Math.pow(datap[i], m);
				lgxi = Math.log(datap[i]);
				sxm = sxm + xitom;
				sxmlnx = sxmlnx + xitom * lgxi;
				sxmlnx2 = sxmlnx2 + (xitom * lgxi * lgxi);
			}
			double numera = (1 / m) + (slnx / datap.length) - (sxmlnx / sxm);
			denom = (1 / (m * m)) + (((sxm * sxmlnx2) - (sxmlnx * sxmlnx)) / (sxm * sxm));
			mnew = m + (numera / denom);
			//
			// Calculate Scale parameter x0
			//
			x0 = Math.pow((sxm / datap.length), 1 / m);
			//
			// Update all the values for the next iteration.
			//
			tt = Math.abs(m - mnew);
			m = mnew;
			niter = niter + 1;
		} // end of the while loop for newtons
			//
			// The 2-parameter weibull distribution calculated parameter are:
			// Shape parameter = mnew = shapepar
			// Scale parameter = x0 = scalepar

		wparam[0] = m;
		wparam[1] = x0;

		return wparam;
	}

	/**
	 * Calculation of the Weibull maximum hazard interval.
	 * 
	 * @param wparam
	 * @return
	 */
	public double getMaximumHazardInterval() {

		double mhibase = 1.0;
		double mhiex = 0.0;
		double mhi = 0.0;
		double shape = wparam[0];
		double scale = wparam[1];

		if (shape > 1.005)
		{
			mhibase = (0.5) * Math.exp(shape * Math.log10(scale) / shape);
			mhiex = 1.0 / (shape - 1.0);
			mhi = Math.pow(mhibase, mhiex);
		}
		else
		{
			mhi = -99;
		}

		return mhi;
	}

	/**
	 * TODO Elena needs to check this!
	 * 
	 * @param percentile
	 * @return
	 */
	public double getExceedencePercentile(Double percentile) {

		double val = percentile / 100;
		return Math.exp(Math.log(-1.0 * Math.log(1.0 - val)) / getShape() + Math.log(getScale()));
	}

	/**
	 * Calculate the lower and upper exceedence intervals.
	 * 
	 * @param wparam
	 * @return
	 */
	public double[] getExceedenceProbability() {

		// boolean hflag =true;
		// double uei=0.0;
		// double lei=0.0;
		// double tolerance=0.125;
		double shape = wparam[0];
		double scale = wparam[1];
		double[] fixval = { 0.001, 0.01, 0.025, 0.05, 0.1, 0.125, 0.2, 0.25, 0.3, 0.333, 0.5, 0.667, 0.7, 0.75, 0.8, 0.875, 0.9, 0.95,
				0.975, 0.99, 0.999 };
		double[] excprb = new double[fixval.length];
		// double[] lowupint = new double[2];
		//
		// Calculation of the lower and upper excident interval
		//
		for (int k = 0; k < fixval.length; k++)
		{
			excprb[k] = Math.exp(Math.log(-1.0 * Math.log(1.0 - fixval[k])) / shape + Math.log(scale));
			// log.debug("luei"+excprb[k]);
			// temp=Math.exp((Math.log(-1.0*Math.log(1.0-fixval[k]))/shape)+Math.log(scale));
			// if(fixval[k]<=tolerance){lei=temp;
			// log.debug("lei  "+excprb[k]);
			// }
			// if(fixval[k]>=(1.0-tolerance)){
			// if(hflag){
			// uei=temp;
			// hflag=false;
			// log.debug("uei  "+excprb[k]);
			// }
			// }
		}

		// lowupint[0]=lei;
		// lowupint[1]=uei;

		return excprb;
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	public double[] getExceedenceProbability2() {

		boolean hflag = true;
		double uei = 0.0;
		double lei = 0.0;
		double tolerance = 0.125;
		double shape = wparam[0];
		double scale = wparam[1];
		double[] fixval = { 0.001, 0.01, 0.025, 0.05, 0.1, 0.125, 0.2, 0.25, 0.3, 0.333, 0.5, 0.667, 0.7, 0.75, 0.8, 0.875, 0.9, 0.95,
				0.975, 0.99, 0.999 };
		double[] excprb = new double[fixval.length];
		double[] lowupint = new double[2];
		double temp = 0.0;
		//
		// Calculation of the lower and upper excident interval
		//
		for (int k = 0; k < fixval.length; k++)
		{
			excprb[k] = Math.exp(Math.log(-1.0 * Math.log(1.0 - fixval[k])) / shape + Math.log(scale));
			// log.debug("luei"+excprb[k]);
			temp = Math.exp((Math.log(-1.0 * Math.log(1.0 - fixval[k])) / shape) + Math.log(scale));
			if (fixval[k] <= tolerance)
			{
				lei = temp;
				// log.debug("lei  "+excprb[k]);
			}
			if (fixval[k] >= (1.0 - tolerance))
			{
				if (hflag)
				{
					uei = temp;
					hflag = false;
					// log.debug("uei  "+excprb[k]);
				}
			}
		}

		lowupint[0] = lei;
		lowupint[1] = uei;

		return lowupint;
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	public double getShape() {

		return wparam[0];
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	public double getScale() {

		return wparam[1];
	}

	/**
	 * Calculate the Weibull mean.
	 * 
	 * @param wparam
	 * @return
	 */
	public double getMean() {

		double xmu = 0.0;
		double wmean = 0;
		double shape = wparam[0];
		double scale = wparam[1];
		//
		// Calculation of the weibull mean
		//
		GammaFunction test = new GammaFunction();

		xmu = 1.0 + (1.0 / shape);
		// log.debug("the gamma functionof 5 is 4 factorial "+test.la_gamma(5));
		wmean = scale * test.la_gamma(xmu);

		return wmean;
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	public double getSigma() {

		double xmu = 0.0;
		double ymu = 0.0;
		double shape = wparam[0];
		double scale = wparam[1];

		//
		// Calculation of the Weibull Sigma
		//
		GammaFunction test = new GammaFunction();
		xmu = 1 + (1 / shape);
		ymu = 1 + (2 / shape);
		double xsig = test.la_gamma(ymu) - (test.la_gamma(xmu) * test.la_gamma(xmu));
		double wsigma = scale * Math.sqrt(xsig);

		return wsigma;
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	public double getSkew() {

		double xmu = 0.0;
		// double ymu=0.0;
		double wskew = 0.0;
		double shape = wparam[0];
		double scale = wparam[1];
		//
		// Calculation of the weibull mean
		//
		GammaFunction test = new GammaFunction();

		xmu = 1.0 + (3.0 / shape);
		double numskew = (test.la_gamma(xmu) * Math.pow(scale, 3.0) - (3.0 * getMean() * getSigma() * getSigma()) - (getMean() * getMean() * getMean()));

		// log.debug("the gamma functionof 5 is 4 factorial "+test.la_gamma(1.5));
		wskew = numskew / (Math.pow(getSigma(), 3.0));

		return wskew;
	}

	/**
	 * Calculate the Weibull mode.
	 * 
	 * @return
	 */
	public double getMode() {

		double wmode = 0.0;
		double xmod = 0.0;
		double shape = wparam[0];
		double scale = wparam[1];

		//
		// Calculation of the weibull mode
		//
		if (shape > 1)
		{
			xmod = (shape - 1) / shape;
			wmode = scale * (Math.pow(xmod, (1.0 / shape)));
		}
		else if (shape == 1)
		{
			wmode = 0;
		}
		else
		{
			// Invalid
			wmode = -99;
		}

		return wmode;
	}

	/**
	 * Calculate the Weibull median.
	 * 
	 * @param wparam
	 * @return
	 */
	public double getMedian() {

		double wmed = 0.0;
		double shape = wparam[0];
		double scale = wparam[1];

		wmed = scale * Math.pow(Math.log(2.0), (1.0 / shape));
		return wmed;
	}

	/**
	 * Calculate Weibull probability.
	 * 
	 * @param data
	 * @return probability = Math.exp(-1*Math.pow((data.get(i)/scale),shape)))
	 */
	public ArrayList<Double> getWeibullProbability(ArrayList<Double> data) {

		ArrayList<Double> weibullProb = new ArrayList<Double>();
		double shape = wparam[0];
		double scale = wparam[1];

		for (int i = 0; i < data.size() - 1; i++)
		{
			weibullProb.add(Math.exp(-1 * Math.pow((data.get(i) / scale), shape)));
		}

		return weibullProb;
	}
}
