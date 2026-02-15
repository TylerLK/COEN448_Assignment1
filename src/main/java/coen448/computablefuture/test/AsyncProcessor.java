package coen448.computablefuture.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AsyncProcessor {
	// Professor's Original Code (Kept as Reference)
//	/**
//	 * Teacher's base code for processAsync
//	 * Process the microservices in parallel and return the results in the same order
//	 * as the input {@code microservices} list.
//	 * @param microservices List of Microservice objects to be processed.
//	 * @param message Message to be sent to the corresponding microservices.
//	 * @return A CompletableFuture of type String that contains the space-separated
//	 *         concatenation of the messages returned by all the microservices, in the
//	 *         same order as the input {@code microservices} list.
//	 */
//	public CompletableFuture<String> processAsync(List<Microservice> microservices, String message) {
//    	
//        List<CompletableFuture<String>> futures = microservices.stream()
//            .map(client -> client.retrieveAsync(message))
//            .collect(Collectors.toList());
//        
//        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
//            .thenApply(v -> futures.stream()
//                .map(CompletableFuture::join)
//                .collect(Collectors.joining(" ")));
//        
//    }
//    /**
//	 * Teacher's base code for processAsyncCompletionOrder
//	 * Process the microservices in parallel and return the results in the order they complete.
//	 * @param microservices List of Microservice objects to be processed.
//	 * @param message Message to be sent to the corresponding microservices.
//	 * @return A CompletableFuture of type List<String> that contains the messages returned by
//	 *         all the microservices, in the order in which the microservices complete.
//	 */
//    public CompletableFuture<List<String>> processAsyncCompletionOrder(
//            List<Microservice> microservices, String message) {
//
//        List<String> completionOrder =
//            Collections.synchronizedList(new ArrayList<>());
//
//        List<CompletableFuture<Void>> futures = microservices.stream()
//            .map(ms -> ms.retrieveAsync(message)
//                .thenAccept(completionOrder::add))
//            .collect(Collectors.toList());
//
//        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
//            .thenApply(v -> completionOrder);
//    }

	// Failure Semantic Policies
	/**
	 * Fail-Fast Policy <br>
	 * If any microservice fails, the entire computation fails immediately.
	 * @param services List of Microservice objects to be processed.
	 * @param messages List of messages to be sent to the corresponding microservices.
	 * @return A CompletableFuture of type String that contains the concatenation of the
	 *         messages returned by all the microservices.
	 */
	public CompletableFuture<String> processAsyncFailFast(
			List<Microservice> services, 
			List<String> messages) {

		// Check if the number of messages received and number of microservices being processed match.
		if (services.size() != messages.size()) {
			return CompletableFuture.failedFuture(
					new IllegalArgumentException("Number of messages received and microservices being processed do not match!"));
		}

		// Print the number of microservices being processed.
		System.out.println("[Fail-Fast] Processing " + services.size() + " microservices...");

		// Launch the microservices concurrently.
		List<CompletableFuture<String>> futures = new ArrayList<>();
		for (int i = 0; i < services.size(); i++) {
			futures.add(services.get(i).retrieveAsync(messages.get(i)));
		}

		// Create a future that we can complete immediately upon the first failure.
		CompletableFuture<String> resultFuture = new CompletableFuture<>();

		// Attach listeners to fail the resultFuture immediately if any single service fails.
		for (CompletableFuture<String> future : futures) {
			future.exceptionally(ex -> {
				// completeExceptionnaly will throw the exception in the resultFuture.
				System.out.println("[Fail-Fast] Failure Detected: " + ex.getMessage());
				resultFuture.completeExceptionally(ex);
				return null;
			});
		}

		// Use allOf only to handle the success case. 
		// If resultFuture was already failed by a listener, resultFuture.complete() will do nothing.
		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
				.thenAccept(v -> {
					if (!resultFuture.isCompletedExceptionally()) {
						String result = futures.stream()
								.map(CompletableFuture::join)
								.collect(Collectors.joining(", "));
						resultFuture.complete(result);
					}
				});

		return resultFuture;
	}

	/**
	 * Fail-Partial Policy <br>
	 * All failures are ignored. The computation returns only the successful
	 * results.
	 * @param services List of Microservice objects to be processed.
	 * @param messages List of messages to be sent to the corresponding
	 *                 microservices.
	 * @return A CompletableFuture of type String that contains the concatenation of
	 *         the messages returned by all the successful microservices.
	 */
	public CompletableFuture<String> processAsyncFailPartial(
			List<Microservice> services,
			List<String> messages) {

		// Check if the number of messages received and number of microservices being processed match.
		if (services.size() != messages.size()) {
			return CompletableFuture.failedFuture(
					new IllegalArgumentException("Number of messages received and microservices being processed do not match!"));
		}

		// Print the number of microservices being processed.
		System.out.println("[Fail-Partial] Processing " + services.size() + " microservices...");

		// Create a list to hold the future microservice results.
		List<CompletableFuture<String>> futures = new ArrayList<>();

		// Loop through the received messages, adding them to the list of futures.
		for (int i = 0; i < messages.size(); i++) {
			Microservice service = services.get(i);
			String message = messages.get(i);

			// Add the future microservice result to the list of futures.
			futures.add(
					service.retrieveAsync(message)
					.exceptionally(ex -> {
						// If a failure occurs, log it and return null, converting this stage to a successful completion whose null result will be ignored later.
						System.out.println("[Fail-Partial] Failure Detected: " + ex.getMessage());
						return null;
					}));
		}

		// Create a barrier for all the microservices to reach using the allOf() method.
		return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
				.thenApply(v -> {
					// Once all microservices have completed (successfully or with null), notify the user.
					System.out.println("[Fail-Partial] All microservices completed (successfully or ignored).");

					// Create a single CompletableFuture<String> containing only successful microservice results.
					return futures.stream()
							.map(CompletableFuture::join)
							.filter(result -> result != null) // Filter out the nulls from failed services.
							.collect(Collectors.joining(", "));
				});
	}
	
	/**
	 * Fail-Soft Policy <br>
	 * All failures are replaced with a pre-defined fallback value. The computation
	 * never fails.
	 * @param services      List of Microservice objects to be processed.
	 * @param messages      List of messages to be sent to the corresponding microservices.
	 * @param fallbackValue The value the microservices should return if failure
	 *                      occurs.
	 * @return A CompletableFuture of type String that contains the concatenation of the
	 *         messages returned by all the microservices.
	 */
	public CompletableFuture<String> processAsyncFailSoft(
			List<Microservice> services, 
			List<String> messages,
			String fallbackValue) {

		// Check if the number of messages received and number of microservices being processed match.
		if (services.size() != messages.size()) {
			return CompletableFuture.failedFuture(
					new IllegalArgumentException("Number of messages received and microservices being processed do not match!"));
		}

		// Print the number of microservices being processed.
		System.out.println("[Fail-Soft] Processing " + services.size() + " microservices...");

		// Create a stream of the future microservice results.
		List<CompletableFuture<String>> futures = new ArrayList<>();

		// Loop through the received messages, adding them to the list of futures.
		for (int i = 0; i < messages.size(); i++) {
			Microservice service = services.get(i);
			String message = messages.get(i);

			// Add the future microservice result to the list of futures.
			futures.add(
					// If no failure has occurred, the microservice will return the message as expected.
					service.retrieveAsync(message)
							.exceptionally(ex -> {
								// If a failure has occurred, print the error message and return the fallback value.
								System.out.println("[Fail-Soft] Failure Detected: " + ex.getMessage());
								System.out.println("[Fail-Soft] WARNING: Failure masked with fallback value: " + fallbackValue);
								return fallbackValue;
							}));

		}

		// Create a barrier for all the microservices to reach using the allOf() method.
		return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
				.thenApply(v -> {
					// Once all microservices have completed (successfully or with fallback values), notify the user.
					System.out.println("[Fail-Soft] All microservices completed (successfully or with fallback values).");

					// Create a single CompletableFuture<String> containing the microservice results.
					return futures.stream()
							.map(CompletableFuture::join)
							.collect(Collectors.joining(", "));
				});
	}
}