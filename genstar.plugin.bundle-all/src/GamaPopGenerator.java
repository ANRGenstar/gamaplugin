/*********************************************************************************************
 *
 * 'GamaRegression.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package gamaplugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import core.metamodel.IAttribute;
import core.metamodel.pop.APopulationAttribute;
import core.metamodel.pop.io.GSSurveyWrapper;
import gospl.entity.attribute.GosplAttributeFactory;
import msi.gama.common.interfaces.IValue;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@vars({@var(name = "attributes", type = IType.LIST, of = IType.STRING, doc = {@doc("Returns the list of attribute names") }),
	 @var(name = "census_files", type = IType.LIST, of = IType.STRING, doc = {@doc("Returns the list of census files") }), 
	 @var(name = "generation_algo", type = IType.STRING, doc = {@doc("Returns the name of the generation algorithm") }),
	 @var(name = "mappers", type = IType.MAP, doc = {@doc("Returns the list of mapper") }),
	 @var(name = "spatial_file", type = IType.STRING, doc = {@doc("Returns the spatial file used to localize entities") }),
	 @var(name = "spatial_mapper_file", type = IType.LIST, of = IType.STRING, doc = {@doc("Returns the list of spatial files used to map the entities to areas") }),
	@var(name = "spatial_matcher_file", type = IType.STRING, doc = {@doc("Returns the spatial file used to match entities and areas") })
})
public class GamaPopGenerator implements IValue {

	// Setup the factory that build attribute
	GosplAttributeFactory attf ;

	// What to define in this configuration file
	List<GSSurveyWrapper> inputFiles;
	Set<APopulationAttribute> inputAttributes ;
	Map<String, IAttribute<? extends core.metamodel.IValue>> inputKeyMap ;
	boolean spatializePopulation;
	String generationAlgorithm;
	
	String spatialContingencyId;
	
	String pathNestedGeometries;
	List<String> pathsRegressionData;
	String pathCensusGeometries;
	String stringOfCensusIdInCSVfile;
	String stringOfCensusIdInShapefile;
	
	String crs;
	
	
	public GamaPopGenerator() {
		inputFiles = new ArrayList<>();
		inputAttributes = new HashSet<>();
		inputKeyMap = new HashMap<>();
		attf = new GosplAttributeFactory();
		generationAlgorithm = "IS";
		pathsRegressionData = new ArrayList<>();
	}
	
	
	
	@Override
	public String serialize(boolean includingBuiltIn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IType<?> getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IValue copy(IScope scope) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	public GosplAttributeFactory getAttf() {
		return attf;
	}

	public void setAttf(GosplAttributeFactory attf) {
		this.attf = attf;
	}

	public List<GSSurveyWrapper> getInputFiles() {
		return inputFiles;
	}

	public void setInputFiles(List<GSSurveyWrapper> inputFiles) {
		this.inputFiles = inputFiles;
	}

	public Set<APopulationAttribute> getInputAttributes() {
		return inputAttributes;
	}

	public void setInputAttributes(Set<APopulationAttribute> inputAttributes) {
		this.inputAttributes = inputAttributes;
	}

	public Map<String, IAttribute<? extends core.metamodel.IValue>> getInputKeyMap() {
		return inputKeyMap;
	}

	public void setInputKeyMap(Map<String, IAttribute<? extends core.metamodel.IValue>> inputKeyMap) {
		this.inputKeyMap = inputKeyMap;
	}





	public void setGenerationAlgorithm(String generationAlgorithm) {
		this.generationAlgorithm = generationAlgorithm;
	}



	


	public void setPathNestedGeometries(String pathGeometries) {
		this.pathNestedGeometries = pathGeometries;
		if (pathGeometries != null)
			spatializePopulation = true;
		else
			spatializePopulation = false;
	}



	


	public List<String> getPathsRegressionData() {
		return pathsRegressionData;
	}



	public void setPathsRegressionData(List<String> pathsRegressionData) {
		this.pathsRegressionData = pathsRegressionData;
	}



	


	public void setPathCensusGeometries(String pathCensusGeometries) {
		this.pathCensusGeometries = pathCensusGeometries;
	}



	public String getStringOfCensusIdInCSVfile() {
		return stringOfCensusIdInCSVfile;
	}



	public void setStringOfCensusIdInCSVfile(String stringOfCensusIdInCSVfile) {
		this.stringOfCensusIdInCSVfile = stringOfCensusIdInCSVfile;
	}



	public String getStringOfCensusIdInShapefile() {
		return stringOfCensusIdInShapefile;
	}



	public void setStringOfCensusIdInShapefile(String stringOfCensusIdInShapefile) {
		this.stringOfCensusIdInShapefile = stringOfCensusIdInShapefile;
	}



	public boolean isSpatializePopulation() {
		return spatializePopulation;
	}



	public void setSpatializePopulation(boolean spatializePopulation) {
		this.spatializePopulation = spatializePopulation;
	}



	public String getCrs() {
		return crs;
	}



	public void setCrs(String crs) {
		this.crs = crs;
	}



	public String getSpatialContingencyId() {
		return spatialContingencyId;
	}



	public void setSpatialContingencyId(String spatialContingencyId) {
		this.spatialContingencyId = spatialContingencyId;
	}
	
	
	@getter("attributes")
	public IList<String> getAttributeName(){
		IList<String> atts = GamaListFactory.create(Types.STRING);
		for (APopulationAttribute a : this.getInputAttributes()) atts.add(a.getAttributeName());
		return atts;
	}
	
	@getter("census_files")
	public IList<String> getCensusFile(){
		IList<String> f = GamaListFactory.create(Types.STRING);
		for (GSSurveyWrapper a : this.getInputFiles()) f.add(a.getAbsolutePath().toString());
		return f;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@getter("mappers")
	public GamaMap getMappers(){
		GamaMap<String, String> lm =GamaMapFactory.create(Types.STRING, Types.STRING);
		for (String a : this.getInputKeyMap().keySet()) {
			lm.put(a, this.getInputKeyMap().get(a).getAttributeName());
		}
		return lm;
	}
	
	@getter("spatial_mapper_file")
	public IList<String> getSpatialMapper(){
		return GamaListFactory.create(GAMA.getRuntimeScope(), Types.STRING, this.getPathsRegressionData());
	}
	@getter("spatial_matcher_file")
	public String getPathCensusGeometries() {
		return pathCensusGeometries;
	}


	@getter("generation_algo")
	public String getGenerationAlgorithm() {
		return generationAlgorithm;
	}
	
	@getter("spatial_file")
	public String getPathNestedGeometries() {
		return pathNestedGeometries;
	}

}
