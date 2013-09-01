package fr.dush.mediamanager.engine;

import org.junit.runner.RunWith;

@RunWith(CdiJunitClassRunner.class)
public abstract class CdiJunitTest {

	public CdiJunitTest() {
		System.setProperty("mediamanager.propertiesfile", "src/test/resources/mainconfig.properties");
	}

}
