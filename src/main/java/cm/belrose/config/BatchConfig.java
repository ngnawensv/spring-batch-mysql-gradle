package cm.belrose.config;

import cm.belrose.model.Customer;
import cm.belrose.reporitory.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
//@EnableBatchProcessing // Not necessary for java17+
@Slf4j
@RequiredArgsConstructor
public class BatchConfig {

    // create Reader
    @Bean
    public FlatFileItemReader<Customer> customerReader(){
        FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new ClassPathResource("customers.csv"));
        itemReader.setName("csv-reader"); //You can give the name of your choose.
        itemReader.setLinesToSkip(1);// skip the first line of my customers.csv file because the header
        itemReader.setLineMapper(lineMapper());
        log.info("BatchConfig:customerReader::itemReader {}",itemReader);
        return itemReader;
    }

    // create Processor
    @Bean
    public CustomerProcessor customerProcessor(){
        return new CustomerProcessor();
    }


    // create Writer
    public RepositoryItemWriter<Customer> customerWriter(CustomerRepository customerRepository){
       RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save");
        log.info("BatchConfig:customerWriter::repositoryItemWriter");
       return writer;
    }

    // create Step
    @Bean
    public Step customerstep(JobRepository jobRepository,PlatformTransactionManager transactionManager,CustomerRepository customerRepository){
        log.info("BatchConfig:customerStep::customerStep-1");
        return new StepBuilder("customer-Step-1",jobRepository)
                .<Customer,Customer>chunk(10,transactionManager) // specify the number of items processed in a single transaction
                .reader(customerReader())
                .processor(customerProcessor())
                .writer(customerWriter(customerRepository))
                .build();
    }

    // create Job
    @Bean
    public Job job(JobRepository jobRepository,PlatformTransactionManager transactionManager,CustomerRepository customerRepository){
        log.info("BatchConfig:job");
        return new JobBuilder("customer-csv-job",jobRepository)
                .flow(customerstep(jobRepository,transactionManager,customerRepository))
                .end()
                .build();
    }

    //this method is used to read each line of your csv file
    private LineMapper<Customer> lineMapper() {
        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false); //This setting tells the tokenizer to not enforce line lengths when tokenizing the line
        lineTokenizer.setNames("id","firstName","lastName","email","gender","contactNo","country","dob");

        BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Customer.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }
}
