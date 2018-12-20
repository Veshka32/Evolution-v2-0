package com.services.implementations;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class GameServiceTest {

    @Autowired
    GameService gameService;

    @Test
    void createGameTest() throws InterruptedException, ExecutionException {
        //id unique test
        Set<Integer> ids = new HashSet<>();
        Callable<Integer> task = () -> gameService.createGame(2, null).getId();
        List<Callable<Integer>> tasks = new ArrayList<>(500);
        IntStream.rangeClosed(1, 500).forEach(n -> tasks.add(task)); //populate tasks list
        ExecutorService executor = Executors.newFixedThreadPool(500);
        List<Future<Integer>> results = executor.invokeAll(tasks);
        executor.invokeAll(tasks);
        for (Future<Integer> result : results) {
            ids.add(result.get());
        }
        executor.shutdown();
        assertEquals(ids.size(), results.size());
    }
}