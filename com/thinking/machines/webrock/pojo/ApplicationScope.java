package com.thinking.machines.webrock.pojo;
import javax.servlet.*;
import javax.servlet.http.*;
public class ApplicationScope
{
public ServletContext servletContext;
public void setAttribute(String attribute,Object value)
{
this.servletContext.setAttribute(attribute,value);
}
public Object getAttribute(String attribute)
{
return this.servletContext.getAttribute(attribute);
}
}