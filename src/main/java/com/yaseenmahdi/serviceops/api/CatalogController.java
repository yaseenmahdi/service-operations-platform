package com.yaseenmahdi.serviceops.api;

import com.yaseenmahdi.serviceops.api.dto.Requests.*;
import com.yaseenmahdi.serviceops.api.dto.Responses.*;
import com.yaseenmahdi.serviceops.service.CatalogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class CatalogController {
    private final CatalogService service;
    public CatalogController(CatalogService service) { this.service = service; }

    @PostMapping("/customers") @ResponseStatus(HttpStatus.CREATED) public CustomerView createCustomer(@Valid @RequestBody CreateCustomer request) { return service.createCustomer(request); }
    @GetMapping("/customers") public List<CustomerView> customers() { return service.listCustomers(); }
    @PostMapping("/customers/{customerId}/sites") @ResponseStatus(HttpStatus.CREATED) public SiteView createSite(@PathVariable UUID customerId, @Valid @RequestBody CreateSite request) { return service.createSite(customerId, request); }
    @GetMapping("/customers/{customerId}/sites") public List<SiteView> sites(@PathVariable UUID customerId) { return service.listSites(customerId); }
    @PostMapping("/sites/{siteId}/assets") @ResponseStatus(HttpStatus.CREATED) public AssetView createAsset(@PathVariable UUID siteId, @Valid @RequestBody CreateAsset request) { return service.createAsset(siteId, request); }
    @GetMapping("/sites/{siteId}/assets") public List<AssetView> assets(@PathVariable UUID siteId) { return service.listAssets(siteId); }
    @PostMapping("/technicians") @ResponseStatus(HttpStatus.CREATED) public TechnicianView createTechnician(@Valid @RequestBody CreateTechnician request) { return service.createTechnician(request); }
    @GetMapping("/technicians") public List<TechnicianView> technicians() { return service.listTechnicians(); }
    @PostMapping("/inventory") @ResponseStatus(HttpStatus.CREATED) public InventoryView createInventory(@Valid @RequestBody CreateInventoryItem request) { return service.createInventoryItem(request); }
    @GetMapping("/inventory") public List<InventoryView> inventory() { return service.listInventory(); }
    @PostMapping("/inventory/{itemId}/receipts") public InventoryView receive(@PathVariable UUID itemId, @Valid @RequestBody ReceiveInventory request) { return service.receiveInventory(itemId, request); }
}
