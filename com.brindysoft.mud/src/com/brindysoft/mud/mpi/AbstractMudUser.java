package com.brindysoft.mud.mpi;

import java.util.HashMap;
import java.util.Map;

import com.brindysoft.mud.api.MudSocketHandler;

public abstract class AbstractMudUser implements MudUser {

	private transient MudSocketHandler socket;

	protected Map<String, String> properties = new HashMap<String, String>();

	protected String placeTag;

	protected String name;

	@Override
	public void setSocketHandler(MudSocketHandler socket) {
	
		if (this.socket != null) {
			this.socket.close();
			this.socket = null;
		}
	
		this.socket = socket;
	}

	@Override
	public boolean isAttached() {
		return socket != null && socket.isAlive();
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void print(String message, Object... params) {
		socket.print(message, params);
	}

	@Override
	public void println(String message, Object... params) {
		socket.println(message, params);
	}

	@Override
	public String readLine() {
		return socket.readLine();
	}

	@Override
	public void setPlaceTag(String placeTag) {
		this.placeTag = placeTag;
	}

	public String getPlaceTag() {
		return placeTag;
	}

	@Override
	public String getProperty(String propertyName) {
		return properties.get(propertyName);
	}

	@Override
	public boolean getPropertyAsBoolean(String propertyName) {
		return Boolean.parseBoolean(properties.get(propertyName));
	}

	@Override
	public double getPropertyAsDouble(String propertyName) {
		if (null == properties.get(propertyName)) {
			return 0.0;
		}
		return Double.parseDouble(properties.get(propertyName));
	}

	@Override
	public float getPropertyAsFloat(String propertyName) {
		if (null == properties.get(propertyName)) {
			return 0.0f;
		}
		return Float.parseFloat(properties.get(propertyName));
	}

	@Override
	public int getPropertyAsInt(String propertyName) {
		if (null == properties.get(propertyName)) {
			return 0;
		}
		return Integer.parseInt(properties.get(propertyName));
	}

	@Override
	public long getPropertyAsLong(String propertyName) {
		if (null == properties.get(propertyName)) {
			return 0L;
		}
		return Long.parseLong(properties.get(propertyName));
	}

	@Override
	public void setProperty(String propertyName, String value) {
		properties.put(propertyName, value);
	}

	@Override
	public void setProperty(String propertyName, boolean value) {
		properties.put(propertyName, String.valueOf(value));
	}

	@Override
	public void setProperty(String propertyName, int value) {
		properties.put(propertyName, String.valueOf(value));
	}

	@Override
	public void setProperty(String propertyName, long value) {
		properties.put(propertyName, String.valueOf(value));
	}

	@Override
	public void setProperty(String propertyName, double value) {
		properties.put(propertyName, String.valueOf(value));
	}

	@Override
	public void setProperty(String propertyName, float value) {
		properties.put(propertyName, String.valueOf(value));
	}

	@Override
	public boolean hasProperty(String propertyName) {
		return properties.containsKey(propertyName);
	}

	@Override
	public void removeProperty(String propertyName) {
		properties.remove(propertyName);
	}

}
