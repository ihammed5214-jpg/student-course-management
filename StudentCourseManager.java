import java.io.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.GridLayout;

public class StudentCourseManager {

    static class Course {
        String code;
        String title;
        int units;

        Course(String code, String title, int units) {
            if (code == null || code.trim().isEmpty())
                throw new IllegalArgumentException("Course code cannot be empty.");
            if (title == null || title.trim().isEmpty())
                throw new IllegalArgumentException("Course title cannot be empty.");
            if (units < 1 || units > 12)
                throw new IllegalArgumentException("Units must be between 1 and 12.");
            this.code = code.trim().toUpperCase();
            this.title = title.trim();
            this.units = units;
        }
    }

    static class CourseManager {
        List<Course> courses = new ArrayList<>();

        void add(Course course) {
            for (Course c : courses) {
                if (c.code.equals(course.code))
                    throw new IllegalArgumentException("DuplicateCourseError: Course already exists!");
            }
            courses.add(course);
        }

        Course searchRecursive(String code, int index) {
            if (index >= courses.size()) return null;
            if (courses.get(index).code.equals(code.trim().toUpperCase()))
                return courses.get(index);
            return searchRecursive(code, index + 1);
        }

        int totalUnitsRecursive(int index) {
            if (index >= courses.size()) return 0;
            return courses.get(index).units + totalUnitsRecursive(index + 1);
        }
    }

    static final String FILE_NAME = "courses.txt";

    public static void main(String[] args) {
        CourseManager manager = new CourseManager();
        
        String[] options = {"1. Add Course", "2. View All", "3. Search Code", "4. Total Units", "5. Save", "6. Load", "0. Exit"};

        while (true) {
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Choose an option:",
                    "Student Course Manager",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == 6 || choice == JOptionPane.CLOSED_OPTION) {
                JOptionPane.showMessageDialog(null, "Goodbye.");
                break;
            }

            switch (choice) {
                case 0: addCourse(manager); break;
                case 1: viewAll(manager); break;
                case 2: searchCourse(manager); break;
                case 3: computeUnits(manager); break;
                case 4: saveData(manager); break;
                case 5: loadData(manager); break;
            }
        }
    }

    static void addCourse(CourseManager manager) {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField codeField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField unitsField = new JTextField();

        panel.add(new JLabel("Course Code:"));
        panel.add(codeField);
        panel.add(new JLabel("Course Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Units (1-12):"));
        panel.add(unitsField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add New Course", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String code = codeField.getText();
                String title = titleField.getText();
                String unitsInput = unitsField.getText();

                int units;
                try {
                    units = Integer.parseInt(unitsInput.trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Units must be a number between 1 and 12.");
                }

                Course newCourse = new Course(code, title, units);
                manager.add(newCourse);
                
                JOptionPane.showMessageDialog(null, "Added: " + newCourse.code + " - " + newCourse.title, "Success", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    static void viewAll(CourseManager manager) {
        if (manager.courses.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No courses added yet.", "Course List", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        StringBuilder text = new StringBuilder();
        text.append(String.format("%-10s %-25s %s\n", "CODE", "TITLE", "UNITS"));
        text.append("--------------------------------------------------\n");
        
        for (Course c : manager.courses) {
            text.append(String.format("%-10s %-25s %d\n", c.code, c.title, c.units));
        }
        
        JOptionPane.showMessageDialog(null, text.toString(), "All Courses", JOptionPane.INFORMATION_MESSAGE);
    }

    static void searchCourse(CourseManager manager) {
        String code = JOptionPane.showInputDialog("Enter course code to search:");
        
        if (code == null || code.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please type a course code to search.", "Search Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Course found = manager.searchRecursive(code, 0);
        
        if (found != null) {
            JOptionPane.showMessageDialog(null, found.code + " - " + found.title + " (" + found.units + " units)", "Course Found", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Course " + code.toUpperCase() + " does not exist.", "Not Found", JOptionPane.ERROR_MESSAGE);
        }
    }

    static void computeUnits(CourseManager manager) {
        int total = manager.totalUnitsRecursive(0);
        JOptionPane.showMessageDialog(null, "You have registered for " + total + " credit units in total.", "Total Units", JOptionPane.INFORMATION_MESSAGE);
    }

    static void saveData(CourseManager manager) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Course c : manager.courses) {
                writer.println(c.code + "," + c.title + "," + c.units);
            }
            JOptionPane.showMessageDialog(null, "Data successfully saved to " + FILE_NAME + "!", "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    static void loadData(CourseManager manager) {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(null, "No saved data found.", "Load Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<Course> loaded = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", 3);
                if (parts.length < 3) continue;
                loaded.add(new Course(parts[0], parts[1], Integer.parseInt(parts[2].trim())));
            }
            manager.courses = loaded;
            JOptionPane.showMessageDialog(null, "Successfully loaded " + manager.courses.size() + " courses!", "Loaded", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}