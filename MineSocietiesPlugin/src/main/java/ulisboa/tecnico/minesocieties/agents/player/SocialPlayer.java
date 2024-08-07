package ulisboa.tecnico.minesocieties.agents.player;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import ulisboa.tecnico.agents.player.IPlayerAgent;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.SocialCharacter;
import ulisboa.tecnico.minesocieties.agents.observation.ItemPickupObservation;
import ulisboa.tecnico.minesocieties.agents.observation.wrapped.SocialReceivedChatFromObservation;
import ulisboa.tecnico.minesocieties.agents.observation.wrapped.SocialWeatherChangeObservation;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenu;
import ulisboa.tecnico.minesocieties.utils.ComponentUtils;

import java.util.List;
import java.util.function.Consumer;

public class SocialPlayer extends SocialCharacter implements IPlayerAgent {

    // Private attributes

    private Player player;
    private GUIMenu currentOpenGUIMenu;
    private Consumer<List<String>> signEditAction;
    private boolean isEditingCustomSign = false;

    // Constructors

    public SocialPlayer(Player player) {
        this.player = player;
    }

    // Getters and setters

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

    public GUIMenu getCurrentOpenGUIMenu() {
        return currentOpenGUIMenu;
    }

    public void setCurrentOpenGUIMenu(GUIMenu currentOpenGUIMenu) {
        this.currentOpenGUIMenu = currentOpenGUIMenu;
    }

    public boolean hasSignEditAction() {
        return signEditAction != null;
    }

    public @Nullable Consumer<List<String>> getSignEditAction() {
        return signEditAction;
    }

    public void setSignEditAction(@Nullable Consumer<List<String>> signEditAction) {
        this.signEditAction = signEditAction;
    }

    public void setEditingCustomSign(boolean editingCustomSign) {
        isEditingCustomSign = editingCustomSign;
    }

    public boolean isEditingCustomSign() {
        return isEditingCustomSign;
    }

    public void noLongerEdittingCustomMenus() {
        isEditingCustomSign = false;
    }

    // Observation methods

    @Override
    public void observeWeatherChange(SocialWeatherChangeObservation observation) {
        // Do nothing
    }

    @Override
    public void receivedChatFrom(SocialReceivedChatFromObservation observation) {
        if (!MineSocieties.getPlugin().isChatBroadcasted()) {
            // Message isn't being broadcasted, so it must be sent to this player instead
            getPlayer().sendMessage(ComponentUtils.sendMessageToPrefix(
                    observation.getObservation().getFrom().getName(),
                    getName(),
                    observation.getObservation().getChat()));
        }
    }

    @Override
    public void observeItemPickup(ItemPickupObservation observation) {
        // Do nothing
    }

    // Other methods
}
