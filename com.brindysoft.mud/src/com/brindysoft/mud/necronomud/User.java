package com.brindysoft.mud.necronomud;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.brindysoft.mud.mpi.AbstractMudUser;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class User extends AbstractMudUser {

	private String name;

	private int insanity;

}
