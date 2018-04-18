package main.java.gama.genstar.plugin.operators;

import core.metamodel.entity.ADemoEntity;
import main.java.gama.genstar.plugin.type.GamaPopGenerator;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.GamaPopulation;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gama.util.graph.GamaGraph;
import msi.gaml.species.GamlSpecies;
import msi.gaml.species.ISpecies;
import spin.algo.factory.SpinNetworkFactory;

public class GenstarGraphOperators {
	@operator(value = "add_graph", can_be_const = true, category = { "Gen*" }, concept = { "Gen*"})
	public static GamaPopGenerator addGraphGenerator(IScope scope, GamaPopGenerator gen, String graphName, String graphGenerator) throws GamaRuntimeException {
		gen.addNetworkGenerator(graphName, SpinNetworkFactory.getInstance().getSpinPopulationGenerator(graphName, graphGenerator));
		return gen;
	}
	
	public static GamaGraph get_graph(IScope scope, GamaPopGenerator gen, String networkName) {
		return null;
		//return gen.getNetwork(networkName);
	}
	
	@operator(value = "associate_population_agents", can_be_const = true, category = { "Gen*" }, concept = { "Gen*"})	
	public static GamaPopGenerator associatePopulation(IScope scope, GamaPopGenerator gen, GamlSpecies pop) {
	//	IContainer<?, ? extends IAgent> c = pop.getAgents(scope);
		Object[] entity = gen.getGeneratedPopulation().toArray();
		
		for(int i = 0 ; i < pop.getPopulation(scope).length(scope) ; i ++) {			
			IAgent agt = pop.getPopulation(scope).getAgent(i);
			gen.add( (ADemoEntity) entity[i], agt);
		}
		return gen;
	}	
}
