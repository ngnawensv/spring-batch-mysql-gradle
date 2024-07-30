package cm.belrose.controller;

import cm.belrose.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/customers")
    public ResponseEntity<Void> loadCustomerCsvToDatabase() throws Exception {
        customerService.loadCustomerCsvToDatabase();
        return ResponseEntity.ok().build();
    }
}
