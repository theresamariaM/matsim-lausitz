package org.matsim.run;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.matsim.application.MATSimApplication;
import org.matsim.testcases.MatsimTestUtils;
import org.matsim.run.scenarios.LausitzScenario;


public class RunIntegrationTest {

	@RegisterExtension
	public MatsimTestUtils utils = new MatsimTestUtils();

	@Test
	public void runScenario() {

		assert MATSimApplication.execute(LausitzScenario.class,
			"--1pct",
			"--iterations", "1",
			"--config:plans.inputPlansFile", "./input/v2024.2/lausitz-v2024.2-1.0-pct-plans-1.xml.gz",
			"--output", utils.getOutputDirectory(),
			"--config:controller.overwriteFiles=deleteDirectoryIfExists") == 0 : "Must return non error code";

	}
}
