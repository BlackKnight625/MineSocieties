package ulisboa.tecnico.llms;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

    public abstract void promptAsyncSupplyPromptAsync(Supplier<String> promptSupplier, Consumer<String> responseReceiver);

    public abstract void promptAsyncSupplyPromptAsync(Supplier<String> promptSupplier, Consumer<String> responseReceiver,
                                                      @Nullable Consumer<Throwable> onError);

    /**
     *  Sends multiple messages to the LLM that are more specialized to the specific LLM type in order to
     * fine-tune it into replying with the desired format. To use this method, you must first understand
     * how to use the LLM's API with different roles, as it works very differently depending on the LLM.
     *  This method waits for the LLM's response.
     * @param messages
     *  A list of messages to be sent to the LLM
     * @return
     *  A single response that's fine-tuned to the given input
     */
    public abstract String promptSync(List<LLMMessage> messages);

    /**
     *  Sends multiple messages to the LLM that are more specialized to the specific LLM type in order to
     * fine-tune it into replying with the desired format. To use this method, you must first understand
     * how to use the LLM's API with different roles, as it works very differently depending on the LLM.
     *  This method returns immediately.
     * @param messages
     *  A list of messages to be sent to the LLM
     * @param responseReceiver
     *  The coonsumer that will get notified with the eventual response.
     */
    public abstract void promptAsync(List<LLMMessage> messages, Consumer<String> responseReceiver);

    /**
     *  Asynchronously prompts the LLM, calling the supplier's get method asynchronously right before
     * sending the full prompt to the LLM. See {@link #promptAsync(List, Consumer)} for more information.
     * @param messageSuplier
     *  The supplier that supplies the messages for the prompt
     * @param responseReceiver
     *  The consumer that will get notified with the eventual response
     */
    public abstract void promptAsyncSupplyMessageAsync(Supplier<List<LLMMessage>> messageSuplier, Consumer<String> responseReceiver);

    /**
     *  Same as {@link #promptAsyncSupplyMessageAsync(Supplier, Consumer)} with the added benefit of alerting the provided onError
     * consumer if something goes wrong with sending the prompt (gets notified after sending error messages to the loggers)
     * @param messageSuplier
     *  The supplier that supplies the messages for the prompt
     * @param responseReceiver
     *  The consumer that will get notified with the eventual response
     * @param onError
     *  The consumer that will get notified with errors if they happen
     */
    public abstract void promptAsyncSupplyMessageAsync(Supplier<List<LLMMessage>> messageSuplier, Consumer<String> responseReceiver,
                                                       @Nullable Consumer<Throwable> onError);

    public abstract void teardown();
}
