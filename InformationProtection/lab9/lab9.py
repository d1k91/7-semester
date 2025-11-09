import hashlib
import os
import random
import json
from primeFerma import *
from EuclideanAlgorithm import *

def find_primitive_root(p):
    q = (p - 1) // 2

    if not isPrimeFerma(q):
        return None

    for g in range(2, p):
        if (modular_exponentiation(g, 2, p) != 1 and
            modular_exponentiation(g, q, p) != 1):
            return g
    return None

def generate_elgamal_keys(bits = 129, silent=False):
    while True:
        q = random.getrandbits(bits)
        q |= (1 << (bits - 1))
        q |= 1
        if isPrimeFerma(q):
            p = 2 * q + 1
            if isPrimeFerma(p):
                break

    if not silent:
        print(f'p = {p}')

    g = find_primitive_root(p)
    if g is None:
        raise ValueError("Не найден первообразный корень по модулю p")
    if not silent:
        print(f'g = {g}')

    x = random.randint(1, p - 2)
    y = modular_exponentiation(g, x, p)
    if not silent:
        print(f'x (приватный ключ) = {x}')
        print(f'y (публичный ключ) = {y}')

    return x, y, p, g


def compute_hash(file_path, hash_algorithm='md5'):
    if hash_algorithm not in hashlib.algorithms_guaranteed:
        raise ValueError(f"Неподдерживаемый алгоритм хеширования: {hash_algorithm}")

    hash_func = hashlib.new(hash_algorithm)
    with open(file_path, "rb") as f:
        while chunk := f.read(4096):
            hash_func.update(chunk)

    hash_digest = hash_func.digest()
    print(hash_digest)
    hash_int = int.from_bytes(hash_digest, 'big')
    print(hash_int)
    return hash_int, hash_func.hexdigest()


def sign_file_elgamal(file_path, x, p, g, output_sig_path=None, hash_algorithm='md5'):
    if not (1 < x < p - 1):
        raise ValueError("Некорректный приватный ключ x")

    h, h_hex = compute_hash(file_path, hash_algorithm)
    print(f"Хэш сообщения ({hash_algorithm}): {h_hex}")
    print(f"Хэш (целое): {h}")

    while True:
        k = random.randint(1, p - 2)
        if gcd(k, p - 1) == 1:
            break

    r = modular_exponentiation(g, k, p)
    k_inv = mod_inverse(k, p - 1)
    u = (h - x * r) % (p - 1)
    s = (u * k_inv) % (p - 1)

    if s == 0:
        raise RuntimeError("Неудача при генерации подписи (s=0), попробуйте снова")

    print(f"Подпись: (r = {r}, s = {s})")

    if output_sig_path is None:
        output_sig_path = file_path + '.sig'

    with open(output_sig_path, 'w', encoding='utf-8') as f:
        json.dump({
            'hash_algorithm': hash_algorithm,
            'p': p,
            'g': g,
            'y': modular_exponentiation(g, x, p),
            'signature': [r, s],
            'hash_value': h_hex
        }, f, ensure_ascii=False, indent=2)

    print(f"Подпись сохранена в {output_sig_path}")
    return (r, s), output_sig_path


def verify_signature_elgamal(file_path, sig_path, hash_algorithm='md5'):

    if not os.path.exists(sig_path):
        raise FileNotFoundError(f"Файл подписи не найден: {sig_path}")

    with open(sig_path, 'r', encoding='utf-8') as f:
        data = json.load(f)

    p = data['p']
    g = data['g']
    y = data['y']
    r, s = data['signature']
    stored_hash_alg = data['hash_algorithm']

    if stored_hash_alg != hash_algorithm:
        print(f"Ожидаемый хэш {hash_algorithm}, в подписи — {stored_hash_alg}")

    if not (0 < r < p):
        print("Ошибка: r вне диапазона")
        return False

    h, h_hex = compute_hash(file_path, hash_algorithm)
    print(f"Хэш файла: {h_hex}")
    print(f"Хэш из подписи: {data['hash_value']}")

    left = modular_exponentiation(g, h, p)
    right1 = modular_exponentiation(y, r, p)
    right2 = modular_exponentiation(r, s, p)
    right = (right1 * right2) % p

    print(f"Левая часть (g^h mod p): {left}")
    print(f"Правая часть (y^r * r^s mod p): {right}")

    if left == right:
        print("✅ Подпись действительна!")
        return True
    else:
        print("❌ Подпись недействительна!")
        return False

def mod_inverse(a, m):
    gcd, x, _ = Euclidean_algorithm(a, m)
    if gcd != 1:
        raise ValueError(f"Обратный элемент не существует для {a} по модулю {m}")
    return (x % m + m) % m


def main():
    print("=== Подпись Эль-Гамаля ===")
    print("1. Сгенерировать ключи")
    print("2. Подписать файл")
    print("3. Проверить подпись")

    keys_generated = False
    x = y = p = g = None

    while True:
        choice = input("\nВыберите действие: ").strip()

        if choice == "1":
            x, y, p, g = generate_elgamal_keys(silent=False)
            keys_generated = True
            print("Ключи сгенерированы.")

        elif choice == "2":
            if not keys_generated:
                print("Сначала сгенерируйте ключи (опция 1)")
                continue
            file_path = input("📄 Путь к файлу: ").strip()
            if not os.path.exists(file_path):
                print("❌ Файл не найден.")
                continue
            sig_path = input("Путь к файлу подписи (Enter для автоприсвоения *.egsig): ").strip() or None
            hash_alg = input("Хэш-алгоритм (Enter для md5): ").strip() or 'md5'
            sign_file_elgamal(file_path, x, p, g, sig_path, hash_alg)

        elif choice == "3":
            file_path = input("📄 Путь к оригинальному файлу: ").strip()
            if not os.path.exists(file_path):
                print("❌ Файл не найден.")
                continue
            sig_path = input("📎 Путь к подписи: ").strip()
            hash_alg = input("🔐 Хэш-алгоритм (Enter для md5): ").strip() or 'md5'
            verify_signature_elgamal(file_path, sig_path, hash_alg)

        else:
            print("Неверный выбор. Попробуйте снова.")


if __name__ == "__main__":
    main()
