from stalker_backend.Models import Organization


def test_new_organization_public():
    organization = Organization.Organization({'name': "Nome",
                                              'image': "Image.URL",
                                              'address': "Indirizzo",
                                              'city': "città",
                                              'region': "regione",
                                              'postal_code': "35010",
                                              'nation': "nazione",
                                              'phone_number': "+391234567890",
                                              'email': "organizzazione@org.it",
                                              'type': "PUBLIC"})
    assert organization.name == "Nome"
    assert organization.image == "Image.URL"
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
    organization = Organization.Organization({'name': "Nome_ldap",
                                              'address': "Indirizzo_ldap",
                                              'city': "città_ldap",
                                              'region': "regione_ldap",
                                              'postal_code': "35010",
                                              'nation': "nazione_ldap",
                                              'phone_number': "+391234567890",
                                              'email': "organizzazione_ldap@org.it",
                                              'type': "PRIVATE",
                                              'ldap_url': "url-for-ldap",
                                              'ldap_port': 389,
                                              'ldap_common_name': "users.accounts",
                                              'ldap_domain_component': "org.organization"})
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
