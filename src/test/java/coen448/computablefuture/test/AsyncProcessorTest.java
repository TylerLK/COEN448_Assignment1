package coen448.computablefuture.test;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AsyncProcessorTest {
	// Setting up an object of AsyncProcessor to be called in all test cases.
	private AsyncProcessor processor;
	
	@BeforeEach
	void setUp() {
		processor = new AsyncProcessor();
	}
	
	// Fail-Fast Policy Tests
	@Test
    @DisplayName("[Fail-Fast] All Microservices are Successful")
    public void testProcessAsyncFailFastAllSuccess() throws ExecutionException, InterruptedException, TimeoutException {
    	// Create a list of microservices to be processed.
    	List<Microservice> services = List.of(
    		new Microservice(),
    		new Microservice(),
    		new Microservice()
    	);
    	
    	// Create a list of messages to be returned by the microservices.
    	List<String> messages = List.of(
    			"msg-a",
    			"msg-b",
    			"msg-c"
    	);
    	
    	// Call the processAsyncFailFast method to process the microservices.
    	CompletableFuture<String> future = processor.processAsyncFailFast(services, messages);
    	String result = future.get(5, TimeUnit.SECONDS);
    	
    	// Assertions to compare result to expected value.
    	assertTrue(result.contains("MSG-A"));
    	assertTrue(result.contains("MSG-B"));
    	assertTrue(result.contains("MSG-C"));
    	assertEquals("MSG-A, MSG-B, MSG-C", result);
    	System.out.println("[Fail-Fast] All Microservices are Successful - Test Successful: " + result + "\n");
    }
	
	@Test
    @DisplayName("[Fail-Fast] Single Microservice Failure")
    public void testProcessAsyncFailFastSingleFailure() throws ExecutionException, InterruptedException, TimeoutException {
    	// Create a list of microservices to be processed.  One will contain a failure.
    	List<Microservice> services = List.of(
			new Microservice(),
			new Microservice() {
				// Override the successful microservice behaviour to return a failure instead.
				@Override
				public CompletableFuture<String> retrieveAsync(String input) {
					return retrieveAsyncFail("Microservice B Failure");
				}
			},
			new Microservice()
		);
    	
    	// Create a list of messages to be returned by the microservices.
    	List<String> messages = List.of(
    			"msg-a",
    			"msg-b",
    			"msg-c"
    	);
    	
    	// Call the processAsyncFailFast method to process the microservices.
    	CompletableFuture<String> future = processor.processAsyncFailFast(services, messages);
    	
    	// Assertions to compare result to expected value.
    	ExecutionException ex = assertThrows(ExecutionException.class, () -> future.get(5, TimeUnit.SECONDS));
    	assertTrue(ex.getCause() instanceof RuntimeException);
    	System.out.println("[Fail-Fast] Single Microservice Failure - Test Successful: " + ex.getCause().getMessage() + "\n");
    }
	
	@Test
    @DisplayName("[Fail-Fast] Multiple Microservice Failures")
    public void testProcessAsyncFailFastMultipleFailure() throws ExecutionException, InterruptedException, TimeoutException {
    	// Create a list of microservices to be processed.  Multiple will contain failures.
    	List<Microservice> services = List.of(
			new Microservice() {
				// Override the successful microservice behaviour to return a failure instead.
				@Override
				public CompletableFuture<String> retrieveAsync(String input) {
					return retrieveAsyncFail("Microservice A Failure");
				}
			},
			new Microservice(),
			new Microservice() {
				// Override the successful microservice behaviour to return a failure instead.
				@Override
				public CompletableFuture<String> retrieveAsync(String input) {
					return retrieveAsyncFail("Microservice C Failure");
				}
			}
		);
    	
    	// Create a list of messages to be returned by the microservices.
    	List<String> messages = List.of(
    			"msg-a",
    			"msg-b",
    			"msg-c"
    	);
    	
    	// Call the processAsyncFailFast method to process the microservices.
    	CompletableFuture<String> future = processor.processAsyncFailFast(services, messages);
    	
    	// Assertions to compare result to expected value.
    	ExecutionException ex = assertThrows(ExecutionException.class, () -> future.get(5, TimeUnit.SECONDS));
    	assertTrue(ex.getCause() instanceof RuntimeException);
    	System.out.println("[Fail-Fast] Multiple Microservice Failures - Test Successful: " + ex.getCause().getMessage() + "\n");
    }
	
	@Test
    @DisplayName("[Fail-Fast] All Microservice Failures")
    public void testProcessAsyncFailFastAllFailure() throws ExecutionException, InterruptedException, TimeoutException {
    	// Create a list of microservices to be processed.  All contain failures.
    	List<Microservice> services = List.of(
			new Microservice() {
				// Override the successful microservice behaviour to return a failure instead.
				@Override
				public CompletableFuture<String> retrieveAsync(String input) {
					return retrieveAsyncFail("Microservice A Failure");
				}
			},
			new Microservice() {
				// Override the successful microservice behaviour to return a failure instead.
				@Override
				public CompletableFuture<String> retrieveAsync(String input) {
					return retrieveAsyncFail("Microservice B Failure");
				}
			},
			new Microservice() {
				// Override the successful microservice behaviour to return a failure instead.
				@Override
				public CompletableFuture<String> retrieveAsync(String input) {
					return retrieveAsyncFail("Microservice C Failure");
				}
			}
		);
    	
    	// Create a list of messages to be returned by the microservices.
    	List<String> messages = List.of(
    			"msg-a",
    			"msg-b",
    			"msg-c"
    	);
    	
    	// Call the processAsyncFailFast method to process the microservices.
    	CompletableFuture<String> future = processor.processAsyncFailFast(services, messages);
    	
    	// Assertions to compare result to expected value.
    	ExecutionException ex = assertThrows(ExecutionException.class, () -> future.get(5, TimeUnit.SECONDS));
    	assertTrue(ex.getCause() instanceof RuntimeException);
    	System.out.println("[Fail-Fast] All Microservice Failures - Test Successful: " + ex.getCause().getMessage() + "\n");
    }
	
	@Test
	@DisplayName("[Fail-Fast] Size Mismatch Between Services and Messages")
	public void testProcessAsyncFailFastSizeMismatch() throws ExecutionException, InterruptedException, TimeoutException {
		// Create a list of microservices to be processed.
    	List<Microservice> services = List.of(
    		new Microservice(),
    		new Microservice(),
    		new Microservice()
    	);
    	
    	// Create a list of messages to be returned by the microservices, but with a size mismatch.
    	List<String> messages = List.of(
    			"msg-a",
    			"msg-b"
    	);
    	
    	// Call the processAsyncFailFast method to process the microservices.
    	CompletableFuture<String> future = processor.processAsyncFailFast(services, messages);
    	
    	// Assertions to compare result to expected value.
    	ExecutionException ex = assertThrows(ExecutionException.class, () -> future.get(5, TimeUnit.SECONDS));
    	assertTrue(ex.getCause() instanceof IllegalArgumentException);
    	System.out.println("[Fail-Fast] Size Mismatch Between Services and Messages - Test Successful: " + ex.getCause().getMessage() + "\n");
	}
	
	// Fail-Partial Policy Tests
	@Test
    @DisplayName("[Fail-Partial] All Microservices are Successful")
    public void testProcessAsyncFailPartialAllSuccess() throws ExecutionException, InterruptedException, TimeoutException {
    	// Create a list of microservices to be processed.
    	List<Microservice> services = List.of(
    		new Microservice(),
    		new Microservice(),
    		new Microservice()
    	);
    	
    	// Create a list of messages to be returned by the microservices.
    	List<String> messages = List.of(
    			"msg-a",
    			"msg-b",
    			"msg-c"
    	);
    	
    	// Call the processAsyncFailPartial method to process the microservices.
    	CompletableFuture<String> future = processor.processAsyncFailPartial(services, messages);
    	String result = future.get(5, TimeUnit.SECONDS);
    	
    	// Assertions to compare result to expected value.
    	assertTrue(result.contains("MSG-A"));
    	assertTrue(result.contains("MSG-B"));
    	assertTrue(result.contains("MSG-C"));
    	assertEquals("MSG-A, MSG-B, MSG-C", result);
    	System.out.println("[Fail-Partial] All Microservices are Successful - Test Successful: " + result + "\n");
    }
    
    @Test
    @DisplayName("[Fail-Partial] Single Microservice Failure")
    public void testProcessAsyncFailPartialSingleFailure() throws ExecutionException, InterruptedException, TimeoutException {
    	// Create a list of microservices to be processed.  One will contain a failure.
    	List<Microservice> services = List.of(
			new Microservice(),
			new Microservice() {
				// Override the successful microservice behaviour to return a failure instead.
				@Override
				public CompletableFuture<String> retrieveAsync(String input) {
					return retrieveAsyncFail("Microservice B Failure");
				}
			},
			new Microservice()
		);
    	
    	// Create a list of messages to be returned by the microservices.
    	List<String> messages = List.of(
    			"msg-a",
    			"msg-b",
    			"msg-c"
    	);
    	
    	// Call the processAsyncFailPartial method to process the microservices.
    	CompletableFuture<String> future = processor.processAsyncFailPartial(services, messages);
    	String result = future.get(5, TimeUnit.SECONDS);
    	
    	// Assertions to compare result to expected value.
    	assertTrue(result.contains("MSG-A"));
    	assertFalse(result.contains("MSG-B"));
    	assertTrue(result.contains("MSG-C"));
    	assertEquals("MSG-A, MSG-C", result);
    	System.out.println("[Fail-Partial] Single Microservice Failure - Test Successful: " + result + "\n");
    }
    
    @Test
    @DisplayName("[Fail-Partial] Multiple Microservice Failures")
    public void testProcessAsyncFailPartialMultipleFailure() throws ExecutionException, InterruptedException, TimeoutException {
    	// Create a list of microservices to be processed.  Multiple will contain failures.
    	List<Microservice> services = List.of(
			new Microservice() {
				// Override the successful microservice behaviour to return a failure instead.
				@Override
				public CompletableFuture<String> retrieveAsync(String input) {
					return retrieveAsyncFail("Microservice A Failure");
				}
			},
			new Microservice(),
			new Microservice() {
				// Override the successful microservice behaviour to return a failure instead.
				@Override
				public CompletableFuture<String> retrieveAsync(String input) {
					return retrieveAsyncFail("Microservice C Failure");
				}
			}
		);
    	
    	// Create of list of messages to be returned by the microservices.
    	List<String> messages = List.of(
    			"msg-a",
    			"msg-b",
    			"msg-c"
    	);
    	
    	// Call the processAsyncFailSoft method to process the microservices.
    	CompletableFuture<String> future = processor.processAsyncFailPartial(services, messages);
    	String result = future.get(5, TimeUnit.SECONDS);
    	
    	// Assertions to compare result to expected value.
    	assertFalse(result.contains("MSG-A"));
    	assertTrue(result.contains("MSG-B"));
    	assertFalse(result.contains("MSG-C"));
    	assertEquals("MSG-B", result);
    	System.out.println("[Fail-Partial] Multiple Microservice Failures - Test Successful: " + result + "\n");
    }
    
    @Test
    @DisplayName("[Fail-Partial] All Microservice Failures")
    public void testProcessAsyncFailPartialAllFailure() throws ExecutionException, InterruptedException, TimeoutException {
    	// Create a list of microservices to be processed.  All contain failures.
    	List<Microservice> services = List.of(
			new Microservice() {
				// Override the successful microservice behaviour to return a failure instead.
				@Override
				public CompletableFuture<String> retrieveAsync(String input) {
					return retrieveAsyncFail("Microservice A Failure");
				}
			},
			new Microservice() {
				// Override the successful microservice behaviour to return a failure instead.
				@Override
				public CompletableFuture<String> retrieveAsync(String input) {
					return retrieveAsyncFail("Microservice B Failure");
				}
			},
			new Microservice() {
				// Override the successful microservice behaviour to return a failure instead.
				@Override
				public CompletableFuture<String> retrieveAsync(String input) {
					return retrieveAsyncFail("Microservice C Failure");
				}
			}
		);
    	
    	// Create of list of messages to be returned by the microservices.
    	List<String> messages = List.of(
    			"msg-a",
    			"msg-b",
    			"msg-c"
    	);
    	
    	// Ensure that no exceptions are thrown to the caller.
    	assertDoesNotThrow(() -> {
	    	// Call the processAsyncFailSoft method to process the microservices.
	    	CompletableFuture<String> future = processor.processAsyncFailPartial(services, messages);
	    	String result = future.get(5, TimeUnit.SECONDS);
	    	
	    	// Assertions to compare result to expected value.
	    	assertFalse(result.contains("MSG-A"));
	    	assertFalse(result.contains("MSG-B"));
	    	assertFalse(result.contains("MSG-C"));
	    	assertTrue(result.isEmpty());
	    	System.out.println("[Fail-Partial] All Microservice Failures - Test Successful: N/A\n");
    	});
    }
    
    @Test
	@DisplayName("[Fail-Partial] Size Mismatch Between Services and Messages")
	public void testProcessAsyncFailPartialSizeMismatch() throws ExecutionException, InterruptedException, TimeoutException {
		// Create a list of microservices to be processed.
    	List<Microservice> services = List.of(
    		new Microservice(),
    		new Microservice(),
    		new Microservice()
    	);
    	
    	// Create a list of messages to be returned by the microservices, but with a size mismatch.
    	List<String> messages = List.of(
    			"msg-a",
    			"msg-b"
    	);
    	
    	// Call the processAsyncFailSoft method to process the microservices.
    	CompletableFuture<String> future = processor.processAsyncFailPartial(services, messages);
    	
    	// Assertions to compare result to expected value.
    	ExecutionException ex = assertThrows(ExecutionException.class, () -> future.get(5, TimeUnit.SECONDS));
    	assertTrue(ex.getCause() instanceof IllegalArgumentException);
    	System.out.println("[Fail-Partial] Size Mismatch Between Services and Messages - Test Successful: " + ex.getCause().getMessage() + "\n");
	}
    
    // Fail-Soft Policy Tests    
    @Test
    @DisplayName("[Fail-Soft] All Microservices are Successful")
    public void testProcessAsyncFailSoftAllSuccess() throws ExecutionException, InterruptedException, TimeoutException {
    	// Create a list of microservices to be processed.
    	List<Microservice> services = List.of(
    		new Microservice(),
    		new Microservice(),
    		new Microservice()
    	);
    	
    	// Create a list of messages to be returned by the microservices.
    	List<String> messages = List.of(
    			"msg-a",
    			"msg-b",
    			"msg-c"
    	);
    	
    	// Create a fallback value in the event of a microservice failure.
    	String fallbackValue = "FALLBACK";
    	
    	// Call the processAsyncFailSoft method to process the microservices.
    	CompletableFuture<String> future = processor.processAsyncFailSoft(services, messages, fallbackValue);
    	String result = future.get(5, TimeUnit.SECONDS);
    	
    	// Assertions to compare result to expected value.
    	assertFalse(result.contains(fallbackValue));
    	assertTrue(result.contains("MSG-A"));
    	assertTrue(result.contains("MSG-B"));
    	assertTrue(result.contains("MSG-C"));
    	assertEquals("MSG-A, MSG-B, MSG-C", result);
    	System.out.println("[Fail-Soft] All Microservices are Successful - Test Successful: " + result + "\n");
    }
    
    @Test
    @DisplayName("[Fail-Soft] Single Microservice Failure")
    public void testProcessAsyncFailSoftSingleFailure() throws ExecutionException, InterruptedException, TimeoutException {
    	// Create a list of microservices to be processed.  One will contain a failure.
    	List<Microservice> services = List.of(
			new Microservice(),
			new Microservice() {
				// Override the successful microservice behaviour to return a failure instead.
				@Override
				public CompletableFuture<String> retrieveAsync(String input) {
					return retrieveAsyncFail("Microservice B Failure");
				}
			},
			new Microservice()
		);
    	
    	// Create a list of messages to be returned by the microservices.
    	List<String> messages = List.of(
    			"msg-a",
    			"msg-b",
    			"msg-c"
    	);
    	
    	// Create a fallback value in the event of a microservice failure.
    	String fallbackValue = "FALLBACK";
    	
    	// Call the processAsyncFailSoft method to process the microservices.
    	CompletableFuture<String> future = processor.processAsyncFailSoft(services, messages, fallbackValue);
    	String result = future.get(5, TimeUnit.SECONDS);
    	
    	// Assertions to compare result to expected value.
    	assertTrue(result.contains(fallbackValue));
    	assertTrue(result.contains("MSG-A"));
    	assertFalse(result.contains("MSG-B"));
    	assertTrue(result.contains("MSG-C"));
    	assertEquals("MSG-A, FALLBACK, MSG-C", result);
    	System.out.println("[Fail-Soft] Single Microservice Failure - Test Successful: " + result + "\n");
    }
    
    @Test
    @DisplayName("[Fail-Soft] Multiple Microservice Failures")
    public void testProcessAsyncFailSoftMultipleFailure() throws ExecutionException, InterruptedException, TimeoutException {
    	// Create a list of microservices to be processed.  Multiple will contain failures.
    	List<Microservice> services = List.of(
			new Microservice() {
				// Override the successful microservice behaviour to return a failure instead.
				@Override
				public CompletableFuture<String> retrieveAsync(String input) {
					return retrieveAsyncFail("Microservice A Failure");
				}
			},
			new Microservice(),
			new Microservice() {
				// Override the successful microservice behaviour to return a failure instead.
				@Override
				public CompletableFuture<String> retrieveAsync(String input) {
					return retrieveAsyncFail("Microservice C Failure");
				}
			}
		);
    	
    	// Create of list of messages to be returned by the microservices.
    	List<String> messages = List.of(
    			"msg-a",
    			"msg-b",
    			"msg-c"
    	);
    	
    	// Create a fallback value in the event of a microservice failure.
    	String fallbackValue = "FALLBACK";
    	
    	// Call the processAsyncFailSoft method to process the microservices.
    	CompletableFuture<String> future = processor.processAsyncFailSoft(services, messages, fallbackValue);
    	String result = future.get(5, TimeUnit.SECONDS);
    	
    	// Assertions to compare result to expected value.
    	assertTrue(result.contains(fallbackValue));
    	assertFalse(result.contains("MSG-A"));
    	assertTrue(result.contains("MSG-B"));
    	assertFalse(result.contains("MSG-C"));
    	assertEquals("FALLBACK, MSG-B, FALLBACK", result);
    	System.out.println("[Fail-Soft] Multiple Microservice Failures - Test Successful: " + result + "\n");
    }
    
    @Test
    @DisplayName("[Fail-Soft] All Microservice Failures")
    public void testProcessAsyncFailSoftAllFailure() throws ExecutionException, InterruptedException, TimeoutException {
    	// Create a list of microservices to be processed.  All contain failures.
    	List<Microservice> services = List.of(
			new Microservice() {
				// Override the successful microservice behaviour to return a failure instead.
				@Override
				public CompletableFuture<String> retrieveAsync(String input) {
					return retrieveAsyncFail("Microservice A Failure");
				}
			},
			new Microservice() {
				// Override the successful microservice behaviour to return a failure instead.
				@Override
				public CompletableFuture<String> retrieveAsync(String input) {
					return retrieveAsyncFail("Microservice B Failure");
				}
			},
			new Microservice() {
				// Override the successful microservice behaviour to return a failure instead.
				@Override
				public CompletableFuture<String> retrieveAsync(String input) {
					return retrieveAsyncFail("Microservice C Failure");
				}
			}
		);
    	
    	// Create of list of messages to be returned by the microservices.
    	List<String> messages = List.of(
    			"msg-a",
    			"msg-b",
    			"msg-c"
    	);
    	
    	// Ensure that no exceptions are thrown to the caller.
    	assertDoesNotThrow(() -> {
    		// Create a fallback value in the event of a microservice failure.
	    	String fallbackValue = "FALLBACK";
	    	
	    	// Call the processAsyncFailSoft method to process the microservices.
	    	CompletableFuture<String> future = processor.processAsyncFailSoft(services, messages, fallbackValue);
	    	String result = future.get(5, TimeUnit.SECONDS);
	    	
	    	// Assertions to compare result to expected value.
	    	assertTrue(result.contains(fallbackValue));
	    	assertFalse(result.contains("MSG-A"));
	    	assertFalse(result.contains("MSG-B"));
	    	assertFalse(result.contains("MSG-C"));
	    	assertEquals("FALLBACK, FALLBACK, FALLBACK", result);
	    	System.out.println("[Fail-Soft] All Microservice Failures - Test Successful: " + result + "\n");
    	});
    }
    
    @Test
	@DisplayName("[Fail-Soft] Size Mismatch Between Services and Messages")
	public void testProcessAsyncFailSoftSizeMismatch() throws ExecutionException, InterruptedException, TimeoutException {
		// Create a list of microservices to be processed.
    	List<Microservice> services = List.of(
    		new Microservice(),
    		new Microservice(),
    		new Microservice()
    	);
    	
    	// Create a list of messages to be returned by the microservices, but with a size mismatch.
    	List<String> messages = List.of(
    			"msg-a",
    			"msg-b"
    	);
    	
    	// Create a fallback value in the event of a microservice failure.
    	String fallbackValue = "FALLBACK";
    	
    	// Call the processAsyncFailSoft method to process the microservices.
    	CompletableFuture<String> future = processor.processAsyncFailSoft(services, messages, fallbackValue);
    	
    	// Assertions to compare result to expected value.
    	ExecutionException ex = assertThrows(ExecutionException.class, () -> future.get(5, TimeUnit.SECONDS));
    	assertTrue(ex.getCause() instanceof IllegalArgumentException);
    	System.out.println("[Fail-Soft] Size Mismatch Between Services and Messages - Test Successful: " + ex.getCause().getMessage() + "\n");
	}
    
    // Liveness Tests
    @Test
    @DisplayName("[Liveness][Fail-Fast] Fails quickly with 13 concurrent microservices")
    public void testLivenessFailFastWithManyServices() {
    	// Arrange
    	int serviceCount = 13;
    	List<Microservice> services = createDelayedServices(serviceCount, Set.of(0), null);
    	List<String> messages = createMessages(serviceCount);
    	CompletableFuture<String> future = processor.processAsyncFailFast(services, messages);
    	
    	// Act
    	ExecutionException thrown = assertThrows(ExecutionException.class,
    			() -> future.get(2, TimeUnit.SECONDS),
    			"Fail-Fast should complete exceptionally without hanging.");
    	
    	// Assert
    	assertNotNull(thrown.getCause());
    	assertTrue(thrown.getCause().getMessage().contains("Synthetic failure"), "Fail-Fast should expose failure cause.");
    	System.out.println("\n[Liveness][Fail-Fast] Failed quickly within timeout. Cause: " + thrown.getCause().getMessage() + "\n");
    }
    
    @Test
    @DisplayName("[Liveness][Fail-Partial] Completes with 14 concurrent microservices")
    public void testLivenessFailPartialWithManyServices() throws ExecutionException, InterruptedException, TimeoutException {
    	// Arrange
    	int serviceCount = 14;
    	List<Microservice> services = createDelayedServices(serviceCount, Set.of(1, 3, 9), null);
    	List<String> messages = createMessages(serviceCount);
    	
    	// Act
    	CompletableFuture<String> future = processor.processAsyncFailPartial(services, messages);
    	String result = future.get(4, TimeUnit.SECONDS);
    	List<String> tokens = splitResults(result);
    	
    	// Assert
    	assertEquals(serviceCount - 3, tokens.size(), "Fail-Partial must return only successful responses.");
    	assertFalse(tokens.contains("FALLBACK"), "Fail-Partial must not inject fallback values.");
    	assertTrue(tokens.stream().allMatch(token -> token.startsWith("MSG-")), "Fail-Partial output should contain only successful uppercased messages.");
    	System.out.println("\n[Liveness][Fail-Partial] Completed within timeout. Result count: " + tokens.size() + "\n");
    }
    
    @Test
    @DisplayName("[Liveness][Fail-Soft] Completes with 12 concurrent microservices")
    public void testLivenessFailSoftWithManyServices() throws ExecutionException, InterruptedException, TimeoutException {
    	// Arrange
    	int serviceCount = 12;
    	List<Microservice> services = createDelayedServices(serviceCount, Set.of(2, 7), null);
    	List<String> messages = createMessages(serviceCount);
    	String fallbackValue = "FALLBACK";
    	
    	// Act
    	CompletableFuture<String> future = processor.processAsyncFailSoft(services, messages, fallbackValue);
    	String result = future.get(4, TimeUnit.SECONDS);
    	List<String> tokens = splitResults(result);
    	
    	// Assert
    	assertEquals(serviceCount, tokens.size(), "Fail-Soft must always preserve cardinality.");
    	assertEquals(2, tokens.stream().filter(fallbackValue::equals).count(), "Fail-Soft must replace failures with fallback.");
    	assertEquals(10, tokens.stream().filter(token -> token.startsWith("MSG-")).count(), "Fail-Soft must keep successful responses.");
    	System.out.println("\n[Liveness][Fail-Soft] Completed within timeout. Result: " + result + "\n");
    }
    
    // Nondeterminism Tests 
    @Test
    @DisplayName("[Nondeterminism] Completion order is observed and logged (not asserted)")
    public void testNondeterminismCompletionOrderObserved() throws ExecutionException, InterruptedException, TimeoutException {
    	// Arrange
    	int serviceCount = 12;
    	List<String> completionOrder = new CopyOnWriteArrayList<>();
    	List<Microservice> services = createDelayedServices(serviceCount, Set.of(), completionOrder);
    	List<String> messages = createMessages(serviceCount);
    	String fallbackValue = "FALLBACK";
    	
    	// Act
    	CompletableFuture<String> future = processor.processAsyncFailSoft(services, messages, fallbackValue);
    	String result = future.get(4, TimeUnit.SECONDS);
    	
    	// Assert
    	assertEquals(serviceCount, completionOrder.size(), "All microservices should be observed as completed.");
    	assertEquals(serviceCount, splitResults(result).size(), "Fail-Soft still preserves cardinality.");
    	System.out.println("\n[Nondeterminism] Input order     : " + messages);
    	System.out.println("[Nondeterminism] Completion order: " + completionOrder);
    	System.out.println("[Nondeterminism] Aggregated result: " + result + "\n");
    }
    
    // Helper Methods
    /**
     * Creates synthetic asynchronous microservices with staggered delays and optional failures.
     * 
     * @param count total number of microservices to create.
     * @param failingIndices service indices that should fail.
     * @param completionOrder optional thread-safe sink used to record completion order.
     * @return list of configured microservice instances.
     */
    private List<Microservice> createDelayedServices(int count, Set<Integer> failingIndices, List<String> completionOrder) {
    	return IntStream.range(0, count)
    			.mapToObj(index -> {
    				long delayMs = 40L + ((count - index) * 10L);
    				boolean shouldFail = failingIndices.contains(index);
    				String serviceId = "svc-" + index;
    				return new ControlledDelayMicroservice(serviceId, delayMs, shouldFail, completionOrder);
    			})
    			.collect(Collectors.toList());
    }
    
    /**
     * Creates a list of request messages in ascending order.
     * 
     * @param count total number of messages.
     * @return ordered message list (e.g., msg-0, msg-1, ...).
     */
    private List<String> createMessages(int count) {
    	return IntStream.range(0, count)
    			.mapToObj(i -> "msg-" + i)
    			.collect(Collectors.toList());
    }
    
    /**
     * Splits a comma-delimited aggregate result into tokens.
     * 
     * @param result aggregated processor output.
     * @return trimmed list of result tokens.
     */
    private List<String> splitResults(String result) {
    	if (result == null || result.isBlank()) {
    		return List.of();
    	}
    	return List.of(result.split(", "))
    			.stream()
    			.map(String::trim)
    			.collect(Collectors.toList());
    }
    
    /**
     * Test helper microservice that supports controlled delay, deterministic failure,
     * and optional completion-order tracing.
     */
    private static class ControlledDelayMicroservice extends Microservice {
    	private static final AtomicInteger FAILURE_COUNTER = new AtomicInteger(0);
    	
    	private final String serviceId;
    	private final long delayMs;
    	private final boolean shouldFail;
    	private final List<String> completionOrder;
    	
    	/**
    	 * Creates a synthetic microservice with configurable behavior.
    	 * 
    	 * @param serviceId service identifier used for logs and completion tracking.
    	 * @param delayMs artificial execution delay in milliseconds.
    	 * @param shouldFail whether this microservice should fail.
    	 * @param completionOrder optional thread-safe completion-order sink.
    	 */
    	ControlledDelayMicroservice(String serviceId, long delayMs, boolean shouldFail, List<String> completionOrder) {
    		this.serviceId = serviceId;
    		this.delayMs = delayMs;
    		this.shouldFail = shouldFail;
    		this.completionOrder = completionOrder;
    	}
    	
    	@Override
    	public CompletableFuture<String> retrieveAsync(String input) {
    		return CompletableFuture.supplyAsync(() -> {
    			try {
    				Thread.sleep(delayMs);
    			} catch (InterruptedException e) {
    				Thread.currentThread().interrupt();
    				throw new RuntimeException("Interrupted: " + serviceId, e);
    			}
    			
    			if (shouldFail) {
    				int failureId = FAILURE_COUNTER.incrementAndGet();
    				throw new RuntimeException("Synthetic failure #" + failureId + " from " + serviceId);
    			}
    			return input.toUpperCase();
    		}).whenComplete((value, ex) -> {
    			if (completionOrder != null) {
    				completionOrder.add(serviceId);
    			}
    		});
    	}
    }
}
