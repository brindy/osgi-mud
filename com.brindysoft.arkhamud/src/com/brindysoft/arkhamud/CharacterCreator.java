package com.brindysoft.arkhamud;

import java.util.Stack;

public class CharacterCreator {

	private Stack<Command> commands = new Stack<Command>();

	private ArkhamCharacter character;

	public CharacterCreator(ArkhamCharacter character) {
		this.character = character;
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
