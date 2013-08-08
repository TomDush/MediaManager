package fr.dush.mediamanager.engine.mock;

import static com.google.common.collect.Lists.*;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Alternative;
import javax.enterprise.util.TypeLiteral;

import lombok.Getter;
import fr.dush.mediamanager.annotations.Module;

/**
 * Module annotation is here to avoid ambiguous Event's declarations.
 *
 * @author Thomas Duchatelle
 *
 */
@Alternative
@Module(id = "not-managed", name = "EventMock")
@Getter
public class EventMock<T> implements Event<T> {

	private List<T> events = newArrayList();

	@Override
	public void fire(T event) {
		events.add(event);
	}

	@Override
	public Event<T> select(Annotation... qualifiers) {
		return null;
	}

	@Override
	public <U extends T> Event<U> select(Class<U> subtype, Annotation... qualifiers) {
		return null;
	}

	@Override
	public <U extends T> Event<U> select(TypeLiteral<U> subtype, Annotation... qualifiers) {
		return null;
	}

}
