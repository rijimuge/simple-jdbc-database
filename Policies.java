import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Policies {
  private static Scanner operationInput = new Scanner(System.in);

  /**
   * Encompasses the process of purchasing a policy, from creating a customer to
   * selecting policy type and checking for agents in the specified city and
   * inserting the new POLICIES_SOLD record based on the input
   * PURCHASE_ID is auto incremented
   * @param db
   */
  public static void purchasePolicy(JdbcApp db) {
    String clientData[] = Clients.addClient(db);
    if (clientData[0].equals("")) {
      return;
    }
    String A_ID = Agents.findAgents(clientData[1], db);
    if (A_ID.equals("")) { return; }
    String P_ID = findPolicies("TYPE", db);
    if (P_ID.equals("")) { return; }
    System.out.println("\nPlease enter the dollar amount of the policy you'd like to purchase:\n");
    double amount = 0;
    try {
      amount = ((double) ((int) (Double.parseDouble(operationInput.next()) * 10))) / 10;
    } catch (NumberFormatException e) {
      System.out.println("\nERROR\nPlease enter a valid dollar amount\n");
      return;
    }
    String date = java.time.LocalDate.now().toString();
    String purchaseValues = "\'" + A_ID +
        "\'," + "\'" + clientData[0] + "\'," + "\'" + P_ID + "\', DATE " +
        "\'" + date + "\'," +  "\'" + amount + "\'";
    insertPolicyPurchase(purchaseValues, db);
  }

  /**
   * Finds policies where a specified column is equal to the requested input
   * Currently just used for TYPE
   *
   * @param column
   * @param db
   * @return returns P_ID of the column found, or an empty string if no column found
   */
  public static String findPolicies(String column, JdbcApp db) {
    System.out.println("\nPlease enter the " + column + " of policy you'd like to purchase:\n");
    String policyField = operationInput.next();
    String query = "SELECT * from POLICY WHERE " + column + " =\"" + policyField + "\"";
    try {
      ResultSet resultSet = db.statement.executeQuery(query);
      if (!resultSet.next()) {
        System.out.println("No results found for that policy type.");
        return "";
      } else {
        String P_ID = resultSet.getString(1);
        db.query(query);
        return P_ID;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return "";
    }
  }

  /**
   * Lists Agent name, policy type and commission percentage for a specified Agent name and city
   * @param db
   * @return returns A_ID of the agent found or an empty string if none found
   */
  public static String findPolicies(JdbcApp db) {
    System.out.println("\nPlease enter the name of the agent whose policies sold you'd like to see:\n");
    String agentName = operationInput.next();
    System.out.println("\nPlease enter the city of the agent whose policies sold you'd like to see:\n");
    String city = operationInput.next();


    String query = "SELECT A_NAME, TYPE, COMMISSION_PERCENTAGE from POLICIES_SOLD, AGENTS, POLICY" +
        " WHERE POLICY.POLICY_ID = POLICIES_SOLD.POLICY_ID" +
        " AND AGENT_ID=A_ID AND A_NAME=\"" + agentName + "\" AND A_CITY=\"" + city + "\"";
    try {
      ResultSet resultSet = db.statement.executeQuery(query);
      if (!resultSet.next()) {
        System.out.println("No results found for that agent name and city combo.");
        return "";
      } else {
        String A_ID = resultSet.getString(1);
        db.query(query);
        return A_ID;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return "";
    }
  }

  /**
   * Inserts new policy purchase with supplied values
   * @param values
   * @param db
   */
  public static void insertPolicyPurchase(String values, JdbcApp db) {
    String query = "INSERT into POLICIES_SOLD (AGENT_ID," +
        " CLIENT_ID, POLICY_ID, DATE_PURCHASED, AMOUNT) values (" + values + ")";
    System.out.println("\n" + query + "\n");
    try {
      db.statement.executeUpdate(query);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * delete a purchased policy from the POLICIES_SOLD table based on P_ID input
   * checks for a valid Purchase ID and responds accordingly
   * @param db
   */
  public static void cancelPolicy(JdbcApp db) {
    listSoldPolicies(db);
    System.out.println("Please enter the Purchase ID of the policy you'd like to cancel:");
    String purchaseId = operationInput.next();
    String delete = "DELETE FROM POLICIES_SOLD WHERE PURCHASE_ID=\"" + purchaseId + "\"";
    try {
      int deleted = db.statement.executeUpdate(delete);
      if (deleted == 0) {
        System.out.println("No Policy found with purchase ID " + purchaseId);
      } else {
        System.out.println("Policy with purchase ID " + purchaseId +  " canceled.");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Lists all policies sold.
   * @param db
   */
  private static void listSoldPolicies(JdbcApp db) {
    String query = "SELECT * FROM POLICIES_SOLD";
    db.query(query);
  }
}
