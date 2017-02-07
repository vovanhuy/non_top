#include <iostream>
#include <iomanip>
#include <vector>
#include <string>
#include <sstream>

using namespace std;

vector<vector<int> > simplices;
int d;
int dimension;
string s;
int current_position = 0;

vector<int> copy_vec(vector<int> v){
	vector<int> v_copy(v.size());
	for(int i = 0; i < v.size(); i++){
		v_copy[i] = v[i];
	}
	return v_copy;
}

void build_simplices(){
	// d is the dimension of simplices
	for(int i = 1; i <= dimension + 1; i++){
		simplices.push_back(vector<int>{i});
	}
	for(int dim = 1; dim <= dimension; dim++){
		while(simplices[current_position].size() == dim){
			vector<int> v_copy = copy_vec(simplices[current_position++]);
			v_copy.push_back(0);
			for(int vertice = v_copy[v_copy.size() - 2] + 1; vertice <= dimension+1; vertice++){
				v_copy[v_copy.size() - 1] = vertice;
				simplices.push_back(copy_vec(v_copy));
			}
		}
	}
}

int main(int argc,char *argv[]){
	if(argc != 3){
		cout << "Please enter input and output files" << endl;
		return 1;
	}
	stringstream ss(argv[1]);
	ss >> dimension;
	dimension++;
	s = string(argv[2]);
	build_simplices();
	int end_position = 0;
	if(s.compare("ball") == 0) end_position = simplices.size();
	else if(s.compare("sphere") == 0) end_position = simplices.size() - 1;

	for(int i = 0; i < end_position; i++){
		cout << fixed << setprecision(1) << simplices[i].size() * 1.0 << " " << simplices[i].size() - 1 << " ";
		for(int j = 0; j < simplices[i].size(); j++){
			cout << simplices[i][j];
			if(j == simplices[i].size() - 1) cout << endl;
			else cout << " ";
		}
	}

}