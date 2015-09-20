//$Id$
package com.aws.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.aws.db.DataBaseOperation;

public class LoginAction 
{
	public static void main (String[] args)
        {
		try
		{
			//dboper.isAthenticated((String)request.getParameter("emailid"),(String)request.getParameter("password"));
                        DataBaseOperation.getInstance().isAthenticated("skprabu@gmail.com","test123");
			System.out.println("success");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("faliure");
		}
	}
}
