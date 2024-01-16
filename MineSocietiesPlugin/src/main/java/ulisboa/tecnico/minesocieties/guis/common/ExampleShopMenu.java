package ulisboa.tecnico.minesocieties.guis.common;

import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;

public class ExampleShopMenu extends GUIMenu {

	public ExampleShopMenu(SocialPlayer shopper) {
		super(shopper, "Example shop", 27);
	}

	@Override
	public void fillShopWithClickables() {
		for(int i = 0; i < 9; i++) {
			addClickable(i, new ExampleDecoration(this));
		}
		
		addClickable(13, new ExampleMenuOpener(this));
	}
}
