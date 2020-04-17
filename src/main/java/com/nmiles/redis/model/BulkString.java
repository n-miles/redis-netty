package com.nmiles.redis.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class BulkString implements RedisObject {

    private final byte[] bytes;

    @Override
    public Type getType() {
        return Type.BULK_STRING;
    }
}
