#include <iostream>
#include <string>
#include <vector>
#include <chrono>
#include <thread>
#include <cstring>
#include <nlohmann/json.hpp>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netdb.h>
#include <fcntl.h>  // Pour fcntl et O_NONBLOCK
#include <errno.h>  // Pour errno

using json = nlohmann::json;

struct PortInfo {
    int port;
    std::string service;
};

/**
 * @brief Récupérer le nom du service associé à un port
 * @param port   Port à scanner
 * @return     Nom du service associé au port
 */
std::string getServiceName(int port) {
    struct servent *service = getservbyport(htons(port), "tcp");
    if (service) {
        return std::string(service->s_name);
    }

    // Liste des services courants pour les ports connus
    switch (port) {
        case 21: return "FTP";
        case 22: return "SSH";
        case 23: return "Telnet";
        case 25: return "SMTP";
        case 53: return "DNS";
        case 67: return "DHCP Server";
        case 68: return "DHCP Client";
        case 69: return "TFTP";
        case 80: return "HTTP";
        case 110: return "POP3";
        case 119: return "NNTP";
        case 123: return "NTP";
        case 143: return "IMAP";
        case 161: return "SNMP";
        case 162: return "SNMP Trap";
        case 389: return "LDAP";
        case 443: return "HTTPS";
        case 465: return "SMTPS";
        case 514: return "Syslog";
        case 587: return "SMTP TLS";
        case 636: return "LDAPS";
        case 989: return "FTPS Data";
        case 990: return "FTPS Control";
        case 993: return "IMAPS";
        case 995: return "POP3S";
        case 1433: return "SQL Server";
        case 1521: return "Oracle DB";
        case 3306: return "MySQL";
        case 5432: return "PostgreSQL";
        case 27017: return "MongoDB";
        case 3389: return "RDP";
        case 5900: return "VNC";
        case 6379: return "Redis";
        case 8080: return "HTTP Proxy";
        case 8443: return "HTTPS Alt";
        case 9000: return "SonarQube / PHP-FPM";
        case 9090: return "Prometheus / Openfire / Web Server";
        case 631: return "IPP (Internet Printing Protocol)";
        case 1716: return "KDE Connect";
        case 6463: return "Discord RPC";
        case 17500: return "Dropbox LAN Sync";
        case 17600: return "Dropbox";
        case 17603: return "Dropbox";
        case 32777: return "Docker/Kubernetes Ephemeral Port";
        case 35729: return "LiveReload";
        case 63342: return "JetBrains IDE Services";
        case 1883: return "MQTT";
        case 8883: return "MQTT Secure";
        case 25565: return "Minecraft Server";
        case 10000: return "Webmin";
        default: return "unknown";
    }
}


/**
 *  @brief Vérifier si un port est ouvert sur un hôte
 * @param host  Adresse IP ou nom d'hôte
 * @param port  Port à scanner
 * @param timeout_ms  Timeout en millisecondes
 * @return  true si le port est ouvert, false sinon
 */
bool isPortOpen(const std::string &host, int port, int timeout_ms) {
    struct sockaddr_in addr;
    int sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock < 0) {
        return false;
    }

    // Configuration du timeout
    struct timeval timeout;
    timeout.tv_sec = timeout_ms / 1000;
    timeout.tv_usec = (timeout_ms % 1000) * 1000;
    setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, &timeout, sizeof(timeout));
    setsockopt(sock, SOL_SOCKET, SO_SNDTIMEO, &timeout, sizeof(timeout));

    // Configuration du socket en non-bloquant
    int flags = fcntl(sock, F_GETFL, 0);
    fcntl(sock, F_SETFL, flags | O_NONBLOCK);

    // Préparation de l'adresse
    memset(&addr, 0, sizeof(addr));
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);

    if (inet_pton(AF_INET, host.c_str(), &addr.sin_addr) <= 0) {
        // Si l'adresse IP n'est pas valide, essayer de résoudre le nom d'hôte
        struct hostent *he = gethostbyname(host.c_str());
        if (he == nullptr) {
            close(sock);
            return false;
        }
        memcpy(&addr.sin_addr, he->h_addr, he->h_length);
    }

    // Tentative de connexion
    int res = connect(sock, (struct sockaddr *) &addr, sizeof(addr));

    if (res < 0) {
        if (errno == EINPROGRESS) {
            // Attente de connexion avec select
            fd_set fdset;
            FD_ZERO(&fdset);
            FD_SET(sock, &fdset);

            res = select(sock + 1, NULL, &fdset, NULL, &timeout);
            if (res > 0) {
                // Vérifier si la connexion a réussi
                int so_error;
                socklen_t len = sizeof(so_error);
                getsockopt(sock, SOL_SOCKET, SO_ERROR, &so_error, &len);
                if (so_error == 0) {
                    close(sock);
                    return true;
                }
            }
        }
        close(sock);
        return false;
    }

    close(sock);
    return true;
}


/**
 *  @brief Scanner les ports d'un hôte
 * @param host  Adresse IP ou nom d'hôte
 * @param start_port  Port de début
 * @param end_port  Port de fin
 * @param timeout_ms  Timeout en millisecondes
 * @return  Liste des ports ouverts
 */
std::vector<PortInfo> scanPorts(const std::string &host, int start_port, int end_port, int timeout_ms) {
    std::vector<PortInfo> openPorts;

    // std::cout << "Scanning ports " << start_port << " to " << end_port
    //         << " on " << host << " (timeout: " << timeout_ms << "ms)" << std::endl;

    for (int port = start_port; port <= end_port; ++port) {
        //std::cout << "\rScanning port " << port << "..." << std::flush;

        if (isPortOpen(host, port, timeout_ms)) {
            PortInfo info;
            info.port = port;
            info.service = getServiceName(port);
            openPorts.push_back(info);

            //std::cout << "\rPort " << port << " is open (" << info.service << ")" << std::endl;
        }
    }

    //std::cout << "\rScan completed.                      " << std::endl;
    return openPorts;
}

/**
 *  @brief Fonction principale
 * @param argc  Nombre d'arguments
 * @param argv  Tableau des arguments
 * @return  Code de retour
 */
int main(int argc, char *argv[]) {
    if (argc < 2) {
        std::cerr << "Usage: " << argv[0] << " <host> [start_port] [end_port] [timeout_ms]" << std::endl;
        std::cerr << "Example: " << argv[0] << " 192.168.1.1 1 1024 200" << std::endl;
        return 1;
    }

    std::string host = argv[1];
    int start_port = (argc > 2) ? std::stoi(argv[2]) : 1;
    int end_port = (argc > 3) ? std::stoi(argv[3]) : 65535;
    int timeout_ms = (argc > 4) ? std::stoi(argv[4]) : 200;

    try {
        // Scanner les ports
        std::vector<PortInfo> openPorts = scanPorts(host, start_port, end_port, timeout_ms);

        // Créer l'objet JSON
        json result;
        result["host"] = host;
        result["scan_range"] = {{"start", start_port}, {"end", end_port}};
        result["open_ports"] = json::array();

        for (const auto &port: openPorts) {
            json portInfo;
            portInfo["port"] = port.port;
            portInfo["service"] = port.service;
            result["open_ports"].push_back(portInfo);
        }

        // Afficher le résultat JSON
        std::cout << std::endl << result.dump(4) << std::endl;
    } catch (const std::exception &e) {
        std::cerr << "Error: " << e.what() << std::endl;
        return 1;
    }

    return 0;
}
