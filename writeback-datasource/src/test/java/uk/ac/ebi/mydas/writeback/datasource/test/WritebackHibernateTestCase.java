package uk.ac.ebi.mydas.writeback.datasource.test;

import junit.framework.TestCase;


import uk.ac.ebi.mydas.writeback.datasource.hibernate.HibernateManager;
import uk.ac.ebi.mydas.writeback.datasource.model.Method;
import uk.ac.ebi.mydas.writeback.datasource.model.Target;
import uk.ac.ebi.mydas.writeback.datasource.model.Type;
import uk.ac.ebi.mydas.writeback.datasource.model.Users;


public class WritebackHibernateTestCase extends TestCase {
	public void testSavingType() {
		HibernateManager hibernate = new HibernateManager(); 
		Type type = new Type();
		type.setCategory("swissprot");
		type.setCvId("SO:12345");
		type.setLabel("JustGuessing");
		type.setTypeId("12345");
		type.setReference(true);
		
		type = hibernate.getType(type, true, true);
		
		assertEquals(type.getCategory(), "swissprot");
		assertEquals(type.getCvId(), "SO:12345");
		assertEquals(type.getLabel(), "JustGuessing");
		assertEquals(type.getTypeId(), "12345");
	}
	public void testSavingMethod() {
		HibernateManager hibernate = new HibernateManager(); 
		Method method=new Method();
		method.setCvId("ECO:12345");
		method.setLabel("MoreGuessing");
		method.setMethodId("12345");
		method = hibernate.getMethod(method, true, true);
		
		assertEquals(method.getCvId(), "ECO:12345");
		assertEquals(method.getLabel(), "MoreGuessing");
		assertEquals(method.getMethodId(), "12345");
	}
	public void testSavingTarget() {
		HibernateManager hibernate = new HibernateManager(); 
		Target target=new Target();
		
		target.setTargetId("0987");
		target.setLabel("TheNewTarget");
		target.setStart(10);
		target.setStop(20);

		target = hibernate.getTarget(target, true, true);
		
		assertEquals(target.getLabel(), "TheNewTarget");
		assertEquals(target.getTargetId(), "0987");
		assertEquals(target.getStart(), new Integer(10));
		assertEquals(target.getStop(), new Integer(20));
	}
	public void testCreateUser() {
		HibernateManager hibernate = new HibernateManager();
		Users user = hibernate.createUser("theuser", "thepassword");
		assertNotNull(user);
		assertEquals(user.getLogin(), "theuser");
	}
	public void testCreateUserWithSameLogin() {
		HibernateManager hibernate = new HibernateManager();
		Users user = hibernate.createUser("theuser", "theotherpassword");
		assertNull(user);
	}
	public void testAuthenticateUser() {
		HibernateManager hibernate = new HibernateManager();
		Users user = hibernate.authenticate("theuser", "thepassword");
		assertNotNull(user);
		assertEquals(user.getLogin(), "theuser");
	}
	public void testAuthenticateBadUser() {
		HibernateManager hibernate = new HibernateManager();
		Users user = hibernate.authenticate("theuser", "theotherpassword");
		assertNull(user);
	}

}
