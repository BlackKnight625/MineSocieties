package ulisboa.tecnico.minesocieties.agents.actions.socialActions;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
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
        actioner.getAgent().setItem(item, EquipmentSlot.HAND);
        actioner.getAgent().lookAt(receiver.getReferencedCharacter().getEyeLocation());
    }

    @Override
    public ActionStatus tick(SocialAgent actioner, int elapsedTicks) {
        // Giving the item after a bit
        if (elapsedTicks == 20) {
            actioner.getAgent().animate(EntityAnimation.SWING_MAIN_ARM);
            clear(actioner);

            // Dropping the item in the direction of the receiver's feet
            Location receiverFeet = receiver.getReferencedCharacter().getLocation();
            Location actionerEyeLocation = actioner.getEyeLocation();
            Item itemEntity = receiverFeet.getWorld().dropItem(actionerEyeLocation, item);
            itemEntity.setVelocity(receiverFeet.toVector().subtract(actionerEyeLocation.toVector()).normalize().multiply(1.5));

            actioner.removeItem(item);

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
