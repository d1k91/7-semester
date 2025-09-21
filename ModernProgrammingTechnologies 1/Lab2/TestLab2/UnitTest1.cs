using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using Lab2;

namespace TestLab2
{
    [TestClass]
    public class UnitTest1
    {
        
        [TestMethod]
        public void SortDescending_FirstGreaster()
        {
            int a = 10, b = 5;
            LibraryFunctions.SortDescending(ref a, ref b);

            Assert.AreEqual(10, a);
            Assert.AreEqual(5, b);
        }

        [TestMethod]
        public void SortDescending_FirstSmaller()
        {
            int a = 5, b = 10;
            LibraryFunctions.SortDescending(ref a, ref b);

            Assert.AreEqual(10, a);
            Assert.AreEqual(5, b);
        }

        [TestMethod]
        public void SortDescending_DoubleSwap()
        {
            double a = 5.2, b = 10.1;
            LibraryFunctions.SortDescending(ref a, ref b);

            Assert.AreEqual(10.1, a);
            Assert.AreEqual(5.2, b);
        }


        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void MultiplyEvenValues_NullArray_ThrowsException()
        {
            int[,] arr = null;

            LibraryFunctions.MultiplyEvenValues(arr);
        }

        [TestMethod]
        public void MultiplyEvenValues_NoEven()
        {
            int[,] array = {
                {1, 3, 5},
                {7, 9, 11}
            };

            long res = LibraryFunctions.MultiplyEvenValues(array);

            Assert.AreEqual(0, res);
        }

        [TestMethod]
        public void MultiplyEvenValues_WithEven()
        {
            int[,] array = {
                {2, 3, 4},
                {5, 6, 7}
            };
            long res = LibraryFunctions.MultiplyEvenValues(array);

            Assert.AreEqual(48, res);
        }


        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void SumEvenAboveSecondaryDiagonal_NullArray()
        {
            double[,] arr = null;
            LibraryFunctions.SumEvenAboveSecondaryDiagonal(arr);
        }

        [TestMethod]
        [ExpectedException(typeof(ArgumentException))]
        public void SumEvenAboveSecondaryDiagonal_NotSquareArray()
        {
            double[,] array = {
                {1.0, 2.0, 3.0},
                {4.0, 5.0, 6.0}
            };
            LibraryFunctions.SumEvenAboveSecondaryDiagonal(array);
        }

        [TestMethod]
        public void SumEvenAboveSecondaryDiagonal_WithElementsOnDiagonal()
        {
            double[,] array = {
                {2.0, 3.5, 4.2},
                {5.1, 6.8, 7.3},
                {8.9, 9.2, 10.0}
            };

            double res = LibraryFunctions.SumEvenAboveSecondaryDiagonal(array);

            Assert.AreEqual(21.9, res, 0.01);
        }
    }
}
