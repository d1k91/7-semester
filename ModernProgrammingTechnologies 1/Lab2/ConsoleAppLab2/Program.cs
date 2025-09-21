using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Lab2;

namespace ConsoleAppLab2
{
    class Program
    {
        static void Main(string[] args)
        {
            Console.WriteLine("Тестирование библиотеки классов:");
            Console.WriteLine("===============================\n");

            TestOrderDescending();

            TestMultiplyEvenValues();

            TestSumAboveSecondaryDiagonal();
        }

        static void TestOrderDescending()
        {
            Console.WriteLine("1. Тест упорядочивания чисел:");

            int a = 15, b = 8;
            Console.WriteLine($"До: a = {a}, b = {b}");
            LibraryFunctions.SortDescending(ref a, ref b);
            Console.WriteLine($"После: a = {a}, b = {b}");

            double x = 3.5, y = 7.2;
            Console.WriteLine($"До: x = {x}, y = {y}");
            LibraryFunctions.SortDescending(ref x, ref y);
            Console.WriteLine($"После: x = {x}, y = {y:F1}\n");
        }

        static void TestMultiplyEvenValues()
        {
            Console.WriteLine("2. Тест произведения четных значений:");

            int[,] array = {
                {2, 3, 4},
                {5, 6, 7},
                {8, 9, 10}
            };

            Console.WriteLine("Массив:");
            for (int i = 0; i < 3; i++)
            {
                for (int j = 0; j < 3; j++)
                {
                    Console.Write($"{array[i, j],4}");
                }
                Console.WriteLine();
            }

            long result = LibraryFunctions.MultiplyEvenValues(array);
            Console.WriteLine($"Произведение четных чисел: {result}\n");
        }

        static void TestSumAboveSecondaryDiagonal()
        {
            Console.WriteLine("3. Тест суммы выше побочной диагонали:");

            double[,] array = {
                {2.0, 3.5, 4.2, 5.0},
                {6.1, 7.8, 8.0, 9.3},
                {10.4, 11.0, 12.7, 13.2},
                {14.9, 15.6, 16.3, 17.0}
            };

            Console.WriteLine("Массив:");
            for (int i = 0; i < 4; i++)
            {
                for (int j = 0; j < 4; j++)
                {
                    Console.Write($"{array[i, j],6:F1}");
                }
                Console.WriteLine();
            }

            double result = LibraryFunctions.SumEvenAboveSecondaryDiagonal(array);
            Console.WriteLine($"Сумма четных на и выше побочной диагонали: {result:F2}\n");
        }
    }
}
