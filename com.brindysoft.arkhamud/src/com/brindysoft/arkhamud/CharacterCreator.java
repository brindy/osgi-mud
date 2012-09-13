package com.brindysoft.arkhamud;

import java.util.Map;
import java.util.Stack;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component(factory = CharacterCreator.FACTORY_NAME, provide = { CharacterCreator.class })
public class CharacterCreator {

	public static final String CHARACTER_PROPERTY = "character";

	public static final String FACTORY_NAME = "arkhamud.character.creator";

	private Stack<Command> commands = new Stack<Command>();

	private ArkhamCharacter character;

	private CommonItemService commonItemService;

	private UniqueItemService uniqueItemService;

	@Reference
	public void setUniqueItemService(UniqueItemService uniqueItemService) {
		this.uniqueItemService = uniqueItemService;
	}

	@Reference
	public void setCommonItemService(CommonItemService commonItemService) {
		this.commonItemService = commonItemService;
	}

	@Activate
	public void start(Map<String, Object> properties) {
		character = (ArkhamCharacter) properties.get(CHARACTER_PROPERTY);
	}

	public void run() {

		while (!character.isComplete()) {
			showMenu();
			String option = character.readLine();

			String trimmed = option.trim();
			if (trimmed.length() != 1) {
				character.println("Please enter a menu option.");
				continue;
			}

			switch (trimmed.toUpperCase().charAt(0)) {
			case '1':
				moreHealth();
				break;

			case '2':
				moreSanity();
				break;

			case '3':
				execute(new BuyFocusCommand());
				break;

			case '4':
				execute(new BuyCommonItemCommand());
				break;

			case '5':
				execute(new BuyUniqueItemCommand());
				break;

			case 'R':
				character.randomise();
				break;

			case 'U':
				undo();
				break;

			case 'Q':
				quit();
				break;
			}
		}

	}

	private void undo() {

		if (commands.size() > 0) {
			commands.pop().undo();
		}

	}

	private void execute(Command cmd) {
		cmd.execute();
		commands.push(cmd);
	}

	private void moreHealth() {
		if (character.getHealth() < 7) {
			character.incHealth();
			character.decSanity();
		}
	}

	private void moreSanity() {
		if (character.getSanity() < 7) {
			character.incSanity();
			character.decHealth();
		}
	}

	private void quit() {
		character.println("");
		character.println("{text:red}The world is doomed!{text}");
		throw new RuntimeException("User has quit.");
	}

	private void showMenu() {
		character.println("");
		character.println("{text:blue}Character Creation Menu{text}");
		character.println("=======================");
		character.println("");
		character.println("Character: %s", character.getName());

		// start = 5, min = 3, max = 7
		character.println("Health: %d (min 3, max 7)", character.getHealth());

		// start = 5, min = 3, max = 7
		character.println("Sanity: %d (min 3, max 7)", character.getSanity());

		// start = 1, min = 1, max = 3>
		character.println("Focus: %d (min 1, max 3)", character.getFocus());

		character.println("Items: %s ", character.getItemSummary());
		character.println("Spells: %s ", character.getSpellsSummary());
		character.println("Skills: %s ", character.getSkillsSummary());
		character.println("Money: $%d", character.getMoney());
		character.println("Clues: %d", character.getClues());
		character.println("");
		character.println("Change health and sanity:");
		character.println("1) +1 health, -1 sanity");
		character.println("2) +1 sanity, -1 health");
		character.println("");
		character.println("Spend some of your %d remaining points on:", character.getBuildPoints());
		character.println("3) +1 focus = 1 point");
		character.println("4) +1 starting common item = 1 point");
		character.println("5) +1 starting unique item = 2 points");
		character.println("6) +1 starting spell = 1 point");
		character.println("7) +1 starting skill = 1 point");
		character.println("8) +1 starting money = 1 point");
		character.println("9) +1 starting clue = 1 point");
		character.println("");
		character.println("or...");
		character.println("U) undo points spend");
		character.println("R) to generate a random character (quick start)");
		character.println("Q) quit");
		character.println("");
		character.print("> ");
	}

	interface Command {
		void execute();

		void undo();
	}

	class BuyUniqueItemCommand implements Command {

		private UniqueItem item;

		@Override
		public void execute() {
			if (character.getBuildPoints() >= 2) {
				item = uniqueItemService.randomItem();
				character.addUniqueItem(item);
				character.decBuildPoints();
				character.decBuildPoints();
			}
		}

		@Override
		public void undo() {
			if (item != null) {
				character.removeUniqueItem(item);
				character.incBuildPoints();
				character.incBuildPoints();
			}
		}

	}

	class BuyCommonItemCommand implements Command {

		private CommonItem item;

		@Override
		public void execute() {
			item = commonItemService.randomItem();
			character.addCommonItem(item);
			character.decBuildPoints();
		}

		@Override
		public void undo() {
			character.removeCommonItem(item);
			character.incBuildPoints();
		}

	}

	class BuyFocusCommand implements Command {

		private boolean spent;

		@Override
		public void execute() {
			if (character.getFocus() < 3) {
				character.incFocus();
				character.decBuildPoints();
				spent = true;
			}
		}

		@Override
		public void undo() {
			if (spent) {
				character.incBuildPoints();
				character.decFocus();
			}
		}

	}

}
