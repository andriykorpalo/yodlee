package com.strabo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"token"})
public class ClientCredentialTokenResponse {
    @JsonProperty("token")
    private ClientCredentialToken token;

    public ClientCredentialTokenResponse() {
    }

    public ClientCredentialToken getToken() {
        return this.token;
    }

    public void setToken(ClientCredentialToken token) {
        this.token = token;
    }
}
