from primeFerma import *
from EuclideanAlgorithm import *
import hashlib
import os

def hash_text_md5_split(text):
    hash_obj = hashlib.md5()
    hash_obj.update(text.encode('utf-8'))
    hex_hash = hash_obj.hexdigest()
    
    return _hex_to_numbers(hex_hash)

def hash_file_md5_split(filename):
    hash_obj = hashlib.md5()
    
    with open(filename, "rb") as f:
        for chunk in iter(lambda: f.read(4096), b''):
            hash_obj.update(chunk)
    
    hex_hash = hash_obj.hexdigest()
    return _hex_to_numbers(hex_hash)

def _hex_to_numbers(hex_hash):
    def hex_to_bin(hex_str):
        hex_to_bin_map = {
            '0': '0000', '1': '0001', '2': '0010', '3': '0011',
            '4': '0100', '5': '0101', '6': '0110', '7': '0111',
            '8': '1000', '9': '1001', 'a': '1010', 'b': '1011',
            'c': '1100', 'd': '1101', 'e': '1110', 'f': '1111'
        }
        return ''.join(hex_to_bin_map[char] for char in hex_str.lower())
    
    bin_hash = hex_to_bin(hex_hash)
    
    numbers = []
    for i in range(8):
        start_idx = i * 16
        end_idx = start_idx + 16
        block = bin_hash[start_idx:end_idx]
        number = int(block, 2)
        numbers.append(number)
    
    
    return numbers

def ShamirCipher(m, p, Ca, Cb):
    x1 = modular_exponentiation(m, Ca, p)
    x2 = modular_exponentiation(x1, Cb, p)
    return x2

def ShamirDecipher(m, p, Da, Db):
    x3 = modular_exponentiation(m, Da, p)
    x4 = modular_exponentiation(x3, Db, p)
    return x4

def main():
    power = 10**9
    key = input("1. Ввод с клавиатуры\n2. Генерация внутри функции\n")
    
    match key:
        case "1":
            m = input("Введите сообщение: ")
            hashed_numbers = hash_text_md5_split(m)

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
            curr_dir = os.path.dirname(os.path.abspath(__file__))
            filename = os.path.join(curr_dir, 'test.txt')
            hashed_numbers = hash_file_md5_split(filename)

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
    
    mode = input('Шифрование (e) или расшифровка (d)? ')

    ciphered_res = []
    for i, num in enumerate(hashed_numbers):
        result = ShamirCipher(num, p, Ca, Cb)
        ciphered_res.append(result)

    print(f"\nЗашифрованное сообщение (Отдельные числа):\n{hashed_numbers} -> {ciphered_res}\n")
    results_individual = []
    for i, num in enumerate(ciphered_res):
        result = ShamirDecipher(num, p, Da, Db)
        results_individual.append(result)
    print(f"Расшифрованное сообщение (Отдельные числа):\n{ciphered_res} -> {results_individual}\n")

    if hashed_numbers == results_individual:
        print(f'Расшифровано успешно!\n{hashed_numbers} = {results_individual}')
    else:
        print(f'Расшифровка не удалась!\n{hashed_numbers} != {results_individual}')


if __name__ == '__main__':
    main()