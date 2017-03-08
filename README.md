##Description

This is exercise project and built on top of Akka with Scala. I have used single persistent actor as 
identifier generator, and persist all data in LevelDB. 

All identifier request was processed as event source, and persisted into LevelDB, whenever reboot actor would recover 
data from LevelDB. Each identifier was make up by following fields:

    [uuid].[id]
    uuid - it's node id and set manually in configuration, and unique in distributed environment.
    id - it's sequential number - Long
    
    
    example:
    
    "7732be3a-1550-3325-b9a5-975fd3c359eb.3120"
    
I exposed Rest API to retrieve identifier and return json as result. I try to improve performance and scalability, so 
used Cluster Sharding. You could added various nodes into Cluster in order to scale.  

##How to configure

2 config files under resources fold

    application.conf - major configuration for application, you can setup basic information inside for application
    reference.conf - it's configuration for persistent actor


##How to try
    0. run application
    gradle run
    
    1. warm up 
    http://localhost:8989/identifier/test
    
    2. get single identifier 
    http://localhost:8989/identifier/id
    
    example: {"identifiers":[{"id":3120,"idSeq":"7732be3a-1550-3325-b9a5-975fd3c359eb.3120","createdTime":1483622594879}]}
    
    3. get a batch of identifiers, num as parameter is number of how many identifiers
    http://localhost:8989/identifiers/{num}
    
##Run Single Node
    1. setup main class in build.gradle
        mainClassName = "org.identifier.SingleIdentifierApp"
    2. try 8888 port
       http://localhost:8888/identifier/test
       http://localhost:8888/identifier/id
       http://localhost:8888/identifiers/{num}
       
##Run Multi-Nodes
    1. set different properties in application.conf
    application.uuid = 7732be3a-1550-3325-b9a5-975fd3c359eb
    application.http.port=8081 
    clustering.port=2552
    
    `OR`
     
     build.gradle
     
     applicationDefaultJvmArgs = ["-Dapplication.uuid=7732be3a-1550-3325-b9a5-975fd3c359eb -Dapplication.http.port=9899 -Dclustering.port=2552"]


    2. set different directory for joural of LevelDB in reference.conf
    
    3. Run application in different JVM

##Why I choose them
Scala is a pure functional language, which make Java developer life easier. Played with Akka not too long, and have a passion for technologies. Prepare to use in real projects.



##How long I have taken

Hours to work on that, include thinking, testing and writing README, etc.

## Tools

IntelliJ / Gradle 2.14 /Scala 2.12.0/ Unix Commands