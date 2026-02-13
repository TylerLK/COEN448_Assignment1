# Asynchronous Concurrency Failure Semantics

---

## Fail-Fast Policy

### Definition

### Examples

### Usage

---

## Fail-Partial Policy

### Definition

### Examples

### Usage

---

## Fail-Soft Policy

### Definition

As with the other policies, different tasks will be completed concurrently and asynchronously.  However, this policy will take a similar route to the fail-partial policy, in that the caller will receive no exceptions.  However, the fail-soft failure semantic policy will take failure-handling a step further.  It always allows the concurrent program to complete normally (*i.e., without throwing an exception*).  To achieve this behaviour, it will replace any failed microservice executions a by a dedicated, pre-defined fallback value.  As such, although some of the microservices have potentially failed during the program's operation, the caller will still receive the same number of results as there are microservices.

### Examples

For these examples, the fallback value will be "FALLBACK"

1. Microservice A --> Execution Success --> Returns "SUCCESS"
   Microservice B --> Execution Success --> Returns "SUCCESS"
   Microservice C --> Execution Success --> Returns "SUCCESS"

   **Result** ------> "SUCCESS, SUCCESS, SUCCESS"
   <br>

2. Microservice A --> Execution Success --> Returns "SUCCESS"
   Microservice B --> Execution Failure --> Returns "FALLBACK"
   Microservice C --> Execution Success --> Returns "SUCCESS"

   **Result** ------> "SUCCESS, FALLBACK, SUCCESS"  
   <br>

### Usage

This type of failure semantic should be used in cases where systems experiencing failure will not lead to catastrophic consequences.  In other words, if a failed microservice will not cause a total system failure, but rather cause the system's output to deteriorate, then the fail-soft policy is a good choice.  An example of a real-world application for such a failure semantic would be video streaming, where dropped frames represent failed microservices.  The system would replace any dropped frames with a pre-defined fallback value to avoid freezing.

---

## Risks Associated with Masking Failure
