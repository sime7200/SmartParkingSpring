package com.parking.demo;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AppConfig {

    //@Value("tcp://localhost:1883")
	@Value("ssl://test.mosquitto.org:8883")
    private String brokerUrl;

    @Bean
    public MqttClient mqttClient() throws MqttException {
        MqttClient mqttClient = new MqttClient(brokerUrl, MqttClient.generateClientId());
        mqttClient.setCallback(new MqttCallback() {

			@Override
			public void connectionLost(Throwable arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void deliveryComplete(IMqttDeliveryToken arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
				// TODO Auto-generated method stub
				
			}
        });
        return mqttClient;
    }


}

