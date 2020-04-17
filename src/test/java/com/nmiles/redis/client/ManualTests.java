package com.nmiles.redis.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ManualTests {

    private RedisClient client;

    @BeforeEach
    public void setup() {
        client = new RedisClient("localhost", 6379);
    }

    @Test
    public void manual() {
        List.of(
                "PING",
                "INCR key1",
                "INCR key1",
                "INCR key1",
                "INCR key1",

                "INCR key2",
                "INCR key2",
                "INCR key2",

                "INCR key1",

                "DEL key1",
                "DEL key2",

                "nonsense that it doesn't understand"
        ).forEach(command ->
                System.out.println(client.execute(command).block())
        );
    }
}
