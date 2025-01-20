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

public class CheckPopulationSizesAndScores {

	public static void main(String[] args) {
		// import original 100 pct plans
		String pathToPopulation = "./input/v2024.2/010.output_plans.xml.gz";
		Population population = PopulationUtils.readPopulation(pathToPopulation);
		int populationSize = population.getPersons().size();
		System.out.println(" Size of Output_plans with all agents (not reduced to only car users):" + populationSize);

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
		System.out.println(" Size of Car Users only of Output Plans: " + newPopulationSize);

		// calculate average score
		double avg = calculateAverageScoreOfSelectedPlan(population);

		System.out.println(" Average scores of 100 pct Car Users only (from Output plans reduced to car users only):" + avg);


		// Import created 100 pct plans
		String pathToCarPopulation = "./input/v2024.2/lausitz-v2024.2-100-pct-plans.xml.gz";
		Population populationCarUsers = PopulationUtils.readPopulation(pathToCarPopulation);
		int populationSizeCarUsers = populationCarUsers.getPersons().size();
		System.out.println(" Size of Population 100 Pct Car Users:" + populationSizeCarUsers);


		// calculate average score
		double avg2 = calculateAverageScoreOfSelectedPlan(populationCarUsers);
		System.out.println(" Average scores of created 100 pct plans (form *100-pct-plans.xml.gz:" + avg2);


	}


	private static double calculateAverageScoreOfSelectedPlan(Population population) {
		ArrayList<Double> scores = new ArrayList<>();
		int number_of_empty_plans = 0;
		for (Person person : population.getPersons().values()) {
			try {
				double score = person.getSelectedPlan().getScore();
				scores.add(score);
			} catch (Exception e) {
				// System.out.print("NullPointerException Caught");
				number_of_empty_plans += 1;
			}

		}
		System.out.print(" Number of NullPointerException Caught: " + number_of_empty_plans);
		System.out.print(" Length of scores Array " + scores.size());


		double sum = 0;

		for (int i = 0; i < scores.size(); i++) {
			sum += scores.get(i);
		}
		double avg = sum / scores.size();
		return avg;

	}

}
