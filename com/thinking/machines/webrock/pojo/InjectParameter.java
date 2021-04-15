package com.thinking.machines.webrock.pojo;
import java.lang.reflect.*;
public class InjectParameter
{
private String attribute;
private Field field;
public InjectParameter()
{
this.attribute="";
this.field=null;
}
public void setAttribute(String attribute)
{
this.attribute=attribute;
}
public String getAttribute()
{
return this.attribute;
}
public void setField(Field field)
{
this.field=field;
}
public Field getField()
{
return this.field;
}
}