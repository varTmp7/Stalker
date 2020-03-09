from stalker_backend.Models import Organization


def test_new_organization_public():
    organization = Organization.Organization("Nome",
                                             "Indirizzo",
                                             "città",
                                             "regione",
                                             "35010",
                                             "nazione",
                                             "+391234567890",
                                             "organizzazione@org.it",
                                             "PUBLIC")
    assert organization.name == "Nome"
    assert organization.address == "Indirizzo"
    assert organization.city == "città"
    assert organization.region == "regione"
    assert organization.postal_code == "35010"
    assert organization.nation == "nazione"
    assert organization.phone_number == "+391234567890"
    assert organization.email == "organizzazione@org.it"
    assert organization.organization_type == "PUBLIC"
    assert organization.ldap_url is None
    assert organization.ldap_port is None
    assert organization.ldap_common_name is None
    assert organization.ldap_domain_component is None


def test_new_organization_private():
    organization = Organization.Organization("Nome_ldap",
                                             "Indirizzo_ldap",
                                             "città_ldap",
                                             "regione_ldap",
                                             "35010",
                                             "nazione_ldap",
                                             "+391234567890",
                                             "organizzazione_ldap@org.it",
                                             "PRIVATE",
                                             "url-for-ldap",
                                             389,
                                             "users.accounts",
                                             "org.organization")
    assert organization.name == "Nome_ldap"
    assert organization.address == "Indirizzo_ldap"
    assert organization.city == "città_ldap"
    assert organization.region == "regione_ldap"
    assert organization.postal_code == "35010"
    assert organization.nation == "nazione_ldap"
    assert organization.phone_number == "+391234567890"
    assert organization.email == "organizzazione_ldap@org.it"
    assert organization.organization_type == "PRIVATE"
    assert organization.ldap_url == "url-for-ldap"
    assert organization.ldap_port == 389
    assert organization.ldap_common_name == "users.accounts"
    assert organization.ldap_domain_component == "org.organization"
