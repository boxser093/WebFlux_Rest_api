package net.ilya.restcontrollerv100.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GenericService<T, V, G> {
    Mono<T> findById(V v);

    Mono<T> create(T t);

    Mono<T> update(T t);

    Mono<T> delete(V v);

    Flux<G> findAll();
}
