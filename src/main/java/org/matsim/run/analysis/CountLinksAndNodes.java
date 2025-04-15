package org.matsim.run.analysis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;


/**
 * Count links and nodes.
 */


final class CountLinksAndNodes {
	private static final Logger log = LogManager.getLogger(CountLinksAndNodes.class);

	private CountLinksAndNodes() {
		// not called;
	}

	public static void main(String[] args) {
		// Path to Network
		String pathToNetwork = "https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/lausitz/lausitz-v2024.2/input/lausitz-v2024.2-network-with-pt.xml.gz";
		Network network = NetworkUtils.readNetwork(pathToNetwork);
		int numberOfLinks = network.getLinks().size();
		int numberOfNodes = network.getNodes().size();
		log.info("Number of links: : {}" + numberOfLinks);
		log.info("Number of nodes: : {}" + numberOfNodes);

	}
}
