package com.efundzz.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum TypeOfLoan {

    PERSONAL_LOAN("Personal Loan", "PersonalLoan", "PL"),
    NEW_PERSONAL_LOAN("New Personal Loan", "NewPersonalLoan", "NPL"),
    HOME_LOAN("Home Loan", "HomeLoan", "HL"),
    LOAN_AGAINST_PROPERTY("Loan Against Property", "LoanAgainstProperty", "LP"),
    LOAN_AGAINST_MUTUAL_FUNDS("Loan Against Mutual Funds", "LoanAgainstMutualFunds", "FF"),
    CAR_LOAN("Car Loan", "CarLoan", "CL"),
    BUSINESS_LOAN("Business Loan", "BusinessLoan", "BL"),
    BUSINESSLOAN_PROPRIETORSHIP("Proprietorship", "BusinessLoan_Proprietorship", "BL"),
    BUSINESSLOAN_PARTNERSHIP("Partnership", "BusinessLoan_Partnership", "BL"),
    BUSINESSLOAN_PRIVATELIMITED("Private Limited", "BusinessLoan_PrivateLimited", "BL"),
    BUSINESSLOAN_PUBLICLIMITED("Public Limited", "BusinessLoan_PublicLimited", "BL"),
    BUSINESSLOAN_LLP("LLP/LLC", "BusinessLoan_LLP", "BL"),
    CREDIT_CARD("Credit Card", "CreditCard", "CC"),
    CASH_CREDIT_LOAN("Cash Credit", "CashCredit", "CCL"),
    TERM_LOAN("Term Loan", "TermLoan", "TL"),
    EXPORT_CREDIT("Export Credit", "ExportCredit", "EC"),
    MERCHANT_LOAN("Merchant Loan", "MerchantLoan", "MEL"),
    STARTUP_LOAN("Startup Loan", "StartupLoan", "SL"),
    STARTUPLOAN_PROPRIETORSHIP("Proprietorship", "StartupLoan_Proprietorship", "SL"),
    STARTUPLOAN_PARTNERSHIP("Partnership", "StartupLoan_Partnership", "SL"),
    STARTUPLOAN_PRIVATELIMITED("Private Limited", "StartupLoan_PrivateLimited", "SL"),
    STARTUPLOAN_LLP("LLP/LLC", "StartupLoan_LLP", "SL"),
    GOLD_LOAN("Gold Loan", "GoldLoan", "GL"),
    AUTO_LOAN("Auto Loan", "AutoLoan", "AL"),
    MEDICAL_LOAN("Medical Loan", "MedicalLoan", "ML"),
    ONE_TAP_LOAN("One Tap Loan", "OneTapLoan", "OT"),
	CREDILIO("Credilio","Credilio","CR"),

    INVOICE_DISCOUNTING("Invoice Discount","InvoiceDiscounting","ID"),
    EDUCATION_LOAN("Education Loan","EducationLoan","EL");

    private String value;
    private String id;
    private String code;

    @JsonCreator
    public static TypeOfLoan decode(final String id) {
        return Stream.of(TypeOfLoan.values()).filter(loan -> loan.id.equals(id)).findFirst().orElse(null);
    }

    public static String getCodeById(final String id) {
        return Stream.of(TypeOfLoan.values()).filter(loan -> loan.id.equals(id)).findFirst().orElse(TypeOfLoan.PERSONAL_LOAN).code;
    }
    public static String getCodeByValue(final String value) {
        return valueOf(value).getCode();
    }

}
