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

	Simplex(Simplex a){
		val = a.val;
		dim = a.dim;
		vert = new TreeSet<Integer>();
		for(Integer el : a.vert){
			vert.add(el);
		}
	}

	Simplex(Scanner sc){
		val = sc.nextFloat();
		dim = sc.nextInt();
		vert = new TreeSet<Integer>();
		for (int i=0; i<=dim; i++)
			vert.add(sc.nextInt());
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
		Integer[] verticeArray = a.getVerticeArray();
		System.out.println(Arrays.toString(verticeArray));
		a.dim--;
		for(int i = 0; i < verticeArray.length; i++){
			a.vert.remove(verticeArray[i]);
			a.vert.add(verticeArray[i]);
		}
	}
}
