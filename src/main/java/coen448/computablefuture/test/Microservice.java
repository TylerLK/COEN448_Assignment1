package coen448.computablefuture.test;


import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

class Microservice {
    // Successful Microservice Execution
	public CompletableFuture<String> retrieveAsync(String input) {
        return CompletableFuture.supplyAsync(() -> input.toUpperCase());
    }
    
    // Failed Microservice Execution
	public CompletableFuture<String> retrieveAsyncFail(String error) {
		return CompletableFuture.supplyAsync(() -> {
			throw new RuntimeException(error);
		});
	}
}