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

import java.util.ArrayList;
import java.util.List;
//import java.util.stream.Stream;
//import java.util.stream.Collectors;


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
			String inputPath1 = "/net/ils/mersini/input/v2024.2/lausitz-v2024.2-";
			String inputPath2 = "-pct-plans.xml.gz";
			if (sampleSize == 10.0 || sampleSize == 5.0 || sampleSize == 1.0) {
				for (int sample_nr = 1; sample_nr < 11; sample_nr++) {
					// Path to Population
					String pathToSampledPopulation = inputPath1 + sampleSize + "-pct-plans-" + sample_nr + ".xml.gz";
					countNumberOfNonCarPlans(pathToSampledPopulation);

				}
			} else if (sampleSize == 25.0) {
				// import regular 25 pct
				String pathToSampledPopulation = inputPath1 + sampleSize + inputPath2;
				countNumberOfNonCarPlans(pathToSampledPopulation);
				// import doubled 25 pct file
				String pathTo25PctDoubled = inputPath1 + sampleSize + "-pct-plans-doubled.xml.gz";
				countNumberOfNonCarPlans(pathTo25PctDoubled);
				// import quadrupled 25 pct file
				String pathTo25PctQuadrupled = inputPath1 + sampleSize + "-pct-plans-quadrupled.xml.gz";
				countNumberOfNonCarPlans(pathTo25PctQuadrupled);
			} else {
				// import plans file
				String pathToSampledPopulation = inputPath1 + sampleSize + inputPath2;
				countNumberOfNonCarPlans(pathToSampledPopulation);

			}

		}


	}

	private static void countNumberOfNonCarPlans(String pathToPlans) {
		Population population = PopulationUtils.readPopulation(pathToPlans);
		List<Id<Person>> numberOfNonCarPlans = new ArrayList<>();
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
			}

		}
		int nNonCarPlans = numberOfNonCarPlans.size();
		int nAgents = population.getPersons().size();
		List<Id<Person>> numberOfAgentsWithNonCarPlans = numberOfNonCarPlans.stream().distinct().toList();
		int nAgentsWithNonCarPlans = numberOfAgentsWithNonCarPlans.size();
		float ratio = nAgentsWithNonCarPlans / nAgents;
		LOG.info("Number of Agents in Population: {}", nAgents);
		LOG.info("Number of Agents with non Car Plans: {}", nAgentsWithNonCarPlans);
		LOG.info("Number of Non Car Plans: {}", nNonCarPlans);
		LOG.info("Ratio of Agents with non Car Plans to Agents in Population: {}", ratio);

	}


}
