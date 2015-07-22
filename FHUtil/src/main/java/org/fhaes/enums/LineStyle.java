package org.fhaes.enums;

/**
 * LineStyle Enum.
 */
public enum LineStyle {
	SOLID(1, 0), DOTTED(1, 1), DASHED(2, 3), LONG_DASH(5, 7);

	double firstCode = 1;
	double secondCode = 0;

	LineStyle(double firstCode, double secondCode) {

		this.firstCode = firstCode;
		this.secondCode = secondCode;
	}

	public String getCode() {

		return firstCode + "," + secondCode;
	}

	public String getCode(int scale) {

		return (firstCode * scale) + "," + (secondCode * scale);
	}

	public String getCodeForChartYearCount(int yrs) {

		Double scale = (yrs / 500.0);

		// Double scale = 1.0 / pxperyear;
		/*
		 * if (yrs <= 50) { scale = 0.05; } else if (yrs <= 100) { scale = 0.1; } else if (yrs <= 200) { scale = 0.2; } else if (yrs <= 300)
		 * { scale = 0.4; } else if (yrs <= 400) { scale = 0.6; } else if (yrs <= 700) { scale = 1.0; } else if (yrs <= 1000) { scale = 2.0;
		 * } else if (yrs <= 1500) { scale = 3.0; } else if (yrs >= 1500) { scale = 4.0; }
		 */
		return (firstCode * scale) + "," + (secondCode * scale);
	}

	public static LineStyle fromString(String str) {

		if (str.equals(LineStyle.SOLID.toString()))
		{
			return LineStyle.SOLID;
		}
		else if (str.equals(LineStyle.DOTTED.toString()))
		{
			return LineStyle.DOTTED;
		}
		else if (str.equals(LineStyle.DASHED.toString()))
		{
			return LineStyle.DASHED;
		}
		else
		{
			return LineStyle.SOLID;
		}
	}

}
