package com.brindysoft.arkhamud;

import java.util.Map;
import java.util.Random;
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
			buildPoints++;
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

			switch (trimmed.length()) {
			case 1:
				handleOption(trimmed.toUpperCase().charAt(0));
				break;

			default:
				character.println("Please enter a menu option.");
				break;
			}
		}
		
		showCharacterInfo();
		
	}

	private void handleOption(char c) {

		switch (c) {
		case '1':
			moreHealth();
			break;

		case '2':
			moreSanity();
			break;

		// available only at character creation

		case '3':
			buyFocus();
			break;

		case '4':
			buySkill();
			break;

		// random shop

		case '5':
			buyMoney();
			break;

		case '6':
			buyCommonItem();
			break;

		case '7':
			buyUniqueItem();
			break;

		case '8':
			buySpell();
			break;

		case 'G':
			randomise(character.getName().hashCode());
			break;

		case 'R':
			randomise(System.currentTimeMillis());
			break;

		case 'U':
			undo();
			break;

		case 'Q':
			quit();
			break;
		}

	}

	private void buySpell() {
		execute(new BuyCommand(spellService.randomItem()));
	}

	private void buyUniqueItem() {
		execute(new BuyCommand(uniqueItemService.randomItem()));
	}

	private void buyCommonItem() {
		execute(new BuyCommand(commonItemService.randomItem()));
	}

	private void buyMoney() {
		execute(new BuyMoneyCommand());
	}

	private void buySkill() {
		execute(new BuyCommand(skillService.randomItem()));
	}

	private void buyFocus() {
		execute(new BuyFocusCommand());
	}

	private void showMenu() {
		character.println("{cls}");
		character.println("{text:u}{text:bold}{text:blue}Character Creation Menu{text}");
		character.println("");

		showCharacterInfo();

		character.println("");
		character.println("Change health and sanity:");
		character.println("{text:bold}1){text} +1 health, -1 sanity");
		character.println("{text:bold}2){text} +1 sanity, -1 health");
		character.println("");
		character.println("You have {text:bold}%d{text} build points remaining", getBuildPoints());
		character.println("");
		character.println("Available at character creation only:");
		character.println("{text:bold}3){text} +1 focus = 4 points");
		character.println("{text:bold}4){text} +1 skill = %d point", Skill.BUILD_COST);
		character.println("");
		character.println("Items and starting money:");
		character.println("{text:bold}5){text} +1 money = 1 point");
		character.println("{text:bold}6){text} +1 common item = %d points", CommonItem.BUILD_COST);
		character.println("{text:bold}7){text} +1 unique item = %d points", UniqueItem.BUILD_COST);
		character.println("{text:bold}8){text} +1 spell = %d point", Spell.BUILD_COST);
		character.println("");
		character.println("or...");
		character.println("{text:bold}U){text} undo points spend");
		character.println("{text:bold}G){text} to generate character for this username (quick start)");
		character.println("{text:bold}R){text} to generate a purely random character (quick start)");
		character.println("{text:bold}Q){text} quit");
		character.println("");
		character.println("or, type a keyword for help, e.g. focus");
		character.print("> ");
	}

	private void showCharacterInfo() {
		character.println("{cls}{text:bold}{text:magenta}Character{text}: {text:bold}%s{text}", character.getName());

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
	}

	private void randomise(long seed) {

		Random rnd = new Random(seed);

		if (rnd.nextBoolean()) {
			if (rnd.nextBoolean()) {
				moreHealth();
				if (rnd.nextBoolean()) {
					moreHealth();
				}
			} else {
				moreSanity();
				if (rnd.nextBoolean()) {
					moreSanity();
				}
			}
		}

		while (buildPoints > 0) {

			if (rnd.nextBoolean()) {
				buyFocus();
			}

			if (rnd.nextBoolean()) {
				buySkill();
			}

			if (rnd.nextBoolean()) {
				buyCommonItem();
			}

			if (rnd.nextBoolean()) {
				buyUniqueItem();
			}

			if (rnd.nextBoolean()) {
				buySpell();
			}

			if (rnd.nextBoolean()) {
				buyMoney();
			}

		}

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
			if (buildPoints >= 4 && character.getFocus() < 3) {
				character.incFocus();
				decBuildPoints(4);
				return true;
			}
			return false;
		}

		@Override
		public void undo() {
			incBuildPoints(4);
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

}
