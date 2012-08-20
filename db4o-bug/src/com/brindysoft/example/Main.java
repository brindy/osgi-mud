package com.brindysoft.example;

import java.util.UUID;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.io.MemoryStorage;

public class Main {

	public static void main(String[] args) throws Exception {
		ObjectContainer container = Db4oEmbedded.openFile(createConfiguration(), "inmemory.db4o");
		populate(container);
		Thread t = accessFromDifferentThread(container);
		t.join();
		container.close();
	}

	private static Thread accessFromDifferentThread(final ObjectContainer container) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				SampleWithHashSet sample = container.query(SampleWithHashSet.class).get(0);
				System.out.println(sample.getNames());
			}
		});
		t.start();
		return t;
	}

	private static void populate(ObjectContainer container) {

		SampleWithHashSet sample1 = new SampleWithHashSet();
		sample1.addName(UUID.randomUUID().toString());
		sample1.addName(UUID.randomUUID().toString());
		container.store(sample1);

		SampleWithHashSet sample2 = new SampleWithHashSet();
		sample2.addName(UUID.randomUUID().toString());
		container.store(sample2);

		container.commit();
	}

	private static EmbeddedConfiguration createConfiguration() {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.file().storage(new MemoryStorage());
		config.common().exceptionsOnNotStorable(true);
		return config;
	}

}
