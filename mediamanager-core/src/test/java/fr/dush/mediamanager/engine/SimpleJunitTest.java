package fr.dush.mediamanager.engine;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.MockitoAnnotations;

@RunWith(BlockJUnit4ClassRunner.class)
public abstract class SimpleJunitTest {

    @Before
    public void initMockito() {
        MockitoAnnotations.initMocks(this);
    }

}
