package org.matsim.run.analysis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.population.PopulationUtils;


import java.util.ArrayList;


final class ScoresTest {
	private static final Logger log = LogManager.getLogger(CheckPopulationSizesAndScores.class);

	private ScoresTest() {
		// not called
	}

	public static void main(String[] args) {
		// import 1 pct plans
		String pathToPopulation = "/home/lola/math_cluster/input/v2024.2/lausitz-v2024.2-1.0-pct-plans-1.xml.gz";
		Population population = PopulationUtils.readPopulation(pathToPopulation);

		ArrayList<Double> avgScores = new ArrayList<>();
		int number_of_empty_plans = 0;
		for (Person person : population.getPersons().values()) {
			try {
				ArrayList<Double> scoresPerson = new ArrayList<>();
				for (Plan plan : person.getPlans()) {
					scoresPerson.add(plan.getScore());
				}
				double avgPerson = calculateAverage(scoresPerson);
				avgScores.add(avgPerson);
			} catch (NullPointerException e) {
				number_of_empty_plans += 1;
			}


		}

		// plans in Iter0
		double avgScore = calculateAverage(avgScores);
		log.info(" Overall Average Score from input Plans: {} ", avgScore);

		String pathToIter0Pop = "/home/lola/Nextcloud/Masterarbeit/03_Outputs/1pct_fCF_sCF_0.01/output-lausitz-1pct-1-fCF_sCF_0.01_gS_default_removeSV_false/ITERS/it.0/lausitz-1pct-1-fCf_sCF_0.01_gS_default.0.plans.xml.gz";
		Population popIt0 = PopulationUtils.readPopulation(pathToIter0Pop);

		ArrayList<Double> avgScoresIt0 = new ArrayList<>();
		int number_of_empty_plansIt0 = 0;
		for (Person person : popIt0.getPersons().values()) {
			try {
				ArrayList<Double> scoresPerson = new ArrayList<>();
				for (Plan plan : person.getPlans()) {
					scoresPerson.add(plan.getScore());
				}
				double avgPerson = calculateAverage(scoresPerson);
				avgScoresIt0.add(avgPerson);
			} catch (NullPointerException e) {
				number_of_empty_plansIt0 += 1;
			}


		}
		double avgScoreIt0 = calculateAverage(avgScoresIt0);
		log.info(" Overall Average Score from Plans at It 0: {} ", avgScoreIt0);

		// It0 experienced Plans
		String pathToIt0ExpPl = "/home/lola/Nextcloud/Masterarbeit/03_Outputs/1pct_fCF_sCF_0.01/output-lausitz-1pct-1-fCF_sCF_0.01_gS_default_removeSV_false/ITERS/it.0/lausitz-1pct-1-fCf_sCF_0.01_gS_default.0.experienced_plans.xml.gz";
		Population popIt0Exp = PopulationUtils.readPopulation(pathToIt0ExpPl);

		int number_of_empty_plansIt0Exp = 0;
		ArrayList<Double> scoresExpPlans = new ArrayList<>();
		for (Person person : popIt0Exp.getPersons().values()) {
			try {
				Plan plan = person.getPlans().get(0);
				scoresExpPlans.add(plan.getScore());
			} catch (NullPointerException e) {
				number_of_empty_plansIt0 += 1;
			}


		}
		double avgExpPlan = calculateAverage(scoresExpPlans);

		log.info(" Overall Average Score from  1 pct Experienced Plans at It 0: {} ", avgExpPlan);


		// 0th Iteration 100 pct excperienced plans average
		String pathToIt0ExpPl100 = "/home/lola/math_cluster/output/output-lausitz-100pct/ITERS/it.0/lausitz-100pct.0.experienced_plans.xml.gz";
		Population popIt0Exp100 = PopulationUtils.readPopulation(pathToIt0ExpPl100);

		int number_of_empty_plansIt0Exp100 = 0;
		ArrayList<Double> scoresExpPlans100 = new ArrayList<>();
		for (Person person : popIt0Exp100.getPersons().values()) {
			try {
				Plan plan = person.getPlans().get(0);
				scoresExpPlans100.add(plan.getScore());

			} catch (NullPointerException e) {
				number_of_empty_plansIt0Exp100 += 1;
			}


		}
		double avgExpPlan100 = calculateAverage(scoresExpPlans100);

		log.info(" Overall Average Score from Experienced Plans at It 0, 100 pct: {} ", avgExpPlan100);


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
