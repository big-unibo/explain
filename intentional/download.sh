#!/bin/bash
set -exo

cd resources
if [ ! -f "foodmart-mysql.sql" ]; then 
    curl -k -o foodmart-mysql.sql https://big.csr.unibo.it/projects/nosql-datasets/foodmart-mysql.sql
fi
if [ ! -f "foodmart-mysql-schema.sql" ]; then 
    curl -k -o foodmart-mysql-schema.sql https://big.csr.unibo.it/projects/nosql-datasets/foodmart-mysql-schema.sql
fi
if [ ! -f "COVID_WEEKLY.DMP" ]; then 
    curl -k -o COVID_WEEKLY.DMP -L https://github.com/w4bo/covid-dataset/releases/download/1.0.2/COVID_WEEKLY.DMP
fi
if [ ! -f "FOODMART.DMP" ]; then 
    curl -k -o FOODMART.DMP https://big.csr.unibo.it/projects/nosql-datasets/FOODMART.DMP
fi
if [ ! -f "FRENCHELECTRICITY.DMP" ]; then 
    curl -k -o FRENCHELECTRICITY.DMP https://big.csr.unibo.it/projects/nosql-datasets/FRENCHELECTRICITY.DMP
fi
if [ ! -f "FRENCHELECTRICITYEXT.DMP" ]; then 
    curl -k -o FRENCHELECTRICITYEXT.DMP https://big.csr.unibo.it/projects/nosql-datasets/FRENCHELECTRICITYEXT.DMP
fi
if [ ! -f "SSB_FLIGHT.DMP" ]; then 
    curl -k -o SSB_FLIGHT.DMP https://big.csr.unibo.it/projects/nosql-datasets/SSB_FLIGHT.DMP
fi

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
