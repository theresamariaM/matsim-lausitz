package org.matsim.run;

import org.matsim.application.MATSimApplication;

/**
 * Run the Lausitz scenario with default configuration.
 */
public final class RunLausitzScenario {

	private RunLausitzScenario() {
	}

	public static void main(String[] args) {
		MATSimApplication.runWithDefaults(LausitzScenario.class, args);
	}

}
