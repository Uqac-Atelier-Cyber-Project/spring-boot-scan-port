#include <iostream>
#include <string>
#include <vector>
#include <unordered_map> // Pour stocker les associations port/service
#include <nlohmann/json.hpp>
#include <arpa/inet.h>
#include <netdb.h>
#include <unistd.h>
#include <sys/socket.h>
#include <fcntl.h>
#include <cstdlib>

using json = nlohmann::json;

struct PortInfo {
    int port;
    std::string service;
};

/**
 * @brief Récupérer le nom du service associé à un port en utilisant une map statique
 */
std::string getServiceName(int port) {
    static const std::unordered_map<int, std::string> serviceMap = {
        {21, "FTP"}, {22, "SSH"}, {23, "Telnet"}, {25, "SMTP"}, {53, "DNS"},
        {67, "DHCP Server"}, {68, "DHCP Client"}, {69, "TFTP"}, {80, "HTTP"},
        {110, "POP3"}, {119, "NNTP"}, {123, "NTP"}, {143, "IMAP"}, {161, "SNMP"},
        {162, "SNMP Trap"}, {389, "LDAP"}, {443, "HTTPS"}, {465, "SMTPS"},
        {514, "Syslog"}, {587, "SMTP TLS"}, {636, "LDAPS"}, {989, "FTPS Data"},
        {990, "FTPS Control"}, {993, "IMAPS"}, {995, "POP3S"}, {1433, "SQL Server"},
        {1521, "Oracle DB"}, {3306, "MySQL"}, {5432, "PostgreSQL"}, {27017, "MongoDB"},
        {3389, "RDP"}, {5900, "VNC"}, {6379, "Redis"}, {8080, "HTTP Proxy"},
        {8443, "HTTPS Alt"}, {9000, "SonarQube / PHP-FPM"}, {9090, "Prometheus / Openfire / Web Server"},
        {631, "IPP (Internet Printing Protocol)"}, {1716, "KDE Connect"}, {6463, "Discord RPC"},
        {17500, "Dropbox LAN Sync"}, {17600, "Dropbox"}, {17603, "Dropbox"},
        {32777, "Docker/Kubernetes Ephemeral Port"}, {35729, "LiveReload"},
        {63342, "JetBrains IDE Services"}, {1883, "MQTT"}, {8883, "MQTT Secure"},
        {25565, "Minecraft Server"}, {10000, "Webmin"}
    };

    // Vérifier si le port est dans la map
    auto it = serviceMap.find(port);
    if (it != serviceMap.end()) {
        return it->second;
    }

    // Tentative de détection dynamique via getservbyport()
    struct servent *service = getservbyport(htons(port), "tcp");
    if (service) {
        return std::string(service->s_name);
    }

    return "Unknown";
}

/**
 * @brief Vérifier si un port est ouvert sur un hôte
 */
bool isPortOpen(const std::string &host, int port, int timeout_ms) {
    struct sockaddr_in addr;
    int sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock < 0) return false;

    struct timeval timeout;
    timeout.tv_sec = timeout_ms / 1000;
    timeout.tv_usec = (timeout_ms % 1000) * 1000;
    setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, &timeout, sizeof(timeout));
    setsockopt(sock, SOL_SOCKET, SO_SNDTIMEO, &timeout, sizeof(timeout));

    memset(&addr, 0, sizeof(addr));
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);

    if (inet_pton(AF_INET, host.c_str(), &addr.sin_addr) <= 0) {
        struct hostent *he = gethostbyname(host.c_str());
        if (!he) {
            close(sock);
            return false;
        }
        memcpy(&addr.sin_addr, he->h_addr, he->h_length);
    }

    int res = connect(sock, (struct sockaddr *) &addr, sizeof(addr));
    close(sock);
    return res == 0;
}

/**
 * @brief Scanner les ports d'un hôte
 */
std::vector<PortInfo> scanPorts(const std::string &host, int start_port, int end_port, int timeout_ms) {
    std::vector<PortInfo> openPorts;
    for (int port = start_port; port <= end_port; ++port) {
        if (isPortOpen(host, port, timeout_ms)) {
            openPorts.push_back({port, getServiceName(port)});
        }
    }
    return openPorts;
}

/**
 * @brief Fonction principale
 */
int main(int argc, char *argv[]) {
    if (argc < 6) {
        json result = {
            {"reportId", -1},
            {"host", ""},
            {"message", "Invalid arguments"},
            {"error", "Usage: <reportId> <host> <start_port> <end_port> <timeout_ms>"},
            {"scanRange", {{"start", 0}, {"end", 0}}},
            {"open_ports", json::array()}
        };
        std::cout << result.dump(4) << std::endl;
        return 1;
    }

    // Validate reportId
    char *endptr;
    long reportId = std::strtol(argv[1], &endptr, 10);
    if (*endptr != '\0' || reportId <= 0) {
        json result = {
            {"reportId", -1},
            {"host", ""},
            {"message", "Invalid reportId"},
            {"error", "reportId must be a positive integer"},
            {"scanRange", {{"start", 0}, {"end", 0}}},
            {"openPorts", json::array()}
        };
        std::cout << result.dump(4) << std::endl;
        return 1;
    }

    std::string host = argv[2];
    int start_port = std::stoi(argv[3]);
    int end_port = std::stoi(argv[4]);
    int timeout_ms = (argc > 5) ? std::stoi(argv[5]) : 200;

    // Start port scanning
    try {
        std::vector<PortInfo> openPorts = scanPorts(host, start_port, end_port, timeout_ms);

        json result = {
            {"reportId", reportId},
            {"host", host},
            {"message", "Scan completed"},
            {"error", ""},
            {"scanRange", {{"start", start_port}, {"end", end_port}}},
            {"openPorts", json::array()}
        };

        for (const auto &port: openPorts) {
            result["openPorts"].push_back({{"port", port.port}, {"service", port.service}});
        }

        std::cout << result.dump(4) << std::endl;
    } catch (const std::exception &e) {
        json result = {
            {"reportId", reportId},
            {"host", host},
            {"message", "Error during scan"},
            {"error", e.what()},
            {"scanRange", {{"start", start_port}, {"end", end_port}}},
            {"openPorts", json::array()}
        };
        std::cout << result.dump(4) << std::endl;
        return 1;
    }

    return 0;
}

