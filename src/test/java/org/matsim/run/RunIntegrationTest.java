package org.matsim.run;

import org.junit.Rule;
import org.junit.Test;
import org.matsim.application.MATSimApplication;
import org.matsim.testcases.MatsimTestUtils;

public class RunIntegrationTest {

	@Rule
	public MatsimTestUtils utils = new MatsimTestUtils();

	@Test
	public void runScenario() {

		assert MATSimApplication.execute(RunLausitzScenario.class,
			"--1pct",
			"--iterations", "1",
			"--output", utils.getOutputDirectory(),
			"--config:controller.overwriteFiles=deleteDirectoryIfExists") == 0 : "Must return non error code";

	}
}
