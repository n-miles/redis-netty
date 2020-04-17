package com.nmiles.redis.client;

import com.nmiles.redis.model.RedisObject;
import com.nmiles.redis.parser.AsyncRedisParser;
import io.netty.handler.timeout.ReadTimeoutHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.tcp.TcpClient;

import java.util.Arrays;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.stream.Collectors.joining;

public class RedisClient {

    private final TcpClient tcpClient;

    public RedisClient(String address, int port) {
        tcpClient = TcpClient.create()
                .doOnConnected(conn -> conn.addHandler(new ReadTimeoutHandler(5, TimeUnit.SECONDS)))
                .host(address)
                .port(port);
    }

    public Mono<RedisObject> execute(String command) {
        CompletableFuture<RedisObject> future = new CompletableFuture<>();

        return tcpClient.handle((in, out) -> {

            AsyncRedisParser parser = new AsyncRedisParser();

            return out.sendByteArray(Mono.just(translateCommand(command)))
                    .then(in.receive()
                            .asByteArray()
                            .flatMap(bytes -> Flux.fromIterable(parser.feed(bytes)))
                            .next()
                            .doOnNext(future::complete)
                            .doOnError(future::completeExceptionally)
                            .doOnCancel(() -> future.completeExceptionally(new CancellationException()))
                            .then());
        }).connect()
                .then(Mono.fromFuture(future));
    }

    private byte[] translateCommand(String command) {
        String[] args = command.split(" +");

        String finalCommand = "*" + args.length + "\r\n" + Arrays.stream(args)
                .map(s -> "$" + s.length() + "\r\n" + s + "\r\n")
                .collect(joining());
        return finalCommand.getBytes(US_ASCII);
    }
}
