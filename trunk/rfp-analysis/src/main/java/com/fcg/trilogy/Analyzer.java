package com.fcg.trilogy;

import java.io.File;

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
}
