package ulisboa.tecnico.minesocieties.visitors;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.inventory.ItemStack;
import ulisboa.tecnico.minesocieties.agents.location.SocialLocation;
import ulisboa.tecnico.minesocieties.agents.npc.state.*;
import ulisboa.tecnico.minesocieties.utils.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class CurrentContextExplainer implements IContextVisitor {
    @Override
    public String explainState(AgentState state) {
        StringBuilder builder = new StringBuilder();

        builder.append(state.getPersona().accept(this));
        builder.append(' ');
        builder.append(state.getPersonalities().accept(this));
        builder.append(' ');
        builder.append(state.getMoods().accept(this));
        builder.append(' ');
        builder.append(state.getInventory().accept(this));
        builder.append(' ');
        builder.append(state.getMemory().accept(this));

        return builder.toString();
    }

    @Override
    public String explainMemory(AgentMemory memory) {
        StringBuilder builder = new StringBuilder();

        builder.append(memory.getConversations().accept(this));
        builder.append(' ');
        builder.append(memory.getPastActions().accept(this));
        builder.append(' ');
        builder.append(memory.getShortTermMemory().accept(this));
        builder.append(' ');
        builder.append(memory.getLongTermMemory().accept(this));

        return builder.toString();
    }

    @Override
    public String explainPersona(AgentPersona persona) {
        return persona.getName() + " is " + persona.getAge() + " years old. They were born in " +
                StringUtils.toBirthdayString(persona.getBirthday()) + ".";
    }

    @Override
    public String explainConversations(AgentConversations conversations) {
        StringBuilder builder = new StringBuilder();

        for (Conversation conversation : conversations.getMemorySections()) {
            builder.append(conversation.getExactHowLongAgo()).append(", ");

            // Appending the speaker and listener's names
            builder.append(conversation.getSpeaker().getName()).append(" told ").append(conversation.getListener().getName());

            // Appending the conversation
            builder.append(": {");
            builder.append(conversation.getConversation());
            builder.append("}. ");
        }

        // Removing the last whitespace
        if (!builder.isEmpty()) {
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

    @Override
    public String explainLocation(AgentLocation location) {
        return location.getDescription();
    }

    @Override
    public String explainLocation(SocialLocation location) {
        return location.getName();
    }

    @Override
    public String explainMoods(AgentMoods moods) {
        var moodsCollection = moods.getStates();

        if (!moodsCollection.isEmpty()) {
            StringBuilder builder = new StringBuilder();

            builder.append("Their current emotions are: ");

            for (String mood : moodsCollection) {
                builder.append(mood);
                builder.append(", ");
            }

            // Deleting last command and whitespace
            builder.delete(builder.length() - 2, builder.length());
            builder.append(".");

            return builder.toString();
        } else {
           return "";
        }
    }

    @Override
    public String explainNotionOfEvents(AgentNotionOfEvents notionOfEvents) {
        StringBuilder builder = new StringBuilder();

        for (NotionOfEvent notion : notionOfEvents.getMemorySections()) {
            builder.append(notion.getApproximateHowLongAgo()).append(", ");
            builder.append(notion.getEventDescription());
            builder.append(". ");
        }

        // Removing the last whitespace
        if (!builder.isEmpty()) {
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

    @Override
    public String explainOpinions(AgentOpinions opinions) {
        StringBuilder builder = new StringBuilder();

        for (var opinion : opinions.getAllOpinions().entrySet()) {
            builder.append("Opinions about ");
            builder.append(opinion.getKey());
            builder.append(": ");
            builder.append(opinion.getValue().getOpinion());
            builder.append(". ");
        }

        // Removing the last whitespace
        if (!builder.isEmpty()) {
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

    @Override
    public String explainPersonalities(AgentPersonalities personalities) {
        var personalitiesCollection = personalities.getStates();

        if (!personalitiesCollection.isEmpty()) {
            StringBuilder builder = new StringBuilder();

            builder.append("Their personality consists of: ");

            for (String personality : personalitiesCollection) {
                builder.append(personality);
                builder.append(", ");
            }

            // Deleting last command and whitespace
            builder.delete(builder.length() - 2, builder.length());
            builder.append(".");

            return builder.toString();
        } else {
            return "";
        }
    }

    @Override
    public String explainReflections(AgentReflections reflections) {
        StringBuilder builder = new StringBuilder();
        var reflectionsCollection = reflections.getMemorySections();

        if (!reflectionsCollection.isEmpty()) {
            for (Reflection reflection : reflectionsCollection) {
                builder.append(reflection.getReflection());
                builder.append(". ");
            }

            builder.deleteCharAt(builder.length() - 1);

            return builder.toString();
        } else {
            return "";
        }
    }

    @Override
    public String explainShortTermMemory(AgentShortTermMemory shortTermMemory) {
        StringBuilder builder = new StringBuilder();
        var shortTermMemorySectionCollection = shortTermMemory.getMemorySections();

        if (!shortTermMemorySectionCollection.isEmpty()) {
            for (ShortTermMemorySection shortTermMemorySection : shortTermMemorySectionCollection) {
                builder.append(shortTermMemorySection.getMemorySection());
                builder.append(". ");
            }

            builder.deleteCharAt(builder.length() - 1);

            return builder.toString();
        } else {
            return "";
        }
    }

    @Override
    public String explainLongTermMemory(AgentLongTermMemory longTermMemory) {
        StringBuilder builder = new StringBuilder();
        var longTermMemorySectionCollection = longTermMemory.getMemorySections();

        if (!longTermMemorySectionCollection.isEmpty()) {
            for (LongTermMemorySection longTermMemorySection : longTermMemorySectionCollection) {
                builder.append(longTermMemorySection.getMemorySection());
                builder.append(". ");
            }

            builder.deleteCharAt(builder.length() - 1);

            return builder.toString();
        } else {
            return "";
        }
    }

    @Override
    public String explainPastActions(AgentPastActions pastActions) {
        StringBuilder builder = new StringBuilder();
        var pastActionsCollection = pastActions.getMemorySections();

        if (!pastActionsCollection.isEmpty()) {
            for (PastAction pastAction : pastActionsCollection) {
                builder.append(pastAction.getExactHowLongAgo()).append(", they ");
                builder.append(pastAction.getPastAction());
                builder.append(". ");
            }

            builder.deleteCharAt(builder.length() - 1);

            return builder.toString();
        } else {
            return "";
        }
    }

    @Override
    public String explainInventory(AgentInventory inventory) {
        // Creating a linked list since it has good removal complexity
        ItemStack[] itemsArray = inventory.getInventory();
        List<ItemStack> contents = new ArrayList<>(itemsArray.length);

        for (ItemStack item : inventory.getInventory()) {
            if (item != null) {
                contents.add(item);
            }
        }

        if (!contents.isEmpty()) {
            StringBuilder builder = new StringBuilder("They have ");

            for (ItemStack item : contents) {
                builder.append(StringUtils.itemToAmountAndName(item));
                builder.append(", ");
            }

            // Removing the last comma and whitespace
            builder.delete(builder.length() - 2, builder.length());
            builder.append(" in their inventory.");

            return builder.toString();
        } else {
            return "";
        }
    }
}
