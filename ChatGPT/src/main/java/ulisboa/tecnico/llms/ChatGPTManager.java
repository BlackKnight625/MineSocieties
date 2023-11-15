package ulisboa.tecnico.llms;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
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
    private long promptTokens = 0;
    private long responseTokens = 0;

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

    // Getters and setters

    public long getPromptTokens() {
        return promptTokens;
    }

    public long getResponseTokens() {
        return responseTokens;
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

    @Override
    public String promptSync(String prompt) {
        return promptSync(List.of(new LLMMessage(LLMRole.USER, prompt)));
    }

    @Override
    public String promptSync(List<LLMMessage> messageList) {
        String body = null;

        try {
            HttpURLConnection connection = establishConnection();
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("model", model);

            JSONArray messages = new JSONArray();

            for (LLMMessage llmMessage : messageList) {
                JSONObject message = new JSONObject();

                message.put("role", llmMessage.role().getChatGPTRoleName());
                message.put("content", llmMessage.message());

                messages.put(message);
            }

            jsonBody.put("messages", messages);

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

            debugLogger.info("Sent prompt to ChatGPT:\n----------\n" + body + "\n----------\n\n" +
                    "Received response:\n----------\n" + responseString + "\n----------");

            // calls the method to extract the message.
            return extractMessageFromJSONResponse(responseString);
        } catch (IOException | RuntimeException e) {
            throw new RuntimeException("An error occurred while trying to send a prompt OpenAI's ChatGPT. Body: " + body, e);
        }
    }

    @Override
    public void promptAsync(String prompt, Consumer<String> responseReceiver) {
        promptAsync(List.of(new LLMMessage(LLMRole.USER, prompt)), responseReceiver);
    }

    @Override
    public void promptAsync(List<LLMMessage> messageList, Consumer<String> responseReceiver) {
        getThreadPool().execute(() -> {
            try {
                responseReceiver.accept(promptSync(messageList));
            } catch (RuntimeException e) {
                logger.severe("Error occurred while asynchronously prompting OpenAI's ChatGPT: " + e.getMessage());

                e.printStackTrace();
            }
        });
    }

    private String extractMessageFromJSONResponse(String response) {
        JSONObject jsonObject = new JSONObject(response);
        JSONArray choices = jsonObject.getJSONArray("choices");
        JSONObject usage = jsonObject.getJSONObject("usage");

        if (choices.isEmpty()) {
            throw new RuntimeException("ChatGPT's reply contains no content in its response:\n----\n" + response + "\n----");
        }

        if (usage == null || usage.isEmpty()) {
            throw new RuntimeException("ChatGPT's reply contains no information about its usage:\n----\n" + response + "\n----");
        }

        JSONObject choice = choices.getJSONObject(0);

        if (!choice.getString("finish_reason").equals("stop")) {
            // When a finish_reason isn't 'stop', then something went wrong. Not enough to throw an exception, but enough
            // to log a warning
            logger.warning("ChatGPT's reply did not finish correctly. It's response is:\n----" + response + "\n----");
        }

        promptTokens += usage.getInt("prompt_tokens");
        responseTokens += usage.getInt("completion_tokens");

        debugLogger.info("Tokens used so far- Prompt: " + promptTokens + ". Response: " + responseTokens + ". Total: " +
                (promptTokens + responseTokens));

        return choice.getJSONObject("message").getString("content");
    }

    @Override
    public void promptAsyncSupplyPromptAsync(Supplier<String> promptSupplier, Consumer<String> responseReceiver) {
        promptAsync(List.of(new LLMMessage(LLMRole.USER, promptSupplier.get())), responseReceiver);
    }

    @Override
    public void promptAsyncSupplyMessageAsync(Supplier<List<LLMMessage>> messageSuplier, Consumer<String> responseReceiver) {
        getThreadPool().execute(() -> {
            try {
                responseReceiver.accept(promptSync(messageSuplier.get()));
            } catch (RuntimeException e) {
                logger.severe("Error occurred while asynchronously prompting OpenAI's ChatGPT: " + e.getMessage());

                e.printStackTrace();
            }
        });
    }

    @Override
    public void teardown() {

    }
}
