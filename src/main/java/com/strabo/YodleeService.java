package com.strabo;

import com.strabo.flow.UserManager;
import com.strabo.model.LinkUserRequest;
import com.strabo.model.TokenResponse;
import com.strabo.model.UserRegistrationRequest;
import com.yodlee.api.model.AbstractModelComponent;
import com.yodlee.api.model.Name;
import com.yodlee.api.model.account.Account;
import com.yodlee.api.model.account.response.AccountResponse;
import com.yodlee.api.model.configs.CreateConfigsNotificationEvent;
import com.yodlee.api.model.configs.enums.ConfigsNotificationEventType;
import com.yodlee.api.model.configs.request.CreateConfigsNotificationEventRequest;
import com.yodlee.api.model.provideraccounts.request.ProviderAccountRequest;
import com.yodlee.api.model.provideraccounts.response.AddedProviderAccountResponse;
import com.yodlee.api.model.providers.response.ProviderDetailResponse;
import com.yodlee.api.model.providers.response.ProviderResponse;
import com.yodlee.api.model.transaction.response.TransactionResponse;
import com.yodlee.api.model.user.UserRegistration;
import com.yodlee.api.model.user.enums.Locale;
import com.yodlee.api.model.user.request.UserRequest;
import com.yodlee.api.model.user.response.UserResponse;
import com.yodlee.sdk.api.*;
import com.yodlee.sdk.api.exception.ApiException;
import com.yodlee.sdk.api.util.ApiUtils;
import com.yodlee.sdk.builder.ClientCredentialAdminContextBuilder;
import com.yodlee.sdk.builder.ClientCredentialUserContextBuilder;
import com.yodlee.sdk.builder.ContextBuilderFactory;
import com.yodlee.sdk.client.*;
import com.yodlee.sdk.configuration.cobrand.ClientCredentialAdminConfiguration;
import com.yodlee.sdk.configuration.user.ClientCredentialUserConfiguration;
import com.yodlee.sdk.context.ClientCredentialAdminContext;
import com.yodlee.sdk.context.ClientCredentialUserContext;
import okhttp3.Call;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class YodleeService {

    @Value("${yodlee.APIURL}")
    public String APIURL;
    @Value("${yodlee.apiKey}")
    public String APIKEY;
    @Value("${yodlee.nodeUrl}")
    public String NODEURL;
    @Value("${yodlee.cobrandName}")
    String cobrandName;
    @Value("${yodlee.apiVersion}")
    String apiVersion;

    @Value("${yodlee.clientId}")
    String clientId;
    @Value("${yodlee.LoginName}")
    String loginName;
    @Value("${yodlee.secret}")
    String secret;
    @Value("${yodlee.callBackUrl}")
    String callBackUrl;


    public ClientCredentialAdminContext getAdminContext() throws ApiException {
        ClientCredentialAdminConfiguration clientCredentialConfiguration = new ClientCredentialAdminConfiguration();
        clientCredentialConfiguration.setClientId(clientId);
        clientCredentialConfiguration.setBasePath(APIURL);
        clientCredentialConfiguration.setReadTimeout(10000);
        clientCredentialConfiguration.setWriteTimeout(10000);
        clientCredentialConfiguration.setApiVersion(apiVersion);
        clientCredentialConfiguration.setConnectionKeepAliveDuration(10000);
        clientCredentialConfiguration.setLocale(Locale.en_US.name());
        clientCredentialConfiguration.setLoginName(loginName);
        clientCredentialConfiguration.setSecret(secret);
        clientCredentialConfiguration.setMaxIdleConnection(10000);

        ClientCredentialAdminContextBuilder clientCredentialAdminContextBuilder = ContextBuilderFactory.createClientCredentialAdminContextBuilder();
        ClientCredentialAdminContext clientCredentialAdminContext = clientCredentialAdminContextBuilder.build(clientCredentialConfiguration);

        return clientCredentialAdminContext;
    }

    public ClientCredentialUserContext getUserContext(String userLoginName) throws ApiException {
        ClientCredentialUserConfiguration clientCredentialUserConfiguration = new ClientCredentialUserConfiguration();
        clientCredentialUserConfiguration.setClientId(clientId);
        clientCredentialUserConfiguration.setBasePath(APIURL);
        clientCredentialUserConfiguration.setReadTimeout(10000);
        clientCredentialUserConfiguration.setWriteTimeout(10000);
        clientCredentialUserConfiguration.setApiVersion(apiVersion);
        clientCredentialUserConfiguration.setConnectionKeepAliveDuration(10000);
        clientCredentialUserConfiguration.setLocale(Locale.en_US.name());
        clientCredentialUserConfiguration.setLoginName(userLoginName);
        clientCredentialUserConfiguration.setSecret(secret);
        clientCredentialUserConfiguration.setMaxIdleConnection(10000);

        ClientCredentialUserContextBuilder clientCredentialUserContextBuilder =
                ContextBuilderFactory.createClientCredentialUserContextBuilder();

        ClientCredentialUserContext clientCredentialUserContext = clientCredentialUserContextBuilder.build(clientCredentialUserConfiguration);
        UserManager.getInstance().addUser(clientCredentialUserConfiguration.getLoginName(),
                clientCredentialUserContext);

        return clientCredentialUserContext;
    }

    public ProviderResponse getProviders() throws ApiException{
        ApiContext apiCallModel = new ApiContext("/providers", HttpMethod.GET, null);
        ApiClient apiClient = getAdminContext().getApiClient(null);
        Call call = apiClient.buildCall(apiCallModel, null);
        CallContext callContext = new CallContext(apiClient, call);
        ApiResponse<ProviderResponse> providerResponse = callContext.getApiClient().execute(callContext.getCall(), ProviderResponse.class);
        return providerResponse.getData();
    }

    public AccountResponse getAccounts(String name) throws ApiException{
        AccountsApi accountsApi = new AccountsApi(getUserContext(name));
        ApiResponse<AccountResponse> accounts = accountsApi.getAllAccounts(null, null, null, null, null, null);
        return accounts.getData();
    }

    public TransactionResponse getAccountTransactions(String userName, Long accountId) throws ApiException{
        ClientCredentialUserContext userContext = getUserContext(userName);
        TransactionsApi transactionsApi = new TransactionsApi(userContext);
        ApiResponse<TransactionResponse> transactions = transactionsApi.getTransactions(new Long[]{accountId}, null, null, null, null, null, null,
                null, null, null, null, null, null);

        return transactions.getData();
    }

    public TransactionResponse getUserTransactions(String userName) throws ApiException {
        ClientCredentialUserContext userContext = getUserContext(userName);
        AccountsApi accountsApi = new AccountsApi(userContext);
        ApiResponse<AccountResponse> accountsResponse = accountsApi.getAllAccounts(null, null, null, null, null, null);
        AccountResponse accounts = accountsResponse.getData();
        List<Long> accountIds = new ArrayList<>();
        for(Account account : accounts.getAccount()) {
            accountIds.add(account.getId());
        }

        TransactionsApi transactionsApi = new TransactionsApi(userContext);
        ApiResponse<TransactionResponse> transactions = transactionsApi.getTransactions(accountIds.toArray(new Long[0]), null, null, null, null, null, null,
                null, null, null, null, null, null);

        return transactions.getData();
    }



    private static ApiListener sampleApiListener() {
        return new ApiListener() {

            @Override
            public void responseUpdate(long bytesRead, long contentLength, boolean done) {
                System.out.println(String.format("responseUpdate : bytesRead = %d, contentLength = %d, done = %s",
                        bytesRead, contentLength, done));
            }

            @Override
            public void requestUpdate(long bytesWritten, long contentLength, boolean done) {
                System.out.println(String.format("requestUpdate : bytesWritten = %d, contentLength = %d, done = %s",
                        bytesWritten, contentLength, done));
            }
        };
    }


    public UserResponse reisterUser(UserRegistrationRequest request) throws ApiException{
        UserApi userApi = new UserApi(getAdminContext());

        UserRegistration userRegistration= new UserRegistration();
        userRegistration.setLoginName(request.userLoginName);
        Name name = new Name();
        // Set user values
        name.setFirst(request.userName);
        name.setLast(request.surname);
        userRegistration.setName(name);
        userRegistration.setEmail(request.mail);

        UserRequest userRequest = new UserRequest();
        userRequest.setUser(userRegistration);

        ApiResponse<UserResponse> registeredUser = userApi.registerUser(userRequest);
        UserResponse userResponse = registeredUser.getData();


        
        
        return userResponse;
    }

    public String subscribeRefreshEvent() throws ApiException{
        ClientCredentialAdminContext adminContext = getAdminContext();

        ConfigsApi configsApi = new ConfigsApi(adminContext);
        CreateConfigsNotificationEventRequest eventRequest = new CreateConfigsNotificationEventRequest();
        CreateConfigsNotificationEvent event = new CreateConfigsNotificationEvent();
        // Set callback URL to subscribe for REFRESH event
        event.setCallbackUrl(callBackUrl);
        eventRequest.setConfigsNotificationEvent(event);
        ApiResponse<AbstractModelComponent> subribeEvent =
                configsApi.createSubscriptionNotificationEvent(ConfigsNotificationEventType.REFRESH, eventRequest);

        return String.valueOf(subribeEvent.getStatusCode());
    }

    public AddedProviderAccountResponse linkUser(LinkUserRequest request) throws ApiException {
        ClientCredentialUserContext userContext = getUserContext(request.userLoginName);

        ProvidersApi providersApi = new ProvidersApi(userContext);
        ApiResponse<ProviderDetailResponse> providerDetails = providersApi.getProvider(request.providerId);
        ProviderDetailResponse provider = providerDetails.getData();

        ProviderAccountRequest providerAccountRequest = new ProviderAccountRequest();
        ProviderAccountsApi providerAccountsApi = new ProviderAccountsApi(userContext);

        ApiResponse<AddedProviderAccountResponse> linkedProviderAccount = providerAccountsApi.linkCredentialProviderAccount(providerAccountRequest, request.providerId);


        return linkedProviderAccount.getData();
    }

    public TokenResponse getAccessToken(String userName) throws ApiException{
        ClientCredentialUserContext userContext = getUserContext(userName);
        TokenResponse response = new TokenResponse();
        response.accessToken = "Bearer "+ userContext.getAccessToken();
        response.nodeUrl = NODEURL;
        return response;

    }
}
