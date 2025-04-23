package com.ecommerce.nashtech.shared.types;

import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import reactor.core.publisher.Mono;


/**
 * A type that represents either success (<code>Ok</code>) or failure (<code>Err</code>).
 * <p>
 * Mimics Rust's {@code Result<T, E>} and is useful for error handling without exceptions.
 * Provides methods to transform, unwrap, and extract the contained success or error values.
 * </p>
 *
 * @param <T> The type of the success value.
 * @param <E> The type of the error value.
 */
public abstract sealed class Result<T, E> permits Result.Ok, Result.Err {
    private Result() {}

    public abstract Mono<T> toMono();

    /**
     * Represents a success holding a value.
     *
     * @param <T> The type of the success value.
     * @param <E> The type of the error value.
     * @see Result
     */
    public static final class Ok<T, E> extends Result<T, E> {
        /** The contained success value. */
        public final T value;

        /**
         * Constructs an <code>Ok</code> instance with the given value.
         *
         * @param value the success value
         */
        public Ok(T value) {
            this.value = value;
        }

        /**
         * Retrieves the contained value.
         *
         * @return the success value
         */
        public T get() {
            return this.value;
        }

        @Override
        public Mono<T> toMono() {
            return Mono.just(this.value);
        }
    }

    /**
     * Represents a failure holding an error.
     *
     * @param <T> The type of the success value.
     * @param <E> The type of the error value.
     * @see Result
     */
    public static final class Err<T, E> extends Result<T, E> {
        /** The contained error value. */
        public final E error;

        /**
         * Constructs an <code>Err</code> instance with the given error.
         *
         * @param error the error value
         */
        public Err(E error) {
            this.error = error;
        }

        /**
         * Retrieves the contained error.
         *
         * @return the error value
         */
        public E get() {
            return this.error;
        }

        @Override
        public Mono<T> toMono() {
            if(Throwable.class.isAssignableFrom(this.error.getClass())){
                return Mono.error((Throwable)this.error);
            } else {
                return Mono.error(new RuntimeException(this.error.toString()));
            }
        }
    }

    /**
     * Creates a {@code Result} containing a success value.
     *
     * @param value the success value
     * @param <T>   the type of the success value
     * @param <E>   the type of the error value
     * @return a new {@code Ok} containing the value
     */
    public static <T, E> Result<T, E> ok(T value) {
        return new Ok<>(value);
    }

    /**
     * Creates a {@code Result} containing an error value.
     *
     * @param error the error value
     * @param <T>   the type of the success value
     * @param <E>   the type of the error value
     * @return a new {@code Err} containing the error
     */
    public static <T, E> Result<T, E> err(E error) {
        return new Err<>(error);
    }

    /**
     * Checks if the result is an instance of {@code Ok}.
     *
     * @return {@code true} if this is an {@code Ok}, {@code false} otherwise
     */
    public boolean isOk() {
        return this instanceof Ok<?, ?>;
    }

    /**
     * Checks if the result is an instance of {@code Err}.
     *
     * @return {@code true} if this is an {@code Err}, {@code false} otherwise
     */
    public boolean isErr() {
        return this instanceof Err<?, ?>;
    }

    /**
     * Returns the contained {@code Ok} value.
     *
     * @return the success value
     * @throws IllegalStateException if this is an {@code Err}
     */
    public T unwrap() {
        return switch (this) {
            case Ok<T, E> ok -> ok.get();
            case Err<T, E> err -> throw new IllegalStateException("Tried to unwrap an Err");
        };
    }

    /**
     * Returns the contained {@code Err} value.
     *
     * @return the error value
     * @throws IllegalStateException if this is an {@code Ok}
     */
    public E unwrapErr() {
        return switch (this) {
            case Ok<T, E> ok -> throw new IllegalStateException("Tried to unwrap an Ok");
            case Err<T, E> err -> err.get();
        };
    }

    /**
     * Returns the contained {@code Ok} value, or the provided {@code defaultValue} if this is an {@code Err}.
     * <p>
     * This method offers a simple way to obtain a value even when the {@code Result} is an {@code Err}.
     * </p>
     *
     * @param defaultValue the default value to return if this is an {@code Err}
     * @return the contained success value or {@code defaultValue} if an error is present
     */
    public T unwrapOr(T defaultValue) {
        return switch (this) {
            case Ok<T, E> ok -> ok.get();
            case Err<T, E> err -> defaultValue;
        };
    }

    /**
     * Returns the contained {@code Ok} value, or computes a replacement value from the contained error using
     * the provided function if this is an {@code Err}.
     * <p>
     * This method allows for a dynamic fallback value based on the error.
     * </p>
     *
     * @param defaultFn the function to compute a value from the error if this is an {@code Err}
     * @return the contained success value or the result of {@code defaultFn} applied to the error
     */
    public T unwrapOrElse(Function<E, T> defaultFn) {
        return switch (this) {
            case Ok<T, E> ok -> ok.get();
            case Err<T, E> err -> defaultFn.apply(err.get());
        };
    }

    /**
     * Chains another result to this one.
     * <p>
     * If this is an Ok, returns the provided result.
     * Otherwise, if this is an Err, returns the current error.
     * </p>
     *
     * @param next the result to return if this is Ok.
     * @param <U>  The type of the success value of the next result.
     * @return the provided result if this is Ok; otherwise, the current Err.
     */
    public <U> Result<U, E> and(Result<U, E> next) {
        return switch (this) {
            case Ok<T, E> ok -> next;
            case Err<T, E> err -> Result.err(err.get());
        };
    }

    /**
     * Chains an operation to the current result, executing the provided function if this is an Ok.
     * <p>
     * This method behaves similarly to flatMap in functional programming:
     * if the current result is an Ok, the provided function is applied to its value,
     * returning a new Result. If this result is an Err, then the same error is propagated.
     * </p>
     *
     * @param f the function to apply to the contained value if this is an Ok.
     * @param <U> the type of the new Ok value.
     * @return the result of applying {@code f} if this is an Ok, or the current Err otherwise.
     */
    public <U> Result<U, E> andThen(Function<T, Result<U, E>> f) {
        return switch (this) {
            case Ok<T, E> ok -> f.apply(ok.get());
            case Err<T, E> err -> Result.err(err.get());
        };
    }

    /**
     * Maps a {@code Result<T, E>} to {@code Result<U, E>} by applying a function to a contained {@code Ok} value,
     * leaving an {@code Err} value untouched.
     * <p>
     * This method is useful for composing and chaining operations on successful results.
     * </p>
     *
     * @param op  the function to apply to the success value
     * @param <U> the type of the new success value
     * @return a new {@code Result} with the mapped value or the original error
     */
    public <U> Result<U, E> map(Function<T, U> op) {
        return switch (this) {
            case Ok<T, E> ok -> Result.ok(op.apply(ok.get()));
            case Err<T, E> err -> Result.err(err.get());
        };
    }

    /**
     * Returns the result of applying {@code f} to the contained value if this is {@code Ok},
     * otherwise returns {@code defaultValue}.
     *
     * @param defaultValue the value to return if this is {@code Err}
     * @param f            the function to apply if this is {@code Ok}
     * @param <U>          the type of the value returned
     * @return the result of applying {@code f} to the contained value or {@code defaultValue}
     */
    public <U> U mapOr(U defaultValue, Function<T, U> f) {
        return switch (this) {
            case Ok<T, E> ok -> f.apply(ok.get());
            case Err<T, E> err -> defaultValue;
        };
    }

    /**
     * Maps a {@code Result<T, E>} to {@code U} by applying the function {@code f} to a contained {@code Ok} value,
     * or the fallback function {@code defaultFn} to a contained {@code Err} value.
     *
     * @param defaultFn the fallback function to apply if this is {@code Err}
     * @param f         the function to apply if this is {@code Ok}
     * @param <U>       the type of the value returned
     * @return the result of applying {@code f} or {@code defaultFn} depending on the Result type
     */
    public <U> U mapOrElse(Function<E, U> defaultFn, Function<T, U> f) {
        return switch (this) {
            case Ok<T, E> ok -> f.apply(ok.get());
            case Err<T, E> err -> defaultFn.apply(err.get());
        };
    }

    /**
     * Maps a {@code Result<T, E>} to {@code Result<T, F>} by applying a function to a contained {@code Err} value,
     * leaving an {@code Ok} value untouched.
     *
     * @param op  the function to apply to the error value
     * @param <F> the type of the new error value
     * @return a new {@code Result} with the original success value or the mapped error
     */
    public <F> Result<T, F> mapErr(Function<E, F> op) {
        return switch (this) {
            case Ok<T, E> ok -> Result.ok(ok.get());
            case Err<T, E> err -> Result.err(op.apply(err.get()));
        };
    }

    /**
     * Returns the contained {@code Ok} value or a default if this is an {@code Err}.
     * <p>
     * This consumes the {@code Result}, then returns the contained value if {@code Ok},
     * otherwise returns the value provided by the default supplier.
     * </p>
     *
     * @param defaultSupplier the supplier to get a default value if this is an {@code Err}
     * @return the contained value or the supplied default value
     */
    public T unwrapOrDefault(Supplier<T> defaultSupplier) {
        return switch (this) {
            case Ok<T, E> ok -> ok.get();
            case Err<T, E> err -> defaultSupplier.get();
        };
    }


    /**
     * Executes a supplier that might throw an exception and wraps the result in a {@code Result<T, E>}.
     * <p>
     * If the supplier executes successfully, returns {@code Ok} with the result. If a {@code Throwable} is thrown,
     * returns {@code Err} containing the exception.
     * </p>
     *
     * @param supplier the operation to execute
     * @param <T>      the type of the successful result
     * @param <E>      the type of the error (expected to be {@link Throwable} or a transformed exception)
     * @return an {@code Ok} if the supplier succeeds, or an {@code Err} if it fails
     */

    public interface ThrowingSupplier<T, E extends Exception> {
        T get() throws E;
    }

    public static <T, E extends Exception> Result<T, E> wrap(ThrowingSupplier<T, E> supplier) {
        try {
            return Result.ok(supplier.get());
        } catch (Exception ex) {
            @SuppressWarnings("unchecked")
            E e = (E) ex;
            return Result.err(e);
        }
    }

    /**
     * Creates a {@code Result} from a {@code BindingResult} typically used in Spring validation.
     * <p>
     * If the binding result contains errors, returns {@code Err} with the first {@code FieldError};
     * otherwise returns {@code Ok} with a {@code null} value.
     * </p>
     *
     * @param result the binding result to examine
     * @return {@code Err} with a field error or {@code Ok} with {@code null} if no errors are present
     */
    public static Result<Void, FieldError> fromBindingResult(BindingResult result) {
        if (result.hasErrors()) {
            return Result.err(result.getFieldError());
        } else {
            return Result.ok(null);
        }
    }

    /**
     * A functional interface similar to {@link Supplier} but which allows for checked exceptions.
     *
     * @param <T> the type of the result
     */
    @FunctionalInterface
    public interface ThrowingFunction<T, R> {
        R apply(T t) throws Exception;
    }

    
    public Option<T> ignore(){
        return switch (this) {
            case Ok<T, E> ok -> Option.some(ok.get());
            case Err<T, E> err -> Option.none();
        };
    }

    public <U> U ifOk(Function<T,U> f){
        return switch (this) {
            case Ok<T, E> ok -> f.apply(ok.get());
            case Err<T, E> err -> null;
        };
    }
}
