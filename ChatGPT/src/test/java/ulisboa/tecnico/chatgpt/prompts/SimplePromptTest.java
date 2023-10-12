package ulisboa.tecnico.chatgpt.prompts;

import org.junit.jupiter.api.Test;

public class SimplePromptTest extends CommonTest {

    @Test
    public void simplePromtTest() {
        String prompt = "What is the most common reply to 'Hello there!'?";
        String response = getManager().promptSync(prompt);

        System.out.println("Prompted ChatGPT with the prompt (" + prompt + "). Its response is:\n\n----" + response + "\n----");
    }
}
