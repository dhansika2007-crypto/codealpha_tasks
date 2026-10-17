import java.util.ArrayList;
import java.util.Scanner;

/**
 * Student Grade Tracker
 *
 * A beginner-friendly console app that:
 *   - reads a class of students (names + marks)
 *   - assigns letter grades
 *   - prints a summary report with average, highest, and lowest marks
 *
 * How to run:
 *   javac *.java
 *   java StudentGradeTracker
 *
 * GUI version:
 *   java StudentGradeTrackerGUI
 *
 * Sample session:
 *
 *   === Student Grade Tracker ===
 *   How many students are in the class? 3
 *
 *   --- Student 1 ---
 *   Name: Alice
 *   Marks (0-100): 95
 *
 *   --- Student 2 ---
 *   Name: Bob
 *   Marks (0-100): 72
 *
 *   --- Student 3 ---
 *   Name: Cara
 *   Marks (0-100): 48
 *
 *   ========================================
 *            CLASS SUMMARY REPORT
 *   ========================================
 *   Name                 Marks   Grade
 *   ----------------------------------------
 *   Alice                 95.00     A+
 *   Bob                   72.00      B
 *   Cara                  48.00      F
 *   ----------------------------------------
 *   Class average:  71.67
 *   Highest marks:  95.00  (Alice)
 *   Lowest marks:   48.00  (Cara)
 *   ========================================
 */
public class StudentGradeTracker {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Student Grade Tracker ===");

        // Parallel lists: names.get(i) belongs with marks.get(i)
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Double> marks = new ArrayList<>();

        int studentCount = readPositiveInt(scanner, "How many students are in the class? ");

        for (int i = 0; i < studentCount; i++) {
            System.out.println();
            System.out.println("--- Student " + (i + 1) + " ---");
            names.add(readName(scanner));
            marks.add(readMarks(scanner));
        }

        printSummaryReport(names, marks);
        scanner.close();
    }

    /**
     * Reads a whole number that is at least 1.
     * Repeats the prompt until the input is valid.
     */
    public static int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);

            if (!scanner.hasNextInt()) {
                System.out.println("Please enter a whole number (for example: 5).");
                scanner.nextLine(); // discard the invalid line
                continue;
            }

            int value = scanner.nextInt();
            scanner.nextLine(); // consume the leftover newline

            if (value < 1) {
                System.out.println("Please enter a number that is at least 1.");
                continue;
            }

            return value;
        }
    }

    /**
     * Reads a non-empty student name.
     */
    public static String readName(Scanner scanner) {
        while (true) {
            System.out.print("Name: ");
            String name = scanner.nextLine().trim();

            if (name.isEmpty()) {
                System.out.println("Name cannot be empty. Please try again.");
                continue;
            }

            return name;
        }
    }

    /**
     * Reads marks and checks they are a number between 0 and 100.
     */
    public static double readMarks(Scanner scanner) {
        while (true) {
            System.out.print("Marks (0-100): ");

            if (!scanner.hasNextDouble()) {
                System.out.println("Please enter a number between 0 and 100.");
                scanner.nextLine();
                continue;
            }

            double marks = scanner.nextDouble();
            scanner.nextLine();

            if (marks < 0 || marks > 100) {
                System.out.println("Marks must be between 0 and 100.");
                continue;
            }

            return marks;
        }
    }

    /**
     * Prints a formatted table of every student, then class statistics.
     */
    public static void printSummaryReport(ArrayList<String> names, ArrayList<Double> marks) {
        double average = GradeCalculator.calculateAverage(marks);
        int highestIndex = GradeCalculator.findHighestIndex(marks);
        int lowestIndex = GradeCalculator.findLowestIndex(marks);

        System.out.println();
        System.out.println("========================================");
        System.out.println("         CLASS SUMMARY REPORT");
        System.out.println("========================================");
        System.out.printf("%-20s %7s  %5s%n", "Name", "Marks", "Grade");
        System.out.println("----------------------------------------");

        for (int i = 0; i < names.size(); i++) {
            String grade = GradeCalculator.assignGrade(marks.get(i));
            System.out.printf("%-20s %7.2f  %5s%n", names.get(i), marks.get(i), grade);
        }

        System.out.println("----------------------------------------");
        System.out.printf("Class average:  %.2f%n", average);
        System.out.printf("Highest marks:  %.2f  (%s)%n",
                marks.get(highestIndex), names.get(highestIndex));
        System.out.printf("Lowest marks:   %.2f  (%s)%n",
                marks.get(lowestIndex), names.get(lowestIndex));
        System.out.println("========================================");
    }
}
