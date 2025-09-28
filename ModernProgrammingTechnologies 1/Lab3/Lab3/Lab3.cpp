#include <iostream>
#include "../Lab3Lib/lab3.h"

using namespace std;

int main()
{
	lab3 methods;

	int start = 123456;
	int shift = 2;
	string dir = "left";
	int res1 = methods.cyclicShift(start, 2, "left");

	cout << "CyclicShift(" << start<<", "<<shift<<", "<<dir<<")= "<< res1 << endl;

	int idxFibonacci = 10;
	cout << "Fibonacci(" << idxFibonacci << ") = " << methods.fibonacci(idxFibonacci)<<endl;

	int idxRemove = 2;
	int countRemove = 2;
	cout << "RemoveDigits(" << start << ", " << idxRemove << ", " << countRemove << ")= " << methods.removeDigits(start, idxRemove, countRemove)<<endl;

	vector<vector<double>> matrix = {
		{1.0, 2.0, 3.0},
		{4.0, 5.0, 6.0},
		{7.0, 8.0, 9.0}
	};

	cout << "SumAboveSecondaryDiagonal() = " << methods.sumAboveSecondaryDiagonalWithEvenIndexesSum(matrix);
}

