# spring-boot-scan-port

## Description
Ce projet est une application de scan de ports développée en utilisant Spring Boot pour la partie Java et C++ pour la partie scan de ports. L'application permet de scanner les ports ouverts sur un hôte spécifié et de retourner les résultats sous forme de JSON.

## Prérequis
- Java 21 ou supérieur
- Maven
- Un compilateur C++ (comme g++)

## Installation
1. Clonez le dépôt :
   ```bash
   git clone https://github.com/Uqac-Atelier-Cyber-Project/spring-boot-scan-port.git
   cd spring-boot-scan-port
   ```

2. Compilez le code C++ :
   ```bash
   cd src/main/resources/cppScanPort
   g++ -o portScan portScan.cpp
   cd ../../../..
   ```

3. Compilez et packagez l'application Spring Boot :
   ```bash
   mvn clean package
   ```

## Utilisation
Pour lancer l'application, exécutez la commande suivante :
```bash
java -jar target/spring-boot-scan-port-0.0.1-SNAPSHOT.jar
```

### API Endpoints
- **POST /scan** : Lance un scan de ports sur un hôte spécifié.
    - **Request Body** :
      ```json
      {
        "reportId": 1,
        "host": "127.0.0.1",
        "startPort": 20,
        "endPort": 80,
        "timeoutMs": 200
      }
      ```
    - **Response** :
      ```json
      {
        "reportId": 1,
        "host": "127.0.0.1",
        "message": "Scan completed",
        "error": "",
        "scanRange": {
          "start": 20,
          "end": 80
        },
        "openPorts": [
          {
            "port": 22,
            "service": "SSH"
          },
          {
            "port": 80,
            "service": "HTTP"
          }
        ]
      }
      ```

## Structure du Projet
- `src/main/java/com/uqac/scan_port` : Contient le code source Java de l'application.
- `src/main/resources/cppScanPort` : Contient le code source C++ pour le scan de ports.
- `src/main/resources/application.properties` : Fichier de configuration de l'application.

