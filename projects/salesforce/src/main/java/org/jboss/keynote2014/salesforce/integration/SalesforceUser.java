package org.jboss.keynote2014.salesforce.integration;

public class SalesforceUser
{
    private final String email;
    private final String mobilePhone;
    
    public SalesforceUser(final String email, final String mobilePhone)
    {
        this.email = email;
        this.mobilePhone = mobilePhone;
    }

    public String getEmail()
    {
        return email;
    }

    public String getMobilePhone()
    {
        return mobilePhone;
    }

    @Override
    public String toString() {
        return "SalesforceUser[" +
                "email='" + email + '\'' +
                ", mobilePhone='" + mobilePhone + '\'' +
                ']';
    }
}
