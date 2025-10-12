from primeFerma import *
import random
from EuclideanAlgorithm import *
import os


def RSA(m, d, N, c):
    e = modular_exponentiation(m, d, N)
    print(f"e = m ^ d mod N = {m} ^ {d} mod {N} = {e}")
    mb = modular_exponentiation(e, c, N)
    print(f"m' = e ^ c mod N = {e} ^ {c} mod {N} = {mb}")
    return mb

def RSA_encrypt(data: bytes, d, N):
    encrypted = []
    for byte in data:
        e = modular_exponentiation(byte, d, N)
        encrypted.append(e)
    return encrypted

def RSA_decrypt(encrypted, c, N):
    decrypted = []
    for enc in encrypted:
        m = modular_exponentiation(enc, c, N)
        decrypted.append(m)
    return bytes(decrypted)

def RSA_encrypt_file(input_file, output_file, d, N ):
    with open(input_file, "rb") as f:
        data = f.read()
    encrypted = RSA_encrypt(data, d, N)

    with open(output_file, "w") as f:
        f.write(" ".join(map(str, encrypted)))

def RSA_decrypt_file(input_file, out_file, c, N ):
    with open(input_file, "r") as f:
        encrypted = list(map(int, f.read().split()))
    decrypted = RSA_decrypt(encrypted, c, N)

    with open(out_file, "wb") as f:
        f.write(decrypted)

def main():
    power = 10**9
    key = input("1. Ввод с клавиатуры\n2. Генерация внутри функции\n")
    
    match(key):
        case "1":
            m_input = input("Введите число m для шифрования: ")
            try:
                m_number = int(m_input)
            except ValueError:
                print("Ошибка: введите целое число!")
                return
            
            p = int(input("Введите p (простое число): "))
            while not isPrimeFerma(p):
                p = int(input("p должно быть простым!\nВведите другое p: "))

            q = int(input("Введите q: "))
            while not isPrimeFerma(q):
                q = int(input("q должно быть простым!\nВведите другое q: "))
            
            phi = (p-1)*(q-1)

            d = int(input("Введите d: "))
            while not gcd(phi, d) == 1 or d >= phi:
                d = int(input("gcd(ф,d)≠1 или d >= ф!\nВведите другое d: "))
        case "2":
            p = random.randint(2, power)
            while not isPrimeFerma(p):
                p = random.randint(2, power)
            print(f'p = {p}')

            q = random.randint(2, power)
            while not isPrimeFerma(q):
                q = random.randint(2, power)
            print(f'q = {q}')

            phi = (p-1)*(q-1)
            print(f'ф = {phi}')

            d = random.randint(2, phi)
            while not gcd(phi, d) == 1:
                d = random.randint(2, power)
            print(f"d = {d}")

    N = p * q
    print(f"N = {N}")

    print("----Считаем с----")
    _, c = Euclidean_algorithm(phi, d, info=True)
    if c < 0:
        c += phi
    print(f"c = {c}")

    while True:
        mode = input('Шифрование файла (e), расшифровка файла (d), ручной ввод сообщения m (f): ')

        if mode == "e":
            infile = input("Введите имя входного файла: ")
            outfile = input("Введите имя выходного файла: ")
            if not os.path.exists(infile):
                print("Ошибка: входной файл не существует!")
                continue
            RSA_encrypt_file(infile, outfile, d, N)
            print("Файл зашифрован.")

        elif mode == "d":
            infile = input("Введите имя входного файла: ")
            outfile = input("Введите имя выходного файла: ")
            if not os.path.exists(infile):
                print("Ошибка: входной файл не существует!")
                continue
            RSA_decrypt_file(infile, outfile, c, N)
            print("Файл расшифрован.")

        elif mode == "f":
            if key == "2":
                m_input = input(f"Введите число m для шифрования: ")
                if m_input.strip():
                    try:
                        m_number = int(m_input)
                    except ValueError:
                        print("Ошибка: введите целое число!")
                        continue
            print(f"Шифруем число: {m_number}")
            mb = RSA(m_number, d, N, c)

            if mb == m_number:
                print("✓ Расшифровка успешна!")
            else:
                print("✗ Ошибка расшифровки!")
            
if __name__ == "__main__":
    main()