#include "pch.h"
#include "CppUnitTest.h"
#include "../Lab3Lib/lab3.h"

using namespace Microsoft::VisualStudio::CppUnitTestFramework;

namespace TestLab3
{
    TEST_CLASS(NumberOperationsTests)
    {
    private:
         lab3 l;

    public:

        TEST_METHOD(TestCyclicShift_LenEqualsOne)
        {
            int result = l.cyclicShift(0, 2, "left");
            Assert::AreEqual(0, result);
        }

        TEST_METHOD(TestCyclicShift_LeftShift)
        {
            int result = l.cyclicShift(123456, 2, "left");
            Assert::AreEqual(345612, result);
        }

        TEST_METHOD(TestCyclicShift_RightShift)
        {
            int result = l.cyclicShift(123456, 2, "right");
            Assert::AreEqual(561234, result);
        }

        TEST_METHOD(TestCyclicShift_ZeroShift)
        {
            int result = l.cyclicShift(123456, 0, "left");
            Assert::AreEqual(123456, result);
        }

        TEST_METHOD(TestCyclicShift_FullCycle)
        {
            int result = l.cyclicShift(123456, 6, "left");
            Assert::AreEqual(123456, result);
        }

        TEST_METHOD(TestCyclicShift_NegativeNumber)
        {
            int result = l.cyclicShift(-123456, 2, "left");
            Assert::AreEqual(-345612, result);
        }

        TEST_METHOD(TestCyclicShift_InvalidDirection)
        {
            auto func = [this]() { l.cyclicShift(123456, 2, "invalid"); };
            Assert::ExpectException<std::invalid_argument>(func);
        }

        TEST_METHOD(TestFibonacci_Zero)
        {
            long long result = l.fibonacci(0);
            Assert::AreEqual(0LL, result);
        }

        TEST_METHOD(TestFibonacci_One)
        {
            long long result = l.fibonacci(1);
            Assert::AreEqual(1LL, result);
        }

        TEST_METHOD(TestFibonacci_Two)
        {
            long long result = l.fibonacci(2);
            Assert::AreEqual(1LL, result);
        }

        TEST_METHOD(TestFibonacci_Five)
        {
            long long result = l.fibonacci(5);
            Assert::AreEqual(5LL, result);
        }

        TEST_METHOD(TestFibonacci_Ten)
        {
            long long result = l.fibonacci(10);
            Assert::AreEqual(55LL, result);
        }

        TEST_METHOD(TestFibonacci_Negative)
        {
            auto func = [this]() { l.fibonacci(-1); };
            Assert::ExpectException<std::invalid_argument>(func);
        }

        TEST_METHOD(TestRemoveDigits_ZeroNumber)
        {
            int result = l.removeDigits(0, 2, 3);
            Assert::AreEqual(0, result);
        }

        TEST_METHOD(TestRemoveDigits_MiddleDigits)
        {
            int result = l.removeDigits(123456, 3, 2);
            Assert::AreEqual(1256, result);
        }

        TEST_METHOD(TestRemoveDigits_StartDigits)
        {
            int result = l.removeDigits(123456, 1, 2);
            Assert::AreEqual(3456, result);
        }

        TEST_METHOD(TestRemoveDigits_EndDigits)
        {
            int result = l.removeDigits(123456, 5, 2);
            Assert::AreEqual(1234, result);
        }

        TEST_METHOD(TestRemoveDigits_AllDigits)
        {
            int result = l.removeDigits(123, 1, 3);
            Assert::AreEqual(0, result);
        }

        TEST_METHOD(TestRemoveDigits_ZeroDigits)
        {
            int result = l.removeDigits(123456, 3, 0);
            Assert::AreEqual(123456, result);
        }

        TEST_METHOD(TestRemoveDigits_NegativePosition)
        {
            int result = l.removeDigits(123456, -1, 2);
            Assert::AreEqual(123456, result);
        }

        TEST_METHOD(TestRemoveDigits_OutOfBounds)
        {
            int result = l.removeDigits(123456, 10, 2);
            Assert::AreEqual(123456, result);
        }

        TEST_METHOD(TestRemoveDigits_NegativeNumber)
        {
            int result = l.removeDigits(-123456, 3, 2);
            Assert::AreEqual(-1256, result);
        }

        TEST_METHOD(TestSumAboveSecondaryDiagonal_EmptyMatrix)
        {
            std::vector<std::vector<double>> emptyMatrix;
            double result = l.sumAboveSecondaryDiagonalWithEvenIndexesSum(emptyMatrix);
            Assert::AreEqual(0.0, result);
        }

        TEST_METHOD(TestSumAboveSecondaryDiagonal_1x1Matrix)
        {
            std::vector<std::vector<double>> matrix = { {5.0} };
            double result = l.sumAboveSecondaryDiagonalWithEvenIndexesSum(matrix);
            Assert::AreEqual(0.0, result);
        }

        TEST_METHOD(TestSumAboveSecondaryDiagonal_2x2Matrix)
        {
            std::vector<std::vector<double>> matrix = {
                {1.0, 2.0},
                {3.0, 4.0}
            };
            double result = l.sumAboveSecondaryDiagonalWithEvenIndexesSum(matrix);
            Assert::AreEqual(1.0, result);
        }

        TEST_METHOD(TestSumAboveSecondaryDiagonal_3x3Matrix)
        {
            std::vector<std::vector<double>> matrix = {
                {1.0, 2.0, 3.0},
                {4.0, 5.0, 6.0},
                {7.0, 8.0, 9.0}
            };
            double result = l.sumAboveSecondaryDiagonalWithEvenIndexesSum(matrix);
            Assert::AreEqual(1.0, result);
        }

        TEST_METHOD(TestSumAboveSecondaryDiagonal_NonSquareMatrix)
        {
            std::vector<std::vector<double>> matrix = {
                {1.0, 2.0, 3.0},
                {4.0, 5.0, 6.0}
            };
            auto func = [this, &matrix]() { l.sumAboveSecondaryDiagonalWithEvenIndexesSum(matrix); };
            Assert::ExpectException<std::invalid_argument>(func);
        }

        TEST_METHOD(TestSumAboveSecondaryDiagonal_4x4Matrix)
        {
            std::vector<std::vector<double>> matrix = {
                {1.0, 2.0, 3.0, 4.0},
                {5.0, 6.0, 7.0, 8.0},
                {9.0, 10.0, 11.0, 12.0},
                {13.0, 14.0, 15.0, 16.0}
            };
            double result = l.sumAboveSecondaryDiagonalWithEvenIndexesSum(matrix);
            Assert::AreEqual(19.0, result);
        }
    };
}