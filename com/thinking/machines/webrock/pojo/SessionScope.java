package com.thinking.machines.webrock.pojo;
import javax.servlet.*;
import javax.servlet.http.*;
public class SessionScope
{
public HttpSession httpSession;
public void setAttribute(String attribute,Object value)
{
this.httpSession.setAttribute(attribute,value);
}
public Object getAttribute(String attribute)
{
return this.httpSession.getAttribute(attribute);
}
}