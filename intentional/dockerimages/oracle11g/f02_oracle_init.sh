chmod -R 777 /data
ls -las /data
# expdp sales_v1/oracle@xe directory=oracle_dump dumpfile=sales_v1-2023-05-30.dmp SCHEMAS=sales_v1
# expdp purchase_v1/oracle@xe directory=oracle_dump dumpfile=purchase_v1-2023-05-30.dmp SCHEMAS=purchase_v1
# expdp salespurchase_v1/oracle@xe directory=oracle_dump dumpfile=salespurchase_v1-2023-05-30.dmp SCHEMAS=salespurchase_v1
impdp foodmart/oracle@127.0.0.1:1521/xe DIRECTORY=ORACLE_DUMP DUMPFILE=FOODMART.DMP SCHEMAS=foodmart
impdp covid_weekly/oracle@127.0.0.1:1521/xe DIRECTORY=ORACLE_DUMP DUMPFILE=COVID_WEEKLY.DMP SCHEMAS=covid_weekly
impdp frenchelectricity/oracle@127.0.0.1:1521/xe DIRECTORY=ORACLE_DUMP DUMPFILE=FRENCHELECTRICITY.DMP SCHEMAS=frenchelectricity
impdp frenchelectricityext/oracle@127.0.0.1:1521/xe DIRECTORY=ORACLE_DUMP DUMPFILE=FRENCHELECTRICITYEXT.DMP SCHEMAS=frenchelectricityext
impdp ssb_flight/oracle@127.0.0.1:1521/xe DIRECTORY=ORACLE_DUMP DUMPFILE=SSB_FLIGHT.DMP SCHEMAS=ssb_flight
impdp sales_v1/oracle@xe directory=oracle_dump dumpfile=sales_v1-2023-05-30.dmp SCHEMAS=sales_v1
impdp purchase_v1/oracle@xe directory=oracle_dump dumpfile=purchase_v1-2023-05-30.dmp SCHEMAS=purchase_v1
impdp salespurchase_v1/oracle@xe directory=oracle_dump dumpfile=salespurchase_v1-2023-05-30.dmp SCHEMAS=salespurchase_v1
touch /data/.ready
