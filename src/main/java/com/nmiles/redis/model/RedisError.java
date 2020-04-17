package com.nmiles.redis.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class RedisError implements RedisObject {

    private final String message;

    @Override
    public Type getType() {
        return Type.ERROR;
    }

    @Override
    public String toString() {
        return "RedisError(" + message + ')';
    }
}
