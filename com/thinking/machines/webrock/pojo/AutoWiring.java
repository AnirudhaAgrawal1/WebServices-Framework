package com.thinking.machines.webrock.pojo;
import java.lang.reflect.*;
public class AutoWiring
{
String name;
Field field;
public AutoWiring()
{
this.name="";
this.field=null;
}
public AutoWiring(String name,Field field)
{
this.name=name;
this.field=field;
}
public void setName(String name)
{
this.name=name;
}
public void setField(Field field)
{
this.field=field;
}
public String getName()
{
return this.name;
}
public Field getField()
{
return this.field;
}
}