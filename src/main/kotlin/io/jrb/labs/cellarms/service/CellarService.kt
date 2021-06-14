/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Jon Brule <brulejr@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.jrb.labs.cellarms.service

import io.jrb.labs.cellarms.mapper.CellarMapper
import io.jrb.labs.cellarms.model.CellarEntity
import io.jrb.labs.cellarms.model.CellarTraitType
import io.jrb.labs.cellarms.model.EntityType
import io.jrb.labs.cellarms.repository.CellarRepository
import io.jrb.labs.cellarms.resource.CellarRequest
import io.jrb.labs.cellarms.resource.CellarResource
import io.jrb.labs.common.service.CrudServiceUtils
import io.jrb.labs.common.service.R2dbcCrudServiceUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

@Service
class CellarService(
    private val cellarRepository: CellarRepository,
    private val lookupValueService: LookupValueService,
    private val cellarMapper: CellarMapper
) {

    private val crudServiceUtils: CrudServiceUtils<CellarEntity> = R2dbcCrudServiceUtils(
        entityClass = CellarEntity::class.java,
        entityName = "Cellar",
        repository = cellarRepository
    )

    @Transactional
    fun createCellar(cellarRequest: CellarRequest): Mono<CellarResource> {
        return Mono.just(cellarMapper.cellarRequestToCellarEntity(cellarRequest))
            .map {
                val timestamp : Instant = Instant.now()
                it.copy(createdOn = timestamp, modifiedOn = timestamp)
            }
            .flatMap { crudServiceUtils.createEntity(it) }
            .flatMap { Mono.zip(
                Mono.just(it),
                lookupValueService.createLookupValues(EntityType.CELLAR.name, it.id!!, CellarTraitType.TAG.name, cellarRequest.tags)
            ) }
            .map { cellarMapper.cellarEntityToCellarResource(it.t1).copy(tags = it.t2) }
    }

    @Transactional
    fun deleteCellar(name: String): Mono<Void> {
        return crudServiceUtils.deleteEntity(name)
    }

    @Transactional
    fun findCellarByName(name: String): Mono<CellarResource> {
        return crudServiceUtils.findEntityByName(name)
            .zipWhen {
                lookupValueService.findLookupValueList(EntityType.CELLAR.name, it.id!!, CellarTraitType.TAG.name)
            }
            .map { cellarMapper.cellarEntityToCellarResource(it.t1).copy(tags = it.t2) }
    }

    @Transactional
    fun listAllCellars(): Flux<CellarResource> {
        return crudServiceUtils.listAllEntities()
            .map(cellarMapper::cellarEntityToCellarResource)
    }

}
