package com.galaxiamc.client.api.models;

import com.google.gson.annotations.SerializedName;

public class ManagedAccount extends Account {
    @SerializedName("account_type")
    public String accountType;
    @SerializedName("parent_id")
    public String parentId;
    
    public enum AccountType {
        NATION,
        COMPANY,
        GUILD,
        PERSONAL
    }
    
    public AccountType getAccountTypeEnum() {
        try {
            return AccountType.valueOf(accountType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return AccountType.PERSONAL;
        }
    }
}
