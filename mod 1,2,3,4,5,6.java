
import java.util.*;
import java.nio.file.*;
import java.io.IOException;
import java.util.stream.Collectors;

class Patient {
    private String name;
    private int age;
    private int severity;

    Patient(String name, int age, int severity) {
        this.name = name;
        setAge(age);
        setSeverity(severity);
    }

    public void setAge(int age) {
        if (age < 0)
            throw new IllegalArgumentException("Invalid age");
        this.age = age;
    }

    public void setSeverity(int severity) {
        if (severity < 1 || severity > 5)
            throw new IllegalArgumentException("Severity must be 1-5");
        this.severity = severity;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int getSeverity() {
        return severity;
    }

    public String getPriority() {
        if (severity == 1) return "Critical";
        if (severity == 2) return "High";
        if (severity == 3) return "Medium";
        return "Low";
    }

    public String toString() {
        return name + ", Age: " + age
                + ", Priority: " + getPriority();
    }

    public String toFile() {
        return name + "|" + age + "|" + severity;
    }
}

public class HospitalTriage {
    static final String FILE = "patients.txt";
    static List<Patient> patients = new ArrayList<>();

    // Module 5: Read saved patient records
    static void loadPatients() {
        if (!Files.exists(Path.of(FILE)))
            return;

        try {
            for (String line : Files.readAllLines(Path.of(FILE))) {
                try {
                    String[] data = line.split("\\|", -1);

                    if (data.length != 3 || data[0].trim().isEmpty())
                        continue;

                    Patient p = new Patient(
                        data[0], Integer.parseInt(data[1]),
                        Integer.parseInt(data[2])
                    );
                    patients.add(p);
                } catch (IllegalArgumentException e) {
                    System.out.println("Skipping invalid record.");
                }
            }
        } catch (IOException e) {
            System.out.println("File reading error.");
        }
    }

    // Module 5: Save patient records
    static void savePatients() {
        List<String> lines = patients.stream()
                .map(Patient::toFile)
                .collect(Collectors.toList());

        try {
            Files.write(Path.of(FILE), lines);
            System.out.println("Records saved successfully.");
        } catch (IOException e) {
            System.out.println("File writing error.");
        }
    }

    // Module 3: Add a patient
    static void addPatient(Scanner sc) {
        try {
            System.out.print("Enter patient name: ");
            String name = sc.nextLine().trim();

            System.out.print("Enter patient age: ");
            int age = Integer.parseInt(sc.nextLine());

            System.out.print("Enter severity (1-5): ");
            int severity = Integer.parseInt(sc.nextLine());

            if (name.isEmpty() || name.contains("|"))
                throw new IllegalArgumentException("Invalid name");

            patients.add(new Patient(name, age, severity));
            System.out.println("Patient added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Input error: " + e.getMessage());
        }
    }

    // Module 3: Search for a patient
    static void searchPatient(Scanner sc) {
        System.out.print("Enter patient name to search: ");
        String name = sc.nextLine().trim();

        Optional<Patient> result = patients.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst();

        result.ifPresentOrElse(
            System.out::println,
            () -> System.out.println("Patient not found.")
        );
    }

    // Module 6: Display all patients
    static void displayPatients() {
        if (patients.isEmpty()) {
            System.out.println("No patient records.");
            return;
        }

        patients.forEach(System.out::println);
    }

    // Module 6: Sort by severity
    static void sortPatients() {
        patients.stream()
                .sorted(Comparator.comparingInt(Patient::getSeverity))
                .forEach(System.out::println);
    }

    // Module 6: Count patients by priority
    static void showSummary() {
        Map<String, Long> summary = patients.stream()
                .collect(Collectors.groupingBy(
                    Patient::getPriority, TreeMap::new,
                    Collectors.counting()
                ));

        System.out.println(summary);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        loadPatients();

        int choice = 0;

        do {
            System.out.println("\n=== HOSPITAL TRIAGE SYSTEM ===");
            System.out.println("1. Add Patient");
            System.out.println("2. Display Patients");
            System.out.println("3. Search Patient");
            System.out.println("4. Sort by Severity");
            System.out.println("5. Priority Summary");
            System.out.println("6. Save Records");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 1:
                        addPatient(sc);
                        break;
                    case 2:
                        displayPatients();
                        break;
                    case 3:
                        searchPatient(sc);
                        break;
                    case 4:
                        sortPatients();
                        break;
                    case 5:
                        showSummary();
                        break;
                    case 6:
                        savePatients();
                        break;
                    case 7:
                        savePatients();
                        System.out.println("Thank you!");
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        } while (choice != 7);

        sc.close();
    }
}
