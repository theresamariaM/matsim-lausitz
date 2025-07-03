package org.matsim.run.analysis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class CountLinksByRoadType {
	public static final Logger LOG = LogManager.getLogger(CountLinksByRoadType.class);

	public static void main(String[] args) {
		String pathToNetwork = "https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/lausitz/lausitz-v2024.2/input/lausitz-v2024.2-network-with-pt.xml.gz";
		String outputPathFile1 = "/home/lola/Nextcloud/Masterarbeit/03_Outputs/Lausitz_Number_of_Links_by_Road_Type.csv";
		String outputPathFile2 = "/home/lola/Nextcloud/Masterarbeit/03_Outputs/Lausitz_Road_Types.csv";
		Network network = NetworkUtils.readNetwork(pathToNetwork);
		int numberOfPtLinks = 0;
		int numberOfMotorways = 0;
		int numberOfPrimary = 0;
		int numberOfSecondary = 0;
		int numberOfTertiary = 0;
		int numberOfLivingStreets = 0;
		int numberOfResidential = 0;
		int numberOfTrunk = 0;
		int numberOfUnclassified = 0;
		int numberOfService = 0;
		int AttributeIsNull = 0;
		int numberOfLinks = network.getLinks().size();
		ArrayList<String> linkTypes = new ArrayList<String>();

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
		try {
			ArrayList<String> uniqueRoadTypes = new ArrayList<>();

			for (String linkType : linkTypes) {
				if (!uniqueRoadTypes.contains(linkType)) {
					uniqueRoadTypes.add(linkType);

				}
				BufferedWriter file = new BufferedWriter(new FileWriter(outputPathFile2));
				file.write("Road_Type" + "\n");
				for (String uniqueRoadType : uniqueRoadTypes) {
					file.write(uniqueRoadType + "\n");

				}
				file.close();
			}
			BufferedWriter file = new BufferedWriter(new FileWriter(outputPathFile1));
			file.write("Type" + "," + "N" + "\n");
			file.write("All" + "," + numberOfLinks + "\n");
			file.write("numberOfMotorways" + "," + numberOfMotorways + "\n");
			file.write("numberOfPrimary" + "," + numberOfPrimary + "\n");
			file.write("numberOfSecondary" + "," + numberOfSecondary + "\n");
			file.write("numberOfTertiary" + "," + numberOfTertiary + "\n");
			file.write("numberOfLivingStreets" + "," + numberOfLivingStreets + "\n");
			file.write("numberOfResidential" + "," + numberOfResidential + "\n");
			file.write("numberOfTrunk" + "," + numberOfTrunk + "\n");
			file.write("numberOfService" + "," + numberOfService + "\n");
			file.write("numberOfUnclassified" + "," + numberOfUnclassified + "\n");
			file.write("numberOfPtLinks" + "," + numberOfPtLinks + "\n");
			file.write("numberOfAttributeIsNull" + "," + AttributeIsNull + "\n");


			file.close();
		} catch (IOException exep) {
			LOG.info("could not create csv file");
		}

	}


}
