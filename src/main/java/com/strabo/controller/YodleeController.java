package com.strabo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strabo.YodleeService;
import com.strabo.model.LinkUserRequest;
import com.strabo.model.ResponseError;
import com.strabo.model.TokenResponse;
import com.strabo.model.UserRegistrationRequest;
import com.yodlee.api.model.account.response.AccountResponse;
import com.yodlee.api.model.provideraccounts.response.AddedProviderAccountResponse;
import com.yodlee.api.model.providers.response.ProviderResponse;
import com.yodlee.api.model.transaction.response.TransactionResponse;
import com.yodlee.api.model.user.response.UserResponse;
import com.yodlee.sdk.api.exception.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@Api(tags = "api")
public class YodleeController {

    @Autowired
    private YodleeService yodleeService;

    @GetMapping("/providers")
    @ApiOperation(value = "Get project by id", response = ProviderResponse.class)
    public ProviderResponse getProviders() throws ApiException {
        return yodleeService.getProviders();
    }

    @GetMapping("/user/{name}/account")
    @ApiOperation(value = "Get project by id", response = AccountResponse.class)
    public AccountResponse getAccounts(@PathVariable String name) throws ApiException {
        return yodleeService.getAccounts(name);
    }

    @GetMapping("/{userName}/{accountId}/transaction")
    @ApiOperation(value = "Get project by id", response = TransactionResponse.class)
    public TransactionResponse getAccountTransactions(@PathVariable String userName, @PathVariable Long accountId) throws ApiException {
        return yodleeService.getAccountTransactions(userName, accountId);
    }

    @GetMapping("/{userName}/account/transaction")
    @ApiOperation(value = "Get project by id", response = TransactionResponse.class)
    public TransactionResponse getUserTransactions(@PathVariable String userName) throws ApiException {
        return yodleeService.getUserTransactions(userName);
    }

    @GetMapping("/{userName}/access-transaction")
    @ApiOperation(value = "Get project by id", response = TokenResponse.class)
    public TokenResponse getAccessToken(@PathVariable String userName) throws ApiException {
        return yodleeService.getAccessToken(userName);
    }

    @PostMapping("/user/register")
    @ApiOperation(value = "Get project by id", response = UserResponse.class)
    public UserResponse registerUser(@RequestBody UserRegistrationRequest request) throws ApiException {
        return yodleeService.reisterUser(request);
    }

    @PostMapping("/user/link")
    @ApiOperation(value = "Get project by id", response = AddedProviderAccountResponse.class)
    public AddedProviderAccountResponse linkUser(@RequestBody LinkUserRequest request) throws ApiException {
        return yodleeService.linkUser(request);
    }

    @GetMapping("/subscribe")
    @ApiOperation(value = "Get project by id", response = String.class)
    public String subscribeForEvents() throws ApiException {
        return yodleeService.subscribeRefreshEvent();
    }




    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleParseException(Exception e) throws IOException {
        System.out.println("Exception during some process");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
    }


}
