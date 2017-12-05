package gama.genstar.plugin;

import core.configuration.GenstarConfigurationFile;
import core.metamodel.IPopulation;
import core.metamodel.attribute.demographic.DemographicAttribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.entity.AGeoEntity;
import core.metamodel.io.GSSurveyType;
import core.metamodel.io.GSSurveyWrapper;
import core.metamodel.io.IGSGeofile;
import core.metamodel.value.IValue;
import core.metamodel.value.numeric.RangeSpace;
import core.metamodel.value.numeric.RangeValue;
import core.util.data.GSDataParser;
import core.util.data.GSEnumDataType;
import core.util.excpetion.GSIllegalRangedData;
import gospl.GosplEntity;
import gospl.GosplPopulation;
import gospl.algo.sr.ISyntheticReconstructionAlgo;
import gospl.algo.sr.is.IndependantHypothesisAlgo;
import gospl.distribution.GosplContingencyTable;
import gospl.distribution.GosplInputDataManager;
import gospl.distribution.exception.IllegalControlTotalException;
import gospl.distribution.exception.IllegalDistributionCreation;
import gospl.distribution.matrix.INDimensionalMatrix;
import gospl.distribution.matrix.coordinate.ACoordinate;
import gospl.generator.DistributionBasedGenerator;
import gospl.generator.ISyntheticGosplPopGenerator;
import gospl.io.exception.InvalidSurveyFormatException;
import gospl.sampler.IDistributionSampler;
import gospl.sampler.ISampler;
import gospl.sampler.sr.GosplBasicSampler;
import msi.gama.common.util.FileUtils;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gaml.operators.Spatial;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.geotools.feature.SchemaException;
import org.opengis.referencing.operation.TransformException;
import spll.SpllEntity;
import spll.SpllPopulation;
import spll.algo.LMRegressionOLS;
import spll.algo.exception.IllegalRegressionException;
import spll.datamapper.exception.GSMapperException;
import spll.io.SPLGeofileBuilder;
import spll.io.SPLRasterFile;
import spll.io.SPLVectorFile;
import spll.io.exception.InvalidGeoFormatException;
import spll.popmapper.SPUniformLocalizer;
import spll.popmapper.normalizer.SPLUniformNormalizer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes", "unchecked"})
public class GenstarOperator {

	
	public static GSSurveyType toSurveyType(String type) {
		if (type.equals("ContingencyTable"))
			return GSSurveyType.ContingencyTable;
		if (type.equals("GlobalFrequencyTable"))
			return GSSurveyType.GlobalFrequencyTable;
		if (type.equals("LocalFrequencyTable"))
			return GSSurveyType.LocalFrequencyTable;
		return GSSurveyType.Sample;
	}
	
	public static GSEnumDataType toDataType(final IType type) {
		int t = type.id();
		if (t == IType.FLOAT)
			return GSEnumDataType.Continue;
		if (t == IType.INT)
			return GSEnumDataType.Integer;
		if (t == IType.BOOL)
			return GSEnumDataType.Boolean;
		return GSEnumDataType.Nominal;
	}
	
	@operator(value = "add_census_file", can_be_const = true, category = { "Gen*" }, concept = { "Gen*"})
	@doc(value = "add a census data file defined by its path (string), its type (\"ContingencyTable\", \"GlobalFrequencyTable\", \"LocalFrequencyTable\" or  \"Sample\"), its separator (string), the index of the first row of data (int) and the index of the first column of data (int) to a population_generator",
	examples = @example(value = "add_census_file(pop_gen, \"../data/Age_Couple.csv\", \"ContingencyTable\", \";\", 1, 1)", test = false))
	public static GamaPopGenerator addCensusFile(IScope scope, GamaPopGenerator gen, String path, String type, String csvSeparator, int firstRowIndex, int firstColumnIndex) throws GamaRuntimeException {
		Path completePath = Paths.get(FileUtils.constructAbsoluteFilePath(scope, path, false));
		gen.getInputFiles().add(new GSSurveyWrapper(completePath, toSurveyType(type), csvSeparator.isEmpty() ? ',':csvSeparator.charAt(0), firstRowIndex, firstColumnIndex));
		return gen;
	}
	
	
	@operator(value = "add_spatial_file", can_be_const = true, category = { "Gen*" }, concept = { "Gen*"})
	@doc(value = "add a spatial data file to locate the entities (nested geometries) defined by its path (string) to a population_generator",
	examples = @example(value = "add_spatial_file(pop_gen, \"../data/buildings.shp\")", test = false))
	public static GamaPopGenerator addGeographicFile(IScope scope, GamaPopGenerator gen, String path) throws GamaRuntimeException {
		String completePath = FileUtils.constructAbsoluteFilePath(scope, path, false);
		gen.setPathNestedGeometries(completePath);
		return gen;
	}
	
	@operator(value = "add_regression_file", can_be_const = true, category = { "Gen*" }, concept = { "Gen*"})
	@doc(value = "add a spatial regression data file defined by its path (string) to a population_generator",
	examples = @example(value = "add_regression_file(pop_gen, \"../data/landuse.tif\")", test = false))
	public static GamaPopGenerator addSpatialRegressionFile(IScope scope, GamaPopGenerator gen, String path) throws GamaRuntimeException {
		String completePath = FileUtils.constructAbsoluteFilePath(scope, path, false);
		gen.getPathsRegressionData().add(completePath);
		return gen;
	}
	
	@operator(value = "add_spatial_contingency_file", can_be_const = true, category = { "Gen*" }, concept = { "Gen*"})
	@doc(value = "add a spatial contingency data file defined by its path (string) and the name of the attribute containing the number used to place the entities to a population_generator",
	examples = @example(value = "add_spatial_contingency_file(pop_gen, \"../data/district.shp\", \"POP\")", test = false))
	public static GamaPopGenerator addSpatialContingencyFile(IScope scope, GamaPopGenerator gen, String path, String contingencyId) throws GamaRuntimeException {
		String completePath = FileUtils.constructAbsoluteFilePath(scope, path, false);
		gen.getPathsRegressionData().add(completePath);
		gen.setSpatialContingencyId(contingencyId);
		return gen;
	}
	
	
	@operator(value = "add_spatial_matcher", can_be_const = true, category = { "Gen*" }, concept = { "Gen*"})
	@doc(value = "add a spatial matcher data (link between the entities and the space) file defined by its path (string), the name of the key attribute in the entities and the name of the key attribute in the geographic file to a population_generator",
	examples = @example(value = "add_spatial_matcher(pop_gen, \"../data/iris.shp\", \"iris\",\"IRIS\")", test = false))
	public static GamaPopGenerator addSpatialMatcher(IScope scope, GamaPopGenerator gen, String path, String idInCensusFile, String isInShapefile) throws GamaRuntimeException {
		String completePath = FileUtils.constructAbsoluteFilePath(scope, path, false);
		gen.setPathCensusGeometries(completePath);
		gen.setStringOfCensusIdInCSVfile(idInCensusFile);
		gen.setStringOfCensusIdInShapefile(isInShapefile);
		return gen;
	}
	
	@operator(value = "add_mapper", can_be_const = true, category = { "Gen*" }, concept = { "Gen*"})
	@doc(value = "add a mapper between source of data for a attribute to a population_generator. A mapper is defined by the name of the attribute, the datatype of attribute (type), the corresponding value (map<list,list>) and the type of attribute (\"unique\" or \"range\")",
	examples = @example(value = " add_mapper(pop_gen, \"Age\", int, [[\"0 to 18\"]::[\"1 to 10\",\"11 to 18\"], [\"18 to 100\"]::[\"18 to 50\",\"51 to 100\"] , \"range\");", test = false))
	public static GamaPopGenerator addMapper(GamaPopGenerator gen, String referenceAttribute, IType dataType, GamaMap values, String attType) {
		if (gen == null) {
			gen = new GamaPopGenerator();
		}
		if (referenceAttribute == null) return gen;
		Optional<DemographicAttribute<? extends IValue>> attopt = gen.getInputAttributes().getAttributes()
				.stream().filter(a -> referenceAttribute.equals(a.getAttributeName())).findFirst();
		if (attopt.isPresent()) {
			DemographicAttribute<? extends IValue> att = attopt.get();
			Map<Collection<String>, Collection<String>> mapper = new Hashtable<>();
			for (Object k : values.keySet()) {
				Object v = values.get(GAMA.getRuntimeScope(), k);
				if (k instanceof Collection && v instanceof Collection) {
					Collection<String> key = new HashSet<String>((Collection)k);
					Collection<String> val = new HashSet<String>((Collection)v);
					mapper.put(key, val);
				}
			}
			try {
				
				String name = att.getAttributeName() + "_" + (gen.getInputAttributes().getAttributes().size() + 1);
				gen.getInputAttributes().addAttributes(gen.getAttf().createMappedAttribute(name, toDataType(dataType), att, mapper));
			} catch (GSIllegalRangedData e) {
				e.printStackTrace();
			}	
			
		}
		return gen;
	}
	

	@operator(value = "add_attribute", can_be_const = true, category = { "Gen*" }, concept = { "Gen*"})
	@doc(value = "add an attribute defined by its name (string), its datatype (type), its list of values (list) to a population_generator",
			examples = @example(value = "add_attribute(pop_gen, \"Sex\", string,[\"Man\", \"Woman\"]);", test = false))
	public static GamaPopGenerator addAttribute(GamaPopGenerator gen, String name, IType dataType, IList value) {
		return addAttribute(gen, name, dataType, value, null);
	}
	
	@operator(value = "add_attribute", can_be_const = true, category = { "Gen*" }, concept = { "Gen*"})
	@doc(value = "add an attribute defined by its name (string), its datatype (type), its list of values (list) and record name (name of the attribute to record) to a population_generator", examples = @example(value = "add_attribute(pop_gen, \"iris\", string,liste_iris, \"unique\", \"P13_POP\")", test = false))
	public static GamaPopGenerator addAttribute(GamaPopGenerator gen, String name, IType dataType, IList value, String record) {
		if (gen == null) {
			gen = new GamaPopGenerator();
		}
		try {
			DemographicAttribute<? extends IValue> attIris = gen.getAttf()
					.createAttribute(name, toDataType(dataType), value);
			 gen.getInputAttributes().addAttributes(attIris);
			 if (record != null && ! record.isEmpty()) {
				 gen.getInputAttributes().addAttributes(gen.getAttf()
						 .createRecordAttribute("population", GSEnumDataType.Integer,
								 attIris, Collections.emptyMap()));
			 }
		} catch (GSIllegalRangedData e) {
			e.printStackTrace();
		}
		return gen;
	}
	
	@operator(value = "with_generation_algo", can_be_const = true, category = { "Gen*" }, concept = { "Gen*"})
	@doc(value = "define the algorithm used for the population generation among: IS (independant hypothesis Algorothm) and simple_draw (simple draw of entities in a sample)",
			examples = @example(value = "my_pop_generator with_generation_algo \"simple_draw\"", test = false))
	public static GamaPopGenerator withGenerationAlgo(GamaPopGenerator gen, String algo) {
		if (gen == null) {
			gen = new GamaPopGenerator();
		}
		gen.setGenerationAlgorithm(algo);
		return gen;
	}
	
	public static IPopulation<ADemoEntity, DemographicAttribute<? extends IValue>> generatePop(final IScope scope, GamaPopGenerator gen, Integer targetPopulation) {
		if (gen == null) {
			return null;
		}
	
		GenstarConfigurationFile confFile = new GenstarConfigurationFile();
		confFile.setSurveyWrappers(gen.getInputFiles());
		confFile.setDemoDictionary(gen.getInputAttributes());

		GosplInputDataManager gdb = null;
       gdb = new GosplInputDataManager(confFile);
       IPopulation<ADemoEntity, DemographicAttribute<? extends IValue>> population = new GosplPopulation();
       if ("simple_draw".equals(gen.getGenerationAlgorithm())) {
    	
    	   try {
				gdb.buildSamples();
			} catch (final RuntimeException e) {
				e.printStackTrace();
			} catch (final IOException e) {
				e.printStackTrace();
			} catch (final InvalidSurveyFormatException e) {
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			}
    	   IPopulation p = gdb.getRawSamples().iterator().next();
	       if (targetPopulation <= 0)
	    	  return p;
	       List<ADemoEntity> popSample = new ArrayList<>(p);
	       for (int i= 0; i < targetPopulation; i++) {
	    	   ADemoEntity ent =  popSample.get(scope.getRandom().between(0, popSample.size()-1));
	    	   Map<DemographicAttribute<? extends IValue>, IValue> atts = ent.getAttributes().stream().collect(Collectors.toMap(a -> a, a -> ent.getValueForAttribute(a)));
	    	   ADemoEntity entity = new GosplEntity(atts);
	    	   population.add(entity);
	       }
	        
	   } else if ("IS".equals(gen.getGenerationAlgorithm())) {
		   try {
			   gdb.buildDataTables();
			} catch (final RuntimeException e) {
				e.printStackTrace();
			} catch (final IOException e) {
				e.printStackTrace();
			} catch (final InvalidSurveyFormatException e) {
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			}

			INDimensionalMatrix<DemographicAttribute<? extends IValue>, IValue, Double> distribution = null;
			try {
				distribution = gdb.collapseDataTablesIntoDistribution();
			} catch (final IllegalDistributionCreation e1) {
				e1.printStackTrace();
			} catch (final IllegalControlTotalException e1) {
				e1.printStackTrace();
			}
			
			// BUILD THE SAMPLER WITH THE INFERENCE ALGORITHM
			final ISyntheticReconstructionAlgo<IDistributionSampler> distributionInfAlgo = new IndependantHypothesisAlgo();
			ISampler<ACoordinate<DemographicAttribute<? extends IValue>, IValue>> sampler = null;
			try {
				sampler = distributionInfAlgo.inferSRSampler(distribution, new GosplBasicSampler());
			} catch (final IllegalDistributionCreation e1) {
				e1.printStackTrace();
			}
			
			if (targetPopulation < 0) {
				int min = Integer.MAX_VALUE;
				for (INDimensionalMatrix<DemographicAttribute<? extends IValue>,IValue,? extends Number> mat: gdb.getRawDataTables()) {
					if (mat instanceof GosplContingencyTable) {
						GosplContingencyTable cmat = (GosplContingencyTable) mat;
						min = Math.min(min, cmat.getMatrix().values().stream().mapToInt(v -> v.getValue()).sum());
					}
				}
				if (min < Integer.MAX_VALUE) {
					targetPopulation =min;
				} else targetPopulation = 1;
			}
			targetPopulation = targetPopulation <= 0 ? 1 : targetPopulation;
			
			// BUILD THE GENERATOR
			final ISyntheticGosplPopGenerator ispGenerator = new DistributionBasedGenerator(sampler);
			// BUILD THE POPULATION
			try {
				population = ispGenerator.generate(targetPopulation);
				
			} catch (final NumberFormatException e) {
				e.printStackTrace();
			}
	   }
       
       if (population == null) return null;
       if (gen.isSpatializePopulation())
			population = spatializePopulation(gen,population);
      
		return population;
	}
	
	@operator(value = "generate_localized_entities", can_be_const = true, category = { "Gen*" }, concept = { "Gen*"})
	@doc(value = "generate a spatialized population taking the form of a list of geometries while trying to infer the entities number from the data", examples = @example(value = "generateLocalizedEntities(my_pop_generator)", test = false))
	public static IList<IShape> generateLocalizedEntities(final IScope scope,GamaPopGenerator gen) {
		return generateLocalizedEntities(scope,gen, null);
	}
	
	@operator(value = "generate_localized_entities", can_be_const = true, category = { "Gen*" }, concept = { "Gen*"})
	@doc(value = "generate a population composed of the given number of entities taking the form of a list of geometries", examples = @example(value = "generateLocalizedEntities(my_pop_generator, 1000)", test = false))
	public static IList<IShape> generateLocalizedEntities(final IScope scope,GamaPopGenerator gen, Integer number) {
		if (number == null) number = -1;
		IPopulation<ADemoEntity, DemographicAttribute<? extends IValue>> population = generatePop(scope, gen, number);
		IList<IShape> entities =  GamaListFactory.create(Types.GEOMETRY);
		if (gen == null) return entities;
		final Collection<DemographicAttribute<? extends IValue>> attributes = population.getPopulationAttributes();
	    int nb = 0;
        List<ADemoEntity> es = new ArrayList(population);
        if (number > 0 && number < es.size()) es = scope.getRandom().shuffle(es);
        for (final ADemoEntity e : es) {
        	IShape entity = null;
        	if (population instanceof SpllPopulation) {
        		SpllEntity newE = (SpllEntity) e;
        		if (newE.getLocation() == null) continue;
        		entity = new GamaShape(gen.getCrs() != null ?
						Spatial.Projections.to_GAMA_CRS(scope, new GamaShape(newE.getLocation()), gen.getCrs())
						: Spatial.Projections.to_GAMA_CRS(scope, new GamaShape(newE.getLocation())));
        	} else 
        		entity = new GamaShape(Spatial.Punctal.any_location_in(scope, scope.getRoot().getGeometry()));
            		
        	for (final DemographicAttribute<? extends IValue> attribute : attributes)
                entity.setAttribute(attribute.getAttributeName(), e.getValueForAttribute(attribute));
            entities.add(entity);
            nb ++;
            if (number > 0 && nb >= number) break;
        }	
		return entities;
	}
	@operator(value = "generate_entities", can_be_const = true, category = { "Gen*" }, concept = { "Gen*"})
	@doc(value = "generate a population taking the form of of a list of map (each map representing an entity) while trying to infer the entities number from the data", examples = @example(value = "generate_entities(my_pop_generator)", test = false))
	public static IList<Map> generateEntities(final IScope scope,GamaPopGenerator gen) {
		return generateEntities(scope, gen, null);
	}
	@operator(value = "generate_entities", can_be_const = true, category = { "Gen*" }, concept = { "Gen*"})
	@doc(value = "generate a population composed of the given number of entities taking the form of a list of map: each map representing an entity", examples = @example(value = "generate_entities(my_pop_generator, 1000)", test = false))
	public static IList<Map> generateEntities(final IScope scope,GamaPopGenerator gen, Integer number) {
		if (number == null) number = -1;
		IPopulation<ADemoEntity, DemographicAttribute<? extends IValue>> population = generatePop(scope, gen, number);
		IList<Map> entities =  GamaListFactory.create(Types.MAP);
		if (gen == null) return entities;
		final Collection<DemographicAttribute<? extends IValue>> attributes = population.getPopulationAttributes();
      
        int nb = 0;
        List<ADemoEntity> es = new ArrayList(population);
        if (number > 0 && number < es.size()) es = scope.getRandom().shuffle(es);
        for (final ADemoEntity e : es) {
        	
        	Map entity =(Map) GamaMapFactory.create();
        	 		
        	for (final DemographicAttribute<? extends IValue> attribute : attributes) {
                final String name = attribute.getAttributeName();
                entity.put(name, e.getValueForAttribute(attribute));

            	if (population instanceof SpllPopulation) {
            		SpllEntity newE = (SpllEntity) e;
            		if (newE.getLocation() != null) {
            			entity.put("location", new GamaShape(new GamaShape(gen.getCrs() != null ?
								Spatial.Projections.to_GAMA_CRS(scope, new GamaShape(newE.getLocation()), gen.getCrs())
								: Spatial.Projections.to_GAMA_CRS(scope, new GamaShape(newE.getLocation())))));
            		}
            	}
            }
            entities.add(entity);
            nb ++;
            if (number > 0 && nb >= number) break;
        }	
		return entities;
	}
	
	
	private static IPopulation spatializePopulation(GamaPopGenerator gen, IPopulation population) {
	
		File sfGeomsF = gen.getPathNestedGeometries() == null ? null : new File(gen.getPathNestedGeometries());
		
		if (sfGeomsF != null && !sfGeomsF.exists()) return population;
		
		SPLVectorFile sfGeoms = null;
		SPLVectorFile sfCensus = null;

		File sfCensusF = gen.getPathCensusGeometries() == null ? null : new File(gen.getPathCensusGeometries());
		
		try {
			sfGeoms = new SPLGeofileBuilder().setFile(sfGeomsF).buildShapeFile();
			if (sfCensusF != null && sfCensusF.exists())
				sfCensus = new SPLGeofileBuilder().setFile(sfCensusF).buildShapeFile();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SchemaException e) {
			e.printStackTrace();
		} catch (InvalidGeoFormatException e) {
			e.printStackTrace();
		}

		gen.setCrs(sfGeoms.getWKTCoordinateReferentSystem());
		List<IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue>> endogeneousVarFile = new ArrayList<>();
		for(String path : gen.getPathsRegressionData()){
			try {
				File pathF = new File(path);
				if (pathF.exists())
					endogeneousVarFile.add(new SPLGeofileBuilder().setFile(pathF).buildGeofile());
			} catch (IllegalArgumentException | TransformException | IOException | InvalidGeoFormatException e2) {
				e2.printStackTrace();
			}
		}
		
		
		// SETUP THE LOCALIZER
		SPUniformLocalizer localizer = new SPUniformLocalizer(new SpllPopulation(population, sfGeoms));
		
		// SETUP GEOGRAPHICAL MATCHER
		// use of the IRIS attribute of the population
		if (sfCensus != null)
			localizer.setMatcher(sfCensus, gen.getStringOfCensusIdInCSVfile(), gen.getStringOfCensusIdInShapefile());
		
		// SETUP REGRESSION
		if (endogeneousVarFile != null && !endogeneousVarFile.isEmpty())
			try {
				if (gen.getSpatialContingencyId() != null && !gen.getSpatialContingencyId().isEmpty()) {
					localizer.setMapper(endogeneousVarFile.get(0), gen.getSpatialContingencyId());
				
				}
				else if (sfCensus != null)
					localizer.setMapper(endogeneousVarFile, new ArrayList<>(), 
						new LMRegressionOLS(), new SPLUniformNormalizer(0, SPLRasterFile.DEF_NODATA));
				
			} catch (IndexOutOfBoundsException | IllegalRegressionException e) {
				e.printStackTrace();
			} catch (TransformException e) {
				e.printStackTrace();
			} catch (SchemaException e) {
				e.printStackTrace();
			} catch (GSMapperException e) {
				e.printStackTrace();
			} catch (InvalidGeoFormatException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		//localize the population
		return localizer.localisePopulation();
	}
	

	    private static Object drawValue(final IScope scope, final IType<?> type, final RangeValue value) {
		Number[] range = value.getActualValue();
	        switch (type.id()) {
	            case IType.INT: {
	                final int lower = range[0].intValue();
	                final int upper = range[1].intValue();
	                return scope.getRandom().between(lower, upper);
	            }
	            case IType.FLOAT: {
	                final double lower = range[0].doubleValue();
	                final double upper = range[1].doubleValue();
	                return scope.getRandom().between(lower, upper);
	            }
	            default:
	                return null;
	        }
	    }

	    private static IType<?> getAttributeType(final DemographicAttribute<? extends IValue> attribute) {
	        final GSEnumDataType gsType = attribute.getValueSpace().getType();
	        switch (gsType) {
	            case Boolean:
	                return Types.BOOL;
	            case Integer:
	                return Types.INT;
				case Continue:
	                return Types.FLOAT;
				case Range:
					GSEnumDataType innerType = new GSDataParser().getValueType(
							((RangeSpace) attribute.getValueSpace()).getMin().toString());
					if(innerType.equals(GSEnumDataType.Integer))
						return Types.INT;
					else
						return Types.FLOAT;
	        }
	        return Types.STRING;
	    }

}
