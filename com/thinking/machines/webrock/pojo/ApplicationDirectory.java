package com.thinking.machines.webrock.pojo;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
public class ApplicationDirectory
{
private File directory;
ApplicationDirectory(File directory)
{
this.directory=directory;
}
public File getDirectory()
{
String path="";
// path=getServletContext().getRealPath(path);
return new File(path);
}
}