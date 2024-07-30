package cm.belrose.service.impl;

import cm.belrose.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl  implements CustomerService {

    private final JobLauncher jobLauncher;
    private final Job job;

    @Override
    public void loadCustomerCsvToDatabase() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("Start-At", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(job,jobParameters);
    }
}
