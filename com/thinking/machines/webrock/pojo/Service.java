package com.thinking.machines.webrock.pojo;
import java.lang.reflect.*;
import java.util.*;
public class Service
{
private Class serviceClass;
private String path;
private Method service;
private boolean isGetAllowed;
private boolean isPostAllowed;
private String forward;
private boolean runOnStartup;
private int priority;
private boolean injectApplicationDirectory;
private boolean injectApplicationScope;
private boolean injectSessionScope;
private boolean injectRequestScope;
private List<AutoWiring> autoWiring;
private List<ServiceParameter> serviceParameters;
private List<InjectParameter> injectParameters;
private boolean isSecured;
private Class checkPost;
private Method guard;
public Service()
{
this.serviceClass=null;
this.path="";
this.service=null;
this.isGetAllowed=false;
this.isPostAllowed=false;
this.forward="";
this.runOnStartup=false;
this.priority=0;
this.autoWiring=new ArrayList<>();
this.serviceParameters=new ArrayList<>();
injectParameters=new ArrayList<>();
this.isSecured=false;
this.checkPost=null;
this.guard=null;
}
public void setServiceClass(Class serviceClass)
{
this.serviceClass=serviceClass;
}
public Class getServiceClass()
{
return this.serviceClass;
}
public void setPath(String path)
{
this.path=path;
}
public String getPath()
{
return this.path;
}
public void setService(Method service)
{
this.service=service;
}
public Method getService()
{
return this.service;
}
public void setIsGetAllowed(boolean isGetAllowed)
{
this.isGetAllowed=isGetAllowed;
}
public boolean getIsGetAllowed()
{
return this.isGetAllowed;
}
public void setIsPostAllowed(boolean isPostAllowed)
{
this.isPostAllowed=isPostAllowed;
}
public boolean getIsPostAllowed()
{
return this.isPostAllowed;
}
public void setForward(String forward)
{
this.forward=forward;
}
public String getForward()
{
return this.forward;
}
public void setRunOnStartup(boolean runOnStartup)
{
this.runOnStartup=runOnStartup;
}
public boolean getRunOnStartup()
{
return this.runOnStartup;
}
public void setPriority(int priority)
{
this.priority=priority;
}
public int getPriority()
{
return this.priority;
}
public void setInjectApplicationDirectory(boolean injectApplicationDirectory)
{
this.injectApplicationDirectory=injectApplicationDirectory;
}
public boolean getInjectApplicationDirectory()
{
return this.injectApplicationDirectory;
}
public void setInjectApplicationScope(boolean injectApplicationScope)
{
this.injectApplicationScope=injectApplicationScope;
}
public boolean getInjectApplicationScope()
{
return this.injectApplicationScope;
}
public void setInjectSessionScope(boolean injectSessionScope)
{
this.injectSessionScope=injectSessionScope;
}
public boolean getInjectSessionScope()
{
return this.injectSessionScope;
}
public void setInjectRequestScope(boolean injectRequestScope)
{
this.injectRequestScope=injectRequestScope;
}
public boolean getInjectRequestScope()
{
return this.injectRequestScope;
}
public void addToAutoWiring(String name,Field field)
{
AutoWiring autoWiring=new AutoWiring(name,field);
this.autoWiring.add(autoWiring);
}
public List getAutoWiring()
{
return this.autoWiring;
}
public void addToServiceParameters(ServiceParameter serviceParameter)
{
this.serviceParameters.add(serviceParameter);
}
public List getServiceParameters()
{
return this.serviceParameters;
}
public void addToInjectParameters(InjectParameter injectParameter)
{
this.injectParameters.add(injectParameter);
}
public List getInjectParameters()
{
return this.injectParameters;
}
public void setIsSecured(boolean isSecured)
{
this.isSecured=isSecured;
}
public boolean getIsSecured()
{
return this.isSecured;
}
public void setCheckPost(Class checkPost)
{
this.checkPost=checkPost;
}
public Class getCheckPost()
{
return this.checkPost;
}
public void setGuard(Method guard)
{
this.guard=guard;
}
public Method getGuard()
{
return this.guard;
}


}