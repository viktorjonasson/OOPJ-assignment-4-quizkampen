import java.util.*;

public class DataBase {
    private final Map<Integer, TriviaCategory> triviaCategories;
    private final Set<Integer> usedCategories = new HashSet<>();
    private final Random random = new Random();

    private static class TriviaCategory {
        private final int id;
        private final String name;

        public TriviaCategory(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    DataBase () {
        triviaCategories = new HashMap<>();
        triviaCategories.put(9, new TriviaCategory(9, "General Knowledge"));
        triviaCategories.put(10, new TriviaCategory(10, "Entertainment: Books"));
        triviaCategories.put(11, new TriviaCategory(11, "Entertainment: Film"));
        triviaCategories.put(12, new TriviaCategory(12, "Entertainment: Music"));
        triviaCategories.put(13, new TriviaCategory(13, "Entertainment: Musicals & Theatres"));
        triviaCategories.put(14, new TriviaCategory(14, "Entertainment: Television"));
        triviaCategories.put(15, new TriviaCategory(15, "Entertainment: Video Games"));
        triviaCategories.put(16, new TriviaCategory(16, "Entertainment: Board Games"));
        triviaCategories.put(17, new TriviaCategory(17, "Science & Nature"));
        triviaCategories.put(18, new TriviaCategory(18, "Science: Computers"));
        triviaCategories.put(19, new TriviaCategory(19, "Science: Mathematics"));
        triviaCategories.put(20, new TriviaCategory(20, "Mythology"));
        triviaCategories.put(21, new TriviaCategory(21, "Sports"));
        triviaCategories.put(22, new TriviaCategory(22, "Geography"));
        triviaCategories.put(23, new TriviaCategory(23, "History"));
        triviaCategories.put(24, new TriviaCategory(24, "Politics"));
        triviaCategories.put(25, new TriviaCategory(25, "Art"));
        triviaCategories.put(26, new TriviaCategory(26, "Celebrities"));
        triviaCategories.put(27, new TriviaCategory(27, "Animals"));
        triviaCategories.put(28, new TriviaCategory(28, "Vehicles"));
        triviaCategories.put(29, new TriviaCategory(29, "Entertainment: Comics"));
        triviaCategories.put(30, new TriviaCategory(30, "Science: Gadgets"));
        triviaCategories.put(31, new TriviaCategory(31, "Entertainment: Japanese Anime & Manga"));
        triviaCategories.put(32, new TriviaCategory(32, "Entertainment: Cartoon & Animations"));
    }

    public ArrayList<String> getQuestionSet() {
        String category = "Category";
        Question question_1 = generateQuestion(category);
        Question question_2 = generateQuestion(category);
        Question question_3 = generateQuestion(category);
        ArrayList<String> question1Shuffled = question_1.getShuffled();
        question1Shuffled.add("|");
        ArrayList<String> question2Shuffled = question_2.getShuffled();
        question2Shuffled.add("|");
        ArrayList<String> question3Shuffled = question_3.getShuffled();

        return new ArrayList<>() {{
            addAll(question1Shuffled);
            addAll(question2Shuffled);
            addAll(question3Shuffled);
        }};
    }

    public String[] getCategorySet() {
        // Create a set of available categories (IDs that haven't been used)
        Set<Integer> availableCategories = new HashSet<>(triviaCategories.keySet());
        availableCategories.removeAll(usedCategories); // Remove used categories

        if (availableCategories.size() < 3) {
            throw new IllegalStateException("Not enough categories left to provide 3 unique categories.");
        }

        String[] result = new String[3];
        for (int i = 0; i < 3; i++) {
            // Randomly pick a category id from the available categories
            int randomIndex = random.nextInt(availableCategories.size());
            Integer[] availableArray = availableCategories.toArray(new Integer[0]);
            int randomId = availableArray[randomIndex];

            // Get the corresponding category name
            TriviaCategory category = triviaCategories.get(randomId);
            String categoryName = category.getName();

            // Remove prefixes like "Science:", "Entertainment:", etc.
            categoryName = categoryName.replaceAll("^(Science:|Entertainment:)", "").trim();

            // Append " |" for the first two categories, but not the last one
            if (i == 2) {
                result[i] = categoryName; // No " |" for the last category
            } else {
                result[i] = categoryName + " |";
            }

            // Mark this category as used
            usedCategories.add(randomId);

            // Remove the selected category from available categories
            availableCategories.remove(randomId);
        }

        return result;
    }


    public Question generateQuestion(String category) {
        String wrongAnswer_1 = "Option 1";
        String wrongAnswer_2 = "Option 2";
        String wrongAnswer_3 = "Option 3";
        String rightAnswer = "Right answer";
        ArrayList<String> wrongAnswers = new ArrayList<>(
                Arrays.asList(wrongAnswer_1, wrongAnswer_2, wrongAnswer_3));
        return new Question(category, "Question 1", wrongAnswers, rightAnswer);
    }
}
