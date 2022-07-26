package com.strabo.flow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yodlee.api.model.AbstractModelComponent;
import com.yodlee.api.model.Field;
import com.yodlee.api.model.Name;
import com.yodlee.api.model.account.response.AccountResponse;
import com.yodlee.api.model.configs.CreateConfigsNotificationEvent;
import com.yodlee.api.model.configs.enums.ConfigsNotificationEventType;
import com.yodlee.api.model.configs.request.CreateConfigsNotificationEventRequest;
import com.yodlee.api.model.provideraccounts.request.ProviderAccountRequest;
import com.yodlee.api.model.provideraccounts.response.AddedProviderAccountResponse;
import com.yodlee.api.model.providers.Providers;
import com.yodlee.api.model.providers.response.ProviderDetailResponse;
import com.yodlee.api.model.providers.response.ProviderResponse;
import com.yodlee.api.model.user.UserRegistration;
import com.yodlee.api.model.user.request.UserRequest;
import com.yodlee.api.model.user.response.UserResponse;
import com.yodlee.sdk.api.*;
import com.yodlee.sdk.api.exception.ApiException;
import com.yodlee.sdk.client.ApiListener;
import com.yodlee.sdk.client.ApiResponse;
import com.yodlee.sdk.context.ClientCredentialAdminContext;
import com.yodlee.sdk.context.ClientCredentialUserContext;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AddAccountFlow {


    private AddAccountFlow() {}

    static ObjectMapper mapper = new ObjectMapper();

    public static void subscribeRefreshEvent(ClientCredentialAdminContext clientCredentialAdminContext, String dns)
            throws ApiException {
        ConfigsApi configsApi = new ConfigsApi(clientCredentialAdminContext);
        CreateConfigsNotificationEventRequest eventRequest = new CreateConfigsNotificationEventRequest();
        CreateConfigsNotificationEvent event = new CreateConfigsNotificationEvent();
        // Set callback URL to subscribe for REFRESH event
        event.setCallbackUrl("http://" + dns + ":" + getPort() + "/yourApp/callback");
        eventRequest.setConfigsNotificationEvent(event);
        ApiResponse<AbstractModelComponent> subribeEvent =
                configsApi.createSubscriptionNotificationEvent(ConfigsNotificationEventType.REFRESH, eventRequest);
        System.out.println(String.format("subscribeEvent : %s ", subribeEvent.getStatusCode()));
    }

    public static UserResponse registerUser(ClientCredentialAdminContext clientCredentialAdminContext, String userName)
            throws ApiException {
        UserApi userApi = new UserApi(clientCredentialAdminContext);
        UserResponse userResponse = null;
        UserRequest userRequest = new UserRequest();
        UserRegistration user = new UserRegistration();
        user.setLoginName(userName);
        Name name = new Name();
        // Set user values
        name.setFirst("john");
        name.setLast("doe");
        user.setName(name);
        user.setEmail("email@email.com");
        // Can set other values in user
        userRequest.setUser(user);
        ApiResponse<UserResponse> registeredUser = userApi.registerUser(userRequest);
        System.out.println(String.format("registerUser : %s ", registeredUser.getStatusCode()));
        userResponse = registeredUser.getData();
        System.out.println("Registered User Response : " + userResponse);
        return userResponse;
    }

    public static long getProviderId(ClientCredentialUserContext clientCredentialUserContext, String name,
                                     String providerId) throws ApiException {
        ProvidersApi providersApi = new ProvidersApi(clientCredentialUserContext);
        providersApi.addApiListener(sampleApiListener());
        ApiResponse<ProviderResponse> providerResponse =
                providersApi.getAllProviders(null, null, name, null, null, null, null);
        ProviderResponse providersList = providerResponse.getData();
        if (providersList != null) {
            List<Providers> providers = providersList.getProviders();
            for (Providers provider : providers) {
                // Here we are checking for providerId passed in is ther in response or not
                if (provider.getId().toString().equals(providerId)) {
                    return provider.getId();
                }
            }
        }
        return -1L;
    }

    public static ProviderDetailResponse getProviderDetails(ClientCredentialUserContext clientCredentialUserContext,
                                                            Long providerId) throws ApiException {
        ProvidersApi providersApi = new ProvidersApi(clientCredentialUserContext);
        ProviderDetailResponse provider = null;
        ApiResponse<ProviderDetailResponse> providerDetails = providersApi.getProvider(providerId);
        provider = providerDetails.getData();
        return provider;
    }

    public static AddedProviderAccountResponse linkAccount(ClientCredentialUserContext clientCredentialUserContext,
                                                           ProviderDetailResponse providerDetails, Long providerId, List<Field> fieldList) throws ApiException {
        List<Field> requestfields = new ArrayList<>();
        // List<Row> rows =
        // providerDetails.getProviders().get(0).getLoginForms().get(0).getRows();
        // for (Row row : rows) {
        // List<Field> fields = row.getFields();
        // for (Field field : fields) {
        // Field newField = new Field();
        // newField.setId(field.getId());
        // newField.setName(field.getName());
        // newField.setValue("value"); //credentials
        // requestfields.add(newField);
        // }
        // }
        for (Field field : fieldList) {
            Field newField = new Field();
            newField.setId(field.getId());
            newField.setValue(field.getValue());
            requestfields.add(newField);
        }
        ProviderAccountRequest providerAccountRequest = new ProviderAccountRequest();
        providerAccountRequest.setField(requestfields);
        System.out.println("Add account request : " + providerAccountRequest);
        ProviderAccountsApi providerAccountsApi = new ProviderAccountsApi(clientCredentialUserContext);
        ApiResponse<AddedProviderAccountResponse> linkedProviderAccount = null;
        linkedProviderAccount = providerAccountsApi.linkCredentialProviderAccount(providerAccountRequest, providerId);
        System.out.println("Account added : " + linkedProviderAccount.getData());
        return linkedProviderAccount.getData();
    }

    public static AccountResponse getAccounts(ClientCredentialUserContext clientCredentialUserContext,
                                              Long providerAccountIds) throws ApiException {
        AccountsApi accountsApi = new AccountsApi(clientCredentialUserContext);
        Long[] providerAccountId = convertStringtoLongArray(providerAccountIds.toString());
        ApiResponse<AccountResponse> accounts = null;
        accounts = accountsApi.getAllAccounts(null, null, null, providerAccountId, null, null);
        return accounts.getData();
    }

    private static int getPort() {
        int port = 0;
        try (InputStream inStream = AddAccountFlow.class.getResourceAsStream("/application.properties");) {
            Properties properties = new Properties();
            properties.load(inStream);
            port = Integer.valueOf(properties.getProperty("server.port"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return port;
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

    private static Long[] convertStringtoLongArray(String param) {
        if (StringUtils.isBlank(param)) {
            return null;
        }
        String[] stringArray = param.split("#");
        Long[] value = new Long[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            value[i] = Long.valueOf(stringArray[i]);
        }
        return value;
    }
}
