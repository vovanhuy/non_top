import java.util.Vector;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Collections;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.lang.Math;

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
    // A map from a simplex to its position in simplices
    TreeMap<Simplex, Integer> position;
    Vector<LinkedList<Integer>> matrix;
    Vector<Barcode> barcode;

    public Accepted(String filename) throws FileNotFoundException{
        // sort simplices in increasing order of the time when 
        // they appear
        simplices = ReadFiltration.readFiltration(filename);
        Collections.sort(simplices, new Comparator<Simplex>(){
            @Override
            public int compare(Simplex a, Simplex b){
                if(a.val < b.val) return -1;
                else if(a.val == b.val) return 0;
                else return 1;
            }
        });
        numOfSimplices = simplices.size();
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
                    return Integer.compare(currentValue1, currentValue2);
                }
            }
        });
        // add simplices to the TreeMap
        for(int i = 0; i < numOfSimplices; i++){
            position.put(simplices.get(i), i);
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
            // add the new column to the matrix
            Collections.sort(newColumn);
            matrix.add(newColumn);
        }
    }

    public void reduceMatrix(){
        // Create a TreeSet of non-empty columns in matrix. Elements in this 
        // TreeSet are sorted in decreasing order of their greatest elements.
        // Since each time we update a column, its low value increases. Storing 
        // columns in such a TreeSet enables us not to revisit all columns after
        // each update  
        TreeSet<Integer> columnPriority = 
            new TreeSet<Integer>(new Comparator<Integer>(){
                @Override
                public int compare(Integer a, Integer b){
                    if(matrix.get(a).getLast() < matrix.get(b).getLast()) 
                        return 1;
                    else if(matrix.get(a).getLast() == matrix.get(b).getLast()) 
                        return 0;
                    else 
                        return -1;
                }
            });

        for(int i = 0; i < numOfSimplices; i++){
            LinkedList<Integer> currentColumn = matrix.get(i);
            // if the current column is empty, we don't add it to the TreeSet
            if(currentColumn.size() == 0) continue;
            // check low values of all previous columns and do update if 
            // necessary
            for(Integer col : columnPriority){
                LinkedList<Integer> previousColumn = matrix.get(col);
                if(previousColumn.size() == 0) continue;
                // update column: elements of the current column after update
                // is the set of elements appear in currentColumn or 
                // previousColumn but not both
                if(previousColumn.getLast() == currentColumn.getLast()){
                    // create an array to count the number of appearances of
                    // elements in two columns. All values in the array are
                    // initialised to 0.
                    int[] numAppearance = new int[numOfSimplices];
                    for(int j = 0; j < numOfSimplices; j++)
                        numAppearance[j] = 0;
                    // count the number of appearances
                    for(Integer el : previousColumn) numAppearance[el]++;
                    for(Integer el : currentColumn) numAppearance[el]++;
                    // update currentColumn
                    currentColumn.clear();
                    for(int j = 0; j < numOfSimplices; j++){
                        if(numAppearance[j] == 1) currentColumn.add(j);
                    }
                }
                if(currentColumn.size() == 0) break;
            }
            // if currentColumn after all updates is non-empty, add it to the
            // TreeSet
            if(currentColumn.size() != 0) columnPriority.add(i);
        }
    }

    public void buildBarcode(){
        // find pivots
        barcode = new Vector<Barcode>();
        for(int i = 0; i < numOfSimplices; i++){
            if(matrix.get(i).size() != 0){
                barcode.add(new Barcode(
                                simplices.get(matrix.get(i).getLast()).dim, 
                                simplices.get(matrix.get(i).getLast()).val, 
                                simplices.get(i).val)
                );
            }
        }

        // find zeroed out columns i such that row i doest not contain pivots 
        for(int i = 0; i < numOfSimplices; i++){
            if(matrix.get(i).size() == 0){
                // check if row i contains a pivot
                boolean inf = true;
                for(int col = 0; col < numOfSimplices; col++){
                    if(matrix.get(col).size() != 0 &&
                        matrix.get(col).getLast() == i){
                        inf = false;
                        break;
                    }
                }
                // assign right = -1 to signify infinity
                if(inf) barcode.add(new Barcode(simplices.get(i).dim, 
                                        simplices.get(i).val, - 1)
                );
            }
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





    public static void main(String[] args) throws FileNotFoundException{
        Accepted obj = new Accepted(args[0]);
        System.out.println(obj.numOfSimplices);
        // for(int i = 0; i < obj.numOfSimplices; i++){
        //  System.out.println(obj.simplices.get(i));
        // }
        System.out.println("Building matrix");
        obj.buildMatrix();
        System.out.println("Finished building matrix");

        // System.out.println("Matrix before reduction");
        // for(int i = 0; i < obj.numOfSimplices; i++){
        //  if(obj.matrix.get(i).size() == 0){
        //      System.out.println("Empty column");
        //      continue;
        //  }
        //  for(Integer el : obj.matrix.get(i)){
        //      System.out.print(el + " ");
        //  }
        //  System.out.println();
        // }
        // System.out.println("Matrix after reduction");
        System.out.println("Reducing matrix");
        obj.reduceMatrix();
        System.out.println("Finished reducing matrix");
        // for(int i = 0; i < obj.numOfSimplices; i++){
        //  if(obj.matrix.get(i).size() == 0){
        //      System.out.println("Empty column");
        //      continue;
        //  }
        //  for(Integer el : obj.matrix.get(i)){
        //      System.out.print(el + " ");
        //  }
        //  System.out.println();
        // }
        System.out.println("Building barcode");
        obj.buildBarcode();
        System.out.println("Finished building barcode");
        for(int i = 0; i < obj.barcode.size(); i++){
            System.out.println(obj.barcode.get(i));
        }
    }

}