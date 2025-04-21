import Result from "./Result";

type OptionType<T> = NonNullable<T> | null | undefined;

export default class Option<T> {
    private readonly value: OptionType<T>;
    private constructor(value: OptionType<T>) {
        this.value = value;
    }

    static Some<T>(value: NonNullable<T>): Option<T> {
        return new Option<T>(value);
    }

    static None(): Option<never> {
        return new Option<never>(null);
    }

    isSome(): boolean {
        return this.value !== null && this.value !== undefined;
    }
    isNone(): boolean {
        return this.value === null || this.value === undefined;
    }
    unwrap(): NonNullable<T> {
        if (this.isNone()) {
            throw new Error("Called unwrap on None");
        }
        return this.value as NonNullable<T>;
    }
    unwrapOr(defaultValue: T): T {
        if (this.isNone()) {
            return defaultValue;
        }
        return this.value as T;
    }
    unwrapOrElse(defaultFn: () => T): T {
        if (this.isNone()) {
            return defaultFn();
        }
        return this.value as T;
    }
    map<U>(op: (value: T) => NonNullable<U>): Option<U> {
        if (this.isSome()) {
            return Option.Some(op(this.value as T));
        } else {
            return Option.None();
        }
    }
    mapOr<U>(defaultValue: U, op: (value: T) => U): U {
        return this.isSome() ? op(this.value as T) : defaultValue;
    }
    mapOrElse<U>(defaultFn: () => U, op: (value: T) => U): U {
        return this.isSome() ? op(this.value as T) : defaultFn();
    }
    expect(message: string): T {
        if (this.isNone()) {
            throw new Error(message);
        }
        return this.value as T;
    }
  
    flatten<U>(): Option<U> {
        if (this.isSome() && this.value instanceof Option) {
            return this.value as Option<U>;
        }
        return Option.None();
    }

    ok_or(error: Error): Result<T> {
        if (this.isSome()) {
            return Result.Ok(this.unwrap());
        } else {
            return Result.Err(error);
        }
    }
}