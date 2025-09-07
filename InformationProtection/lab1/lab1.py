import math
import random
from sympy import *

def get_bin(number):

    binary_digits = []

    while number != 0:
        last_bit = number & 1
        binary_digits.append(int(last_bit))
        number = number >> 1

    return binary_digits

def isPrimeFerma(p, k = 5):
    for i in range(k):
        a = random.randint(1,p)
        if  modular_exponentiation(a, p-1, p) == 1:
            continue
        else:
            return False
    return True
    
def row(a,p,t, info = false):
    nums = []
    for i in range(t+1):
        if i == 0:
            nums.append(a%p)
            if info:
                print(f'{a}^{2**i} mod {p} = {a%p}')
        else:
            if info:
                print(f"{a}^{2**i} mod {p} = {nums[-1]}^2 mod {p} = {(nums[-1] ** 2)%p}")
            nums.append((nums[-1] ** 2)%p)
            
    return nums

def modular_exponentiation(a,x,p, info = false):
    t = int(math.log2(x))
    nums = row(a,p,t, info)
    binary = get_bin(x)
    ans = 1
    if info:
        print(nums)
        print(f"{x} = {''.join(str(b) for b in binary[::-1])}v2")
        ans_str = f'y = {a}^{x} mod {p} = ('
        for j in range(t+1):
            if binary[j] == 1:
                ans *= nums[j]
                ans_str += f' {nums[j]} *'
        ans_str = ans_str[:-1]
        ans = ans % p
        ans_str += f') mod {p} = {ans}'
        print(ans_str)
    else:
        for j in range(t+1):
            if binary[j] == 1:
                ans *= nums[j]
        ans = ans % p
    return ans

def Euclidean_algorithm(a,b):
    U = [a, 1, 0]
    V = [b, 0, 1]

    while V[0] != 0:
        q = U[0] // V[0]
        T = [U[0] % V[0], U[1] - q*V[1], U[2] - q*V[2]]
        U = V
        V = T
    return tuple(U)


def main():
    power = 10 ** 9
    # p = random.randint(1, power)
    # while not isPrimeFerma(p):
    #     p = random.randint(1, power)
    
    # print(isprime(p))

    # x = random.randint(1, p)
    # a = random.randint(1,power)

    # # a = 3
    # # x = 100
    # # p = 7
    # modular_exponentiation(a,x,p, info=True)



    key = input("1. Ввод с клавиатуры\n2. Генерация внутри функции\n3. Генерация простых\n")
    
    match key:
        case "1":
            a = int(input("Введите а: "))
            b = int(input("Введите b: "))
            if a < b: a,b = b,a

        case "2":
            a = random.randint(1,power)
            b = random.randint(1, a)

        case "3":
            a = random.randint(1,power)
            while not isPrimeFerma(a):
                a = random.randint(1,power)
            b = random.randint(1, a)
            while not isPrimeFerma(b):
                b = random.randint(1,a)
    print(a,b, isprime(a), isprime(b))

    # a = 28
    # b = 19

    gcd, x, y = Euclidean_algorithm(a,b)
    print(f'{a} * {x} + {b} * {y} = {a*x} + {b*y} = {gcd}')
    pass

    

if __name__ == '__main__':
    main()