Feature: Tenant management

  Scenario: Create a tenant successfully
    Given the platform is ready to receive tenant registrations
    When an administrator creates a tenant with slug "tenant-a" and display name "Tenant A"
    Then the tenant should be active
    And the tenant slug should be "tenant-a"

  Scenario: Reject duplicated tenant slug
    Given a tenant with slug "tenant-a" already exists
    When an administrator tries to create another tenant with slug "tenant-a"
    Then the operation should fail
