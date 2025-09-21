using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lab1
{
    public class ArrayOperations
    {
        public static double MultiplyEvenIndexedElements(double[] array)
        {
            if (array == null || array.Length == 0) return 0;

            double res = 1;

            for (int i = 0; i < array.Length; i += 2)
            {
                res *= array[i];
            }

            return res;
        }

        public static double[] CyclicShift(double[] array, int shift, string dirrection)
        {
            if (array == null || array.Length == 0)
            {
                Console.WriteLine("Array must exist and its lenght must be greater then 0!");
                return null;
            }
            if (array.Length == 1 || shift == 0) return (double[])array.Clone();

            int n = array.Length;
            double[] res = (double[])array.Clone();

            shift %= n;

            if (shift < 0) shift += n;

            if (dirrection == "r")
            {
                Shift(res, 0, n - 1);
                Shift(res, 0, shift - 1);
                Shift(res, shift, n - 1);
            }
            else if (dirrection == "l")
            {
                Shift(res, 0, n - 1);
                Shift(res, 0, n - shift - 1);
                Shift(res, n - shift, n - 1);
            }
            else
            {
                Console.WriteLine("Dirrection must be r or l!");
                return (double[])array.Clone();
            }
            return res;
        }

        public static void Shift(double[] array, int start, int end)
        {
            while (start < end)
            {
                double temp = array[start];
                array[start] = array[end];
                array[end] = temp;
                start++;
                end--;
            }
        }

        public static int FindMaxEvenValueAndIndex(int[] array)
        {
            if (array == null || array.Length == 0)
            {
                Console.WriteLine("Array must exist and its lenght must be greater then 0!");
                return int.MinValue;
            }
            int MaxValue = int.MinValue;
            bool found = false;

            for (int i = 0; i < array.Length; i += 2)
            {
                if (array[i] % 2 == 0)
                {
                    if (!found || array[i] > MaxValue)
                    {
                        MaxValue = array[i];
                        found = true;
                    }
                }
            }
            if (!found)
            {
                Console.WriteLine("\nВ массиве нет четных значений с четным индексом!");
            }
            return found ? MaxValue : int.MinValue;
        }
    }

    class Program
    {
        static void Main()
        {
            double[] arr = { 1.0, 2.0, 3.4, 1.1, 5.2, 9.1, 11.11 };
            double res1 = ArrayOperations.MultiplyEvenIndexedElements(arr);
            Console.WriteLine($"Произведение элементов с четными индексами: {res1}");

            Console.WriteLine("\nМассив до сдвига: " + string.Join("    ", arr.Select(n => n.ToString("F1"))));
            int shift = 3;
            double[] res2 = ArrayOperations.CyclicShift(arr, shift, "r");
            Console.WriteLine($"Массив после сдвига на {shift}: " + string.Join("    ", res2.Select(n => n.ToString("F1"))));

            int[] intArr = { 1, 2, 3, 4, 5, 6, 7, 8 };
            int maxEven = ArrayOperations.FindMaxEvenValueAndIndex(intArr);
            Console.WriteLine($"\nМаксимальное четное число с четным индексом: {maxEven}");
        }
    }
}
