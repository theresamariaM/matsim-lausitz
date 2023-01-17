##### Analysis of linkstats and counts based on BASt data
devtools::install_github("matsim-vsp/matsim-r",ref="counts")

library(matsim)
library(tidyverse)

COUNTS <- "Y:/matsim-lausitz/input/v1.0/lausitz-v1.0-counts-car-bast.xml.gz"
NETWORK <- "Y:/matsim-lausitz/input/v1.0/lausitz-v1.0-network-with-pt.xml.gz"

linkstats <- readLinkStats(runId = "v1.0-uncalibrated", file = "Y:/matsim-lausitz/qa/output/lausitz-25pct.output_linkstats.csv.gz")

counts <- readCounts(COUNTS)
network <- loadNetwork(NETWORK)

join <- mergeCountsAndLinks(counts = counts, linkStats  = list(linkstats), network = network,
                            networkModes = c("car"), aggr_to = "day")

#### VIA-styled scatterplot ####

FILE_DIR = "C:/Users/ACER/Desktop/Uni/VSP/Lausitz-Plots/"

createCountScatterPlot(joinedFrame = join)
ggsave(filename = paste0(FILE_DIR, "Traffic_Count_Scatterplot_with_freight.jpg"))

#### Analysis of DTV distribution ####
join.dtv.distribution <- processLinkStatsDtvDistribution(joinedFrame = join, to = 50000)

ggplot(join.dtv.distribution, aes(x = traffic_bin, y = share, fill = type)) +
  
  geom_col() +
  
  facet_grid(src ~ type) +
  
  labs(x = "Daily traffic volume", y = "Share") +
  
  theme_bw() +
  
  theme(legend.position = "none", axis.text.x = element_text(angle = 90))

ggsave(filename = paste0(FILE_DIR, "Traffic_volume_distribution_by_road_type_with_freight.jpg"))
rm(join.dtv.distribution)


#### Analysis of Estimation Quality ####

join.est.quality <- processDtvEstimationQuality(joinedFrame = join, aggr = T) %>%
  filter(!type %in% c("residential", "unclassified", NA))

ggplot(join.est.quality, aes(estimation, share, fill = type)) +
  
  geom_col() +
  
  labs(y = "Share", x = "Quality category") +
  
  facet_grid(src ~ type) +
  
  theme_bw() +
  
  theme(legend.position = "none", axis.text.x = element_text(angle = 90))

rm(join.est.quality)
ggsave(filename = paste0(FILE_DIR, "Estimation_quality_by_road_type_with_freight.jpg"))