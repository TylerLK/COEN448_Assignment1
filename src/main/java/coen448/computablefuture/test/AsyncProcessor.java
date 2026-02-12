package coen448.computablefuture.test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AsyncProcessor {
	public CompletableFuture<String> processAsync(List<Microservice> microservices) {
		List<CompletableFuture<String>> futures = microservices.stream().map(client -> client.retrieveAsync("hello"))
				.collect(Collectors.toList());
		return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
				.thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.joining(" ")));
	}

	// Failure Semantic Policies
	/**
	 * Fail-Soft Policy <br>
	 * <br>
	 * All failures are replaced with a pre-defined fallback value. The computation
	 * never fails. <br>
	 * <br>
	 * 
	 * @param services      List of Microservice objects to be processed.
	 * @param messages      List of messages returned by the microservices.
	 * @param fallbackValue The value the microservices should return if failure
	 *                      occurs. <br>
	 *                      <br>
	 * @return A CompletableFuture of type String that lists the concatenated of the
	 *         messages return by all the microservices.
	 */
	public CompletableFuture<String> processAsyncFailSoft(
			List<Microservice> services, 
			List<String> messages,
			String fallbackValue) {

		// Print the number of microservices being processed.
		System.out.println("[Fail-Sof] Processing" + services.size() + " microservices...");

		// Create a stream of the future microservice results, map them to an expected
		// value,
		// and collect them into a list.
		List<CompletableFuture<String>> futures = services.stream()
				.map(ms -> ms.retrieveAsync("Success")
						.exceptionally(ex -> {
							// If a failure has occurred, print the error message and return the fallback value.
							System.out.println("[Fail-Soft] Failure Detected: " + ex.getMessage());

							return fallbackValue;
						})
				)
				.collect(Collectors.toList());

		// Create a barrier for all the microservices to reach using the allOf() method.
		return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
				.thenApply(v -> {
				// If no failures have occurred, notify the user.
				System.out.println("[Fail-Soft] All microservices completed successfully.");
	
				// Create a single CompletableFuture<String> containing the microservice results.
				return futures.stream()
						.map(CompletableFuture::join)
						.collect(Collectors.joining(", "));
				});
	}

}