package com.fcg.trilogy;

import java.io.File;
import java.util.Iterator;

import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.Forecaster;
import net.sourceforge.openforecast.ForecastingModel;
import net.sourceforge.openforecast.Observation;

import com.fcg.trilogy.model.RFPManager;

/**
 * Main class for analyzing RFP numbers every week.
 *
 * @author tha
 */
public class Analyzer {

	public static void main(String[] args) throws Exception {

		String inputDirectory = "\\\\trilogy-svr\\Trilogy";
		RFPManager manager = new RFPManager();
		InputLoader.load(new File(inputDirectory), manager);

		ModelAnalyzer.analyze(manager);
	}

	public static void main2(String[] args) {
		DataSet ds = new DataSet();

		// Create Observation for quarter 1
		Observation observationQ1 = new Observation(500.0);
		observationQ1.setIndependentValue("quarter", 1);
		ds.add(observationQ1);

		// Create Observation for quarter 2
		Observation observationQ2 = new Observation(600.0);
		observationQ2.setIndependentValue("quarter", 2);
		ds.add(observationQ2);

		// Create Observation for quarter 3
		Observation observationQ3 = new Observation(700.0);
		observationQ3.setIndependentValue("quarter", 3);
		ds.add(observationQ3);

		ForecastingModel model = Forecaster.getBestForecast(ds);
		model.init(ds);

		// Create DataPoint/Observation for quarter 4
		DataPoint fcDataPointQ4 = new Observation(0.0);
		fcDataPointQ4.setIndependentValue("quarter", 4);

		// Create Observation/DataPoint for quarter 5
		DataPoint fcDataPointQ5 = new Observation(0.0);
		fcDataPointQ5.setIndependentValue("quarter", 5);

		// Create forecast data set and add these DataPoints
		DataSet fcDataSet = new DataSet();
		fcDataSet.add(fcDataPointQ4);
		fcDataSet.add(fcDataPointQ5);

		model.forecast(fcDataSet);

		// After calling model.forecast, our fcDataSet now contains
		// forecast data points
		Iterator it = fcDataSet.iterator();
		while (it.hasNext()) {
			DataPoint dp = (DataPoint) it.next();
			double forecastValue = dp.getDependentValue();

			// Do something with the forecast value, e.g.
			System.out.println(dp);
		}
	}
}
