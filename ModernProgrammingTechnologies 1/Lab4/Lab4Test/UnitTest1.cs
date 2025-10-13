using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using Lab4;

namespace Lab4Test
{
    [TestClass]
    public class MatrixTests
    {
        [TestMethod]
        [ExpectedException(typeof(ArgumentException))]
        public void Constructor_InvalidDimensions_ThrowsException()
        {
            new Matrix(0, 1);
        }

        [TestMethod]
        public void Constructor_ValidDimensions_CreatesMatrix()
        {
            Matrix m = new Matrix(1, 1);
            Assert.AreEqual(1, m.I);
            Assert.AreEqual(1, m.J);
            Assert.AreEqual(0, m[0, 0]);
        }

        [TestMethod]
        [ExpectedException(typeof(IndexOutOfRangeException))]
        public void Indexer_InvalidIndex_ThrowsException()
        {
            Matrix m = new Matrix(1, 1);
            int val = m[-2, 0];
        }

        [TestMethod]
        public void Indexer_ValidIndex_GetsAndSetsValue()
        {
            Matrix m = new Matrix(2, 2);
            m[0, 0] = 5;
            m[-1, -1] = 10;
            Assert.AreEqual(5, m[0, 0]);
            Assert.AreEqual(10, m[-1, -1]);
            Assert.AreEqual(10, m[1, 1]);
        }


        [TestMethod]
        [ExpectedException(typeof(ArgumentException))]
        public void OperatorPlus_DimensionsMismatch_ThrowsException()
        {
            Matrix a = new Matrix(1, 1);
            Matrix b = new Matrix(1, 2);
            Matrix c = a + b;
        }

        [TestMethod]
        public void OperatorPlus_SameDimensions1x1_AddsElements()
        {
            Matrix a = new Matrix(1, 1); a[0, 0] = 1;
            Matrix b = new Matrix(1, 1); b[0, 0] = 2;
            Matrix c = a + b;
            Assert.AreEqual(3, c[0, 0]);
        }

        [TestMethod]
        public void OperatorPlus_SameDimensions2x2_AddsElements()
        {
            Matrix a = new Matrix(2, 2); a[0, 0] = 1; a[0, 1] = 2; a[1, 0] = 3; a[1, 1] = 4;
            Matrix b = new Matrix(2, 2); b[0, 0] = 4; b[0, 1] = 3; b[1, 0] = 2; b[1, 1] = 1;
            Matrix c = a + b;
            Assert.AreEqual(5, c[0, 0]); Assert.AreEqual(5, c[0, 1]);
            Assert.AreEqual(5, c[1, 0]); Assert.AreEqual(5, c[1, 1]);
        }

        [TestMethod]
        [ExpectedException(typeof(ArgumentException))]
        public void OperatorMinusBinary_DimensionsMismatch_ThrowsException()
        {
            Matrix a = new Matrix(1, 1);
            Matrix b = new Matrix(1, 2);
            Matrix c = a - b;
        }

        [TestMethod]
        public void OperatorMinusBinary_SameDimensions1x1_SubtractsElements()
        {
            Matrix a = new Matrix(1, 1); a[0, 0] = 3;
            Matrix b = new Matrix(1, 1); b[0, 0] = 1;
            Matrix c = a - b;
            Assert.AreEqual(2, c[0, 0]);
        }

        [TestMethod]
        public void OperatorMinusBinary_SameDimensions2x2_SubtractsElements()
        {
            Matrix a = new Matrix(2, 2); a[0, 0] = 5; a[0, 1] = 5; a[1, 0] = 5; a[1, 1] = 5;
            Matrix b = new Matrix(2, 2); b[0, 0] = 1; b[0, 1] = 2; b[1, 0] = 3; b[1, 1] = 4;
            Matrix c = a - b;
            Assert.AreEqual(4, c[0, 0]); Assert.AreEqual(3, c[0, 1]);
            Assert.AreEqual(2, c[1, 0]); Assert.AreEqual(1, c[1, 1]);
        }

        [TestMethod]
        public void OperatorUnaryMinus_1x1_NegatesElements()
        {
            Matrix a = new Matrix(1, 1); a[0, 0] = 1;
            Matrix c = -a;
            Assert.AreEqual(-1, c[0, 0]);
            Assert.AreEqual(-1, c[-1, -1]);
        }

        [TestMethod]
        public void OperatorUnaryMinus_2x2_NegatesElements()
        {
            Matrix a = new Matrix(2, 2); a[0, 0] = 1; a[0, 1] = 2; a[1, 0] = 3; a[1, 1] = 4;
            Matrix c = -a;
            Assert.AreEqual(-1, c[0, 0]); Assert.AreEqual(-2, c[0, 1]);
            Assert.AreEqual(-3, c[1, 0]); Assert.AreEqual(-4, c[1, 1]);
            Assert.AreEqual(-4, c[-1, -1]);
            Assert.AreEqual(-1, c[-2, -2]);
        }

        [TestMethod]
        public void OperatorUnaryMinus_2x3_NegatesElementsWithNegativeIndices()
        {
            Matrix a = new Matrix(2, 3); 
            a[0, 0] = 1; a[0, 1] = 2; a[0, 2] = 3; 
            a[1, 0] = 4; a[1, 1] = 5; a[1, 2] = 6;
            Matrix c = -a;
            Assert.AreEqual(-1, c[0, 0]); 
            Assert.AreEqual(-2, c[0, 1]); 
            Assert.AreEqual(-3, c[0, 2]);
            Assert.AreEqual(-4, c[1, 0]); 
            Assert.AreEqual(-5, c[1, 1]); 
            Assert.AreEqual(-6, c[1, 2]);
            Assert.AreEqual(-6, c[-1, -1]);
            Assert.AreEqual(-1, c[-2, 0]);
            Assert.AreEqual(-3, c[0, -1]);
        }

        [TestMethod]
        [ExpectedException(typeof(ArgumentException))]
        public void OperatorMultiply_IncompatibleDimensions_ThrowsException()
        {
            Matrix a = new Matrix(1, 2);
            Matrix b = new Matrix(1, 2);
            Matrix c = a * b;
        }

        [TestMethod]
        public void OperatorMultiply_Compatible1x1_Multiplies()
        {
            Matrix a = new Matrix(1, 1); a[0, 0] = 2;
            Matrix b = new Matrix(1, 1); b[0, 0] = 3;
            Matrix c = a * b;
            Assert.AreEqual(6, c[0, 0]);
        }

        [TestMethod]
        public void OperatorMultiply_Compatible2x2_Multiplies()
        {
            Matrix a = new Matrix(2, 2); a[0, 0] = 1; a[0, 1] = 2; a[1, 0] = 3; a[1, 1] = 4;
            Matrix b = new Matrix(2, 2); b[0, 0] = 5; b[0, 1] = 6; b[1, 0] = 7; b[1, 1] = 8;
            Matrix c = a * b;
            Assert.AreEqual(19, c[0, 0]); Assert.AreEqual(22, c[0, 1]);
            Assert.AreEqual(43, c[1, 0]); Assert.AreEqual(50, c[1, 1]);
        }

        [TestMethod]
        public void OperatorMultiply_Compatible2x3_and_3x2_Multiplies()
        {
            Matrix a = new Matrix(2, 3); a[0, 0] = 1; a[0, 1] = 2; a[0, 2] = 3; a[1, 0] = 4; a[1, 1] = 5; a[1, 2] = 6;
            Matrix b = new Matrix(3, 2); b[0, 0] = 7; b[0, 1] = 8; b[1, 0] = 9; b[1, 1] = 10; b[2, 0] = 11; b[2, 1] = 12;
            Matrix c = a * b;
            Assert.AreEqual(58, c[0, 0]); Assert.AreEqual(64, c[0, 1]);
            Assert.AreEqual(139, c[1, 0]); Assert.AreEqual(154, c[1, 1]);
        }

        [TestMethod]
        public void OperatorEquals_BothNull_True()
        {
            Matrix a = null;
            Matrix b = null;
            Assert.IsTrue(a == b);
        }

        [TestMethod]
        public void OperatorEquals_OneNull_False()
        {
            Matrix a = new Matrix(1, 1);
            Matrix b = null;
            Assert.IsFalse(a == b);
        }

        [TestMethod]
        public void OperatorEquals_DimensionsMismatch_False()
        {
            Matrix a = new Matrix(1, 1);
            Matrix b = new Matrix(1, 2);
            Assert.IsFalse(a == b);
        }

        [TestMethod]
        public void OperatorEquals_SameDimensionsElementsDiffer_False()
        {
            Matrix a = new Matrix(1, 1); a[0, 0] = 1;
            Matrix b = new Matrix(1, 1); b[0, 0] = 2;
            Assert.IsFalse(a == b);
        }

        [TestMethod]
        public void OperatorEquals_SameDimensionsElementsSame_True()
        {
            Matrix a = new Matrix(2, 2); a[0, 0] = 1; a[0, 1] = 2; a[1, 0] = 3; a[1, 1] = 4;
            Matrix b = new Matrix(2, 2); b[0, 0] = 1; b[0, 1] = 2; b[1, 0] = 3; b[1, 1] = 4;
            Assert.IsTrue(a == b);
        }

        [TestMethod]
        public void Transp_1x1_Unchanged()
        {
            Matrix a = new Matrix(1, 1); a[0, 0] = 5;
            Matrix t = a.Transp();
            Assert.AreEqual(5, t[0, 0]);
            Assert.AreEqual(1, t.I); Assert.AreEqual(1, t.J);
        }

        [TestMethod]
        public void Transp_2x2_Transposes()
        {
            Matrix a = new Matrix(2, 2); a[0, 0] = 1; a[0, 1] = 2; a[1, 0] = 3; a[1, 1] = 4;
            Matrix t = a.Transp();
            Assert.AreEqual(1, t[0, 0]); Assert.AreEqual(3, t[0, 1]);
            Assert.AreEqual(2, t[1, 0]); Assert.AreEqual(4, t[1, 1]);
        }

        [TestMethod]
        public void Transp_2x1_TransposesTo1x2()
        {
            Matrix a = new Matrix(2, 1); a[0, 0] = 1; a[1, 0] = 2;
            Matrix t = a.Transp();
            Assert.AreEqual(1, t[0, 0]); Assert.AreEqual(2, t[0, 1]);
            Assert.AreEqual(1, t.I); Assert.AreEqual(2, t.J);
        }

        [TestMethod]
        public void Min_1x1_ReturnsSingleElement()
        {
            Matrix a = new Matrix(1, 1); a[0, 0] = -5;
            Assert.AreEqual(-5, a.Min());
        }

        [TestMethod]
        public void Min_2x2_MinInFirst_ReturnsMinNoUpdate()
        {
            Matrix a = new Matrix(2, 2); a[0, 0] = 1; a[0, 1] = 2; a[1, 0] = 3; a[1, 1] = 4;
            Assert.AreEqual(1, a.Min());
        }

        [TestMethod]
        public void Min_2x2_MinInLast_UpdatesMultipleTimes()
        {
            Matrix a = new Matrix(2, 2); a[0, 0] = 4; a[0, 1] = 3; a[1, 0] = 2; a[1, 1] = 1;
            Assert.AreEqual(1, a.Min());
        }

        [TestMethod]
        public void ToString_1x1_ReturnsCorrectFormat()
        {
            Matrix a = new Matrix(1, 1); a[0, 0] = 5;
            Assert.AreEqual("{5}", a.ToString());
        }

        [TestMethod]
        public void ToString_2x2_ReturnsCorrectFormat()
        {
            Matrix a = new Matrix(2, 2); a[0, 0] = 1; a[0, 1] = 2; a[1, 0] = 3; a[1, 1] = 4;
            Assert.AreEqual("{1,2;\n3,4}", a.ToString());
        }

        [TestMethod]
        public void ToString_1x2_ReturnsCorrectFormat()
        {
            Matrix a = new Matrix(1, 2); a[0, 0] = 1; a[0, 1] = -1;
            Assert.AreEqual("{1,-1}", a.ToString());
        }
    }
}
