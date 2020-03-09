package com.vartmp7.stalker.GsonBeans;

public class Organizzazione {

    private String address;
    private String city;
    private String email;
    private String id;
    private String name;
    private String nation;
    private String phone_number;
    private String postal_code;
    private String region;
    private String type;
    private String ldap_common_name;
    private String ldap_domain_component;
    private String ldap_port;

    public String getLdap_common_name() {
        return ldap_common_name;
    }

    public void setLdap_common_name(String ldap_common_name) {
        this.ldap_common_name = ldap_common_name;
    }

    public String getLdap_domain_component() {
        return ldap_domain_component;
    }

    public void setLdap_domain_component(String ldap_domain_component) {
        this.ldap_domain_component = ldap_domain_component;
    }

    public String getLdap_port() {
        return ldap_port;
    }

    public void setLdap_port(String ldap_port) {
        this.ldap_port = ldap_port;
    }

    public String getLdap_url() {
        return ldap_url;
    }

    public void setLdap_url(String ldap_url) {
        this.ldap_url = ldap_url;
    }

    private String ldap_url;
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrgInfo() {
        return getName()+" presso: "+getAddress();
    }
}
