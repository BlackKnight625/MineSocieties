package ulisboa.tecnico.llms;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  This is the class responsible for interfacing with Open AI's ChatGPT.
 *
 *  Credits to https://rollbar.com/blog/how-to-use-chatgpt-api-with-java/
 */
public class ChatGPTManager extends LLMManager {

    // Private attributes

    private final String apiKey;
    private final String url = "https://api.openai.com/v1/chat/completions";
    private final String model;
    private final Logger logger;
    private final Logger debugLogger;
    private OutputStreamWriter writer;

    // Constructors

    /**
     *  Creates the Manager that allows communication with OpenAI's ChatGPT model.
     * After this Manager is constructed, it needs its initialize method to be called in order
     * to become operational and for its prompt methods to work.
     *  Before the program shuts down or when you no longer need this Manager, call its teardown method.
     * @param apiKey
     *  The OpenAI API Key. You can create one at https://platform.openai.com/account/api-keys
     *  Do not share your key with anyone, as it has a limited amount of prompts.
     *  I advise anyone that uses this class to store the key in a file that can be accessed by the program that
     * runs this class.
     * @param model
     *  The name of the ChatGPT model to be used
     * @param logger
     *  A logger for showing error messages
     */
    public ChatGPTManager(String apiKey, String model, Logger logger, Logger debugLogger) {
        this.apiKey = apiKey;
        this.model = model;
        this.logger = logger;
        this.debugLogger = debugLogger;
    }

    // Other methods

    @Override
    public void initialize() {
        // Establishing a random connection just to check if it can connect to OpenAI
        establishConnection().disconnect();
    }

    private HttpURLConnection establishConnection() {
        try {
            URL obj = new URL(url);
            HttpURLConnection connection;
            connection = (HttpURLConnection) obj.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            return connection;
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while trying to establish a connection with OpenAI's ChatGPT. The set-up has " +
                    "failed and as such, this ChatGPTManager is disabled.", e);
        }
    }

    /**
     *  Sends a prompt to OpenAI's ChatGPT 3.5 Turbo model. Waits for the model's response and returns it
     * @param prompt
     *  The prompt to send to the model
     * @return
     *  The model's response
     */
    @Override
    public String promptSync(String prompt) {
        String body = null;

        try {
            HttpURLConnection connection = establishConnection();
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("model", model);

            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();

            message.put("role", "user");
            message.put("content", prompt);

            messages.put(message);

            jsonBody.put("messages", messages);

            // The request body
            // body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]}";

            body = jsonBody.toString();

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(body);
            writer.flush();
            writer.close();

            // Response from ChatGPT
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            StringBuilder response = new StringBuilder();

            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            br.close();

            String responseString = response.toString();

            debugLogger.log(Level.INFO, "Sent prompt to ChatGPT:\n----------\n" + body + "\n----------\n\n" +
                    "Received response:\n----------\n" + responseString + "\n----------");

            // calls the method to extract the message.
            return extractMessageFromJSONResponse(responseString);
        } catch (IOException | RuntimeException e) {
            throw new RuntimeException("An error occurred while trying to send a prompt OpenAI's ChatGPT. Body: " + body, e);
        }
    }

    /**
     *  Sends a prompt to OpenAI's ChatGPT 3.5 Turbo model. Immediately returns, and when the model's response
     * eventually arrives, the given consumer gets notified with the response. If an error occurs, the given consumer
     * never gets notified, the manager's logger receives an error message and a stack strace is printed.
     * @param prompt
     *  The prompt to send to the model
     * @param responseReceiver
     *  The coonsumer that will get notified with the eventual response.
     */
    @Override
    public void promptAsync(String prompt, Consumer<String> responseReceiver) {
        getThreadPool().execute(() -> {
            try {
                responseReceiver.accept(promptSync(prompt));
            } catch (RuntimeException e) {
                logger.severe("Error occurred while asynchronously prompting OpenAI's ChatGPT: " + e.getMessage());

                e.printStackTrace();
            }
        });
    }

    public String extractMessageFromJSONResponse(String response) {
        JSONObject jsonObject = new JSONObject(response);
        JSONArray choices = jsonObject.getJSONArray("choices");

        if (choices.isEmpty()) {
            throw new RuntimeException("ChatGPT's reply contains no content in its response:\n----" + response + "\n----");
        }

        JSONObject choice = choices.getJSONObject(0);

        if (!choice.getString("finish_reason").equals("stop")) {
            // When a finish_reason isn't 'stop', then something went wrong. Not enough to throw an exception, but enough
            // to log a warning
            logger.warning("ChatGPT's reply did not finish correctly. It's response is:\n----" + response + "\n----");
        }

        return choice.getJSONObject("message").getString("content");
    }

    @Override
    public void teardown() {

    }
}
