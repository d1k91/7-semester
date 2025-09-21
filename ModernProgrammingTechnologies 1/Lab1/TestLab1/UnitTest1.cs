using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using Lab1;

namespace TestLab1
{
    [TestClass]
    public class MultiplyEvenIndexedElementsTest
    {
        [TestMethod]
        public void MultiplyEvenIndexedElements_NormalArray_CorrectReturn()
        {
            double[] array = { 1.0, 2.0, 3.0, 4.0, 5.0 };
            double result = ArrayOperations.MultiplyEvenIndexedElements(array);

            Assert.AreEqual(15.0, result);
        }

        [TestMethod]
        public void MultiplyEvenIndexedElements_EmptyArray()
        {
            double[] array = {};
            double result = ArrayOperations.MultiplyEvenIndexedElements(array);

            Assert.AreEqual(0, result);
        }

        [TestMethod]
        public void MultiplyEvenIndexedElements_ArrayLenEqOne()
        {
            double[] array = {7.5};
            double result = ArrayOperations.MultiplyEvenIndexedElements(array);

            Assert.AreEqual(7.5, result);
        }

        [TestMethod]
        public void MultiplyEvenIndexedElements_NullArray()
        {
            double[] array = null;
            double result = ArrayOperations.MultiplyEvenIndexedElements(array);

            Assert.AreEqual(0, result);
        }
    }

    [TestClass]
    public class CyclicShiftTest
    {
        [TestMethod]
        public void CyclicShift_NormalArray_CorrectReturn()
        {
            double[] array = { 1.0, 2.0, 3.0, 4.0, 5.0 };
            double[] expected = { 4.0, 5.0, 1.0, 2.0, 3.0 };
            double[] result = ArrayOperations.CyclicShift(array, 2, "r");

            CollectionAssert.AreEqual(expected, result);
        }

        [TestMethod]
        public void CyclicShift_EmtyArray()
        {
            double[] array = {};
            double[] expected = null;
            double[] result = ArrayOperations.CyclicShift(array, 2, "r");

            CollectionAssert.AreEqual(expected, result);
        }

        [TestMethod]
        public void CyclicShift_IncorrectDirrection()
        {
            double[] array = { 1.0, 2.0, 3.0, 4.0, 5.0 };
            double[] expected = { 1.0, 2.0, 3.0, 4.0, 5.0 };
            double[] result = ArrayOperations.CyclicShift(array, 2, "x");

            CollectionAssert.AreEqual(expected, result);
        }
    }

    [TestClass]
    public class FindMaxEvenValueAndIndexTest
    {
        [TestMethod]
        public void FindMaxEvenValueAndIndex_NormalArray_CorrectReturn()
        {
            int[] array = { 2, 3, 4, 5, 6, 7, 8 };
            int expected = 8;

            int res = ArrayOperations.FindMaxEvenValueAndIndex(array);

            Assert.AreEqual(expected, res);
        }

        [TestMethod]
        public void FindMaxEvenValueAndIndex_EmptyArray()
        {
            int[] array = {};
            int expected = int.MinValue;

            int res = ArrayOperations.FindMaxEvenValueAndIndex(array);

            Assert.AreEqual(expected, res);
        }

        [TestMethod]
        public void FindMaxEvenValueAndIndex_NoEven()
        {
            int[] array = { 1, 2, 3, 4, 5, 6, 7, 8 };
            int expected = int.MinValue;

            int res = ArrayOperations.FindMaxEvenValueAndIndex(array);

            Assert.AreEqual(expected, res);
        }
    }
}
