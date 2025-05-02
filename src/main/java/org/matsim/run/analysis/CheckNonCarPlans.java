package org.matsim.run.analysis;


import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.population.PopulationUtils;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.core.router.TripStructureUtils;

import java.io.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Check the number of non car plans.
 */

final class CheckNonCarPlans {
	public static final Logger LOG = LogManager.getLogger(CheckNonCarPlans.class);

	private CheckNonCarPlans() {
		// not called
	}

	public static void main(String[] args) {
		// Import 50, 25, 10, 5 and 1 and check Sizes and the number of Agents with non Car Plans
		// and number of Non Car Plans
		double[] sampleSizes = {100.0, 50.0, 25.0, 10.0, 5.0, 1.0};
		for (double sampleSize : sampleSizes) {
			String outputPath = "/net/ils/mersini/output/CheckNonCarPlans/";
			String inputPath1 = "/net/ils/mersini/input/v2024.2/lausitz-v2024.2-";
			String inputPath2 = "-pct-plans.xml.gz";
			if (sampleSize == 10.0 || sampleSize == 5.0 || sampleSize == 1.0) {
				for (int sample_nr = 1; sample_nr < 11; sample_nr++) {
					// Path to Population
					String outputPathFile = outputPath + "-" + sampleSize + "-pct-plans-" + sample_nr + "-AgentsWithNonCarPlans.csv";
					String pathToSampledPopulation = inputPath1 + sampleSize + "-pct-plans-" + sample_nr + ".xml.gz";
					countNumberOfNonCarPlans(pathToSampledPopulation, outputPathFile);

				}
			} else if (sampleSize == 25.0) {
				// import regular 25 pct
				String pathToSampledPopulation = inputPath1 + sampleSize + inputPath2;
				String outputPathFile1 = outputPath + "-" + sampleSize + "-pct-plans-AgentsWithNonCarPlans.csv";
				countNumberOfNonCarPlans(pathToSampledPopulation, outputPathFile1);
				// import doubled 25 pct file
				String outputPathFile2 = outputPath + "-" + sampleSize + "-pct-plans-doubled-AgentsWithNonCarPlans.csv";
				String pathTo25PctDoubled = inputPath1 + sampleSize + "-pct-plans-doubled.xml.gz";
				countNumberOfNonCarPlans(pathTo25PctDoubled, outputPathFile2);
				// import quadrupled 25 pct file
				String outputPathFile3 = outputPath + "-" + sampleSize + "-pct-plans-quadrupled-AgentsWithNonCarPlans.csv";
				String pathTo25PctQuadrupled = inputPath1 + sampleSize + "-pct-plans-quadrupled.xml.gz";
				countNumberOfNonCarPlans(pathTo25PctQuadrupled, outputPathFile3);
			} else {
				// import plans file
				String outputPathFile = outputPath + "-" + sampleSize + "-pct-plans-AgentsWithNonCarPlans.csv";
				String pathToSampledPopulation = inputPath1 + sampleSize + inputPath2;
				countNumberOfNonCarPlans(pathToSampledPopulation, outputPathFile);

			}

		}


	}

	private static void countNumberOfNonCarPlans(String pathToPlans, String outputPathFile) {
		Population population = PopulationUtils.readPopulation(pathToPlans);
		List<Id<Person>> numberOfNonCarPlans = new ArrayList<>();
		List<Id<Person>> numberOfAgentsWithNonCarPlans = new ArrayList<>();
		for (Person person : population.getPersons().values()) {
			for (Plan plan : person.getPlans()) {
				boolean containsCarLeg = false;
				for (Leg leg : TripStructureUtils.getLegs(plan)) {
					if (TransportMode.car.equals(leg.getMode())) {
						containsCarLeg = true;
						break;
					}
				}
				if (!containsCarLeg) {
					numberOfNonCarPlans.add(person.getId());
				}
				if (!numberOfAgentsWithNonCarPlans.contains(person.getId()) && !containsCarLeg)
					numberOfAgentsWithNonCarPlans.add(person.getId());
			}

		}
		int nNonCarPlans = numberOfNonCarPlans.size();
		int nAgents = population.getPersons().size();
		int nAgentsWithNonCarPlans = numberOfAgentsWithNonCarPlans.size();
		double ratio = (double) nAgentsWithNonCarPlans / (double) nAgents;
		LOG.info("Number of Agents in Population: {}", nAgents);
		LOG.info("Number of Agents with non Car Plans: {}", nAgentsWithNonCarPlans);
		LOG.info("Number of Non Car Plans: {}", nNonCarPlans);
		LOG.info("Ratio of Agents with non Car Plans to Agents in Population: {}", ratio);
		try {
			BufferedWriter file = new BufferedWriter(new FileWriter(outputPathFile));
			file.write("Id\n");
			for (Id<Person> personId : numberOfNonCarPlans) {
				file.write(personId.toString() + "\n");

			}
			file.close();
		} catch (IOException exep) {
			LOG.info("could not create csv file");
		}
	}


}



