package org.matsim.run.prepare;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import java.util.ArrayList;
import java.util.List;


/**
 * Reduce population to car users only.
 */

final class PreparePopulationCarUsers {
	private PreparePopulationCarUsers(){
		//not called
	}
	private final static  Logger Log = LogManager.getLogger(PreparePopulationCarUsers.class);
	public static void main( String [] args ) {
		final String outputFilePopulation = "./input/v2024.2-car-users-only/lausitz-v2024.2-100-pct-plans.xml.gz";
		Config config = ConfigUtils.loadConfig("./input/v2024.2/lausitz-v2024.2-100pct.config.xml");
		Scenario scenario = ScenarioUtils.loadScenario( config );
		Population population = scenario.getPopulation();
		List<Id<Person>> nonCarUsers = new ArrayList<>();
		List<Id<Person>> notAPerson = new ArrayList<>();

		for(Person person : population.getPersons().values()) {
			if(!person.getAttributes().getAttribute("subpopulation").equals("person")){
				notAPerson.add(person.getId());
			}
			Plan plan = person.getSelectedPlan();
			boolean containsCarLeg = false;

			for(Leg leg: TripStructureUtils.getLegs(plan)){
				if(TransportMode.car.equals( leg.getMode() ) ){
					containsCarLeg = true;
					break;
				}
			}

			if(!containsCarLeg){
				nonCarUsers.add(person.getId());
			}


		}

		for (Id<Person> personId : nonCarUsers) {
			population.removePerson(personId);
		}

		for (Id<Person> personId : notAPerson) {
			population.removePerson(personId);
		}

		new PopulationWriter(population, scenario.getNetwork()).write(outputFilePopulation);
		Log.info("Population written to:" + outputFilePopulation);


	}

}
