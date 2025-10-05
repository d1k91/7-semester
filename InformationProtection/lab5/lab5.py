import os
import random
from primeFerma import isPrimeFerma, modular_exponentiation
from EuclideanAlgorithm import Euclidean_algorithm, gcd

def find_primitive_root(p):
    if p == 2:
        return 1
    
    factors = set()
    phi = p - 1
    n = phi
    
    i = 2
    while i * i <= n:
        if n % i == 0:
            factors.add(i)
            while n % i == 0:
                n //= i
        i += 1
    if n > 1:
        factors.add(n)
    
    for g in range(2, p):
        if all(modular_exponentiation(g, phi // factor, p) != 1 for factor in factors):
            return g
    return None

def ElGamal_encrypt(data: bytes, p, g, y):
    encrypted = []
    for byte in data:
        m = byte % p
        if m == 0:
            m = 1
            
        k = random.randint(2, p-2)
        a = modular_exponentiation(g, k, p)
        b = (modular_exponentiation(y, k, p) * m) % p
        encrypted.append((a, b))
    return encrypted

def ElGamal_decrypt(encrypted, p, Db):
    decrypted = []
    for a, b in encrypted:
        power = p - 1 - Db
        m = (b * modular_exponentiation(a, power, p)) % p
        decrypted.append(m)
    return bytes(decrypted)

def encrypt_file(input_file, output_file, p, g, y):
    with open(input_file, "rb") as f:
        data = f.read()
    encrypted = ElGamal_encrypt(data, p, g, y)
    
    with open(output_file, "w") as f:
        for a, b in encrypted:
            f.write(f"{a} {b}\n")

def decrypt_file(input_file, output_file, p, Db):
    encrypted = []
    with open(input_file, "r") as f:
        for line in f:
            a, b = map(int, line.strip().split())
            encrypted.append((a, b))
    
    decrypted = ElGamal_decrypt(encrypted, p, Db)
    
    with open(output_file, "wb") as f:
        f.write(decrypted)

def main():
    power = 10**9
    key = input("1. Ввод с клавиатуры\n2. Генерация внутри функции\n")
    
    if key == "1":
        p = int(input("Введите p (простое число): "))
        while not isPrimeFerma(p):
            p = int(input("P должно быть простым!\nВведите другое p: "))

        g = int(input(f"Введите g (первообразный корень по модулю {p}): "))
        if modular_exponentiation(g, p-1, p) != 1:
            print("Предупреждение: g не является первообразным корнем по модулю p!")
        
        Cb = int(input("Введите Cb (секретный ключ Боба, 1 < Cb < p-1): "))
        while Cb <= 1 or Cb >= p-1:
            Cb = int(input("Cb должно быть в диапазоне 1 < Cb < p-1: "))

    else:
        p = random.randint(2, power)
        while not isPrimeFerma(p):
            p = random.randint(2, power)
        print(f'p = {p}')

        g = find_primitive_root(p)
        print(f'g = {g}')

        Cb = random.randint(2, p-2)
        print(f'Cb (секретный ключ Боба) = {Cb}')

    y = modular_exponentiation(g, Cb, p)
    print(f'Открытый ключ Боба (y) = {y}')

    while True:
        mode = input('Шифрование (e) или расшифровка (d)? ').lower()

        if mode == 'e':
            infile = input("Введите имя входного файла: ")
            outfile = input("Введите имя выходного файла: ")
            if not os.path.exists(infile):
                print("Ошибка: входной файл не существует!")
                continue
            encrypt_file(infile, outfile, p, g, y)
            print("Файл зашифрован.")
            
        elif mode == 'd':
            infile = input("Введите имя входного файла: ")
            outfile = input("Введите имя выходного файла: ")
            if not os.path.exists(infile):
                print("Ошибка: входной файл не существует!")
                continue
            decrypt_file(infile, outfile, p, Cb)
            print("Файл расшифрован.")


if __name__ == '__main__':
    main()