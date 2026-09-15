package com.yaseenmahdi.serviceops.service;

import com.yaseenmahdi.serviceops.api.dto.Requests.*;
import com.yaseenmahdi.serviceops.api.dto.Responses.*;
import com.yaseenmahdi.serviceops.domain.*;
import com.yaseenmahdi.serviceops.exception.NotFoundException;
import com.yaseenmahdi.serviceops.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CatalogService {
    private final CustomerRepository customers;
    private final ServiceSiteRepository sites;
    private final AssetRepository assets;
    private final TechnicianRepository technicians;
    private final InventoryItemRepository inventory;
    private final ApiViewMapper mapper;

    public CatalogService(CustomerRepository customers, ServiceSiteRepository sites, AssetRepository assets, TechnicianRepository technicians, InventoryItemRepository inventory, ApiViewMapper mapper) {
        this.customers = customers; this.sites = sites; this.assets = assets; this.technicians = technicians; this.inventory = inventory; this.mapper = mapper;
    }

    public CustomerView createCustomer(CreateCustomer request) { return mapper.customer(customers.save(new Customer(request.name().trim(), request.email().trim().toLowerCase(), request.phone()))); }
    @Transactional(readOnly = true) public List<CustomerView> listCustomers() { return customers.findAll().stream().map(mapper::customer).toList(); }

    public SiteView createSite(UUID customerId, CreateSite request) {
        ZoneId.of(request.timeZone());
        Customer customer = customers.findById(customerId).orElseThrow(() -> new NotFoundException("Customer not found"));
        return mapper.site(sites.save(new ServiceSite(customer, request.name().trim(), request.addressLine1().trim(), request.city().trim(), request.state().trim(), request.postalCode().trim(), request.timeZone().trim())));
    }
    @Transactional(readOnly = true) public List<SiteView> listSites(UUID customerId) { return sites.findByCustomerIdOrderByName(customerId).stream().map(mapper::site).toList(); }

    public AssetView createAsset(UUID siteId, CreateAsset request) {
        ServiceSite site = sites.findById(siteId).orElseThrow(() -> new NotFoundException("Service site not found"));
        return mapper.asset(assets.save(new Asset(site, request.name().trim(), request.assetType().trim(), request.serialNumber().trim(), request.installedOn())));
    }
    @Transactional(readOnly = true) public List<AssetView> listAssets(UUID siteId) { return assets.findBySiteIdOrderByName(siteId).stream().map(mapper::asset).toList(); }

    public TechnicianView createTechnician(CreateTechnician request) { return mapper.technician(technicians.save(new Technician(request.fullName().trim(), request.email().trim().toLowerCase(), request.skills()))); }
    @Transactional(readOnly = true) public List<TechnicianView> listTechnicians() { return technicians.findAll().stream().map(mapper::technician).toList(); }

    public InventoryView createInventoryItem(CreateInventoryItem request) { return mapper.inventory(inventory.save(new InventoryItem(request.sku().trim().toUpperCase(), request.name().trim(), request.quantityOnHand(), request.reorderPoint()))); }
    public InventoryView receiveInventory(UUID itemId, ReceiveInventory request) {
        InventoryItem item = inventory.findById(itemId).orElseThrow(() -> new NotFoundException("Inventory item not found"));
        item.receive(request.quantity());
        return mapper.inventory(item);
    }
    @Transactional(readOnly = true) public List<InventoryView> listInventory() { return inventory.findAllByOrderBySku().stream().map(mapper::inventory).toList(); }
}
