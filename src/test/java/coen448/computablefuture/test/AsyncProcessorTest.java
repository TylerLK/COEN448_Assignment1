package coen448.computablefuture.test;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.concurrent.*;

public class AsyncProcessorTest {
    /* Professor's Pre-Defined Code
	@Test
    public void testProcessAsyncSuccess() throws ExecutionException, InterruptedException {
        Microservice mockService1 = mock(Microservice.class);
        Microservice mockService2 = mock(Microservice.class);
        when(mockService1.retrieveAsync(any())).thenReturn(CompletableFuture.completedFuture("Hello"));
        when(mockService2.retrieveAsync(any())).thenReturn(CompletableFuture.completedFuture("World"));

        AsyncProcessor processor = new AsyncProcessor();
        CompletableFuture<String> resultFuture = processor.processAsync(List.of(mockService1, mockService2));
        
        String result = resultFuture.get();
        assertEquals("Hello World", result);
    }
    */
	
	// Setting up an object of AsyncProcessor to be called in all test cases.
	private AsyncProcessor processor;
	
	@BeforeEach
	void setUp() {
		processor = new AsyncProcessor();
	}
    
    // Fail-Soft Policy Tests    
    @Test
    @DisplayName("[Fail-Soft] All Microservices are Successful")
    public void testProcessAsyncFailSoftAllSuccess() throws ExecutionException, InterruptedException {
    	// Create a list of microservices to be processed.
    	List<Microservice> services = List.of(
    		new Microservice(),
    		new Microservice(),
    		new Microservice()
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
    	String result = future.get();
    	
    	// Assertions to compared result to expected value.
    	assertFalse(result.contains(fallbackValue));
    	assertTrue(result.contains("MSG-A"));
    	assertTrue(result.contains("MSG-B"));
    	assertTrue(result.contains("MSG-C"));
    	System.out.println("[Fail-Soft] Policy Test Successful: " + result + "\n");
    }
    
    @Test
    @DisplayName("[Fail-Soft] Single Microservice Failure")
    public void testProcessAsyncFailSoftSingleFailure() throws ExecutionException, InterruptedException {
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
    	String result = future.get();
    	
    	// Assertions to compared result to expected value.
    	assertTrue(result.contains(fallbackValue));
    	assertTrue(result.contains("MSG-A"));
    	assertFalse(result.contains("MSG-B"));
    	assertTrue(result.contains("MSG-C"));
    	System.out.println("[Fail-Soft] Policy Test Successful: " + result + "\n");
    }
    
    @Test
    @DisplayName("[Fail-Soft] Multiple Microservice Failures")
    public void testProcessAsyncFailSoftMultipleFailure() throws ExecutionException, InterruptedException {
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
    	String result = future.get();
    	
    	// Assertions to compared result to expected value.
    	assertTrue(result.contains(fallbackValue));
    	assertFalse(result.contains("MSG-A"));
    	assertTrue(result.contains("MSG-B"));
    	assertFalse(result.contains("MSG-C"));
    	System.out.println("[Fail-Soft] Policy Test Successful: " + result + "\n");
    }
    
    @Test
    @DisplayName("[Fail-Soft] Multiple Microservice Failures")
    public void testProcessAsyncFailSoftAllFailure() throws ExecutionException, InterruptedException {
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
	    	String result = future.get();
	    	
	    	// Assertions to compared result to expected value.
	    	assertTrue(result.contains(fallbackValue));
	    	assertFalse(result.contains("MSG-A"));
	    	assertFalse(result.contains("MSG-B"));
	    	assertFalse(result.contains("MSG-C"));
	    	System.out.println("[Fail-Soft] Policy Test Successful: " + result + "\n");
    	});
    }
}