package com.uqac.scan_port.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
public class CppExecutionService {

    /**
     * Executes the C++ program with the specified IP address as a parameter.
     * @param ip the IP address to scan.
     * @return the output of the C++ program execution.
     */
    public String executeCppProgram(String ip) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("src/main/java/com/uqac/scan_port/cppScanPort/port_scanner", ip);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return result.toString();
            } else {
                return "Error executing C++ program";
            }
        } catch (Exception e) {
            return "Exception: " + e.getMessage();
        }
    }
}