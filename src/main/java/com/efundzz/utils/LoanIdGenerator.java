package com.efundzz.utils;

import com.efundzz.entity.Loan;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicReference;

public class LoanIdGenerator implements IdentifierGenerator {

    @Override
    public String generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        Loan loan = (Loan) object;
        AtomicReference<String> suffix = new AtomicReference<>("");

        try {
            session.doWork(connection -> {
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("select nextval('"+getSeqName(loan.getLoanType())+"')");
                if(rs.next()) {
                    int seqVal = rs.getInt(1);
                    suffix.set(String.format("%06d", seqVal));
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return getPrefix(loan.getLoanType(), loan.getBrand()) + suffix.get();
    }

    private String getSeqName(String loanType) {
        if("BusinessLoan".equals(loanType)) {
            return "BL_loan_id_seq";
        }
        else return "loan_id_seq";
    }

    private String getPrefix(String loanType, String brand) {
        if("BusinessLoan".equals(loanType)) {
            return brand+"-BL-";
        }
        if("StartupLoan".equals(loanType)) {
            return brand+"-SL-";
        }
        if("HomeLoan".equals(loanType)) {
            return brand+"-HL-";
        }
        if("CreditCard".equals(loanType)) {
            return brand+"-CC-";
        }
        if("GoldLoan".equals(loanType)) {
            return brand+"-GL-";
        }
        if("AutoLoan".equals(loanType)) {
            return brand+"-AL-";
        }
        if("MedicalLoan".equals(loanType)) {
            return brand+"-ML-";
        }
        if("LoanAgainstProperty".equals(loanType)) {
            return brand+"-LP-";
        }
        if("PersonalLoan".equals(loanType)) {
            return brand+"-PL-";
        }
        if("OneTapLoan".equals(loanType)) {
            return brand+"-OT-";
        }
        if("Credilio".equals(loanType)) {
            return brand+"-CR-";
        }
        if("LoanAgainstMutualFunds".equals(loanType)) {
            return brand+"-FF-";
        }
        else return brand+"-PL-";
    }
}
