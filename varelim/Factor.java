package varelim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class Factor {
    private ArrayList<ArrayList<String>> values = new ArrayList<>();
    private ArrayList<Double> prob = new ArrayList<>();
    private ArrayList<Variable> variables = new ArrayList<>();

    public Factor(Table table) {
        variables.add(table.getVariable());
        if (table.getParents() != null) {
            variables.addAll(table.getParents());
        }
        for (int i = 0; i < table.size(); i++) {
            values.add(table.getTable().get(i).getValues());
            prob.add(table.getTable().get(i).getProb());
        }
    }

    /**
     * Performs the reduction operation on the Factor
     *
     * @param v    The variable that needs to be removed
     * @param bool The value of v
     * @return The new factor with Variable v removed
     */

    public Factor reduction(Variable v, String bool){
        int index = variables.indexOf(v);
        Table table = new Table(new Variable("", new ArrayList<>()), new ArrayList<>());
        Factor factor = new Factor(table);
        factor.variables.clear();
        factor.getVariable().addAll(variables);
        for (int i = 0; i < values.size(); i++){
            if (values.get(i).get(index).equals(bool)){
                ArrayList<String> value = new ArrayList<>(values.get(i));
                factor.values.add(value);
                factor.prob.add(prob.get(i));
            }
        }
        for (int j = 0; j < factor.values.size(); j++){
            factor.values.get(j).remove(index);
        }

        factor.variables.removeIf(va -> va == v);

        return factor;
    }

    /**
     * Takes two Factors and applies the product operation on them to create a new Factor
     *
     * @param f2 The second Factor
     * @return The new Factor which is the product of the two Factors
     */
    public Factor product(Factor f2) {
        Table table = new Table(new Variable("", new ArrayList<>()), new ArrayList<>());
        Factor f3 = new Factor(table);
        f3.variables.clear();

        Variable same = null;
        ArrayList<Variable> different = new ArrayList<>();
        for (int x = 0; x < f2.variables.size(); x++) {
            if (variables.contains(f2.variables.get(x))) {
                same = f2.variables.get(x);
            }
            if (!variables.contains(f2.variables.get(x))) {
                different.add(f2.variables.get(x));
            }
        }

        int index1 = variables.indexOf(same);
        int index2 = f2.variables.indexOf(same);
        for (int i = 0; i < prob.size(); i++) {
            for (int j = 0; j < f2.prob.size(); j++) {
                if (values.get(i).get(index1).equals(f2.values.get(j).get(index2))) {
                    f3.prob.add(prob.get(i) * f2.prob.get(j));
                    ArrayList<String> list = new ArrayList<>(values.get(i));
                    for (Variable v : different){
                        list.add(f2.values.get(j).get(f2.variables.indexOf(v)));
                    }
                    f3.values.add(list);
                }
            }
        }
        f3.variables.addAll(variables);
        for (Variable v : different){
            if (!f3.variables.contains(v)){
                f3.variables.add(v);
            }
        }
        return f3;
    }

        /**
         * Sums out the given Variable from the Factor
         * @param v The Variable over which it needs to sum
         * @return The new Factor after Variable v has been summed out
         */
        public Factor marginalization (Variable v){
            Table table = new Table(new Variable("", new ArrayList<>()), new ArrayList<>());
            Factor factor = new Factor(table);
            factor.variables.clear();
            factor.variables.addAll(variables);
            factor.values.addAll(values);
            factor.prob.addAll(prob);
            int index = factor.variables.indexOf(v);
            for (int i = 0; i < factor.variables.size(); i++) {
                if (factor.variables.get(i) == v) {
                    factor.variables.remove(factor.variables.get(i));
                }
            }
            for (int j = 0; j < factor.values.size(); j++) {
                factor.values.get(j).remove(index);
            }
            ArrayList<ArrayList<String>> alreadyChecked = new ArrayList<>();
            ArrayList<Double> newProb = new ArrayList<>();
            for (int x = 0; x < factor.values.size(); x++) {
                if (!alreadyChecked.contains(factor.values.get(x))) {
                    double p = factor.prob.get(x);
                    alreadyChecked.add(factor.values.get(x));
                    for (int y = 0; y < factor.values.size(); y++) {
                        if (x != y) {
                            if (factor.values.get(x).equals(factor.values.get(y))) {
                                p = p + factor.prob.get(y);
                            }
                        }
                    }
                    newProb.add(p);
                }
            }
            factor.values = alreadyChecked;
            factor.prob = newProb;
            return factor;
        }

        public ArrayList<ArrayList<String>> getValues () {
            return values;
        }

        public ArrayList<Double> getProb () {
            return prob;
        }

        public ArrayList<Variable> getVariable () {
            return variables;
        }
    }
