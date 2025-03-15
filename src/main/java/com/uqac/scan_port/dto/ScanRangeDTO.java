package com.uqac.scan_port.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ScanRangeDTO {
    // Getters et Setters
    private int start;
    private int end;

    // Default constructor
    public ScanRangeDTO() {
    }

    // Parameterized constructor
    public ScanRangeDTO(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return "{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
