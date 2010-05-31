package uk.ac.ebi.mydas.writeback.datasource.hibernate;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.hibernate.Session;

import uk.ac.ebi.mydas.writeback.datasource.model.Method;
import uk.ac.ebi.mydas.writeback.datasource.model.Target;
import uk.ac.ebi.mydas.writeback.datasource.model.Type;
import uk.ac.ebi.mydas.writeback.datasource.model.Users;

public class HibernateManager {

	
	public Type getType(Type type,boolean addIfnotInDB,boolean updateIfDifferent){
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		Type result = (Type) session.createQuery("FROM Type WHERE typeid = ?").setString(0, type.getTypeId()).uniqueResult();
		if (result==null){
			if (addIfnotInDB){
				result=type;
				session.save(result);
			}
		}else if (updateIfDifferent){
			result.setCategory(type.getCategory());
			result.setCvId(type.getCvId());
			result.setLabel(type.getLabel());
			result.setTypeId(type.getTypeId());
			result.setReference(type.isReference());
			result.setSubparts(type.isSubparts());
			result.setSuperparts(type.isSuperparts());
			session.update(result);
		}
		session.getTransaction().commit();		
		return result;
	}
	public Method getMethod(Method method,boolean addIfnotInDB,boolean updateIfDifferent){
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		Method result = (Method) session.createQuery("FROM Method WHERE methodid = ?").setString(0, method.getMethodId()).uniqueResult();
		if (result==null){
			if (addIfnotInDB){
				result=method;
				session.save(result);
			}
		}else if (updateIfDifferent){
			result.setCvId(method.getCvId());
			result.setLabel(method.getLabel());
			result.setMethodId(method.getMethodId());
			session.update(result);
		}
		session.getTransaction().commit();		
		return result;
	}
	public Target getTarget(Target target,boolean addIfnotInDB,boolean updateIfDifferent){
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		Target result = (Target) session.createQuery("FROM Target WHERE targetid = ?").setString(0, target.getTargetId()).uniqueResult();
		if (result==null){
			if (addIfnotInDB){
				result=target;
				session.save(result);
			}
		}else if (updateIfDifferent){
			result.setTargetId(target.getTargetId());
			result.setLabel(target.getLabel());
			result.setStart(target.getStart());
			result.setStop(target.getStop());
			session.update(result);
		}
		session.getTransaction().commit();		
		return result;
	}
	public Users createUser(String username, String password){
		password = this.getMD5(password);
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		Users result = (Users) session.createQuery("FROM Users WHERE login = ?").setString(0, username).uniqueResult();
		if (result==null){
			result=new Users();
			result.setLogin(username);
			result.setPassword(password);
			session.save(result);
		}else{
			session.getTransaction().commit();		
			return null;
		}
		session.getTransaction().commit();		
		return result;
	}
	public Users authenticate(String username, String password){
		password = this.getMD5(password);
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		Users result = (Users) session.createQuery("FROM Users WHERE login = ? AND password =?").setString(0, username).setString(1, password).uniqueResult();
		session.getTransaction().commit();		
		return result;
		
	}	
	private String getMD5(String string){
		MessageDigest m;
		try {
			m = MessageDigest.getInstance("MD5");
			byte[] data = string.getBytes(); 
			m.update(data,0,data.length);
			BigInteger i = new BigInteger(1,m.digest());
			return String.format("%1$032X", i);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}

	}
}
