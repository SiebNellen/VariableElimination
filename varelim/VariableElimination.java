package varelim;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

public class VariableElimination {
    private ArrayList<Factor> factors;
    private ArrayList<Variable> variables;
    private ArrayList<Variable> observed;
    private Variable query;

    public VariableElimination(ArrayList<Factor> factors, ArrayList<Variable> variables, ArrayList<Variable> observed, Variable query) {
        this.factors = factors;
        this.variables = variables;
        this.observed = observed;
        this.query = query;
    }

    /**
     * Applies Variable elimination on the Factor given the observed and queried Variables
     *
     * @return The Factor of the queried Variable after Variable elimination has been done
     */

    public Factor elimination() {
        for (Factor f : factors) {
            System.out.println(f.getVariable());
            System.out.println(f.getValues());
            System.out.println(f.getProb());
            System.out.println();
        }
        Queue<Variable> eliminationList = new PriorityQueue<>();
        eliminationList = queueMaker(query, eliminationList);
        eliminationList.removeAll(observed);
        System.out.println("Elimination order is: ");
        for (Variable v : eliminationList) {
            System.out.print(v + ", ");
        }
        System.out.println();
        remove(eliminationList);
        eliminationList.remove(query);
        for (int i = 0; i < factors.size(); i++) {
            for (Variable v : observed) {
                if (factors.get(i).getVariable().contains(v)) {
                    System.out.println("Factor being reduced on (" + v + ") is:");
                    System.out.println(factors.get(i).getVariable());
                    System.out.println(factors.get(i).getValues());
                    System.out.println(factors.get(i).getProb());
                    System.out.println();

                    Factor f = factors.get(i).reduction(v, v.getObservedValue());
                    System.out.println("Result of reduction is:");
                    System.out.println(f.getVariable());
                    System.out.println(f.getValues());
                    System.out.println(f.getProb());
                    System.out.println();
                    factors.set(i, f);
                }
            }
        }

        for (Factor f : factors) {
            System.out.println(f.getVariable());
            System.out.println(f.getValues());
            System.out.println(f.getProb());
            System.out.println();
        }

        while (!eliminationList.isEmpty()) {
            Variable v = eliminationList.remove();
            int x = getFirst(factors, v);
            Factor factor = factors.get(x);
            for (int i = 0; i < factors.size(); i++) {
                if (factors.get(i).getVariable().contains(v) && i != x) {
                    System.out.println("Factors being multiplied are:");
                    System.out.println("1");
                    System.out.println(factor.getVariable());
                    System.out.println(factor.getValues());
                    System.out.println(factor.getProb());
                    System.out.println("2");
                    System.out.println(factors.get(i).getVariable());
                    System.out.println(factors.get(i).getValues());
                    System.out.println(factors.get(i).getProb());
                    System.out.println();
                    factor = factor.product(factors.get(i));
                    System.out.println("Result of product is:");
                    System.out.println(factor.getVariable());
                    System.out.println(factor.getValues());
                    System.out.println(factor.getProb());
                    System.out.println();
                }
            }
            factors.removeIf(f -> f.getVariable().contains(v));
            factors.add(factor);

            for (Factor f : factors) {
                System.out.println(f.getVariable());
                System.out.println(f.getValues());
                System.out.println(f.getProb());
                System.out.println();
            }
            factors.remove(factor);
            factors.add(factor);
            System.out.println("Factor being marginalized on (" + v + ") is:");
            System.out.println(factors.get(factors.size() - 1).getVariable());
            System.out.println(factors.get(factors.size() - 1).getValues());
            System.out.println(factors.get(factors.size() - 1).getProb());
            factors.set((factors.size() - 1), factors.get(factors.size() - 1).marginalization(v));
            System.out.println("Result of marginalization is:");
            System.out.println(factors.get(factors.size() - 1).getVariable());
            System.out.println(factors.get(factors.size() - 1).getValues());
            System.out.println(factors.get(factors.size() - 1).getProb());
            System.out.println();

            for (Factor f : factors) {
                System.out.println(f.getVariable());
                System.out.println(f.getValues());
                System.out.println(f.getProb());
                System.out.println();
            }
        }
        ArrayList<Variable> list = new ArrayList<>();
        list.add(query);
        for (Factor f : factors) {
            if (f.getVariable().equals(list)) {
                System.out.println("Variable elimination complete!");
                return f;
            }
        }
        System.out.println("Variable elimination has failed");
        return null;
    }

    /**
     * Creates the Queue of Variables which need to be eliminated i.e. the elimination list
     *
     * @param q    The Variable from which we want to find the parents
     * @param list The elimination list
     * @return The complete elimination list
     */
    public Queue<Variable> queueMaker(Variable q, Queue<Variable> list) {
        if (!q.hasParents())
            return list;
        else {
            list.addAll(q.getParents()); //needed to make the Variable class implement Comparable<Variable> to add to the queue
            for (int i = 0; i < q.getParents().size(); i++)
                list = queueMaker(q.getParents().get(i), list);
        }
        return list;
    }

    /**
     * Returns the first index of a Variable
     * @param factors list of factors
     * @param v Variable
     * @return index of the first factor in factors with variable v, -1 if it isn't in the list.
     */

    public int getFirst(ArrayList<Factor> factors, Variable v) {
        for (Factor f : factors) {
            if (f.getVariable().contains(v)) {
                return factors.indexOf(f);
            }
        }
        return -1;
    }

    /**
     * Removes factors if it contains a specific Variable
     * @param list
     */

    public void remove(Queue<Variable> list) {
        list.add(query);
        variables.removeAll(list);
        variables.removeAll(observed);
        for (int i = 0; i < factors.size(); i++) {
            for (int j = 0; j < variables.size(); j++) {
                if (factors.get(i).getVariable().contains(variables.get(j))) {
                    factors.remove(factors.get(i));
                }
            }
        }
    }
}
