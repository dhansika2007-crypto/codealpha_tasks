import java.util.ArrayList;

/**
 * Shared grading helpers used by both the console app and the GUI.
 * All methods are static so beginners can call them without creating an object.
 */
public class GradeCalculator {

    /**
     * Converts a mark into a letter grade using if-else.
     */
    public static String assignGrade(double marks) {
        if (marks >= 90) {
            return "A+";
        } else if (marks >= 80) {
            return "A";
        } else if (marks >= 70) {
            return "B";
        } else if (marks >= 60) {
            return "C";
        } else if (marks >= 50) {
            return "D";
        } else {
            return "F";
        }
    }

    /**
     * Returns the average of all marks in the list.
     */
    public static double calculateAverage(ArrayList<Double> marks) {
        double total = 0;
        for (double mark : marks) {
            total += mark;
        }
        return total / marks.size();
    }

    /**
     * Returns the index of the highest mark.
     * If two students share the top score, the first one in the list is used.
     */
    public static int findHighestIndex(ArrayList<Double> marks) {
        int highestIndex = 0;
        for (int i = 1; i < marks.size(); i++) {
            if (marks.get(i) > marks.get(highestIndex)) {
                highestIndex = i;
            }
        }
        return highestIndex;
    }

    /**
     * Returns the index of the lowest mark.
     * If two students share the lowest score, the first one in the list is used.
     */
    public static int findLowestIndex(ArrayList<Double> marks) {
        int lowestIndex = 0;
        for (int i = 1; i < marks.size(); i++) {
            if (marks.get(i) < marks.get(lowestIndex)) {
                lowestIndex = i;
            }
        }
        return lowestIndex;
    }
}
