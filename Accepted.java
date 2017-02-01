import java.util.Vector;
import java.util.LinkedList;
import java.util.Collection;
import java.io.FileNotFoundException;

public class Accepted{
	Vector<Simplex> simplices;

	public Accepted(String filename) throws FileNotFoundException{
		simplices = ReadFiltration.readFiltration(filename);
		Collection.sort(simplices, new Comparator<Simplex>(){
			@Override
			int compare(Simplex a, Simplex b){
				if(a.val)
			}
		});
	}
}