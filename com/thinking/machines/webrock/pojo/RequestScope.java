package com.thinking.machines.webrock.pojo;
import javax.servlet.*;
import javax.servlet.http.*;
public class RequestScope
{
public HttpServletRequest httpServletRequest;
public void setAttribute(String attribute,Object value)
{
this.httpServletRequest.setAttribute(attribute,value);
}
public Object getAttribute(String attribute)
{
return this.httpServletRequest.getAttribute(attribute);
}
}