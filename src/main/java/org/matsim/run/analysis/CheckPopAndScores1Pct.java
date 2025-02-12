package org.matsim.run.analysis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.population.PopulationUtils;


import java.util.ArrayList;


final class CheckPopAndScores1Pct {
	private static final Logger log = LogManager.getLogger(CheckPopAndScores1Pct.class);

	private CheckPopAndScores1Pct() {
		// not called
	}

	public static void main(String[] args) {
		// import 1 pct plans
		String pathToPopulation = "/home/lola/math_cluster/input/v2024.2/lausitz-v2024.2-1.0-pct-plans-1.xml.gz";
		Population population = PopulationUtils.readPopulation(pathToPopulation);
		int populationSize = population.getPersons().size();
		log.info("Size of lausitz-v2024.2-1.0-pct-plans-1 : {}", populationSize);

		// calculate average score of selected Plans
		double avg = calculateAverageScoreOfSelectedPlan(population);
		log.info(" Average scores of selected plans of lausitz-v2024.2-1.0-pct-plans-1: {}", avg);

		// calculate overall average score
		double avgOfAvg1pct = calculateAvgOfAvgOfPersPlans(population);
		log.info(" Average scores of average Scores of Plans of created 1 pct plans (from lausitz-v2024.2-1.0-pct-plans-1.xml.gz): {}", avgOfAvg1pct);


		// import plans from 0th iteration
		String pathToPlansIt0 = "/home/lola/Nextcloud/Masterarbeit/03_Outputs/1pct_fCF_sCF_0.01/output-lausitz-1pct-1-fCF_sCF_0.01_gS_default_removeSV_false/ITERS/it.0/lausitz-1pct-1-fCf_sCF_0.01_gS_default.0.plans.xml.gz";
		Population pop0 = PopulationUtils.readPopulation(pathToPlansIt0);
		int pop0Size = pop0.getPersons().size();
		log.info(" Size of ITERS/it.0/lausitz-1pct-1-fCf_sCF_0.01_gS_default.0.plans.xml.gz: {}", pop0Size);

		double avg0 = calculateAverageScoreOfSelectedPlan(pop0);
		log.info("Average Scores of selected Plans ITERS/it.0/lausitz-1pct-1-fCf_sCF_0.01_gS_default.0.plans.xml.gz: {}", avg0);

		double avgIt0 = calculateAvgOfAvgOfPersPlans(pop0);
		log.info(" Average scores of average Scores of Plans of 1 pct plans at iteration 0: {}", avgIt0);


		// import experienced plans from 0th iteration
		String pathToPlansIt0Exp = "/home/lola/Nextcloud/Masterarbeit/03_Outputs/1pct_fCF_sCF_0.01/output-lausitz-1pct-1-fCF_sCF_0.01_gS_default_removeSV_false/ITERS/it.0/lausitz-1pct-1-fCf_sCF_0.01_gS_default.0.experienced_plans.xml.gz";
		Population pop0Exp = PopulationUtils.readPopulation(pathToPlansIt0Exp);
		int pop0ExpSize = pop0Exp.getPersons().size();
		log.info("Size of /ITERS/it.0/lausitz-1pct-1-fCf_sCF_0.01_gS_default.0.experienced_plans.xml.gz: {}", pop0ExpSize);

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

		log.info(" Overall Average Score from Experienced Plans at It 0, 1 pct: {} ", avgExpPlan1);

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
		// calculate Average
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
		// calculate average
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
