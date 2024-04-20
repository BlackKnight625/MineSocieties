package ulisboa.tecnico.minesocieties.agents.observation;

import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import ulisboa.tecnico.agents.observation.IObservation;
import ulisboa.tecnico.minesocieties.agents.npc.state.CharacterReference;

import javax.annotation.Nullable;

public class ItemPickupObservation implements IObservation<ISocialObserver> {

    // Private attributes

    private final ItemStack itemStack;
    private final Item item;
    private final @Nullable CharacterReference thrower; // Possible character that dropped the item

    // Constructors

    public ItemPickupObservation(ItemStack itemStack, Item item,  @Nullable CharacterReference thrower) {
        this.itemStack = itemStack;
        this.item = item;
        this.thrower = thrower;
    }

    // Getters and setters

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public Item getItem() {
        return this.item;
    }

    @Nullable
    public CharacterReference getThrower() {
        return this.thrower;
    }

    // Other methods

    @Override
    public void accept(ISocialObserver observer) {
        observer.observeItemPickup(this);
    }
}
