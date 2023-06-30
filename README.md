# Task

- Create a random generator class which takes as input a seed of numbers and their associated probabilities. 
- Implement the method nextNum() and a minimal but effective set of unit tests. 
- The method nextNum() should only ever return one of the seeded numbers and given enough calls the probability of the output should converge on the seed probability. 


# Running the project

- Make sure you have Maven installed on your system.
- Navigate to the project's root directory (where the pom.xml file is located) using the command line.
- Build the project by running the following command:
  ```console
  mvn clean install
  ```

- This command will compile the source code, run the tests, and package the project into a JAR file.
- After a successful build, you can run the RandomGenApplication class using the following command:
  ```console
  java -cp target/random-gen-test-1.0-SNAPSHOT.jar com.jmorrissey.RandomGenApplication
  ```
  
- The application will run and print the results to the console.

# Running the Tests
- With maven installed as above run
- ```console
  mvn test
  ```

