
/**
 * @TODO:
 * 	- show feedback on incorrect answers
 * 	- fix comma separated formatting issues
 * 	- auto clean the csv for bad input
 * 	- give a summary instead of a simply score in the results part
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JOptionPane;

public class CSVQuestions {
	private static final String TITLE = "Translate English -> Latin";
	private ArrayList<String> questions = new ArrayList<String>();
	private ArrayList<String> answers = new ArrayList<String>();
	private ArrayList<Integer> questionsAsked = new ArrayList<Integer>();
	private String feedback = ""; // This is the text that is displayed to provide feedback for the previous
									// question, such as "wrong, <CORRECT ANSWER> etc..."

	void setup(String csvFile) {
		String line = "";
		String cvsSplitBy = "\",";
		String[] row;
		String question;
		String answer;
		InputStream is = getClass().getResourceAsStream(csvFile);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			while ((line = br.readLine()) != null) {
				row = line.split(cvsSplitBy);
				if (row.length == 2) {
					question = row[0].trim().replace("\"", "");
					answer = row[1].trim().replace("\"", "").toLowerCase().replaceAll("[^A-Za-z ]", "");
					// answer = answer.substring(0, answer.indexOf("-"));
					questions.add(question);
					answers.add(answer);
				}
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
		if (questions.size() != answers.size()) {
			System.err.println("Questions length (" + questions.size() + ") not equal to answers length (" + answers.size() + "), exiting!");
			System.exit(1);
		}
	}

	/**
	 * Asks a random question from CSVQuestions' questions array.
	 * 
	 * @return True if answer was correct, false if incorrect.
	 */
	boolean askQuestion() {
		int randomNum = ThreadLocalRandom.current().nextInt(0, questions.size());
		String q = "";
		if (!feedback.isEmpty()) {
			q += feedback + "\n\n";
		}
		q += questions.get(randomNum);
		String answer = JOptionPane.showInputDialog(null, q, TITLE, JOptionPane.QUESTION_MESSAGE);
		if (answer == null) {
			// cancel pressed, exit
			System.out.println("Exiting...");
			System.exit(1);
		}
		questionsAsked.add(randomNum);
		return checkAnswer(answer, answers.get(randomNum));
	}

	boolean checkAnswer(String answerGiven, String correctAnswer) {
		// check if answer is empty
		if (answerGiven.isEmpty()) {
			return false;
		}

		// sanitise
		answerGiven = answerGiven.toLowerCase().trim();
		correctAnswer = correctAnswer.toLowerCase().trim();

		// check substring
		if (correctAnswer.contains(answerGiven)) {
			return true;
		}

		// fail
		return false;
	}

	void askQuestions() {
		while (true) {
			feedback = getFeedback(askQuestion());
		}
	}

	String getFeedback(boolean correct) {
		StringBuilder sb = new StringBuilder();
		sb.append((correct) ? "Correct!\n" : "Not quite!\n");
		sb.append(questions.get(questionsAsked.get(questionsAsked.size() - 1)) + " = " + answers.get(questionsAsked.get(questionsAsked.size() - 1)));
		return sb.toString();
	}

	String questionsToString() {
		StringBuilder sb = new StringBuilder();
		for (String s : questions) {
			sb.append(s + "\n");
		}
		return sb.toString();
	}

	String answersToString() {
		StringBuilder sb = new StringBuilder();
		for (String s : answers) {
			sb.append(s + "\n");
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		CSVQuestions csvq = new CSVQuestions();
		csvq.setup("Level-1-Latin-Glossary-rev-1.csv");
		csvq.askQuestions();
	}
}
