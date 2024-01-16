package ulisboa.tecnico.minesocieties.guis;

import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;

public class ExampleSubMenu extends GUIMenu {

	public ExampleSubMenu(SocialPlayer shopper) {
		super(shopper, "Stick shop", 9);
	}

	@Override
	public void fillShopWithClickables() {
		for(int i = 0; i < 8; i++) {
			if(i == 4) {
				addClickable(i, new ExampleAction(this));
			}
			else {
				addClickable(i, new ExampleDecoration(this));
			}
		}
		
		addClickable(8, new GoBack(this));
	}

}
