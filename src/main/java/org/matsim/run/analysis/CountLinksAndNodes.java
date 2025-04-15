package org.matsim.run.analysis;

import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;

public class CountLinksAndNodes {
	public static void main(String[] args) {
		// Path to Network
		String pathToNetwork = "https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/lausitz/lausitz-v2024.2/input/lausitz-v2024.2-network-with-pt.xml.gz";
		Network network = NetworkUtils.readNetwork(pathToNetwork);
		Integer numberOfLinks = network.getLinks().size();
		Integer numberOfNodes = network.getNodes().size();
		System.out.println("Number of links: " + numberOfLinks);
		System.out.println("Number of nodes: " + numberOfNodes);

	}
}
