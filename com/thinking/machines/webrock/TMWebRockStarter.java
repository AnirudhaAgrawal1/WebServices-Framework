package com.thinking.machines.webrock;
import com.thinking.machines.webrock.model.*;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.pojo.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import java.lang.annotation.*;
public class TMWebRockStarter extends HttpServlet
{
public void init()
{
try
{
String path="";
List<String> pojosCreated=new ArrayList<>();
List<Service> loadOnStartup=new ArrayList<>();
ServletContext servletContext=getServletContext();
// js section starts here
String jsFileName=servletContext.getInitParameter("jsfile");
RandomAccessFile randomAccessFile=null;
if(jsFileName!=null)
{
String jsFilePath="";
jsFilePath=servletContext.getRealPath(jsFilePath);
jsFilePath=jsFilePath+"WEB-INF\\js";
File jsFile=new File(jsFilePath);
if(!jsFile.exists()) jsFile.mkdir();
jsFilePath=jsFilePath+"\\"+jsFileName;
jsFile=new File(jsFilePath);
randomAccessFile=new RandomAccessFile(jsFile,"rw");
}
File tmpFile=new File(servletContext.getRealPath(path)+"WEB-INF\\web.xml");
RandomAccessFile tmpRandomAccessFile=new RandomAccessFile(tmpFile,"r");
int found=0;
String baseURLPattern="";
while(tmpRandomAccessFile.getFilePointer()<tmpRandomAccessFile.length())
{
if(tmpRandomAccessFile.readLine().equals("<servlet-name>TMWebRock</servlet-name>")) found++;
if(found==2) 
{
while(baseURLPattern.length()==0) baseURLPattern=tmpRandomAccessFile.readLine();
break;
}
}
tmpRandomAccessFile.close();
baseURLPattern=baseURLPattern.substring(baseURLPattern.indexOf("/")+1);
baseURLPattern=baseURLPattern.substring(0,baseURLPattern.indexOf("/"));
// js section ends here
WebRockModel webRockModel=new WebRockModel();
List<String> pathToTraverse=new LinkedList<>();
String packagePrefix=servletContext.getInitParameter("SERVICE_PACKAGE_PREFIX");
path=servletContext.getRealPath(path);
path=path+"WEB-INF\\classes\\";
File file=new File(path);
String[] folders=file.list();
String tmpPath;
for(String folder:folders)
{
if(folder.startsWith(packagePrefix))
{
path=path+folder+"\\";
pathToTraverse.add(path);
}
}
while(pathToTraverse.size()!=0)
{
path=pathToTraverse.remove(0);
folders=new File(path).list();
for(String folder:folders)
{
tmpPath=path+folder;
if(new File(tmpPath).isDirectory())
{
tmpPath+="\\";
pathToTraverse.add(0,tmpPath);
}
if(folder.endsWith(".class"))
{
addToDS(tmpPath,webRockModel,loadOnStartup);
if(randomAccessFile!=null) addToJS(tmpPath,randomAccessFile,webRockModel,pojosCreated,baseURLPattern);
}
}
}
loadOnStartup(loadOnStartup);
servletContext.setAttribute("WebRockModel",webRockModel);
randomAccessFile.close();
}catch(Exception exception)
{
System.out.println(exception);
}
}



public void addToDS(String path,WebRockModel webRockModel,List loadOnStartup)
{
try
{
Service service;
int index=path.indexOf("classes");
String packageName=path.substring(index+8);
while(packageName.indexOf("\\")!=-1)
{
packageName=packageName.replace("\\",".");
}
packageName=packageName.substring(0,packageName.length()-6);
Class c=Class.forName(packageName);
Annotation onStartupAnnotation;
Annotation methodPathAnnotation;
Annotation methodGETAnnotation;
Annotation methodPOSTAnnotation;
Annotation forwardAnnotation;
Annotation classPathAnnotation=c.getAnnotation(Path.class);
Annotation classGETAnnotation=c.getAnnotation(GET.class);
Annotation classPOSTAnnotation=c.getAnnotation(POST.class);
Annotation injectApplicationScopeAnnotation=c.getAnnotation(InjectApplicationScope.class);
Annotation injectSessionScopeAnnotation=c.getAnnotation(InjectSessionScope.class);
Annotation injectRequestScopeAnnotation=c.getAnnotation(InjectRequestScope.class);
Annotation injectApplicationDirectoryAnnotation=c.getAnnotation(InjectApplicationDirectory.class);
Annotation autoWiredAnnotation;
Annotation requestParameters[][];
Annotation classSecuredAccessAnnotation=c.getAnnotation(SecuredAccess.class);
Annotation methodSecuredAccessAnnotation;
Class parameterTypes[];
Field fields[];
boolean found=false;
Method methods[]=c.getMethods();
int x,y;
int priority=0;
String parameterType;
Annotation parameterAnnotations[][];
Class checkPost;
Method guard;
String name;
Method mm[];
for(Method method:methods)
{
onStartupAnnotation=method.getAnnotation(onStartup.class);
if(onStartupAnnotation!=null && (method.getReturnType().toString().equals("void")))
{
service=new Service();
service.setServiceClass(c);
service.setService(method);
service.setRunOnStartup(true);
if(injectApplicationScopeAnnotation!=null) service.setInjectApplicationScope(true);
if(injectRequestScopeAnnotation!=null) service.setInjectRequestScope(true);
if(injectSessionScopeAnnotation!=null) service.setInjectSessionScope(true);
if(injectApplicationDirectoryAnnotation!=null) service.setInjectApplicationDirectory(true);
priority=((onStartup)onStartupAnnotation).priority();
service.setPriority(priority);
x=0;
while(x<loadOnStartup.size())
{
if(priority<(((Service)loadOnStartup.get(x)).getPriority()))
{
loadOnStartup.add(x,service);
break;
}
x++;
}
if(x==loadOnStartup.size()) 
{
loadOnStartup.add(service); 
}
}
if(classPathAnnotation!=null)
{
String classPathAnnotationValue=((Path)classPathAnnotation).value();
methodPathAnnotation=method.getAnnotation(Path.class);
if(methodPathAnnotation!=null && onStartupAnnotation==null)
{
String methodPathAnnotationValue=((Path)methodPathAnnotation).value();
methodGETAnnotation=method.getAnnotation(GET.class);
methodPOSTAnnotation=method.getAnnotation(POST.class);
forwardAnnotation=method.getAnnotation(Forward.class);
service=new Service();
service.setServiceClass(c);
service.setPath(classPathAnnotationValue+methodPathAnnotationValue);
service.setService(method);
if(classSecuredAccessAnnotation!=null)
{
service.setIsSecured(true);
checkPost=Class.forName(((SecuredAccess)classSecuredAccessAnnotation).checkPost());
service.setCheckPost(checkPost);
mm=checkPost.getMethods();
for(Method m:mm)
{
if(m.getName().equals(((SecuredAccess)classSecuredAccessAnnotation).guard()))
{
service.setGuard(m);
break;
}
}
}
methodSecuredAccessAnnotation=method.getAnnotation(SecuredAccess.class);
if(methodSecuredAccessAnnotation!=null)
{
service.setIsSecured(true);
checkPost=Class.forName(((SecuredAccess)methodSecuredAccessAnnotation).checkPost());
service.setCheckPost(checkPost);
mm=checkPost.getMethods();
for(Method m:mm)
{
if(m.getName().equals(((SecuredAccess)methodSecuredAccessAnnotation).guard()))
{
service.setGuard(m);
break;
}
}
}
if(methodGETAnnotation!=null) service.setIsGetAllowed(true);
if(methodPOSTAnnotation!=null) service.setIsPostAllowed(true);
if(methodGETAnnotation==null && methodPOSTAnnotation==null)
{
if(classGETAnnotation!=null) service.setIsGetAllowed(true);
if(classPOSTAnnotation!=null) service.setIsPostAllowed(true);
}
parameterTypes=method.getParameterTypes();
if(parameterTypes.length>0)
{
parameterType=parameterTypes[0].getSimpleName();
parameterAnnotations=method.getParameterAnnotations();
if(isPrimitive(parameterType)==false && parameterType.equals("RequestScope")==false && parameterType.equals("SessionScope")==false && parameterType.equals("ApplicationScope")==false && parameterType.equals("ApplicationDirectory")==false)
{
for(int k=0;k<parameterAnnotations.length;k++)
{
for(int z=0;z<parameterAnnotations[k].length;z++)
{
if(parameterAnnotations[z][k] instanceof RequestParameter) 
{
found=true;
continue;
}
}
}
for(int z=1;z<parameterTypes.length;z++)
{
parameterType=parameterTypes[z].getSimpleName();
if(parameterType.equals("RequestScope")==false && parameterType.equals("SessionScope")==false && parameterType.equals("ApplicationScope")==false && parameterType.equals("ApplicationDirectory")==false) found=true;
}
if(found==true) continue;
ServiceParameter serviceParameter=new ServiceParameter();

if(service.getIsPostAllowed())
{
serviceParameter.setIsJson(true);
serviceParameter.setJsonParameterType(parameterTypes[0]);
serviceParameter.setIsForward(true);
serviceParameter.setForwardParameterType(parameterTypes[0]);
service.addToServiceParameters(serviceParameter);
}
else
{
serviceParameter.setIsForward(true);
serviceParameter.setForwardParameterType(parameterTypes[0]);
service.addToServiceParameters(serviceParameter);
}
}
}

if(forwardAnnotation!=null) service.setForward(((Forward)forwardAnnotation).value());
if(injectApplicationScopeAnnotation!=null) service.setInjectApplicationScope(true);
if(injectRequestScopeAnnotation!=null) service.setInjectRequestScope(true);
if(injectSessionScopeAnnotation!=null) service.setInjectSessionScope(true);
if(injectApplicationDirectoryAnnotation!=null) service.setInjectApplicationDirectory(true);
fields=c.getDeclaredFields();
for(Field field:fields)
{
autoWiredAnnotation=field.getAnnotation(AutoWired.class);
if(autoWiredAnnotation!=null)
{
service.addToAutoWiring(((AutoWired)autoWiredAnnotation).name(),field);
}
}
Annotation injectRequestParameterAnnotation;
InjectParameter injectParameter;
for(Field field:fields)
{
injectRequestParameterAnnotation=field.getAnnotation(InjectRequestParameter.class);
if(injectRequestParameterAnnotation!=null)
{
injectParameter=new InjectParameter();
injectParameter.setAttribute(((InjectRequestParameter)injectRequestParameterAnnotation).value());
injectParameter.setField(field);
service.addToInjectParameters(injectParameter);
}
}
requestParameters=method.getParameterAnnotations();
parameterTypes=method.getParameterTypes();
ServiceParameter serviceParameter;
for(y=0;y<parameterTypes.length;y++)
{
if(parameterTypes[y].getSimpleName().equals("RequestScope"))
{
serviceParameter=new ServiceParameter();
serviceParameter.setIsRequestScope(true);
service.addToServiceParameters(serviceParameter);
continue;
}
if(parameterTypes[y].getSimpleName().equals("SessionScope"))
{
serviceParameter=new ServiceParameter();
serviceParameter.setIsSessionScope(true);
service.addToServiceParameters(serviceParameter);
continue;
}
if(parameterTypes[y].getSimpleName().equals("ApplicationScope"))
{
serviceParameter=new ServiceParameter();
serviceParameter.setIsApplicationScope(true);
service.addToServiceParameters(serviceParameter);
continue;
}
if(parameterTypes[y].getSimpleName().equals("ApplicationDirectory"))
{
serviceParameter=new ServiceParameter();
serviceParameter.setIsApplicationDirectory(true);
service.addToServiceParameters(serviceParameter);
continue;
}
if(requestParameters[y].length>0)
{
for(int k=0;k<requestParameters[y].length;k++)
{
if(requestParameters[y][k] instanceof RequestParameter)
{
serviceParameter=new ServiceParameter();
serviceParameter.setIsRequestParameterAnnotationApplied(true);
String attribute=((RequestParameter)requestParameters[y][k]).value();
serviceParameter.setAttribute(attribute);
serviceParameter.setParameterType(parameterTypes[y].getName());
service.addToServiceParameters(serviceParameter);
continue;
}
}
}
}
webRockModel.model.put(classPathAnnotationValue+methodPathAnnotationValue,service);
}
}
}
}catch(Exception e)
{
System.out.println(e);
}
}



public void loadOnStartup(List list)
{
try
{
Service service;
Method[] methods;
boolean setterFound=false;
for(int x=0;x<list.size();x++)
{
service=(Service)list.get(x);
if(service.getInjectApplicationScope())
{
methods=service.getServiceClass().getMethods();
for(Method method:methods)
{
if(method.getName().equals("setApplicationScope")) 
{
ApplicationScope applicationScope=new ApplicationScope();
applicationScope.servletContext=getServletContext();
method.invoke(service.getClass().newInstance(),applicationScope);
break;
}
}
}
service.getService().invoke(service.getServiceClass().newInstance());
}
}catch(Exception exception)
{
System.out.println(exception);
}
}
public boolean isPrimitive(String typeName)
{
if(typeName.equals("long")==true || typeName.equals("java.lang.Long")) return true;
if(typeName.equals("int")==true || typeName.equals("java.lang.Integer")) return true;
if(typeName.equals("short")==true || typeName.equals("java.lang.Short")) return true;
if(typeName.equals("byte")==true || typeName.equals("java.lang.Byte")) return true;
if(typeName.equals("double")==true || typeName.equals("java.lang.Double")) return true;
if(typeName.equals("float")==true || typeName.equals("java.lang.Float")) return true;
if(typeName.equals("char")==true || typeName.equals("java.lang.Character")) return true;
if(typeName.equals("boolean")==true || typeName.equals("java.lang.Boolean")) return true;
if(typeName.equals("java.lang.String")==true || typeName.equals("String")) return true;
return false;
}

public void addToJS(String path,RandomAccessFile randomAccessFile,WebRockModel webRockModel,List<String> pojosCreated,String baseURLPattern)
{
try
{
int index=path.indexOf("classes");
String packageName=path.substring(index+8);
while(packageName.indexOf("\\")!=-1)
{
packageName=packageName.replace("\\",".");
}
List<Class> pojos=new ArrayList<>();
packageName=packageName.substring(0,packageName.length()-6);
Class c=Class.forName(packageName);
String bytesToWrite="";
Annotation classPathAnnotation=c.getAnnotation(Path.class);
if(classPathAnnotation!=null)
{
bytesToWrite="class "+c.getSimpleName()+"\n{\n";
randomAccessFile.writeBytes(bytesToWrite);
String url="";
Method methods[]=c.getMethods();
Annotation methodPathAnnotation;
List<ServiceParameter> parameters;
ServiceParameter serviceParameter;
for(Method method:methods)
{
methodPathAnnotation=method.getAnnotation(Path.class);
if(methodPathAnnotation!=null)
{
url=((Path)classPathAnnotation).value()+((Path)methodPathAnnotation).value();
if(webRockModel.model.containsKey(url))
{
bytesToWrite=method.getName()+"(";
parameters=webRockModel.model.get(url).getServiceParameters();
for(int i=0;i<parameters.size();i++)
{
serviceParameter=parameters.get(i);
if(serviceParameter.getIsRequestParameterAnnotationApplied())
{
if(i!=0) bytesToWrite+=",";
bytesToWrite+=serviceParameter.getAttribute();
}
if(serviceParameter.getIsJson()) 
{
pojos.add(serviceParameter.getJsonParameterType());
bytesToWrite+=serviceParameter.getJsonParameterType().getSimpleName().toLowerCase();
}
}
bytesToWrite+=")\n{\n";
randomAccessFile.writeBytes(bytesToWrite);
bytesToWrite="var promise=new Promise(function(done,problem){\n$.ajax({\ntype:\"";
randomAccessFile.writeBytes(bytesToWrite);
if(webRockModel.model.get(url).getIsGetAllowed()) bytesToWrite="GET";
else bytesToWrite="POST";
bytesToWrite+="\",\nurl:\""+baseURLPattern+url+"\",\n";
randomAccessFile.writeBytes(bytesToWrite);
bytesToWrite="";
int j=0;
for(int i=0;i<parameters.size();i++)
{
serviceParameter=parameters.get(i);
if(serviceParameter.getIsRequestParameterAnnotationApplied())
{
if(i==0) bytesToWrite+="data:{";
if(i!=0) bytesToWrite+=",\n";
j=1;
bytesToWrite+="\""+serviceParameter.getAttribute()+"\":"+serviceParameter.getAttribute();
}
if(serviceParameter.getIsJson()) 
{
bytesToWrite+="data:JSON.stringify("+serviceParameter.getJsonParameterType().getSimpleName().toLowerCase()+"),\n";
bytesToWrite+="contentType:\"application/json\",\n";
}
}
if(j==1) bytesToWrite+="},\n";
randomAccessFile.writeBytes(bytesToWrite);
randomAccessFile.writeBytes("success:done,\nerror:problem\n})\n});\nreturn promise\n}\n");
}
}
}
randomAccessFile.writeBytes("}\n");
}
for(Class cc:pojos)
{
if(!pojosCreated.contains(cc.getSimpleName()))
{
bytesToWrite="class "+cc.getSimpleName()+"\n{\n";
randomAccessFile.writeBytes(bytesToWrite);
Field fields[]=cc.getDeclaredFields();
randomAccessFile.writeBytes("constructor()\n{\n");
bytesToWrite="";
for(Field field:fields)
{
bytesToWrite+="this."+field.getName()+"=";
if(isPrimitive(field.getType().getSimpleName()))
{
if(field.getType().getSimpleName().equals("String") || field.getType().getSimpleName().equals("java.lang.String")) bytesToWrite+="\"\";\n";
else if(field.getType().getSimpleName().equals("char") || field.getType().getSimpleName().equals("java.lang.Character")) bytesToWrite+="\'\';\n";
else if(field.getType().getSimpleName().equals("Boolean") || field.getType().getSimpleName().equals("java.lang.Boolean")) bytesToWrite+="false;\n";
else bytesToWrite+="0;\n";
}
}
bytesToWrite+="}\n";
randomAccessFile.writeBytes(bytesToWrite);
for(Field field:fields)
{
bytesToWrite="set"+field.getName().substring(0,1).toUpperCase()+field.getName().substring(1)+"("+field.getName()+"){this."+field.getName()+"="+field.getName()+";}\n";
bytesToWrite+="get"+field.getName().substring(0,1).toUpperCase()+field.getName().substring(1)+"("+field.getName()+"){return this."+field.getName()+";}\n";
randomAccessFile.writeBytes(bytesToWrite);
}
randomAccessFile.writeBytes("}\n");
pojosCreated.add(cc.getSimpleName());
}
}
}catch(Exception exception)
{
System.out.println(exception);
}
}
}