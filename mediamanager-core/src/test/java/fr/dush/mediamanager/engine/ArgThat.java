package fr.dush.mediamanager.engine;

import com.google.common.base.Function;
import fr.dush.mediamanager.domain.media.Assertions;
import fr.dush.mediamanager.domain.media.MediaReference;
import fr.dush.mediamanager.domain.media.MediaType;
import fr.dush.mediamanager.domain.media.Recovery;
import org.bson.types.ObjectId;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.google.common.collect.Lists.*;
import static org.mockito.Matchers.*;

/**
 * @author Thomas Duchatelle
 */
public class ArgThat {

    public static MediaReference argReference(final MediaType type, final String id) {
        return argThat(new BaseMatcher<MediaReference>() {
            @Override
            public boolean matches(Object item) {
                if (item instanceof MediaReference) {
                    MediaReference r = (MediaReference) item;
                    Assertions.assertThat(r).hasId(id).hasMediaType(type);

                    return true;
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {

            }
        });
    }

    public static Recovery argRecovery(final int position) {
        return argThat(new BaseMatcher<Recovery>() {
            @Override
            public boolean matches(Object item) {
                if (item instanceof Recovery) {
                    Recovery r = (Recovery) item;
                    List<String> medias =
                            transform(newArrayList(Paths.get("hello_CD1.avi"), Paths.get("hello_CD2.avi")),
                                      new Function<Path, String>() {
                                          @Override
                                          public String apply(Path input) {
                                              return input.toAbsolutePath().toString();
                                          }
                                      });
                    Assertions.assertThat(r)
                              .hasLength(100)
                              .hasMediaFiles(medias.toArray(new String[medias.size()]))
                              .hasPosition(position);
                    Assertions.assertThat(r.getId())
                              .hasId("5240760958eff5a9e1d18203")
                              .hasMediaType(MediaType.MOVIE);

                    return true;
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {

            }
        });
    }
}
