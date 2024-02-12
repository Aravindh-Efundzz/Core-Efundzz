insert into reference_data (image, is_enabled, "key", "value", ref_key1, ref_key2, ref_key3, ref_key4)
values ('icons/personal-loan-icon.svg', 'Y', 'PersonalLoan', 'Personal Loan', 'PRODUCTS', '', '', ''),
       ('icons/home-loan-icon.svg', 'N', 'HomeLoan', 'Home Loan', 'PRODUCTS', '', '', ''),
       ('icons/loan-against-property-icon.svg', 'N', 'LoanAgainstProperty', 'Loan Against Property', 'PRODUCTS', '', '', ''),
       ('icons/car-loan-icon.svg', 'N', 'CarLoan', 'Car Loan', 'PRODUCTS', '', '', ''),
       ('icons/business-loan-icon.svg', 'Y', 'BusinessLoan', 'Business Loan', 'PRODUCTS', '', '', ''),
       ('icons/cash-credit-icon.svg', 'N', 'CreditCard', 'Credit Card', 'PRODUCTS', '', '', ''),
       ('icons/export-credit-icon.svg', 'N', 'CashCredit', 'Cash Credit', 'PRODUCTS', '', '', ''),
       ('icons/term-loan-icon.svg', 'N', 'TermLoan', 'Term Loan', 'PRODUCTS', '', '', ''),
       ('icons/merchant-loan-icon.svg', 'N', 'MerchantLoan', 'Merchant Loan', 'PRODUCTS', '', '', ''),
       ('icons/export-credit-icon.svg', 'N', 'ExportCredit', 'Export Credit', 'PRODUCTS', '', '', '');

insert into reference_data (image, is_enabled, "key", "value", ref_key1, ref_key2, ref_key3, ref_key4)
values ('', 'Y', 'Salaried', 'Salaried', 'PersonalLoan', 'EMPLOYMENT_TYPE', '', ''),
       ('', 'Y', 'SelfEmployed', 'Self Employed', 'PersonalLoan', 'EMPLOYMENT_TYPE', '', '');

insert into reference_data (image, is_enabled, "key", "value", ref_key1, ref_key2, ref_key3, ref_key4)
values ('', 'Y', 'Proprietorship', 'Proprietorship', 'BusinessLoan', 'LOAN_SUB_TYPE', '', ''),
       ('', 'Y', 'Partnership', 'Partnership', 'BusinessLoan', 'LOAN_SUB_TYPE', '', ''),
       ('', 'Y', 'PrivateLimited', 'Private Limited', 'BusinessLoan', 'LOAN_SUB_TYPE', '', ''),
       ('', 'Y', 'PublicLimited', 'Public Limited', 'BusinessLoan', 'LOAN_SUB_TYPE', '', '');