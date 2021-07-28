# Install Java Agent
To install the Java Agent for your Tibco instance follow these steps:    
1. Download the latest Java Agent using the zip file.  (https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/)
2. Extract the zip file onto the file system.   
3. Edit newrelic.yml and substitute your license key for the string <%= license_key %>.   
4. Locate the TRA file used by the application.  
5. Locate the property java.extended.properties in the TRA file.  If it does not exist then create it.
6. Add -javaagent:*path_to_newrelic.jar* to java.extended.properties
7. Save the TRA file
8. Restart the Tibco instance
