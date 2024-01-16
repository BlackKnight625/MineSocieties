package ulisboa.tecnico.minesocieties.guis;

import org.bukkit.ChatColor;

public class GUIException extends RuntimeException {
    public GUIException(String message) {
        super(ChatColor.RED + message);
    }
}
