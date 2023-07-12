package com.parking.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Component;

import com.parking.demo.model.Parcheggio;

@Component
public class DbCreate {

	
	private static final String url = "jdbc:sqlite:src/databasePark.db";
	
	
	
	public static void main(String[] args) {
		try(Connection connection = DriverManager.getConnection(url);
			Statement statement = connection.createStatement()) {
			createTable(statement);
			createTableTickets(statement);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}		
	}
	
	
	
	private static void createTable(Statement statement) throws SQLException {
	    String sql = "CREATE TABLE IF NOT EXISTS parkings (" +
	            "idParcheggio INT PRIMARY KEY, " +
	            "nomeParcheggio TEXT, " +
	            "postiTotali INT, " +
	            "postiDisponibili INT, " +
	            "isOpen INT" +
	            ")";

	    statement.executeUpdate(sql);
	}
	
	
	private static void createTableTickets(Statement statement) throws SQLException {
	    String sql = "CREATE TABLE IF NOT EXISTS tickets (" +
	            "id INT PRIMARY KEY " +
	            ")";

	    statement.executeUpdate(sql);
	}
	

	
	public void addParking(String nomeParcheggio, int postiTotali, int postiDisponibili, boolean isOpen) {
	    int statoParc = (isOpen) ? 1 : 0;
	    try (Connection connection = DriverManager.getConnection(url);
	         PreparedStatement statement = connection.prepareStatement(
	                 "INSERT INTO parkings (nomeParcheggio, postiTotali, postiDisponibili, isOpen) " +
	                         "VALUES (?, ?, ?, ?)")) {

	        statement.setString(1, nomeParcheggio);
	        statement.setInt(2, postiTotali);
	        statement.setInt(3, postiDisponibili);
	        statement.setInt(4, statoParc);

	        int rowsInserted = statement.executeUpdate();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	public void addTicket(int id) {
	    try (Connection connection = DriverManager.getConnection(url);
	         PreparedStatement statement = connection.prepareStatement(
	                 "INSERT INTO tickets (id) " +
	                         "VALUES (?)")) {

	        statement.setInt(1, id);
	        int rowsInserted = statement.executeUpdate();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	

	public List<Parcheggio> getAllParkings() {
        List<Parcheggio> parkings = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM parkings")) {
            while (resultSet.next()) {
                int idParcheggio = resultSet.getInt("idParcheggio");
                String nomeParcheggio = resultSet.getString("nomeParcheggio");
                int postiTotali = resultSet.getInt("postiTotali");
                int postiDisponibili = resultSet.getInt("postiDisponibili");
                String isOpen = resultSet.getString("isOpen");

                Parcheggio parcheggio = new Parcheggio(idParcheggio, nomeParcheggio, postiTotali, postiDisponibili, isOpen);
                parkings.add(parcheggio);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return parkings;
    }

	
	
	public void rimuoviParcheggio(int idParcheggio) {
		String s= "DELETE FROM parkings WHERE idParcheggio=?";
		 try (Connection connection = DriverManager.getConnection(url);
			PreparedStatement deleteStatement = connection.prepareStatement(s)) {	
			 deleteStatement.setInt(1, idParcheggio);
			 
			 int row = deleteStatement.executeUpdate();
			 if(row>0) {
				 System.out.println("Parcheggio rimosso con successo");
	            } else {
	                System.out.println("Nessun parcheggio trovato con l'ID specificato: " + idParcheggio);
	            }
		 }
		 catch (SQLException e) {
			 e.printStackTrace();
		 }
	}
	
	
	
	public void modificaStatoParcheggio(int idParcheggio) {
	    String selectQuery = "SELECT isOpen FROM parkings WHERE idParcheggio=?";
	    String updateQuery = "UPDATE parkings SET isOpen=? WHERE idParcheggio=?";

	    try (Connection connection = DriverManager.getConnection(url);
	         PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
	         PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {

	        selectStatement.setInt(1, idParcheggio);
	        ResultSet resultSet = selectStatement.executeQuery();
	        if (resultSet.next()) {
	            int isOpen = resultSet.getInt("isOpen");
	            int newIsOpen = (isOpen == 1) ? 0 : 1;
	            updateStatement.setInt(1, newIsOpen);
	            updateStatement.setInt(2, idParcheggio);

	            int rowsAffected = updateStatement.executeUpdate();

	            if (rowsAffected > 0) {
	                System.out.println("Stato del parcheggio modificato con successo");
	            } else {
	                System.out.println("Nessun parcheggio trovato con l'ID specificato: " + idParcheggio);
	            }
	        } else {
	            System.out.println("Nessun parcheggio trovato con l'ID specificato: " + idParcheggio);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	
	public boolean accessoParcheggio(int idParcheggio) throws MqttException {
	    String selectQuery = "SELECT postiDisponibili, isOpen FROM parkings WHERE idParcheggio=?";
	    String updateQuery = "UPDATE parkings SET postiDisponibili=? WHERE idParcheggio=?";
	    String insertQuery = "INSERT INTO tickets (id) VALUES (?)";

	    try (Connection connection = DriverManager.getConnection(url);
	         PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
	         PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
	         PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {

	        selectStatement.setInt(1, idParcheggio);
	        ResultSet resultSet = selectStatement.executeQuery();

	        if (resultSet.next()) {
	            int postiDisponibili = resultSet.getInt("postiDisponibili");
	            int isOpen = resultSet.getInt("isOpen");

	            if ((isOpen == 1) && (postiDisponibili > 0)) {
	                int ticketId = generateRandomId();
	                insertStatement.setInt(1, ticketId);
	                insertStatement.executeUpdate();

	                updateStatement.setInt(1, postiDisponibili - 1);
	                updateStatement.setInt(2, idParcheggio);
	                updateStatement.executeUpdate();

	                //System.out.println("Un utente è entrato nel parcheggio " + idParcheggio + ". Ticket generato: " + ticketId);
	                System.out.println("Ticket generato per l'ingresso: " + ticketId);
	                
	                return true;
	            } else {
	                System.out.println("Il parcheggio " + idParcheggio + " non ha posti liberi o è chiuso");
	                return false;
	            }
	        } else {
	            System.out.println("Nessun parcheggio trovato con l'ID specificato: " + idParcheggio);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

	
	
	public void uscitaParcheggio(int idParcheggio) throws MqttException {
		String selectQuery = "SELECT postiDisponibili, isOpen FROM parkings WHERE idParcheggio=?";
	    String updateQuery = "UPDATE parkings SET postiDisponibili=? WHERE idParcheggio=?";

	    try (Connection connection = DriverManager.getConnection(url);
		PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
		PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {    	
	        selectStatement.setInt(1, idParcheggio);
	        ResultSet resultSet = selectStatement.executeQuery();
	        if (resultSet.next()) {
	            int postiDisponibili = resultSet.getInt("postiDisponibili");
	            int isOpen = resultSet.getInt("isOpen");
	            if((isOpen == 1) && (postiDisponibili>0)) {
	            	//messaggioUscita(idParcheggio);
		            int newPostiDisponibili = postiDisponibili+1;
		            // Aggiorna il valore di isOpen nel database
		            updateStatement.setInt(1, newPostiDisponibili);
		            updateStatement.setInt(2, idParcheggio);
		            int rowsAffected = updateStatement.executeUpdate();
		            if (rowsAffected > 0) {
		                System.out.println("Un utente è uscito dal parcheggio "+ idParcheggio);
		            } else {
		                System.out.println("Nessun parcheggio trovato con l'ID specificato: " + idParcheggio);
		            }
		        } else {
		            System.out.println("Il parcheggio " + idParcheggio + " è chiuso");
		        }
	    	}
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	
	public int generateRandomId() {
        Random random = new Random();
        return random.nextInt(Integer.MAX_VALUE);
    }
	
}
