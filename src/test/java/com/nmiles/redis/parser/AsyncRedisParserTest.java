package com.nmiles.redis.parser;

import com.nmiles.redis.model.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.assertj.core.api.Assertions.assertThat;

public class AsyncRedisParserTest {

    private AsyncRedisParser parser;

    @BeforeEach
    public void setup() {
        parser = new AsyncRedisParser();
    }

    @Test
    public void fullIntegerChunkParses() {
        feedAndExpect(":567\r\n", new RedisInteger(567));
    }

    @Test
    public void partialIntegerParses() {
        feed(":123");
        feedAndExpect("456\r\n", new RedisInteger(123456));
    }

    @Test
    public void multipleIntegersParse() {
        feedAndExpect(":123\r\n:456\r\n", new RedisInteger(123), new RedisInteger(456));
    }

    @Test
    public void fullSimpleStringParses() {
        feedAndExpect("+OK\r\n", new SimpleString("OK"));
    }

    @Test
    public void partialSimpleStringParses() {
        feed("+Ca");
        feedAndExpect("t\r\n", new SimpleString("Cat"));
    }

    @Test
    public void fullBulkStringParses() {
        feedAndExpect("$5\r\nhello\r\n", new BulkString("hello".getBytes(US_ASCII)));
    }

    @Test
    public void partialBulkStringParses() {
        feed("$5\r\n");
        feedAndExpect("hello\r\n", new BulkString("hello".getBytes(US_ASCII)));
    }

    @Test
    public void emptyBulkStringParses() {
        feedAndExpect("$0\r\n\r\n", new BulkString(new byte[0]));
    }

    @Test
    public void nullBulkStringParses() {
        feedAndExpect("$-1\r\n", (RedisObject) null);
    }

    @Test
    public void emptyArrayParses() {
        feedAndExpect("*0\r\n", new RedisArray(List.of()));
    }

    @Test
    public void bulkStringArrayParses() {
        feedAndExpect("*2\r\n$3\r\nfoo\r\n$3\r\nbar\r\n",
                new RedisArray(List.of(
                        new BulkString("foo".getBytes(US_ASCII)),
                        new BulkString("bar".getBytes(US_ASCII)))
                )
        );
    }

    @Test
    public void splitUpMixedArrayParses() {
        var lines = List.of("*4\r\n",
                "+special123 string!\r",
                "\n:-987",
                "\r\n-Oh no, there was", " an error :(\r\n",
                "$9\r\n123456789\r\n"
        );

        for (int i = 0; i < lines.size() - 1; i++) {
            feed(lines.get(i));
        }

        var expected = new RedisArray(List.of(
                new SimpleString("special123 string!"),
                new RedisInteger(-987),
                new RedisError("Oh no, there was an error :("),
                new BulkString("123456789".getBytes(US_ASCII))
        ));

        feedAndExpect(lines.get(lines.size() - 1), expected);
    }

    private void feed(String toFeed) {
        assertThat(parser.feed(toBytes(toFeed)))
                .isEmpty();
    }

    private void feedAndExpect(String toFeed, RedisObject...expected) {
        assertThat(parser.feed(toBytes(toFeed)))
                .containsExactly(expected);
    }

    private byte[] toBytes(String s) {
        return s.getBytes(US_ASCII);
    }
}
