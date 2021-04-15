package com.thinking.machines.webrock.exception;
public class ServiceException extends Exception implements java.io.Serializable
{
public ServiceException(String message)
{
super(message);
}
}