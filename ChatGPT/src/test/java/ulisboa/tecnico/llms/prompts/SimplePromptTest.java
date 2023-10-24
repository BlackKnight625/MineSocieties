package ulisboa.tecnico.llms.prompts;

import org.junit.jupiter.api.Test;

public class SimplePromptTest extends CommonTest {

    @Test
    public void simplePromtTest() {
        String prompt = "What is the most common reply to 'Hello there!'?";
        String response = getManager().promptSync(prompt);

        System.out.println("Prompted ChatGPT with the prompt (" + prompt + "). Its response is:\n----\n" + response + "\n----");
    }

    @Test
    public void memoryTest() {
        // This test shows that ChatGPT does not remember past conversations. It's ok. It's fine. Don't cry...

        String prompt1 = "When I say 'Hello There', reply with 'General Kenobi'";
        String response1 = getManager().promptSync(prompt1);

        System.out.println("Prompted ChatGPT with the prompt (" + prompt1 + "). Its response is:\n----\n" + response1 + "\n----");

        String prompt2 = "Hello there!";
        String response2 = getManager().promptSync(prompt2);

        System.out.println("Prompted ChatGPT with the prompt (" + prompt2 + "). Its response is:\n----\n" + response2 + "\n----");
    }
}
