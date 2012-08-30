package com.brindysoft.mud.mpi;

import com.brindysoft.mud.api.MudIo;
import com.brindysoft.mud.api.MudSocketHandler;

public interface MudUser extends MudIo {

	void attachToSocket(MudSocketHandler socket);

	boolean isAttached();

	String getName();

	void setPlaceTag(String tag);

	String getProperty(String propertyName);

	boolean getPropertyAsBoolean(String propertyName);

	long getPropertyAsLong(String propertyName);

	int getPropertyAsInt(String propertyName);

	float getPropertyAsFloat(String propertyName);

	double getPropertyAsDouble(String propertyName);

	void removeProperty(String propertyName);

	boolean hasProperty(String propertyName);

	void setProperty(String propertyName, String value);

	void setProperty(String propertyName, boolean value);

	void setProperty(String propertyName, int value);

	void setProperty(String propertyName, long value);

	void setProperty(String propertyName, double value);

	void setProperty(String propertyName, float value);

}
