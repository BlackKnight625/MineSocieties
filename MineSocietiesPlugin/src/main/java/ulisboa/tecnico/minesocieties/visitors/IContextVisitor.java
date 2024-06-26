package ulisboa.tecnico.minesocieties.visitors;

import ulisboa.tecnico.minesocieties.agents.location.SocialLocation;
import ulisboa.tecnico.minesocieties.agents.npc.state.*;

public interface IContextVisitor {

    String explainState(AgentState state);

    String explainMemory(AgentMemory memory);

    String explainPersona(AgentPersona persona);

    String explainConversations(AgentConversations conversations);

    String explainLocation(AgentLocation location);

    String explainLocation(SocialLocation location);

    String explainMoods(AgentMoods moods);

    String explainNotionOfEvents(AgentNotionOfEvents notionOfEvents);

    String explainOpinions(AgentOpinions opinions);

    String explainPersonalities(AgentPersonalities personalities);

    String explainReflections(AgentReflections reflections);

    String explainShortTermMemory(AgentShortTermMemory shortTermMemory);

    String explainLongTermMemory(AgentLongTermMemory longTermMemory);

    String explainPastActions(AgentPastActions pastActions);

    String explainInventory(AgentInventory inventory);
}
