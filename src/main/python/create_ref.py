#!/usr/bin/env python
# -*- coding: utf-8 -*-

import geopandas as gpd

from matsim.scenariogen.data import run_create_ref_data


def person_filter(df):
    df = gpd.GeoDataFrame(df, geometry=gpd.GeoSeries.from_wkt(df.geom, crs="EPSG:4326").to_crs("EPSG:25832"))
    df = gpd.sjoin(df, region, how="inner", predicate="intersects")

    return df[df.present_on_day & (df.reporting_day <= 5)]


def trip_filter(df):
    # Other modes are ignored in the total share
    return df[df.main_mode != "other"]


if __name__ == "__main__":
    region = gpd.read_file("../../../../shared-svn/projects/DiTriMo/data/shp/lausitz.shp").to_crs("EPSG:25832")

    persons, trips, share = run_create_ref_data.create("/Volumes/Untitled/B3_Lokal-Datensatzpaket/CSV",
                                                       person_filter, trip_filter,
                                                       run_create_ref_data.InvalidHandling.REMOVE_TRIPS)

    print("Filtered %s persons" % len(persons))
    print("Filtered %s trips" % len(trips))

    print(share)
