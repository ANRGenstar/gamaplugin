/*********************************************************************************************
 *
 * 'gamaplugin.CreateFromGenstarDelegate.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/

package gama.genstar.plugin;

import core.metamodel.IPopulation;
import core.metamodel.attribute.demographic.DemographicAttribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.value.IValue;
import msi.gama.common.interfaces.ICreateDelegate;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaMapFactory;
import msi.gaml.operators.Spatial;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.CreateStatement;
import msi.gaml.types.IType;
import spll.SpllEntity;
import spll.SpllPopulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
		IPopulation<ADemoEntity, DemographicAttribute<? extends IValue>> population = GenstarOperator.generatePop(scope, gen, number);
		if (gen == null) return false;
		
		final Collection<DemographicAttribute<? extends IValue>> attributes = population.getPopulationAttributes();
	    int nb = 0;
        List<ADemoEntity> es = new ArrayList(population);
        if (number > 0 && number < es.size()) es = scope.getRandom().shuffle(es);
        for (final ADemoEntity e : es) {
        	final Map map = (Map) GamaMapFactory.create();
        	if (population instanceof SpllPopulation) {
        		SpllEntity spllE = (SpllEntity) e;
        		if (spllE.getLocation() == null) continue;
        		map.put(IKeyword.SHAPE, new GamaShape(gen.getCrs() != null
						? Spatial.Projections.to_GAMA_CRS(scope, new GamaShape(spllE.getLocation()), gen.getCrs())
						: Spatial.Projections.to_GAMA_CRS(scope, new GamaShape(spllE.getLocation()))));
        	}
        	for (final DemographicAttribute<? extends IValue> attribute : attributes) {
        		map.put(attribute.getAttributeName(), e.getValueForAttribute(attribute));
        	}
        	statement.fillWithUserInit(scope, map);
    		inits.add(map);
            nb ++;
            if (number > 0 && nb >= number) break;
        }	
		return true;
	}

}
