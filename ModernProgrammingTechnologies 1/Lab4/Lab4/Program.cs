using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lab4
{
    public class Matrix
    {
        private int[,] data;
        public int I { get; private set; }
        public int J { get; private set; }

        public Matrix(int m, int n)
        {
            if (m <= 0 || n <= 0)
            {
                throw new ArgumentException("Число строк и столбцов должно быть больше 0.");
            }
            data = new int[m, n];
            I = m;
            J = n;
        }

        public int this[int i, int j]
        {
            get
            {
                int effectiveI = i >= 0 ? i : I + i;
                int effectiveJ = j >= 0 ? j : J + j;

                if (effectiveI < 0 || effectiveI >= I || effectiveJ < 0 || effectiveJ >= J)
                {
                    throw new IndexOutOfRangeException("Индексы выходят за пределы матрицы.");
                }
                return data[effectiveI, effectiveJ];
            }
            set
            {
                int effectiveI = i >= 0 ? i : I + i;
                int effectiveJ = j >= 0 ? j : J + j;

                if (effectiveI < 0 || effectiveI >= I || effectiveJ < 0 || effectiveJ >= J)
                {
                    throw new IndexOutOfRangeException("Индексы выходят за пределы матрицы.");
                }
                data[effectiveI, effectiveJ] = value;
            }
        }

        public static Matrix operator +(Matrix a, Matrix b)
        {
            if (a.I != b.I || a.J != b.J)
            {
                throw new ArgumentException("Число строк и столбцов в суммируемых матрицах должно совпадать.");
            }
            Matrix result = new Matrix(a.I, a.J);
            for (int i = 0; i < a.I; i++)
            {
                for (int jj = 0; jj < a.J; jj++)
                {
                    result[i, jj] = a[i, jj] + b[i, jj];
                }
            }
            return result;
        }

        public static Matrix operator -(Matrix a, Matrix b)
        {
            if (a.I != b.I || a.J != b.J)
            {
                throw new ArgumentException("Число строк и столбцов в вычитаемых матрицах должно совпадать.");
            }
            Matrix result = new Matrix(a.I, a.J);
            for (int i = 0; i < a.I; i++)
            {
                for (int jj = 0; jj < a.J; jj++)
                {
                    result[i, jj] = a[i, jj] - b[i, jj];
                }
            }
            return result;
        }

        public static Matrix operator -(Matrix a)
        {
            Matrix result = new Matrix(a.I, a.J);
            for (int i = 0; i < a.I; i++)
            {
                for (int jj = 0; jj < a.J; jj++)
                {
                    result[i, jj] = -a[i, jj];
                }
            }
            return result;
        }

        public static Matrix operator *(Matrix a, Matrix b)
        {
            if (a.J != b.I)
            {
                throw new ArgumentException("Матрицы, участвующие в умножении, должны быть согласованными для этой операции по числу строк и столбцов.");
            }
            Matrix result = new Matrix(a.I, b.J);
            for (int i = 0; i < a.I; i++)
            {
                for (int jj = 0; jj < b.J; jj++)
                {
                    int sum = 0;
                    for (int k = 0; k < a.J; k++)
                    {
                        sum += a[i, k] * b[k, jj];
                    }
                    result[i, jj] = sum;
                }
            }
            return result;
        }

        public static bool operator ==(Matrix a, Matrix b)
        {
            if (ReferenceEquals(a, null) && ReferenceEquals(b, null)) return true;
            if (ReferenceEquals(a, null) || ReferenceEquals(b, null)) return false;
            if (a.I != b.I || a.J != b.J) return false;
            for (int i = 0; i < a.I; i++)
            {
                for (int jj = 0; jj < a.J; jj++)
                {
                    if (a[i, jj] != b[i, jj]) return false;
                }
            }
            return true;
        }

        public static bool operator !=(Matrix a, Matrix b)
        {
            return !(a == b);
        }

        public Matrix Transp()
        {
            Matrix result = new Matrix(J, I);
            for (int i = 0; i < I; i++)
            {
                for (int jj = 0; jj < J; jj++)
                {
                    result[jj, i] = this[i, jj];
                }
            }
            return result;
        }

        public int Min()
        {
            if (I == 0 || J == 0)
            {
                throw new InvalidOperationException("Матрица пуста.");
            }
            int min = data[0, 0];
            for (int i = 0; i < I; i++)
            {
                for (int jj = 0; jj < J; jj++)
                {
                    if (data[i, jj] < min)
                    {
                        min = data[i, jj];
                    }
                }
            }
            return min;
        }

        public override string ToString()
        {
            StringBuilder sb = new StringBuilder();
            sb.Append("{");
            for (int i = 0; i < I; i++)
            {
                if (i > 0) sb.Append(";\n");
                for (int jj = 0; jj < J; jj++)
                {
                    if (jj > 0) sb.Append(",");
                    sb.Append(data[i, jj]);
                }
            }
            sb.Append("}");
            return sb.ToString();
        }
    }
}
