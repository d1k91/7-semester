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

def baby_row(y, a, p, m, info = false):
    baby = []

    for i in range(m):
        if i == 0:
            baby.append(y % p)
            if info:
                print(f'{y} % {p} = {baby[-1]}')
        else: 
            if info:
                print(f'{a}^{i} * {y} % {p} = {baby[-1]} * {a} % {p} = {(baby[-1] * a) % p}')
            baby.append((baby[-1] * a) % p)
    if info:
        print('\n')
    return baby

def giant_row(y, a, p, m, k, info=false):
    giant = []
    for i in range(1, k+1):
        if i == 1:
            giant.append((a ** m) % p)
            if info:
                print(f'{a}^{m} % {p} = {giant[-1]}')
        else: 
            if info:
                print(f'{a}^({i}*{m}) % {p} = {giant[-1]} * {a}^{m} % {p} = {(giant[-1] * (a ** m)) % p}')
            giant.append((giant[-1] * (a ** m)) % p)
    if info:    
        print('\n')
    return giant

def find_match(baby, giant):
    for i, el in enumerate(giant):
        if el in baby:
            j = baby.index(el)
            return i,j
    return None

def shanks_method(y, a, p, info = false):
    m = math.ceil(math.sqrt(p))
    k = math.floor(p/m) + 1

    # m = 6
    # k = 4
    print(f'm = {m}, k = {k}')
    baby = baby_row(y, a, p, m, info = info)
    giant = giant_row(y, a, p, m, k, info = info)

    print(baby)
    print(giant)

    res = find_match(baby, giant)
    if res:
        i, j = res
        x = (i+1)*m - j
        print(f'{(i+1)}*{m} - {j} = ',x)
        return x
    else:
        print('Нет решений!')

def main():
    power = 10 ** 2

    key = input("1. Ввод с клавиатуры\n2. Генерация внутри функции\n3. Расчет у через возведение в степень по модулю\n")
    
    match key:
        case "1":
            a = int(input("Введите а: "))
            y = int(input("Введите y: "))
            p = int(input("Введите p: "))
            

        case "2":
            p = random.randint(1, power)
            while not isPrimeFerma(p):
                p = random.randint(1, power)
            a = random.randint(1,power)
            y = random.randint(1,p)

        case "3":
            p = random.randint(1, power)
            while not isPrimeFerma(p):
                p = random.randint(1, power)
            
            x = random.randint(1, p)
            a = random.randint(1, p)

            print(f'a = {a}, x = {x}, p = {p}')

            y = modular_exponentiation(a,x,p, info=false)

            print(f'y = {y}')
    
    x_shanks = shanks_method(y, a, p, info=true)
    if x_shanks:
        print(f'x_shanks = {x_shanks}')

if __name__ == '__main__':
    main()