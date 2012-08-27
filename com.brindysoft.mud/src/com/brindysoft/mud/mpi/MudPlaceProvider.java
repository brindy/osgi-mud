package com.brindysoft.mud.mpi;

public interface MudPlaceProvider {

	/**
	 * @return an array of {@link MudPlace} objects - MUST NOT be null
	 */
	MudPlace[] getPlaces();

	/**
	 * @return an array of {@link Connection} objects - MUST NOT be null
	 */
	Connection[] getConnections();

	/**
	 * Connect a {@link MudPlace} to a place with the specified tag in the
	 * direction from the given {@link MudPlace}.
	 */
	public static class Connection {

		/** The place that requires a connection. */
		public final MudPlace place;

		/** The tag of the place to connect to. */
		public final String connectedTo;

		/** The direction from the given place to the place with the tag. */
		public final String fromDirection;

		/** The direction to the given place from the place with the tag. */
		public final String toDirection;

		public Connection(MudPlace place, String connectedTo, String fromDirection, String toDirection) {
			super();
			this.place = place;
			this.connectedTo = connectedTo;
			this.fromDirection = fromDirection;
			this.toDirection = toDirection;
		}
		
	}

}
