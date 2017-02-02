/*********************************************************************************************
 *
 * 'CreateFromGenstarDelegate.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package genstar.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import core.metamodel.IPopulation;
import core.metamodel.pop.APopulationAttribute;
import core.metamodel.pop.APopulationEntity;
import core.metamodel.pop.APopulationValue;
import msi.gama.common.interfaces.ICreateDelegate;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaMapFactory;
import msi.gaml.operators.Spatial;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.CreateStatement;
import msi.gaml.types.IType;
import spll.SpllPopulation;

/**
 * Class CreateFromDatabaseDelegate.
 *
 * @author Patrick Taillandier
 * @since 30 january 2017
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class CreateFromGenstarDelegate implements ICreateDelegate {

	public static IType type = new GamaPopGeneratorType();
	/**
	 * Method acceptSource()
	 *
	 * @see msi.gama.common.interfaces.ICreateDelegate#acceptSource(IScope, java.lang.Object)
	 */
	@Override
	public boolean acceptSource(IScope scope, final Object source) {
		return source instanceof GamaPopGenerator;
	}

	
	/**
	 * Method fromFacetType()
	 * 
	 * @see msi.gama.common.interfaces.ICreateDelegate#fromFacetType()
	 */
	@Override
	public IType fromFacetType() {
		if (type == null) type = new GamaPopGeneratorType();
		if (type.getName() == null) ((GamaPopGeneratorType) type).init();
		return type;
	}

	@Override
	public boolean createFrom(IScope scope, List<Map<String, Object>> inits, Integer number, Object source, Arguments init,
			CreateStatement statement) {
		GamaPopGenerator gen = (GamaPopGenerator) source;
		if (number == null) number = -1;
		IPopulation<APopulationEntity, APopulationAttribute, APopulationValue> population = GenstarOperator.generatePop(scope, gen, number);
		if (gen == null) return false;
		
		final Collection<APopulationAttribute> attributes = population.getPopulationAttributes();
	    int nb = 0;
        List<APopulationEntity> es = new ArrayList(population);
        if (number > 0 && number < es.size()) es = scope.getRandom().shuffle(es);
        for (final APopulationEntity e : es) { 	
        	final Map map = (Map) GamaMapFactory.create();
        	if (population instanceof SpllPopulation) {
        		if (e.getLocation() == null) continue;
        		map.put(IKeyword.SHAPE, new GamaShape(gen.getCrs() != null ? Spatial.Projections.to_GAMA_CRS(scope, new GamaShape(e.getLocation()), gen.getCrs()): Spatial.Projections.to_GAMA_CRS(scope, new GamaShape(e.getLocation()))));
        	}
        	for (final APopulationAttribute attribute : attributes) {
        		final String name = attribute.getAttributeName();
        		map.put(name, GenstarOperator.getAttributeValue(scope, e, attribute));
        	}
        	statement.fillWithUserInit(scope, map);
    		inits.add(map);
            nb ++;
            if (number > 0 && nb >= number) break;
        }	
		return true;
	}

}
