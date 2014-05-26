package fr.dush.mediamanager.engine;

import fr.dush.mediamanager.SpringConfiguration;
import lombok.Getter;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringConfiguration.class})
public abstract class SpringJUnitTest extends SimpleJunitTest {

    static {
        System.setProperty("mediamanager.propertiesfile", "src/test/resources/dbconfig-junit.properties");
    }

    @Inject
    @Getter
    protected ApplicationContext applicationContext;

}
