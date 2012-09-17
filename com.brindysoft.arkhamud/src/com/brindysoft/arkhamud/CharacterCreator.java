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

	private SpellService spellService;

	private SkillService skillService;

	@Reference
	public void setSkillService(SkillService skillService) {
		this.skillService = skillService;
	}

	@Reference
	public void setSpellService(SpellService spellService) {
		this.spellService = spellService;
	}

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
				execute(new BuyCommand(commonItemService.randomItem()));
				break;

			case '5':
				execute(new BuyCommand(uniqueItemService.randomItem()));
				break;

			case '6':
				execute(new BuyCommand(spellService.randomItem()));
				break;

			case '7':
				execute(new BuyCommand(skillService.randomItem()));
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
		character.println("{text:bold}{text:red}Noooooo! The world is doomed!{text}");
		character.println("");
		throw new RuntimeException("User has quit.");
	}

	private void showMenu() {
		character.println("");
		character.println("{text:u}{text:bold}{text:blue}Character Creation Menu{text}");
		// character.println("=======================");
		character.println("");
		character.println("{text:bold}{text:magenta}Character{text}: {text:bold}%s{text}", character.getName());

		// start = 5, min = 3, max = 7
		character.println("{text:bold}{text:magenta}Health{text}: {text:bold}%d{text} (min 3, max 7)",
				character.getHealth());

		// start = 5, min = 3, max = 7
		character.println("{text:bold}{text:magenta}Sanity{text}: {text:bold}%d{text} (min 3, max 7)",
				character.getSanity());

		// start = 1, min = 1, max = 3>
		character.println("{text:bold}{text:magenta}Focus{text}: {text:bold}%d{text} (min 1, max 3)",
				character.getFocus());

		character.println("{text:bold}{text:magenta}Items{text}: {text:bold}%s{text}", character.getItemSummary());
		character.println("{text:bold}{text:magenta}Spells{text}: {text:bold}%s{text}", character.getSpellsSummary());
		character.println("{text:bold}{text:magenta}Skills{text}: {text:bold}%s{text}", character.getSkillsSummary());
		character.println("{text:bold}{text:magenta}Money{text}: {text:bold}$%d{text}", character.getMoney());
		character.println("{text:bold}{text:magenta}Clues{text}: {text:bold}%d{text}", character.getClues());
		character.println("");
		character.println("Change health and sanity:");
		character.println("{text:bold}1){text} +1 health, -1 sanity");
		character.println("{text:bold}2){text} +1 sanity, -1 health");
		character.println("");
		character.println("Spend some of your %d remaining points on:", character.getBuildPoints());
		character.println("{text:bold}3){text} +1 focus = 1 point");
		character.println("{text:bold}4){text} +1 starting common item = 1 point");
		character.println("{text:bold}5){text} +1 starting unique item = 2 points");
		character.println("{text:bold}6){text} +1 starting spell = 1 point");
		character.println("{text:bold}7){text} +1 starting skill = 1 point");
		character.println("{text:bold}8){text} +1 starting money = 1 point");
		character.println("{text:bold}9){text} +1 starting clue = 1 point");
		character.println("");
		character.println("or...");
		character.println("{text:bold}U){text} undo points spend");
		character.println("{text:bold}R){text} to generate a random character (quick start)");
		character.println("{text:bold}Q){text} quit");
		character.println("");
		character.print("> ");
	}

	interface Command {
		void execute();

		void undo();
	}

	class BuyCommand implements Command {

		private Object item;

		public BuyCommand(Object item) {
			this.item = item;
		}

		@Override
		public void execute() {
			character.decBuildPoints();
			if (item instanceof CommonItem) {
				character.addCommonItem((CommonItem) item);
			} else if (item instanceof UniqueItem) {
				character.decBuildPoints();
				character.addUniqueItem((UniqueItem) item);
			}
		}

		@Override
		public void undo() {
			character.incBuildPoints();
			if (item instanceof CommonItem) {
				character.removeCommonItem((CommonItem) item);
			} else if (item instanceof UniqueItem) {
				character.incBuildPoints();
				character.removeUniqueItem((UniqueItem) item);
			}
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
