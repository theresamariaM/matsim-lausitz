package org.matsim.run.prepare;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.*;
import org.matsim.application.MATSimAppCommand;
import org.matsim.application.prepare.population.ExtractHomeCoordinates;
import org.matsim.core.population.PersonUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.router.TripStructureUtils;
import picocli.CommandLine;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@CommandLine.Command(
	name = "population",
	description = "Prepares  populations such that only people with car uses remain."
)


public class PreparePopulationCarUsers {
	private static final Logger log = LogManager.getLogger(PreparePopulationCarUsers.class);
	public static void main ( String [] args ) {
		final String outputFilePopulation = "./input/v1.0/1pct-plans-car-users-only-v2.xml.gz";
		Config config = ConfigUtils.loadConfig("./input/v1.0/lausitz-v1.0-1pct.config.xml");
		Scenario scenario = ScenarioUtils.loadScenario( config );
		Population population = scenario.getPopulation();
		List<Id<Person>> NonCarUsers = new ArrayList<>();
		for (Person person : population.getPersons().values()) {
			System.out.println("entering person for loop");
			Plan plan = person.getSelectedPlan();
			boolean containsCarLeg = false;
			for(Leg leg: TripStructureUtils.getLegs(plan)){
				System.out.println("entering leg for loop");
				System.out.println("TransportMode.car.equals( leg.getMode() ) "+ TransportMode.car.equals( leg.getMode() ));
				if(TransportMode.car.equals( leg.getMode() ) ){
					containsCarLeg = true;
					break;
				}
			}

			if(!containsCarLeg){
				System.out.println("entering if (!containsCarLeg)");
				NonCarUsers.add(person.getId());
			}


		}
		System.out.println("NonCarUsers[1:10] " + NonCarUsers.get(1));

		for (Id<Person> personId : NonCarUsers) {
			population.removePerson(personId);
		}
		new PopulationWriter(population, scenario.getNetwork()).write(outputFilePopulation);
		log.info("Population written to:" + outputFilePopulation);


	}

}
