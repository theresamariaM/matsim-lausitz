package org.matsim.run.analysis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;


/**
 * Count links by road type.
 */

final class CountLinksByRoadType {
	public static final Logger LOG = LogManager.getLogger(CountLinksByRoadType.class);

	public static void main(String[] args) throws IOException {
		String pathToNetwork = "https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/lausitz/lausitz-v2024.2/input/lausitz-v2024.2-network-with-pt.xml.gz";
		String outputPathFile1 = "/net/ils/mersini/output/CheckNonCarPlans/Lausitz_Number_of_Links_by_Road_Type.csv";
		Network network = NetworkUtils.readNetwork(pathToNetwork);
		HashMap<String, Integer> counts = countLinkTypes(network);
		try {
			writeLinkTypesAndFreqToFile(counts, outputPathFile1);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


	}


	private static HashMap<String, Integer> countLinkTypes(Network network) {
		HashMap<String, Integer> linkTypesAndTheirFrequency = new HashMap<>();
		Integer numberOfPtLinks = 0;
		Integer numberOfMotorways = 0;
		Integer numberOfPrimary = 0;
		Integer numberOfSecondary = 0;
		Integer numberOfTertiary = 0;
		Integer numberOfLivingStreets = 0;
		Integer numberOfResidential = 0;
		Integer numberOfTrunk = 0;
		Integer numberOfUnclassified = 0;
		Integer numberOfService = 0;
		Integer AttributeIsNull = 0;
		Integer numberOfLinks = network.getLinks().size();

		for (Link link : network.getLinks().values()) {
			if (link.getAttributes().getAttribute("type") == null) {
				AttributeIsNull++;

			}
			//linkTypes.add(link.getAttributes().getAttribute("type").toString());
			if (link.getId().toString().startsWith("pt_")) {
				numberOfPtLinks++;


			} else if (link.getAttributes().getAttribute("type").toString().startsWith("highway.motorway")) {
				numberOfMotorways++;

			} else if (link.getAttributes().getAttribute("type").toString().startsWith("highway.primary")) {
				numberOfPrimary++;

			} else if (link.getAttributes().getAttribute("type").toString().startsWith("highway.secondary")) {
				numberOfSecondary++;
			} else if (link.getAttributes().getAttribute("type").toString().startsWith("highway.tertiary")) {
				numberOfTertiary++;
			} else if (link.getAttributes().getAttribute("type").toString().startsWith("highway.living_street")) {
				numberOfLivingStreets++;

			} else if (link.getAttributes().getAttribute("type").toString().startsWith("highway.residential")) {
				numberOfResidential++;
			} else if (link.getAttributes().getAttribute("type").toString().startsWith("highway.trunk")) {
				numberOfTrunk++;
			} else if (link.getAttributes().getAttribute("type").toString().startsWith("highway.unclassified")) {
				numberOfUnclassified++;
			} else if (link.getAttributes().getAttribute("type").toString().startsWith("highway.service")) {
				numberOfService++;
			}


		}
		linkTypesAndTheirFrequency.put("numberOfPtLinks", numberOfPtLinks);
		linkTypesAndTheirFrequency.put("numberOfMotorways", numberOfMotorways);
		linkTypesAndTheirFrequency.put("numberOfPrimary", numberOfPrimary);
		linkTypesAndTheirFrequency.put("numberOfSecondary", numberOfSecondary);
		linkTypesAndTheirFrequency.put("numberOfTertiary", numberOfTertiary);
		linkTypesAndTheirFrequency.put("numberOfLivingStreets", numberOfLivingStreets);
		linkTypesAndTheirFrequency.put("numberOfResidential", numberOfResidential);
		linkTypesAndTheirFrequency.put("numberOfTrunk", numberOfTrunk);
		linkTypesAndTheirFrequency.put("numberOfUnclassified", numberOfUnclassified);
		linkTypesAndTheirFrequency.put("numberOfService", numberOfService);
		linkTypesAndTheirFrequency.put("AttributeIsNull", AttributeIsNull);
		linkTypesAndTheirFrequency.put("numberOfLinks", numberOfLinks);

		return linkTypesAndTheirFrequency;
	}

	public static void writeLinkTypesAndFreqToFile(HashMap<String, Integer> linkTypesAndTheirFrequency, String outputPath) throws IOException {
		try {
			BufferedWriter file = new BufferedWriter(new FileWriter(outputPath));
			file.write("Type" + "," + "N" + "\n");
			file.write("All" + "," + linkTypesAndTheirFrequency.get("numberOfLinks") + "\n");
			file.write("numberOfMotorways" + "," + linkTypesAndTheirFrequency.get("numberOfMotorways") + "\n");
			file.write("numberOfPrimary" + "," + linkTypesAndTheirFrequency.get("numberOfPrimary") + "\n");
			file.write("numberOfSecondary" + "," + linkTypesAndTheirFrequency.get("numberOfSecondary") + "\n");
			file.write("numberOfTertiary" + "," + linkTypesAndTheirFrequency.get("numberOfTertiary") + "\n");
			file.write("numberOfLivingStreets" + "," + linkTypesAndTheirFrequency.get("numberOfLivingStreets") + "\n");
			file.write("numberOfResidential" + "," + linkTypesAndTheirFrequency.get("numberOfResidential") + "\n");
			file.write("numberOfTrunk" + "," + linkTypesAndTheirFrequency.get("numberOfTrunk") + "\n");
			file.write("numberOfService" + "," + linkTypesAndTheirFrequency.get("numberOfService") + "\n");
			file.write("numberOfUnclassified" + "," + linkTypesAndTheirFrequency.get("numberOfUnclassified") + "\n");
			file.write("numberOfPtLinks" + "," + linkTypesAndTheirFrequency.get("numberOfPtLinks") + "\n");
			file.write("numberOfAttributeIsNull" + "," + linkTypesAndTheirFrequency.get("AttributeIsNull") + "\n");


			file.close();
		} catch (IOException exception) {
			LOG.info("could not create csv file");
		}

	}

}
