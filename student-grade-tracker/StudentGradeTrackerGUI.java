import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Student Grade Tracker — GUI version (Java Swing).
 *
 * How to run:
 *   javac *.java
 *   java StudentGradeTrackerGUI
 */
public class StudentGradeTrackerGUI extends JFrame {

    private final ArrayList<String> names = new ArrayList<>();
    private final ArrayList<Double> marks = new ArrayList<>();

    private final JTextField nameField = new JTextField(16);
    private final JTextField marksField = new JTextField(8);
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JLabel averageLabel = new JLabel("Class average: —");
    private final JLabel highestLabel = new JLabel("Highest marks: —");
    private final JLabel lowestLabel = new JLabel("Lowest marks: —");

    public StudentGradeTrackerGUI() {
        super("Student Grade Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(640, 480);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new String[] {"Name", "Marks", "Grade"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setFillsViewportHeight(true);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildSummaryPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(8, 8));
        header.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));

        JLabel title = new JLabel("Student Grade Tracker", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Marks (0–100):"));
        form.add(marksField);

        JButton addButton = new JButton("Add student");
        JButton removeButton = new JButton("Remove selected");
        JButton clearButton = new JButton("Clear all");

        addButton.addActionListener(e -> addStudent());
        removeButton.addActionListener(e -> removeSelectedStudent());
        clearButton.addActionListener(e -> clearAll());
        marksField.addActionListener(e -> addStudent());

        form.add(addButton);
        form.add(removeButton);
        form.add(clearButton);
        header.add(form, BorderLayout.CENTER);

        JLabel hint = new JLabel(
                "Grades: 90–100 A+  |  80–89 A  |  70–79 B  |  60–69 C  |  50–59 D  |  below 50 F",
                SwingConstants.CENTER);
        header.add(hint, BorderLayout.SOUTH);

        return header;
    }

    private JScrollPane buildTablePanel() {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        return scroll;
    }

    private JPanel buildSummaryPanel() {
        JPanel summary = new JPanel(new GridLayout(3, 1, 4, 4));
        summary.setBorder(BorderFactory.createEmptyBorder(8, 16, 16, 16));
        summary.add(averageLabel);
        summary.add(highestLabel);
        summary.add(lowestLabel);
        return summary;
    }

    private void addStudent() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty.",
                    "Invalid name", JOptionPane.WARNING_MESSAGE);
            nameField.requestFocusInWindow();
            return;
        }

        double mark;
        try {
            mark = Double.parseDouble(marksField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a number between 0 and 100.",
                    "Invalid marks", JOptionPane.WARNING_MESSAGE);
            marksField.requestFocusInWindow();
            return;
        }

        if (mark < 0 || mark > 100) {
            JOptionPane.showMessageDialog(this, "Marks must be between 0 and 100.",
                    "Invalid marks", JOptionPane.WARNING_MESSAGE);
            marksField.requestFocusInWindow();
            return;
        }

        names.add(name);
        marks.add(mark);
        tableModel.addRow(new Object[] {name, String.format("%.2f", mark), GradeCalculator.assignGrade(mark)});

        nameField.setText("");
        marksField.setText("");
        nameField.requestFocusInWindow();
        updateSummary();
    }

    private void removeSelectedStudent() {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a student in the table first.",
                    "Nothing selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        names.remove(row);
        marks.remove(row);
        tableModel.removeRow(row);
        updateSummary();
    }

    private void clearAll() {
        names.clear();
        marks.clear();
        tableModel.setRowCount(0);
        updateSummary();
    }

    private void updateSummary() {
        if (marks.isEmpty()) {
            averageLabel.setText("Class average: —");
            highestLabel.setText("Highest marks: —");
            lowestLabel.setText("Lowest marks: —");
            return;
        }

        int high = GradeCalculator.findHighestIndex(marks);
        int low = GradeCalculator.findLowestIndex(marks);

        averageLabel.setText(String.format("Class average:  %.2f",
                GradeCalculator.calculateAverage(marks)));
        highestLabel.setText(String.format("Highest marks:  %.2f  (%s)",
                marks.get(high), names.get(high)));
        lowestLabel.setText(String.format("Lowest marks:   %.2f  (%s)",
                marks.get(low), names.get(low)));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentGradeTrackerGUI window = new StudentGradeTrackerGUI();
            window.setVisible(true);
        });
    }
}
