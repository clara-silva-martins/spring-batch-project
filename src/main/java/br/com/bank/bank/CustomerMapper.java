package br.com.bank.bank;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class CustomerMapper implements FieldSetMapper<Customer> {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");

    public LocalDate convertToLocalDate(String referenceMonthString) {
        if (referenceMonthString == null || referenceMonthString.isBlank()) {
            return null; // Tratar valores nulos ou vazios
        }
        YearMonth yearMonth = YearMonth.parse(referenceMonthString, formatter);
        return yearMonth.atDay(1); // Define o primeiro dia do mês
    }


    @Override
    public Customer mapFieldSet(FieldSet fieldSet) throws BindException {
        Customer customer = new Customer();
        customer.setName(fieldSet.readString("name"));
        customer.setCpf(fieldSet.readString("cpf"));
        customer.setAgency(fieldSet.readInt("agency"));
        customer.setAccount(fieldSet.readString("account"));
        customer.setAmount(fieldSet.readDouble("amount"));
        customer.setReferenceMonth(convertToLocalDate(fieldSet.readString("referenceMonth")));
        return customer;


    }
}
