package ulisboa.tecnico.llms;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public abstract class LLMManager {

    // Private attributes

    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    // Getters and setters

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    // Other methods

    public abstract void initialize();

    /**
     *  Sends a prompt to the LLM. Waits for the model's response and returns it
     * @param prompt
     *  The prompt to send to the model
     * @return
     *  The model's response
     */
    public abstract String promptSync(String prompt);

    /**
     *  Sends a prompt to the LLM. Immediately returns, and when the model's response
     * eventually arrives, the given consumer gets notified with the response. If an error occurs, the given consumer
     * never gets notified, the manager's logger receives an error message and a stack strace is printed.
     * @param prompt
     *  The prompt to send to the model
     * @param responseReceiver
     *  The coonsumer that will get notified with the eventual response.
     */
    public abstract void promptAsync(String prompt, Consumer<String> responseReceiver);

    public abstract void teardown();
}
