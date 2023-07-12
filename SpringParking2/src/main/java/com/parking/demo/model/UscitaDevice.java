package com.parking.demo.model;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.eclipse.paho.client.mqttv3.MqttException;

import com.parking.demo.service.ParkingService;

public class UscitaDevice implements Runnable {
	
	
    private final int idParcheggio;
    private ParkingService p;


	public UscitaDevice(int idParcheggio) {
        this.idParcheggio = idParcheggio;
    }
	
	public void setParkingService(ParkingService parkingService) {
	    this.p = parkingService;
	}

    public void run() {
        try {
			p.messaggioUscita(idParcheggio);
		} catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException | CertificateException
				| MqttException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}




