import java.io.IOException;
import java.util.Scanner;
import java.sql.*;

public class JdbcApp {
  private static boolean toQuit = false;
  private static Scanner operationInput = new Scanner(System.in);
  public static JdbcApp activeDB;

  // The instance variables for the class
  public Connection connection;
  public Statement statement;

  // The constructor for the class
  public JdbcApp() {
    connection = null;
    statement = null;
  }

  /**
   * Enum of Operations, for easy menu printing
   * and modularization
   */
  enum Operation {
    FIND_AGENTS_CLIENTS {
      @Override
      public void apply() throws IOException {
        System.out.println("\nPlease enter the name of a city: \n");
        String city = operationInput.next();
        Agents.findAgents(city, activeDB);
        Clients.findClients(city, activeDB);
      }
    },
    PURCHASE_POLICY {
      @Override
      public void apply() throws IOException {
        Policies.purchasePolicy(activeDB);
      }
    },
    LIST_POLICIES_BY_AGENT {
      @Override
      public void apply() throws IOException {
        Policies.findPolicies(activeDB);
      }
    },
    CANCEL_POLICY {
      @Override
      public void apply() throws IOException {
        Policies.cancelPolicy(activeDB);
      }
    },
    ADD_AGENT_FOR_CITY {
      @Override
      public void apply() throws IOException {
        System.out.println("\nPlease enter the name of a city:\n");
        String agentCity = operationInput.next();

        Agents.addAgent(activeDB);
        Agents.findAgents(agentCity, activeDB);
      }
    },
    QUIT {
      @Override
      public void apply() {

        toQuit = true;
      }
    };

    public abstract void apply() throws IOException;
  }

  /**
   * Generates the menu options by printing out the Operations ENUMS
   * @throws IOException
   */
  static void simpleMenu() throws IOException {
    System.out.println("Pick enter one of the following operations (case sensitive):");
    for (Operation o : Operation.values()) {
      System.out.printf("%s%n", o);
    }
    try {
      Operation operationToApply = Operation.valueOf(operationInput.next());
      operationToApply.apply();
    } catch (IllegalArgumentException e) {
      System.out.println("Invalid database operation\n\n");
    }
  }

  public static void main(String[] args) throws IOException, SQLException {
    String Username = "darusse";              // Change to your own username
    String mysqlPassword = "iec7ud0Z";    // Change to your own mysql Password
    activeDB = new JdbcApp();
    activeDB.connect(Username, mysqlPassword);
    activeDB.statement = activeDB.connection.createStatement();
    /*
    initDatabase();
    Operation.ADD_AGENT_FOR_CITY.apply();
    Operation.ADD_AGENT_FOR_CITY.apply();
    Operation.ADD_AGENT_FOR_CITY.apply();
    Operation.LIST_POLICIES_BY_AGENT.apply();
    Operation.LIST_POLICIES_BY_AGENT.apply();
    Operation.PURCHASE_POLICY.apply();
    Operation.LIST_POLICIES_BY_AGENT.apply();
    Operation.CANCEL_POLICY.apply();
    Operation.PURCHASE_POLICY.apply();
    Operation.CANCEL_POLICY.apply();
    Operation.PURCHASE_POLICY.apply();
    Operation.CANCEL_POLICY.apply();
    Operation.PURCHASE_POLICY.apply();
    Operation.CANCEL_POLICY.apply();
    Operation.FIND_AGENTS_CLIENTS.apply();
    Operation.FIND_AGENTS_CLIENTS.apply();
    return;
    */
    while (!toQuit) {
      simpleMenu();
    }
    activeDB.disconnect();

  }

  /**
   * taken from Dr. Gauch's JDBC example
   * @param Username
   * @param mysqlPassword
   * @throws SQLException
   */
  // Connect to the database
  public void connect(String Username, String mysqlPassword) throws SQLException {
    try {
      connection = DriverManager.getConnection("jdbc:mysql://localhost/" + Username + "?" +
          "user=" + Username + "&password=" + mysqlPassword);
    } catch (Exception e) {
      throw e;
    }
  }

  /**
   * taken from Dr. Gauch's JDBC example
   * @throws SQLException
   */
  // Disconnect from the database
  public void disconnect() throws SQLException {
    connection.close();
    statement.close();
  }

  /**
   * taken from Dr. Gauch's JDBC example
   * @throws SQLException
   */
  // Execute an SQL query passed in as a String parameter
  // and print the resulting relation
  public void query(String q) {
    try {
      ResultSet resultSet = statement.executeQuery(q);
      System.out.println("\n---------------------------------");
      System.out.println("Query: \n" + q + "\n\nResult: ");
      print(resultSet);
    } catch (SQLException e) {
      e.printStackTrace();
      e.printStackTrace();
    }
  }

  /**
   * taken from Dr. Gauch's JDBC example
   * @throws SQLException
   */
  // Print the results of a query with attribute names on the first line
  // Followed by the tuples, one per line
  public void print(ResultSet resultSet) throws SQLException {
    ResultSetMetaData metaData = resultSet.getMetaData();
    int numColumns = metaData.getColumnCount();

    printHeader(metaData, numColumns);
    printRecords(resultSet, numColumns);
  }

  /**
   * taken from Dr. Gauch's JDBC example
   * @throws SQLException
   */
  // Print the attribute names
  public void printHeader(ResultSetMetaData metaData, int numColumns) throws SQLException {
    for (int i = 1; i <= numColumns; i++) {
      if (i > 1)
        System.out.print(",  ");
      System.out.print(metaData.getColumnName(i));
    }
    System.out.println();
  }

  /**
   * taken from Dr. Gauch's JDBC example
   * @throws SQLException
   */
  // Print the attribute values for all tuples in the result
  public void printRecords(ResultSet resultSet, int numColumns) throws SQLException {
    String columnValue;
    while (resultSet.next()) {
      for (int i = 1; i <= numColumns; i++) {
        if (i > 1)
          System.out.print(",  ");
        columnValue = resultSet.getString(i);
        System.out.print(columnValue);
      }
      System.out.println("");
    }
  }

  /**
   * taken from Dr. Gauch's JDBC example
   * @throws SQLException
   */
  // Insert into any table, any values from data passed in as String parameters
  public void insert(String table, String values) {
    String query = "INSERT into " + table + " values (" + values + ")";
    try {
      statement.executeUpdate(query);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * taken from Dr. Gauch's JDBC example, updated with HOMEWORK3 values
   * @throws SQLException
   */
  // Remove all records and fill them with values for testing
  // Assumes that the tables are already created
  public static void initDatabase() throws SQLException {
    activeDB.statement.executeUpdate("DELETE from POLICIES_SOLD");
    activeDB.statement.executeUpdate("DELETE from CLIENTS");
    activeDB.statement.executeUpdate("DELETE from AGENTS");
    activeDB.statement.executeUpdate("DELETE from POLICY");
    activeDB.statement.executeUpdate("DELETE from POLICIES_SOLD");

    activeDB.insert("CLIENTS", "101, 'CHRIS', 'DALLAS', 43214");
    activeDB.insert("CLIENTS", "102, 'OLIVIA', 'BOSTON', 83125");
    activeDB.insert("CLIENTS", "103, 'ETHAN', 'FAYETTEVILLE', 72701");
    activeDB.insert("CLIENTS", "104, 'DANIEL', 'NEWYORK', 54341");
    activeDB.insert("CLIENTS", "105, 'TAYLOR', 'ROGERS', 78291");
    activeDB.insert("CLIENTS", "106, 'CLAIRE', 'PHOENIX', 85011");

    activeDB.insert("AGENTS", "201, 'ANDREW', 'DALLAS', 43214");
    activeDB.insert("AGENTS", "202, 'PHILIP', 'PHOENIX', 85011");
    activeDB.insert("AGENTS", "203, 'JERRY', 'BOSTON', 83125");
    activeDB.insert("AGENTS", "204, 'BRYAN', 'ROGERS', 78291");
    activeDB.insert("AGENTS", "205, 'TOMMY', 'DALLAS', 43214");
    activeDB.insert("AGENTS", "206, 'BRANT', 'FAYETTEVILLE', 72701");
    activeDB.insert("AGENTS", "207, 'SMITH', 'ROGERS', 78291");

    activeDB.insert("POLICY", "301, 'CIGNAHEALTH', 'DENTAL', 5");
    activeDB.insert("POLICY", "302, 'GOLD', 'LIFE', 8");
    activeDB.insert("POLICY", "303, 'WELLCARE', 'HOME', 10");
    activeDB.insert("POLICY", "304, 'UNITEDHEALTH', 'HEALTH', 7");
    activeDB.insert("POLICY", "305, 'UNITEDCAR', 'VEHICLE', 9");

    activeDB.insert("POLICIES_SOLD", "401, 204, 106, 303, DATE '2020-01-02', 2000.00");
    activeDB.insert("POLICIES_SOLD", "402, 201, 105, 305, DATE '2019-08-11', 1500.00");
    activeDB.insert("POLICIES_SOLD", "403, 203, 106, 301, DATE '2019-09-08', 3000.00");
    activeDB.insert("POLICIES_SOLD", "404, 207, 101, 305, DATE '2019-06-21', 1500.00");
    activeDB.insert("POLICIES_SOLD", "405, 203, 104, 302, DATE '2019-11-14', 4500.00");
    activeDB.insert("POLICIES_SOLD", "406, 207, 105, 305, DATE '2019-12-25', 1500.00");
    activeDB.insert("POLICIES_SOLD", "407, 205, 103, 304, DATE '2020-10-15', 5000.00");
    activeDB.insert("POLICIES_SOLD", "408, 204, 103, 304, DATE '2020-02-15', 5000.00");
    activeDB.insert("POLICIES_SOLD", "409, 203, 103, 304, DATE '2020-01-10', 5000.00");
    activeDB.insert("POLICIES_SOLD", "410, 202, 103, 303, DATE '2020-01-30', 2000.00");
  }
}

