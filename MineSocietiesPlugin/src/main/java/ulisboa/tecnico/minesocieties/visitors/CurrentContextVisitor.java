package ulisboa.tecnico.minesocieties.visitors;

import ulisboa.tecnico.minesocieties.agents.npc.state.*;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class CurrentContextVisitor implements IContextVisitor {
    @Override
    public String explainState(AgentState state) {
        StringBuilder builder = new StringBuilder();

        builder.append(state.getPersona().accept(this));
        builder.append(' ');
        builder.append(state.getPersonalities().accept(this));
        builder.append(' ');
        builder.append(state.getMoods().accept(this));
        builder.append(' ');
        builder.append(state.getMemory().accept(this));

        return builder.toString();
    }

    @Override
    public String explainMemory(AgentMemory memory) {
        StringBuilder builder = new StringBuilder();

        builder.append(memory.getReflections().accept(this));
        builder.append(' ');
        builder.append(memory.getOpinions().accept(this));
        builder.append(' ');
        builder.append(memory.getNotionOfEvents().accept(this));

        return builder.toString();
    }

    @Override
    public String explainPersona(AgentPersona persona) {
        return persona.getName() + " is " + persona.getAge() + " years old. They were born in " +
                DateTimeFormatter.ISO_LOCAL_DATE.format(persona.getBirthday()) + ".";
    }

    @Override
    public String explainConversations(AgentConversations conversations) {
        Instant now = Instant.now();
        StringBuilder builder = new StringBuilder();

        for (Conversation conversation : conversations.getMemorySections()) {
            Instant longAgo = now.minus(conversation.getInstant().toEpochMilli(), ChronoUnit.MILLIS);

            builder.append(longAgo(longAgo));

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
        Instant now = Instant.now();
        StringBuilder builder = new StringBuilder();

        for (NotionOfEvent notion : notionOfEvents.getMemorySections()) {
            Instant longAgo = now.minus(notion.getInstant().toEpochMilli(), ChronoUnit.MILLIS);

            builder.append(longAgo(longAgo));
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

        for (Opinion opinion : opinions.getAllOpinions()) {
            builder.append(opinion.getOpinion());
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
            for (Reflection reflection : reflections.getMemorySections()) {
                builder.append(reflection.getReflection());
                builder.append(". ");
            }

            builder.deleteCharAt(builder.length() - 1);

            return builder.toString();
        } else {
            return "";
        }
    }

    private String longAgo(Instant longAgo) {
        StringBuilder builder = new StringBuilder();
        long secondsAgo = longAgo.getEpochSecond();
        long minutesAgo = secondsAgo / 60L;
        long hoursAgo = minutesAgo / 60L;

        // Explaining how long ago the something took place
        if (hoursAgo > 0) {
            // Happened a long time ago
            builder.append(hoursAgo).append(hoursAgo == 1 ? " hour " : " hours ").append("ago, ");
        } else if (minutesAgo > 0) {
            // Was recent
            builder.append(minutesAgo).append(minutesAgo == 1 ? " minute " : " minutes ").append("ago, ");
        } else {
            // Was very recent
            builder.append(secondsAgo).append(secondsAgo == 1 ? " second " : " seconds ").append("ago, ");
        }

        return builder.toString();
    }
}
