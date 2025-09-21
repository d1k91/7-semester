using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lab2
{
    public class LibraryFunctions
    {
        public static void SortDescending(ref int a, ref int b)
        {
            if (a < b) 
            {
                int temp = a; a = b; b = temp;
            }

        }

        public static void SortDescending(ref double a, ref double b)
        {
            if (a < b)
            {
                double temp = a; a = b; b = temp;
            }

        }

        public static long MultiplyEvenValues(int[,] A)
        {
            if (A == null) throw new ArgumentNullException(nameof(A), "Массив не может быть Null!");

            long res = 1;
            int rows = A.GetLength(0);
            int cols = A.GetLength(1);
            bool hasEvenNumbers = false;

            for (int i = 0; i < rows; i++) 
            {
                for (int j = 0; j < cols; j++) 
                { 
                    if ( A[i, j] %2 == 0)
                    {
                        res *= A[i, j];
                        hasEvenNumbers = true;
                    }
                }
            }

            if (!hasEvenNumbers) 
            {
                Console.WriteLine("В массиве нет четных значений!");
                return 0;
            }

            return res;
        }

        public static double SumEvenAboveSecondaryDiagonal(double[,] A)
        {
            if (A == null) throw new ArgumentNullException(nameof(A), "Массив не может быть Null!");

            double sum = 0;
            int n = A.GetLength(0);

            if (A.GetLength(1) != n) throw new ArgumentException("Массив должен быть квадратным!");

            for (int i = 0; i<= n; i++)
            {
                for (int j = 0;  j< n; j++)
                {
                    if ( i + j <= n - 1)
                    {
                        if (Math.Floor(A[i, j]) % 2  == 0)
                        {
                            sum += A[i, j];
                            Console.WriteLine(A[i, j]);
                        }
                    }
                }
            }

            return sum;
        }
    }
}
