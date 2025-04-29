import java.sql.*;
import java.util.Scanner;

public class RailwayManagement {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/railway?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "Abhinand@2005";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish Connection
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            while (true) {
                System.out.println("\n===== RAILWAY MANAGEMENT SYSTEM =====");
                System.out.println("1. Add Train");
                System.out.println("2. Make a reservation");
                System.out.println("3. View All Trains");
                System.out.println("4. View All Customers");
                System.out.println("5. Search Customer by ID");
                System.out.println("6. View all the passengers of a train");
                System.out.println("7. View all stations for a train");
                System.out.println("8. View all trains in a station");
                System.out.println("9. View trains by departure time");
                System.out.println("10. Cancel booking");
                System.out.println("11. Check number of seats available");
                System.out.println("12. Exit");
                System.out.print("Enter your choice: ");
                
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        addTrain(conn, scanner);
                        break;
                    case 2:
                        makeReservation(conn, scanner);
                        break;
                    case 3:
                        viewTrains(conn);
                        break;
                    case 4:
                        viewPassengers(conn);
                        break;
                    case 5:
                        searchPassengerById(conn, scanner);
                        break;
                    case 6:
                        viewPassengersInTrain(conn, scanner);
                        break;
                    case 7:
                        viewStationsForTrain(conn, scanner);
                        break;
                    case 8:
                        viewTrainsAtStation(conn, scanner);
                        break;
                    case 9:
                        viewTrainsByDeparture(conn, scanner);
                        break;
                    case 10:
                        cancelReservation(conn, scanner);
                        break;
                    case 11:
                        checkSeatAvailability(conn, scanner);
                        break;
                    case 12:
                        System.out.println("Exiting...");
                        conn.close();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    // Add Train to Database
    private static void addTrain(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter Train ID: ");
        int trainId = scanner.nextInt();
        scanner.nextLine(); // Consume newline
    
        System.out.print("Enter Train Name: ");
        String name = scanner.nextLine();
    
        System.out.print("Enter Start Point: ");
        String startPoint = scanner.nextLine();
    
        System.out.print("Enter End Point: ");
        String endPoint = scanner.nextLine();
    
        System.out.print("Enter 1st class capacity: ");
        int firstClass = scanner.nextInt();
    
        System.out.print("Enter 2nd class capacity: ");
        int secondClass = scanner.nextInt();
    
        System.out.print("Enter 3rd class capacity: ");
        int thirdClass = scanner.nextInt();
    
        System.out.print("Enter Sleeper class capacity: ");
        int sleeperClass = scanner.nextInt();
    
        String query = "INSERT INTO Trains (ID, Name, start_point, end_point, cap_1, cap_2, cap_3, cap_sleeper) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, trainId);
        pstmt.setString(2, name);
        pstmt.setString(3, startPoint);
        pstmt.setString(4, endPoint);
        pstmt.setInt(5, firstClass);
        pstmt.setInt(6, secondClass);
        pstmt.setInt(7, thirdClass);
        pstmt.setInt(8, sleeperClass);
    
        int rows = pstmt.executeUpdate();
        System.out.println(rows + " Train added successfully!");
    }
    

    // View All Trains
    private static void viewTrains(Connection conn) throws SQLException {
        String query = "SELECT * FROM Trains";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
    
        System.out.println("\n===== TRAIN DETAILS =====");
        while (rs.next()) {
            System.out.println("Train ID: " + rs.getInt("ID") +
                    ", Name: " + rs.getString("Name") +
                    ", Starting: " + rs.getString("start_point") +
                    ", Destination: " + rs.getString("end_point") +
                    ", 1st Class: " + rs.getInt("cap_1") +
                    ", 2nd Class: " + rs.getInt("cap_2") +
                    ", 3rd Class: " + rs.getInt("cap_3") +
                    ", Sleeper: " + rs.getInt("cap_sleeper"));
        }
    }
    

    // View All Customers
    private static void viewPassengers(Connection conn) throws SQLException {
        String query = "SELECT * FROM Passengers";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
    
        System.out.println("\n===== PASSENGER DETAILS =====");
        while (rs.next()) {
            System.out.println("Passenger ID: " + rs.getInt("C_ID") +
                    ", Name: " + rs.getString("C_name") +
                    ", Train ID: " + rs.getInt("ID") +
                    ", Station: " + rs.getString("station") +
                    ", Departure Time: " + rs.getTime("departure_time") +
                    ", Seat Number: " + rs.getInt("seat_no") +
                    ", Compartment: " + rs.getString("compartment") +
                    ", Gender: " + rs.getString("gender") +
                    ", Class: " + rs.getString("class_type"));
        }
    }
    
    

    // Search Customer by ID
    private static void searchPassengerById(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter Passenger ID to search: ");
        int passengerId = scanner.nextInt();
    
        String query = "SELECT * FROM Passengers WHERE C_ID = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, passengerId);
        ResultSet rs = pstmt.executeQuery();
    
        if (rs.next()) {
            System.out.println("Passenger ID: " + rs.getInt("C_ID") +
                    ", Name: " + rs.getString("C_name") +
                    ", Train ID: " + rs.getInt("ID") +
                    ", Station: " + rs.getString("station") +
                    ", Departure Time: " + rs.getTime("departure_time") +
                    ", Seat No: " + rs.getInt("seat_no") +
                    ", Compartment: " + rs.getString("compartment") +
                    ", Gender: " + rs.getString("gender") +
                    ", Class: " + rs.getString("class_type"));
        } else {
            System.out.println("Passenger not found!");
        }
    }
    
    
    private static void makeReservation(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter Customer ID: ");
        int customerId = scanner.nextInt();
        scanner.nextLine();
    
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
    
        // === Station Validation ===
        String station = "";
        while (true) {
            System.out.print("Enter Station: ");
            station = scanner.nextLine();
    
            String stationQuery = "SELECT COUNT(*) FROM Stations WHERE S_name = ?";
            PreparedStatement stationStmt = conn.prepareStatement(stationQuery);
            stationStmt.setString(1, station);
            ResultSet stationResult = stationStmt.executeQuery();
    
            if (stationResult.next() && stationResult.getInt(1) > 0) break;
            else System.out.println("Invalid station. Please enter a valid station name.");
        }
    
        // === Show Trains at Station ===
        String trainQuery = "SELECT DISTINCT T.ID, T.Name, S.reach_time FROM Trains T JOIN Stations S ON T.ID = S.ID WHERE S.S_name = ? ORDER BY S.reach_time";
        PreparedStatement pstmt = conn.prepareStatement(trainQuery);
        pstmt.setString(1, station);
        ResultSet rs = pstmt.executeQuery();
    
        System.out.println("\nTrains stopping at " + station + ":");
        while (rs.next()) {
            int trainId = rs.getInt("ID");
            System.out.println("Train ID: " + trainId + ", Name: " + rs.getString("Name") + ", Arrival Time: " + rs.getTime("reach_time"));
    
            // Show stations for each train
            String stationListQuery = "SELECT S_name, reach_time, dep_time FROM Stations WHERE ID = ?";
            PreparedStatement stationListStmt = conn.prepareStatement(stationListQuery);
            stationListStmt.setInt(1, trainId);
            ResultSet stationListRs = stationListStmt.executeQuery();
    
            System.out.println("  Stops:");
            while (stationListRs.next()) {
                System.out.println("    - " + stationListRs.getString("S_name") +
                                   " (Arrival: " + stationListRs.getTime("reach_time") +
                                   ", Departure: " + stationListRs.getTime("dep_time") + ")");
            }
        }
    
        System.out.print("\nEnter Train ID: ");
        int trainId = scanner.nextInt();
        scanner.nextLine();
    
        System.out.println("Available Classes: 1st, 2nd, 3rd, sleeper");
        System.out.print("Enter Class Type: ");
        String classType = scanner.nextLine();
    
        System.out.print("Enter Departure Time (HH:MM:SS): ");
        String departure = scanner.nextLine();
    
        System.out.print("Enter Seat Number: ");
        int seatNo = scanner.nextInt();
        scanner.nextLine();
    
        System.out.print("Enter Compartment: ");
        String compartment = scanner.nextLine();
    
        System.out.print("Enter Gender (M/F): ");
        String gender = scanner.nextLine();
    
        // === Prevent Overbooking ===
        String capacityCol = switch (classType.toLowerCase()) {
            case "1st" -> "cap_1";
            case "2nd" -> "cap_2";
            case "3rd" -> "cap_3";
            case "sleeper" -> "cap_sleeper";
            default -> throw new IllegalArgumentException("Invalid class type!");
        };
    
        String checkCapacityQuery =
                "SELECT T." + capacityCol + " AS total, " +
                "       (SELECT COUNT(*) FROM passengers P WHERE P.ID = T.ID AND P.class_type = ?) AS booked " +
                "FROM trains T WHERE T.ID = ?";
    
        PreparedStatement capStmt = conn.prepareStatement(checkCapacityQuery);
        capStmt.setString(1, classType);
        capStmt.setInt(2, trainId);
        ResultSet capRs = capStmt.executeQuery();
    
        if (capRs.next()) {
            int total = capRs.getInt("total");
            int booked = capRs.getInt("booked");
    
            if (booked >= total) {
                System.out.println("No available seats in " + classType + " class. Cannot book!");
                return;
            }
        } else {
            System.out.println("Train not found.");
            return;
        }
    
        // === Check if seat already booked ===
        String checkSeatQuery = "SELECT status FROM passengers WHERE seat_no = ? AND class_type = ? AND ID = ?";
        PreparedStatement checkSeatStmt = conn.prepareStatement(checkSeatQuery);
        checkSeatStmt.setInt(1, seatNo);
        checkSeatStmt.setString(2, classType);
        checkSeatStmt.setInt(3, trainId);
        ResultSet seatResult = checkSeatStmt.executeQuery();
    
        if (seatResult.next() && "booked".equalsIgnoreCase(seatResult.getString("status"))) {
            System.out.println("Seat already booked in this class. Try another seat.");
            return;
        }
    
        // === Book the seat ===
        String insertQuery = "INSERT INTO passengers (C_ID, C_name, station, departure_time, seat_no, compartment, gender, ID, class_type, status) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'booked')";
        PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
        insertStmt.setInt(1, customerId);
        insertStmt.setString(2, name);
        insertStmt.setString(3, station);
        insertStmt.setString(4, departure);
        insertStmt.setInt(5, seatNo);
        insertStmt.setString(6, compartment);
        insertStmt.setString(7, gender);
        insertStmt.setInt(8, trainId);
        insertStmt.setString(9, classType);
    
        int rows = insertStmt.executeUpdate();
        if (rows > 0) {
            System.out.println("Reservation successful in " + classType + " class!");
    
            // === Show remaining seats in this class ===
            capRs = capStmt.executeQuery();
            if (capRs.next()) {
                int total = capRs.getInt("total");
                int booked = capRs.getInt("booked") + 1; // include this one
                System.out.println("Remaining Seats in " + classType + " class: " + (total - booked));
            }
        } else {
            System.out.println("Reservation failed.");
        }
    }
    
    
    
    
    private static void viewPassengersInTrain(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter Train ID: ");
        int trainId = scanner.nextInt();
    
        String query = "SELECT C_name, seat_no, compartment, class_type FROM passengers WHERE ID = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, trainId);
        ResultSet rs = pstmt.executeQuery();
    
        System.out.println("\nPassengers in Train ID " + trainId + ":");
        while (rs.next()) {
            System.out.println("Passenger: " + rs.getString("C_name") +
                               ", Seat No: " + rs.getInt("seat_no") +
                               ", Compartment: " + rs.getString("compartment") +
                               ", Class: " + rs.getString("class_type"));
        }
    }
    
    
    private static void viewTrainsAtStation(Connection conn, Scanner scanner) throws SQLException {
        scanner.nextLine(); // Consume leftover newline
        System.out.print("Enter Station Name: ");
        String station = scanner.nextLine();
    
        String query = "SELECT T.ID, T.Name, S.reach_time " +
                       "FROM Trains T " +
                       "JOIN Stations S ON T.ID = S.ID " +
                       "WHERE S.S_name = ? " +
                       "ORDER BY S.reach_time";
    
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, station);
        ResultSet rs = pstmt.executeQuery();
    
        System.out.println("\nTrains stopping at " + station + " (sorted by Arrival Time):");
        while (rs.next()) {
            System.out.println("Train ID: " + rs.getInt("ID") +
                               ", Name: " + rs.getString("Name") +
                               ", Arrival Time: " + rs.getTime("reach_time"));
        }
    }
    
    
    
    private static void viewStationsForTrain(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter Train ID: ");
        int trainId = scanner.nextInt();
    
        String query = "SELECT S_name, reach_time, dep_time FROM Stations WHERE ID = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, trainId);
        ResultSet rs = pstmt.executeQuery();
    
        System.out.println("\nStations for Train ID " + trainId + ":");
        while (rs.next()) {
            System.out.println("Station Name: " + rs.getString("S_name") +
                               ", Arrival: " + rs.getTime("reach_time") +
                               ", Departure: " + rs.getTime("dep_time"));
        }
    }
    
    

    private static void viewTrainsByDeparture(Connection conn, Scanner scanner) throws SQLException {
        scanner.nextLine(); // Consume leftover newline
        System.out.print("Enter Station Name: ");
        String station = scanner.nextLine();
    
        String query = "SELECT T.ID, T.Name, S.dep_time " +
                       "FROM Trains T " +
                       "JOIN Stations S ON T.ID = S.ID " +
                       "WHERE S.S_name = ? " +
                       "ORDER BY S.dep_time ASC";
    
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, station);
        ResultSet rs = pstmt.executeQuery();
    
        System.out.println("\nTrains departing from " + station + " (sorted by Departure Time):");
        while (rs.next()) {
            System.out.println("Train ID: " + rs.getInt("ID") +
                               ", Name: " + rs.getString("Name") +
                               ", Departure Time: " + rs.getTime("dep_time"));
        }
    }
    

    private static void cancelReservation(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter Customer ID to cancel reservation: ");
        int customerId = scanner.nextInt();
    
        String query = "DELETE FROM passengers WHERE C_ID = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, customerId);
    
        int rows = pstmt.executeUpdate();
        if (rows > 0) {
            System.out.println("Reservation cancelled successfully.");
        } else {
            System.out.println(" No reservation found for this Customer ID.");
        }
    }

    private static void checkSeatAvailability(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter Train ID: ");
        int trainId = scanner.nextInt();
    
        String query = """
            SELECT 
                t.ID,
                t.Name,
                t.cap_1 - COUNT(CASE WHEN p.class_type = '1st' AND p.status = 'booked' THEN 1 END) AS available_first,
                t.cap_2 - COUNT(CASE WHEN p.class_type = '2nd' AND p.status = 'booked' THEN 1 END) AS available_second,
                t.cap_3 - COUNT(CASE WHEN p.class_type = '3rd' AND p.status = 'booked' THEN 1 END) AS available_third,
                t.cap_sleeper - COUNT(CASE WHEN p.class_type = 'sleeper' AND p.status = 'booked' THEN 1 END) AS available_sleeper
            FROM trains t
            LEFT JOIN passengers p ON t.ID = p.ID
            WHERE t.ID = ?
            GROUP BY t.ID, t.Name, t.cap_1, t.cap_2, t.cap_3, t.cap_sleeper
            """;
    
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, trainId);
        ResultSet rs = pstmt.executeQuery();
    
        if (rs.next()) {
            System.out.println("\nSeat Availability for Train ID: " + rs.getInt("ID") +
                               " (" + rs.getString("Name") + ")");
            System.out.println("1st Class     : " + rs.getInt("available_first"));
            System.out.println("2nd Class     : " + rs.getInt("available_second"));
            System.out.println("3rd Class     : " + rs.getInt("available_third"));
            System.out.println("Sleeper Class : " + rs.getInt("available_sleeper"));
        } else {
            System.out.println("Train not found!");
        }
    }
    
    
}
