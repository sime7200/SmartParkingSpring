package com.parking.demo.model;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.eclipse.paho.client.mqttv3.*;

import com.parking.demo.service.ParkingService;

public class EntrataDevice implements Runnable {
	
	
	private final int idParcheggio;
    private ParkingService p;


	public EntrataDevice(int idParcheggio) {
        this.idParcheggio = idParcheggio;
    }
	
	public void setParkingService(ParkingService parkingService) {
	    this.p = parkingService;
	}

    public void run() {
        try {
			p.messaggioEntrata(idParcheggio);
		} catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException | CertificateException
				| MqttException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
 
}
