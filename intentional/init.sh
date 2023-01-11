#!/bin/bash
set -exo

echo "Replacing .env.example with .env"
cp .env.example .env
echo "Replacing src/main/resources/config.example.yml src/main/resources/config.yml"
cp src/main/resources/config.example.yml src/main/resources/config.yml
echo "Replacing ../web/js/config.example.js ../web/js/config.js"
cp ../web/js/config.example.js ../web/js/config.js

P=$(pwd)
echo $P

if [ -d "src/main/python/venv" ] 
then
    echo "The virtual environment already exists" 
else
    echo "Creating the virtual environment" 
    cd src/main/python
    python -m venv venv
    if [ -d "venv/bin" ]
    then
        source venv/bin/activate
    else
        source venv/Scripts/activate
    fi
    pip install -r requirements.txt
    chmod -R 777 .
    cd -
fi

sed -i "s+\!HOME\!+${P}+g" src/main/resources/config.yml
sed -i "s+\!HOME\!+${P}+g" .env
