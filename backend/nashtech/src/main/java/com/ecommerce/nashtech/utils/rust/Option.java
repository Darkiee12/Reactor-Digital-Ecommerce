package com.ecommerce.nashtech.utils.rust;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;

/**
 * A type that represents either success ({@code Some}) or None.
 *
 * Mimics Rust's `Option<T>` and is useful for error handling without exceptions.
 *
 * @param <T> The type of the some value.
 */
public abstract sealed class Option<T> {
    private Option() {}

    /**
     * Some value of type T.
     */
    public static final class Some<T> extends Option<T> {
        public final T value;

        public Some(T value) {
            this.value = value;
        }

        public T get() {
            return this.value;
        }
    }

    /**
     * No value.
     */
    public static final class None<T> extends Option<T> {}

    /**
     * Creates a {@code Option} containing some value.
     * @param value The value to wrap.
     * @param <T> The type of the value.
     * @return An {@code Option} containing the value.
     * @throws IllegalArgumentException if the value is null.
     */
    public static <T> Option<T> some(T value) {
        return new Some<>(value);
    }

    /**
     * Creates a {@code None} option.
     * @param <T> The type of the value.
     * @return A {@code None} option.
     */
    public static <T> Option<T> none() {
        return new None<>();
    }

    /**
     * From Java's Optional class.
     * Create a {@code Option} from a Java Optional.
     * @param optional The Java Optional to convert.
     * @param <T> The type of the value.
     * @return An {@code Option} containing the value if present, or {@code None} if not.
     */
    public static <T> Option<T> fromOptional(Optional<T> optional) {
        return optional.map(Option::some).orElseGet(None::new);
    }

    /**
     * Checks if the option is empty.
     * @return true if the option is empty, false otherwise.
     */
    public boolean isNone() {
        return this instanceof None;
    }

    /**
     * Checks if the option is not empty.
     * @return true if the option is not empty, false otherwise.
     */
    public boolean isSome() {
        return this instanceof Some;
    }

    /**
     * Gets the value if present, or throws an exception if not.
     * @return The value if present.
     * @throws NoSuchElementException if the option is empty.
     */
    public T unwrap() {
        switch(this){
            case Some<T> some -> {
                return some.get();
            }
            case None<T> none -> {
                throw new NoSuchElementException("No value present");
            }
        }
    }

    /**
     * Gets the value if present, or returns the default value if not.
     * @param defaultValue The default value to return if the option is empty.
     * @return The value if present, or the default value if not.
     */
    public T unwrapOr(T defaultValue) {
        switch(this){
            case Some<T> some -> {
                return some.get();
            }
            case None<T> none -> {
                return defaultValue;
            }
        }
    }

    /**
     * Gets the value if present, or computes a default value using a function.
     * @param defaultSupplier The function that computes the default value if the option is empty.
     * @return The value if present, or the computed default value if not.
     */
    public T unwrapOrElse(Supplier<T> defaultSupplier) {
        switch(this){
            case Some<T> some -> {
                return some.get();
            }
            case None<T> none -> {
                return defaultSupplier.get();
            }
        }
    }

    /**
     * FlatMaps the value inside the Option to a new Option using a function.
     * @param mapper The function to apply to the value.
     * @param <U> The type of the new value.
     * @return A new Option containing the mapped value.
     */
    public <U> Option<U> andThen(Function<T, Option<U>> mapper) {
        switch(this){
            case Some<T> some -> {
                return mapper.apply(some.get());
            }
            case None<T> none -> {
                return new None<>();
            }
        }
    }

    /**
     * Filters the value inside the Option using a predicate.
     * @param predicate The predicate to apply to the value.
     * @return A new Option, which is None if the value is not valid.
     */
    public Option<T> filter(Function<T, Boolean> predicate) {
        switch(this){
            case Some<T> some -> {
                return predicate.apply(some.get()) ? this : new None<>();
            }
            case None<T> none -> {
                return this;
            }
        }
    }
    /**
     * Maps the value inside the Option to a new Option using a function.
     * @param op The function to apply to the value.
     * @param <U> The type of the new value.
     * @return A new Option containing the mapped value.
     */
    public <U> Option<U> map(Function<T, U> op){
        switch(this){
            case Some<T> some -> {
                return new Some<>(op.apply(some.get()));
            }
            case None<T> none -> {
                return new None<>();
            }
        }
    }

    /**
     * 
     * Returns the provided default result (if none), or applies a function to the contained value (if any).
     * Arguments passed to {@code mapOr} are eagerly evaluated; if you are passing the result of a function call, it is recommended to use map_or_else, which is lazily evaluated.
     * @param <U>
     * @param defaultValue
     * @param op
     * @return the result of applying {@code op} to the contained value if present, or {@code defaultValue} if empty. Arguments passed to map_or are eagerly evaluated; if you are passing the result of a function call, it is recommended to use map_or_else, which is lazily evaluated.
     */
    public <U> U mapOr(U defaultValue, Function<T, U> op){
        switch(this){
            case Some<T> some -> {
                return op.apply(some.get());
            }
            case None<T> none -> {
                return defaultValue;
            }
        }
    }

    /**
     * Maps the contained value to another value by applying the provided function,
     * or computes a default value using the provided supplier if the {@code Option} is empty.
     * This method is lazily evaluated.
     *
     * @param defaultSupplier the supplier that computes the default value.
     * @param mapper          the function to apply to the contained value.
     * @param <U>             the type of the value returned by the function.
     * @return the result of applying {@code mapper} to the contained value if present,
     *         or the result of {@code defaultSupplier.get()} if empty.
     */
    public <U> U mapOrElse(Supplier<U> defaultSupplier, Function<T, U> mapper) {
        switch(this){
            case Some<T> some -> {
                return mapper.apply(some.get());
            }
            case None<T> none -> {
                return defaultSupplier.get();
            }
        }
    }

    /**
     * Converts the Option to a Result, returning the provided error value if the Option is empty.
     * @param error The error value to return if the Option is empty.
     * @param <E> The type of the error value.
     * @return A Result containing either the value or the error.
     */

    public <E> Result<T,E> okOr(E error){
        switch(this){
            case Some<T> some -> {
                return Result.ok(some.get());
            }
            case None<T> none -> {
                return Result.err(error);
            }
        }
    }
    /**
     * Converts the Option to a Result, returning the provided error value if the Option is empty.
     * @param errorSupplier The supplier that provides the error value to return if the Option is empty.
     * @param <E> The type of the error value.
     * @return A Result containing either the value or the error.
     */
    public <E> Result<T,E> okOrElse(Supplier<E> errorSupplier){
        switch(this){
            case Some<T> some -> {
                return Result.ok(some.get());
            }
            case None<T> none -> {
                return Result.err(errorSupplier.get());
            }
        }
    }
}
