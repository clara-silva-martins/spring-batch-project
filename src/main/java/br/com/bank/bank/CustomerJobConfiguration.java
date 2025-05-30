package br.com.bank.bank;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;


@Configuration
public class CustomerJobConfiguration {

    @Autowired
    private final PlatformTransactionManager transactionManager;


    public CustomerJobConfiguration(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job job(Step firstStep, JobRepository jobRepository) {
        return new JobBuilder("payments", jobRepository)
                .start(firstStep)

                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step firstStep(ItemReader <Customer> reader, ItemWriter<Customer> writer, JobRepository jobRepository) {
        return new StepBuilder("file-reading", jobRepository)
                .<Customer, Customer>chunk(200, transactionManager)
                .reader(reader)
                .processor(processor())
                .writer(writer)
                .build();

    }

    @Bean
    public ItemReader<Customer> reader(){
        return new FlatFileItemReaderBuilder<Customer>()
                .name("reading-csv")
                .resource(new FileSystemResource("files/dados_ficticios.csv"))
                .delimited()
                .delimiter("|")
                .names("name", "cpf", "agency", "account", "amount", "referenceMonth")
                .fieldSetMapper(new CustomerMapper())
                .linesToSkip(1)
                .build();

    }

    @Bean
    public ItemWriter<Customer> writer(DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<Customer>()
                .dataSource(dataSource)
                .sql("""
                        INSERT INTO customer (name, cpf, agency, account, amount, reference_month, amount_minimum_salaries) VALUES (:name, :cpf, :agency, :account, :amount, :referenceMonth, :amountMinimumSalaries)
                        
                        """)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();


    }

    @Bean
    public CustomerProcessor processor(){
        return new CustomerProcessor();
    }

}
