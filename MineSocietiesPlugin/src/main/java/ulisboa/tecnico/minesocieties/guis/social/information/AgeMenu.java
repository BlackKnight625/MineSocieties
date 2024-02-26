package ulisboa.tecnico.minesocieties.guis.social.information;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.*;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

public class AgeMenu extends GUIMenu {

    // Private attributes

    private OffsetDateTime birthday;
    private final SocialAgent agent;

    // Constructors

    public AgeMenu(SocialPlayer player, SocialAgent agent) {
        super(player, "Editing the birthday", 27);

        this.agent = agent;
        this.birthday = agent.getState().getPersona().getBirthday().atOffset(ZoneOffset.UTC);
    }

    // Other methods

    @Override
    public void fillShopWithClickables() {
        // Adding the incrementers
        addClickable(2, new DaysIncrementer());
        addClickable(4, new MonthsIncrementer());
        addClickable(6, new YearsIncrementer());

        // Adding decorations in between
        addDecorations();

        // Adding the decrementers
        addClickable(20, new DaysDecrementer());
        addClickable(22, new MonthsDecrementer());
        addClickable(24, new YearsDecrementer());

        // Save and go back
        addClickable(18, new Save());
        addClickable(26, new GoBack(this));

        fillRestWithPanes(Material.YELLOW_STAINED_GLASS_PANE);

        reset();
    }

    private void addDecorations() {
        addClickable(11, new GUIDecoration(this, Material.GOLD_NUGGET,
                ChatColor.GOLD + "Day: " + ChatColor.BLUE + birthday.get(ChronoField.DAY_OF_MONTH)));
        addClickable(13, new GUIDecoration(this, Material.GOLD_INGOT,
                ChatColor.GOLD + "Month: " + ChatColor.BLUE + birthday.get(ChronoField.MONTH_OF_YEAR)));
        addClickable(15, new GUIDecoration(this, Material.GOLD_BLOCK,
                ChatColor.GOLD + "Year: " + ChatColor.BLUE + birthday.get(ChronoField.YEAR)));
    }

    private void setBirthday(OffsetDateTime birthday) {
        OffsetDateTime now = OffsetDateTime.now();
        // Checkinf if the new birthday is not in the future or too much into the past
        if (!birthday.isAfter(now) && !birthday.isBefore(now.minus(150, ChronoUnit.YEARS))) {
            this.birthday = birthday;

            // Refreshing the decorations so they show the correct information
            addDecorations();

            // Refreshing the menu
            update();
        }
    }

    // Private classes

    private class DaysIncrementer extends AmountButton<Integer> {

        // Constructors

        public DaysIncrementer() {
            super(AgeMenu.this, Material.SPECTRAL_ARROW, ChatColor.GREEN + "Click to increase the day of the month",
                    number -> {
                        setBirthday(birthday.plus(number, ChronoUnit.DAYS));

                        getPlayer().getPlayer().playSound(getPlayer().getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f * number);
                    }
                    , 1, 3, 10, 5);
        }
    }

    private class DaysDecrementer extends AmountButton<Integer> {

        // Constructors

        public DaysDecrementer() {
            super(AgeMenu.this, Material.ARROW, ChatColor.RED + "Click to decrease the day of the month",
                    number -> {
                        setBirthday(birthday.plus(number, ChronoUnit.DAYS));

                        getPlayer().getPlayer().playSound(getPlayer().getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, -0.5f * number);
                    }
                    , -1, 3, 10, 5);
        }
    }

    private class MonthsIncrementer extends AmountButton<Integer> {

        // Constructors

        public MonthsIncrementer() {
            super(AgeMenu.this, Material.SPECTRAL_ARROW, ChatColor.GREEN + "Click to increase the month of the year",
                    number -> {
                        setBirthday(birthday.plus(number, ChronoUnit.MONTHS));

                        getPlayer().getPlayer().playSound(getPlayer().getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f * number);
                    }
                    , 1, 3, 10, 5);
        }
    }

    private class MonthsDecrementer extends AmountButton<Integer> {

        // Constructors

        public MonthsDecrementer() {
            super(AgeMenu.this, Material.ARROW, ChatColor.RED + "Click to decrease the month of the year",
                    number -> {
                        setBirthday(birthday.plus(number, ChronoUnit.MONTHS));

                        getPlayer().getPlayer().playSound(getPlayer().getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, -0.5f * number);
                    }
                    , -1, 3, 10, 5);
        }
    }

    private class YearsIncrementer extends AmountButton<Integer> {

        // Constructors

        public YearsIncrementer() {
            super(AgeMenu.this, Material.SPECTRAL_ARROW, ChatColor.GREEN + "Click to increase the year of birth",
                    number -> {
                        setBirthday(birthday.plus(number, ChronoUnit.YEARS));

                        getPlayer().getPlayer().playSound(getPlayer().getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f * number);
                    }
                    , 1, 3, 10, 5);
        }
    }

    private class YearsDecrementer extends AmountButton<Integer> {

        // Constructors

        public YearsDecrementer() {
            super(AgeMenu.this, Material.ARROW, ChatColor.RED + "Click to decrease the year of birth",
                    number -> {
                        setBirthday(birthday.plus(number, ChronoUnit.YEARS));

                        getPlayer().getPlayer().playSound(getPlayer().getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, -0.5f * number);
                    }
                    , -1, 3, 10, 5);
        }
    }

    private class Save extends GUIItem {

            // Constructors

            public Save() {
                super(AgeMenu.this, Material.GREEN_WOOL, ChatColor.DARK_GREEN + "Click to save the new birthday");
            }

            // Other methods

            @Override
            public void clicked(ClickType click) {
                agent.getState().getPersona().setBirthday(birthday.toInstant());
                agent.getState().markDirty();

                getPlayer().getPlayer().playSound(getPlayer().getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);

                getMenu().getPreviousMenu().open();
            }
    }
}
