
import java.sql.*;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n1. Add Student");
            System.out.println("2. Mark Attendance");
            System.out.println("3. View Attendance");
            System.out.println("4. Generate Report");
            System.out.println("5. Exit");
            System.out.print("Choose: ");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> addStudent();
                case 2 -> markAttendance();
                case 3 -> viewAttendance();
                case 4 -> generateReport();
                case 5 -> System.exit(0);
            }
        }
    }

    static void addStudent() {
        try (Connection conn = DatabaseHandler.connect()) {
            System.out.print("Name: ");
            String name = scanner.next();
            System.out.print("Roll Number: ");
            String roll = scanner.next();

            String sql = "INSERT INTO students (name, roll_number) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, roll);
            ps.executeUpdate();
            System.out.println("Student added.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void markAttendance() {
        try (Connection conn = DatabaseHandler.connect()) {
            System.out.print("Enter Student ID: ");
            int sid = scanner.nextInt();
            System.out.print("Enter Date (YYYY-MM-DD): ");
            String date = scanner.next();
            System.out.print("Status (Present/Absent): ");
            String status = scanner.next();

            String sql = "INSERT INTO attendance (student_id, date, status) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, sid);
            ps.setString(2, date);
            ps.setString(3, status);
            ps.executeUpdate();
            System.out.println("Attendance marked.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void viewAttendance() {
        try (Connection conn = DatabaseHandler.connect()) {
            String sql = "SELECT s.name, s.roll_number, a.date, a.status FROM students s JOIN attendance a ON s.id = a.student_id";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                System.out.println("Name: " + rs.getString("name") +
                        ", Roll: " + rs.getString("roll_number") +
                        ", Date: " + rs.getString("date") +
                        ", Status: " + rs.getString("status"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void generateReport() {
        try (Connection conn = DatabaseHandler.connect()) {
            String sql = """
                SELECT s.id, s.name, s.roll_number,
                       COUNT(a.id) AS total_days,
                       SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END) AS present_days
                FROM students s
                LEFT JOIN attendance a ON s.id = a.student_id
                GROUP BY s.id, s.name, s.roll_number
            """;

            ResultSet rs = conn.createStatement().executeQuery(sql);
            System.out.println("\n--- Attendance Report ---");
            while (rs.next()) {
                String name = rs.getString("name");
                String roll = rs.getString("roll_number");
                int total = rs.getInt("total_days");
                int present = rs.getInt("present_days");
                double percent = (total == 0) ? 0 : (present * 100.0 / total);

                System.out.printf("Name: %s, Roll: %s, Attendance: %.2f%%\n", name, roll, percent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
