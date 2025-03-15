package com.uqac.scan_port.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PortInfoDTO {
    private int port;
    private String service;

    // Constructeurs
    public PortInfoDTO() {}

    public PortInfoDTO(int port, String service) {
        this.port = port;
        this.service = service;
    }

    @Override
    public String toString() {
        return "{" +
                "port=" + port +
                ", service='" + service + '\'' +
                '}';
    }
}
