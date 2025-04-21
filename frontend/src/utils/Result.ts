import Option  from "./Option";

type ResultType<T> = [NonNullable<T>, null] | [null, Error];
export default class Result<T> {
    private readonly result: ResultType<T>;
    private constructor(result: ResultType<T>) {
        this.result = result;
    }

    static Ok<T>(value: NonNullable<T>): Result<T> {
        return new Result<T>([value, null]);
    }

    static Err(error: Error | string): Result<never> {
        let err;
        if (typeof error === "string") {
            err = new Error(error);
        } else {
            err = error;
        }
        return new Result<never>([null, err]);
    }

    unwrap(): NonNullable<T>{
        const [value, error] = this.result;
        if (error) {
            throw error;
        }
        return value;
    }

    unwrapErr(): Error{
        const [_, error] = this.result;
        if(!error) {
            throw new Error("No error");
        }
        return error;
    }

    unWrapOr(defaultValue: T): T {
        const [value, error] = this.result;
        if (error) {
            return defaultValue;
        }
        return value;
    }


    isOk(): boolean {
        return this.result[0] !== null;
    }

    isErr(): boolean {
        return this.result[1] !== null;
    }

    map<U>(op: (value: T) => NonNullable<U>): Result<U> {
        if (this.isOk()) {
            return Result.Ok(op(this.result[0] as T));
        } else {
            return Result.Err(this.result[1] as Error);
        }
    }

    mapOr<U>(defaultValue: U, op: (value: T) => U): U {
        return this.isOk() ? op(this.result[0] as T) : defaultValue;
    }

    mapOrElse<U>(defaultFn: (err: Error) => U, op: (value: T) => U): U {
        return this.isOk() ? op(this.result[0] as T) : defaultFn(this.result[1] as Error);
    }
    
    expect(message: string): T {
        const [value, error] = this.result;
        if (error) {
            throw new Error(`${message}: ${error.message}`);
        }
        return value;
    }

    ok(): Option<T>{
        if (this.isOk()) {
            return Option.Some(this.unwrap());
        } else {
            return Option.None();
        }
    }
    
    
}
