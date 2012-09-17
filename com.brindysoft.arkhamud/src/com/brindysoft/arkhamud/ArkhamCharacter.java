package com.brindysoft.arkhamud;

import java.util.HashSet;
import java.util.Set;

import com.brindysoft.mud.mpi.AbstractMudUser;

public class ArkhamCharacter extends AbstractMudUser {

	private int health = 5;

	private int sanity = 5;

	private int focus = 1;

	private int money;

	private int clues;

	private int buildPoints = 15;

	private Set<CommonItem> commonItems = new HashSet<CommonItem>();

	private Set<UniqueItem> uniqueItems = new HashSet<UniqueItem>();

	private Set<Skill> skills = new HashSet<Skill>();

	private Set<Spell> spells = new HashSet<Spell>();

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

	public void incMoney() {
		money++;
	}

	public void decMoney() {
		money--;
	}

	public void incClues() {
		clues++;
	}

	public void decClues() {
		clues--;
	}

	public void addSpell(Spell item) {
		spells.add(item);
	}

	public void removeSpell(Spell item) {
		spells.remove(item);
	}

	public void addSkill(Skill item) {
		skills.add(item);
	}

	public void removeSkill(Skill item) {
		skills.remove(item);
	}

	public void addCommonItem(CommonItem item) {
		commonItems.add(item);
	}

	public void removeCommonItem(CommonItem item) {
		commonItems.remove(item);
	}

	public void addUniqueItem(UniqueItem item) {
		uniqueItems.add(item);
	}

	public void removeUniqueItem(UniqueItem item) {
		uniqueItems.remove(item);
	}

	public String getItemSummary() {

		StringBuilder builder = new StringBuilder();
		if (commonItems.size() > 0) {
			builder.append(commonItems.size()).append(" common items");
		}

		if (uniqueItems.size() > 0) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(uniqueItems.size()).append(" unique items");
		}

		return builder.length() == 0 ? "None" : builder.toString();
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

	public String getSpellSummary() {
		return spells.size() == 0 ? "None" : spells.size() + " spells";
	}

	public String getSkillsSummary() {
		return skills.size() == 0 ? "None" : skills.size() + " skills";
	}

}
