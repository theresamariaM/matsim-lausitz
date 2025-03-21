package org.matsim.run.prepare;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.population.PopulationUtils;


import java.util.ArrayList;
import java.util.List;

/**
 * Create doubled and quadrupled 25 percent plans.
 */

final class CreateDoubledAndQuadrupled25PctPlans {

	private static final Logger log = LogManager.getLogger(CreateDoubledAndQuadrupled25PctPlans.class);

	private CreateDoubledAndQuadrupled25PctPlans() {
		// not called
	}

	public static void main(String[] args) {
		// Create doubled and quadrupled Versions of the 25 percent plans
		String[] sampleSizes = {"doubled", "quadrupled"};
		for (String size : sampleSizes) {
			if (size.equals("doubled")) {
				// create doubled
				// Inputs
				String inputFilePopulation = "./input/v2024.2/lausitz-v2024.2-25.0-pct-plans.xml.gz";


				// Output Path
				String outputFilePopulation = "./input/v2024.2/lausitz-v2024.2-25.0-pct-plans-" + size + ".xml.gz";
				clonePlansAndWritePopulation(inputFilePopulation, outputFilePopulation);


			} else {
				// create quadrupled
				// Inputs
				String inputFilePopulation = "./input/v2024.2/lausitz-v2024.2-25.0-pct-plans-doubled.xml.gz";
				// Output Path
				String outputFilePopulation = "./input/v2024.2/lausitz-v2024.2-25.0-plans-" + size + ".xml.gz";
				clonePlansAndWritePopulation(inputFilePopulation, outputFilePopulation);

			}

		}
	}

	private static void clonePlansAndWritePopulation(String inputPath, String outputPath) {
		// Load Inputs and create populationFactory
		Population population = PopulationUtils.readPopulation(inputPath);
		PopulationFactory populationFactory = population.getFactory();

		// Create array with original agents IDs
		List<Id<Person>> allPeople = new ArrayList<>();
		for (Person person : population.getPersons().values()) {
			allPeople.add(person.getId());
		}
		log.info("Size of original Population:   {}", population.getPersons().size());
		for (Id<Person> personId : allPeople) {
			Person person = population.getPersons().get(personId);

			if (person != null) {
				Person personCloned = populationFactory.createPerson(Id.create(personId + "c", Person.class));

				for (String attribute : person.getAttributes().getAsMap().keySet()) {
					personCloned.getAttributes().putAttribute(attribute, person.getAttributes().getAttribute(attribute));
				}

				for (Plan plan : person.getPlans()) {
					personCloned.addPlan(plan);
				}
				population.addPerson(personCloned);


			} else {
				log.info("Person with the following ID {} not found", personId);
			}
		}
		log.info("Size of Population with clones:   {}", population.getPersons().size());
		log.info("Writing Population to:   {}", outputPath);
		PopulationUtils.writePopulation(population, outputPath);

	}


}
