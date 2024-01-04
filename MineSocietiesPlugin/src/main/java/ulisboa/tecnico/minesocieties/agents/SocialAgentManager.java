package ulisboa.tecnico.minesocieties.agents;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.entityutils.entity.npc.player.AnimatedPlayerNPC;
import org.jetbrains.annotations.Nullable;
import revxrsal.commands.exception.CommandErrorException;
import ulisboa.tecnico.agents.AbstractAgentManager;
import ulisboa.tecnico.agents.observation.ReceivedChatObservation;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.observation.SocialEventListener;
import ulisboa.tecnico.minesocieties.agents.observation.wrapped.SocialReceivedChatFromObservation;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.commands.SocialAgentCommand;
import ulisboa.tecnico.minesocieties.utils.ComponentUtils;

public class SocialAgentManager extends AbstractAgentManager<SocialAgent, SocialPlayer, SocialCharacter> {

    // Constructors

    public SocialAgentManager(JavaPlugin plugin) {
        super(plugin);
    }

    // Other methods

    @Override
    public void initialize() {
        new SocialEventListener(this).register();
    }

    @Override
    protected SocialAgent getNewAgentInstance(String name, Location location) {
        return new SocialAgent(new AnimatedPlayerNPC(name, location, getPlugin()));
    }

    @Override
    protected SocialPlayer getNewPlayerWrapper(Player player) {
        return new SocialPlayer(player);
    }

    public SocialAgent deployAgent(String name, Location location, @Nullable String description) throws IllegalArgumentException {
        SocialAgent agent = deployAgent(name, location);

        if (description != null && !description.isEmpty()) {
            agent.getState().insertDescriptionAsync(description);
        }

        return agent;
    }

    public void talkWith(SocialPlayer player, SocialAgent agent, String message) throws CommandErrorException {
        SocialReceivedChatFromObservation observation = new SocialReceivedChatFromObservation(
                new ReceivedChatObservation(player, message)
        );

        agent.receivedChatFrom(observation);

        if (MineSocieties.getPlugin().isChatBroadcasted()) {
            Bukkit.broadcast(ComponentUtils.sendMessageToPrefix(player.getName(), agent.getName(), message));
        }
    }
}
