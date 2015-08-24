#!/bin/sh

##JAVA
sudo apt-get purge openjdk-\*
sudo mkdir -p /usr/local/java
sudo cp -r jre-8u45-linux-i586.tar.gz /usr/local/java
cd /usr/local/java
sudo tar xvzf jre-8u45-linux-i586.tar.gz

##ant
sudo apt-get install ant

##OVS

##Flow
sudo apt-get install

##queue, init in switch; 3 queues, based on experental bandWidth, runtime bandWidth need to be abtain automaticly
ovs-vsctl -- set port s3-eth1 qos=@newqos -- --id=@newqos create qos type=linux-htb \
queues=0=@q0,1=@q1 -- --id=@q0 create queue other-config:min-rate=200000000 \
other-config:max-rate=800000000 -- --id=@q1 create queue other-config:min-rate=50000 \
other-config:max-rate=50000000

ovs-vsctl -- set port s3-eth1 qos=@newqos -- --id=@newqos create qos type=linux-htb \
queues=0=@q0,1=@q1 -- --id=@q0 create queue other-config:min-rate=200000000 \
other-config:max-rate=800000000 -- --id=@q1 create queue other-config:min-rate=50000 \
other-config:max-rate=50000000

ovs-vsctl -- set port s3-eth1 qos=@newqos -- --id=@newqos create qos type=linux-htb \
queues=0=@q0,1=@q1 -- --id=@q0 create queue other-config:min-rate=200000000 \
other-config:max-rate=800000000 -- --id=@q1 create queue other-config:min-rate=50000 \
other-config:max-rate=50000000


##note
