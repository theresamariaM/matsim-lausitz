package org.matsim.run.prepare;

import org.matsim.api.core.v01.Id;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.router.TripStructureUtils;


import java.util.ArrayList;
import java.util.List;


/**
 * Reduce population to car users only.
 */

final class PreparePopulationCarUsers {
	private static final Logger log = LogManager.getLogger(PreparePopulationCarUsers.class);

	private PreparePopulationCarUsers() {
		//not called
	}


	public static void main(String[] args) {
		final String outputFilePopulation = "./input/v2024.2/lausitz-v2024.2-100.0-pct-plans.xml.gz";
		String pathToPopulation = "https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/lausitz/lausitz-v2024.2/output/100pct/lausitz-v2024.2-100pct-base-case.output_plans.xml.gz";
		Population population = PopulationUtils.readPopulation(pathToPopulation);

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
		PopulationUtils.writePopulation(population, outputFilePopulation);
		log.info("Population written to: {}" + outputFilePopulation);


	}

}
