package com.yaseenmahdi.serviceops.api;

import com.yaseenmahdi.serviceops.api.dto.Requests.*;
import com.yaseenmahdi.serviceops.api.dto.Responses.WorkOrderView;
import com.yaseenmahdi.serviceops.domain.WorkOrderStatus;
import com.yaseenmahdi.serviceops.service.WorkOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/work-orders")
public class WorkOrderController {
    private final WorkOrderService service;
    public WorkOrderController(WorkOrderService service) { this.service = service; }

    @PostMapping @ResponseStatus(HttpStatus.CREATED) public WorkOrderView create(@Valid @RequestBody CreateWorkOrder request) { return service.create(request); }
    @GetMapping public List<WorkOrderView> list(@RequestParam(required = false) WorkOrderStatus status) { return service.list(status); }
    @GetMapping("/{id}") public WorkOrderView get(@PathVariable UUID id) { return service.get(id); }
    @PostMapping("/{id}/assignment") public WorkOrderView assign(@PathVariable UUID id, @Valid @RequestBody AssignTechnician request) { return service.assign(id, request); }
    @PostMapping("/{id}/status") public WorkOrderView transition(@PathVariable UUID id, @Valid @RequestBody TransitionWorkOrder request) { return service.transition(id, request); }
    @PostMapping("/{id}/parts") public WorkOrderView reservePart(@PathVariable UUID id, @Valid @RequestBody ReservePart request) { return service.reservePart(id, request); }
    @DeleteMapping("/{id}/parts/{partId}") public WorkOrderView releasePart(@PathVariable UUID id, @PathVariable UUID partId) { return service.releasePart(id, partId); }
    @PostMapping("/{id}/appointments") public WorkOrderView schedule(@PathVariable UUID id, @Valid @RequestBody ScheduleAppointment request) { return service.schedule(id, request); }
    @PostMapping("/{id}/completion") public WorkOrderView complete(@PathVariable UUID id, @Valid @RequestBody CompleteWorkOrder request) { return service.complete(id, request); }
}
