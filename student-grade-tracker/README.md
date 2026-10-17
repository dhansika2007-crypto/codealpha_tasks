# Student Grade Tracker

A beginner-level Java app for entering student names and marks, assigning letter grades, and showing class average, highest, and lowest marks.

You can use either interface:

- **GUI (recommended):** a window with a form and a table
- **Console:** questions in the terminal, then a printed report

## Requirements

- Java 8 or later (`javac` and `java` on your PATH)

## How to run (GUI)

```bash
cd C:\Users\Hansika\student-grade-tracker
javac *.java
java StudentGradeTrackerGUI
```

A window opens. Type a name and marks, click **Add student** (or press Enter in the marks box). The table and the summary at the bottom update as you go.

## How to run (console)

```bash
cd C:\Users\Hansika\student-grade-tracker
javac *.java
java StudentGradeTracker
```

## What it does

1. Stores names and marks in `ArrayList`s
2. Validates that marks are numbers between 0 and 100
3. Assigns grades with if-else:
   - 90–100: A+
   - 80–89: A
   - 70–79: B
   - 60–69: C
   - 50–59: D
   - Below 50: F
4. Shows a summary with class average, highest marks, and lowest marks

## Sample console session

```
=== Student Grade Tracker ===
How many students are in the class? 3

--- Student 1 ---
Name: Alice
Marks (0-100): 95

--- Student 2 ---
Name: Bob
Marks (0-100): 72

--- Student 3 ---
Name: Cara
Marks (0-100): 48

========================================
         CLASS SUMMARY REPORT
========================================
Name                   Marks  Grade
----------------------------------------
Alice                  95.00     A+
Bob                    72.00      B
Cara                   48.00      F
----------------------------------------
Class average:  71.67
Highest marks:  95.00  (Alice)
Lowest marks:   48.00  (Cara)
========================================
```
