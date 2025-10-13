from primeFerma import *
import random
from EuclideanAlgorithm import *
import os

def RSA_with_sign(m, d, N, c, p, q, test = False):
    power = 10**2
    p_a = random.randint(2, max(p,q))
    while not isPrimeFerma(p_a):
        p_a = random.randint(2, max(p,q))

    q_a = random.randint(2, max(p,q))
    while not isPrimeFerma(q_a) or p_a * q_a >= N:
        q_a = random.randint(2, max(p,q))

    phi_a = (p_a-1)*(q_a-1)

    d_a = random.randint(2, phi_a)
    while not gcd(phi_a, d_a) == 1:
        d_a = random.randint(2, power)
    
    N_a = p_a * q_a
    while True:
        _, c_a = Euclidean_algorithm(phi_a, d_a, info=False)
        if c_a < 0:
            c_a += phi_a

        if N > c and N > c_a and N_a > c and N_a > c_a:
            break

        p_a = random.randint(2, max(p,q))
        while not isPrimeFerma(p_a):
            p_a = random.randint(2, max(p,q))
        
        q_a = random.randint(2, max(p,q))
        while not isPrimeFerma(q_a) or p_a * q_a >= N:
            q_a = random.randint(2, max(p,q))
        
        phi_a = (p_a-1)*(q_a-1)

        d_a = random.randint(1, phi_a)
        while not gcd(phi_a, d_a) == 1:
            d_a = random.randint(1, power)

        N_a = p_a*q_a

    print(f'p_a = {p_a}')
    print(f'q_a = {q_a}')
    print(f'ф_a = {phi_a}')
    print(f"d_a = {d_a}")
    print(f"c_a = {c_a}")
    e = modular_exponentiation(m, c_a, N_a)
    print(f"e = m^c_a mod N_a = {m}^{c_a} mod {N_a} = {e}")
    f = modular_exponentiation(e, d, N)
    print(f"f = e^d_b mod N_b = {e}^{d} mod {N} = {f}")
    u = modular_exponentiation(f, c, N)
    print(f"u = f^c_b mod N_b = {f}^{c} mod {N} = {u}")
    w = modular_exponentiation(u, d_a, N_a)
    print(f"w = u^d_a mod N_a = {u}^{d_a} mod {N_a} = {w}")

    if test:
        return w, (N_a, c_a, phi_a)
    else:
        return w

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
        mode = input('Шифрование файла (e), расшифровка файла (d), ручной ввод сообщения m (f), ручной ввод с подписью(s): ')

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

        elif mode == "s":
            if key == "2":
                m_input = input(f"Введите число m для шифрования: ")
                if m_input.strip():
                    try:
                        m_number = int(m_input)
                    except ValueError:
                        print("Ошибка: введите целое число!")
                        continue
            
            
            # res = []
            # for i in range(100):
            #     print(f"Шифруем число: {m_number}")
            #     w, params = RSA_with_sign(m_number, d, N, c, p, q, test=True)
            #     N_a, c_a, phi_a = params
            #     if w == m_number:
            #         # print("✓ Расшифровка успешна!")
            #         res.append(('✓', {'N_a':N_a, 'c_a':c_a, 'ф_a':phi_a}, {'N_b':N, 'c_b':c, 'ф_b':phi}, True if (N_a > c_a and N_a > c and N > c_a and N > c) else False ))
            #     else:
            #         # print("✗ Ошибка расшифровки!")
            #         res.append(('✗', {'N_a':N_a, 'c_a':c_a, 'ф_a':phi_a}, {'N_b':N, 'c_b':c, 'ф_b':phi}, True if (N_a > c_a and N_a > c and N > c_a and N > c) else False ))
            
            # for r in res:
            #     print(r)

            print(f"Шифруем число: {m_number}")
            w = RSA_with_sign(m_number, d, N, c, p, q)
            if w == m_number:
                print("✓ Расшифровка успешна!")
            else:
                print("✗ Ошибка расшифровки!")

# оба модуля больше обоих секретных ключей
if __name__ == "__main__":
    main()