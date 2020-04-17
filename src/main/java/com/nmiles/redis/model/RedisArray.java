package com.nmiles.redis.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class RedisArray implements RedisObject {

    private final List<RedisObject> elements;

    @Override
    public Type getType() {
        return Type.ARRAY;
    }

    @Override
    public String toString() {
        return elements.toString();
    }
}
