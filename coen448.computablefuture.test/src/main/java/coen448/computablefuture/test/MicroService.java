package coen448.computablefuture.test;


import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

class Microservice {
    public CompletableFuture<String> retrieveAsync(String input) {
        return CompletableFuture.supplyAsync(() -> input.toUpperCase());
    }
}