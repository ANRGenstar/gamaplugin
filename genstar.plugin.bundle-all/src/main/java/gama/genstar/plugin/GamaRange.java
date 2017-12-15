package main.java.gama.genstar.plugin;

import msi.gama.common.interfaces.IValue;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@vars({ @var(name = "min_value", type = IType.FLOAT),
@var(name = "max_value", type = IType.FLOAT)})
public class GamaRange implements IValue{

	Number min; Number max;
	
	public GamaRange(Number min, Number max) {
		this.min = min;
		this.max = max;
	}

	@getter("min_value")
	public Number getMin() {
		return min.doubleValue();
	}

	@getter("max_value")
	public Number getMax() {
		return max.doubleValue();
	}


	@Override
	public IType<?> getType() {
		return Types.get(GamaRangeType.id);
	}

	@Override
	public String serialize(boolean includingBuiltIn) {
		return min +"->" +max;
	}

	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		return serialize(true);
	}
	public String toString()  {
		return serialize(true);
	}

	@Override
	public IValue copy(IScope scope) throws GamaRuntimeException {
		return new GamaRange(min, max);
	}
}
