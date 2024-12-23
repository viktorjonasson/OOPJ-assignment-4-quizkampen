import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;


public class DataBase {
    private final Map<Integer, TriviaCategory> triviaCategories;
    private final Set<Integer> usedCategories = new HashSet<>();
    private final Random random = new Random();
    private int amountOfQuestions = 0;
    private final Game game;

    private record TriviaCategory(int id, String name) {
        public String getName() {
            return name;
        }
    }

    DataBase(int amountOfQuestions, Game game) {
        this.game = game;
        triviaCategories = new HashMap<>();
        addTriviaCategories();
        this.amountOfQuestions = amountOfQuestions;
    }

    public void loadQuestions(String category) throws Exception {
        int categoryId = getCategoryID(category);
        // Fetch questions from API
        String apiUrl = "https://opentdb.com/api.php?amount=" + amountOfQuestions +
                        "&category=" + categoryId +
                        "&difficulty=medium&type=multiple";

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse JSON response
            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
            JsonArray results = jsonResponse.getAsJsonArray("results");

            ArrayList<String> combinedQuestionSet = new ArrayList<>();
            for (int i = 0; i < results.size(); i++) {
                JsonObject questionObj = results.get(i).getAsJsonObject();

                // Extract question details
                String question = unescapeHtml(questionObj.get("question").getAsString());


                String correctAnswer = unescapeHtml(questionObj.get("correct_answer").getAsString());

                JsonArray incorrectAnswersJson = questionObj.getAsJsonArray("incorrect_answers");

                // Convert incorrect answers
                ArrayList<String> incorrectAnswers = new ArrayList<>();
                for (int j = 0; j < incorrectAnswersJson.size(); j++) {
                    incorrectAnswers.add(unescapeHtml(incorrectAnswersJson.get(j).getAsString()));
                }
                game.round.questions[i] = new Question(category, question, incorrectAnswers, correctAnswer);

                // Create a list of all answers
                ArrayList<String> allAnswers = new ArrayList<>(incorrectAnswers);
                allAnswers.add(correctAnswer);

                // Shuffle the answers
                Collections.shuffle(allAnswers);

                // Prepare the shuffled answers list
                ArrayList<String> shuffledAnswers = new ArrayList<>();
                shuffledAnswers.add(question);
                shuffledAnswers.addAll(allAnswers);

                // Add separator
                if (i < 2) {
                    shuffledAnswers.add("|");
                }


                // Combine the current question's shuffled answers
                combinedQuestionSet.addAll(shuffledAnswers);
            }
        }
    }

    // Helper method to unescape HTML entities
    private String unescapeHtml(String input) {
        return input
                .replace("&quot;", "\"")
                .replace("&#039;", "'")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">");
    }

    public int getCategoryID(String category) {
        game.round.category = category;
        Map<TriviaCategory, Integer> triviaCategoryToKey = new HashMap<>();

        for (Map.Entry<Integer, TriviaCategory> entry : triviaCategories.entrySet()) {
            triviaCategoryToKey.put(entry.getValue(), entry.getKey());
        }

        TriviaCategory triviaCategory = getTriviaCategory(category);
        return triviaCategoryToKey.get(triviaCategory);
    }

    public TriviaCategory getTriviaCategory(String categoryName) {
        // Iterate over the map to find the TriviaCategory by name
        for (Map.Entry<Integer, TriviaCategory> entry : triviaCategories.entrySet()) {
            if (entry.getValue().getName().equalsIgnoreCase(categoryName)) {
                return entry.getValue();
            }
        }
        // Return null if no matching category is found
        return null;
    }

    public String[] getCategorySet() {
        Set<Integer> availableCategories = new HashSet<>(triviaCategories.keySet());
        availableCategories.removeAll(usedCategories);

        String[] categories = new String[3];
        for (int i = 0; i < 3; i++) {
            int randomIndex = random.nextInt(availableCategories.size());
            Integer[] availableArray = availableCategories.toArray(new Integer[0]);
            int randomCategoryID = availableArray[randomIndex];

            TriviaCategory category = triviaCategories.get(randomCategoryID);

            String categoryName = category.name();
            categoryName = categoryName.replaceAll("^(Science:|Entertainment:)", "").trim();

            // This makes sure that the categories are separated by a '|'
            // but there is no trailing '|' after the last element.
            if (i == 2) {
                categories[i] = categoryName;
            } else {
                categories[i] = categoryName + " |";
            }

            usedCategories.add(randomCategoryID);
            availableCategories.remove(randomCategoryID);
        }

        return categories;
    }

    public void addTriviaCategories() {
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
        triviaCategories.put(31, new TriviaCategory(31, "Entertainment: Japanese Anime & Manga"));
        triviaCategories.put(32, new TriviaCategory(32, "Entertainment: Cartoon & Animations"));
    }
}
