package cm.belrose.config;

import cm.belrose.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

//@Component
@Slf4j
public class CustomerProcessor implements ItemProcessor<Customer,Customer> {

    @Override
    public Customer process(Customer item) throws Exception {
        //Inside this method add your logic
        return item;
    }
}
