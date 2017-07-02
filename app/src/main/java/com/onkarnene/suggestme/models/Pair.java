package com.onkarnene.suggestme.models;
/*
 * Created by Onkar Nene on 25-01-2017.
 */

public class Pair
{
    private String m_customerID;
    private String m_shirtPath;
    private String m_pantPath;

    public String getCustomerID()
    {
        return m_customerID;
    }

    public void setCustomerID(String customerID)
    {
        this.m_customerID = customerID;
    }

    public String getShirtPath()
    {
        return m_shirtPath;
    }

    public void setShirtPath(String shirtPath)
    {
        this.m_shirtPath = shirtPath;
    }

    public String getPantPath()
    {
        return m_pantPath;
    }

    public void setPantPath(String pantPath)
    {
        this.m_pantPath = pantPath;
    }
}
