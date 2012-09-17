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

	private int buildPoints = 15;

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

	public int getBuildPoints() {
		return buildPoints;
	}

	public void decBuildPoints(int... points) {

		if (null == points || points.length == 0) {
			buildPoints--;
		} else {
			for (int i : points) {
				buildPoints -= i;
			}
		}

	}

	public void incBuildPoints(int... points) {

		if (null == points || points.length == 0) {
			buildPoints--;
		} else {
			for (int i : points) {
				buildPoints += i;
			}
		}

	}

	public void run() {

		while (buildPoints > 0) {
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

			case '8':
				execute(new BuyMoneyCommand());
				break;

			case '9':
				execute(new BuyClueCommand());
				break;

			case 'R':
				randomise();
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

	private void randomise() {
	}

	private void undo() {

		if (commands.size() > 0) {
			commands.pop().undo();
		}

	}

	private void execute(Command cmd) {
		if (cmd.execute()) {
			commands.push(cmd);
		}
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
		character.println("{text:bold}{text:magenta}Spells{text}: {text:bold}%s{text}", character.getSpellSummary());
		character.println("{text:bold}{text:magenta}Skills{text}: {text:bold}%s{text}", character.getSkillsSummary());
		character.println("{text:bold}{text:magenta}Money{text}: {text:bold}$%d{text}", character.getMoney());
		character.println("{text:bold}{text:magenta}Clues{text}: {text:bold}%d{text}", character.getClues());
		character.println("");
		character.println("Change health and sanity:");
		character.println("{text:bold}1){text} +1 health, -1 sanity");
		character.println("{text:bold}2){text} +1 sanity, -1 health");
		character.println("");
		character.println("Spend some of your %d remaining points on:", getBuildPoints());
		character.println("{text:bold}3){text} +1 focus = 1 point");
		character.println("{text:bold}4){text} +1 starting common item = %d points", CommonItem.BUILD_COST);
		character.println("{text:bold}5){text} +1 starting unique item = %d points", UniqueItem.BUILD_COST);
		character.println("{text:bold}6){text} +1 starting spell = %d point", Spell.BUILD_COST);
		character.println("{text:bold}7){text} +1 starting skill = %d point", Skill.BUILD_COST);
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
		boolean execute();

		void undo();
	}

	class BuyCommand implements Command {

		private Object item;

		public BuyCommand(Object item) {
			this.item = item;
		}

		@Override
		public boolean execute() {
			boolean bought = true;
			if (item instanceof CommonItem && getBuildPoints() >= CommonItem.BUILD_COST) {
				decBuildPoints(CommonItem.BUILD_COST);
				character.addCommonItem((CommonItem) item);
			} else if (item instanceof UniqueItem && getBuildPoints() >= UniqueItem.BUILD_COST) {
				decBuildPoints(UniqueItem.BUILD_COST);
				character.addUniqueItem((UniqueItem) item);
			} else if (item instanceof Spell && getBuildPoints() >= Spell.BUILD_COST) {
				decBuildPoints(Spell.BUILD_COST);
				character.addSpell((Spell) item);
			} else if (item instanceof Skill && getBuildPoints() >= Skill.BUILD_COST) {
				decBuildPoints(Skill.BUILD_COST);
				character.addSkill((Skill) item);
			} else {
				bought = false;
			}

			return bought;
		}

		@Override
		public void undo() {
			if (item instanceof CommonItem) {
				incBuildPoints(CommonItem.BUILD_COST);
				character.removeCommonItem((CommonItem) item);
			} else if (item instanceof UniqueItem) {
				incBuildPoints(UniqueItem.BUILD_COST);
				character.removeUniqueItem((UniqueItem) item);
			} else if (item instanceof Spell) {
				incBuildPoints(Spell.BUILD_COST);
				character.removeSpell((Spell) item);
			} else if (item instanceof Skill) {
				incBuildPoints(Skill.BUILD_COST);
				character.removeSkill((Skill) item);
			}
		}

	}

	class BuyFocusCommand implements Command {

		@Override
		public boolean execute() {
			if (character.getFocus() < 3) {
				character.incFocus();
				decBuildPoints();
				return true;
			}
			return false;
		}

		@Override
		public void undo() {
			incBuildPoints();
			character.decFocus();
		}

	}

	class BuyMoneyCommand implements Command {

		@Override
		public boolean execute() {
			decBuildPoints();
			character.incMoney();
			return true;
		}

		@Override
		public void undo() {
			incBuildPoints();
			character.decMoney();
		}

	}

	class BuyClueCommand implements Command {

		@Override
		public boolean execute() {
			decBuildPoints();
			character.incClues();
			return true;
		}

		@Override
		public void undo() {
			incBuildPoints();
			character.decClues();
		}

	}

}
