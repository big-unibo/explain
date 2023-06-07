#!/bin/bash
set -exo
cd resources
for file in "salespurchase_v1-2023-06-07.dmp" "sales_v1-2023-06-07.dmp" "purchase_v1-2023-06-07.dmp" "SSB_FLIGHT.DMP" "foodmart-mysql.sql" "foodmart-mysql-schema.sql" "COVID_WEEKLY.DMP" "FOODMART.DMP" "FRENCHELECTRICITY.DMP" "FRENCHELECTRICITYEXT.DMP"; do
  if [ ! -f "$file" ]; then
    curl -k -o "$file" "https://big.csr.unibo.it/projects/nosql-datasets/$file"
  fi
done
ls -las
cd -
cd libs
if [ ! -f "instantclient-basic-linux.x64-21.1.0.0.0.zip" ]; then
  curl -k -o instantclient-basic-linux.x64-21.1.0.0.0.zip https://big.csr.unibo.it/projects/nosql-datasets/instantclient-basic-linux.x64-21.1.0.0.0.zip
  unzip instantclient-basic-linux.x64-21.1.0.0.0.zip
  chmod -R 777 instantclient_21_1
fi
if [ ! -f "instantclient-basic-windows.x64-21.3.0.0.0.zip" ]; then
  curl -k -o instantclient-basic-windows.x64-21.3.0.0.0.zip https://big.csr.unibo.it/projects/nosql-datasets/instantclient-basic-windows.x64-21.3.0.0.0.zip
  unzip instantclient-basic-windows.x64-21.3.0.0.0.zip
  chmod -R 777 instantclient_21_3
fi
curl -k -o db-migration-0.1.0.jar https://big.csr.unibo.it/projects/nosql-datasets/db-migration-0.1.0.jar
ls -las
cd -
