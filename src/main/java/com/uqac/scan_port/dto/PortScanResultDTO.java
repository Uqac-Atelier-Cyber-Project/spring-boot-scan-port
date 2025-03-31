package com.uqac.scan_port.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PortScanResultDTO {
    // Getters et Setters
    private Long reportId;
    private String host;
    private String message;
    private String error;
    private ScanRangeDTO scanRange;
    private List<PortInfoDTO> openPorts;

    // Constructeurs
    public PortScanResultDTO() {
    }

    public PortScanResultDTO(Long reportId, String host, String message, String error, ScanRangeDTO scanRange, List<PortInfoDTO> openPorts) {
        this.reportId = reportId;
        this.host = host;
        this.message = message;
        this.error = error;
        this.scanRange = scanRange;
        this.openPorts = openPorts;
    }

    @Override
    public String toString() {
        return "{" +
                "reportId=" + reportId +
                ", host='" + host + '\'' +
                ", message='" + message + '\'' +
                ", error='" + error + '\'' +
                ", scanRange=" + scanRange +
                ", openPorts=" + openPorts +
                '}';
    }
}
