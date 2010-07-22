package uk.ac.ebi.mydas.writeback.datasource.hibernate;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import uk.ac.ebi.mydas.writeback.datasource.model.Feature;
import uk.ac.ebi.mydas.writeback.datasource.model.Method;
import uk.ac.ebi.mydas.writeback.datasource.model.Orientation;
import uk.ac.ebi.mydas.writeback.datasource.model.Phase;
import uk.ac.ebi.mydas.writeback.datasource.model.Segment;
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
	public Feature addFeature(Feature feature, Session session){
		Long result = (Long) session.createQuery("SELECT max(id) FROM Feature").uniqueResult();
		result=(result==null)?0:result++;
		feature.setFeatureId("http://writeback/"+result);
		feature.setVersion(1);
		feature.setDatecreated(new Date());
		session.save(feature);
//		session.getTransaction().commit();		
		return feature;
	}
	public Feature updateFeature(Feature feature, Session session){
//		Session session = HibernateUtil.getSessionFactory().openSession();
//		session.beginTransaction();
		Feature result = (Feature) session.createQuery("FROM Feature WHERE featureId = ?").setString(0, feature.getFeatureId()).uniqueResult();
		if (result==null)
			feature.setVersion(1);
		else
			feature.setVersion(result.getVersion()+1);

		feature.setDatecreated(new Date());
		session.save(feature);
//		session.getTransaction().commit();		
		return feature;
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
	public Segment addFeaturesFromSegment(Segment segment,boolean addIfnotInDB,boolean updateIfDifferent){
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		Segment result = (Segment) session.createQuery("FROM Segment WHERE idSegment = ?").setString(0, segment.getIdSegment()).uniqueResult();
		if (result==null){
			if (addIfnotInDB){
				result=segment;
				Set<Feature> features = segment.getFeatures();
				result.setFeatures(new HashSet<Feature>());
				for(Feature featuretoadd:features){
					featuretoadd.setSegment(result);
					this.addFeature(featuretoadd,session);
					result.addFeature(featuretoadd);
				}
				session.save(result);
			}
		}else if (updateIfDifferent){
			
			result.setIdSegment(segment.getIdSegment());
			result.setLabel(segment.getLabel());
			result.setStart(segment.getStart());
			result.setStop(segment.getStop());
			result.setVersion(segment.getVersion());
			for(Feature featuretoadd:segment.getFeatures()){
				featuretoadd.setSegment(result);
				this.addFeature(featuretoadd,session);
				result.addFeature(featuretoadd);
			}
			session.update(result);
		}
		session.getTransaction().commit();		
		return result;
	}
	public Segment updateFeaturesFromSegment(Segment segment,boolean addIfnotInDB,boolean updateIfDifferent){
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		Segment result = (Segment) session.createQuery("FROM Segment WHERE idSegment = ?").setString(0, segment.getIdSegment()).uniqueResult();
		if (result==null){
			if (addIfnotInDB){
				result=segment;
				Set<Feature> features = segment.getFeatures();
				result.setFeatures(new HashSet<Feature>());
				for(Feature featuretoedit:features){
					featuretoedit.setSegment(result);
					this.updateFeature(featuretoedit,session);
					result.addFeature(featuretoedit);
				}
				session.save(result);
			}
		}else if (updateIfDifferent){
			
			result.setIdSegment(segment.getIdSegment());
			result.setLabel(segment.getLabel());
			result.setStart(segment.getStart());
			result.setStop(segment.getStop());
			result.setVersion(segment.getVersion());
			for(Feature featuretoedit:segment.getFeatures()){
				featuretoedit.setSegment(result);
				this.updateFeature(featuretoedit,session);
				result.addFeature(featuretoedit);
			}
			session.update(result);
		}
		session.getTransaction().commit();		
		return result;
	}
	public Segment deleteFeaturesFromSegment(Segment segment,boolean addIfnotInDB){
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		Segment result = (Segment) session.createQuery("FROM Segment WHERE idSegment = ?").setString(0, segment.getIdSegment()).uniqueResult();
		if (result==null){
			if (addIfnotInDB){
				result=segment;
				Set<Feature> features = segment.getFeatures();
				result.setFeatures(new HashSet<Feature>());
				for(Feature featuretoedit:features){
					featuretoedit.setSegment(result);
					featuretoedit.setDeleted(true);
					featuretoedit.setLabel("DELETED");
					featuretoedit.setPhase(Phase.PHASE_NOT_APPLICABLE);
					featuretoedit.setOrientation(Orientation.ORIENTATION_NOT_APPLICABLE);
					this.updateFeature(featuretoedit,session);
					result.addFeature(featuretoedit);
				}
				session.save(result);
			}
		}else {
			result.setIdSegment(segment.getIdSegment());
			for(Feature featuretoedit:segment.getFeatures()){
				featuretoedit.setDeleted(true);
				featuretoedit.setLabel("DELETED");
				featuretoedit.setPhase(Phase.PHASE_NOT_APPLICABLE);
				featuretoedit.setOrientation(Orientation.ORIENTATION_NOT_APPLICABLE);
				featuretoedit.setSegment(result);
				this.updateFeature(featuretoedit,session);
				result.addFeature(featuretoedit);
			}
			session.update(result);
		}
		session.getTransaction().commit();		
		return result;
	}
	public Segment getSegmentFromId(String segmentId) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		Segment result = (Segment) session.createQuery("FROM Segment WHERE idSegment = ?").setString(0, segmentId).uniqueResult();
		if (result==null){
			return null;
		}else{
//			DetachedCriteria subquery = DetachedCriteria.forClass(Feature.class, "f1");
//			subquery.setProjection(Projections.groupProperty("featureId"));
//			subquery.setProjection(Projections.max("version"));
//			subquery.setProjection(Property.forName("featureId"));
////			DetachedCriteria subquery2 = DetachedCriteria.forClass(Segment.class, "s");
////			subquery2.add(Restrictions.eq("s.idsegment", segmentId));
////			subquery.setProjection(Property.forName("id"));
//			Criteria criteria = session.createCriteria(Feature.class, "f");
////			criteria.add(Subqueries.propertyEq("featureid",subquery));
//			criteria.createAlias("segment", "s").add(Restrictions.eq("s.idSegment", segmentId));
//			criteria.add(Subqueries.propertyEq("featureId",subquery));
//			criteria.add(Subqueries.propertyEq("version",subquery));
//			Iterator<Feature> iterator = criteria.list().iterator();
			
			Iterator<Feature> iterator=session.createSQLQuery("SELECT f.* from feature f, (select max(f.version),f.featureid from segment_feature sf, feature f,segment s where sf.feature_id=f.id and sf.segment_id=s.id and s.idsegment=? group by f.featureid) f2 where f.version=f2.max and f.featureid=f2.featureid" ).addEntity(Feature.class).setString(0, segmentId).list().iterator();
			result.setFeatures(new HashSet<Feature>());
			while (iterator.hasNext()){
				result.addFeature((Feature)iterator.next());
			}
			return result;
		}
	}
}
