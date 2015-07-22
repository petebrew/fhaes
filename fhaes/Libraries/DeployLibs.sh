# The Jars within this folder are those not found in standard Maven repositories.  
# The following commands will install them to the TRiDaS Maven repository so that 
# they are available to others without hassles.  


mvn deploy:deploy-file  
  -DgroupId=org.openstreetmap         
  -DartifactId=jmapviewer                
  -Dversion=1.0.2   
  -Dpackaging=jar 
  -Dfile=jmapviewer-1.0.2.jar    
  -DrepositoryId=tridas-releases  
  -Durl=http://maven.tridas.org/repository/tridas-releases


# Alternatively you can install them locally using these commands

mvn install:install-file  
  -DgroupId=org.openstreetmap         
  -DartifactId=jmapviewer                
  -Dversion=1.0.2  
  -Dpackaging=jar 
  -Dfile=jmapviewer-1.0.2.jar 

