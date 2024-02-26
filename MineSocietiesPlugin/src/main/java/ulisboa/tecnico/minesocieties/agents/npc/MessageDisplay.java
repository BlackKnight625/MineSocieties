package ulisboa.tecnico.minesocieties.agents.npc;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ulisboa.tecnico.minesocieties.MineSocieties;

import java.util.LinkedList;

/**
 *  Represents the message displays that can appear on top of an NPC
 */
public class MessageDisplay {

    // Private attributes

    private final LinkedList<Message> currentMessages = new LinkedList<>();
    private final SocialAgent agent;
    private TextDisplay textDisplay;
    private Vector lastAgentLocation = new Vector();

    private static final int MAX_WIDTH = 200;
    private static final Vector OFFSET = new Vector(0, 2.35, 0);

    // Constructors

    public MessageDisplay(SocialAgent agent) {
        this.agent = agent;
    }

    // Getters and setters

    public TextDisplay getTextDisplay() {
        return textDisplay;
    }

    public void setTextDisplay(TextDisplay textDisplay) {
        this.textDisplay = textDisplay;
    }

    // Other methods

    public void initialize() {
        textDisplay = agent.getLocation().getWorld().spawn(agent.getLocation().add(OFFSET), TextDisplay.class);

        textDisplay.setLineWidth(MAX_WIDTH);
        textDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);
        textDisplay.setBillboard(Display.Billboard.VERTICAL);
        textDisplay.setTextOpacity();

        // Associating text display with the agent
        agent.addUuidToContainer(textDisplay.getPersistentDataContainer());
    }

    public void displayMessage(Message message) {
        currentMessages.addFirst(message); // Adding to the beginning so that the last (oldest) message is always at the bottom

        refreshText();

        // Removing this message after a while
        new BukkitRunnable() {
            @Override
            public void run() {
                removeMessage(message);
            }
        }.runTaskLater(MineSocieties.getPlugin(), message.getDurationTicks());
    }

    public void tick() {
        Vector currentAgentLocation = agent.getLocation().toVector();

        // Checking if the agent has moved
        if (!lastAgentLocation.equals(currentAgentLocation)) {
            // Updating the location of the text display
            textDisplay.teleport(agent.getLocation().add(OFFSET).add(0, getHeightOffset(), 0));

            lastAgentLocation = currentAgentLocation;
        }
    }

    private void removeMessage(Message message) {
        currentMessages.remove(message);

        refreshText();
    }

    private double getHeightOffset() {
        return textDisplay.getDisplayHeight();
    }

    private void refreshText() {
        Component mainComponent = Component.empty();

        // Gathering all the lines
        for (Message message : currentMessages) {
            for (Component component : message.getlines()) {
                mainComponent = mainComponent.append(component).append(Component.text('\n'));
            }

            // Adding one more new line to seperate different messages
            mainComponent = mainComponent.append(Component.text('\n'));
        }

        textDisplay.text(mainComponent);
    }
}
