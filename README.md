Prequisites
- JDK 8 or above
- Maven 3.3.0 or above

To generate a war...
- Clone the repository
- Browse to the base directory (the one that contains the pom.xml) via the command prompt/shell
- Run the command "mvn install"
- The war should now be available within the <base directory>/target folder

To run on eclipse
- Import the project as an "Existing Maven Project" by specifying the path of the base directory
- The most convenient way to run is probably via the "Run Jetty Run" plugin of Eclipse

Once deployed on eclipse or on a Servlet container, the application is available at the following URL

http://localhost:8090/ECLBuilder

User the id/password user1/user1pw to log in.
