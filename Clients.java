import java.util.Scanner;
import java.sql.*;

public class Clients {
  private static Scanner operationInput = new Scanner(System.in);

  /**
   * Lists clients by city, or prints a statement that none were found
   * @param city
   * @param db
   */
  public static void findClients(String city, JdbcApp db) {
    String clients = "SELECT * from CLIENTS WHERE C_CITY =\"" + city + "\"";
    try {
      ResultSet resultSet = db.statement.executeQuery(clients);
      if (!resultSet.next()) {
        System.out.println("No clients found in that city.");
        return;
      } else {
        db.print(resultSet);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return;
  }

  /**
   * calls insert and returns string array from insert
   * @param db
   * @return
   */
  public static String[] addClient(JdbcApp db) {
    return insert(newClientInput(), db);
  }

  /**
   * Solicits new client information from the user
   * @return returns values if values are valid, or an empty string if not
   */
  public static String newClientInput() {
    System.out.println("\nPlease enter your name:\n");
    String clientName = operationInput.next();
    System.out.println("\nPlease enter your city:\n");
    String clientCity = operationInput.next();
    System.out.println("\nPlease enter your 5-digit zip code:\n");
    String clientZip = operationInput.next();
    try {
      Integer.parseInt(clientZip);
      int num = Integer.parseInt(clientZip);
      if (num < 1 || num > 99999 ) {
        System.out.println("The zipcode you entered is not valid");
        return "";
      }
    } catch (NumberFormatException e) {
      System.out.println("The zipcode you entered is not valid");
      return "";
    }
    return "'" + clientName + "'," + "'" + clientCity + "'," + clientZip;
  }

  /**
   * inserts new client into CLIENT table
   * @param values
   * @param db
   * @return returns C_ID and CITY if insert is succesfull, otherwise returns an array of 2 empty strings
   */
  public static String[] insert(String values, JdbcApp db) {
    if (values.equals("")) {
      return new String[] {"", ""};
    }
    String query = "INSERT into CLIENTS (C_NAME, C_CITY, C_ZIP) values (" + values + ")";
    String lastInserted = "SELECT * FROM CLIENTS WHERE C_ID=(SELECT LAST_INSERT_ID())";
    System.out.println("\n" + query + "\n");
    try {
      db.statement.executeUpdate(query);
      ResultSet resultSet = db.statement.executeQuery(lastInserted);
      resultSet.next();
      return new String[] {resultSet.getString(1), resultSet.getString(3)};
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return new String[] {"", ""};
  }
}
