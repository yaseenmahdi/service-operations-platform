package com.yaseenmahdi.serviceops.api;

import com.yaseenmahdi.serviceops.api.dto.Responses.DashboardView;
import com.yaseenmahdi.serviceops.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService service;
    public DashboardController(DashboardService service) { this.service = service; }
    @GetMapping public DashboardView dashboard() { return service.get(); }
}
