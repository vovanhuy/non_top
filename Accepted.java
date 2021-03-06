import java.util.Vector;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Collections;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.lang.Math;
import java.util.Arrays;
import java.util.Map;
import java.io.PrintWriter;
import java.io.File;

class Barcode{
    int dim;
    float left, right;
    public Barcode(int dim, float left, float right){
        this.dim = dim;
        this.left = left;
        this.right = right;
    }
    public String toString(){
        if(right < 0) return "" + dim + " " + left + " inf";
        else return "" + dim + " " + left + " " + right;
    }
}

public class Accepted{
    Vector<Simplex> simplices;
    int numOfSimplices;
    Vector<LinkedList<Integer>> matrix;
    TreeMap<Simplex, Integer> position;
    Vector<Barcode> barcode;
    int[] simplexDimension;

    public Accepted(String filename) throws FileNotFoundException{
        // sort simplices in increasing order of the time when
        // they appear
        simplices = ReadFiltration.readFiltration(filename);
        Collections.sort(simplices, new Comparator<Simplex>(){
            @Override
            public int compare(Simplex a, Simplex b){
                if(a.val != b.val) return Float.compare(a.val, b.val);
                else return Integer.compare(a.dim, b.dim);
            }
        });
        numOfSimplices = simplices.size();
        simplexDimension = new int[numOfSimplices];
        for(int i = 0; i < numOfSimplices; i++){
        	simplexDimension[i] = simplices.get(i).dim;
        }
    }

    private int maxDimension(){
    	int maxDim = 0;
    	for(int i = 0; i < numOfSimplices; i++){
    		if(maxDim < simplexDimension[i]) maxDim = simplexDimension[i];
    	}
    	return maxDim;
    }

    private void buildPosition(){
        // Initialise the TreeMap and define the comparator on simplices
        position = new TreeMap<Simplex, Integer>(new Comparator<Simplex>(){
            @Override
            public int compare(Simplex a, Simplex b){
                if(a.dim != b.dim) return Integer.compare(a.dim, b.dim);
                else{
                    Iterator<Integer> iter1 = a.vert.iterator();
                    Iterator<Integer> iter2 = b.vert.iterator();
                    int currentValue1 = -1, currentValue2 = -1;
                    for(int i = 0; i <= a.dim; i++){
                        currentValue1 = iter1.next();
                        currentValue2 = iter2.next();
                        if(currentValue1 != currentValue2)
                            return Integer.compare(currentValue1,
                                                   currentValue2);
                    }
                    return 0;
                }
            }
        });
        // add simplices to the TreeMap
        for(int i = 0; i < numOfSimplices; i++){
            position.put(new Simplex(simplices.get(i)), i);
        }
    }

    public void buildMatrix(){
        matrix = new Vector<LinkedList<Integer>>();
        buildPosition();
        for(int i = 0; i < numOfSimplices; i++){
            Simplex currentSimplex = simplices.get(i);
            // create the column in the matrix corresponding to
            // the (i+1)-th Simplex
            LinkedList<Integer> newColumn = new LinkedList<Integer>();
            // get an array of vertices of currentSimplex
            Integer[] verticeArray = currentSimplex.getVerticeArray();
            // temporarily decrease the dimension of currentSimplex to compare
            // with (dim-1)-simplex in the simplicial complexe
            currentSimplex.dim--;
            // try to remove each of the vertices in verticeArray from
            // currentSimplex and find the index of simplex obtained after
            // removing in simplices. We store only indexes of rows whose
            // corresponding element in the column is not 0.
            for(int j = 0; j < verticeArray.length; j++){
                currentSimplex.vert.remove(verticeArray[j]);
                Integer pos = position.get(currentSimplex);
                if(pos != null) newColumn.add(pos);
                currentSimplex.vert.add(verticeArray[j]);
            }
            // re-increase the dimension of currentSimplex
            currentSimplex.dim++;
            // we no longer need vertices of a simplex, so we clear 
            // vector containing its vertives to save memory 
            currentSimplex.vert.clear();
            // add the new column to the matrix
            Collections.sort(newColumn);
            matrix.add(newColumn);
        }
        position.clear();
    }

    public void reduceMatrix(){
        // Create a TreeSet of non-empty columns in matrix. Elements in this
        // TreeSet are sorted in decreasing order of their greatest elements.
        // Since each time we update a column, its low value increases. Storing
        // columns in such a TreeSet enables us not to revisit all columns after
        // each update
        Vector<TreeSet<Integer>> columnPriority = 
        										new Vector<TreeSet<Integer>>();
        for(int i = 0; i <= maxDimension(); i++){
            columnPriority.add(new TreeSet<Integer>(new Comparator<Integer>(){
                @Override
                public int compare(Integer a, Integer b){
                    if(matrix.get(a).getLast() < matrix.get(b).getLast())
                        return 1;
                    else if(matrix.get(a).getLast().intValue() == 
                    		matrix.get(b).getLast().intValue())
                        return 0;
                    else
                        return -1;
                }
            }));
        }

        for(int i = 0; i < numOfSimplices; i++){
        	// debug
            // if(i % 1000 == 0) System.out.println(i);
            LinkedList<Integer> currentColumn = matrix.get(i);
            // if the current column is empty, we have nothing to do
            if(currentColumn.size() == 0) continue;
            // check low values of all previous columns and do update if
            // necessary
            for(Integer col : columnPriority.get(simplexDimension[i])){
                LinkedList<Integer> previousColumn = matrix.get(col);
                // if(previousColumn.size() == 0) continue;
                // update column: elements of the current column after update
                // is the set of elements appear in currentColumn or
                // previousColumn but not both
                if(previousColumn.getLast().intValue() == currentColumn.getLast().intValue()){
                    LinkedList<Integer> newColumn = new LinkedList<Integer>();
                    Iterator<Integer> iter1 = currentColumn.iterator();
                    Iterator<Integer> iter2 = previousColumn.iterator();
                    int currentValue1 = -1, currentValue2 = -1;
                    while((iter1.hasNext() || currentValue1 != -1) && 
                          (iter2.hasNext() || currentValue2 != -1)){

                        if(currentValue1 == -1) 
                        	currentValue1 = iter1.next().intValue();
                        if(currentValue2 == -1) 
                        	currentValue2 = iter2.next().intValue();

                        int comp = Integer.compare(currentValue1,currentValue2);
                        // add the smaller one between currentValue1 and currentValue2 to
                        // newColumn
                        if(comp < 0){
                            newColumn.add(currentValue1);
                            currentValue1 = -1;
                        }
                        else if(comp > 0){
                            newColumn.add(currentValue2);
                            currentValue2 = -1;
                        }
                        else{
                            currentValue1 = -1;
                            currentValue2 = -1;
                        }

                    }
                    if(currentValue1 != -1) newColumn.add(currentValue1);
                    if(currentValue2 != -1) newColumn.add(currentValue2);
                    while(iter1.hasNext()){
                        newColumn.add(iter1.next());
                    }
                    while(iter2.hasNext()){
                        newColumn.add(iter2.next());
                    }

                    // update the i-th column
                    currentColumn = newColumn;
                }
                // early stopping
                else if(previousColumn.getLast().intValue() < 
                							currentColumn.getLast().intValue()){
                    break;
                }
                if(currentColumn.size() == 0) break;
            }
            // if currentColumn after all updates is non-empty, add it to the
            // TreeSet
            matrix.set(i, currentColumn);
            if(currentColumn.size() != 0) columnPriority.get(
            									simplexDimension[i]).add(i);
        }
    }

    public void buildBarcode(){
        // construct an array to store the pivot's 
        int[] pivot = new int[numOfSimplices];
        for(int i = 0; i < numOfSimplices; i++){
            pivot[i] = -1;
        }

        // build barcode for rows that have a pivot
        // fill the pivot array
        barcode = new Vector<Barcode>();
        for(int i = 0; i < numOfSimplices; i++){
            if(matrix.get(i).size() != 0){
                barcode.add(new Barcode(
                                simplices.get(
                                	matrix.get(i).getLast().intValue()
                                	).dim,
                                simplices.get(
                                	matrix.get(i).getLast().intValue()
                                	).val,
                                simplices.get(i).val)
                );
                pivot[matrix.get(i).getLast()] = i;
            }
        }

        // build barcode for columns which are zeroed out
        for(int i = 0; i < numOfSimplices; i++){
            if(pivot[i] == -1 && matrix.get(i).size() == 0) 
            	barcode.add(new Barcode(simplices.get(i).dim,
                                    simplices.get(i).val, - 1));
        }

        // sort barcode in natural increasing order
        Collections.sort(barcode, new Comparator<Barcode>(){
            @Override
            public int compare(Barcode a, Barcode b){
                if(a.dim != b.dim) return Integer.compare(a.dim, b.dim);
                else if(a.left != b.left) return Float.compare(a.left, b.left);
                else return Float.compare(a.right, b.right);
            }
        });
    }

	public void writeBarcode(String filename) {
        // write barcodes to a file.
		File f = new File(filename);
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(f);
			for(int i = 0; i < barcode.size(); i++){
				writer.println(barcode.get(i));
			}
		} catch (Exception e) {
			System.out.print("Error");
		} finally {
			if (writer != null)
				writer.close();
		}

	}




    public static void main(String[] args) throws FileNotFoundException{
        Accepted obj = new Accepted(args[0]);
        System.out.println("Number of simplices is " + obj.numOfSimplices);
        
        long startTime = System.currentTimeMillis();
        System.out.println("Building matrix");
        obj.buildMatrix();
        System.out.println("Finished building matrix");
        System.out.println("Time is " + 
                    (double)(System.currentTimeMillis()-startTime)/1000 + "s");
        

        startTime = System.currentTimeMillis();
        System.out.println("Reducing matrix");
        obj.reduceMatrix();
        System.out.println("Finished reducing matrix");
        System.out.println("Time is " + 
                    (double)(System.currentTimeMillis()-startTime)/1000 + "s");


        startTime = System.currentTimeMillis();
        System.out.println("Building barcode");
        obj.buildBarcode();
        System.out.println("Finished building barcode");
        System.out.println("Time is " + 
                    (double)(System.currentTimeMillis()-startTime)/1000 + "s");
        
        obj.writeBarcode(args[1]);

    }

}
