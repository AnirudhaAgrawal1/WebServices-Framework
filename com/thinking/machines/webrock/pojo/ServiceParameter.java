package com.thinking.machines.webrock.pojo;
public class ServiceParameter
{
private String attribute;
private String parameterType;
private boolean isRequestParameterAnnotationApplied;
private boolean isSessionScope;
private boolean isRequestScope;
private boolean isApplicationScope;
private boolean isApplicationDirectory;
private boolean isJson;
private Class jsonParameterType;
private boolean isForward;
private Class forwardParameterType;
public ServiceParameter()
{
this.attribute="";
this.parameterType="";
this.isRequestParameterAnnotationApplied=false;
this.isSessionScope=false;
this.isRequestScope=false;
this.isApplicationScope=false;
this.isApplicationDirectory=false;
this.isJson=false;
this.jsonParameterType=null;
this.isForward=false;
this.forwardParameterType=null;
}
public void setAttribute(String attribute)
{
this.attribute=attribute;
}
public String getAttribute()
{
return this.attribute;
}
public void setParameterType(String parameterType)
{
this.parameterType=parameterType;
}
public String getParameterType()
{
return this.parameterType;
}
public void setIsRequestParameterAnnotationApplied(boolean isRequestParameterAnnotationApplied)
{
this.isRequestParameterAnnotationApplied=isRequestParameterAnnotationApplied;
}
public boolean getIsRequestParameterAnnotationApplied()
{
return this.isRequestParameterAnnotationApplied;
}
public void setIsSessionScope(boolean isSessionScope)
{
this.isSessionScope=isSessionScope;
}
public boolean getIsSessionScope()
{
return this.isSessionScope;
}
public void setIsRequestScope(boolean isRequestScope)
{
this.isRequestScope=isRequestScope;
}
public boolean getIsRequestScope()
{
return this.isRequestScope;
}
public void setIsApplicationScope(boolean isApplicationScope)
{
this.isApplicationScope=isApplicationScope;
}
public boolean getIsApplicationScope()
{
return this.isApplicationScope;
}
public void setIsApplicationDirectory(boolean isApplicationDirectory)
{
this.isApplicationDirectory=isApplicationDirectory;
}
public boolean getIsApplicationDirectory()
{
return this.isApplicationDirectory;
}


public void setIsJson(boolean isJson)
{
this.isJson=isJson;
}
public boolean getIsJson()
{
return this.isJson;
}

public void setJsonParameterType(Class jsonParameterType)
{
this.jsonParameterType=jsonParameterType;
}
public Class getJsonParameterType()
{
return this.jsonParameterType;
}

public void setIsForward(boolean isForward)
{
this.isForward=isForward;
}
public boolean getIsForward()
{
return this.isForward;
}

public void setForwardParameterType(Class forwardParameterType)
{
this.forwardParameterType=forwardParameterType;
}
public Class getForwardParameterType()
{
return this.forwardParameterType;
}

}