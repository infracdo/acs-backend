#!/bin/bash
echo "####################################################\n"
echo "INSTALLING OPENJDK \n"
echo "####################################################\n"

apt-get update
apt-get install default-jdk -y
echo -e  "JAVA_HOME=\"/usr/lib/jvm/java-1.11.0-openjdk-amd64\"" >> /etc/environment
source /etc/environment

echo "####################################################\n"
echo "INSTALLING MAVEN ................ \n"
echo "####################################################\n"

apt-get install maven -y

echo "####################################################\n"
echo "INSTALLING MYSQL SERVER ................ \n"
echo "####################################################\n"

apt-get install mysql-server -y

echo "####################################################\n"
echo "PREPARING BACKEND DATABASE ................ \n"
echo "####################################################\n"

mysql -e "CREATE DATABASE acsDB DEFAULT CHARACTER SET utf8;"
mysql -e "CREATE USER apollo@localhost IDENTIFIED BY '2020@p0LL0!';"
mysql -e "GRANT ALL PRIVILEGES ON *.* TO 'apollo'@'localhost';"
mysql -e "FLUSH PRIVILEGES;"

echo "####################################################\n"
echo "IMPORTING MYSQL DATABASE ................ \n"
echo "####################################################\n"

mysql -p acsDB < acsDB.sql

echo "####################################################\n"
echo "BUILDING BACKEND APPLICATION ................ \n"
echo "####################################################\n"

mvn clean install

echo "####################################################\n"
echo "COPY SERVICE TO SYSTEMD ................ \n"
echo "####################################################\n"

cp acs-backend.service /etc/systemd/system/acs-backend.service

echo "####################################################\n"
echo "START ACS SERVICE ................ \n"
echo "####################################################\n"

systemctl start acs-backend.service