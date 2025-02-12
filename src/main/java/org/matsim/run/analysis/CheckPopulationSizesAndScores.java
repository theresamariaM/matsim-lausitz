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
		String pathToPopulation = "/net/ils/mersini/input/v2024.2/010.output_plans.xml.gz";
		Population population = PopulationUtils.readPopulation(pathToPopulation);
		int populationSize = population.getPersons().size();
		log.info("Size of Output_plans with all agents (not reduced to only car users): {}", populationSize);


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
		log.info(" Size of Car Users only of Output Plans: {} ", newPopulationSize);


		// calculate average score of selected Plans
		double avg = calculateAverageScoreOfSelectedPlan(population);
		log.info(" Average scores of selected Plans of 100 pct Car Users only (from Output plans reduced to car users only): {}", avg);

		// calculate average of average score
		double avgOfAvgOP = calculateAvgOfAvgOfPersPlans(population);
		log.info(" Average scores of average Scores of Plans  100 pct Car Users only (from Output plans reduced to car users only): {}", avgOfAvgOP);


		// Import created 100 pct plans
		String pathToCarPopulation = "/net/ils/mersini/input/v2024.2/lausitz-v2024.2-100-pct-plans.xml.gz";
		Population populationCarUsers = PopulationUtils.readPopulation(pathToCarPopulation);
		int populationSizeCarUsers = populationCarUsers.getPersons().size();
		log.info(" Size of Population 100 Pct Car Users: {}", populationSizeCarUsers);


		// calculate average score
		double avg2 = calculateAverageScoreOfSelectedPlan(populationCarUsers);
		log.info(" Average scores of selected Plan of created 100 pct plans (from *100-pct-plans.xml.gz): {}", avg2);

		// calculate average of average score
		double avgOfAvg = calculateAvgOfAvgOfPersPlans(populationCarUsers);
		log.info(" Average scores of average Scores of Plans of created 100 pct plans (from *100-pct-plans.xml.gz): {}", avgOfAvg);

		// Calculate average of experienced plans
		String pathToPlansIt0Exp = "/net/ils/mersini/output/output-lausitz-100pct/ITERS/it.0/lausitz-100pct.0.experienced_plans.xml.gz";
		Population pop0Exp = PopulationUtils.readPopulation(pathToPlansIt0Exp);
		int pop0ExpSize = pop0Exp.getPersons().size();
		log.info("Size of it.0/lausitz-100pct.0.experienced_plans.xml.gz: {}", pop0ExpSize);

		int number_of_empty_plansIt0Exp1 = 0;
		ArrayList<Double> scoresExpPlans1 = new ArrayList<>();
		for (Person person : pop0Exp.getPersons().values()) {
			try {
				Plan plan = person.getPlans().get(0);
				scoresExpPlans1.add(plan.getScore());

			} catch (NullPointerException e) {
				number_of_empty_plansIt0Exp1 += 1;
			}


		}
		double avgExpPlan1 = calculateAverage(scoresExpPlans1);

		log.info(" Overall Average Score from Experienced Plans at It 0, 100 pct: {} ", avgExpPlan1);

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
