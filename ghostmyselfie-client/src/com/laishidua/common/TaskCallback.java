/* 
**
** Copyright 2014, Jules White
**
** 
*/
package com.laishidua.common;

public interface TaskCallback<T> {

    public void success(T result);

    public void error(Exception e);

}
