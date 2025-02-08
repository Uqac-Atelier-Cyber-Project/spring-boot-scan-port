package com.uqac.scan_port.service;

import com.uqac.scan_port.model.PortResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class PortScanService {

    // Table d'association de ports avec applications connues
    private final Map<Integer, String> portToApplicationMap = new HashMap<>() {{
        // Protocoles standards
        put(21, "FTP");          // Transfert de fichiers
        put(22, "SSH");          // Connexion sécurisée
        put(23, "Telnet");       // Connexion à distance (non sécurisé)
        put(25, "SMTP");         // Envoi d'emails
        put(53, "DNS");          // Résolution de noms de domaine
        put(67, "DHCP Server");  // Attribution d'IP
        put(68, "DHCP Client");  // Réception d'IP
        put(69, "TFTP");         // Transfert de fichiers simple (UDP)
        put(80, "HTTP");         // Serveur Web
        put(110, "POP3");        // Récupération d'emails
        put(119, "NNTP");        // Usenet news
        put(123, "NTP");         // Synchronisation de l'heure
        put(143, "IMAP");        // Lecture d’emails en ligne
        put(161, "SNMP");        // Monitoring réseau
        put(162, "SNMP Trap");   // Notifications SNMP
        put(389, "LDAP");        // Annuaire réseau
        put(443, "HTTPS");       // Serveur Web sécurisé
        put(465, "SMTPS");       // SMTP sécurisé
        put(514, "Syslog");      // Logs système (UDP)
        put(587, "SMTP TLS");    // Envoi d’emails sécurisé
        put(636, "LDAPS");       // LDAP sécurisé
        put(989, "FTPS Data");   // Transfert FTP sécurisé
        put(990, "FTPS Control"); // Contrôle FTP sécurisé
        put(993, "IMAPS");       // IMAP sécurisé
        put(995, "POP3S");       // POP3 sécurisé

        // Bases de données
        put(1433, "SQL Server"); // Microsoft SQL Server
        put(1521, "Oracle DB");  // Oracle Database
        put(3306, "MySQL");      // MySQL Database
        put(5432, "PostgreSQL"); // PostgreSQL Database
        put(27017, "MongoDB");   // MongoDB

        // Services divers
        put(3389, "RDP");        // Bureau à distance Windows
        put(5900, "VNC");        // Connexion bureau à distance
        put(6379, "Redis");      // Base de données NoSQL Redis
        put(8080, "HTTP Proxy"); // Proxy web
        put(8443, "HTTPS Alt");  // Serveur web sécurisé alternatif
        put(9000, "SonarQube / PHP-FPM"); // Outils de développement
        put(9090, "Prometheus / Openfire / Web Server"); // Monitoring et applications web

        // Ports détectés sur ton PC
        put(631, "IPP (Internet Printing Protocol)"); // Impression réseau
        put(1716, "KDE Connect"); // Communication entre appareils KDE
        put(6463, "Discord RPC"); // Communication avec Discord
        put(17500, "Dropbox LAN Sync"); // Synchronisation locale Dropbox
        put(17600, "Dropbox"); // Service Dropbox
        put(17603, "Dropbox"); // Autre port utilisé par Dropbox
        put(32777, "Docker/Kubernetes Ephemeral Port");
        put(35729, "LiveReload"); // Outil pour recharger une page web automatiquement
        put(63342, "JetBrains IDE Services"); // IntelliJ, PyCharm, WebStorm...

        // Autres ports potentiellement ouverts
        put(1883, "MQTT");       // Protocole IoT
        put(8883, "MQTT Secure"); // Version sécurisée de MQTT
        put(25565, "Minecraft Server"); // Serveur de jeu Minecraft
        put(10000, "Webmin");    // Interface d’administration serveur
    }};

    private static final int TIMEOUT = 500; // Timeout pour tester un port en millisecondes

    /**
     * Scans a range of ports on the specified target IP address and identifies their status.
     *
     * @param targetIp the target IP address to scan.
     * @param startPort the starting port number of the range to scan, inclusive.
     * @param endPort the ending port number of the range to scan, inclusive.
     * @return a list of PortResult objects representing the scanned ports and their statuses.
     */
    public List<PortResult> scanPorts(String targetIp, int startPort, int endPort) {
        List<PortResult> results = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(50); // Crée un pool de 10 threads

        List<Future<PortResult>> futures = new ArrayList<>();

        for (int port = startPort; port <= endPort; port++) {
            int currentPort = port; // Requis pour l'utilisation dans une lambda
            futures.add(executor.submit(() -> scanPort(targetIp, currentPort)));
        }

        // Attendre les résultats des tâches
        for (Future<PortResult> future : futures) {
            try {
                PortResult result = future.get();
                if (result != null) {
                    results.add(result);
                }
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Erreur lors du traitement : " + e.getMessage());
            }
        }

        // Shutdown le pool de threads
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES); // Attente maximale d'1 minute
        } catch (InterruptedException e) {
            System.err.println("Erreur durant le shutdown du pool de threads : " + e.getMessage());
        }

        return results;
    }

    /**
     * Scans a specific port on a target IP address to check if it is open and attempts to
     * map the port to a known application.
     *
     * @param targetIp the IP address of the target to scan
     * @param port the port number to be scanned
     * @return a PortResult object containing the port number and the associated application name
     *         if the port is open; null if the port is closed or inaccessible due to an error
     */
    private PortResult scanPort(String targetIp, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(targetIp, port), TIMEOUT);
            String app = portToApplicationMap.getOrDefault(port, "Application inconnue");
            return new PortResult(port, app);
        } catch (IOException e) {
            // En cas d'erreur, renvoyer null (port fermé ou inaccessible)
            return null;
        }
    }


}