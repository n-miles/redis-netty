package com.nmiles.redis.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class SimpleString implements RedisObject {

    private final String value;

    @Override
    public Type getType() {
        return Type.SIMPLE_STRING;
    }

    @Override
    public String toString() {
        return value;
    }
}
