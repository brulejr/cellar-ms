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
package io.jrb.labs.cellarms.rest

import io.jrb.labs.cellarms.resource.CellarRequest
import io.jrb.labs.cellarms.resource.CellarResource
import io.jrb.labs.cellarms.service.CellarService
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping("/api/cellars")
class CellarController(
    val cellarService: CellarService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createCellar(@Valid @RequestBody cellar: CellarRequest): Mono<EntityModel<CellarResource>> {
        return cellarService.createCellar(cellar).map {
            EntityModel.of(it)
                .add(selfLink(it.name))
                .add(collectionLink())
        }
    }

    @DeleteMapping("/{cellarName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteDocument(@PathVariable cellarName: String): Mono<Void> {
        return cellarService.deleteCellar(cellarName)
    }

    @GetMapping("/{cellarName}")
    fun findCellarByName(@PathVariable cellarName: String): Mono<EntityModel<CellarResource>> {
        return cellarService.findCellarByName(cellarName).map {
            EntityModel.of(it)
                .add(selfLink(cellarName))
                .add(collectionLink())
        }
    }

    @GetMapping
    fun listAllCellars(): Flux<EntityModel<CellarResource>> {
        return cellarService.listAllCellars().map {
            EntityModel.of(it)
                .add(selfLink(it.name))
        }
    }

    private fun collectionLink(): Link {
        return WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(javaClass).listAllCellars()).withRel("collection")
    }

    private fun selfLink(name: String): Link {
        return WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(javaClass).findCellarByName(name)).withSelfRel()
    }

}
