package com.example.lab1.data;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "Metall", strict = false)
public class MetalRates {

    @Attribute(name = "FromDate", required = false)
    public String fromDate;

    @Attribute(name = "ToDate", required = false)
    public String toDate;

    @Attribute(name = "name", required = false)
    public String name;

    @ElementList(entry = "Record", inline = true, required = false)
    public List<Record> records;
}