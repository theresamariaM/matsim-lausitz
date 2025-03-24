package org.matsim.run.analysis;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.router.TripStructureUtils;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Check the sizes of the original population reduced to car users and the created 100 pct car user plans.
 */


final class CheckPopulationSizesAndScores {
	private static final Logger log = LogManager.getLogger(CheckPopulationSizesAndScores.class);

	private CheckPopulationSizesAndScores() {
		// not called
	}

	public static void main(String[] args) {
		// import original 100 pct plans
		String pathToPopulation = "https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/lausitz/lausitz-v2024.2/output/100pct/lausitz-v2024.2-100pct-base-case.output_plans.xml.gz";
		Population population = PopulationUtils.readPopulation(pathToPopulation);
		int populationSize = population.getPersons().size();
		log.info("Size of lausitz-v2024.2-100pct-base-case.output_plans.xml.gz (not reduced to only car users): {}", populationSize);
		double avg0 = calculateAverageScoreOfSelectedPlan(population);
		log.info(" Average scores of selected Plans of lausitz-v2024.2-100pct-base-case.output_plans.xml.gz: {}", avg0);

		// reduce to car users only
		List<Id<Person>> nonCarUsers = new ArrayList<>();
		List<Id<Person>> notAPerson = new ArrayList<>();

		for (Person person : population.getPersons().values()) {
			if (!person.getAttributes().getAttribute("subpopulation").equals("person")) {
				notAPerson.add(person.getId());
			}
			Plan plan = person.getSelectedPlan();
			boolean containsCarLeg = false;

			for (Leg leg : TripStructureUtils.getLegs(plan)) {
				if (TransportMode.car.equals(leg.getMode())) {
					containsCarLeg = true;
					break;
				}
			}

			if (!containsCarLeg) {
				nonCarUsers.add(person.getId());
			}

		}

		for (Id<Person> personId : nonCarUsers) {
			population.removePerson(personId);
		}

		for (Id<Person> personId : notAPerson) {
			population.removePerson(personId);
		}
		int newPopulationSize = population.getPersons().size();
		log.info("Size of lausitz-v2024.2-100pct-base-case.output_plans.xml.gz reduced to car users only: {} ", newPopulationSize);

		// calculate average score of selected Plans
		double avg = calculateAverageScoreOfSelectedPlan(population);
		log.info(" Average scores of selected Plans of 100 pct Car Users only (from Output plans reduced to car users only): {}", avg);

		// calculate average of average score
		double avgOfAvgOP = calculateAvgOfAvgOfPersPlans(population);
		log.info(" Average scores of average Scores of Plans  100 pct Car Users only (from Output plans reduced to car users only): {}", avgOfAvgOP);


		// Import created 100 pct plans
		String pathToCarPopulation = "/net/ils/mersini/input/v2024.2/lausitz-v2024.2-100.0-pct-plans.xml.gz";
		Population populationCarUsers = PopulationUtils.readPopulation(pathToCarPopulation);
		int populationSizeCarUsers = populationCarUsers.getPersons().size();
		log.info(" Size of Population 100 Pct Car Users: {}", populationSizeCarUsers);

		// calculate average score
		double avg2 = calculateAverageScoreOfSelectedPlan(populationCarUsers);
		log.info(" Average scores of selected Plan of created 100 pct plans (from *100-pct-plans.xml.gz): {}", avg2);

		// calculate average of average score
		double avgOfAvg = calculateAvgOfAvgOfPersPlans(populationCarUsers);
		log.info(" Average scores of average Scores of Plans of created 100 pct plans (from *100-pct-plans.xml.gz): {}", avgOfAvg);

		// Import 50, 25, 10, 5 and 1 and check Sizes and calculate their average Score
		double[] sampleSizes = {100.0, 50.0, 25.0, 10.0, 5.0, 1.0};
		for (double sampleSize : sampleSizes) {
			String inputPath1 = "/net/ils/mersini/input/v2024.2/lausitz-v2024.2-";
			String inputPath2 = "-pct-plans.xml.gz";
			if (sampleSize == 10.0 || sampleSize == 5.0 || sampleSize == 1.0) {
				for (int sample_nr = 1; sample_nr < 11; sample_nr++) {
					// Path to Population
					String pathToSampledPopulation = inputPath1 + sampleSize + "-pct-plans-" + sample_nr + ".xml.gz";
					writeSizeAndAverageOfSelPlanToLog(pathToSampledPopulation, sampleSize);

				}
			} else if (sampleSize == 25.0) {
				// import regular 25 pct
				String pathToSampledPopulation = inputPath1 + sampleSize + inputPath2;
				writeSizeAndAverageOfSelPlanToLog(pathToSampledPopulation, sampleSize);

				// import doubled 25 pct file
				String pathTo25PctDoubled = inputPath1 + sampleSize + "-pct-plans-doubled.xml.gz";
				writeSizeAndAverageOfSelPlanToLog(pathTo25PctDoubled, sampleSize);

				// import quadrupled 25 pct file
				String pathTo25PctQuadrupled = inputPath1 + sampleSize + "-pct-plans-quadrupled.xml.gz";
				writeSizeAndAverageOfSelPlanToLog(pathTo25PctQuadrupled, sampleSize);
			} else {
				// import plans file
				String pathToSampledPopulation = inputPath1 + sampleSize + inputPath2;
				writeSizeAndAverageOfSelPlanToLog(pathToSampledPopulation, sampleSize);

			}

		}


	}

	private static void writeSizeAndAverageOfSelPlanToLog(String pathToPopulation, double sampleSize) {
		Population pop = PopulationUtils.readPopulation(pathToPopulation);
		int popSize = pop.getPersons().size();
		log.info("Population Size: {} of  Sample Size {}", popSize, sampleSize);
		double avgOfSelPlan = calculateAverageScoreOfSelectedPlan(pop);
		log.info("Average of Selected Plans: {}", avgOfSelPlan);
	}


	private static double calculateAverageScoreOfSelectedPlan(Population population) {
		ArrayList<Double> scores = new ArrayList<>();
		int number_of_empty_plans = 0;
		for (Person person : population.getPersons().values()) {
			try {
				double score = person.getSelectedPlan().getScore();
				scores.add(score);
			} catch (NullPointerException e) {

				// System.out.print("NullPointerException Caught");
				number_of_empty_plans += 1;
			}

		}
		double avg = calculateAverage(scores);
		return avg;

	}

	private static double calculateAvgOfAvgOfPersPlans(Population population) {
		ArrayList<Double> avgScores = new ArrayList<>();
		int number_of_empty_plansIt0 = 0;
		for (Person person : population.getPersons().values()) {
			try {
				ArrayList<Double> scoresPerson = new ArrayList<>();
				for (Plan plan : person.getPlans()) {
					scoresPerson.add(plan.getScore());
				}
				double avgPerson = calculateAverage(scoresPerson);
				avgScores.add(avgPerson);
			} catch (NullPointerException e) {
				number_of_empty_plansIt0 += 1;
			}


		}
		double avgOfAvg = calculateAverage(avgScores);
		return avgOfAvg;

	}

	private static double calculateAverage(ArrayList<Double> scores) {
		double sum = 0;

		for (double score : scores) {
			sum += score;
		}
		double avg = (sum / scores.size());
		return avg;


	}

}
