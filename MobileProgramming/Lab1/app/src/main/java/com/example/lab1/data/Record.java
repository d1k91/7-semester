package com.example.lab1.data;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Record", strict = false)
public class Record {

    @Attribute(name = "Date", required = false)
    public String date;

    @Attribute(name = "Code", required = false)
    public int code;

    @Element(name = "Buy", required = false)
    public String buy;

    @Element(name = "Sell", required = false)
    public String sell;
}