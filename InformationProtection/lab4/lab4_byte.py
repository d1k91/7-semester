import os
import random
from primeFerma import isPrimeFerma, modular_exponentiation
from EuclideanAlgorithm import Euclidean_algorithm, gcd

def shamir_encrypt(data: bytes, p, Ca, Cb, Da, Db):
    encrypted = []
    for byte in data:
        x1 = modular_exponentiation(byte, Ca, p)
        x2 = modular_exponentiation(x1, Cb, p)
        encrypted.append(x2)
    return encrypted


def shamir_decrypt(encrypted, p, Ca, Cb, Da, Db):
    decrypted = []
    for enc in encrypted:
        x3 = modular_exponentiation(enc, Da, p)
        x4 = modular_exponentiation(x3, Db, p)
        decrypted.append(x4)
    return bytes(decrypted)


def encrypt_file(input_file, output_file, p, Ca, Cb, Da, Db):
    with open(input_file, "rb") as f:
        data = f.read()
    encrypted = shamir_encrypt(data, p, Ca, Cb, Da, Db)
    with open(output_file, "w") as f:
        f.write(" ".join(map(str, encrypted)))


def decrypt_file(input_file, output_file, p, Ca, Cb, Da, Db):
    with open(input_file, "r") as f:
        encrypted = list(map(int, f.read().split()))
    decrypted = shamir_decrypt(encrypted, p, Ca, Cb, Da, Db)
    with open(output_file, "wb") as f:
        f.write(decrypted)


def main():
    power = 10**9
    key = input("1. Ввод с клавиатуры\n2. Генерация внутри функции\n")
    
    match key:
        case "1":
            p = int(input("Введите p: "))
            while not isPrimeFerma(p):
                p = int(input("P должно быть простым!\nВведите другое p: "))
            
            Ca = int(input("Введите Ca: "))
            gcd_Ca = gcd(p-1, Ca)
            while gcd_Ca != 1 or Ca >= p:
                Ca = int(input("p-1 и Ca должны быть взаимнопростыми, Ca < p: "))
                gcd_Ca = gcd(p-1, Ca)

            Cb = int(input("Введите Cb: "))
            gcd_Cb = gcd(p-1, Cb)
            while gcd_Cb != 1 or Cb >= p:
                Cb = int(input("p-1 и Cb должны быть взаимнопростыми, Cb < p: "))
                gcd_Cb = gcd(p-1, Cb)

        case "2":
            p = random.randint(2,power)
            while not isPrimeFerma(p):
                p = random.randint(2,power)
            print(f'p = {p}')

            Ca = random.randint(2, p)
            while gcd(p-1, Ca) != 1:
                Ca = random.randint(2, p)
            print(f'Ca = {Ca}')

            Cb = random.randint(2, p)
            while gcd(p-1, Cb) != 1:
                Cb = random.randint(2, p)
            print(f'Cb = {Cb}')

    _, Da = Euclidean_algorithm(p-1, Ca)
    if Da < 0:
        Da += p-1
    print(f'Da = {Da}')
    _, Db = Euclidean_algorithm(p-1, Cb)
    if Db < 0:
        Db += p-1
    print(f'Db = {Db}')   

    while True:
        mode = input("Шифрование (e) или расшифровка (d)? ")
        if mode == "e":
            infile = input("Введите имя входного файла: ")
            outfile = input("Введите имя выходного файла: ")
            encrypt_file(infile, outfile, p, Ca, Cb, Da, Db)
            print("Файл зашифрован.")
        else:
            infile = input("Введите имя входного файла: ")
            outfile = input("Введите имя выходного файла: ")
            decrypt_file(infile, outfile, p, Ca, Cb, Da, Db)
            print("Файл расшифрован.")       

if __name__ == "__main__":
    main()

    
