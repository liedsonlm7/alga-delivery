package com.algaworks.algadelivery.delivery.tracking.api.controller;

import com.algaworks.algadelivery.delivery.tracking.api.model.CourierIdInput;
import com.algaworks.algadelivery.delivery.tracking.api.model.DeliveryInput;
import com.algaworks.algadelivery.delivery.tracking.domain.model.Delivery;
import com.algaworks.algadelivery.delivery.tracking.domain.repository.DeliveryRepository;
import com.algaworks.algadelivery.delivery.tracking.domain.service.DeliveryCheckpointService;
import com.algaworks.algadelivery.delivery.tracking.domain.service.DeliveryPreparationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryPreparationService deliveryPreparationService;

    private final DeliveryCheckpointService deliveryCheckpointService;

    private final DeliveryRepository deliveryRepository;

    @PostMapping
    public ResponseEntity<Delivery> draft(@RequestBody @Valid DeliveryInput input) {
        Delivery delivery = deliveryPreparationService.draft(input);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(delivery.getId()).toUri();

        return ResponseEntity.created(uri).body(delivery);
    }

    @PutMapping("/{deliveryId}")
    public ResponseEntity<Delivery> edit(@PathVariable UUID deliveryId, @RequestBody @Valid DeliveryInput input) {
        Delivery delivery = deliveryPreparationService.edit(deliveryId, input);
        return ResponseEntity.ok().body(delivery);
    }

    @SneakyThrows
    @GetMapping
    public ResponseEntity<PagedModel<Delivery>> findAll(@PageableDefault Pageable pageable) {
        int millis = new Random().nextInt(400);
        Thread.sleep(millis);
        PagedModel<Delivery> deliveries = new PagedModel<>(deliveryRepository.findAll(pageable));
        return ResponseEntity.ok().body(deliveries);
    }

    @GetMapping("/{deliveryId}")
    public ResponseEntity<Delivery> findById(@PathVariable UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok().body(delivery);
    }

    @PostMapping("/{deliveryId}/placement")
    public void place(@PathVariable UUID deliveryId) {
        deliveryCheckpointService.place(deliveryId);
    }

    @PostMapping("/{deliveryId}/pickups")
    public void pickup(@PathVariable UUID deliveryId,
                       @Valid @RequestBody CourierIdInput input) {
        deliveryCheckpointService.pickUp(deliveryId, input.getCourierId());
    }

    @PostMapping("/{deliveryId}/completion")
    public void complete(@PathVariable UUID deliveryId) {
        deliveryCheckpointService.complete(deliveryId);
    }
}
