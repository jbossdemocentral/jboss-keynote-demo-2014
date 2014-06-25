package org.jboss.keynote.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.jboss.keynote.model.User;


public class ContactProcessor implements Processor{
	
	String email;
	String mobilePhone;
	String firstName;
	String lastName;
	String dept;
	
	
	 @Override
	 public void process(Exchange exchange) throws Exception {
		 // get the id of the input
		String clientId = exchange.getIn().getBody(String.class);
	    
		 
	    User customer = new User();
	    customer.setId(clientId);
	    customer.setEmail(this.email);
	    customer.setDept(this.dept);
	    customer.setFirstName(this.firstName);
	    customer.setLastName(this.lastName);
	    customer.setMobilePhone(this.mobilePhone);
	   
	    
	    exchange.getOut().setBody(customer);
	 }


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getMobilePhone() {
		return mobilePhone;
	}


	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public String getDept() {
		return dept;
	}


	public void setDept(String dept) {
		this.dept = dept;
	}
	 
	 
	 
}
