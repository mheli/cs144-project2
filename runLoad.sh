#!/bin/bash

# Run the drop.sql batch file to drop existing tables
# Inside the drop.sql, you sould check whether the table exists. Drop them ONLY if they exists.
mysql CS144 < drop.sql

# Run the create.sql batch file to create the database and tables
mysql CS144 < create.sql

# Compile and run the parser to generate the appropriate load files
ant
ant run-all

# If the Java code does not handle duplicate removal, do this now
sort category.csv | uniq > categoryTemp
mv categoryTemp category.csv

# Run the load.sql batch file to load the data
mysql CS144 < load.sql

# Remove all temporary files
rm bid.csv category.csv item.csv user.csv