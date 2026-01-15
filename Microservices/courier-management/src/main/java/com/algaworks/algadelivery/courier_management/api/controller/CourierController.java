package com.algaworks.algadelivery.courier_management.api.controller;

import com.algaworks.algadelivery.courier_management.api.model.CourierInput;
import com.algaworks.algadelivery.courier_management.domain.model.Courier;
import com.algaworks.algadelivery.courier_management.domain.repository.CourierRepository;
import com.algaworks.algadelivery.courier_management.domain.service.CourierRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/couriers")
@RequiredArgsConstructor
public class CourierController {

    private final CourierRegistrationService courierRegistrationService;
    private final CourierRepository courierRepository;

    @PostMapping
    public ResponseEntity<Courier> create(@Valid @RequestBody CourierInput input) {
        Courier courier = courierRegistrationService.create(input);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(courier.getId()).toUri();
        return ResponseEntity.created(uri).body(courier);
    }

    @PutMapping("/{courierId}")
    public ResponseEntity<Courier> update(@PathVariable UUID courierId,
                                          @Valid @RequestBody CourierInput input) {
        Courier courier = courierRegistrationService.update(courierId, input);
        return ResponseEntity.ok().body(courier);
    }

    @GetMapping
    public ResponseEntity<PagedModel<Courier>> findAll(@PageableDefault Pageable pageable) {
        PagedModel<Courier> courierList = new PagedModel<>(courierRepository.findAll(pageable));
        return ResponseEntity.ok().body(courierList);
    }

    @GetMapping("/{courierId}")
    public ResponseEntity<Courier> findById(@PathVariable UUID courierId) {
        Courier courier = courierRepository.findById(courierId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok().body(courier);
    }



}
