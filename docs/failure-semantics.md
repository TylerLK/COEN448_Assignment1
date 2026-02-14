# Asynchronous Concurrency Failure Semantics

---

## 1. Fail-Fast Policy

### 1.1 Definition

As with the other policies, different tasks will be completed concurrently and asynchronously. However, the fail-fast policy is the most sensitive to errors. If any single microservice execution fails, the entire concurrent operation is immediately terminated and an exception is propagated to the caller immediately. Unlike other policies, the caller does not receive any partial or fallback results if a failure occurs; instead, they must handle the resulting error.

### 1.2 Examples

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

### 1.3 Usage

This policy should be used in systems where the results are interdependent, and a single failure renders the entire result set invalid. A typical use is the correctness-critical systems where partial results are invalid. By failing fast, the system avoids wasting resources on remaining tasks once it is clear the overall operation cannot succeed.

---

## 2. Fail-Partial Policy

### 2.1 Definition

As with the other policies, different tasks will be completed concurrently and asynchronously. Similar to the fail-soft policy, the caller will receive no exceptions and the program will complete normally. However, instead of masking failures with a fallback value, the fail-partial policy simply ignores failed tasks. The results of any failed microservice executions are discarded, meaning the caller will receive a list of results containing only the successful executions.

### 2.2 Examples

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

### 2.3 Usage

The fail-partial policy is suitable for scenarios where partial data is still highly valuable to the user. A typical use is dashboards, analytics, or aggregation where partial results are useful. For instance, a search aggregator that polls multiple sources can use this policy to return whatever results are available, even if one or two sources are temporarily down. This ensures high availability and responsiveness at the cost of potential incompleteness.

---

## 3. Fail-Soft Policy

### 3.1 Definition

As with the other policies, different tasks will be completed concurrently and asynchronously.  However, this policy will take a similar route to the fail-partial policy, in that the caller will receive no exceptions.  However, the fail-soft failure semantic policy will take failure-handling a step further.  It always allows the concurrent program to complete normally (*i.e., without throwing an exception*).  To achieve this behaviour, it will replace any failed microservice executions by a dedicated, pre-defined fallback value.  As such, although some of the microservices have potentially failed during the program's operation, the caller will still receive the same number of results as there are microservices.

### 3.2 Examples

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

### 3.3 Usage

This type of failure semantic should be used in cases where systems experiencing failure will not lead to catastrophic consequences.  In other words, if a failed microservice will not cause a total system failure, but rather cause the system's output to deteriorate, then the fail-soft policy is a good choice.  An example of a real-world application for such a failure semantic would be video streaming, where dropped frames represent failed microservices.  The system would replace any dropped frames with a pre-defined fallback value to avoid freezing.

---

## 4. Risks Associated with Masking Failure

As seen in previous sections, choosing a failure policy is situation-dependent, and can vary widely with regard to the criticality of a system (*See Section 1.3, 2.3, & 3.3*).  However, it is also important to know how well a system can perform its desired function, especially when created using microservices.  A crucial aspect of creating concurrent software systems is the reliability of its process (*i.e., the ability to prevent or withstand errors*).  While such a system should handle errors effectively (*especially if catastrophic*), it is equally important to achieve high performance (*e.g., speed, throughput, etc.*).  On the one hand, the Fail-Fast policy will terminate the process if even a single error occurs.  While this is important for highly critical systems, this could also greatly hinder performance.  On the other, using the Fail-Partial and Fail-Soft will always return a result, which is important for low-criticality, high-volume systems.  However, it will also create the illusion of perfect performance, in that any failed microservice is either not returned or replaced.  Therefore, while masking failures can allow for seamless, continuous use from the perspective of the caller, this might also create a false sense of system reliability and accuracy.  Finally, to illustrate this trade-off, a real-world example can be seen through the field of large data processing systems.  If a fail-soft policy were to be implemented for such a system, a high number of failed microservices would affect the accuracy of the results.
