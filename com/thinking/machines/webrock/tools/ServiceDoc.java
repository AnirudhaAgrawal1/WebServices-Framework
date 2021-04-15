package com.thinking.machines.webrock.tools;
import com.thinking.machines.webrock.model.*;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.pojo.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import java.lang.annotation.*;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.*;
import java.io.BufferedReader;
import java.io.*;
import java.util.*;
public class ServiceDoc
{
public static void main(String gg[])
{
try
{
String path="";
List<Service> loadOnStartup=new ArrayList<>();
RandomAccessFile randomAccessFile=null;
WebRockModel webRockModel=new WebRockModel();
List<String> pathToTraverse=new LinkedList<>();
List<Error> errors=new LinkedList<>();
path=gg[0]+"\\";
File file=new File(path);
String[] folders=file.list();
String tmpPath;
for(String folder:folders)
{
tmpPath=path+folder+"\\";
pathToTraverse.add(tmpPath);
break;
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
addToDS(tmpPath,webRockModel,loadOnStartup,errors);
}
}
}
createServiceDoc(gg[1],webRockModel,loadOnStartup,errors);
}catch(Exception exception)
{
System.out.println(exception);
}
}





public static void addToDS(String path,WebRockModel webRockModel,List loadOnStartup,List<Error> errors)
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
Error error=null;
for(Method method:methods)
{
onStartupAnnotation=method.getAnnotation(onStartup.class);
if(classPathAnnotation!=null && onStartupAnnotation!=null && method.getAnnotation(Path.class)!=null)
{
service=new Service();
service.setServiceClass(c);
service.setService(method);
error.error="Both onStartup and Path annotation are applied it should contain either onStartup annotation or Path annotation.";
error.service=service;
errors.add(error);
continue;
}
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
if(method.getParameterCount()>0)
{
error.error="Parameter count is "+method.getParameterCount()+" it should be zero.";
error.service=service;
errors.add(error);
continue;
}
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
error.error="Request parameter annotation can only be applied on parameters having primitive datatype.";
error.service=service;
errors.add(error);
continue;
}
}
}
if(found==true) continue;
for(int z=1;z<parameterTypes.length;z++)
{
parameterType=parameterTypes[z].getSimpleName();
if(parameterType.equals("RequestScope")==false && parameterType.equals("SessionScope")==false && parameterType.equals("ApplicationScope")==false && parameterType.equals("ApplicationDirectory")==false) found=true;
}
if(found==true) 
{
error.error="Invalid parameters type.";
error.service=service;
errors.add(error);
continue;
}
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

public static boolean isPrimitive(String typeName)
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

public static void createServiceDoc(String path,WebRockModel webRockModel,List loadOnStartup,List<Error> errors)
{
try
{
File file=new File(path);
if(!file.exists()) file.getParentFile().mkdirs();
PdfWriter writer=new PdfWriter(path+"//ServiceDoc.pdf");
PdfDocument pdfDocument=new PdfDocument(writer);
Document document=new Document(pdfDocument);
PdfFont headerFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
Paragraph para=new Paragraph("Service Doc").setFont(headerFont);
para.setFontSize(24f);
para.setTextAlignment(TextAlignment.CENTER);
para.setBold();
document.add(para);
ServiceParameter serviceParameter;
InjectParameter injectParameter;
AutoWiring autoWiring;
if(loadOnStartup.size()>0)
{
para=new Paragraph("\n\n Startup Services \n\n").setFont(headerFont);
para.setFontSize(20f);
para.setTextAlignment(TextAlignment.LEFT);
para.setBold();
document.add(para);
}
Service service;
Table table;
float[] columnWidth={140f,350f};
int k;
for(int i=0;i<loadOnStartup.size();i++)
{
k=0;
service=(Service)loadOnStartup.get(i);
para=new Paragraph(service.getService().getName()+"\n");
para.setBold();
para.setFontSize(16f);
document.add(para);
table=new Table(columnWidth);
table.addCell(new Cell().add(new Paragraph("Class")).setBold());
table.addCell(new Cell().add(new Paragraph(service.getServiceClass().toString())));
table.addCell(new Cell().add(new Paragraph("Priority")).setBold());
table.addCell(new Cell().add(new Paragraph(Integer.toString(service.getPriority()))));
table.addCell(new Cell().add(new Paragraph("Parameters")).setBold());
table.addCell(new Cell().add(new Paragraph("No Parameters")));
table.addCell(new Cell().add(new Paragraph("Return type")).setBold());
table.addCell(new Cell().add(new Paragraph("Void")));
table.addCell(new Cell().add(new Paragraph("Inject properties")).setBold());
para=new Paragraph("");
if(service.getInjectApplicationScope()==true)
{
para.add("Application Scope");
k++;
}
if(service.getInjectApplicationDirectory()==true)
{
if(k!=0) para.add("\n");
para.add("Application Directory");
k++;
}
if(service.getInjectSessionScope()==true)
{
if(k!=0) para.add("\n");
para.add("Session Scope");
k++;
}
if(service.getInjectRequestScope()==true)
{
if(k!=0) para.add("\n");
para.add("Request Scope");
k++;
}
table.addCell(new Cell().add(para));
document.add(table);
}
para=new Paragraph("\n\n Services \n\n").setFont(headerFont);
para.setFontSize(20f);
para.setTextAlignment(TextAlignment.LEFT);
para.setBold();
document.add(para);
Collection list=webRockModel.model.values();
Iterator iter=list.iterator();
while(iter.hasNext())
{
service=(Service)iter.next();
para=new Paragraph(service.getService().getName()+"\n");
para.setBold();
para.setFontSize(16f);
document.add(para);
table=new Table(columnWidth);
table.addCell(new Cell().add(new Paragraph("URL")).setBold());
table.addCell(new Cell().add(new Paragraph(service.getPath().toString())));
table.addCell(new Cell().add(new Paragraph("Class")).setBold());
table.addCell(new Cell().add(new Paragraph(service.getServiceClass().toString())));
table.addCell(new Cell().add(new Paragraph("Is Secured")).setBold());
table.addCell(new Cell().add(new Paragraph(Boolean.toString(service.getIsSecured()))));
table.addCell(new Cell().add(new Paragraph("Method type")).setBold());
if(service.getIsPostAllowed()) table.addCell(new Cell().add(new Paragraph("POST")));
else table.addCell(new Cell().add(new Paragraph("GET")));
table.addCell(new Cell().add(new Paragraph("Parameters ")).setBold());
para=new Paragraph("");
int j=0;
while(j<service.getServiceParameters().size())
{
serviceParameter=(ServiceParameter)service.getServiceParameters().get(j);
if(serviceParameter.getParameterType().length()!=0)
{
if(j!=0) para.add("\n");
para.add(serviceParameter.getParameterType());
if(serviceParameter.getIsRequestParameterAnnotationApplied()) para.add(" (RequestParameter)");
j++;
}
if(serviceParameter.getIsJson())
{
if(j!=0) para.add("\n");
para.add(serviceParameter.getJsonParameterType().toString());
j++;
}
if(serviceParameter.getIsSessionScope())
{
if(j!=0) para.add("\n");
para.add("SessionScope");
j++;
}
if(serviceParameter.getIsRequestScope())
{
if(j!=0) para.add("\n");
para.add("RequestScope");
j++;
}if(serviceParameter.getIsApplicationScope())
{
if(j!=0) para.add("\n");
para.add("ApplicationScope");
j++;
}if(serviceParameter.getIsApplicationDirectory())
{
if(j!=0) para.add("\n");
para.add("ApplicationDirectory");
j++;
}
}
table.addCell(new Cell().add(para));
table.addCell(new Cell().add(new Paragraph("Return type")).setBold());
table.addCell(new Cell().add(new Paragraph(service.getService().getReturnType().toString())));
table.addCell(new Cell().add(new Paragraph("Inject properties ")).setBold());
para=new Paragraph("");
k=0;
if(service.getInjectApplicationScope()==true)
{
para.add("Application Scope");
k++;
}
if(service.getInjectApplicationDirectory()==true)
{
if(k!=0) para.add("\n");
para.add("Application Directory");
k++;
}
if(service.getInjectSessionScope()==true)
{
if(k!=0) para.add("\n");
para.add("Session Scope");
k++;
}
if(service.getInjectRequestScope()==true)
{
if(k!=0) para.add("\n");
para.add("Request Scope");
}
j=0;
while(j<service.getInjectParameters().size())
{
injectParameter=(InjectParameter)service.getInjectParameters().get(j);
if(j!=0) para.add("\n");
para.add(injectParameter.getField().getType()+" "+injectParameter.getAttribute()+" (Request parameter)");
}
table.addCell(new Cell().add(para));
table.addCell(new Cell().add(new Paragraph("Auto wired properties")).setBold());
j=0;
para=new Paragraph();
while(j<service.getAutoWiring().size())
{
autoWiring=(AutoWiring)service.getAutoWiring().get(j);
if(j!=0) para.add("\n");
para.add(autoWiring.getField()+" "+autoWiring.getName());
j++;
}
table.addCell(new Cell().add(para));
document.add(table);
}
para=new Paragraph("\n\n Errors \n\n").setFont(headerFont);
para.setFontSize(20f);
para.setTextAlignment(TextAlignment.LEFT);
para.setBold();
document.add(para);
float[] columnsWidth={170f,100f,300f};
table=new Table(columnsWidth);
if(errors.size()>0)
{
table.addCell(new Cell().add(new Paragraph("Class").setBold()));
table.addCell(new Cell().add(new Paragraph("Method").setBold()));
table.addCell(new Cell().add(new Paragraph("Error").setBold()));
}
int j=0;
Error error;
while(j<errors.size())
{
error=(Error)errors.get(j);
table.addCell(new Cell().add(new Paragraph(error.service.getServiceClass().toString())));
table.addCell(new Cell().add(new Paragraph(error.service.getService().getName())));
table.addCell(new Cell().add(new Paragraph(error.error)));
j++;
}
if(j>0) document.add(table);
document.close();
}catch(Exception exception)
{
System.out.println(exception);
}
}
class Error
{
public Service service;
String error;
}
}