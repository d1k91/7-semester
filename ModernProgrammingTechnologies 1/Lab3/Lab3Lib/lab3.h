#pragma once
#include <vector>
#include <string>
#include <stdexcept>
#include <cmath>

class lab3 {
public:
    int cyclicShift(int a, int n, const std::string& direction);
    long long fibonacci(int n);
    int removeDigits(int a, int p, int n);
    double sumAboveSecondaryDiagonalWithEvenIndexesSum(const std::vector<std::vector<double>>& A);
};