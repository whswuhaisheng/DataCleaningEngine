package com.binggou.cloud.cleanengine;


import com.binggou.cloud.business.impl.ProjectCleanServiceImpl;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class App 
{
	public static ConfigurableApplicationContext context = null;
    public static void main(String[] args )
    {
    	context = new ClassPathXmlApplicationContext("spring-context.xml");
        System.out.println( "-------------Hello World!" );
        ProjectCleanServiceImpl dataCleanService = (ProjectCleanServiceImpl)context.getBean("dataCleanService");
        dataCleanService.dataCleaning();
        
    }
}
