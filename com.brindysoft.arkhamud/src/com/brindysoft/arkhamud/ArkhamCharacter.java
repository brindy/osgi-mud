package com.brindysoft.arkhamud;

import com.brindysoft.mud.mpi.AbstractMudUser;

public class ArkhamCharacter extends AbstractMudUser {

	private int health = 5;

	private int sanity = 5;

	private int focus = 1;

	private int money;

	private int clues;

	private int buildPoints = 15;

	public ArkhamCharacter(String name) {
		setName(name);
	}

	public int getHealth() {
		return health;
	}

	public void incHealth() {
		health++;
	}

	public void decHealth() {
		health--;
	}

	public int getSanity() {
		return sanity;
	}

	public void incSanity() {
		sanity++;
	}

	public void decSanity() {
		sanity--;
	}

	public int getFocus() {
		return focus;
	}

	public void incFocus() {
		focus++;
	}

	public void decFocus() {
		focus--;
	}

	public int getMoney() {
		return money;
	}

	public int getClues() {
		return clues;
	}

	public int getBuildPoints() {
		return buildPoints;
	}

	public void decBuildPoints() {
		buildPoints--;
	}

	public void incBuildPoints() {
		buildPoints++;
	}

	public String getItemSummary() {
		return "None";
	}

	public boolean isComplete() {
		return buildPoints == 0;
	}

	public void randomise() {
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArkhamCharacter other = (ArkhamCharacter) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

}
