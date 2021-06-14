package io.jrb.labs.cellarms.service

import io.jrb.labs.common.model.LookupValue
import io.jrb.labs.cellarms.repository.LookupValueRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class LookupValueService(val lookupValueRepository: LookupValueRepository) {

    fun createLookupValue(entityType: String, entityId: Long, valueType: String, value: String): Mono<String> {
        return Mono.just(value)
            .map { LookupValue(null, entityType, entityId, valueType, it) }
            .flatMap { lookupValueRepository.save(it) }
            .map(LookupValue::value)
    }

    fun createLookupValues(entityType: String, entityId: Long, valueType: String, values: List<String>): Mono<List<String>> {
        return Flux.fromIterable(values)
            .map { value -> LookupValue(null, entityType, entityId, valueType, value) }
            .flatMap { lookupValueRepository.save(it) }
            .map(LookupValue::value)
            .collectList()
    }

    fun deleteLookupValues(entityType: String, entityId: Long): Mono<Void> {
        return lookupValueRepository.deleteByEntityTypeAndEntityId(entityType, entityId)
    }

    fun findLookupValue(entityType: String, entityId: Long, valueType: String): Mono<String> {
        return lookupValueRepository.findByEntityTypeAndEntityIdAndValueType(entityType, entityId, valueType)
            .map { it.value }
            .next()
    }

    fun findLookupValueList(entityType: String, entityId: Long, valueType: String): Mono<List<String>> {
        return lookupValueRepository.findByEntityTypeAndEntityIdAndValueType(entityType, entityId, valueType)
            .map { it.value }
            .collectList()
    }

}
