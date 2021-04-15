package com.thinking.machines.webrock;
import javax.servlet.*;
import javax.servlet.http.*;
import java.lang.reflect.*;
import java.util.*;
import java.io.*;
import com.google.gson.*;
import java.lang.annotation.*;
import com.thinking.machines.webrock.pojo.*;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.exception.*;
import com.thinking.machines.webrock.model.*;
public class TMWebRock extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
try
{
String URI=request.getRequestURI();
URI=URI.substring(1);
URI=URI.substring(URI.indexOf("/"));
URI=URI.substring(1);
URI=URI.substring(URI.indexOf("/"));
Service service;
Class className;
Method method;
Method[] methods;
Object instance;
Object obj=null;
Class[] parameterTypes;
Object[] arguments;
String typeName;
String data;
List serviceParameters;
ServiceParameter serviceParameter;
int x;
ServletContext servletContext=getServletContext();
String contentType;
WebRockModel webRockModel=(WebRockModel)servletContext.getAttribute("WebRockModel");
PrintWriter pw=response.getWriter();
if(webRockModel.model.containsKey(URI))
{
service=webRockModel.model.get(URI);
className=service.getServiceClass();
method=service.getService();
if(service.getIsGetAllowed()==false && service.getIsPostAllowed()==true)
{
response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
return;
}
if(service.getIsSecured())
{
boolean isSecure=checkSecurityAccess(service,request,response,webRockModel);
if(isSecure==false) 
{
response.sendError(HttpServletResponse.SC_NOT_FOUND);
return;
}
}
String forward="";
instance=className.newInstance();
injectScope(service,servletContext,request,instance);
autoWiring(service,servletContext,request,instance);
injectParameter(service,request,response,instance);
arguments=getServiceArguments(service,request,response);
try
{
if(arguments!=null)
{
obj=method.invoke(instance,arguments);
}
else
{
obj=method.invoke(instance);
}
}catch(Exception exception)
{
response.setContentType("text/plain");
pw.print(exception.getCause().getMessage());
return;
}
forward=service.getForward();
while(forward.length()!=0)
{
obj=forward(request,response,webRockModel,forward,obj);
forward=(webRockModel.model.get(forward)).getForward();
}
}
else
{
response.sendError(HttpServletResponse.SC_NOT_FOUND);
}
if(obj!=null) pw.print(obj);
}catch(Exception exception)
{
try
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
}catch(Exception e)
{
// do nothing
}
}
}



public void doPost(HttpServletRequest request,HttpServletResponse response)
{
try
{
String URI=request.getRequestURI();
URI=URI.substring(1);
URI=URI.substring(URI.indexOf("/"));
URI=URI.substring(1);
URI=URI.substring(URI.indexOf("/"));
ServletContext servletContext=getServletContext();
WebRockModel webRockModel=(WebRockModel)servletContext.getAttribute("WebRockModel");
Object instance;
Method[] methods;
Object obj=null;
Class[] parameterTypes;
Object[] arguments;
String typeName;
String data;
List requestParameters;
ServiceParameter serviceParameter;
int x;
PrintWriter pw=response.getWriter();
if(webRockModel.model.containsKey(URI))
{
Service service=webRockModel.model.get(URI);
Class className=service.getServiceClass();
Method method=service.getService();
if(service.getIsGetAllowed()==true && service.getIsPostAllowed()==false)
{
response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
return;
}
String forward="";
instance=className.newInstance();
injectScope(service,servletContext,request,instance);
autoWiring(service,servletContext,request,instance);
injectParameter(service,request,response,instance);
arguments=getServiceArguments(service,request,response);
if(request.getContentType()!=null)
{
if(request.getContentType().equalsIgnoreCase("application/json"))
{
serviceParameter=(ServiceParameter)service.getServiceParameters().get(0);
if(serviceParameter.getIsJson()==true)
{
BufferedReader br=request.getReader();
StringBuffer sb=new StringBuffer();
String d;
Object c;
while(true)
{
d=br.readLine();
if(d==null) break;
sb.append(d);
}
String rawData=sb.toString();
Gson gson=new Gson();
c=gson.fromJson(rawData,serviceParameter.getJsonParameterType());
arguments[0]=c;
}
}
}
try
{
if(arguments!=null)
{
obj=method.invoke(instance,arguments);
}
else
{
obj=method.invoke(instance);
}
}catch(Exception exception)
{
response.setContentType("text/plain");
response.getWriter().print(exception.getCause().getMessage());
return;
}
forward=service.getForward();
while(forward.length()!=0)
{
obj=forward(request,response,webRockModel,forward,obj);
forward=(webRockModel.model.get(forward)).getForward();
}

}
else
{
response.sendError(HttpServletResponse.SC_NOT_FOUND);
}
if(obj!=null) pw.print(obj);
}catch(Exception exception)
{
try
{
System.out.println("Error generated"+exception);
return;
//response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
}catch(Exception e)
{
// do nothing
}
}
}



private void injectScope(Service service,ServletContext servletContext,HttpServletRequest request,Object instance)
{
try
{
Method[] methods;
Class className=service.getServiceClass();
if(service.getInjectApplicationScope())
{
methods=className.getMethods();
for(Method m:methods)
{
if(m.getName().equals("setApplicationScope"))
{
ApplicationScope applicationScope=new ApplicationScope();
applicationScope.servletContext=servletContext;
m.invoke(instance,applicationScope);
break;
}
}
}
if(service.getInjectSessionScope())
{
methods=className.getMethods();
for(Method m:methods)
{
if(m.getName().equals("setSessionScope"))
{
SessionScope sessionScope=new SessionScope();
sessionScope.httpSession=request.getSession();
m.invoke(instance,sessionScope);
break;
}
}
}
if(service.getInjectRequestScope())
{
methods=className.getMethods();
for(Method m:methods)
{
RequestScope requestScope=new RequestScope();
requestScope.httpServletRequest=request;
if(m.getName().equals("setRequestScope"))
{
m.invoke(instance,requestScope);
break;
}
}
}
}catch(Exception e)
{
// do nothing
}
}
private void autoWiring(Service service,ServletContext servletContext,HttpServletRequest request,Object instance)
{
try
{
List<AutoWiring> autoWiringProperties;
int x;
Field[] fields;
Field field;
String fieldName;
String name;
AutoWiring autoWiring;
Object obj;
Method[] methods;
String setter;
Class[] parameterTypes;
Class className=service.getServiceClass();
autoWiringProperties=service.getAutoWiring();
if(autoWiringProperties.size()!=0)
{
x=0;
while(x<autoWiringProperties.size())
{
autoWiring=autoWiringProperties.get(x);
x++;
name=autoWiring.getName();
field=autoWiring.getField();
fieldName=field.getName();
obj=request.getAttribute(name);
if(obj==null) obj=request.getSession().getAttribute(name);
if(obj==null) obj=servletContext.getAttribute(name);
if(obj==null) continue;
setter="set"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
methods=className.getMethods();
for(Method m:methods)
{
if(m.getName().equals(setter))
{
parameterTypes=m.getParameterTypes();
if(parameterTypes.length!=1) continue;
if(parameterTypes[0].isInstance(obj)==false) continue; 
m.invoke(instance,obj);
}
}
}
}
}catch(Exception e)
{
// do nothing
}
}

public void injectParameter(Service service,HttpServletRequest request,HttpServletResponse response,Object instance)
{
List<InjectParameter> injectParameters=service.getInjectParameters();
InjectParameter injectParameter;
String setter;
Field field;
String attribute;
Method methods[];
String data;
Object obj=null;
String typeName;
String fieldName;
try
{
for(int x=0;x<injectParameters.size();x++)
{
injectParameter=(InjectParameter)injectParameters.get(x);
attribute=injectParameter.getAttribute();
data=request.getParameter(attribute);
field=injectParameter.getField();
fieldName=field.getName();
setter="set"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
methods=service.getServiceClass().getMethods();
for(Method m:methods)
{
if(m.getName().equals(setter))
{
if(m.getParameterCount()!=1) continue; 
typeName=(m.getParameterTypes())[0].getSimpleName();
if(typeName.equals("long")==true || typeName.equals("java.lang.Long")) obj=Long.parseLong(data);
if(typeName.equals("int")==true || typeName.equals("java.lang.Integer")) obj=Integer.parseInt(data);
if(typeName.equals("short")==true || typeName.equals("java.lang.Short")) obj=Short.parseShort(data);
if(typeName.equals("byte")==true || typeName.equals("java.lang.Byte")) obj=Byte.parseByte(data);
if(typeName.equals("double")==true || typeName.equals("java.lang.Double")) obj=Double.parseDouble(data);
if(typeName.equals("float")==true || typeName.equals("java.lang.Float")) obj=Float.parseFloat(data);
if(typeName.equals("char")==true || typeName.equals("java.lang.Character")) obj=data.charAt(0);
if(typeName.equals("boolean")==true || typeName.equals("java.lang.Boolean")) obj=Boolean.parseBoolean(data);
if(typeName.equals("java.lang.String")==true || typeName.equals("String")) obj=data;
if(obj==null) continue;
m.invoke(instance,obj);
}
}
}
}catch(Exception e)
{
try
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
}catch(Exception ee)
{
// nothing to do
}
}
}

public Object[] getServiceArguments(Service service,HttpServletRequest request,HttpServletResponse response)
{
Object arguments[]=null;
List serviceParameters=service.getServiceParameters();
ServiceParameter serviceParameter;
if(serviceParameters.size()>0)
{
arguments=new Object[serviceParameters.size()];
SessionScope sessionScope=new SessionScope();
sessionScope.httpSession=request.getSession();
RequestScope requestScope=new RequestScope();
requestScope.httpServletRequest=request;
ApplicationScope applicationScope=new ApplicationScope();
applicationScope.servletContext=getServletContext();
ApplicationDirectory applicationDirectory=null;
for(int x=0;x<serviceParameters.size();x++)
{
serviceParameter=(ServiceParameter)serviceParameters.get(x);
if(serviceParameter.getIsRequestScope())
{
arguments[x]=requestScope;
continue;
}
if(serviceParameter.getIsSessionScope())
{
arguments[x]=sessionScope;
continue;
}
if(serviceParameter.getIsApplicationScope())
{
arguments[x]=applicationScope;
continue;
}
if(serviceParameter.getIsApplicationDirectory())
{
arguments[x]=applicationDirectory;
continue;
}
if(serviceParameter.getIsRequestParameterAnnotationApplied())
{
try
{
String data=request.getParameter(serviceParameter.getAttribute());
String typeName=serviceParameter.getParameterType();
if(typeName.equals("long")==true || typeName.equals("java.lang.Long")) arguments[x]=Long.parseLong(data);
if(typeName.equals("int")==true || typeName.equals("java.lang.Integer")) arguments[x]=Integer.parseInt(data);
if(typeName.equals("short")==true || typeName.equals("java.lang.Short")) arguments[x]=Short.parseShort(data);
if(typeName.equals("byte")==true || typeName.equals("java.lang.Byte")) arguments[x]=Byte.parseByte(data);
if(typeName.equals("double")==true || typeName.equals("java.lang.Double")) arguments[x]=Double.parseDouble(data);
if(typeName.equals("float")==true || typeName.equals("java.lang.Float")) arguments[x]=Float.parseFloat(data);
if(typeName.equals("char")==true || typeName.equals("java.lang.Character")) arguments[x]=data.charAt(0);
if(typeName.equals("boolean")==true || typeName.equals("java.lang.Boolean")) arguments[x]=Boolean.parseBoolean(data);
if(typeName.equals("java.lang.String")==true) arguments[x]=data;
}catch(Exception ee)
{
try
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
}catch(Exception e)
{
// nothing to do
}
}
}
}
}
return arguments;
}
public Object forward(HttpServletRequest request,HttpServletResponse response,WebRockModel webRockModel,String forward,Object obj)
{
Service service;
Object instance;
Class className;
Method method;
Object arguments[];
ServletContext servletContext=getServletContext();
try
{
if(webRockModel.model.containsKey(forward))
{
service=webRockModel.model.get(forward);
className=service.getServiceClass();
instance=className.newInstance();
method=service.getService();
forward="";
instance=className.newInstance();
injectScope(service,servletContext,request,instance);
autoWiring(service,servletContext,request,instance);
injectParameter(service,request,response,instance);
arguments=getServiceArguments(service,request,response);
try
{
if(method.getParameterCount()>0)
{
if(obj!=null && (method.getParameterTypes()[0]).isInstance(obj))
{
arguments[0]=obj;
}
obj=method.invoke(instance,arguments);
}
else
{
obj=method.invoke(instance);
}
}catch(Exception exception)
{
response.setContentType("text/plain");
response.getWriter().print(exception.getCause().getMessage());
return null;
}
}
else
{
RequestDispatcher requestDispatcher=request.getRequestDispatcher(forward);
requestDispatcher.forward(request,response);
}
}catch(Exception e)
{
try
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
}catch(Exception ee)
{
// do nothing
}
}
return obj;
}


public boolean checkSecurityAccess(Service service,HttpServletRequest request,HttpServletResponse response,WebRockModel webRockModel)
{
try
{
Service ss=new Service();
Class checkPost=service.getCheckPost();
Method guard=service.getGuard();
ss.setServiceClass(checkPost);
ss.setGuard(guard);
Object instance=checkPost.newInstance();
Annotation annotations[]=checkPost.getAnnotations();
ServiceParameter serviceParameter;
for(Annotation annotation:annotations)
{
if(annotation instanceof InjectRequestScope) ss.setInjectRequestScope(true);
if(annotation instanceof InjectSessionScope) ss.setInjectSessionScope(true);
if(annotation instanceof InjectApplicationScope) ss.setInjectApplicationScope(true);
if(annotation instanceof InjectApplicationDirectory) ss.setInjectApplicationDirectory(true);
}
Class parameterTypes[]=guard.getParameterTypes();
for(Class parameterType:parameterTypes)
{
serviceParameter=new ServiceParameter();
if(parameterType.getSimpleName().equals("RequestScope")) serviceParameter.setIsRequestScope(true);
if(parameterType.getSimpleName().equals("SessionScope")) serviceParameter.setIsSessionScope(true);
if(parameterType.getSimpleName().equals("ApplicationScope")) serviceParameter.setIsApplicationScope(true);
if(parameterType.getSimpleName().equals("ApplicationDirectory")) serviceParameter.setIsApplicationDirectory(true);
ss.addToServiceParameters(serviceParameter);
}
injectScope(ss,getServletContext(),request,instance);
Object arguments[]=getServiceArguments(ss,request,response);
try
{
if(arguments!=null) 
{
guard.invoke(instance,arguments);
}
else guard.invoke(instance);
}catch(Exception exception)
{
return false;
}
}catch(Exception e)
{
try
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
}catch(Exception ee)
{
// do nothing
}
}
return true;
}
}