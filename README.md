# 🚗 Smart Parking System with MQTT and Java Microservices

This repository contains the project developed for the **IoT and Microservices Communication** module. The goal was to build a **simulated Smart Parking service** using **Java**, **Spring Boot**, and **MQTT** via **Mosquitto**, integrating a secure RESTful web interface and support for **OAuth2.0 authentication via Facebook**.

---

## 📚 Project Overview

The project simulates the management of multiple parking lots within a city, using:
- Simulated **IoT sensors** (entry, exit, payment)
- MQTT messaging via **Mosquitto broker**
- A secure **web interface** using **Spring Boot**, **HTML/JavaScript**
- **TLS encryption** on MQTT messages
- **SQLite** database for persistent data storage
- **OAuth2.0 login** for admin access via Facebook

All MQTT interactions follow the **publish/subscribe** model with a topic structure like:
parking/{parkingId}/entrata parking/{parkingId}/uscita parking/{parkingId}/cassa

## 🧱 Architecture

The system is based on a **distributed architecture** with these main components:

- **WebApp**: User and admin interface (HTTPS on port 8443)
- **Spring Boot Server**: Core logic, REST API, and MQTT client
- **Mosquitto Broker**: Cloud-based MQTT broker on `test.mosquitto.org` (port 8883 with TLS)
- **Simulated IoT Devices**: Java classes representing entry, exit, and payment sensors
- **SQLite Database**: Stores parking data and tickets

## 🛠️ Technologies Used

- Java 17 + Spring Boot
- MQTT (Mosquitto broker) with TLS (port 8883)
- SQLite + DB Browser
- HTML, JavaScript for front-end
- Facebook OAuth2.0 for authentication
- Threading in Java to simulate IoT devices
- HTTPS with self-signed certificate (parkings.p12)

## 🌐 WebApp Features
### 👤 User Area
- View available parkings
- Enter/Exit a parking lot
- Pay for a ticket

### 🔐 Admin Area (Facebook OAuth2.0)
- Add/remove/modify parking lots
- Activate/deactivate parking access
- View and manage parking details
- Admin access is protected and requires Facebook login.
- The web interface is served over HTTPS on port 8443.

### 📡 MQTT Communication
- Each simulated IoT device is implemented as a Java thread that sends a message to the correct MQTT topic using a dedicated or shared client.
- All MQTT messages are published securely with TLS using Mosquitto's public cloud broker.
1. Entry → parking/{id}/entrata
2. Exit → parking/{id}/uscita
3. Payment → parking/{id}/cassa

A global MQTT client subscribes to all topics and prints received messages in the server logs.

### 🗃️ Database
Stored in src/databasePark.db, contains two tables:
- parkings: All managed parking lots
- tickets: Temporary tickets generated upon entrance
Operations such as add, remove, update are handled by the DbCreate class, triggered by user interactions or admin commands.

### 🔐 Security
- HTTPS (port 8443) with parkings.p12 keystore
- OAuth2.0 login flow using Facebook
- TLS encryption on MQTT communication

🧪 Testing & Validation
- Functional testing via browser + developer tools
- MQTT testing using custom messageArrived() handler in global client
- Database state verified using DB Browser for SQLite
- REST endpoints tested with Postman during development phase
