#!/usr/bin/env python
# -*- coding: utf-8 -*-

import geopandas as gpd
import os
import pandas as pd

try:
    from matsim import calibration
except:
    import calibration

# %%

if os.path.exists("mid.csv"):
    srv = pd.read_csv("mid.csv")
    sim = pd.read_csv("sim.csv")

    _, adj = calibration.calc_adjusted_mode_share(sim, srv)

    print(srv.groupby("mode").sum())

    print("Adjusted")
    print(adj.groupby("mode").sum())

    adj.to_csv("mid_adj.csv", index=False)

# %%

modes = ["walk", "car", "ride", "pt", "bike"]
fixed_mode = "walk"
initial = {
    "bike": -0.141210,
    "pt": 0,
    "car": 0,
    "ride": -2.22873502992
}

# Based on MiD 2017, filtered on Lausitz region
target = {
    "walk": 0.199819,
    "bike": 0.116362,
    "pt": 0.049501,
    "car": 0.496881,
    "ride": 0.137437
}

region = gpd.read_file("../input/shp/lausitz.shp").to_crs("EPSG:25832")


def filter_persons(persons):
    persons = gpd.GeoDataFrame(persons, geometry=gpd.points_from_xy(persons.home_x, persons.home_y))
    df = gpd.sjoin(persons.set_crs("EPSG:25832"), region, how="inner", op="intersects")

    print("Filtered %s persons" % len(df))

    return df


def filter_modes(df):
    # Set multi-modal trips to pt
    df.loc[df.main_mode.str.startswith("pt_"), "main_mode"] = "pt"

    return df[df.main_mode.isin(modes)]


study, obj = calibration.create_mode_share_study("calib", "matsim-lausitz-1.x-SNAPSHOT-e8458e3.jar",
                                                 "../input/v1.0/lausitz-v1.0-100pct.config.xml",
                                                 modes, target,
                                                 initial_asc=initial,
                                                 args="--25pct",
                                                 jvm_args="-Xmx60G -Xmx60G -XX:+AlwaysPreTouch -XX:+UseParallelGC",
                                                 transform_persons=filter_persons, transform_trips=filter_modes,
                                                 lr=calibration.linear_lr_scheduler(start=0.3, interval=8),
                                                 chain_runs=calibration.default_chain_scheduler)

# %%

study.optimize(obj, 10)
