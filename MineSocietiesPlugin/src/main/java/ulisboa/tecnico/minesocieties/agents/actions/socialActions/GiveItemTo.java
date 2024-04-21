package ulisboa.tecnico.minesocieties.agents.actions.socialActions;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.entityutils.entity.npc.EntityAnimation;
import ulisboa.tecnico.agents.actions.ActionStatus;
import ulisboa.tecnico.agents.actions.TemporalAction;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.actions.IActionWithArguments;
import ulisboa.tecnico.minesocieties.agents.actions.ISocialAction;
import ulisboa.tecnico.minesocieties.agents.actions.exceptions.MalformedActionArgumentsException;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.CharacterReference;
import ulisboa.tecnico.minesocieties.visitors.IActionArgumentsExplainerVisitor;
import ulisboa.tecnico.minesocieties.visitors.IActionVisitor;

public class GiveItemTo extends TemporalAction<SocialAgent> implements IActionWithArguments, ISocialAction, INearbyInteraction {

    // Private attributes

    private ItemStack item;
    private CharacterReference receiver;

    // Getters and setters

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public CharacterReference getReceiver() {
        return receiver;
    }

    public void setReceiver(CharacterReference receiver) {
        this.receiver = receiver;
    }

    // Other methods


    @Override
    public void start(SocialAgent actioner) {
        int amountInInventory = actioner.getAmountOfItemInInventory(item);

        if (amountInInventory != 0) {
            actioner.getAgent().setItem(item, EquipmentSlot.HAND);
            actioner.getAgent().lookAt(receiver.getReferencedCharacter().getEyeLocation());
        }
    }

    @Override
    public ActionStatus tick(SocialAgent actioner, int elapsedTicks) {
        // Giving the item after a bit
        if (elapsedTicks == 20) {
            int amountInInventory = actioner.getAmountOfItemInInventory(item);

            if (amountInInventory == 0) {
                // The Agent no longer has the item in their inventory. It's whatever, so SUCCESS is returned.
                return ActionStatus.SUCCESS;
            }

            ItemStack itemToDrop = item.clone();
            // If the agent has 6 items and 5 are to be dropped, 5 will be dropped.
            // If the agent has 6 items and 7 are to be dropped, 6 will be dropped.
            itemToDrop.setAmount(Math.min(amountInInventory, item.getAmount()));

            actioner.getAgent().animate(EntityAnimation.SWING_MAIN_ARM);
            clear(actioner);

            // Dropping the item in the direction of the receiver's feet
            Location receiverFeet = receiver.getReferencedCharacter().getLocation();
            Location actionerEyeLocation = actioner.getEyeLocation();
            Item itemEntity = receiverFeet.getWorld().dropItem(actionerEyeLocation, itemToDrop);
            Vector velocity = receiverFeet.toVector().subtract(actionerEyeLocation.toVector()).normalize().multiply(0.5);

            // Giving velocity after a tick
            new BukkitRunnable() {
                @Override
                public void run() {
                    itemEntity.setVelocity(velocity);
                }
            }.runTask(MineSocieties.getPlugin());

            actioner.removeItem(itemToDrop);

            MineSocieties.getPlugin().getSocialAgentManager().characterDroppedItem(itemEntity, actioner);

            return ActionStatus.SUCCESS;
        }

        return ActionStatus.IN_PROGRESS;
    }

    @Override
    public void acceptArgumentsInterpreter(IActionArgumentsExplainerVisitor visitor, String arguments) throws MalformedActionArgumentsException {
        visitor.setArgumentsOfGiveItemTo(this, arguments);
    }

    @Override
    public String acceptArgumentsExplainer(IActionArgumentsExplainerVisitor visitor, SocialAgent actioner) {
        return visitor.explainGiveItemTo(this, actioner);
    }

    @Override
    public String accept(IActionVisitor visitor) {
        return visitor.visitGiveItemTo(this);
    }

    @Override
    public boolean canBeExecuted(SocialAgent actioner) {
        // Agent can give an item as long as they have items in their inventory and there are people to give the item to nearby
        return actioner.getState().getInventory().hasItems() && !getNamesOfNearbyCharacters(actioner).isEmpty();
    }

    public void clear(SocialAgent actioner) {
        actioner.getAgent().setItem(null, EquipmentSlot.HAND);
    }

    @Override
    public void cancel(SocialAgent actioner) {
        clear(actioner);
    }
}
