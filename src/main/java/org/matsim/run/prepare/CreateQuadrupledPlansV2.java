package org.matsim.run.prepare;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.population.routes.NetworkRoute;
import org.matsim.utils.objectattributes.attributable.AttributesUtils;
import org.matsim.vehicles.PersonVehicles;
import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.VehicleUtils;

import java.util.ArrayList;
import java.util.List;

public class CreateQuadrupledPlansV2 {
	private static final Logger log = LogManager.getLogger(CreateQuadrupledPlansV2.class);

	private CreateQuadrupledPlansV2() {
		// not called
	}

	public static void main(String[] args) {
		String inputFilePopulation = "./input/v2024.2/lausitz-v2024.2-25.0-pct-plans-doubled.xml.gz";

		String intermediateOutputFilePopulation = "./input/v2024.2/lausitz-v2024.2-25.0-pct-plans-doubled-cloning-Intermediate.xml.gz";

		String outputFilePopulation = "./input/v2024.2/lausitz-v2024.2-25.0-pct-plans-quadrupled.xml.gz";

		createClonedIntermediatePopulation(inputFilePopulation, intermediateOutputFilePopulation, "q");
		createClonedPopulation(inputFilePopulation, intermediateOutputFilePopulation, outputFilePopulation);


	}

	private static void createClonedIntermediatePopulation(String inputPath, String intermediatePath, String idAddOn) {
		// Load Inputs and create populationFactory
		Population population = PopulationUtils.readPopulation(inputPath);
		log.info("Size of original Population:   {}", population.getPersons().size());

		PopulationFactory populationFactory = population.getFactory();


		// Create array with original agents IDs
		List<Id<Person>> originalPerson = new ArrayList<>();

		List<Id<Person>> clonedPerson = new ArrayList<>();


		for (Person person : population.getPersons().values()) {
			originalPerson.add(person.getId());
		}

		for (Id<Person> personId : originalPerson) {
			Person person = population.getPersons().get(personId);

			if (person != null) {
				// Create a cloned person
				Person personCloned = populationFactory.createPerson(Id.create(personId + idAddOn, Person.class));

				// iterate over the plans of the original person and add all the plans to the new person
				for (Plan plan : person.getPlans()) {
					// create a new plan for the cloned person for every plan the original person has
					Plan newPlan = populationFactory.createPlan();
					PopulationUtils.copyFromTo(plan, newPlan);
					personCloned.addPlan(newPlan);
				}
				// copy attributes
				AttributesUtils.copyAttributesFromTo(person, personCloned);
				// add the cloned person
				population.addPerson(personCloned);
				clonedPerson.add(personCloned.getId());
				// remove the original Person
				population.removePerson(person.getId());

			} else {
				log.info("Person with the following ID {} not found", personId);
			}

		}
		// Available vehicles
		ArrayList<String> availVehicles = new ArrayList<String>();
		availVehicles.add("truck8t");
		availVehicles.add("truck40t");
		availVehicles.add("truck18t");
		availVehicles.add("car");
		availVehicles.add("longDistanceFreight");
		availVehicles.add("ride");
		availVehicles.add("bike");

		for (Id<Person> personId : clonedPerson) {
			Person newPerson = population.getPersons().get(personId);

			// create new  modeVehicles = new HashMap<>();
			PersonVehicles clonedPersonVehicles = new PersonVehicles();
			for (String veh : availVehicles) {
				clonedPersonVehicles.addModeVehicle(veh, Id.createVehicleId(personId + "_" + veh));
			}
			// put updated ids into attributes
			VehicleUtils.insertVehicleIdsIntoPersonAttributes(newPerson, clonedPersonVehicles.getModeVehicles());
			// now loop over the plans and set the RefId
			for (Plan plan : newPerson.getPlans()) {
				for (int i = 0; i < plan.getPlanElements().size(); i++) {
					if (plan.getPlanElements().get(i) instanceof Leg leg) {
						Route newRoute = leg.getRoute();
						if (newRoute instanceof NetworkRoute networkRoute) {
							String mode = leg.getMode();
							Id<Vehicle> newVehicleId = Id.createVehicleId(personId + "_" + mode);
							networkRoute.setVehicleId(newVehicleId);
						}
					}

				}
			}

		}
		writePopulation(population, intermediatePath);


	}

	private static void createClonedPopulation(String inputPath, String intermediatePath, String outputPath) {
		Population population = PopulationUtils.readPopulation(inputPath);
		PopulationFactory populationFactory = population.getFactory();

		Population clonedPopulation = PopulationUtils.readPopulation(intermediatePath);

		List<Id<Person>> clonedPerson = new ArrayList<>();
		for (Person person : clonedPopulation.getPersons().values()) {
			clonedPerson.add(person.getId());
		}
		for (Id<Person> clonedPersonId : clonedPerson) {

			Person person = clonedPopulation.getPersons().get(clonedPersonId);

			if (person != null) {
				Person personClonedInPopulation = populationFactory.createPerson(clonedPersonId);
				// iterate over the plans of the original person and add all the plans to the new person
				for (Plan plan : person.getPlans()) {
					// create a new plan for the cloned person for every plan the original person has
					Plan newPlan = populationFactory.createPlan();
					PopulationUtils.copyFromTo(plan, newPlan);
					personClonedInPopulation.addPlan(newPlan);
				}
				// copy attributes
				AttributesUtils.copyAttributesFromTo(person, personClonedInPopulation);
				// add the cloned person
				population.addPerson(personClonedInPopulation);

			} else {
				log.info("Person with the following ID {} not found", clonedPersonId);
			}
		}
		writePopulation(population, outputPath);
	}

	private static void writePopulation(Population population, String outputPath) {
		log.info("Size of Population written to file:   {}", population.getPersons().size());
		log.info("Writing Population to:   {}", outputPath);
		PopulationUtils.writePopulation(population, outputPath);
	}

}
