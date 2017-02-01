import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.Vector;
import java.util.Iterator;
import java.util.Arrays;



class Simplex {
	float val;
	int dim;
	TreeSet<Integer> vert;

	Simplex(float val, int dim, int[] vert){
		this.val = val;
		this.dim = dim;
		this.vert = new TreeSet<Integer>();
		for(int i = 0; i <= dim; i++){
			this.vert.add(vert[i]);
		}
	}

	Simplex(Scanner sc){
		val = sc.nextFloat();
		dim = sc.nextInt();
		vert = new TreeSet<Integer>();
		for (int i=0; i<=dim; i++)
			vert.add(sc.nextInt());
	}

	static boolean isEqual(Simplex a, Simplex b){
		if(a.dim != b.dim) return false;

		Iterator<Integer> iter1 = a.vert.iterator(), iter2 = b.vert.iterator();
		for(int i = 0; i <= a.dim; i++){
			if(iter1.next() != iter2.next()) return false;
		}
		return true;
	}

	Integer[] getVerticeArray(){
		Integer[] verticeArray = new Integer[vert.size()];
		Iterator<Integer> iter = vert.iterator();
		for(int i = 0; i <= dim; i++){
			verticeArray[i] = iter.next();
		}
		return verticeArray;
	}

	public String toString(){
		return "{val="+val+"; dim="+dim+"; "+vert+"}\n";
	}

}

public class ReadFiltration {

	static Vector<Simplex> readFiltration (String filename) throws FileNotFoundException {
		Vector<Simplex> F = new Vector<Simplex>();
		Scanner sc = new Scanner(new File(filename));
		sc.useLocale(Locale.US);
		while (sc.hasNext())
			F.add(new Simplex(sc));
		sc.close();
		return F;
	}

	public static void main(String[] args) throws FileNotFoundException {
		if (args.length != 1) {
			System.out.println("Syntax: java ReadFiltration <filename>");
			System.exit(0);
		}
			
		System.out.println(readFiltration(args[0]));
		Simplex a = new Simplex(2, 2, new int[]{1, 2, 3});
		Simplex b = new Simplex(3, 2, new int[]{4, 1, 3});
		System.out.println(a);
		System.out.println(b);
		System.out.println(Simplex.isEqual(a, b));
		Integer[] verticeArray = a.getVerticeArray();
		System.out.println(Arrays.toString(verticeArray));
		a.dim--;
		for(int i = 0; i < verticeArray.length; i++){
			a.vert.remove(verticeArray[i]);
			System.out.println(Simplex.isEqual(a, new Simplex(1, 1, new int[]{1,2})));
			a.vert.add(verticeArray[i]);
		}
	}
}
