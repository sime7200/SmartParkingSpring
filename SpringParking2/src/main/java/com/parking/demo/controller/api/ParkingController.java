package com.parking.demo.controller.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.parking.demo.model.Parcheggio;
import com.parking.demo.service.ParkingService;

@RestController
public class ParkingController {
	
	@Autowired
	private ParkingService parkingService;
	
	
	public ParkingController() {}


	@RequestMapping("/user/api/parkings")
	public Iterable<Parcheggio> getAll() {
		return parkingService.getAll();
	}
	
	@RequestMapping(value = "/user/api/parkings/accesso/{id}", method = RequestMethod.PUT)
    public void accessoParcheggio(@PathVariable("id") int idParcheggio) throws MqttException {
		try {
			parkingService.accessoParcheggio(idParcheggio);
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
    }
	
	
	@RequestMapping(value = "/user/api/parkings/pagamento/{id}", method = RequestMethod.POST)
    public void pagaParcheggio(@PathVariable("id") int idParcheggio) throws MqttException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException {
        parkingService.pagaParcheggio(idParcheggio);
    }
	
	@RequestMapping(value = "/user/api/parkings/uscita/{id}", method = RequestMethod.PUT)
    public void uscitaParcheggio(@PathVariable("id") int idParcheggio) throws MqttException, KeyManagementException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, KeyStoreException, IOException {
        parkingService.uscitaParcheggio(idParcheggio);
    }
	
	@RequestMapping("/api/parkings/{id}")
	public Parcheggio getParkingById(@PathVariable int id) {
		java.util.Optional<Parcheggio> parcheggio = parkingService.getParkingById(id);
		if(parcheggio.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "item not found");
		}
		return parcheggio.get();
	}
}
