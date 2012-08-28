package com.brindysoft.necronomud;

import com.brindysoft.mud.mpi.AbstractMudUser;

public class User extends AbstractMudUser {

	private String placeTag;

	private String name;

	private int insanity;

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getInsanity() {
		return insanity;
	}

	public void setInsanity(int insanity) {
		this.insanity = insanity;
	}

	public String getPlaceTag() {
		return placeTag;
	}

	public void setPlaceTag(String placeTag) {
		this.placeTag = placeTag;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
