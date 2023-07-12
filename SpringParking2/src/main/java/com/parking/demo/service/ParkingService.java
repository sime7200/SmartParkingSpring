package com.parking.demo.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

import com.parking.demo.DbCreate;
import com.parking.demo.model.CassaDevice;
import com.parking.demo.model.EntrataDevice;
import com.parking.demo.model.Parcheggio;
import com.parking.demo.model.UscitaDevice;



@Service
public class ParkingService {

    private DbCreate dbCreate;
    private MqttClient mqttClient;
	private final String broker = "ssl://test.mosquitto.org:8883";
	private Map<Integer, MqttClient> mqttClients = new HashMap<>();
	private MqttClient mqttClientGlobale;
	private List<Parcheggio> parcheggi;


	
    public ParkingService(DbCreate dbCreate, MqttClient mqttClient) throws MqttException, KeyManagementException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, KeyStoreException, IOException {
        this.dbCreate = dbCreate;
        this.setMqttClient(mqttClient);
        creaGlobalMqttClient(true); 
    }
	
    
    // ho creato var nel caso in cui venga aggiunto un nuovo parcheggio
    // così viene aggiunto in parcheggi senza ricreare il clientMqtt
    private void creaGlobalMqttClient(boolean var) throws MqttException, KeyManagementException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, KeyStoreException {
        parcheggi = new ArrayList<Parcheggio>();
    	parcheggi=dbCreate.getAllParkings();
    	if(var) {
    		mqttClientGlobale = new MqttClient(broker, "global-client");
        	MqttConnectOptions options = new MqttConnectOptions();
            char[] truststorePassword = "123456".toCharArray();
            KeyStore truststore = KeyStore.getInstance("JKS");
            truststore.load(new FileInputStream("src/main/java/prova/truststore.jks"), truststorePassword);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(truststore);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
            options.setSocketFactory(sslContext.getSocketFactory());
            mqttClientGlobale.connect(options);
            
    	}
        // Sottoscrizione a tutti i topic dei parcheggi
        for (Parcheggio p : parcheggi) {
        	int idP = p.getIdParcheggio();
        	String idParcheggio = String.valueOf(idP);
            String topicEntrata = "parking/" + idParcheggio + "/entrata";
            String topicUscita = "parking/" + idParcheggio + "/uscita";
            String topicCassa = "parking/" + idParcheggio + "/cassa";
            mqttClientGlobale.subscribe(topicEntrata);
            mqttClientGlobale.subscribe(topicUscita);
            mqttClientGlobale.subscribe(topicCassa);
        }
     	mqttClientGlobale.setCallback(new MqttCallback() {
			
     		// quando ricevo un messaggio mqtt, stampo sul terminale info su quel messaggio
			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				String contenuto = new String(message.getPayload());
		        System.out.println("Messaggio ricevuto. Topic: " + topic + ", Messaggio MQTT: " + contenuto);

		        if (topic.endsWith("entrata")) {
		            int idParcheggio = Integer.parseInt(topic.split("/")[1]);
		            System.out.println("Apertura transenna per l'entrata del parcheggio " + idParcheggio);
		        } 
		        else if (topic.endsWith("uscita")) {
		            int idParcheggio = Integer.parseInt(topic.split("/")[1]);
		            System.out.println("Apertura transenna per l'uscita del parcheggio " + idParcheggio);
		        } 
		        else if (topic.endsWith("cassa")) {
		            int idParcheggio = Integer.parseInt(topic.split("/")[1]);
		            System.out.println("Pagamento del parcheggio " + idParcheggio);
		        }				
			}
			@Override
			public void deliveryComplete(IMqttDeliveryToken arg0) {
				// TODO Auto-generated method stub			
			}
			@Override
			public void connectionLost(Throwable arg0) {
				// TODO Auto-generated method stub
			}
		});
    }
	
	
    
    public MqttClient getMqttClient() {
		return mqttClient;
	}

	public void setMqttClient(MqttClient mqttClient) {
		this.mqttClient = mqttClient;
	}

    public Iterable<Parcheggio> getAll() {
        return dbCreate.getAllParkings();
    }

    
    public Parcheggio creaParcheggio(Parcheggio parcheggio) throws IOException, KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException, MqttException {
    	boolean isOpen = false;
        if (parcheggio.getIsOpen() != null) {
            isOpen = parcheggio.getIsOpen().equals("APERTO");
        }
        dbCreate.addParking(parcheggio.getNomeParcheggio(), parcheggio.getNumPostiTot(), parcheggio.getNumPostiAvailable(), isOpen);
        creaGlobalMqttClient(false);
        return parcheggio;
    }


    public Optional<Parcheggio> getParkingById(int idParcheggio) {
        List<Parcheggio> parkings = dbCreate.getAllParkings();
        for (Parcheggio parcheggio : parkings) {
            if (parcheggio.getIdParcheggio() == idParcheggio) {
                return Optional.of(parcheggio);
            }
        }
        return Optional.empty();
    }


    public void deleteParcheggio(int idParcheggio) {
    	dbCreate.rimuoviParcheggio(idParcheggio);
	}
	
    public void updateParcheggio(int idParcheggio) {
    	dbCreate.modificaStatoParcheggio(idParcheggio);
	}
    
    
    
    // creo un mqttClient solo se non esiste già. max 1 per ogni parcheggio
    private void inviaMessaggioMqtt(int idParcheggio, String tipoMessaggio, String testoMessaggio)
            throws MqttException, KeyStoreException, NoSuchAlgorithmException, CertificateException,
            FileNotFoundException, IOException, KeyManagementException {

        String idParcheggioString = String.valueOf(idParcheggio);
        // Controllo se esiste già un client MQTT associato all'idParcheggio
        MqttClient client = mqttClients.get(idParcheggio);
        if (client == null) {
            // Creo un nuovo client MQTT solo se non esiste
        	//System.out.println("Creato un nuovo mqttClient");
            client = new MqttClient(broker, idParcheggioString);
            mqttClients.put(idParcheggio, client);  
        }
        MqttConnectOptions options = new MqttConnectOptions();
        char[] truststorePassword = "123456".toCharArray();
        KeyStore truststore = KeyStore.getInstance("JKS");
        truststore.load(new FileInputStream("src/main/java/prova/truststore.jks"), truststorePassword);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(truststore);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
        options.setSocketFactory(sslContext.getSocketFactory());
        client.connect(options);
        
        String topic = "parking/" + idParcheggioString + "/" + tipoMessaggio;
        String message = testoMessaggio;
        client.publish(topic, message.getBytes(), 0, false);
        client.disconnect();
    }
    
   
    
    // messaggi mqtt chiamati nei thread
    public void messaggioEntrata(int idParcheggio) throws MqttException, KeyStoreException, NoSuchAlgorithmException,
	    CertificateException, FileNotFoundException, IOException, KeyManagementException {
		String testoMessaggio = "Verificato accesso nel parcheggio " + idParcheggio;
		inviaMessaggioMqtt(idParcheggio, "entrata", testoMessaggio);
	}
    
    
    public void messaggioUscita(int idParcheggio) throws MqttException, KeyStoreException, NoSuchAlgorithmException,
    CertificateException, FileNotFoundException, IOException, KeyManagementException {
		String testoMessaggio = "Verificata uscita dal parcheggio " + idParcheggio;
		inviaMessaggioMqtt(idParcheggio, "uscita", testoMessaggio);
	}

    
    public void messaggioCassa(int idParcheggio) throws MqttException, KeyStoreException, NoSuchAlgorithmException,
    CertificateException, FileNotFoundException, IOException, KeyManagementException {
		String testoMessaggio = "Pagamento ticket nel parcheggio " + idParcheggio;
		inviaMessaggioMqtt(idParcheggio, "cassa", testoMessaggio);
	}

  
    // Funzioni richiamate in ParkingController.
    // richiama thread per mandare messaggio mqtt
    public void accessoParcheggio(int idParcheggio) throws Exception {
    	if(dbCreate.accessoParcheggio(idParcheggio)) {
    		EntrataDevice entrataDevice = new EntrataDevice(idParcheggio);
    		entrataDevice.setParkingService(this);
    		Thread entrataThread = new Thread(entrataDevice);
    		entrataThread.start();
    	}
	}
       
    // richiama thread per mandare messaggio mqtt
    public void uscitaParcheggio(int idParcheggio) throws MqttException, KeyManagementException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, KeyStoreException, IOException {
	    dbCreate.uscitaParcheggio(idParcheggio);
	    UscitaDevice uscitaDevice = new UscitaDevice(idParcheggio);
	    uscitaDevice.setParkingService(this);
	    Thread uscitaThread = new Thread(uscitaDevice);
	    uscitaThread.start();
	}
        
    // richiama thread per mandare messaggio mqtt
    public void pagaParcheggio(int idParcheggio) throws MqttException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException {
    	CassaDevice cassaDevice = new CassaDevice(idParcheggio);
    	cassaDevice.setParkingService(this);
	    Thread cassaThread = new Thread(cassaDevice);
	    cassaThread.start();
    }
    
    
}

