# Asynchronous Concurrency Failure Semantics

---

## Fail-Fast Policy

### Definition

As with the other policies, different tasks will be completed concurrently and asynchronously. However, the fail-fast policy is the most sensitive to errors. If any single microservice execution fails, the entire concurrent operation is immediately terminated and an exception is propagated to the caller immediately. Unlike other policies, the caller does not receive any partial or fallback results if a failure occurs; instead, they must handle the resulting error.

### Examples

1. Microservice A --> Execution Success --> Returns "SUCCESS"  
   Microservice B --> Execution Success --> Returns "SUCCESS"  
   Microservice C --> Execution Success --> Returns "SUCCESS"  

   **Result** ------> "SUCCESS, SUCCESS, SUCCESS"
   <br>

2. Microservice A --> Execution Success --> Returns "SUCCESS"  
   Microservice B --> Execution Failure --> Throws Exception  
   Microservice C --> Execution Success --> (Execution Terminated)  

   **Result** ------> **Exception Thrown**
   <br>

### Usage

This policy should be used in systems where the results are interdependent, and a single failure renders the entire result set invalid. A typical use is the correctness critical systems where partial results are invalid. By failing fast, the system avoids wasting resources on remaining tasks once it is clear the overall operation cannot succeed.

---

## Fail-Partial Policy

### Definition

As with the other policies, different tasks will be completed concurrently and asynchronously. Similar to the fail-soft policy, the caller will receive no exceptions and the program will complete normally. However, instead of masking failures with a fallback value, the fail-partial policy simply ignores failed tasks. The results of any failed microservice executions are discarded, meaning the caller will receive a list of results containing only the successful executions.

### Examples

1. Microservice A --> Execution Success --> Returns "SUCCESS-A"  
   Microservice B --> Execution Success --> Returns "SUCCESS-B"  
   Microservice C --> Execution Success --> Returns "SUCCESS-C"  

   **Result** ------> "SUCCESS-A, SUCCESS-B, SUCCESS-C"
   <br>

2. Microservice A --> Execution Success --> Returns "SUCCESS-A"  
   Microservice B --> Execution Failure --> Result Discarded  
   Microservice C --> Execution Success --> Returns "SUCCESS-C"  

   **Result** ------> "SUCCESS-A, SUCCESS-C" 
   <br>

### Usage

The fail-partial policy is suitable for scenarios where partial data is still highly valuable to the user. A typical use is dashboards, analytics, or aggregation where partial results are useful. For instance, a search aggregator that polls multiple sources can use this policy to return whatever results are available, even if one or two sources are temporarily down. This ensures high availability and responsiveness at the cost of potential incompleteness.

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
