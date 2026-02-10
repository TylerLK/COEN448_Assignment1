package coen448.computablefuture.test;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.concurrent.*;

public class AsyncProcessorTest {
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
}