package com.uqac.scan_port.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CppExecutionService {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(CppExecutionService.class);

    // Map pour stocker l'état du scan (clé = scanId)
    private final Map<String, String> scanStatus = new ConcurrentHashMap<>();

    /**
     * Exécute le programme C++ de scan de ports
     * @param ip Adresse IP à scanner
     * @param scanId Identifiant du scan
     */
    @Async
    public void executeCppProgram(String ip, String scanId) {
        try {
            scanStatus.put(scanId, "IN_PROGRESS");

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
                logger.info("Scan terminé pour IP {} : {}", ip, result);
                scanStatus.put(scanId, "COMPLETED: " + result.toString());
            } else {
                scanStatus.put(scanId, "ERROR: Exit code " + exitCode);
            }
        } catch (Exception e) {
            scanStatus.put(scanId, "EXCEPTION: " + e.getMessage());
            logger.error("Erreur lors de l'exécution du scan", e);
        }
    }

    /**
     * Lance un scan de ports pour une adresse IP donnée
     * @param scanId Identifiant du scan
     * @return Identifiant du scan
     */
    public String getScanStatus(String scanId) {
        return scanStatus.getOrDefault(scanId, "UNKNOWN_SCAN_ID");
    }
}
