package ulisboa.tecnico.llms.prompts;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ulisboa.tecnico.llms.chatgpt.ChatGPTManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

public class CommonTest {

    // Private attributes

    private ChatGPTManager manager;

    // Getters and setters

    public ChatGPTManager getManager() {
        return manager;
    }

    // Test methods

    @BeforeEach
    public void setup() throws URISyntaxException, IOException {
        // Reading the API key
        String apiKey = Files.readString(Path.of(getClass().getClassLoader().getResource("ChatGPT_API_Key.txt").toURI()));

        System.out.println("API key: " + apiKey);

        manager = new ChatGPTManager(apiKey, "gpt-3.5-turbo", Logger.getGlobal(), Logger.getGlobal());

        manager.initialize();
    }

    @AfterEach
    public void cleanup() {
        if (manager != null) {
            manager.teardown();
        }
    }
}
