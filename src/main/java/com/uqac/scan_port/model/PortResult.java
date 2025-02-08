package com.uqac.scan_port.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Represents the result of a port scan, containing information about an open port
 * and the associated application.
 */
public class PortResult {
    private int port;
    private String application;

    /**
     * Constructs a PortResult object with the specified port and application.
     *
     * @param port the port number associated with the result
     * @param application the name of the application associated with the port
     */
    public PortResult(int port, String application) {
        this.port = port;
        this.application = application;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    /**
     * Converts this PortResult object to a JSON string.
     *
     * @return a JSON representation of the PortResult object
     * @throws RuntimeException if JSON serialization fails
     */
    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize PortResult to JSON", e);
        }
    }


}
