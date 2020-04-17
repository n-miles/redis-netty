package com.nmiles.redis.model;

public interface RedisObject {
    enum Type {
        BULK_STRING,
        ERROR,
        ARRAY,
        INTEGER,
        SIMPLE_STRING,
    }

    Type getType();
}
