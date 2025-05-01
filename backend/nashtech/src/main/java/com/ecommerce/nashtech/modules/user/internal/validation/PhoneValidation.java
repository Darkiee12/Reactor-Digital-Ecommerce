package com.ecommerce.nashtech.modules.user.internal.validation;

import com.ecommerce.nashtech.modules.user.error.UserError;
import com.ecommerce.nashtech.modules.user.internal.types.RawPhoneNumber;
import com.ecommerce.nashtech.shared.types.Option;
import com.ecommerce.nashtech.shared.types.Result;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import java.util.function.Function;

import org.springframework.stereotype.Service;

@Service
public class PhoneValidation implements Function<RawPhoneNumber, Result<String, UserError>> {
    private PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    @Override
    public Result<String, UserError> apply(RawPhoneNumber phoneNumber) {
        String fullNumber = phoneNumber.toString();
        Result<PhoneNumber, NumberParseException> proto = Result.wrap(() -> phoneUtil.parse(fullNumber, null));

        switch (proto) {
            case Result.Ok<PhoneNumber, NumberParseException> ok -> {
                if (phoneUtil.isValidNumber(ok.get())) {
                    String formatted = phoneUtil.format(ok.get(), PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
                    return Result.ok(formatted);
                } else {
                    return Result.err(UserError.PhoneNumberValidationError.build(Option.some(fullNumber)));
                }
            }
            case Result.Err<PhoneNumber, NumberParseException> err -> {
                return Result.err(UserError.PhoneNumberValidationError.build(Option.some(fullNumber)));
            }
        }
    }
}
