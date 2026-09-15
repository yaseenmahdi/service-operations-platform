package com.yaseenmahdi.serviceops.api;

import com.yaseenmahdi.serviceops.domain.WorkOrderStatus;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/reference")
public class ReferenceDataController {
    private final MessageSource messages;
    public ReferenceDataController(MessageSource messages) { this.messages = messages; }

    @GetMapping("/work-order-statuses")
    public Map<String, String> workOrderStatuses(Locale locale) {
        var response = new LinkedHashMap<String, String>();
        Arrays.stream(WorkOrderStatus.values()).forEach(status -> response.put(status.name(), messages.getMessage("workOrder.status." + status.name(), null, status.name(), locale)));
        return response;
    }
}
