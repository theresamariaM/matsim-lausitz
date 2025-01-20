package org.matsim.run.prepare;

//import org.apache.logging.log4j.LogManager;
// import org.apache.logging.log4j.Logger;

import org.matsim.api.core.v01.population.Population;
import org.matsim.core.population.PopulationUtils;


/**
 * Create population samples.
 */
final class CreatePopulationSamples {
	private CreatePopulationSamples() {
		// not called
	}

	//private static final Logger log = LogManager.getLogger(CreatePopulationSamples.class);
	public static void main(String[] args) {


		String outputFilePopulationPart1 = "./input/v2024.2/lausitz-v2024.2-";
		String outputFilePopulationPart3 = "-pct-plans";
		String outputFilePopulationPart5 = ".xml.gz";
		//double Sample = 1.0;
		double[] sampleSizes = {0.01, 0.05, 0.1, 0.2, 0.5};
		for (int index = 0; index < sampleSizes.length; index++) {
			double sampleSize = sampleSizes[index];
			// create 10 samples of the following sizes: 0.01, 0.05, 0.1
			if (sampleSize == 0.01 || sampleSize == 0.05 || sampleSize == 0.1) {
				for (int i = 1; i <= 10; i++) {

					String pathToPopulation = "./input/v2024.2/lausitz-v2024.2-100-pct-plans.xml.gz";
					Population population = PopulationUtils.readPopulation(pathToPopulation);
					PopulationUtils.sampleDown(population, sampleSize);
					int sampleSizeInPct = (int) (sampleSize * 100);
					String sampleSizeString = Double.toString(sampleSizeInPct);
					String space = "-";
					String versionNr = Integer.toString(i);
					String outputFilePopulation = outputFilePopulationPart1 + sampleSizeString + outputFilePopulationPart3 + space + versionNr + outputFilePopulationPart5;

					// log.info("Writing {} sample to {}", sampleSize, outputFilePopulation);
					PopulationUtils.writePopulation(population, outputFilePopulation);
				}
			} else {
				// create the other samples, 0.2, 0.5
				int sampleSizeInPct = (int) (sampleSize * 100);
				String sampleSizeString = Double.toString(sampleSizeInPct);
				String outputFilePopulation = outputFilePopulationPart1 + sampleSizeString + outputFilePopulationPart3 + outputFilePopulationPart5;
				String pathToPopulation = "./input/v2024.2/lausitz-v2024.2-100-pct-plans.xml.gz";
				Population population = PopulationUtils.readPopulation(pathToPopulation);
				PopulationUtils.sampleDown(population, sampleSize);
				// log.info("Writing {} sample to {}", sampleSize, outputFilePopulation);
				PopulationUtils.writePopulation(population, outputFilePopulation);
			}
		}

	}
}
