package hr.fer.zemris.diglog.qmc;

import java.util.*;

/**
 * Created by bmihaela.
 */
public class MinimalShape {
	private List<Implicant> implicants;
	private Set<Integer> minterms;

	public MinimalShape(){
		implicants = new ArrayList<>();
		minterms = new HashSet<>();
	}

	public MinimalShape(Implicant implicant){
		this();
		implicants.add(implicant);
		minterms.addAll(implicant.getMintermsAndDontCares());
	}

	public MinimalShape(MinimalShape shape){
		this();
		implicants.addAll(shape.implicants);
		minterms.addAll(minterms);
	}

	public MinimalShape(List<Implicant> worthyImplicants) {
		this();
		this.addImplicants(worthyImplicants);
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		int size = implicants.size();
		boolean first = true;
		if(minterms.size() == Math.pow(2,implicants.get(0).getNumberOfVariables())){
			return "T";
		}
		for(int i = 0; i < size; i ++){
			String info = implicants.get(i).getVariableString(implicants.get(0).getNumberOfVariables());
			if(first){
				builder.append(info);
				first = false;
			}else {
				builder.append(" + ").append(info);
			}
		}
		return builder.toString();
	}

	public void addImplicant(Implicant implicant) {
		if(!implicants.contains(implicant)){
			implicants.add(implicant);
			minterms.addAll(implicant.getMintermsAndDontCares());
		}
	}

	public int getSize() {
		return implicants.size();
	}

	public void addImplicants(List<Implicant> worthyImplicants) {
		implicants.addAll(worthyImplicants);
		worthyImplicants.forEach(z -> minterms.addAll(z.getMintermsAndDontCares()));
	}

	public List<Implicant> getImplicants() {
		return implicants;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		MinimalShape that = (MinimalShape) o;
		return Objects.equals(implicants, that.implicants);
	}

	@Override
	public int hashCode() {
		return Objects.hash(implicants);
	}
}
