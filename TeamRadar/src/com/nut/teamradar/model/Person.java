package com.nut.teamradar.model;

public class Person {
	private Integer id;
	private String subscription;
	private String name;
	public Person()  
	{  
	}  
	
	public Person(Integer id, String name, String subscription) {  
	      
	    this.id = id;  
	    this.name = name;  
	    this.subscription = subscription;  
	} 
	
	public Integer getId() {  
	    return id;  
	}  
	
	public void setId(Integer id) {  
	    this.id = id;  
	}  
	
	public String getName() {  
	    return name;  
	}  
	
	public void setName(String name) {  
	    this.name = name;  
	}  
	  	  	
	public String getSubscription() {  
	    return subscription;  
	}  
	
	public void setSubscription(String subscription) {  
	    this.subscription = subscription;  
	}
	@Override  
	public String toString() {  
	    return "Person [id=" + id + ", name=" + name + ", subscription=" + subscription + "]";  
	} 	
}
