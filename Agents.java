import java.util.Scanner;
import java.sql.*;
public class Agents {
  private static Scanner operationInput = new Scanner(System.in);

  /**
   * Lists all agents matching the city parameter,
   * @param city
   * @param db
   * @return returns first A_ID in the resultset, to be used for some calling functions
   * returns an empty string if no results
   */
  public static String findAgents(String city, JdbcApp db) {
    String agents = "SELECT * from AGENTS WHERE A_CITY =\"" + city + "\"";
    try {
      ResultSet resultSet = db.statement.executeQuery(agents);
      if (!resultSet.next()) {
        System.out.println("No agents found in that city.");
        return "";
      } else {
        String A_ID =  resultSet.getString(1);
        resultSet.beforeFirst();
        db.print(resultSet);
        return A_ID;
      }

    } catch (SQLException e) {
      e.printStackTrace();
      return "";
    }
  }

  /**
   * calls newAgentInput() and inserts a new agent record based on the input
   * A_ID is auto-incremented, so it is not included in the insert
   * @param db
   */
    public static void addAgent(JdbcApp db) {
    String values = newAgentInput();
    if(values.equals("")) {
      return;
    }
    insert(values, db);
  }

  /**
   *
   * @return Returns values for a new agent insert
   */
  public static String newAgentInput() {
    System.out.println("\nPlease enter the name of the new Agent:\n");
    String agentName = operationInput.next();
    System.out.println("\nPlease enter the city of the new Agent:\n");
    String agentCity = operationInput.next();
    System.out.println("\nPlease enter the 5 digit zip code of the new Agent:\n");
    String agentZip = operationInput.next();
    try {
      int num = Integer.parseInt(agentZip);
      if (num < 1 || num > 99999 ) {
        System.out.println("The zipcode you entered is not valid");
        return "";
      }
    } catch (NumberFormatException e) {
      System.out.println("The zipcode you entered is not valid");
      return "";
    }

    return "'" + agentName + "'," + "'" + agentCity + "'," + agentZip;
  }

  /**
   * Inserts into the AGENTS tables, with the JdbcApp instance supplied,
   * the values paseed in.
   * @param values
   * @param db
   */
  public static void insert(String values, JdbcApp db) {
    String query = "INSERT into AGENTS (A_NAME, A_CITY, A_ZIP) values (" + values + ")";
    System.out.println("\n" + query + "\n");
    try {
      db.statement.executeUpdate(query);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

}

