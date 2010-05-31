package uk.ac.ebi.mydas.writeback.datasource.hibernate;

import org.hibernate.Session;

import uk.ac.ebi.mydas.writeback.datasource.model.Method;
import uk.ac.ebi.mydas.writeback.datasource.model.Target;
import uk.ac.ebi.mydas.writeback.datasource.model.Type;

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
}
