package fr.dush.mediamanager.engine.festassert.configuration;

import static java.lang.String.*;

import org.fest.assertions.api.AbstractAssert;

import fr.dush.mediamanager.dto.configuration.Field;


/**
 * {@link Field} specific assertions - Generated by CustomAssertionGenerator.
 */
public class FieldAssert extends AbstractAssert<FieldAssert, Field> {

  /**
   * Creates a new </code>{@link FieldAssert}</code> to make assertions on actual Field.
   * @param actual the Field we want to make assertions on.
   */
  public FieldAssert(Field actual) {
    super(actual, FieldAssert.class);
  }

  /**
   * An entry point for FieldAssert to follow Fest standard <code>assertThat()</code> statements.<br>
   * With a static import, one's can write directly : <code>assertThat(myField)</code> and get specific assertion with code completion.
   * @param actual the Field we want to make assertions on.
   * @return a new </code>{@link FieldAssert}</code>
   */
  public static FieldAssert assertThat(Field actual) {
    return new FieldAssert(actual);
  }

  /**
   * Verifies that the actual Field is defaultValue.
   * @return this assertion object.
   * @throws AssertionError - if the actual Field is not defaultValue.
   */
  public FieldAssert isDefaultValue() {
    // check that actual Field we want to make assertions on is not null.
    isNotNull();

    // we overrides the default error message with a more explicit one
    String errorMessage = format("Expected actual Field to be defaultValue but was not.", actual);

    // check
    if (!actual.isDefaultValue()) throw new AssertionError(errorMessage);

    // return the current assertion for method chaining
    return this;
  }

  public FieldAssert isNotDefaultValue() {
    // check that actual Field we want to make assertions on is not null.
    isNotNull();

    // we overrides the default error message with a more explicit one
    String errorMessage = format("Expected actual Field to NOT be defaultValue but was.", actual);

    // check
    if (actual.isDefaultValue()) throw new AssertionError(errorMessage);

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual Field's description is equal to the given one.
   * @param description the given description to compare the actual Field's description to.
   * @return this assertion object.
   * @throws AssertionError - if the actual Field's description is not equal to the given one.
   */
  public FieldAssert hasDescription(String description) {
    // check that actual Field we want to make assertions on is not null.
    isNotNull();

    // we overrides the default error message with a more explicit one
    String errorMessage = format("Expected Field's description to be <%s> but was <%s>", description, actual.getDescription());

    // check
    if (!actual.getDescription().equals(description)) { throw new AssertionError(errorMessage); }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual Field's key is equal to the given one.
   * @param key the given key to compare the actual Field's key to.
   * @return this assertion object.
   * @throws AssertionError - if the actual Field's key is not equal to the given one.
   */
  public FieldAssert hasKey(String key) {
    // check that actual Field we want to make assertions on is not null.
    isNotNull();

    // we overrides the default error message with a more explicit one
    String errorMessage = format("Expected Field's key to be <%s> but was <%s>", key, actual.getKey());

    // check
    if (!actual.getKey().equals(key)) { throw new AssertionError(errorMessage); }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual Field's name is equal to the given one.
   * @param name the given name to compare the actual Field's name to.
   * @return this assertion object.
   * @throws AssertionError - if the actual Field's name is not equal to the given one.
   */
  public FieldAssert hasName(String name) {
    // check that actual Field we want to make assertions on is not null.
    isNotNull();

    // we overrides the default error message with a more explicit one
    String errorMessage = format("Expected Field's name to be <%s> but was <%s>", name, actual.getName());

    // check
    if (!actual.getName().equals(name)) { throw new AssertionError(errorMessage); }

    // return the current assertion for method chaining
    return this;
  }

  /**
   * Verifies that the actual Field's value is equal to the given one.
   * @param value the given value to compare the actual Field's value to.
   * @return this assertion object.
   * @throws AssertionError - if the actual Field's value is not equal to the given one.
   */
  public FieldAssert hasValue(String value) {
    // check that actual Field we want to make assertions on is not null.
    isNotNull();

    // we overrides the default error message with a more explicit one
    String errorMessage = format("Expected Field's value to be <%s> but was <%s>", value, actual.getValue());

    // check
    if (!actual.getValue().equals(value)) { throw new AssertionError(errorMessage); }

    // return the current assertion for method chaining
    return this;
  }

}
