package ulisboa.tecnico.minesocieties.visitors;

import ulisboa.tecnico.minesocieties.agents.SocialCharacter;
import ulisboa.tecnico.minesocieties.agents.npc.state.CharacterReference;
import ulisboa.tecnico.minesocieties.agents.observation.ISocialObserver;
import ulisboa.tecnico.minesocieties.agents.observation.ItemPickupObservation;
import ulisboa.tecnico.minesocieties.agents.observation.wrapped.SocialReceivedChatFromObservation;
import ulisboa.tecnico.minesocieties.agents.observation.wrapped.SocialWeatherChangeObservation;
import ulisboa.tecnico.minesocieties.utils.StringUtils;

/**
 *  This class allows the explanation, in natural language, of observations that can be made by social agents.
 *
 *  Since the IObserver interface's methods return void, the getLastExplanation() method should be called right after
 *  an explanation is requested from this Explainer.
 */
public class ObservationExplainerVisitor implements ISocialObserver {

    // Private attributes

    private String lastExplanation = "";

    // Getters and setters

    public String getLastExplanation() {
        return lastExplanation;
    }

    // Visitor methods

    @Override
    public void observeWeatherChange(SocialWeatherChangeObservation observation) {
        switch (observation.getObservation().getWeatherType()) {
            case DOWNFALL -> {
                lastExplanation = "Rain started to fall";
            }
            case CLEAR -> {
                lastExplanation = "It's no longer raining";
            }
        }
    }

    @Override
    public void receivedChatFrom(SocialReceivedChatFromObservation observation) {
        lastExplanation = observation.getObservation().getFrom().getName() + " said {" + observation.getObservation().getChat() + "}";
    }

    @Override
    public void observeItemPickup(ItemPickupObservation observation) {
        lastExplanation = "They picked up " + StringUtils.itemToAmountAndName(observation.getItemStack());

        CharacterReference whoDroppedReference = observation.getThrower();

        if (whoDroppedReference != null) {
            SocialCharacter whoDropped = whoDroppedReference.getReferencedCharacter();

            // The character that dropped this item might have been removed
            if (whoDropped != null) {
                lastExplanation += " that was dropped by " + whoDropped.getName();
            }
        }
    }
}
