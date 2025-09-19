import random
from primeFerma import *

def Diffie_Hellman(p, g, x_a, x_b):
    y_a = modular_exponentiation(g, x_a, p)
    y_b = modular_exponentiation(g, x_b, p)

    z_ab = modular_exponentiation(y_b, x_a, p)
    z_ba = modular_exponentiation(y_a, x_b, p)

    print(f'Zab = {z_ab}, Zba = {z_ba}')



def main():
    power = 10**9
    key = input("1. Ввод с клавиатуры\n2. Генерация внутри функции\n")
    
    match key:
        case "1":
            p = int(input("Введите p: "))
            while not isPrimeFerma(p):
                p = int(input("P должно быть простым!\nВведите другое p: "))
            q = int((p-1)/2)
            g = int(input("Введите g: "))
            while modular_exponentiation(g, q, p) == 1:
                g = int(input("p^q mod p = 1\nВведите другое g: "))
            x_a = int(input("Введите Xa: "))
            x_b = int(input("Введите Xb: "))
            
        case "2":
            p = random.randint(1,power)
            while not isPrimeFerma(p):
                p = random.randint(1,power)
            print(f'p = {p}')
            q = int((p-1)/2)
            print(f'q = {q}')
            g = random.randint(2,p-1)
            while modular_exponentiation(g, q, p) == 1 and g < p-1:
                print(f'g = {g}')
                g = random.randint(2,p-1)
            x_a = random.randint(1,p)
            print(f'x_a = {x_a}')
            x_b = random.randint(1,p)
            print(f'x_b = {x_b}')

    
    Diffie_Hellman(p,g,x_a,x_b)

    
if __name__ == "__main__":
    main()