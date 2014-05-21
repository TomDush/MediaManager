package fr.dush.mediamanager.engine;

import javax.inject.Inject;

import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.dush.mediamanager.SpringConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SpringConfiguration.class })
public abstract class SpringJUnitTest extends SimpleJunitTest {

    @Inject
    protected ApplicationContext applicationContext;

}
