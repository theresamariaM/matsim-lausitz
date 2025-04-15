package org.matsim.run.analysis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.population.PopulationUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Count agents and freight agents.
 */


final class CountAgentsAndFreightSubpopulation {
	private static final Logger log = LogManager.getLogger(CountAgentsAndFreightSubpopulation.class);

	private CountAgentsAndFreightSubpopulation() {
		// not callec;
	}

	public static void main(String[] args) {
		String pathToPopulation = "https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/lausitz/lausitz-v2024.2/output/100pct/lausitz-v2024.2-100pct-base-case.output_plans.xml.gz";
		Population population = PopulationUtils.readPopulation(pathToPopulation);
		int numberOfAgents = population.getPersons().size();
		log.info("Number of agents: : {}" + numberOfAgents);
		List<Id<Person>> notAPerson = new ArrayList<>();
		for (Person person : population.getPersons().values()) {
			if (!person.getAttributes().getAttribute("subpopulation").equals("person")) {
				notAPerson.add(person.getId());
			}
		}
		int numberOfFreights = notAPerson.size();
		log.info("Number of freight Agents: : {}" + numberOfFreights);
	}
}
