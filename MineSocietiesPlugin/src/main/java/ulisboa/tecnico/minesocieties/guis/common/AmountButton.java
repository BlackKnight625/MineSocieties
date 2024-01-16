package ulisboa.tecnico.minesocieties.guis.common;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import ulisboa.tecnico.minesocieties.MineSocieties;

import java.util.function.Consumer;

/**
 *  Button for changing the amounts of something
 *
 * @param <T> The type of the number being manipulated. Can either be Integer or Double. Otherwise,
 *           errors will be thrown.
 */
public class AmountButton<T extends Number> extends GUIItem {

    // Private attributes

    private final Consumer<T> whenClicked;
    private final T baseDifference;
    private final T differenceMultiplier;
    private final int[] clickSpeeds;
    private final boolean incrementing;
    private long lastClickedTicks = 0;

    // Constructors

    /**
     *  Creates a new GUI Item that, when clicked, tells the given consumer that a
     * certain number has been selected (Ex: to create a button that increases an ItemStack's amount,
     * pass a consumer that affects the item's amount with the amount difference that the consumer receives)
     * @param menu
     *  The Menu this item is being placed on
     * @param material
     *  This item's material
     * @param name
     *  This item's name
     * @param whenClicked
     *  What to do when this item is clicked
     * @param baseDifference
     *  The number to pass the consumer for normal clicks
     * @param differenceMultiplier
     *  How much the base difference should be affected depending on the click speed
     * @param clickSpeeds
     *  The click speeds (ticks) that affect the base difference by multiplying it in descending order
     *  Ex: if the speeds are {40, 20, 10}, that means that if a player clicks slower than 1 time every 2 seconds (40 ticks), then the
     * number passed to the consumer is baseDifference. If a player clicks slower than 1 time every second but faster than 1 time
     * every 2 seconds, then differenceMultiplier * baseDifference. If the player clicks faster or equal than 2 times per second,
     * then differenceMultiplier * differenceMultiplier * baseDifference
     */
    public AmountButton(GUIMenu menu, Material material, String name, Consumer<T> whenClicked,
                        T baseDifference, T differenceMultiplier, int... clickSpeeds) {
        super(menu, material, name);

        if(differenceMultiplier.doubleValue() < 0) {
            throw new IllegalArgumentException("Difference must be greater or equal than 0");
        }

        this.whenClicked = whenClicked;
        this.incrementing = baseDifference.doubleValue() >= 0;
        this.baseDifference = baseDifference;
        this.differenceMultiplier = differenceMultiplier;
        this.clickSpeeds = clickSpeeds;

        String increment = (incrementing ? "increment" : "decrement");

        if(clickSpeeds.length == 0) {
            addDescription(ChatColor.BLUE, "Click to " + increment + " the amount by " + baseDifference);
        }
        else {
            addDescription(ChatColor.BLUE,
                    "Click slowly to " + increment + " by " + getBaseMultiplied(0));

            for(int i = 0; i < clickSpeeds.length; i++) {
                addDescription(ChatColor.BLUE,
                        "Click " + getTimesPerSecond(clickSpeeds[i]) + " times per second to ",
                        increment + " the amount by " + getBaseMultiplied(i + 1));
            }
        }
    }

    // Other methods

    public String getTimesPerSecond(int speed) {
        double frequency = 20.0 / speed;

        if(((int) frequency) == frequency) {
            // The division yielded a whole number
            return "" + ((int) frequency);
        }
        else {
            String frequencyString = "" + frequency;

            // Rounding to the first decimal
            return frequencyString.substring(0, frequencyString.indexOf('.') + 2);
        }
    }

    private T getBaseMultiplied(int power) {
        double result = Math.pow(differenceMultiplier.doubleValue(), power) * baseDifference.doubleValue();

        if(baseDifference instanceof Integer) {
            return (T) Integer.valueOf((int) result);
        }
        else {
            // It's a double
            return (T) Double.valueOf(result);
        }
    }

    @Override
    public void clicked(ClickType click) {
        if(clickSpeeds.length == 0) {
            whenClicked.accept(baseDifference);
        }
        else {
            // Checking at what speed this item was clicked in order to apply the correct multiplier
            int i = 0;
            long ticksDifference = MineSocieties.getPlugin().getElapsedTicks() - lastClickedTicks;

            while(clickSpeeds.length != i && clickSpeeds[i] >= ticksDifference) {
                i++;
            }

            whenClicked.accept(getBaseMultiplied(i));
        }

        lastClickedTicks = MineSocieties.getPlugin().getElapsedTicks();
    }
}
