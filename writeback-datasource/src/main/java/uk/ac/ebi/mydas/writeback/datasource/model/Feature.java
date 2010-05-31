package uk.ac.ebi.mydas.writeback.datasource.model;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Feature {
	private Long id;
	private String featureId;
	private String label;
	private Type type;
	private Method method;
	private Integer start;
	private Integer stop;
	private Double score; 
	private Orientation orientation;
	private Phase phase;
	private List<String> notes;
	private Map<URL,String> links;
	private List<Target> targets;
	private List<String> parents;
	private List<String> parts;
	private Date datecreated;
	private Users user; 
	
	private Segment segment;
	
	public Feature(){}

	public Segment getSegment() {
		return segment;
	}

	public void setSegment(Segment segment) {
		this.segment = segment;
	}

	public Date getDatecreated() {
		return datecreated;
	}

	public void setDatecreated(Date datecreated) {
		this.datecreated = datecreated;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFeatureId() {
		return featureId;
	}
	public void setFeatureId(String featureId) {
		this.featureId = featureId;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public Integer getStart() {
		return start;
	}
	public void setStart(Integer start) {
		this.start = start;
	}
	public Integer getStop() {
		return stop;
	}
	public void setStop(Integer stop) {
		this.stop = stop;
	}
	public Double getScore() {
		return score;
	}
	public void setScore(Double score) {
		this.score = score;
	}
	public Orientation getOrientation() {
		return orientation;
	}
	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}
	public Phase getPhase() {
		return phase;
	}
	public void setPhase(Phase phase) {
		this.phase = phase;
	}
	public List<String> getNotes() {
		return notes;
	}
	public void setNotes(List<String> notes) {
		this.notes = notes;
	}
	public Map<URL, String> getLinks() {
		return links;
	}
	public void setLinks(Map<URL, String> links) {
		this.links = links;
	}
	public List<Target> getTargets() {
		return targets;
	}
	public void setTargets(List<Target> targets) {
		this.targets = targets;
	}
	public List<String> getParents() {
		return parents;
	}
	public void setParents(List<String> parents) {
		this.parents = parents;
	}
	public List<String> getParts() {
		return parts;
	}
	public void setParts(List<String> parts) {
		this.parts = parts;
	}
}
