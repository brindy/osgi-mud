package com.brindysoft.mud.core.mpi;

public interface MudCommand {

	/**
	 * Invoke the command. The args array contains all the entries on the
	 * commandline separated by whitepsace, including the original command.
	 * 
	 * @param args
	 *            the component parts of the command line used
	 * @param user
	 *            the user issuing the command
	 * @return true if the mud command was invoked successfully. Most commands
	 *         should return true, unless they want the system to generate a
	 *         message to the user.
	 */
	boolean invoke(String[] args, MudUser user);

	String[] getVerbs();

}
