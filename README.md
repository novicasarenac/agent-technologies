# Agent technologies

Framework for intelligent Agents development built using Java EE and AngularJS. Application operates in cluster. Each Agent Center(node) is connected to other nodes. Master node controls the cluster. Communication between nodes is implemeted with JeroMQ (Java implementation of ZeroMQ). Web Sockets and Jax-RS are used for communication between client and server.

## Setup ##

To run application you need [WildFly](http://wildfly.org/) enterprise application server (formerly JBoss AS) and configure endpoints (Queues):
```
java:/jms/queue/activateListener
java:/jms/queue/heartbeatListener
java:/jms/queue/agentsCommunicationListener
java:/jms/queue/notificationListener
java:/jms/queue/messageListener
```
Publish AgentTechnologiesEnterprise.ear to ```/deployments``` folder into Wildfly.

## Running application ##
To start master node of application navigate to ```/bin``` folder and start Wildfly:
```
.\standalone.bat --server-config=standalone-full.xml
```
To start other (non-master) node you have to set port offset using ```"jboss.socket.binding.port-offset"``` property and address of master node using ```master``` property. Also you can set (optionally) alias for node using ```alias```  property and file with supporting agent types using ```filename``` property. Start Wildfly:
```
.\standalone.bat -D"jboss.socket.binding.port-offset"=100 -Dfilename="agents2.txt" -Dmaster="localhost" --server-config=standalone-full.xml
```
