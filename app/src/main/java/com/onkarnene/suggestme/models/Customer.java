package com.onkarnene.suggestme.models;
/*
 * Created by Onkar Nene on 23-01-2017.
 */

public class Customer
{
    private String m_customerID;
    private String m_firstName;
    private String m_surname;
    private String m_email;
    private String m_password;
    private String m_dob;
    private String m_mobileNumber;

    public String getCustomerID()
    {
        return m_customerID;
    }

    public void setCustomerID(String customerID)
    {
        this.m_customerID = customerID;
    }

    public String getFirstName()
    {
        return m_firstName;
    }

    public void setFirstName(String firstName)
    {
        this.m_firstName = firstName;
    }

    public String getSurname()
    {
        return m_surname;
    }

    public void setSurname(String surname)
    {
        this.m_surname = surname;
    }

    public String getEmail()
    {
        return m_email;
    }

    public void setEmail(String email)
    {
        this.m_email = email;
    }

    public String getPassword()
    {
        return m_password;
    }

    public void setPassword(String password)
    {
        this.m_password = password;
    }

    public String getDob()
    {
        return m_dob;
    }

    public void setDob(String dob)
    {
        this.m_dob = dob;
    }

    public String getMobileNumber()
    {
        return m_mobileNumber;
    }

    public void setMobileNumber(String mobileNumber)
    {
        this.m_mobileNumber = mobileNumber;
    }
}
