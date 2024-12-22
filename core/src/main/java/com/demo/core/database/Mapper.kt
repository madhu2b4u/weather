package com.demo.core.database

interface Mapper<T, E> {

    fun from(e: E): T

    fun to(t: T): E

}