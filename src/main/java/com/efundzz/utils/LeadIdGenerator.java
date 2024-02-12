package com.efundzz.utils;

import com.efundzz.entity.Lead;
import com.efundzz.enums.TypeOfLoan;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicReference;

public class LeadIdGenerator implements IdentifierGenerator {

    @Override
    public String generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        Lead lead = (Lead) object;
        AtomicReference<String> suffix = new AtomicReference<>("");

        try {
            session.doWork(connection -> {
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("select nextval('"+getSeqName(lead.getTypeOfLoan(), lead.getBrand())+"')");
                if(rs.next()) {
                    int seqVal = rs.getInt(1);
                    suffix.set(String.format("%06d", seqVal));
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }

         return getPrefix(lead.getTypeOfLoan(), lead.getBrand()) + suffix.get();
    }


    private String getSeqName(String loanType, String brand) {
        return "lead_id_seq-" + brand + "-" + TypeOfLoan.getCodeByValue(loanType);
    }

    private String getPrefix(String loanType, String brand) {
        return brand + "-LF-" + TypeOfLoan.getCodeByValue(loanType) + "-";
    }

}
