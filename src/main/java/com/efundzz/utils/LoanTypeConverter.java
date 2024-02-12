package com.efundzz.utils;

import com.efundzz.enums.TypeOfLoan;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class LoanTypeConverter implements Converter<String, TypeOfLoan> {

        @Override
        public TypeOfLoan convert(String source) {
            return TypeOfLoan.decode(source);
        }
}
