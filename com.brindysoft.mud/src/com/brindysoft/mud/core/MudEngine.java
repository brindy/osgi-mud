package com.brindysoft.mud.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface MudEngine {

	String FACTORY = "com.brindysoft.mud.core.MudEngine";

	boolean run(InputStream inputStream, OutputStream outputStream) throws IOException;

}
