package com.geekshirt.orderservice.client;

import com.geekshirt.orderservice.config.OrderServiceConfig;
import com.geekshirt.orderservice.dto.AccountDto;
import com.geekshirt.orderservice.dto.AddressDto;
import com.geekshirt.orderservice.dto.CreditCardDto;
import com.geekshirt.orderservice.dto.CustomerDto;
import com.geekshirt.orderservice.utils.AccountStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
@Slf4j
public class CustomerServiceClient {

    private RestTemplate restTemplate;

    @Autowired private OrderServiceConfig config;

    public CustomerServiceClient(RestTemplateBuilder builder){
        restTemplate = builder.build();
    }

    public Optional<AccountDto>
    findAccountById(String accountId) {
        Optional<AccountDto> result = Optional.empty();

        try {
            result = Optional.ofNullable(restTemplate.getForObject(config.getCustomerServiceUrl() + "/{id}", AccountDto.class, accountId));
        }
        catch (HttpClientErrorException e){
            if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
                throw e;
            }
        }
        return result;
    }

    public AccountDto createDummyAccount() {
        AddressDto address = AddressDto.builder()
                .street("Bernaldes")
                .city("Captial Federal")
                .country("Argentina")
                .zipCode("1426")
                .state("America Latina").build();

        CustomerDto customer = CustomerDto.builder()
                .firstName("Leonardo")
                .lastName("Di Primo")
                .email("leo@gmail.com").build();

        CreditCardDto creditCard = CreditCardDto.builder()
                .nameOnCard("Leonardo Di Primo")
                .number("1234 1234 1234 0000")
                .type("Visa")
                .expirationMonth("11")
                .expirationYear("22")
                .ccv("123").build();

        return AccountDto.builder()
                .address(address)
                .customer(customer)
                .creditCard(creditCard)
                .status(AccountStatus.ACTIVE).build();
    }

    public AccountDto createAccount(AccountDto account) {
        return restTemplate.postForObject(config.getCustomerServiceUrl(),
                account, AccountDto.class);
    }

    //Este está basado en HTTP, trae el status, headers, etc.
    public AccountDto createAccountBody(AccountDto account) {
        ResponseEntity<AccountDto> responseAccount = restTemplate.postForEntity(config.getCustomerServiceUrl(),
                account, AccountDto.class);

        log.info("Response" + responseAccount.getHeaders());
        return responseAccount.getBody();
    }

    public void updateAccount(AccountDto account) {
        restTemplate.put(config.getCustomerServiceUrl() + "/{id}", account, account.getId());
    }

    public void deleteAccount(AccountDto account) {
        restTemplate.delete(config.getCustomerServiceUrl() + "/{id}", account.getId());
    }

}
