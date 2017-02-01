import java.util.Vector;
import java.util.LinkedList;
import java.util.Collections;
import java.io.FileNotFoundException;
import java.util.Comparator;

public class Accepted{
	Vector<Simplex> simplices;
	Vector<LinkedList<Integer>> matrix;

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
	}

	public void buildMatrix(){
		int n = simplices.size();
		matrix = new Vector<LinkedList<Integer>>();
		for(int i = 0; i < n; i++){
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
				for(int k = 0; k < n; k++){
					if(Simplex.isEqual(currentSimplex, simplices.get(k)) &&
						k != i){
						newColumn.add(k);
						break;
					}
				}
				currentSimplex.vert.add(verticeArray[j]);
			}
			// re-increase the dimension of currentSimplex
			currentSimplex.dim++;
			// add the new column to the matrix
			Collections.sort(newColumn);
			matrix.add(newColumn);
		}
	}

	public static void main(String[] args) throws FileNotFoundException{
		Accepted obj = new Accepted(args[0]);
		for(int i = 0; i < obj.simplices.size(); i++){
			System.out.println(obj.simplices.get(i));
		}
		obj.buildMatrix();
		for(int i = 0; i < obj.simplices.size(); i++){
			if(obj.matrix.get(i).size() == 0){
				System.out.println("Empty column");
				continue;
			}
			for(Integer el : obj.matrix.get(i)){
				System.out.print(el + " ");
			}
			System.out.println();
		}
	}

}