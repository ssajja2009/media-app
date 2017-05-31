# media-app
An app that retrieve video objects from a REST service and counts hd objects

Build using Maven
-----------------
mvn package

Run Using Maven
--------------
mvn exec:java -Dexec.mainClass="main.MediaApplication"

Build using java
-------------------
java -cp lib/gson-2.8.0.jar main/MediaApplication.java service/MediaService.java utils/LogUtils.java -d target/classes

Run using java

java -classpath target/classes:lib/gson-2.8.0.gar  main.MediaApplication
