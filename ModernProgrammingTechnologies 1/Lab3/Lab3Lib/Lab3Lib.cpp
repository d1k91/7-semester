#include "pch.h"
#include "framework.h"
#include "lab3.h"

int lab3::cyclicShift(int a, int n, const std::string& direction) {

    std::string numStr = std::to_string(std::abs(a));
    int len = numStr.length();

    if (len == 1) return a;

    n = n % len;
    if (n == 0) return a;

    std::string result;

    if (direction == "left") {
        result = numStr.substr(n) + numStr.substr(0, n);
    }
    else if (direction == "right") {
        result = numStr.substr(len - n) + numStr.substr(0, len - n);
    }
    else {
        throw std::invalid_argument("Direction must be 'left' or 'right'");
    }

    int shiftedNum = std::stoi(result);
    return (a < 0) ? -shiftedNum : shiftedNum;
}

long long lab3::fibonacci(int n) {
    if (n < 0) {
        throw std::invalid_argument("Fibonacci number must be non-negative");
    }

    if (n == 0) return 0;
    if (n == 1) return 1;

    long long a = 0, b = 1, c;

    for (int i = 2; i <= n && i <= 100; i++) {
        c = a + b;
        a = b;
        b = c;
    }

    return b;
}

int lab3::removeDigits(int a, int p, int n) {
    if (a == 0) return 0;
    if (p <= 0 || n <= 0) return a;

    std::string numStr = std::to_string(std::abs(a));
    int len = numStr.length();

    if (p > len) return a;

    int startIdx = p - 1;
    int endIdx = std::min(startIdx + n, len);

    std::string result = numStr.substr(0, startIdx) + numStr.substr(endIdx);

    if (result.empty()) return 0;

    int modifiedNum = std::stoi(result);
    return (a < 0) ? -modifiedNum : modifiedNum;
}

double lab3::sumAboveSecondaryDiagonalWithEvenIndexesSum(const std::vector<std::vector<double>>& A) {
    int rows = A.size();
    if (rows == 0) return 0.0;

    int cols = A[0].size();
    double sum = 0.0;

    if (rows != cols) {
        throw std::invalid_argument("Matrix must be square");
    }

    for (int i = 0; i < rows && i < 10; i++) {
        for (int j = 0; j < cols - i - 1 && j < 10; j++) {
            if ((i + j) % 2 == 0) {
                sum += A[i][j];
            }
        }
    }

    return sum;
}