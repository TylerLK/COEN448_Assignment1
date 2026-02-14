# COEN448_Assignment1

This repository will be used to contain the code and documentation of our solution to the first assignment for the COEN 448.

## Team

- Tyler Kassis - 40231047
- Sunil Kublalsingh - 40212432

---

## Prerequisites

- Java JDK 11 or newer installed and available on `PATH`.
- Maven (3.6+) installed and available on `PATH`.
- (Optional) VS Code with the Java Extension Pack for editing and debugging.

---

## Build

From the repository root (the folder that contains `pom.xml`), run:

```bash
mvn compile
```

---

## Run (CLI)

The program's main class is `coen448.computablefuture.test.AsyncProcessor`.

To run from the command line:

```bash
mvn exec:java -Dexec.mainClass="coen448.computablefuture.test.AsyncProcessor"
```

The program reads interactive input from stdin (commands described by the
menu shown when the program starts). When running from VS Code, set the
`console` to `integratedTerminal` in the launch configuration so the program can
read user input.

---

## Run Tests

To execute the unit tests:

```bash
mvn test
```

Test outputs and reports will be generated in `target/surefire-reports/`.

---

## Running in VS Code

- Open the project folder in VS Code.
- Install the **Java Extension Pack** (if not already installed).
- Use the provided `.vscode/launch.json` configuration named `App`.
- Make sure the configuration contains `"console": "integratedTerminal"` so the program can read stdin from the integrated terminal.
- Start the program with **F5** (debug) or **Ctrl+F5** (run without debugging).

---

## Running in Eclipse

- Import the project: File > Import... > Maven > Existing Maven Projects and select the folder containing `pom.xml`.
- After import, right-click the project > Run As > Maven build... and use goal `exec:java -Dexec.mainClass="coen448.computablefuture.test.AsyncProcessor"`, or
- Create a Java run configuration: Run > Run Configurations... > Java Application, set the **Main class** to `coen448.computablefuture.test.AsyncProcessor`, apply, and Run. The Eclipse Console will accept interactive input.

---

## Project Structure

- `src/main/java` — application source code
- `src/test/java` — JUnit tests
- `pom.xml` — Maven project file

---

## AI Usage

AI Usage Claim

### Tools Used

The primary tools used throughout the development and testing of this assignment were GitHub Copilot, Codex (OpenAI), and ChatGPT (OpenAI).  More specifically, the primary usage of GitHub Copilot was performed through marketplace extensions on our team’s varying IDEs (e.g., VSCode, Eclipse, etc.).  Furthermore, the integrated AI code review feature on our GitHub repository was employed during every Pull Request.  For the latter case, the embedded GitHub version normally catches minor spelling errors or improper formatting.  Finally, in terms of code generation, the two main uses for generative AI while vibe coding were for Test Case Development and Mechanism Validation.  Finally, these tools were also used to learn more about Microservices, `CompletableFuture`, and Asynchronous Concurrency Failure Semantics.  In each case, the COSTAR method was applied for enhanced prompt engineering while employing vibe coding (See Section 6.2).

### Prompts

#### Learning About Assignment Context (*ChatGPT*)

**Context:**
I would like to learn about the following concepts (In the Java programming language)

- `Microservice` interface
- `CompletableFuture` class
- Asynchronous Concurrency Failure Semantics
- How they might be used together.

**Outcome:**

- Implement the failure semantics for fail-fast, fail-partial, and fail-soft policies
- Implement unit tests for each policy.

**Steps:**

1. Explain each of the desired concepts simply and concisely, providing important functions
2. Explain each failure semantic policy simply and concisely, with examples
3. Explain how these concepts tie together, providing examples.

**Tools:**

- Junit5

**Audience:**

- Developer implementing asynchronous concurrency failure semantics, having no prior knowledge or experience with this type of programming.

**Relevance:**

- Builds foundational knowledge for upcoming assignments and exams.

---

#### Liveness and Nondeterminism Test Generation (*Codex*)

**Context:**
We are doing an assignment that covers the importance of the implementation of explicit exception handling policies in Microservices for concurrent execution. Specifically, we are covering the cases: Fail-Fast, Fail-Partial, and Fail-Soft.

We have the code written in Java and have written specific tests for each of the cases. Now, we want to test for liveness and nondeterminism.

General Rules:

– No `Mockito`
– All futures must be awaited with timeouts
– tests must verify policy semantics

Strict Category Requirements:

- Liveness: no deadlock or infinite wait (*use timeouts*)(*try using more than 10 micro-services at once*)
- Nondeterminism: completion order observed (*not asserted*)

**Outcome:**
Design JUnit unit tests to verify the correctness of the application functionality for the following two issues:

- Liveness
- Nondeterminism

Ensure that there are no duplicate tests. If any helper classes and/or methods are needed, properly document them with javadocs.

**Steps:**

1. Derive test cases per command, isolating side effects.
2. Use the Arrange–Act–Assert structure for every test.
3. Validate the results
4. Add print statements to display the results

**Tools:**

- `JUnit 5`
- `Maven`

**Relevance:**

Tests help ensure the program is working as it should be.

#### Failure Semantics Policy Review (*GitHub Copilot*)

**Context:**

- Current code: `AsyncProcessor.java`, `Microservice.java`
- Constraint: Asynchronous Failure Semantics Policies must be implemented correctly.

**Outcome:**

- Produce a revised file, `AsyncProcessorRevised.java`, that verifies each failure semantic policy.
- Produce a revised file, `MicroserviceRevised.java`, that verifies the implementation of the Microservice interface.

**Steps:**

1. Read the files `../AsyncProcessor.java` and `../Microservice.java` in the Maven build project root.
2. Revise the behaviour of the Fail-Fast policy and make any necessary changes.
3. Revise the behaviour of the Fail-Partial policy and make any necessary changes.
4. Revise the behaviour of the Fail-Soft policy and make any necessary changes.
5. Revise the behaviour of the Microservice interface and make any necessary changes.
6. Clearly indicate with comments where changes are made.

**Tools:**

- `Java 9+` Libraries
- `Java 8` Libraries

**Audience:**

- Development team implementing the three failure semantics policies.

**Relevance:**

- Confirms proper behaviour and mechanisms for each policy.
- Confirms policy compatibility with Microservice implementation.

**Additional constraints:**

- The method prototypes (*Scope, name, parameters*) cannot change.
- Fail-Fast: Uses `CompletableFuture.allOf()`.  Propagates Exceptions.  No partial results returned.
- Fail-Partial: Handles failures per service.  Returns successful results only (*or clearly documented markers*).  No exception escapes to the caller.
- Fail-Soft: Uses fallback values for failures.  Always completes normally.  Clearly documents the risks of masking failures.

#### Failure Semantics Testing Review (*GitHub Copilot*)

**Context:**

- Current code: `AsyncProcessor.java`, `Microservice.java`, `AsyncProcessorTest.java`
- Constraint: Asynchronous Failure Semantics Policy Testing must provide a minimum of test cases for 100% coverage.

**Outcome:**

- Produce a revised file, `AsyncProcessorTestRevised.java`, containing any added tests for coverage, while checking for redundant tests.

**Steps:**

1. Read the files `../AsyncProcessor.java`, `../Microservice.java`, `../AsyncProcessorTest.java` in the Maven build project root.
2. Revise the tests for the Fail-Fast policy and add any necessary test cases.
3. Revise the tests for the Fail-Partial policy and add any necessary test cases.
4. Revise the tests for the Fail-Soft policy and add any necessary test cases.
5. Revise the tests for liveness and add any necessary test cases.
6. Revise the tests for nondeterminism and add any necessary test cases.
7. Comment out any redundant test cases.
8. Clearly indicate with comments where changes are made, including added, deleted, or erroneous tests.

**Tools:**

- `Java 9+` Libraries
- `Java 8` Libraries
- `JUnit5`

**Audience:**

- Development team testing the implementation of all three failure semantics policies.

**Relevance:**

- Confirms proper behaviour and mechanisms for each policy.
- Confirms policy compatibility with Microservice implementation.
- Confirms proper testing methods for each policy.

Additional constraints:

- No `Mockito`.
- All futures must be awaited with timeouts.
- Tests must verify policy semantics.
- Required Test Categories: Fail-Fast (*Failure propagates (`assertThrows`)*), Fail-Partial (*Partial Results Returned*), Fail-Soft (*Fallback values used*), Liveness (*No deadlock or infinite wait; > 10 Microservices*), Nondeterminism (*Completion order observed (Not asserted)*)
