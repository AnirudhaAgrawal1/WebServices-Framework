# Web Services Framework
A java based web services framework, which provide an easier way to setup and create backend/server side of the web application.
## Benifits of using this framework
- No need to configure XML file (deployment descriptor).
- No need to write servlets classes.
- No need to worry about GET/POST request and how to handle them
- User can use ServicesDoc tool in framework to find list of services inside application.
- The framework generates a javascript file, Which contains the necessary implementation of all the service classes at backend/serverside.
- User can use ServicesDoc tool in framework to find list of services inside application.
## How to use this framework
### Setting up
1. Download TMWebRock.zip file.
2. Extract the zip file.
3. After extraction Cut/Copy that TMWebRock.jar file to tomcat9/webapps/"Project_Name"/WEB-INF/lib/.
4. Copy/cut web.xml to tomcat9/Webapps/"Project_Name"/WEB-INF/.
5. Do some canges in web.xml file.
   - You need to specify param-value against param-name 'SERVICE_PACKAGE_PREFIX'. In this project it is 'bobby'. User just need to change/write a single word inside web.xml and that was the value of param-name 'SERVICE_PACKAGE_PREFIX' by default there was "bobby", user have to change it. It is the package prefix like if package name is : "bobby.xyz.pqr" then you need to write 'bobby'. 'bobby' is the name of a folder.

   Note : The folder-name mentioned should exists inside tomcat9/Webapps/"project name"/WEB-INF/classes/.
   ```
   <context-param>
   <param-name>SERVICE_PACKAGE_PREFIX</param-name>
   <param-value>bobby</param-value>
   </context-param>
   ```
   User also have to change a single word inside web.xml, instead of 'school' user have to write application entity name there.
   ```
   <servlet>
   <servlet-name>TMWebRock</servlet-name>
   <servlet-class>com.thinking.machines.webrock.TMWebRock</servlet-class>
   </servlet>
   <servlet-mapping>
   <servlet-name>TMWebRock</servlet-name>
   <url-pattern>/school/*</url-pattern>
   </servlet-mapping>
   ```
6. Now copy all files inside Dependencies folder and paste them inside tomcat9/lib/. These are all the files you will ever need to create a web service. Our framework is Dependent on some of the these files. Some of the jar file may already be present there you can skip those files.

7. You are done setting up the environment, now you can use the framework easily.

## Other Features

### To generate documentation
There is a tool in this framework that generate the documentation. package name: com.thinking.machines.tools You have to pass two things as command line argument

First argument : The Path to the folder where package exists
Second arguement :Path where pdf will be saved.
Along with that you also need to mention all the jar files location in classpath.
```
java -classpath c:\tomcat9\webapps\myApp\WEB-INF\lib;c:\tomcat9\webapps\myApp\WEB-INF\classes;c:\tomcat9\lib\*;. ServiceDoc c:\tomcat9\webapps\myApp\WEB-INF\classes\ c:\tomcat9\webapps\myApp\
```
The pdf will be created in the specified folder

### To dynamically generate JavaScript file
1. You need to add one more param-name in web.xml as shown below.
```
<context-param>
<param-name>jsfile</param-name>
<param-value>abcd.js</param-value>
</context-param>
```
You need to specify the name for the dynamically generated js file as param-value of param-name "jsfile"

## Tutorials and reference documentation:
User can create Web service by using these annotations on class and methods. The user has to follow the specified guidelines regarding annotations mentioned below otherwise exception will be raised.
### Annotations:
Path annotation can be applied on class and method. The value of this annotation should always starts with front slash (/) followed by path.
```
import com.thinking.machines.webrock.annotations.*;
@Path("/employee")
public class Employee
{
@Path("/view")
public void view()
{
	System.out.println("View Service");
}
}
```
user can access this service by sending request to "User's entity name"/employee/view.

2. @RequestParameter("username")

RequestParameter annotation can only be applied on Parameter. User can use the following annotation to request data which arrives in the web request. Search for data with given entity in request Bag and provide if found. User is supposed to apply Request Parameter annotation on all the primitive types. Example:-
```
import com.thinking.machines.webrock.annotations.*;
@Path("/employee")
public class Employee
{
@Path("/add")
public String add(@RequestParameter("username") String name,@RequestParameter("gender") String gender,@RequestParameter("indian") boolean indian)
{
System.out.println("Name :"name+"\nGender :"+gender+"\nIs Iandian :"+indian);
return "Employee Added.";
}
}
```
Example url to access add service http://localhost:8080/"user's-application-context-name"/"user's-entity-name"/employee/add?username=Anirudha+Agrawal&gender=male&indian=true

3. @SecuredAccess(checkPost="bobby.Security.ValidateUser",guard="checkUser")

By using this annotation user dont have to write verification code for every service that need to be secured,user can just apply this annotation to all the services that are needed to be secured from unidentified access. SecuredAccess annotation can only be applied on Service/Method.

checkPost = full classname(with package) to your verification class.
guard = method name of verification class.

Example-
```
import com.thinking.machines.webrock.annotations.*;
@Path("/employee")
public class Employee
{
@Path("/get")
@SecuredAccess(checkPost="bobby.validateUser",guard="validate")
public String get()
{
System.out.println("Get method got invoked");
return "Employee ";
}
}
```
4. @Forward("/employee/view")
 
Using this annotation user can forward request to another web service or to some client side technology like (html files) .The example below shows, How to use forward annotaton to forward request to other service "/employee/view",you can also forward to some JSP also, by giving JSP file name as value of forward annotation. Forward annotation can only be applied on Services(Method which has path annotation applied on it).
Example-
```
import com.thinking.machines.webrock.annotations.*;
@Path("/employee")
@Get
public class Employee
{
@Path("/add")
@Get
@Forward("/employee/view")
public String add(@RequestParameter("username") String name,@RequestParameter("gender") String gender,@RequestParameter("indian") boolean indian)
{
System.out.println("Name :"name+"\nGender :"+gender+"\nIs Iandian :"+indian);
return "Employee Added";}
```

5. @Get
 
By using this annotation user is declaring that only GET type request allowed for this service. Get annotation can be applied to both class and method. If this annotation is applied on class then all the services inside that class can only accept GET type request.

6. @Post

Similarly as Get annotation, Post annotation can be used for allowing POST type request. and it can also applied on both class and method.

If neither Get nor Post annotation is applied on method then both GET and POST type requests allowed.
```
@Path("/view")
@Post
public void view()
{
System.out.println("View Service");
}
}
```
Framework provides three classes:

- RequestScope
- SessionScope
- ApplicationScope
If you want to use web application scopes . You can simply use these classes. All the above classes has two methods.

void setAttribute(String key,Object value);
Object getAttribute(String key);
For all the three classes there are three annotations:

7. @InjectApplicationScope

8. @InjectSessionScope

9. @InjectRequestScope

Example
```
package bobby.test;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.pojo.*;
@InjectApplicationScope
public class Test
{
private aaa a;
private ApplicationScope applicationScope;
@Path("/sam")
public void sam()
{
this.a=new aaa();
this.a.setData(50);
this.applicationScope.setAttribute("a_data",this.a);
}
public void setApplicationScope(ApplicationScope applicationScope)
{
this.applicationScope=applicationScope;
}
}
```
The class Test requires application scope. For that, the user has to declare a variable of type ApplicationScope along with the setter method as shown in the above code. Whenever there is a request arrived for sam, then before the invocation of sam service the setApplicationScope method got invoked.

Note: All this three class are in package : com.thinking.machines.webrock.pojo.*;

There is also a alternative approach to write that code. Instead of applying the InjectApplicationScope on that class you can simply introduce one more parameter in sam method of type Application Scope, As shown below in code.
```
package bobby.test;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.pojo.*;
public class Test
{
private aaa a;
@Path("/sam")
public void sam(ApplicationScope applicationScope)
{
this.a=new aaa();
this.a.setData(50);
applicationScope.setAttribute("a_data",this.a);
}
}
```
Similarly, The code can be written for RequestScope & SessionScope

10. @InjectRequestParameter("gender")

This annotation is same as RequestParameter annotation, but unlike it is applied on class properties. It works same as RequestParameter. The benefit of using this annotation is that, If some data is arriving through query string & more than one service of a particular class requires that data then instead of applying RequestParameter annotation on all services, the user can apply InjectRequestParameter annotation on that field, which is accessible to all services.

Example:-
```
import com.thinking.machines.webrock.annotations.*;
@Path("/employee")
public class EmployeeManager
{
@InjectRequestParameter("empId")
private int empId;
@Path("/getByEmployeeId")
public Employee getByEmployeeId()
{
int empId=this.empId;
//code to search the employee in Database
Employee emp=new Employee;
return emp;
}
@Path("/delete")
public void delete()
{
int empId=this.empId;
//code to delete empId from database;
}
public void setEmpid(int empId)
{
this.empId=empId;
}
}
```
Here, In the above code whenever the request arrives for any service in EmployeeManager class then At First, The data is extracted from query string & setEmpId method got invoked & after that the appropriate service got invoked.

If the user does not define the setter method, then exception will be raised.

11. @OnStartup(priority=1)

This annotation can only be applied on method/service. User uses this annotation if user wants that one or more service got called when server get started then he/she must apply this annotation over those services and user have to mention priority of calling this services, lesser priority number will called first.

Example :
```
package bobby.test;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.pojo.*;
public class Test
{
private Bulb bulb;
@Startup(priority=1)
public void sam(ApplicationScope applicationScope)
{
this.bulb=new Bulb();
this.bulb.setWattage(50);
applicationScope.setAttribute("bulb_data",this.bulb);
}
}
```

Note: You are not supposed to apply Path annotation with startup annotation. you cannot use RequestScope or SessionScope as a parameter. you can only use ApplicationScope as a parameter.

12. @AutoWired(name="")

AutoWired annotation can only be applied on properties of a service class. This annotation is used for binding the properties. If some data has been stored in some scope as key value pair. If the user wants that data, So, according to the key name the data will be extracted from the appropriate scope as value & that value will be assigned to the property which has AutoWired annotation applied on it. Setter method should be present for that property so that value can be setted. If the setter method is not found, then exception will be raised.

i.e. order of finding the value in the scope is: - Request scope -> Session scope -> Application scope.

Example :
```
import com.thinking.machines.webrock.annotations.*;
@Path("/employee")
public class Employee
{
@AutoWired(name="username")
private String name;
public void setName(String name)
{
this.name=name;
}
@Path("/get")
public String getName()
{
System.out.print("Name of employee: "+this.name);
return this.name;
}
}
```   
If the username key exists in any of these scope, then its value will be assigned to name attribute of Employee class.

13.If user send raw data in post Request user can just use it simply as:-
```
@Path("/add")
public Student add(Student s)
{
return s;
}
```
Framework will automaically find the type of parameter and parse the raw data into that type and pass it as argument while invoking the method.
