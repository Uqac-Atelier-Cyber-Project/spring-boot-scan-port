package com.uqac.scan_port.controller;

import com.uqac.scan_port.model.PortResult;
import com.uqac.scan_port.model.ScanRequest;
import com.uqac.scan_port.service.PortScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scan")
public class PortScanController {

    @Autowired
    private PortScanService portScanService;

    /**
     * Scans the specified target for open ports within the defined port range and
     * retrieves the results of the port scanning operation.
     *
     * @param scanRequest the request data containing the target IP address, start port, and end port range.
     * @return a list of {@link PortResult} objects containing the scanned port and associated application information.
     */
    @PostMapping
    public List<PortResult> scanTarget(@RequestBody ScanRequest scanRequest) {
        // Call Services
        return portScanService.scanPorts(
                scanRequest.getTargetIp().trim(),
                scanRequest.getStartPort(),
                scanRequest.getEndPort()
        );
    }
}
