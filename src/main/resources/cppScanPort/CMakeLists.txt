cmake_minimum_required(VERSION 3.30)
project(PortScanner)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Recherche de la bibliothèque nlohmann_json
find_package(nlohmann_json QUIET)
if(NOT nlohmann_json_FOUND)
    include(FetchContent)
    FetchContent_Declare(
            nlohmann_json
            URL https://github.com/nlohmann/json/releases/download/v3.11.3/json.tar.xz
    )
    FetchContent_MakeAvailable(nlohmann_json)
endif()

# Définir les sources
set(SOURCES
    portScan.cpp
)

# Créer l'exécutable port_scanner
add_executable(port_scanner ${SOURCES})

# Lier les bibliothèques pour port_scanner
target_link_libraries(port_scanner PRIVATE nlohmann_json::nlohmann_json)

# Vérifier si nous sommes sur un système UNIX pour ajouter les bibliothèques réseau
if(UNIX)
    find_package(Threads REQUIRED)
    target_link_libraries(port_scanner PRIVATE Threads::Threads)
endif()
