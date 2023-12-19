package ulisboa.tecnico.minesocieties.llms;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import ulisboa.tecnico.llms.ChatGPTManager;
import ulisboa.tecnico.llms.LLMManager;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.SocialAgentManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

public class BaseLLMTest {
    // Private attributes

    private LLMManager llmManager;

    // Getters and setters

    public LLMManager getLlmManager() {
        return llmManager;
    }

    // Test methods

    @BeforeEach
    public void setup() throws URISyntaxException, IOException {

        // Reading the API key
        String apiKey = Files.readString(Path.of(getClass().getClassLoader().getResource("ChatGPT_API_Key.txt").toURI()));

        System.out.println("API key: " + apiKey);

        // gpt-3.5-turbo
        // gpt-4-1106-preview

        Logger logger = Logger.getGlobal();

        Server server = Mockito.mock(Server.class);

        Mockito.when(server.isPrimaryThread()).thenReturn(true);
        Mockito.when(server.getLogger()).thenReturn(logger);

        Bukkit.setServer(server);

        llmManager = new ChatGPTManager(apiKey, "gpt-3.5-turbo", Logger.getGlobal(), Logger.getGlobal());

        llmManager.initialize();


        MineSocieties plugin = Mockito.mock(MineSocieties.class);
        Mockito.when(plugin.getLLMManager()).thenReturn(llmManager);

        SocialAgentManager socialAgentManager = new SocialAgentManager(plugin);

        Mockito.when(plugin.getSocialAgentManager()).thenReturn(socialAgentManager);
        Mockito.when(plugin.isChatBroadcasted()).thenReturn(false);
        Mockito.when(plugin.getLogger()).thenReturn(Logger.getGlobal());

        MineSocieties.setPlugin(plugin);

        plugin.setLLMManager(llmManager);
        plugin.setSocialAgentManager(new SocialAgentManager(plugin));
    }

    @AfterEach
    public void cleanup() {
        if (llmManager != null) {
            llmManager.teardown();
        }
    }
}
