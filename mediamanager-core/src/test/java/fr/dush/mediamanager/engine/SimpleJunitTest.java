package fr.dush.mediamanager.engine;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.MockitoAnnotations;
import org.slf4j.bridge.SLF4JBridgeHandler;

@RunWith(BlockJUnit4ClassRunner.class)
public abstract class SimpleJunitTest {

    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    @Before
    public void initMockito() {
        MockitoAnnotations.initMocks(this);
    }

}
