package resources;

public class Round {
    String category;
    Question[] questions = new Question[3];
    int currentQuestion = 0;

    public Round(String category) {
        this.category = category;
    }

    public Question[] getQuestions() {
        return questions;
    }

    public void populateQuestion(int number, Question generatedQuestion) {
      if (number >= 0 && number < questions.length) {
          questions[number] = generatedQuestion;
      } else {
          System.err.println("Tried to add element to index out of bounds");
      }
    }
}
