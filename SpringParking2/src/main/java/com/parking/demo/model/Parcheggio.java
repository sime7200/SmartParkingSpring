package com.parking.demo.model;

import org.eclipse.paho.client.mqttv3.MqttClient;

public class Parcheggio {
	
	private int idParcheggio;
	private String nomeParcheggio;
	private int postiTotali;	
	private int postiDisponibili;
	private String isOpen;
	//private IoTDevices entrance;
	//private IoTDevices exit;
	//private IoTDevices paymentBox;
	private MqttClient mqttClient;

	

	public Parcheggio(int numPostiTot, String nomeParcheggio) {
		this.nomeParcheggio = nomeParcheggio;
		this.postiDisponibili = numPostiTot;
		this.postiTotali = numPostiTot;
		//this.entrance = new IoTDevices("Entrance");
		//this.exit = new IoTDevices("Exit");
		//this.paymentBox = new IoTDevices("Payment");	
	}
	
	
	public Parcheggio(int idParcheggio, String nomeParcheggio, int postiTotali, int postiDisponibili, String isOpen) {
		this.idParcheggio = idParcheggio;
		this.nomeParcheggio = nomeParcheggio;
		this.postiTotali = postiTotali;
		this.postiDisponibili = postiDisponibili;
		this.isOpen = isOpen;
	}
	
	
	public Parcheggio(int idParcheggio, String nomeParcheggio, int postiTotali, int postiDisponibili, String isOpen, MqttClient mqttClient) {
	    this.idParcheggio = idParcheggio;
	    this.nomeParcheggio = nomeParcheggio;
	    this.postiTotali = postiTotali;
	    this.postiDisponibili = postiDisponibili;
	    this.isOpen = isOpen;
	    this.setMqttClient(mqttClient);
	}

	
	// Costruttore di default
	public Parcheggio() {
	}

	public int getPostiTotali() {
		return postiTotali;
	}


	public void setPostiTotali(int postiTotali) {
		this.postiTotali = postiTotali;
	}


	public int getPostiDisponibili() {
		return postiDisponibili;
	}


	public void setPostiDisponibili(int postiDisponibili) {
		this.postiDisponibili = postiDisponibili;
	}

	
	public String getNomeParcheggio() {
		return nomeParcheggio;
	}
	

	public void setNomeParcheggio(String nomeParcheggio) {
		this.nomeParcheggio = nomeParcheggio;
	}



	public void setOpened() {
		isOpen = "APERTO";
	}
	
	public void setClosed() {
		isOpen = "CHIUSO";
	}
	
	
	public String getIsOpen() {
		return isOpen;
	}


	public void setIsOpen(String isOpen) {
		this.isOpen = isOpen;
	}


	public int getIdParcheggio() {
		return idParcheggio;
	}
	
	public int getNumPostiAvailable() {
		return postiDisponibili;
	}

	public int getNumPostiTot() {
		return postiTotali;
	}

	
	
	public boolean isAvailable() {
		return postiDisponibili > 0;
	}
	
	public void parcheggioOccupato() {
        if (isAvailable()) {
            postiDisponibili--;
        }
    }

    public void parcheggioLiberato() {
        if (postiDisponibili < postiTotali) {
            postiDisponibili++;
        }
    }


	public void setIdParcheggio(int newParkingId) {
		idParcheggio = newParkingId;
	}

/*
	public IoTDevices getEntrance() {
		return entrance;
	}


	public void setEntrance(IoTDevices entrance) {
		this.entrance = entrance;
	}


	public IoTDevices getExit() {
		return exit;
	}


	public void setExit(IoTDevices exit) {
		this.exit = exit;
	}


	public IoTDevices getPaymentBox() {
		return paymentBox;
	}


	public void setPaymentBox(IoTDevices paymentBox) {
		this.paymentBox = paymentBox;
	}
*/

	public MqttClient getMqttClient() {
		return mqttClient;
	}


	public void setMqttClient(MqttClient mqttClient) {
		this.mqttClient = mqttClient;
	}

    
    
    /*
    // restituisce l'oggetto IoTDevice associato alla barriera di ingresso
	public IoTDevices getEntrance() {
		return entrance;
	}

	
	// restituisce l'oggetto IoTDevice associato alla barriera di uscita
	public IoTDevices getExit() {
		return exit;
	}

	// restituisce l'oggetto IoTDevice associato al boxPagamento
	public IoTDevices getPaymentBox() {
		return paymentBox;
	}
	*/


}