package br.com.bank.bank;

import org.springframework.batch.item.ItemProcessor;

public class CustomerProcessor implements ItemProcessor<Customer, Customer> {

    private static final double minimumWage = 1518.0 ;

    @Override
    public Customer process(Customer customer) throws Exception {


        double salary = customer.getAmount();

        Integer amountMinimumSalaries= calculateSalaries(salary);

        customer.setAmountMinimumSalaries(amountMinimumSalaries);

        return customer;
    }

    private Integer calculateSalaries(double salary) {
        if (salary < minimumWage) {
            return 0;
        }
        return (int) Math.floor(salary / minimumWage);
    }

//    private int calculateSalaries(double salary) {
//        if (salary < minimumWage) {
//            return 0;
//        } else if (salary >= minimumWage && salary < 2 * minimumWage) {
//            return 1;
//        } else if (salary >= 2 * minimumWage && salary < 3 * minimumWage) {
//            return 2;
//        } else if (salary >= 3 * minimumWage && salary < 4 * minimumWage) {
//            return 3;
//        }
//        return 0;
//    }
}
