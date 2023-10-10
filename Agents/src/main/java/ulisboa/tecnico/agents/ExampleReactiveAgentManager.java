package ulisboa.tecnico.agents;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.entityutils.entity.npc.player.AnimatedPlayerNPC;
import ulisboa.tecnico.agents.npc.ExampleReactiveAgent;
import ulisboa.tecnico.agents.player.ExampleInformativePlayerAgent;

public class ExampleReactiveAgentManager extends AbstractAgentManager<
        ExampleReactiveAgent, ExampleInformativePlayerAgent, ICharacter> {

    // Constructors

    public ExampleReactiveAgentManager(JavaPlugin plugin) {
        super(plugin);
    }

    // Other methods

    @Override
    protected ExampleReactiveAgent getNewAgentInstance(String name, Location location) {
        return new ExampleReactiveAgent(new AnimatedPlayerNPC(name, location, getPlugin()));
    }

    @Override
    protected ExampleInformativePlayerAgent getNewPlayerWrapper(Player player) {
        return new ExampleInformativePlayerAgent(player);
    }
}
