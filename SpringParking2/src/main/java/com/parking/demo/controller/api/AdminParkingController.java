package com.parking.demo.controller.api;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Optional;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.parking.demo.DbCreate;
import com.parking.demo.model.Parcheggio;
import com.parking.demo.service.ParkingService;



@RestController
public class AdminParkingController {

    private ParkingService parkingService;
    private DbCreate dbCreate = new DbCreate();

    public AdminParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    
    @RequestMapping("/admin/api/parkings")
    public Iterable<Parcheggio> getAll() {
        return parkingService.getAll();
    }

    
    @RequestMapping("/admin/api/parkings/{id}")
    public Parcheggio getParkingById(@PathVariable("id") int idParcheggio) {
        Optional<Parcheggio> parcheggio = parkingService.getParkingById(idParcheggio);
        if (parcheggio.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "item not found");
        }
        return parcheggio.get();
    }

    
    @RequestMapping(value = "/admin/api/parkings", method = RequestMethod.POST)
    public Parcheggio creaParcheggio(@RequestBody Parcheggio parcheggio) throws IOException, MqttException, KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
        return parkingService.creaParcheggio(parcheggio);
    }
    
    
    @RequestMapping(value = "/admin/api/parkings/{id}", method = RequestMethod.DELETE)
    public void rimuoviParcheggio(@PathVariable("id") int idParcheggio) {
        parkingService.deleteParcheggio(idParcheggio);
    }
    
    
    @RequestMapping(value = "/admin/api/parkings/{id}", method = RequestMethod.PUT)
    public void updateParcheggio(@PathVariable("id") int idParcheggio) {
        parkingService.updateParcheggio(idParcheggio);
    }
    
    
}

