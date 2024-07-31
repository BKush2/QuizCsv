import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Quizcsv {
    private static final int QUIZ_DURATION_SECONDS = 15; // Duration of the quiz in seconds
    private static final int TOTAL_QUESTIONS = 10; // Total number of questions in the quiz

    public static void main(String[] args) {
        String csvFile = "/home/admi/Desktop/quiz.csv";
        List<String[]> questions = new ArrayList<>();

        System.out.println("Welcome to the Quiz! \n" +
                "You will be presented with a series of questions. \n" +
                "Type your answer and press Enter. \n" +
                "To quit the quiz at any time, simply press 'q' and Enter. \n" +
                "Total questions: " + TOTAL_QUESTIONS + "\n" +
                "Let's begin!");

        // Read questions from CSV file
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                questions.add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        Scanner scanner = new Scanner(System.in);
        final int[] answeredCount = {0};
        final int[] correctCount = {0};
        final boolean[] quizFinished = {false}; // Flag to indicate if the quiz is finished

        // Schedule task to mark quiz as finished after time limit
        executor.schedule(() -> {
            if (!quizFinished[0]) {
                quizFinished[0] = true; // Set the quizFinished flag
                System.out.println("\nTime's up!");
                displayResults(answeredCount[0], correctCount[0]);
                System.exit(0); // Terminate the program
            }
        }, QUIZ_DURATION_SECONDS, TimeUnit.SECONDS);

        // Read user input
        while (!quizFinished[0] && answeredCount[0] < TOTAL_QUESTIONS) {
            String[] data = questions.get(answeredCount[0]);
            String question = data[0];
            String correctAnswer = data[1];

            System.out.println("\nQuestion: " + question);
            System.out.print("Your Answer (or 'q' to quit): ");

            String userAnswer = scanner.nextLine().trim();

            if (userAnswer.equals("q")) {
                quizFinished[0] = true; // Mark quiz as finished
                break; // Exit the loop if user wants to quit
            }

            // Validate user input
            if (!isNumeric(userAnswer)) {
                System.out.println("Invalid input. Please enter a number.");
                continue; // Skip the rest of the loop and prompt for input again
            }

            answeredCount[0]++;
            if (userAnswer.equalsIgnoreCase(correctAnswer)) {
                correctCount[0]++;
            }
        }

        // Shutdown the executor if it's still running
        if (!executor.isShutdown()) {
            executor.shutdown();
        }

        // Display results
        displayResults(answeredCount[0], correctCount[0]);

        scanner.close();
    }

    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static void displayResults(int answeredCount, int correctCount) {
        System.out.println("\nQuiz Results:");
        System.out.print("You answered " + answeredCount + "/" + TOTAL_QUESTIONS + " question/s. ");
        if (answeredCount > 0) {
            System.out.println(correctCount + "/" + answeredCount + " were correct.");
        } else {
            System.out.println("No questions were answered.");
        }
    }
}